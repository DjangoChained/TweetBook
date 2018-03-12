package servlets;

import beans.Human;
import dao.DAOException;
import dao.DAOFactory;
import dao.FriendshipActivityDao;
import dao.HumanDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet qui permet de rechercher des utilisateurs
 */
@WebServlet(name = "FriendsSearch", urlPatterns = {"/friends/search"})
public class FriendsSearch extends HttpServlet {
    
    /**
     * Le Dao qui permet de manipuler les utilisateurs
     */
    private HumanDao humanDao;
    /**
     * Le Dao qui permet de manipuler les liens d'amitié
     */
    private FriendshipActivityDao friendshipDao;
    
    /**
     * Permet d'initialiser les Dao lors de l'instanciation de la servlet
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        this.friendshipDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getFriendshipActivityDao();
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getHumanDao();
    }

    /**
     * Permet de rechercher des utilisateurs à partir d'une chaîne de caractères
     * Reçois au format JSON une chaîne de caractères de laquelle seront déduit des utilisateurs
     * dont le nom ou prénom pourraient correspondre
     * @param request la requête HTTP
     * @param response la réponse HTTP
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        Human human = (Human)request.getSession(false).getAttribute("sessionHuman");
        PrintWriter out = response.getWriter();
        try {
            String query = request.getParameter("q").toLowerCase();
            ArrayList<Human> humans = humanDao.getAll();
            humans.removeAll(friendshipDao.getByHuman(human.getId()));
            out.println(humans.stream()
                .filter(h -> h.getId() != human.getId() && (h.getFirstName() + " " + h.getLastName()).toLowerCase().contains(query))
                .map(h -> "{\"id\": \"" + h.getId() + "\", \"name\": \"" + h.getFirstName() + " " + h.getLastName() + "\"}")
                .collect(Collectors.joining(",", "{\"status\": \"success\", \"results\": [", "]}")));
        } catch (DAOException e) {
            out.println("{\"status\": \"error\", \"message\": \"Une erreur interne s'est produite lors de la recherche d'ami.\"}");
            log(e.getMessage());
        }
    }
}
