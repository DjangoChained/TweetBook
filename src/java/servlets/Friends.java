/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pierant
 */
@WebServlet(name = "Friends", urlPatterns = {"/friends"})
public class Friends extends HttpServlet {
    
    public static final String ATT_SESSION_USER = "sessionHuman";
    public static final String CONF_DAO_FACTORY = "daofactory";
    private FriendshipActivityDao friendshipDao;
    private HumanDao humanDao;
    
    @Override
    public void init() throws ServletException {
        this.friendshipDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getFriendshipActivityDao();
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        
        Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        
        PrintWriter out = response.getWriter();
        
        ArrayList<Human> friends = humanDao.getFriends(friendshipDao.getFriends(human.getId()));
        
        ArrayList<String> res = new ArrayList<>();
            
        out.print("{\"status\": \"success\"," +
                  "    \"friends\": [");
        for(Human friend : friends){
                res.add("{" +
                            "   \"id\": \""+friend.getId()+"\", " +
                            "   \"fristname\": \""+friend.getFirstName()+"\", " +
                            "   \"lastname\": \""+friend.getLastName()+"\" " +
                            "}");
        }
        int i = 0;
        for (String s : res){
            if(i++ == res.size() - 1){
                out.print(s);
            } else {
                out.print(s+",");
            }
        }
        out.print("]}");
}


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        
        Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        
        PrintWriter out = response.getWriter();
        
        try {
                FriendshipActivity act = new FriendshipActivity();
                act.setDate(ZonedDateTime.parse(data.getProperty("date")).toLocalDateTime());
                act.setId_human(human.getId());
                act.setId_second_human(Integer.parseInt(data.getProperty("id_friend")));
                friendshipDao.create(act);

                out.println("{\"status\": \"success\",\n\"id\": \""+act.getId()+"\")}");
            } catch (DAOException e){
                out.println("{\"status\": \"error\",\"message\": \"Erreur lors de la création de l'amitié\"}");
            }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        
        Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        
        PrintWriter out = response.getWriter();
        
        try {
            int id_friendship_activity = friendshipDao.getByFriends(human.getId(), Integer.parseInt(data.getProperty("id_friend")));
            friendshipDao.delete(id_friendship_activity);

            out.println("{\"status\": \"success\"}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\",\"message\": \"Erreur lors de la création de l'amitié\"}");
        }
    }

}
