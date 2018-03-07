/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author pierant
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
        PrintWriter out = response.getWriter();
        int postId;
        try {
            postId = Integer.parseInt(request.getParameter("id"));
        } catch(NumberFormatException e){
            out.println("{\"status\": \"error\", \"message\": \"Identifiant de publication invalide\"}");
            return;
        }
        TextPost textpost = textPostDao.get(postId);
        PhotoPost photopost = photoPostDao.get(postId);
        LinkPost linkpost = linkPostDao.get(postId);
        if (textpost != null){
            Human human = humanDao.get(textpost.getId_human());
            out.println(textpost.getJson()+
            "    \"type\": \"text\"," +
            "    \"authorname\": \"" + human.getFirstName() + " " + human.getLastName() + "\"}}");
        } else if (photopost != null) {
            Human human = humanDao.get(photopost.getId_human());
            out.println(photopost.getJson()+
            "    \"type\": \"photo\"," +
            "    \"url\": \""+photopost.getPhotoPath()+"\"," +
            "    \"authorname\": \"" + human.getFirstName() + " " + human.getLastName() + "\"}}");
        } else if (linkpost != null) {
            Human human = humanDao.get(linkpost.getId_human());
            out.println(linkpost.getJson()+
            "    \"type\": \"link\"," +
            "    \"url\": \""+linkpost.getUrl()+"\"," +
            "    \"title\": \""+linkpost.getTitle()+"\"," +
            "    \"authorname\": \"" + human.getFirstName() + " " + human.getLastName() + "\"}}");
        } else {
            out.println("{\"status\": \"error\", \"message\": \"Cette publication n'existe pas.\"}");
        }
    }
}
