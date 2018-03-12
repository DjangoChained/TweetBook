package dao;

import beans.PhotoPost;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import static dao.DAO.initialisePreparedStatement;
import static dao.DAO.quietClose;

/**
 * Implémentation du Dao gérant les publications contenant une photo
 */
public class PhotoPostDao extends BasicDao{
    private final DAOFactory daoFactory;
    
    /**
     * permet de récupérer une connexion à la base de données
     * @param daoFactory
     */
    public PhotoPostDao(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    /**
     * instancier une publication contenant une photo
     * @param resultSet resultSet permmettant de récupérer les données de la publication
     * @return la publication créée ou null
     * @throws SQLException 
     */
    private static PhotoPost map( ResultSet resultSet ) throws SQLException {
        PhotoPost post = new PhotoPost();
        post.setId(resultSet.getInt( "id"));
        post.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        post.setId_human(resultSet.getInt("id_human"));
        post.setContent(resultSet.getString("content"));
        post.setPhotoPath(resultSet.getString("photopath"));
        
        return post;
    }
    
    /**
     * la requête SQL permettant de créer la publication
     */
    private static final String SQL_INSERT = "INSERT INTO photopost (date, id_human, content, photopath) VALUES (?, ?, ?, ?)";
    
    /**
     * créer la publication
     * @param post la publication à créer
     * @return la publication créée ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public PhotoPost create(PhotoPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_INSERT, true, Timestamp.valueOf(post.getDate()), post.getId_human(), post.getContent(), post.getPhotoPath());
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
        return post;
    }

    /**
     * la requête SQL permettant de récupérer toutes les publications contenant une photo
     */
    private static final String SQL_SELECT_ALL = "SELECT id, date, id_human, content, photopath FROM photopost ph LEFT JOIN post p ON ph.id = p.id LEFT JOIN activity a ON ph.id = a.id";
    
    /**
     * récupérer toutes les publications contenant une photo
     * @return toutes les publications contenant un lien (ou une ArrayList vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public ArrayList<PhotoPost> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        PhotoPost post = null;
        ArrayList<PhotoPost> posts = new ArrayList<>();

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
     * la requête SQL permettant de récupérer une publication contenant une photo par son identifiant
     */
    private static final String SQL_SELECT_BY_ID = "SELECT id, date, id_human, content, photopath FROM photopost ph LEFT JOIN post p ON ph.id = p.id LEFT JOIN activity a ON ph.id = a.id WHERE id = ?";
    
    /**
     * récupérer une publication contenant une photo par son identifiant
     * @param id
     * @return
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public PhotoPost get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PhotoPost post = null;

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
     * la requête SQL permettant de récupérer les publications contenant une photo d'un utilisateur par son identifiant
     */
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT id, date, id_human, content, photopath FROM photopost ph LEFT JOIN post p ON ph.id = p.id LEFT JOIN activity a ON ph.id = a.id WHERE id_human = ?";
    
    /**
     * récupérer les publications contenant une photo d'un utilisateur par son identifiant
     * @param id_human l'identifiant de l'utilisateur
     * @return les publications récupérées (ou une ArrayList vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public ArrayList<PhotoPost> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PhotoPost post = null;
        ArrayList<PhotoPost> posts = new ArrayList<>();

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
     * requête SQL permettant de mettre à jour une publication contenant une photo
     */
    private static final String SQL_UPDATE = "UPDATE photopost SET photopath = ? WHERE id = ?";
    
    /**
     * mettre à jour une publication contenant une photo
     * @param post la publication à mettre à jour
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public void update(PhotoPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            super.updateActivity(daoFactory, post.getDate(), post.getId_human(), post.getId());
            super.updatePost(daoFactory, post.getId(), post.getContent());
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_UPDATE, false, post.getPhotoPath(), post.getId());
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }
    }

    /**
     * la requête SQL permettant de supprimer une publication contenant une photo
     */
    private static final String SQL_DELETE= "DELETE FROM photopost WHERE id = ?";
    
    /**
     * supprimer une publication contenant une photo
     * @param id l'identifiant de la publication
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, "DELETE FROM activity WHERE id = ?");
        super.delete(daoFactory, id, "DELETE FROM post WHERE id = ");
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
