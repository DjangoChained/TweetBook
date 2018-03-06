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
        
        PrintWriter out = response.getWriter();
        
        try {
            Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
            ArrayList<Human> users = humanDao.getFriends(friendshipDao.getFriends(human.getId()));
            users.add(human);
            
            ArrayList<ArrayList<LikeActivity>> likes = new ArrayList<>();
            ArrayList<ArrayList<DislikeActivity>> dislikes = new ArrayList<>();
            ArrayList<ArrayList<TextPost>> textPosts = new ArrayList<>();
            ArrayList<ArrayList<LinkPost>> linkPosts = new ArrayList<>();
            ArrayList<ArrayList<FriendshipActivity>> friends = new ArrayList<>();
            ArrayList<String> res = new ArrayList<>();
            
            for(Human curHuman : users){
                likes.add(likeActivityDao.getByHuman(curHuman.getId()));
                dislikes.add(dislikeActivityDao.getByHuman(curHuman.getId()));
                textPosts.add(textPostDao.getByHuman(curHuman.getId()));
                linkPosts.add(linkPostDao.getByHuman(curHuman.getId()));
                friends.add(friendshipDao.getByHuman(curHuman.getId()));
            }
            
            out.print("{" +
                            "    \"status\": \"success\"," +
                            "    \"activities\": [");
            for(ArrayList<LikeActivity> like : likes){
                for(LikeActivity act : like){
                    Human author = humanDao.get(act.getId_human());
                    int post_author_id = -1;
                    TextPost text = textPostDao.get(act.getId_post());
                    if (text != null){
                        post_author_id = text.getId_human();
                    } else {
                        LinkPost link = linkPostDao.get(act.getId_post());
                        post_author_id = link.getId_human();
                    }
                    Human post_author = humanDao.get(post_author_id);
                    res.add("{" +
                                "   \"type\": \"reaction\", " +
                                "   \"reaction\": \"like\", " +
                                "   \"id\": \""+act.getId()+"\", " +
                                "   \"date\": \""+act.getDate()+"\", " +
                                "   \"id_post\": \""+act.getId_post()+"\", " +
                                "   \"authorname\": \""+author.getFirstName()+" "+author.getLastName()+"\", " +
                                "   \"othername\": \""+post_author.getFirstName()+" "+post_author.getLastName()+"\" " +
                                "}");
                }
            }
            for(ArrayList<DislikeActivity> dislike : dislikes){
                for(DislikeActivity act : dislike){
                    Human author = humanDao.get(act.getId_human());
                    int post_author_id = -1;
                    TextPost text = textPostDao.get(act.getId_post());
                    if (text != null){
                        post_author_id = text.getId_human();
                    } else {
                        LinkPost link = linkPostDao.get(act.getId_post());
                        post_author_id = link.getId_human();
                    }
                    Human post_author = humanDao.get(post_author_id);
                    res.add("{" +
                                "   \"type\": \"reaction\", " +
                                "   \"reaction\": \"dislike\", " +
                                "   \"id\": \""+act.getId()+"\", " +
                                "   \"date\": \""+act.getDate()+"\", " +
                                "   \"id_post\": \""+act.getId_post()+"\", " +
                                "   \"authorname\": \""+author.getFirstName()+" "+author.getLastName()+"\", " +
                                "   \"othername\": \""+post_author.getFirstName()+" "+post_author.getLastName()+"\" " +
                                "}");
                }
            }
            for(ArrayList<TextPost> textPost : textPosts){
                for(TextPost post : textPost){
                    Human author = humanDao.get(post.getId_human());
                    res.add("{" +
                                "   \"type\": \"text\", " +
                                "   \"id\": \""+post.getId()+"\", " +
                                "   \"date\": \""+post.getDate()+"\", " +
                                "   \"id_human\": \""+post.getId_human()+"\", " +
                                "   \"content\": \""+post.getContent()+"\", " +
                                "   \"authorname\": \""+author.getFirstName()+" "+author.getLastName()+"\" " +
                                "}");
                }
            }
            for(ArrayList<LinkPost> linkPost : linkPosts){
                for(LinkPost post : linkPost) {
                    Human author = humanDao.get(post.getId_human());
                    res.add("{" +
                                "   \"type\": \"link\", " +
                                "   \"id\": \""+post.getId()+"\", " +
                                "   \"date\": \""+post.getDate()+"\", " +
                                "   \"id_human\": \""+post.getId_human()+"\", " +
                                "   \"url\": \""+post.getUrl()+"\", " +
                                "   \"title\": \""+post.getTitle()+"\", " +
                                "   \"content\": \""+post.getContent()+"\", " +
                                "   \"authorname\": \""+author.getFirstName()+" "+author.getLastName()+"\" " +
                                "}");
                }
            }
            for(ArrayList<FriendshipActivity> friend : friends){
                for(FriendshipActivity act : friend){
                    Human author = humanDao.get(act.getId_human());
                    Human author_friend = humanDao.get(act.getId_second_human());
                    res.add("{" +
                                "   \"type\": \"friend\", " +
                                "   \"id\": \""+act.getId()+"\", " +
                                "   \"date\": \""+act.getDate()+"\", " +
                                "   \"id_human\": \""+act.getId_human()+"\", " +
                                "   \"id_friend\": \""+act.getId_second_human()+"\", " +
                                "   \"authorname\": \""+author.getFirstName()+" "+author.getLastName()+"\", " +
                                "   \"othername\": \""+author_friend.getFirstName()+" "+author_friend.getLastName()+"\" " +
                                "}");
                }
            }
            out.print(String.join(",", res));
            out.print("]}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\"}");   
        }
    }
}
