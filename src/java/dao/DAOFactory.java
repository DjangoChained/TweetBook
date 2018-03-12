package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;

/**
 * Classe permettant d'instancier les Dao
 */
public class DAOFactory {

    /**
     * L'url, le nom d'utilisateur et le mot de passe permettant de se connecter à la base de données
     */
    private final String url, username, password;

    /**
     * Constructeur de la DaoFactory
     * @param url url permmettant de se connecter à la base de données
     * @param username nom d'utilisateur permmettant de se connecter à la base de données
     * @param password mot de passe permmettant de se connecter à la base de données
     */
    DAOFactory( String url, String username, String password ) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Méthode chargée de récupérer les informations de connexion à la base de
     * données, charger le driver JDBC et retourner une instance de la Factory
     * @param context le contexte de l'application web
     * @return une instance de la DAOFactory
     * @throws DAOConfigurationException 
     */
    public static DAOFactory getInstance(ServletContext context) throws DAOConfigurationException {
        String url, driver, username, password;
        url = context.getInitParameter("jdbc-url");
        driver = context.getInitParameter("jdbc-driver");
        username = context.getInitParameter("jdbc-username");
        password = context.getInitParameter("jdbc-password");

        try {
            Class.forName( driver );
        } catch ( ClassNotFoundException e ) {
            throw new DAOConfigurationException( "Le driver est introuvable dans le classpath.", e );
        }

        DAOFactory instance = new DAOFactory( url, username, password );
        return instance;
    }

    /**
     * Méthode chargée de fournir une connexion à la base de données 
     * @return la connexion à la base de données
     * @throws SQLException lorsque qu'une erreur SQL est survenue
     */
    Connection getConnection() throws SQLException {
        return DriverManager.getConnection( url, username, password );
    }
    
    /**
     * Méthode de récupération de l'implémentation du Dao gérant les utilisateurs
     * @return Dao gérant les utilisateurs
     */
    public HumanDao getHumanDao() {
        return new HumanDao(this);
    }

    /**
     * Méthode de récupération de l'implémentation du Dao gérant les post contenant du texte
     * @return Dao gérant les post contenant du texte
     */
    public TextPostDao getTextPostDao() {
        return new TextPostDao(this);
    }

    /**
     * Méthode de récupération de l'implémentation du Dao gérant les post contenant un lien
     * @return Dao gérant les post contenant un lien
     */
    public LinkPostDao getLinkPostDao() {
        return new LinkPostDao(this);
    }

    /**
     * Méthode de récupération de l'implémentation du Dao gérant les post contenant une photo
     * @return Dao gérant les post contenant une photo
     */
    public PhotoPostDao getPhotoPostDao() {
        return new PhotoPostDao(this);
    }

    /**
     * Méthode de récupération de l'implémentation du Dao gérant les amis
     * @return Dao gérant les amis
     */
    public FriendshipActivityDao getFriendshipActivityDao() {
        return new FriendshipActivityDao(this);
    }

    /**
     * Méthode de récupération de l'implémentation du Dao gérant les réactions à des posts
     * @return Dao gérant les réactions à des posts
     */
    public ReactionActivityDao getReactionActivityDao() {
        return new ReactionActivityDao(this);
    }
}
