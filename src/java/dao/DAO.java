package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe offrant aux Dao des méthodes utilitaires 
 */
public class DAO {

    /**
     * Permet d'initialiser une requête préparée avec des arguments
     * @param connection connexion à la base de données
     * @param sql la requête qui sera effectuée en base
     * @param returnGeneratedKeys indique si la méthode doit renvoyer l'id qui a été généré en base
     * @param objets tous les arguments de la requête préparée
     * @return la requête préparée
     * @throws SQLException lorsqu'une erreur SQL est survenue
     */
    public static PreparedStatement initialisePreparedStatement( Connection connection, String sql, boolean returnGeneratedKeys, Object... objets ) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement( sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS );

        for ( int i = 0; i < objets.length; i++ ) {
            preparedStatement.setObject( i + 1, objets[i] );
        }
        return preparedStatement;
    }
    
    /**
     * Fermeture silencieuse du resultset
     * @param resultSet
     */
    public static void quietClose( ResultSet resultSet ) {
        if ( resultSet != null ) {
            try {
                resultSet.close();
            } catch ( SQLException e ) {
                System.out.println( "Échec de la fermeture du ResultSet : " + e.getMessage() );
            }
        }
    }

    /**
     * Fermeture silencieuse du statement
     * @param statement
     */
    public static void quietClose( Statement statement ) {
        if ( statement != null ) {
            try {
                statement.close();
            } catch ( SQLException e ) {
                System.out.println( "Échec de la fermeture du Statement : " + e.getMessage() );
            }
        }
    }

    /**
     * Fermeture silencieuse de la connexion
     * @param connexion
     */
    public static void quietClose( Connection connexion ) {
        if ( connexion != null ) {
            try {
                connexion.close();
            } catch ( SQLException e ) {
                System.out.println( "Échec de la fermeture de la connexion : " + e.getMessage() );
            }
        }
    }

    /**
     * Fermetures silencieuses du statement et de la connexion
     * @param statement
     * @param connexion
     */
    public static void quietClose( Statement statement, Connection connexion ) {
        DAO.quietClose( statement );
        DAO.quietClose( connexion );
    }

    /**
     * Fermetures silencieuses du resultset, du statement et de la connexion
     * @param resultSet
     * @param statement
     * @param connexion
     */
    public static void quietClose( ResultSet resultSet, Statement statement, Connection connexion ) {
        DAO.quietClose( resultSet );
        DAO.quietClose( statement );
        DAO.quietClose( connexion );
    }
}
