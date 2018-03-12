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
 * Implémentation du Dao gérant les utilisateurs
 */
public class HumanDao extends BasicDao {
    /**
     * permet de récupérer une connexion à la base de données
     */
    private final DAOFactory daoFactory;
    
    /**
     * Constucteur du Dao gérant les utilisateurs
     * @param daoFactory Classe permettant de récupérer une connexion à la base de données
     */
    public HumanDao(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    /**
     * instancier un utilisateur
     * @param result resultSet permmettant de récupérer les données d'un utilisateur
     * @return l'utilisateur créé ou null
     * @throws SQLException lorsqu'une erreur SQL est survenue
     */
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
    
    /**
     * la requête SQL permettant de créer un utilisateur
     */
    private static final String SQL_INSERT = "INSERT INTO human (lastname, firstname, birthdate, email, username, password, activityvisibility) VALUES (?, ?, ?, ?, ?, ?, ?::activityvisibility)";
    
    /**
     * créer un utilisateur
     * @param human l'utilisateur à créer
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
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
            quietClose( valeursAutoGenerees, preparedStatement, connexion );
        }
    }
    
    /**
     * la requête SQL permettant de récupérer tous les utilisateurs
     */
    private static final String SQL_SELECT_ALL = "SELECT id, lastname, firstname, birthdate, email, username, password, activityvisibility FROM human";
    
    /**
     * récupérer tous les utilisateurs
     * @return tous les utilisateurs récupérés (ou une ArrayListe vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
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
            quietClose( result, preparedStatement, connexion );
        }

        return humans;
    }
    
    /**
     * requête SQL permettant de récupérer un utilisateur par son identifiant
     */
    private static final String SQL_SELECT_BY_ID = "SELECT id, lastname, firstname, birthdate, email, username, password, activityvisibility FROM human WHERE id = ?";
    
    /**
     * récupérer un utilisateur par son identifiant
     * @param id l'identifiant en base de l'utilisateur
     * @return l'utilisateur récupéré ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
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
            quietClose( resultSet, preparedStatement, connexion );
        }

        return human;
    }
    
    /**
     * requête SQL permettant de récupérer un utilisateur par son adresse mail(qui est unique en base)
     */
    private static final String SQL_SELECT_BY_EMAIL = "SELECT id, lastname, firstname, birthdate, email, username, password, activityvisibility FROM human WHERE email = ?";
    
    /**
     * récupérer un utilisateur par son adresse mail
     * @param email adresse mail de l'utilisateur
     * @return l'utilisateur récupéré ou null
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
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
            quietClose( resultSet, preparedStatement, connexion );
        }

        return human;
    }
    
    /**
     * récupérer tous les amis d'un utilisateur à partir de leur identifiant
     * @param friends_ids les identifiants des amis de l'utilisateur
     * @return les amis récupérés (ou une ArrayList vide)
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
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
    
    /**
     * la requête SQL permettant de mettre à jour un utilisateur
     */
    private static final String SQL_UPDATE = "UPDATE human SET lastname = ?, firstname = ?, birthdate = ?, email = ?, username = ?, activityvisibility = ?::activityvisibility WHERE id = ?";
    
    /**
     * mettre à jour un utilisateur
     * @param human l'utilisateur à mettre à jour
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
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
            quietClose( resultSet, preparedStatement, connexion );
        }
    }
    
    /**
     * requête SQL permattant de supprimer un utilisateur
     */
    private static final String SQL_DELETE= "DELETE FROM human WHERE id = ?";
    
    /**
     * supprimer un utilisateur
     * @param id
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
     */
    public void delete(int id) throws DAOException {
        super.delete(daoFactory, id, SQL_DELETE);
    }
    
    /**
     * la requête SQL permettant de mettre à jour le mot de passe d'un utilisateur
     */
    private static final String SQL_UPDATE_PASSWORD= "UPDATE human SET password = ? WHERE id = ?";
    /**
     * mettre à jour le mot de passe d'un utilisateur
     * @param human l'utilisateur dont le mot de passe va être mis à jour
     * @param password le nouveau mot de passe de l'utilisateur
     * @throws DAOException lorsqu'une erreur est survenue dans le Dao
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
            quietClose( resultSet, preparedStatement, connexion );
        }
    }
}
