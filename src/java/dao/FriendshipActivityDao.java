package dao;

import beans.FriendshipActivity;
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
 * Implémentation du Dao gérant les liens d'amitié
 */
public class FriendshipActivityDao extends BasicDao {
    /**
     * permet de récupérer une connexion à la base de données
     */
    private final DAOFactory daoFactory;
    
    /**
     * Constructeur du Dao gérant les liens d'amitié
     * @param daoFactory Classe permettant de récupérer une connexion à la base de données
     */
    public FriendshipActivityDao(DAOFactory daoFactory){
        super();
        this.daoFactory = daoFactory;
    }
    
    /**
     * instancier un lien d'amitié
     * @param resultSet resultSet permmettant de récupérer les données d'un lien d'amitié
     * @return le lien d'amitié créé ou null
     * @throws SQLException lorsqu'une erreur SQL est survenue
     */
    private static FriendshipActivity map( ResultSet resultSet ) throws SQLException {
        FriendshipActivity activity = new FriendshipActivity();
        activity.setId(resultSet.getInt( "id"));
        activity.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        activity.setId_human(resultSet.getInt("id_human"));
        activity.setId_second_human(resultSet.getInt("id_second_human"));
        
        return activity;
    }
    
    /**
     * la requête SQL permettant de créer un lien d'amitié
     */
    private static final String SQL_INSERT = "INSERT INTO friendshipactivity (id, id_second_human) VALUES (?, ?)";
    /**
     * insérer un lien d'amitié en base de données
     * @param activity le lien d'amitié à créer
     * @return une instance du lien d'amitié créé ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public FriendshipActivity create(FriendshipActivity activity) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;
        
        try {
            int id_activity = super.createActivity(daoFactory, activity.getDate(), activity.getId_human());
            
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_INSERT, true, id_activity, activity.getId_second_human());
            int status = preparedStatement.executeUpdate();
            if ( status == 0 ) {
                throw new DAOException( "Échec de la création de l'activité, aucune ligne ajoutée dans la table." );
            }
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
            if ( valeursAutoGenerees.next() ) {
                activity.setId( valeursAutoGenerees.getInt( 1 ) );
            } else {
                throw new DAOException( "Échec de la création de l'activité en base, aucun ID auto-généré retourné." );
            }
        } catch (SQLException e) {
            
        } finally {
            quietClose( valeursAutoGenerees, preparedStatement, connexion );
        }
        return activity;
    }
    
    /**
     * la requête SQL permettant de récupérer tous les liens d'amitié
     */
    private static final String SQL_SELECT_ALL = "SELECT a.id as id, id_second_human, date, id_human FROM friendshipactivity f LEFT JOIN activity a ON f.id = a.id";
    /**
     * récupérer tous les liens d'amitié
     * @return tous les liens d'amitié récupérés (ou une Arraylist vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public ArrayList<FriendshipActivity> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        FriendshipActivity activity = null;
        ArrayList<FriendshipActivity> activities = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            Statement statement = connexion.createStatement();
            result = statement.executeQuery(SQL_SELECT_ALL);
            while(result.next()) {
                activity = map(result);
                activities.add(activity);
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( result, preparedStatement, connexion );
        }

        return activities;
    }
    
    /**
     * la requête SQL permettant de récupérer un lien d'amitié par son identifiant
     */
    private static final String SQL_SELECT_BY_ID = "SELECT a.id as id, id_second_human, date, id_human FROM friendshipactivity f LEFT JOIN activity a ON f.id = a.id WHERE a.id = ?";
    /**
     * récupérer un lien d'amitié par son identifiant
     * @param id l'id en base du lien d'amitié
     * @return le lien d'amitié récupéré ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public FriendshipActivity get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FriendshipActivity activity = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID, false, id );
            resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() ) {
                activity = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }

        return activity;
    }
    
    /**
     * la requête SQL permettant de récupérer les identifiants de tous les amis d'un utilisateur
     */
    private static final String SQL_SELECT_FRIENDS = "SELECT a.id as id, id_second_human, id_human FROM friendshipactivity f LEFT JOIN activity a ON f.id = a.id WHERE id_human = ? OR id_second_human = ?";
    /**
     * récupérer les identifiants de tous les amis d'un utilisateur
     * @param id_human l'identifiant en base de l'utilisateur
     * @return tous les identifiants de tous les amis d'un utilisateur (ou une Arraylist vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public ArrayList<Integer> getFriends(int id_human) throws DAOException { 
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Integer> friends = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_FRIENDS, false, id_human, id_human );
            resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() ) {
                if(resultSet.getInt("id_human") == id_human){
                    friends.add(resultSet.getInt("id_second_human"));
                } else {
                    friends.add(resultSet.getInt("id_human"));
                }
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }

        return friends;
    }
    
    /**
     * la requête SQL permettant de récupérer les liens d'amitié d'un utilisateur
     */
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT a.id as id, id_second_human, date, id_human FROM friendshipactivity f LEFT JOIN activity a ON f.id = a.id WHERE id_human = ? OR id_second_human = ?";
    /**
     * récupérer les liens d'amitié d'un utilisateur dans une d'ArrayList
     * @param id_human l'identifiant en base de l'utilisateur
     * @return tous les liens d'amitié d'un utilisateur (ou une ArrayList vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public ArrayList<FriendshipActivity> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FriendshipActivity activity = null;
        ArrayList<FriendshipActivity> activities = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                activity = map( resultSet );
                activities.add(activity);
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }

        return activities;
    }
    
    /**
     * récupérer les liens d'amitié d'un utilisateur dans une HashMap
     * @param id_human l'identifiant en base de l'utilisateur
     * @return tous les liens d'amitié d'un utilisateur (ou une HashMap vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public Map<Integer, FriendshipActivity> getHashByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FriendshipActivity activity = null;
        Map<Integer, FriendshipActivity> activities = new HashMap<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                activity = map( resultSet );
                activities.put(activity.getId(), activity);
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }

        return activities;
    }
    
    /**
     * requête permettant de récupérer l'identifiant en base du lien d'amitié entre deux utilisateurs
     */
    private static final String SQL_SELECT_BY_FRIENDS = "SELECT a.id as id FROM friendshipactivity f LEFT JOIN activity a ON f.id = a.id WHERE (id_human = ? OR id_second_human = ?) AND (id_human = ? OR id_second_human = ?)";
    /**
     * récupérer l'identifiant en base du lien d'amitié entre deux utilisateurs
     * @param id_human identifiant en base du premier utilisateur
     * @param id_friend identifiante en base du second utilisateur
     * @return l'identifiant en base du lien d'amitié ou un nombre négatif
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public int getByFriends(int id_human, int id_friend) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int id_friendship_activity = -1;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_FRIENDS, false, id_human, id_human, id_friend, id_friend );
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                id_friendship_activity = resultSet.getInt("id");
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }
        return id_friendship_activity;
    }

    /**
     * la requête SQL permettant de supprimer un lien d'amitié
     */
    private static final String SQL_DELETE= "DELETE FROM friendshipactivity WHERE id = ?";
    /**
     * supprimer un lien d'amitié
     * @param id l'identifiant en base du lien d'amitié à supprimer
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, "DELETE FROM activity WHERE id = ?");
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
