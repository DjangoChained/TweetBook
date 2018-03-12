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
 *
 *
 */
@WebServlet(name = "getPost", urlPatterns = {"/post"})
public class GetPost extends HttpServlet {

    public static final String CONF_DAO_FACTORY = "daofactory";

    private HumanDao humanDao;
    private TextPostDao textPostDao;
    private LinkPostDao linkPostDao;
    private PhotoPostDao photoPostDao;

    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
        this.textPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getTextPostDao();
        this.linkPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getLinkPostDao();
        this.photoPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getPhotoPostDao();
    }

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
                "    \"url\": \"/"+photopost.getPhotoPath()+"\"\n"
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
