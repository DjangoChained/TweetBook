package servlets;

import beans.FriendshipActivity;
import beans.Human;
import com.google.gson.Gson;
import dao.DAOException;
import dao.DAOFactory;
import dao.FriendshipActivityDao;
import dao.HumanDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 *
 */
@WebServlet(name = "Friends", urlPatterns = {"/friends"})
public class Friends extends HttpServlet {
    /**
     * Dao permettant de manipuler les liens d'amitié
     */
    private FriendshipActivityDao friendshipDao;
    /**
     * Dao permettant de manipuler les utilisateurs
     */
    private HumanDao humanDao;
    
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
     * Permet de récupérer les identifiants, noms et prénoms des amis de l'utilisateur connecté au format Json
     *
     * @param request la requête HTTP
     * @param response la réponse HTTP
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        Human human = (Human)request.getSession(false).getAttribute("sessionHuman");
        
        PrintWriter out = response.getWriter();
        
        ArrayList<Human> friends = humanDao.getFriends(friendshipDao.getFriends(human.getId()));
        for (int a : friendshipDao.getFriends(human.getId())){
            log ("id ami dans boucle int: "+a);
        }
        for (Human a : friends){
            log("username :" +a.getUsername());
        }
        
        
        ArrayList<String> res = new ArrayList<>();
            
        out.print("{\"status\": \"success\"," +
                  "    \"friends\": [");
        for(Human friend : friends){
            res.add("{" +
                    "   \"id\": \""+friend.getId()+"\", " +
                    "   \"name\": \""+friend.getFirstName()+" "+friend.getLastName()+"\"}");
        }
        out.print(String.join(",", res));
        out.print("]}");
}

    /**
     * Permet d'ajouter un ami.
     * Reçois au format JSON l'identifiant de l'utilisateur à ajouter en ami ("id_friend")
     * @param request la requête HTTP
     * @param response la réponse HTTP
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        
        Human human = (Human)request.getSession(false).getAttribute("sessionHuman");
        
        PrintWriter out = response.getWriter();
        
        try {
                FriendshipActivity act = new FriendshipActivity();
                act.setDate(LocalDateTime.now());
                act.setId_human(human.getId());
                act.setId_second_human(Integer.parseInt(data.getProperty("id_friend")));
                friendshipDao.create(act);

                out.println("{\"status\": \"success\",\n\"id\": \""+act.getId()+"\"}");
            } catch (DAOException e){
                out.println("{\"status\": \"error\",\"message\": \"Erreur lors de la création de la relation d'ami\"}");
                log(e.getMessage());
            }
    }
    
    /**
     * Permet de retirer un utilisateur de sa liste d'amis.
     * Reçois au format JSON l'identifiant de l'utilisateur à retirer des amis ("id_friend")
     * @param request la requête HTTP
     * @param response la réponse HTTP
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        
        Human human = (Human)request.getSession(false).getAttribute("sessionHuman");
        
        PrintWriter out = response.getWriter();
        
        try {
            int id_friendship_activity = friendshipDao.getByFriends(human.getId(), Integer.parseInt(data.getProperty("id_friend")));
            if (id_friendship_activity == -1){
              out.println("{\"status\": \"error\",\"message\": \"Aucune relation d'amitié à supprimer.\"}");  
            } else {
                friendshipDao.delete(id_friendship_activity);
                out.println("{\"status\": \"success\"}");
            }
        } catch (NullPointerException e) {
            out.println("{\"status\": \"error\",\"message\": \"Aucune relation d'amitié à supprimer.\"}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\",\"message\": \"Erreur lors de la suppression de l'amitié.\"}");
            log(e.getMessage());
            throw e;
        }
    }

}
