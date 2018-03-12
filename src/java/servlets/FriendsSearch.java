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
 *
 *
 */
@WebServlet(name = "FriendsSearch", urlPatterns = {"/friends/search"})
public class FriendsSearch extends HttpServlet {
    
    /**
     *
     */
    public static final String ATT_SESSION_USER = "sessionHuman";

    /**
     *
     */
    public static final String CONF_DAO_FACTORY = "daofactory";
    private HumanDao humanDao;
    private FriendshipActivityDao friendshipDao;
    
    /**
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        this.friendshipDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getFriendshipActivityDao();
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
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
