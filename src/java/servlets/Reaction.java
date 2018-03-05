/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.DislikeActivity;
import beans.Human;
import beans.LikeActivity;
import com.google.gson.Gson;
import dao.DAOException;
import dao.DAOFactory;
import dao.DislikeActivityDao;
import dao.LikeActivityDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
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
@WebServlet(name = "Reaction", urlPatterns = {"/reaction"})
public class Reaction extends HttpServlet {
    
    public static final String ATT_SESSION_USER = "sessionHuman";
    public static final String CONF_DAO_FACTORY = "daofactory";
    private LikeActivityDao likeDao;
    private DislikeActivityDao dislikeDao;
    
    @Override
    public void init() throws ServletException {
        this.likeDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getLikeActivityDao();
        this.dislikeDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getDislikeActivityDao();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
                beans.Reaction reaction = beans.Reaction.valueOf(data.getProperty("reaction"));
                int id_human = Integer.parseInt(data.getProperty("id_human"));
                int id_post = Integer.parseInt(data.getProperty("id_post"));
                
                if (reaction == beans.Reaction.LIKE) {
                    DislikeActivity dislike = dislikeDao.get(id_human, id_post);
                    if (dislike != null){
                        dislikeDao.delete(dislike.getId());
                    }
                    LikeActivity like = new LikeActivity();
                    like.setDate(ZonedDateTime.parse(data.getProperty("date")).toLocalDateTime());
                    like.setId_human(id_human);
                    like.setId_post(id_post);
                    likeDao.create(like);
                    out.println("{\"status\": \"success\",\n\"id\": \""+like.getId()+"\")}");
                } else if (reaction == beans.Reaction.DISLIKE){
                    LikeActivity like = likeDao.get(id_human, id_post);
                    if (like != null){
                        likeDao.delete(like.getId());
                    }
                    DislikeActivity dislike = new DislikeActivity();
                    dislike.setDate(ZonedDateTime.parse(data.getProperty("date")).toLocalDateTime());
                    dislike.setId_human(id_human);
                    dislike.setId_post(id_post);
                    likeDao.create(like);
                    out.println("{\"status\": \"success\",\n\"id\": \""+dislike.getId()+"\")}");
                } else {
                    out.println("{\"status\": \"error\",\n\"message\": \"réaction invalide\"}");
                }
            } catch (DAOException e){
                out.println("{\"status\": \"error\",\n\"message\": \"Erreur lors de la réaction au post\"}");
            }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }
}
