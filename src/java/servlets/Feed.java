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
import dao.HumanDao;
import dao.LikeActivityDao;
import dao.LinkPostDao;
import dao.PhotoPostDao;
import dao.TextPostDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pierant
 */
@WebServlet(name = "Feed", urlPatterns = {"/feed"})
public class Feed extends HttpServlet {
    
    public static final String ATT_SESSION_USER = "sessionHuman";
    public static final String CONF_DAO_FACTORY = "daofactory";
    private HumanDao humanDao;
    private LikeActivityDao likeActivityDao;
    private DislikeActivityDao dislikeActivityDao;
    private TextPostDao textPostDao;
    private LinkPostDao linkPostDao;
    private PhotoPostDao photoPostDao;
    private FriendshipActivityDao friendshipDao;
    
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
        this.likeActivityDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getLikeActivityDao();
        this.dislikeActivityDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getDislikeActivityDao();
        this.textPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getTextPostDao();
        this.linkPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getLinkPostDao();
        this.photoPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getPhotoPostDao();
        this.friendshipDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getFriendshipActivityDao();
    }

    private static final HashMap<Integer, String> names = new HashMap<>();
    private String getHumanName(int id) {
        if(!names.containsKey(id)) {
            Human h = humanDao.get(id);
            names.put(id, h.getFirstName() + " " + h.getLastName());
        }
        return names.get(id);
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
        response.setContentType("application/json");
        names.clear();
        PrintWriter out = response.getWriter();
        
        try {
            Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
            ArrayList<Human> users = humanDao.getFriends(friendshipDao.getFriends(human.getId()));
            users.add(human);
            
            Map<Integer, LikeActivity> likes = new HashMap<>();
            Map<Integer, DislikeActivity> dislikes = new HashMap<>();
            Map<Integer, TextPost> textPosts = new HashMap<>();
            Map<Integer, LinkPost> linkPosts = new HashMap<>();
            Map<Integer, FriendshipActivity> friends = new HashMap<>();

            ArrayList<String> res = new ArrayList<>();
            
            for(Human curHuman : users){
                likes.putAll(likeActivityDao.getHashByHuman(curHuman.getId()));
                dislikes.putAll(dislikeActivityDao.getHashByHuman(curHuman.getId()));
                textPosts.putAll(textPostDao.getHashByHuman(curHuman.getId()));
                linkPosts.putAll(linkPostDao.getHashByHuman(curHuman.getId()));
                friends.putAll(friendshipDao.getHashByHuman(curHuman.getId()));
            }
            
            out.print("{" +
                            "    \"status\": \"success\"," +
                            "    \"activities\": [");
            
            for(Map.Entry<Integer, LikeActivity> like : likes.entrySet()) {
                int post_author_id = -1;
                TextPost text = textPostDao.get(like.getValue().getId_post());
                if (text != null){
                    post_author_id = text.getId_human();
                } else {
                    LinkPost link = linkPostDao.get(like.getValue().getId_post());
                    post_author_id = link.getId_human();
                }
                res.add("{" +
                            "   \"type\": \"relike.getValue()ion\", " +
                            "   \"relike.getValue()ion\": \"like\", " +
                            "   \"id\": \""+like.getValue().getId()+"\", " +
                            "   \"date\": \""+like.getValue().getDate()+"\", " +
                            "   \"id_post\": \""+like.getValue().getId_post()+"\", " +
                            "   \"authorname\": \""+getHumanName(like.getValue().getId_human())+"\", " +
                            "   \"othername\": \""+getHumanName(post_author_id)+"\" " +
                            "}");
            }
            for(Map.Entry<Integer, DislikeActivity> dislike : dislikes.entrySet()) {
                int post_author_id = -1;
                TextPost text = textPostDao.get(dislike.getValue().getId_post());
                if (text != null){
                    post_author_id = text.getId_human();
                } else {
                    LinkPost link = linkPostDao.get(dislike.getValue().getId_post());
                    post_author_id = link.getId_human();
                }
                res.add("{" +
                            "   \"type\": \"redislike.getValue()ion\", " +
                            "   \"redislike.getValue()ion\": \"dislike\", " +
                            "   \"id\": \""+dislike.getValue().getId()+"\", " +
                            "   \"date\": \""+dislike.getValue().getDate()+"\", " +
                            "   \"id_post\": \""+dislike.getValue().getId_post()+"\", " +
                            "   \"authorname\": \""+getHumanName(dislike.getValue().getId_human())+"\", " +
                            "   \"othername\": \""+getHumanName(post_author_id)+"\" " +
                            "}");
            }
            for(Map.Entry<Integer, TextPost> textPost : textPosts.entrySet()) {
                res.add("{" +
                            "   \"type\": \"text\", " +
                            "   \"id\": \""+textPost.getValue().getId()+"\", " +
                            "   \"date\": \""+textPost.getValue().getDate()+"\", " +
                            "   \"id_human\": \""+textPost.getValue().getId_human()+"\", " +
                            "   \"content\": \""+textPost.getValue().getContent()+"\", " +
                            "   \"authorname\": \""+getHumanName(textPost.getValue().getId_human())+"\" " +
                            "}");
            }
            for(Map.Entry<Integer, LinkPost> linkPost : linkPosts.entrySet()) {
                res.add("{" +
                            "   \"type\": \"link\", " +
                            "   \"id\": \""+linkPost.getValue().getId()+"\", " +
                            "   \"date\": \""+linkPost.getValue().getDate()+"\", " +
                            "   \"id_human\": \""+linkPost.getValue().getId_human()+"\", " +
                            "   \"url\": \""+linkPost.getValue().getUrl()+"\", " +
                            "   \"title\": \""+linkPost.getValue().getTitle()+"\", " +
                            "   \"content\": \""+linkPost.getValue().getContent()+"\", " +
                            "   \"authorname\": \""+getHumanName(linkPost.getValue().getId_human())+"\" " +
                            "}");
            }
            for(Map.Entry<Integer, FriendshipActivity> friend : friends.entrySet()) {
                res.add("{" +
                            "   \"type\": \"friend\", " +
                            "   \"id\": \""+friend.getValue().getId()+"\", " +
                            "   \"date\": \""+friend.getValue().getDate()+"\", " +
                            "   \"id_human\": \""+friend.getValue().getId_human()+"\", " +
                            "   \"id_friend\": \""+friend.getValue().getId_second_human()+"\", " +
                            "   \"authorname\": \""+getHumanName(friend.getValue().getId_human())+"\", " +
                            "   \"othername\": \""+getHumanName(friend.getValue().getId_human())+"\" " +
                            "}");            out.print(String.join(",", res));
            }
            out.print("]}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\"}");   
        }
    }
}
