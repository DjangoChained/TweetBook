/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.LinkPost;
import beans.PhotoPost;
import beans.TextPost;
import dao.DAOFactory;
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
    
    private TextPostDao textPostDao;
    private LinkPostDao linkPostDao;
    private PhotoPostDao photoPostDao;

    @Override
    public void init() throws ServletException {
        this.textPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getTextPostDao();
        this.linkPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getLinkPostDao();
        this.photoPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getPhotoPostDao();
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            int postId = -1;
            
            try {
               postId = Integer.parseInt(request.getParameter("id"));
            } catch(NumberFormatException e){
                out.println("{\n" +
"    \"status\": \"error\",\n" +
"    \"message\": \"identifiant de publication invalide\"\n" +
"}");
            }
            if (postId != -1){
                TextPost textpost = textPostDao.get(postId);
                PhotoPost photopost = photoPostDao.get(postId);
                LinkPost linkpost = linkPostDao.get(postId);
                    if (textpost != null){
                        out.println(textpost.getJson()+
                        "    \"type\": \"text\"\n" +
                        "    }\n" +
                        "}");
                    } else if (photopost != null) {
                        out.println(photopost.getJson()+
                        "    \"type\": \"photo\",\n" +
                        "    \"url\": \"/"+photopost.getPhotoPath()+"\"\n" +
                        "    }\n" +
                        "}");
                    } else if (linkpost != null) {
                        out.println(linkpost.getJson()+
                        "    \"type\": \"link\",\n" +
                        "    \"url\": \"/"+linkpost.getUrl()+"\",\n" +
                        "    \"title\": \"/"+linkpost.getTitle()+"\"\n" +
                        "    }\n" +
                        "}");
                    } else {
                        out.println("{\n" +
"    \"status\": \"error\",\n" +
"    \"message\": \"Il n'existe aucun post avec cet id\"\n" +
"}");
                    }
            }
        }
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
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
