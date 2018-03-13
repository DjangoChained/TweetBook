package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import static dao.DAO.initialisePreparedStatement;
import static dao.DAO.quietClose;
import static dao.DAO.quietClose;

/**
 * Classe de base comprenant des méthodes utilitaires pour les Dao 
 */
public abstract class BasicDao {
    
    /**
     * Permet de supprimer un humain ou une activité en base
     * @param daoFactory objet permettant de se connecter à la base de données
     * @param id l'identifiant en base de l'élément à supprimer
     * @param request la requête SQL qui permettra la suppression
     * @throws DAOException
     */
    public void delete(DAOFactory daoFactory, int id, String request) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, request, false, id );
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }
    }
    
    /**
     * Permet de créer une activité (méthode utilisée en complément d'une autre pour simuler l'héritage)
     * @param daoFactory objet permettant de se connecter à la base de données
     * @param date date de création de l'activité
     * @param id_human identifiant en base de l'utilisateur ayant produit l'activité
     * @return l'identifiant en base de l'activité créée
     * @throws DAOException lors d'une erreur SQL
     */
    public int createActivity(DAOFactory daoFactory, LocalDateTime date, int id_human) throws DAOException {
        String request = "INSERT INTO activity(date, id_human) VALUES (?, ?)";
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, request, true, Timestamp.valueOf(date), id_human );
            int status = preparedStatement.executeUpdate();
            if ( status == 0 ) {
                throw new DAOException( "Échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
            }
            resultSet = preparedStatement.getGeneratedKeys();
            if ( resultSet.next() ) {
                Long res = resultSet.getLong(1);
                return res.intValue();
            } else {
                throw new DAOException( "Échec de la création de l'utilisateur en base, aucun ID auto-généré retourné." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }
    }
    
    /**
     * Permet de créer un post (méthode utilisée en complément d'une autre pour simuler l'héritage)
     * @param daoFactory objet permettant de se connecter à la base de données
     * @param id identifiant en base du post
     * @param content le contenu du post au format texte
     * @throws DAOException lors d'une erreur SQL
     */
    public void createPost(DAOFactory daoFactory, int id, String content) throws DAOException {
        String request = "INSERT INTO post(id, content) VALUES (?, ?)";
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, request, true, id, content );
            int status = preparedStatement.executeUpdate();
            if ( status == 0 ) {
                throw new DAOException( "Échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }
    }
    
    /**
     * mettre à jour une activité
     * @param daoFactory objet permettant de se connecter à la base de données
     * @param date date de création de l'activité
     * @param id_human identifiant en base de l'utilisateur ayant produit l'activité
     * @param id identifiant en base de l'activité à mettre à jour
     * @throws DAOException lors d'une erreur SQL
     */
    public void updateActivity(DAOFactory daoFactory, LocalDateTime date, int id_human, int id) throws DAOException {
        String request = "UPDATE activity SET date = ?, id_human = ? WHERE id = ?";
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, request, true, Timestamp.valueOf(date), id_human, id);
            int status = preparedStatement.executeUpdate();
            if ( status == 0 ) {
                throw new DAOException( "Échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }
    }
    
    /**
     * mettre à jour un post
     * @param daoFactory objet permettant de se connecter à la base de données
     * @param id identifiant en base du post à mettre à jour
     * @param content le contenu du post au format texte
     * @throws DAOException lors d'une erreur SQL
     */
    public void updatePost(DAOFactory daoFactory, int id, String content) throws DAOException {
        String request = "UPDATE post SET content = ? WHERE id = ?";
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connexion = daoFactory.getConnection();
            preparedStatement = initialisePreparedStatement( connexion, request, true, id, content);
            int status = preparedStatement.executeUpdate();
            if ( status == 0 ) {
                throw new DAOException( "Échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            quietClose( resultSet, preparedStatement, connexion );
        }
    }
}
