package dao;

import beans.LinkPost;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static dao.DAO.initialisePreparedStatement;
import static dao.DAO.quietClose;

/**
 * Implémentation du Dao gérant les publications contenant un lien
 */
public class LinkPostDao extends BasicDao{
    private final DAOFactory daoFactory;
    
    /**
     * permet de récupérer une connexion à la base de données
     * @param daoFactory
     */
    public LinkPostDao(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    /**
     * instancier une publication contenant un lien
     * @param resultSet resultSet permmettant de récupérer les données de la publication
     * @return la publication créée ou null
     * @throws SQLException 
     */
    private static LinkPost map( ResultSet resultSet ) throws SQLException {
        LinkPost post = new LinkPost();
        post.setId(resultSet.getInt( "id"));
        post.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        post.setId_human(resultSet.getInt("id_human"));
        post.setContent(resultSet.getString("content"));
        post.setUrl(resultSet.getString("url"));
        post.setTitle(resultSet.getString("title"));
        
        return post;
    }
    
    /**
     * la requête SQL permettant de créer la publication
     */
    private static final String SQL_INSERT = "INSERT INTO linkpost (id, url, title) VALUES (?, ?, ?)";
    
    /**
     * la créer la publication
     * @param post la publication à créer
     * @return la publication créée ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public LinkPost create(LinkPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            int id_activity = super.createActivity(daoFactory, post.getDate(), post.getId_human());
            super.createPost(daoFactory, id_activity, post.getContent());
            
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_INSERT, true, id_activity, post.getUrl(), post.getTitle());
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
     * la requête SQL permettant de récupérer toutes les publications contenant un lien
     */
    private static final String SQL_SELECT_ALL = "SELECT a.id as id, date, id_human, content, url, title FROM linkpost l LEFT JOIN post p ON l.id = p.id LEFT JOIN activity a ON l.id = a.id";
    
    /**
     * récupérer toutes les publications contenant un lien
     * @return toutes les publications contenant un lien (ou une ArrayList vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public ArrayList<LinkPost> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        LinkPost post = null;
        ArrayList<LinkPost> posts = new ArrayList<>();

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
     * la requête SQL permettant de récupérer une publication contenant un lien par son identifiant
     */
    private static final String SQL_SELECT_BY_ID = "SELECT a.id as id, date, id_human, content, url, title FROM linkpost l LEFT JOIN post p ON l.id = p.id LEFT JOIN activity a ON l.id = a.id WHERE a.id = ?";
    
    /**
     * récupérer une publication contenant un lien par son identifiant
     * @param id l'dentifiant de la publication
     * @return la publication récupérée ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public LinkPost get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        LinkPost post = null;

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
     * la requête SQL permettant de récupérer les publications contenant un lien d'un utilisateur par son identifiant
     */
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT a.id as id, date, id_human, content, url, title FROM linkpost l LEFT JOIN post p ON l.id = p.id LEFT JOIN activity a ON l.id = a.id WHERE id_human = ?";
    
    /**
     * récupérer les publications contenant un lien d'un utilisateur par son identifiant
     * @param id_human l'identifiant de l'utilisateur
     * @return les publications récupérées (ou une ArrayList vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public ArrayList<LinkPost> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        LinkPost post = null;
        ArrayList<LinkPost> posts = new ArrayList<>();

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
     * récupérer les publications contenant un lien d'un utilisateur par son identifiant
     * @param id_human l'identifiant de l'utilisateur
     * @return les publications récupérées (ou une HashMap vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public Map<Integer, LinkPost> getHashByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        LinkPost post = null;
        Map<Integer, LinkPost> posts = new HashMap<>();

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
     * requête SQL permettant de mettre à jour une publication contenant un lien
     */
    private static final String SQL_UPDATE = "UPDATE linkpost SET url = ?, title = ? WHERE id = ?";
    
    /**
     * mettre à jour une publication contenant un lien
     * @param post la publication à mettre à jour
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public void update(LinkPost post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            super.updateActivity(daoFactory, post.getDate(), post.getId_human(), post.getId());
            super.updatePost(daoFactory, post.getId(), post.getContent());
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_UPDATE, false, post.getUrl(), post.getTitle(), post.getId());
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }
    }

    /**
     * la requête SQL permettant de supprimer une publication contenant un lien
     */
    private static final String SQL_DELETE= "DELETE FROM linkpost WHERE id = ?";
    
    /**
     * supprimer une publication contenant un lien
     * @param id l'identifiant de la publication
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, "DELETE FROM activity WHERE id = ?");
        super.delete(daoFactory, id, "DELETE FROM post WHERE id = ");
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
