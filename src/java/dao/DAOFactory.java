package dao;

import beans.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DAOFactory {

    private static final String PROPERTIES_FILEPATH       = "/dao/dao.properties";
    private static final String PROPERTY_URL             = "url";
    private static final String PROPERTY_DRIVER          = "driver";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_MOT_DE_PASSE    = "password";

    private String              url;
    private String              username;
    private String              password;

    DAOFactory( String url, String username, String password ) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /*
     * Méthode chargée de récupérer les informations de connexion à la base de
     * données, charger le driver JDBC et retourner une instance de la Factory
     */
    public static DAOFactory getInstance() throws DAOConfigurationException {
        Properties properties = new Properties();
        String url;
        String driver;
        String username;
        String password;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream fichierProperties = classLoader.getResourceAsStream( PROPERTIES_FILEPATH );

        if ( fichierProperties == null ) {
            throw new DAOConfigurationException( "Le fichier properties " + PROPERTIES_FILEPATH + " est introuvable." );
        }

        try {
            properties.load( fichierProperties );
            url = properties.getProperty( PROPERTY_URL );
            driver = properties.getProperty( PROPERTY_DRIVER );
            username = properties.getProperty( PROPERTY_USERNAME );
            password = properties.getProperty( PROPERTY_MOT_DE_PASSE );
        } catch ( IOException e ) {
            throw new DAOConfigurationException( "Impossible de charger le fichier properties " + PROPERTIES_FILEPATH, e );
        }

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
    public HumanDao getHumanDao() {
        return new HumanDaoImpl(this);
    }
    
    public TextPostDao getTextPostDao() {
        return new TextPostDaoImpl(this);
    }
    
    public LinkPostDao getLinkPostDao() {
        return new LinkPostDaoImpl(this);
    }
    
    public PhotoPostDao getPhotoPostDao() {
        return new PhotoPostDaoImpl(this);
    }
    
    public FriendshipActivityDao getFriendshipActivityDao() {
        return new FriendshipActivityDaoImpl(this);
    }
    
    public LikeActivityDao getLikeActivityDao() {
        return new LikeActivityDaoImpl(this);
    }
    
    public DislikeActivityDao getDislikeActivityDao() {
        return new DislikeActivityDaoImpl(this);
    }
}