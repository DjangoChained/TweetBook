package config;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import dao.DAOFactory;
/**
 *
 * @author pierant
 */
public class InitialiseDaoFactory implements ServletContextListener {
    private static final String ATT_DAO_FACTORY = "daofactory";
    private DAOFactory daoFactory;

    @Override
    public void contextInitialized( ServletContextEvent event ) {
        ServletContext servletContext = event.getServletContext();
        this.daoFactory = DAOFactory.getInstance(servletContext);
        servletContext.setAttribute( ATT_DAO_FACTORY, this.daoFactory );
    }

    @Override
    public void contextDestroyed( ServletContextEvent event ) {
    }
}
