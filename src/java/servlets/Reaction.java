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
 * Servlet qui permet de poster ou de supprimer une réaction à un post
 */
@WebServlet(name = "Reaction", urlPatterns = {"/reaction"})
public class Reaction extends HttpServlet {

    private ReactionActivityDao reactionDao;

    /**
     * Permet d'initialiser les Dao lors de l'instanciation de la servlet
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        this.reactionDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getReactionActivityDao();
    }

    /**
     * Permet de poster une réaction.
     * Reçois au format JSON l'identifiant du post auquel l'utilisateur souhaite réagir ("id_post")
     * et la reaction à proprement parler ("reaction" -> 'like' ou 'dislike')
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
                beans.Reaction reaction = beans.Reaction.fromString(data.getProperty("reaction"));
                int id_human = human.getId();
                int id_post = Integer.parseInt(data.getProperty("id_post"));

                ReactionActivity reac = reactionDao.get(id_human, id_post);

                if(reac != null && reac.getReaction() != reaction)
                    reactionDao.delete(reac.getId());
                else reac = new ReactionActivity();
                reac.setDate(LocalDateTime.now());
                reac.setId_human(id_human);
                reac.setId_post(id_post);
                reac.setReaction(reaction);
                reactionDao.create(reac);

                out.println("{\"status\": \"success\",\n\"id\": \""+reac.getId()+"\"}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\",\n\"message\": \"Erreur lors de la réaction au post\"}");
            log(e.getMessage());
        }
    }

    /**
     * Permet de supprimer une réaction
     * Reçois au format JSON l'identifiant du post auquel fait référence la réaction ("id_post")
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
            log(e.getMessage());
        }
    }
}
