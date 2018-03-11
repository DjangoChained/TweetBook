package servlets;

import beans.ReactionActivity;
import beans.Human;
import com.google.gson.Gson;
import dao.DAOException;
import dao.DAOFactory;
import dao.ReactionActivityDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
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
@WebServlet(name = "Reaction", urlPatterns = {"/reaction"})
public class Reaction extends HttpServlet {
    
    /**
     *
     */
    public static final String ATT_SESSION_USER = "sessionHuman";

    /**
     *
     */
    public static final String CONF_DAO_FACTORY = "daofactory";
    private ReactionActivityDao reactionDao;
    
    /**
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        this.reactionDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getReactionActivityDao();
    }

    /**
     *
     * @param request
     * @param response
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
        Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        PrintWriter out = response.getWriter();
        
        try {
                beans.Reaction reaction = beans.Reaction.valueOf(data.getProperty("reaction"));
                int id_human = human.getId();
                int id_post = Integer.parseInt(data.getProperty("id_post"));
                
                ReactionActivity reac = reactionDao.get(id_human, id_post);
                
                if(reac != null && reac.getReaction() != reaction)
                    reactionDao.delete(reac.getId());
                reac.setDate(LocalDateTime.now());
                reac.setId_human(id_human);
                reac.setId_post(id_post);
                reac.setReaction(reaction);
                reactionDao.create(reac);
                
                out.println("{\"status\": \"success\",\n\"id\": \""+reac.getId()+"\"}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\",\n\"message\": \"Erreur lors de la réaction au post\"}");
        }
    }

    /**
     *
     * @param request
     * @param response
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
        Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        PrintWriter out = response.getWriter();
        try {
                int id_human = human.getId();
                int id_post = Integer.parseInt(data.getProperty("id_post"));
                
                ReactionActivity reaction = reactionDao.get(id_human, id_post);
                if (reaction != null) {
                    reactionDao.delete(reaction.getId());
                    out.println("{\"status\": \"success\"}");
                    return;
                }
                out.println("{\"status\": \"error\",\n\"message\": \"Aucune réaction trouvée.\"}");
        } catch (DAOException e) {
            out.println("{\"status\": \"error\",\n\"message\": \"Erreur lors de l'annulation de la réaction\"}");
        }
    }
}
