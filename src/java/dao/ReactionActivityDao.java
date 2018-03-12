package dao;

import beans.Reaction;
import beans.ReactionActivity;
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
 * Implémentation du Dao gérant les réactions
 */
public class ReactionActivityDao extends BasicDao {
    private final DAOFactory daoFactory;

    /**
     * permet de récupérer une connexion à la base de données
     * @param daoFactory 
     */
    public ReactionActivityDao(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }

    /**
     * instancier une réaction
     * @param resultSet resultSet permmettant de récupérer les données d'un lien d'amitié
     * @return la réaction créée ou null
     * @throws SQLException lorsqu'une erreur SQL est survenue
     */
    private static ReactionActivity map( ResultSet resultSet ) throws SQLException {
        ReactionActivity activity = new ReactionActivity();
        activity.setId(resultSet.getInt( "id"));
        activity.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        activity.setId_human(resultSet.getInt("id_human"));
        activity.setReaction(Reaction.valueOf(resultSet.getString( "reaction")));
        activity.setId_post(resultSet.getInt("id_post"));

        return activity;
    }

    /**
     * la requête SQL permettant de créer une réaction
     */
    private static final String SQL_INSERT = "INSERT INTO reactionactivity (id, reaction, id_post) VALUES (?, ?::reaction, ?)";
    /**
     * créer une réaction
     * @param activity le lien d'amitié à créer
     * @return une instance de la réaction créée ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao 
     */
    public ReactionActivity create(ReactionActivity activity) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            int id_activity = super.createActivity(daoFactory, activity.getDate(), activity.getId_human());

            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_INSERT, true, id_activity, activity.getReaction().toString(), activity.getId_post());
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
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( valeursAutoGenerees, preparedStatement, connexion );
        }
        return activity;
    }

    /**
     * la requête SQL permettant de récupérer toutes les réactions
     */
    private static final String SQL_SELECT_ALL = "SELECT a.id as id, date, id_human, id_post, reaction FROM reactionactivity r LEFT JOIN activity a ON r.id = a.id";
    /**
     * récupérer toutes les réactions
     * @return les réactions récupérées (ou une Arraylist vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao 
     */
    public ArrayList<ReactionActivity> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        ReactionActivity activity = null;
        ArrayList<ReactionActivity> activities = new ArrayList<>();

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
     * la requête SQL permettant de récupérer une réaction par son identifiant
     */
    private static final String SQL_SELECT_BY_ID = "SELECT a.id as id, date, id_human, id_post, reaction FROM reactionactivity r LEFT JOIN activity a ON r.id = a.id WHERE a.id = ?";
    /**
     * récupérer une réaction par son identifiant
     * @param id l'identifiant de la réaction
     * @return la réaction récupérée ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao 
     */
    public ReactionActivity get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReactionActivity activity = null;

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
     * la requête SQL permettant de récupérer une réaction selon un utilisateur et une publication
     */
    private static final String SQL_SELECT_BY_HUMAN_AND_POST = "SELECT a.id as id, date, id_human, id_post, reaction FROM reactionactivity r LEFT JOIN activity a ON r.id = a.id WHERE id_human = ? AND id_post = ?";
    /**
     * récupérer une réaction selon un utilisateur et une publication
     * @param id_human l'identifiant de l'utilisateur
     * @param id_post l'identifiant de la publication
     * @return la réaction récupérée ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao 
     */
    public ReactionActivity get(int id_human, int id_post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReactionActivity activity = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_HUMAN_AND_POST, false, id_human, id_post );
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
     * la requête SQL permettant de récupérer les réactions d'un utilisateur
     */
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT a.id as id, date, id_human, id_post, reaction FROM reactionactivity r LEFT JOIN activity a ON r.id = a.id WHERE id_human = ?";
    /**
     * récupérer les réactions d'un utilisateur
     * @param id_human l'identifiant de l'utilisateur
     * @return les réactions de l'utilisateur (ou une ArrayList vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao 
     */
    public ArrayList<ReactionActivity> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReactionActivity activity = null;
        ArrayList<ReactionActivity> activities = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
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
     * récupérer les réactions d'un utilisateur
     * @param id_human id_human l'identifiant de l'utilisateur
     * @return les réactions de l'utilisateur (ou une HashMap vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao 
     */
    public Map<Integer, ReactionActivity> getHashByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ReactionActivity activity = null;
        Map<Integer, ReactionActivity> activities = new HashMap<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
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
     * la requête SQL permettant de supprimer une réaction
     */
    private static final String SQL_DELETE= "DELETE FROM reactionactivity WHERE id = ?";
    /**
     * supprimer une réaction
     * @param id l'identifiant de la réaction
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao 
     */
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, "DELETE FROM activity WHERE id = ?");
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
