/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.Human;
import com.google.gson.Gson;
import dao.DAOFactory;
import dao.HumanDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pierant
 */
@WebServlet(name = "getHuman", urlPatterns = {"/user"})
public class GetHuman extends HttpServlet {

    public static final String CONF_DAO_FACTORY = "daofactory";
    
    private HumanDao humanDao;
    
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
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

        try (PrintWriter out = response.getWriter()) {
            int humanId = -1;
            
            try {
               humanId = Integer.parseInt(request.getParameter("id"));
            } catch(NumberFormatException e){
                out.println("{\n" +
"    \"status\": \"error\",\n" +
"    \"message\": \"Veuillez entrer un entier valide\"\n" +
"}");
            }
            
            if (humanId != -1){
                Human human = humanDao.get(humanId);
                if(human != null){
                    out.println("{\n" +
                                "    \"status\": \"success\",\n" +
                                "    \"user\": {\n" +
                            "        \"id\": \""+human.getId()+"\",\n" +
                            "        \"firstName\": \""+human.getFirstName()+"\",\n" +
                            "        \"lastName\": \""+human.getLastName()+"\"\n" +
                            "    }\n" +
                            "}");
                } else {
                out.println("{\n" +
"    \"status\": \"error\",\n" +
"    \"message\": \"Il n'existe aucun utilisateur avec cet identifiant\"\n" +
"}");
                }
            } 
        }
    }
}
