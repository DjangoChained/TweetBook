package servlets;

import beans.FriendshipActivity;
import beans.Human;
import beans.LinkPost;
import beans.PhotoPost;
import beans.ReactionActivity;
import beans.TextPost;

import dao.DAOException;
import dao.DAOFactory;
import dao.FriendshipActivityDao;
import dao.HumanDao;
import dao.LinkPostDao;
import dao.PhotoPostDao;
import dao.TextPostDao;
import dao.ReactionActivityDao;

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

import com.google.gson.Gson;

@WebServlet(name = "Wall", urlPatterns = {"/wall"})
public class Wall extends HttpServlet {
    
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
            ArrayList<ReactionActivity> reactions = reactionDao.getByHuman(human.getId());
            ArrayList<TextPost> textPosts = textPostDao.getByHuman(human.getId());
            ArrayList<LinkPost> linkPosts = linkPostDao.getByHuman(human.getId());
            ArrayList<FriendshipActivity> friends = friendshipDao.getByHuman(human.getId());
            ArrayList<String> res = new ArrayList<>();
            
            out.print("{" +
                            "    \"status\": \"success\"," +
                            "    \"activities\": [");
            for(ReactionActivity act : reactions){
                Human author = humanDao.get(act.getId_human());
                int post_author_id = -1;
                // Ce qui suit permet de récupérer l'auteur de la publication pour laquelle il y a une réaction
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
                            "   \"reaction\": \""+act.getReaction().toString()+"\", " +
                            "   \"id\": \""+act.getId()+"\", " +
                            "   \"date\": \""+act.getDate()+"\", " +
                            "   \"id_post\": \""+act.getId_post()+"\", " +
                            "   \"authorname\": \""+author.getFirstName()+" "+author.getLastName()+"\", " +
                            "   \"othername\": \""+post_author.getFirstName()+" "+post_author.getLastName()+"\" " +
                            "}");
            }
            for(TextPost post : textPosts){
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
            for(LinkPost post : linkPosts) {
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
            for(FriendshipActivity act : friends){
                Human author = humanDao.get(act.getId_human());
                Human friend = humanDao.get(act.getId_second_human());
                res.add("{" +
                            "   \"type\": \"friend\", " +
                            "   \"id\": \""+act.getId()+"\", " +
                            "   \"date\": \""+act.getDate()+"\", " +
                            "   \"id_human\": \""+act.getId_human()+"\", " +
                            "   \"id_friend\": \""+act.getId_second_human()+"\", " +
                            "   \"authorname\": \""+author.getFirstName()+" "+author.getLastName()+"\", " +
                            "   \"othername\": \""+friend.getFirstName()+" "+friend.getLastName()+"\" " +
                            "}");
            }
            out.print(String.join(",", res));
            out.print("]}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\",\"message\":\"Un problème est survenu lors de la récupération du wall.\"}"); 
            log(e.getMessage());
        }
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
        
        if (data.getProperty("type").equals("text")){
            try {
                TextPost post = new TextPost();
                post.setDate(LocalDateTime.now());
                post.setId_human(human.getId());
                post.setContent(data.getProperty("content"));
                textPostDao.create(post);

                out.println("{\"status\": \"success\",\n\"id\": \""+post.getId()+"\")}");
            } catch (DAOException e){
                out.println("{\"status\": \"error\",\n\"message\": \"Erreur lors de la création du post\"}");
                log(e.getMessage());
            }
        } else if (data.getProperty("type").equals("link")){
            try {
                LinkPost post = new LinkPost();
                post.setDate(LocalDateTime.now());
                post.setId_human(human.getId());
                post.setContent(data.getProperty("content"));
                post.setTitle(data.getProperty("title"));
                post.setUrl(data.getProperty("url"));
                linkPostDao.create(post);

                out.println("{\"status\": \"success\",\n\"id\": \""+post.getId()+"\")}");
            } catch (DAOException e){
                out.println("{\"status\": \"error\",\n\"message\": \"Erreur lors de la création du post\"}");
                log(e.getMessage());
            }
        } else if (data.getProperty("type").equals("photo")){
            try {
                PhotoPost post = new PhotoPost();
                post.setDate(LocalDateTime.now());
                post.setId_human(human.getId());
                post.setContent(data.getProperty("content"));
                post.setPhotoPath(data.getProperty("photopath"));
                photoPostDao.create(post);

                out.println("{\"status\": \"success\",\n\"id\": \""+post.getId()+"\")}");
            } catch (DAOException e){
                out.println("{\"status\": \"error\",\n\"message\": \"Erreur lors de la création du post\"}");
                log(e.getMessage());
            }
        } else {
            out.println("{\"status\": \"error\",\n\"message\": \"Un type de publication invalide a été spécifié.\"}");
        }
    }

}
