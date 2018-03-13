package dao;

import static dao.DAO.*;
import beans.TextPost;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation du Dao gérant les publications contenant du texte
 */
public class TextPostDao extends BasicDao {
    private final DAOFactory daoFactory;
    
    /**
     * permet de récupérer une connexion à la base de données
     * @param daoFactory
     */
    public TextPostDao(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    /**
     * instancier une publication contenant du texte
     * @param resultSet resultSet permmettant de récupérer les données de la publication
     * @return la publication créée ou null
     * @throws SQLException 
     */
    private static TextPost map( ResultSet resultSet ) throws SQLException {
        TextPost post = new TextPost();
        post.setId(resultSet.getInt( "id"));
        post.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        post.setId_human(resultSet.getInt("id_human"));
        post.setContent(resultSet.getString("content"));
        
        return post;
    }
    
    /**
     * la requête SQL permettant de créer la publication
     */
    private static final String SQL_INSERT = "INSERT INTO textpost (id) VALUES (?)";
    
    /**
     * créer la publication
     * @param post la publication à créer
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public void create(TextPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            int id_activity = super.createActivity(daoFactory, post.getDate(), post.getId_human());
            super.createPost(daoFactory, id_activity, post.getContent());
            
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_INSERT, true, id_activity);
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
            quietClose( valeursAutoGenerees, preparedStatement, connexion );
        }
    }

    /**
     * la requête SQL permettant de récupérer toutes les publications contenant du texte
     */
    private static final String SQL_SELECT_ALL = "SELECT a.id as id, date, id_human, content FROM textpost t INNER JOIN post p ON t.id = p.id INNER JOIN activity a ON t.id = a.id";
    
    /**
     * récupérer toutes les publications contenant du texte
     * @return
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
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
            quietClose( result, preparedStatement, connexion );
        }

        return posts;
    }

    /**
     * la requête SQL permettant de récupérer une publication contenant du texte par son identifiant
     */
    private static final String SQL_SELECT_BY_ID = "SELECT a.id as id, date, id_human, content FROM textpost t INNER JOIN post p ON t.id = p.id INNER JOIN activity a ON t.id = a.id WHERE a.id = ?";
    
    /**
     * récupérer une publication contenant du texte par son identifiant
     * @param id
     * @return
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public TextPost get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        TextPost post = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID, false, id );
            resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() ) {
                post = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }

        return post;
    }
    
    /**
     * la requête SQL permettant de récupérer les publications contenant du texte d'un utilisateur par son identifiant
     */
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT a.id as id, date, id_human, content FROM textpost t INNER JOIN post p ON t.id = p.id INNER JOIN activity a ON t.id = a.id WHERE id_human = ?";
    
    /**
     * récupérer les publications contenant du texte d'un utilisateur par son identifiant
     * @param id_human l'identifiant de l'utilisateur
     * @return les publications récupérées (ou une ArrayList vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public ArrayList<TextPost> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        TextPost post = null;
        ArrayList<TextPost> posts = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                post = map( resultSet );
                posts.add(post);
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }

        return posts;
    }
    
    /**
     * récupérer les publications contenant du texte d'un utilisateur par son identifiant
     * @param id_human l'identifiant de l'utilisateur
     * @return les publications récupérées (ou une ArrayList vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public Map<Integer, TextPost> getHashByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        TextPost post = null;
        Map<Integer, TextPost> posts = new HashMap<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                post = map( resultSet );
                posts.put(post.getId(), post);
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }

        return posts;
    }
    
    /**
     * mettre à jour une publication contenant du texte
     * @param post la publication à mettre à jour
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public void update(TextPost post) throws DAOException {
        super.updateActivity(daoFactory, post.getDate(), post.getId_human(), post.getId());
        super.updatePost(daoFactory, post.getId(), post.getContent());
    }

    /**
     * la requête SQL permettant de supprimer une publication contenant du texte
     */
    private static final String SQL_DELETE= "DELETE FROM textpost WHERE id = ?";
    
    /**
     * supprimer une publication contenant du texte
     * @param id l'identifiant de la publication
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, "DELETE FROM activity WHERE id = ?");
        super.delete(daoFactory, id, "DELETE FROM post WHERE id = ");
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
