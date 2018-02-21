/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.Activity;
import beans.Reaction;
import beans.LikeActivity;
import static dao.DAOImpl.fermeturesSilencieuses;
import static dao.DAOImpl.initialisationRequetePreparee;
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
public class LikeActivityDaoImpl extends BasicDaoImpl implements LikeActivityDao {
    private DAOFactory daoFactory;
    
    public LikeActivityDaoImpl(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    private static LikeActivity map( ResultSet resultSet ) throws SQLException {
        LikeActivity activity = new LikeActivity();
        activity.setId(resultSet.getInt( "id"));
        activity.setDate(resultSet.getDate("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        activity.setId_human(resultSet.getInt("id_human"));
        activity.setReaction(Reaction.LIKE);
        activity.setId_post(resultSet.getInt("id_post"));
        
        return activity;
    }
    
    private static final String SQL_INSERT = "INSERT INTO reactionactivity (date, id_human, reaction, id_post) VALUES (?, ?, ?, ?)";
    @Override
    public LikeActivity create(LikeActivity activity) throws IllegalArgumentException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_INSERT, true, activity.getDate(), activity.getId_human(), activity.getReaction(), activity.getId_post());
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

    private static final String SQL_SELECT_ALL = "SELECT id, date, id_human, id_post FROM reactionactivity WHERE reaction = 'like'";
    @Override
    public ArrayList<Activity> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        LikeActivity activity = null;
        ArrayList<Activity> activities = new ArrayList<>();

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

    private static final String SQL_SELECT_BY_ID = "SELECT id, date, id_human, id_post FROM reactionactivity WHERE id = ?";
    @Override
    public LikeActivity get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        LikeActivity activity = null;

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
    
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT id, date, id_human, reaction, id_post FROM reactionactivity WHERE id_human = ? AND reaction = 'like'";
    @Override
    public ArrayList<Activity> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        LikeActivity activity = null;
        ArrayList<Activity> activities = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human );
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                if ( resultSet.next() ) {
                    activity = map( resultSet );
                    activities.add(activity);
                }
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
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
