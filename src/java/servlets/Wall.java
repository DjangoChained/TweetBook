/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.DislikeActivity;
import beans.FriendshipActivity;
import beans.Human;
import beans.LikeActivity;
import beans.LinkPost;
import beans.TextPost;
import dao.DAOException;
import dao.DAOFactory;
import dao.DislikeActivityDao;
import dao.FriendshipActivityDao;
import dao.LikeActivityDao;
import dao.LinkPostDao;
import dao.TextPostDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Wall", urlPatterns = {"/wall"})
public class Wall extends HttpServlet {
    
    public static final String ATT_SESSION_USER = "sessionHuman";
    public static final String CONF_DAO_FACTORY = "daofactory";
    private LikeActivityDao likeActivityDao;
    private DislikeActivityDao dislikeActivityDao;
    private TextPostDao textPostDao;
    private LinkPostDao linkPostDao;
    private FriendshipActivityDao friendshipDao;
    
    @Override
    public void init() throws ServletException {
        this.likeActivityDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getLikeActivityDao();
        this.dislikeActivityDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getDislikeActivityDao();
        this.textPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getTextPostDao();
        this.linkPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getLinkPostDao();
        this.friendshipDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getFriendshipActivityDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        PrintWriter out = response.getWriter();
        
        try {
            Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
            ArrayList<LikeActivity> likes = likeActivityDao.getByHuman(human.getId());
            ArrayList<DislikeActivity> dislikes = dislikeActivityDao.getByHuman(human.getId());
            ArrayList<TextPost> textPosts = textPostDao.getByHuman(human.getId());
            ArrayList<LinkPost> linkPosts = linkPostDao.getByHuman(human.getId());
            ArrayList<FriendshipActivity> friends = friendshipDao.getByHuman(human.getId());
            
            out.println("{\n" +
                            "    \"status\": \"success\",\n" +
                            "    \"activities\": [\n");
            for(LikeActivity act : likes){
                out.println("{" +
                            "   \"type\": \"like\", " +
                            "   \"id\": \""+act.getId()+"\", " +
                            "   \"date\": \""+act.getDate()+"\", " +
                            "   \"id_post\": \""+act.getId_post()+"\" " +
                            "},\n");
            }
            for(DislikeActivity act : dislikes){
                out.println("{" +
                            "   \"type\": \"dislike\", " +
                            "   \"id\": \""+act.getId()+"\", " +
                            "   \"date\": \""+act.getDate()+"\", " +
                            "   \"id_post\": \""+act.getId_post()+"\" " +
                            "},\n");
            }
            for(TextPost post : textPosts){
                out.println("{" +
                            "   \"type\": \"text\", " +
                            "   \"id\": \""+post.getId()+"\", " +
                            "   \"date\": \""+post.getDate()+"\", " +
                            "   \"id_human\": \""+post.getId_human()+"\", " +
                            "   \"content\": \""+post.getContent()+"\" " +
                            "},\n");
            }
            for(LinkPost post : linkPosts){
                out.println("{" +
                            "   \"type\": \"link\", " +
                            "   \"id\": \""+post.getId()+"\", " +
                            "   \"date\": \""+post.getDate()+"\", " +
                            "   \"id_human\": \""+post.getId_human()+"\", " +
                            "   \"url\": \""+post.getUrl()+"\", " +
                            "   \"title\": \""+post.getTitle()+"\", " +
                            "   \"content\": \""+post.getContent()+"\" " +
                            "},\n");
            }
            for(FriendshipActivity act : friends){
                out.println("{" +
                            "   \"type\": \"text\", " +
                            "   \"id\": \""+act.getId()+"\", " +
                            "   \"date\": \""+act.getDate()+"\", " +
                            "   \"id_human\": \""+act.getId_human()+"\", " +
                            "   \"id_second_human\": \""+act.getId_second_human()+"\" " +
                            "},\n");
            }
            out.println("{}\n");
            out.println("]\n}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\"}");
            
        }
        
        
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
