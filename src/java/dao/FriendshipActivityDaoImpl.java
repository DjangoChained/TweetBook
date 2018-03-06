/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.Activity;
import beans.FriendshipActivity;
import static dao.DAOImpl.fermeturesSilencieuses;
import static dao.DAOImpl.initialisationRequetePreparee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 *
 * @author pierant
 */
public class FriendshipActivityDaoImpl extends BasicDaoImpl implements FriendshipActivityDao {
    private DAOFactory daoFactory;
    
    public FriendshipActivityDaoImpl(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    private static FriendshipActivity map( ResultSet resultSet ) throws SQLException {
        FriendshipActivity activity = new FriendshipActivity();
        activity.setId(resultSet.getInt( "id"));
        activity.setDate(resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        activity.setId_human(resultSet.getInt("id_human"));
        activity.setId_second_human(resultSet.getInt("id_second_human"));
        
        return activity;
    }
    
    private static final String SQL_INSERT = "INSERT INTO friendshipactivity (id, id_second_human) VALUES (?, ?)";
    @Override
    public FriendshipActivity create(FriendshipActivity activity) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;
        
        try {
            int id_activity = super.createActivity(daoFactory, activity.getDate(), activity.getId_human());
            
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_INSERT, true, id_activity, activity.getId_second_human());
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
            fermeturesSilencieuses( valeursAutoGenerees, preparedStatement, connexion );
        }
        return activity;
    }

    //private static final String SQL_SELECT_ALL = "SELECT id, date, id_human, id_second_human FROM friendshipactivity";
    private static final String SQL_SELECT_ALL = "SELECT a.id as id, id_second_human, date, id_human FROM friendshipactivity f INNER JOIN activity a ON f.id = a.id";
    @Override
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
            fermeturesSilencieuses( result, preparedStatement, connexion );
        }

        return activities;
    }

    //private static final String SQL_SELECT_BY_ID = "SELECT id, date, id_human, id_second_human FROM friendshipactivity WHERE id = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT a.id as id, id_second_human, date, id_human FROM friendshipactivity f INNER JOIN activity a ON f.id = a.id WHERE a.id = ?";
    @Override
    public FriendshipActivity get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FriendshipActivity activity = null;

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
    
    private static final String SQL_SELECT_FRIENDS = "SELECT a.id as id, id_second_human, id_human FROM friendshipactivity f INNER JOIN activity a ON f.id = a.id WHERE id_human = ? OR id_second_human = ?";
    @Override
    public ArrayList<Integer> getFriends(int id_human) throws DAOException { 
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Integer> friends = new ArrayList<>();

    try {
        connexion = daoFactory.getConnection();
        preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_FRIENDS, false, id_human, id_human );
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
        fermeturesSilencieuses( resultSet, preparedStatement, connexion );
    }

    return friends;
}
    
    //private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT id, date, id_human, id_second_human FROM friendshipactivity WHERE id_human = ? OR id_second_human = ?";
    private static final String SQL_SELECT_BY_ID_HUMAN = "SELECT a.id as id, id_second_human, date, id_human FROM friendshipactivity f INNER JOIN activity a ON f.id = a.id WHERE id_human = ? OR id_second_human = ?";
    @Override
    public ArrayList<FriendshipActivity> getByHuman(int id_human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        FriendshipActivity activity = null;
        ArrayList<FriendshipActivity> activities = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human, id_human );
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
    
    private static final String SQL_SELECT_BY_FRIENDS = "SELECT a.id as id FROM friendshipactivity f INNER JOIN activity a ON f.id = a.id WHERE (id_human = ? OR id_second_human = ?) AND (id_human = ? OR id_second_human = ?)";
    @Override
    public int getByFriends(int id_human, int id_friend) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int id_friendship_activity = -1;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_SELECT_BY_ID_HUMAN, false, id_human, id_friend );
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                id_friendship_activity = resultSet.getInt("id");
            }
            
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }
        return id_friendship_activity;
    }

    private static final String SQL_DELETE= "DELETE FROM friendshipactivity WHERE id = ?";
    @Override
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, "DELETE FROM activity WHERE id = ?");
        super.delete(daoFactory, id, SQL_DELETE);
    }
}
