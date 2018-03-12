package dao;

/**
 * Exception levée lorsqu'il y a une erreur dans la configuration du Dao
 */
public class DAOConfigurationException extends RuntimeException {

    public DAOConfigurationException( String message ) {
        super( message );
    }

    public DAOConfigurationException( String message, Throwable cause ) {
        super( message, cause );
    }

    public DAOConfigurationException( Throwable cause ) {
        super( cause );
    }
}