package dao;

import static dao.DAO.*;
import beans.Post;
import beans.TextPost;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author pierant
 */
public class TextPostDao extends BasicDao implements TextPostDao {
    private DAOFactory daoFactory;
    
    public TextPostDao(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    private static TextPost map( ResultSet resultSet ) throws SQLException {
        TextPost post = new TextPost();
        post.setId(resultSet.getInt( "id"));
        post.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        post.setId_human(resultSet.getInt("id_human"));
        post.setContent(resultSet.getString("content"));
        
        return post;
    }
    
    private static final String SQL_INSERT = "INSERT INTO textpost (id) VALUES (?)";
    
    public void create(TextPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            int id_activity = super.createActivity(daoFactory, post.getDate(), post.getId_human());
            super.createPost(daoFactory, id_activity, post.getContent());
            
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_INSERT, true, id_activity);
            int status = preparedStatement.executeUpdate();
            if ( status == 0 ) {
                throw new DAOException( "Échec de la création du post, aucune ligne ajoutée dans la table." );
            }
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
            if ( valeursAutoGenerees.next() ) {
                post.setId( valeursAutoGenerees.getInt( 1 ) );
            } else {
                throw new DAOException( "Échec de la création du post en base, aucun ID auto-généré retourné." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( valeursAutoGenerees, preparedStatement, connexion );
        }
    }

    private static final String SQL_SELECT_ALL = "SELECT a.id as id, date, id_human, content FROM activity a INNER JOIN post p ON a.id = p.id INNER JOIN textpost t ON t.id = p.id";
    
    public ArrayList<TextPost> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        TextPost post = null;
        ArrayList<TextPost> posts = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            Statement statement = connexion.createStatement();
            result = statement.executeQuery(SQL_SELECT_ALL);
            while(result.next()) {
                post = map(result);
                posts.add(post);
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( result, preparedStatement, connexion );
        }

        return posts;
    }

    private static final String SQL_SELECT_BY_ID = "SELECT a.id as id, date, id_human, content FROM activity a INNER JOIN post p ON a.id = p.id INNER JOIN textpost t ON t.id = p.id WHERE a.id = ?";
    
    public TextPost get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        TextPost post = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID, false, id );
            resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() ) {
                post = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return post;
    }
    
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT a.id as id, date, id_human, content FROM activity a INNER JOIN post p ON a.id = p.id INNER JOIN textpost t ON t.id = p.id WHERE id_human = ?";
    
    public ArrayList<TextPost> getByHuman(int id_human) throws DAOException {
        System.out.println("entré dans textpost avec id_human = "+id_human);
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        TextPost post = null;
        ArrayList<TextPost> posts = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                post = map( resultSet );
                posts.add(post);
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return posts;
    }
    
    
    public Map<Integer, TextPost> getHashByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        TextPost post = null;
        Map<Integer, TextPost> posts = new HashMap<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                post = map( resultSet );
                posts.put(post.getId(), post);
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return posts;
    }

    
    public void update(TextPost post) throws DAOException {
        super.updateActivity(daoFactory, post.getDate(), post.getId_human(), post.getId());
        super.updatePost(daoFactory, post.getId(), post.getContent());
    }

    private static final String SQL_DELETE= "DELETE FROM textpost WHERE id = ?";
    
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, "DELETE FROM activity WHERE id = ?");
        super.delete(daoFactory, id, "DELETE FROM post WHERE id = ");
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
