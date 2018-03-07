/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.Activity;
import beans.DislikeActivity;
import beans.Reaction;
import static dao.DAOImpl.fermeturesSilencieuses;
import static dao.DAOImpl.initialisationRequetePreparee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pierant
 */
public class DislikeActivityDaoImpl extends BasicDaoImpl implements DislikeActivityDao {
    private DAOFactory daoFactory;
    
    public DislikeActivityDaoImpl(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    private static DislikeActivity map( ResultSet resultSet ) throws SQLException {
        DislikeActivity activity = new DislikeActivity();
        activity.setId(resultSet.getInt( "id"));
        activity.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        activity.setId_human(resultSet.getInt("id_human"));
        activity.setReaction(Reaction.DISLIKE);
        activity.setId_post(resultSet.getInt("id_post"));
        
        return activity;
    }
    
    private static final String SQL_INSERT = "INSERT INTO reactionactivity (id, reaction, id_post) VALUES (?, ?::reaction, ?)";
    @Override
    public DislikeActivity create(DislikeActivity activity) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            int id_activity = super.createActivity(daoFactory, activity.getDate(), activity.getId_human());
            
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_INSERT, true, id_activity, activity.getReaction().toString(), activity.getId_post());
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
            fermeturesSilencieuses( valeursAutoGenerees, preparedStatement, connexion );
        }
        return activity;
    }
    
    private static final String SQL_SELECT_ALL = "SELECT a.id as id, date, id_human, id_post FROM reactionactivity r INNER JOIN activity a ON r.id = a.id  WHERE r.reaction = 'dislike'";
    @Override
    public ArrayList<DislikeActivity> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        DislikeActivity activity = null;
        ArrayList<DislikeActivity> activities = new ArrayList<>();

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
            fermeturesSilencieuses( result, preparedStatement, connexion );
        }

        return activities;
    }
    
    private static final String SQL_SELECT_BY_ID = "SELECT a.id as id, date, id_human, id_post FROM reactionactivity r INNER JOIN activity a ON r.id = a.id WHERE a.id = ?";
    @Override
    public DislikeActivity get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DislikeActivity activity = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID, false, id );
            resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() ) {
                activity = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return activity;
    }
    
    private static final String SQL_SELECT_BY_HUMAN_AND_POST = "SELECT a.id as id, date, id_human, id_post FROM reactionactivity r INNER JOIN activity a ON r.id = a.id WHERE id_human = ? AND id_post = ? AND reaction = 'dislike'";
    @Override
    public DislikeActivity get(int id_human, int id_post) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DislikeActivity activity = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_HUMAN_AND_POST, false, id_human, id_post );
            resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() ) {
                activity = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return activity;
    }
    
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT a.id as id, date, id_human, id_post FROM reactionactivity r INNER JOIN activity a ON r.id = a.id WHERE id_human = ? AND reaction = 'dislike'";
    @Override
    public ArrayList<DislikeActivity> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DislikeActivity activity = null;
        ArrayList<DislikeActivity> activities = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                activity = map( resultSet );
                activities.add(activity);
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return activities;
    }
    
    @Override
    public Map<Integer, DislikeActivity> getHashByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DislikeActivity activity = null;
        Map<Integer, DislikeActivity> activities = new HashMap<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                activity = map( resultSet );
                activities.put(activity.getId(), activity);
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return activities;
    }

    private static final String SQL_DELETE= "DELETE FROM reactionactivity WHERE id = ?";
    @Override
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, "DELETE FROM activity WHERE id = ?");
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
