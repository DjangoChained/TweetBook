/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import beans.ActivityVisibility;
import static dao.DAO.*;
import beans.Human;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 *
 */
public class HumanDao extends BasicDao {
    private final DAOFactory daoFactory;
    
    /**
     *
     * @param daoFactory
     */
    public HumanDao(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    private static Human map( ResultSet result ) throws SQLException {
        Human human = new Human();
        human.setId( result.getInt( "id" ) );
        human.setLastName( result.getString( "lastname" ) );
        human.setFirstName( result.getString( "firstname" ) );
        human.setBirthDate( result.getTimestamp("birthdate").toLocalDateTime());
        human.setEmail( result.getString( "email" ) );
        human.setUsername( result.getString( "username" ) );
        human.setPassword( result.getString( "password" ) );
        human.setVisibility(ActivityVisibility.valueOf(result.getString( "activityvisibility")));
        
        return human;
    }
    
    private static final String SQL_INSERT = "INSERT INTO human (lastname, firstname, birthdate, email, username, password, activityvisibility) VALUES (?, ?, ?, ?, ?, ?, ?::activityvisibility)";
    
    /**
     *
     * @param human
     * @throws DAOException
     */
    public void create(Human human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_INSERT, true, human.getLastName(), human.getFirstName(), Timestamp.valueOf(human.getBirthDate()),
                                                               human.getEmail(), human.getUsername(), human.getPassword(), human.getVisibility().toString());
            int status = preparedStatement.executeUpdate();
            /* Analyse du statut retourné par la requête d'insertion */
            if ( status == 0 ) {
                throw new DAOException( "Échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
            }
            /* Récupération de l'id auto-généré par la requête d'insertion */
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
            if ( valeursAutoGenerees.next() ) {
                human.setId( valeursAutoGenerees.getInt( 1 ) );
            } else {
                throw new DAOException( "Échec de la création de l'utilisateur en base, aucun ID auto-généré retourné." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( valeursAutoGenerees, preparedStatement, connexion );
        }
    }
    
    private static final String SQL_SELECT_ALL = "SELECT id, lastname, firstname, birthdate, email, username, password, activityvisibility FROM human";
    
    /**
     *
     * @return
     * @throws DAOException
     */
    public ArrayList<Human> getAll() throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        Human human = null;
        ArrayList<Human> humans = new ArrayList<>();

        try {
            connexion = daoFactory.getConnection();
            Statement statement = connexion.createStatement();
            result = statement.executeQuery(SQL_SELECT_ALL);
            while(result.next()) {
                human = map(result);
                humans.add(human);
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( result, preparedStatement, connexion );
        }

        return humans;
    }
    
    private static final String SQL_SELECT_BY_ID = "SELECT id, lastname, firstname, birthdate, email, username, password, activityvisibility FROM human WHERE id = ?";
    
    /**
     *
     * @param id
     * @return
     * @throws DAOException
     */
    public Human get(int id) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Human human = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_ID, false, id );
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de données de l'éventuel ResulSet retourné */
            if ( resultSet.next() ) {
                human = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return human;
    }
    
    private static final String SQL_SELECT_BY_EMAIL = "SELECT id, lastname, firstname, birthdate, email, username, password, activityvisibility FROM human WHERE email = ?";
    
    /**
     *
     * @param email
     * @return
     * @throws DAOException
     */
    public Human get(String email) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Human human = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_SELECT_BY_EMAIL, false, email );
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de données de l'éventuel ResulSet retourné */
            if ( resultSet.next() ) {
                human = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return human;
    }
    
    /**
     *
     * @param friends_ids
     * @return
     * @throws DAOException
     */
    public ArrayList<Human> getFriends(ArrayList<Integer> friends_ids) throws DAOException {
        ArrayList<Human> humans = new ArrayList<>();

        for (Integer id: friends_ids) {
            Human h = this.get(id);
            if(h.getVisibility() != ActivityVisibility.authoronly)
                humans.add(h);
        }

        return humans;
    }
    
    private static final String SQL_UPDATE = "UPDATE human SET lastname = ?, firstname = ?, birthdate = ?, email = ?, username = ?, activityvisibility = ?::activityvisibility WHERE id = ?";
    
    /**
     *
     * @param human
     * @throws DAOException
     */
    public void update(Human human) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            String visibility = "all";
            if (human.getVisibility() == ActivityVisibility.authoronly)
                visibility = "authoronly";
            else if (human.getVisibility() == ActivityVisibility.friends)
                visibility = "friends";
            preparedStatement = initialisePreparedStatement( connexion, SQL_UPDATE, false, human.getLastName(), human.getFirstName(), human.getBirthDate(),
                                                               human.getEmail(), human.getUsername(), human.getVisibility().toString(), human.getId());
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }
    }

    private static final String SQL_DELETE= "DELETE FROM human WHERE id = ?";
    
    /**
     *
     * @param id
     * @throws DAOException
     */
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, SQL_DELETE);
    }
    
    private static final String SQL_UPDATE_PASSWORD= "UPDATE human SET password = ? WHERE id = ?";
    
    /**
     *
     * @param human
     * @param password
     * @throws DAOException
     */
    public void updatePassword(Human human, String password) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, SQL_UPDATE_PASSWORD, false, password, human.getId());
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }
    }
}
