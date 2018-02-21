package dao;

import static dao.DAOImpl.*;
import beans.Post;
import beans.TextPost;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.ArrayList;
/**
 *
 * @author pierant
 */
public class TextPostDaoImpl extends BasicDaoImpl implements TextPostDao {
    private DAOFactory daoFactory;
    
    public TextPostDaoImpl(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    private static TextPost map( ResultSet resultSet ) throws SQLException {
        TextPost post = new TextPost();
        post.setId(resultSet.getInt( "id"));
        post.setDate(resultSet.getDate("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        post.setId_human(resultSet.getInt("id_human"));
        post.setContent(resultSet.getString("content"));
        
        return post;
    }
    
    private static final String SQL_INSERT = "INSERT INTO textpost (date, id_human, content) VALUES (?, ?, ?)";
    @Override
    public void create(TextPost post) throws IllegalArgumentException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_INSERT, true, post.getDate(), post.getId_human(), post.getContent());
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

    private static final String SQL_SELECT_ALL = "SELECT id, date, id_human, content FROM textpost";
    @Override
    public ArrayList<Post> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        TextPost post = null;
        ArrayList<Post> posts = new ArrayList<>();

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

    private static final String SQL_SELECT_BY_ID = "SELECT id, date, id_human, content FROM textpost WHERE id = ?";
    @Override
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
    
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT id, date, id_human, content FROM textpost WHERE id_human = ?";
    @Override
    public ArrayList<Post> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        TextPost post = null;
        ArrayList<Post> posts = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                if ( resultSet.next() ) {
                    post = map( resultSet );
                    posts.add(post);
                }
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return posts;
    }

    private static final String SQL_UPDATE = "UPDATE textpost SET date = ?, id_human = ?, content = ? WHERE id = ?";
    @Override
    public void update(TextPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_UPDATE, false, post.getDate(), post.getId_human(), post.getContent(), post.getId());
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }
    }

    private static final String SQL_DELETE= "DELETE FROM textpost WHERE id = ?";
    @Override
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
