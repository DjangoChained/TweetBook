package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;

/**
 *
 *
 */
public class DAOFactory {

    private static final String PROPERTY_URL      = "jdbc-url";
    private static final String PROPERTY_DRIVER   = "jdbc-driver";
    private static final String PROPERTY_USERNAME = "jdbc-username";
    private static final String PROPERTY_PASSWORD = "jdbc-password";

    private final String url, username, password;

    DAOFactory( String url, String username, String password ) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /*
     * Méthode chargée de récupérer les informations de connexion à la base de
     * données, charger le driver JDBC et retourner une instance de la Factory
     */
    public static DAOFactory getInstance(ServletContext context) throws DAOConfigurationException {
        String url, driver, username, password;
        url = context.getInitParameter( PROPERTY_URL );
        driver = context.getInitParameter( PROPERTY_DRIVER );
        username = context.getInitParameter( PROPERTY_USERNAME );
        password = context.getInitParameter(PROPERTY_PASSWORD );

        try {
            Class.forName( driver );
        } catch ( ClassNotFoundException e ) {
            throw new DAOConfigurationException( "Le driver est introuvable dans le classpath.", e );
        }

        DAOFactory instance = new DAOFactory( url, username, password );
        return instance;
    }

    /* Méthode chargée de fournir une connexion à la base de données */
     /* package */ Connection getConnection() throws SQLException {
        return DriverManager.getConnection( url, username, password );
    }

    /*
     * Méthodes de récupération de l'implémentation des différents DAO (un seul
     * pour le moment)
     */

    /**
     *
     * @return
     */

    public HumanDao getHumanDao() {
        return new HumanDao(this);
    }

    /**
     *
     * @return
     */
    public TextPostDao getTextPostDao() {
        return new TextPostDao(this);
    }

    /**
     *
     * @return
     */
    public LinkPostDao getLinkPostDao() {
        return new LinkPostDao(this);
    }

    /**
     *
     * @return
     */
    public PhotoPostDao getPhotoPostDao() {
        return new PhotoPostDao(this);
    }

    /**
     *
     * @return
     */
    public FriendshipActivityDao getFriendshipActivityDao() {
        return new FriendshipActivityDao(this);
    }

    /**
     *
     * @return
     */
    public ReactionActivityDao getReactionActivityDao() {
        return new ReactionActivityDao(this);
    }
}
