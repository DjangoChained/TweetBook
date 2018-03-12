package servlets;

import beans.Human;
import beans.LinkPost;
import beans.PhotoPost;
import beans.TextPost;
import dao.DAOFactory;
import dao.HumanDao;
import dao.TextPostDao;
import dao.LinkPostDao;
import dao.PhotoPostDao;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet qui permet de récupérer un post
 */
@WebServlet(name = "getPost", urlPatterns = {"/post"})
public class GetPost extends HttpServlet {

    /**
     * Dao permettant de manipuler les utilisateurs
     */
    private HumanDao humanDao;
    /**
     * Dao permettant de manipuler les publications contenant du texte
     */
    private TextPostDao textPostDao;
    /**
     * Dao permettant de manipuler les publications contenant un lien
     */
    private LinkPostDao linkPostDao;
    /**
     * Dao permettant de manipuler les publications contenant une photo
     */
    private PhotoPostDao photoPostDao;

    /**
     * Permet d'initialiser les Dao lors de l'instanciation de la servlet
     * @throws ServletException 
     */
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getHumanDao();
        this.textPostDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getTextPostDao();
        this.linkPostDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getLinkPostDao();
        this.photoPostDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getPhotoPostDao();
    }

    /**
     * Permet de récupérer un post par son identifiant
     * Reçois au format JSON l'identifiant du post que l'on souhaite récupérer ("id")
     * @param request la requête HTTP
     * @param response la réponse HTTP
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        int postId = -1;
        PrintWriter out = response.getWriter();

        try {
           postId = Integer.parseInt(request.getParameter("id"));
        } catch(NumberFormatException e){
            out.println("{\"status\": \"error\",\"message\": \"identifiant de publication invalide\"}");
            log(e.getMessage());
        }
        if (postId != -1){
            TextPost textpost = textPostDao.get(postId);
            PhotoPost photopost = photoPostDao.get(postId);
            LinkPost linkpost = linkPostDao.get(postId);
            if (textpost != null){
                Human human = humanDao.get(textpost.getId_human());
                out.println(textpost.getJson()+
                "    \"type\": \"text\"\n" +
                "    \"authorname\": \"" + human.getFirstName() + " " + human.getLastName() + "\"}}");
            } else if (photopost != null) {
                Human human = humanDao.get(photopost.getId_human());
                out.println(photopost.getJson()+
                "    \"type\": \"photo\",\n" +
                "    \"url\": \"/"+photopost.getPhotoPath()+"\"\n" +
                "    \"authorname\": \"" + human.getFirstName() + " " + human.getLastName() + "\"}}");
            } else if (linkpost != null) {
                Human human = humanDao.get(linkpost.getId_human());
                out.println(linkpost.getJson()+
                "    \"type\": \"link\",\n" +
                "    \"url\": \"/"+linkpost.getUrl()+"\",\n" +
                "    \"title\": \"/"+linkpost.getTitle()+"\"\n" +
                "    \"authorname\": \"" + human.getFirstName() + " " + human.getLastName() + "\"}}");
            } else {
                out.println("{\"status\": \"error\",\"message\": \"Il n'existe aucun post avec cet id\"}");
            }
        }
    }
}
