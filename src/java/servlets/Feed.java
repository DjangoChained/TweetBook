/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.FriendshipActivity;
import beans.Human;
import beans.ReactionActivity;
import beans.LinkPost;
import beans.TextPost;

import dao.DAOException;
import dao.DAOFactory;
import dao.ReactionActivityDao;
import dao.FriendshipActivityDao;
import dao.HumanDao;
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
 *
 */
@WebServlet(name = "Feed", urlPatterns = {"/feed"})
public class Feed extends HttpServlet {

    public static final String ATT_SESSION_USER = "sessionHuman";
    public static final String CONF_DAO_FACTORY = "daofactory";
    private HumanDao humanDao;
    private ReactionActivityDao reactionDao;
    private TextPostDao textPostDao;
    private LinkPostDao linkPostDao;
    private PhotoPostDao photoPostDao;
    private FriendshipActivityDao friendshipDao;

    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
        this.reactionDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getReactionActivityDao();
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

            Map<Integer, ReactionActivity> reactions = new HashMap<>();
            Map<Integer, TextPost> textPosts = new HashMap<>();
            Map<Integer, LinkPost> linkPosts = new HashMap<>();
            Map<Integer, FriendshipActivity> friends = new HashMap<>();

            ArrayList<String> res = new ArrayList<>();

            for(Human curHuman : users){
                reactions.putAll(reactionDao.getHashByHuman(curHuman.getId()));
                textPosts.putAll(textPostDao.getHashByHuman(curHuman.getId()));
                linkPosts.putAll(linkPostDao.getHashByHuman(curHuman.getId()));
                friends.putAll(friendshipDao.getHashByHuman(curHuman.getId()));
            }

            out.print("{" +
                            "    \"status\": \"success\"," +
                            "    \"activities\": [");

            for(Map.Entry<Integer, ReactionActivity> reaction : reactions.entrySet()) {
                Human author = humanDao.get(reaction.getValue().getId_human());
                int post_author_id = -1;
                // Ce qui suit permet de récupérer l'auteur de la publication pour laquelle il y a une réaction
                TextPost text = textPostDao.get(reaction.getValue().getId_post());
                if (text != null){
                    post_author_id = text.getId_human();
                } else {
                    LinkPost link = linkPostDao.get(reaction.getValue().getId_post());
                    post_author_id = link.getId_human();
                }
                Human post_author = humanDao.get(post_author_id);
                res.add("{" +
                            "   \"type\": \"reaction\", " +
                            "   \"reaction:\": \""+reaction.getValue().getReaction().toString()+"\", " +
                            "   \"id\": \""+reaction.getValue().getId()+"\", " +
                            "   \"date\": \""+reaction.getValue().getDate()+"\", " +
                            "   \"id_post\": \""+reaction.getValue().getId_post()+"\", " +
                            "   \"authorname\": \""+author.getFirstName()+" "+author.getLastName()+"\", " +
                            "   \"othername\": \""+post_author.getFirstName()+" "+post_author.getLastName()+"\" " +
                            "}");
            }
            for(Map.Entry<Integer, TextPost> textPost : textPosts.entrySet()) {
                res.add("{\"type\": \"text\", " +
                            "\"id\": \""+textPost.getValue().getId()+"\", " +
                            "\"date\": \""+textPost.getValue().getDate()+"\", " +
                            "\"id_human\": \""+textPost.getValue().getId_human()+"\", " +
                            "\"content\": \""+textPost.getValue().getContent()+"\", " +
                            "\"authorname\": \""+getHumanName(textPost.getValue().getId_human())+"\"}");
            }
            for(Map.Entry<Integer, LinkPost> linkPost : linkPosts.entrySet()) {
                res.add("{\"type\": \"link\", " +
                            "\"id\": \""+linkPost.getValue().getId()+"\", " +
                            "\"date\": \""+linkPost.getValue().getDate()+"\", " +
                            "\"id_human\": \""+linkPost.getValue().getId_human()+"\", " +
                            "\"url\": \""+linkPost.getValue().getUrl()+"\", " +
                            "\"title\": \""+linkPost.getValue().getTitle()+"\", " +
                            "\"content\": \""+linkPost.getValue().getContent()+"\", " +
                            "\"authorname\": \""+getHumanName(linkPost.getValue().getId_human())+"\"}");
            }
            for(Map.Entry<Integer, FriendshipActivity> friend : friends.entrySet()) {
                res.add("{\"type\": \"friend\", " +
                            "\"id\": \""+friend.getValue().getId()+"\", " +
                            "\"date\": \""+friend.getValue().getDate()+"\", " +
                            "\"id_human\": \""+friend.getValue().getId_human()+"\", " +
                            "\"id_friend\": \""+friend.getValue().getId_second_human()+"\", " +
                            "\"authorname\": \""+getHumanName(friend.getValue().getId_human())+"\", " +
                            "\"othername\": \""+getHumanName(friend.getValue().getId_second_human())+"\"}");
            }

            out.print("{\"status\": \"success\", \"activities\": [");
            out.print(String.join(",", res));
            out.print("]}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\",\"message\":\"Un problème est survenu lors de la récupération du feed.\"}");
            log(e.getMessage());
        }
    }
}
