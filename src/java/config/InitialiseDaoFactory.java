package config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import dao.DAOFactory;

/**
 * Classe permettant d'initialiser l'objet qui instancie les Dao 
 */
public class InitialiseDaoFactory implements ServletContextListener {
    /**
     * le nom de l'objet qui instancie les Dao
     */
    private static final String ATT_DAO_FACTORY = "daofactory";
    private DAOFactory daoFactory;

    /**
     * Créé l'objet qui instancie les Dao au démarrage de l'application web
     */
    @Override
    public void contextInitialized( ServletContextEvent event ) {
        ServletContext servletContext = event.getServletContext();
        this.daoFactory = DAOFactory.getInstance(servletContext);
        servletContext.setAttribute( ATT_DAO_FACTORY, this.daoFactory );
    }
}
