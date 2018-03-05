/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.Human;
import com.google.gson.Gson;
import dao.DAOException;
import dao.DAOFactory;
import dao.HumanDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
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
@WebServlet(name = "Settings", urlPatterns = {"/user/settings"})
public class Settings extends HttpServlet {
    public static final String ATT_SESSION_USER = "sessionHuman";
    public static final String CONF_DAO_FACTORY = "daofactory";
    private HumanDao humanDao;
    
    @Override
    public void init() throws ServletException {
        /* Récupération d'une instance de notre DAO Utilisateur */
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
        
        Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        
        try (PrintWriter out = response.getWriter()) {
            out.println("{\n" +
                                "    \"status\": \"success\",\n" +
                                "    \"user\": {\n" +
                            "        \"id\": \""+human.getId()+"\",\n" +
                            "        \"firstName\": \""+human.getFirstName()+"\",\n" +
                            "        \"lastName\": \""+human.getLastName()+"\",\n" +
                            "        \"birthdate\": \""+human.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"\",\n" +
                            "        \"email\": \""+human.getEmail()+"\",\n" +
                            "        \"username\": \""+human.getUsername()+"\"\n" +
                            "    }\n" +
                            "}");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        
        Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        human.setFirstName(data.getProperty("firstname"));
        human.setLastName(data.getProperty("lastname"));
        human.setBirthDate(data.getProperty("birthdate"));
        human.setUsername(data.getProperty("username"));
        human.setEmail(data.getProperty("email"));
        human.setVisibility(data.getProperty("visibility"));
        
        try {
            humanDao.update(human);
            try (PrintWriter out = response.getWriter()) {
                out.println("{\"status\": \"success\",\n\"id\": \""+human.getId()+"\"}");
            }
        } catch (DAOException e){
            try (PrintWriter out = response.getWriter()) {
                out.println("{\"status\": \"error\",\n\"message\": \""+e.getMessage().replace("\"", "\\\"").replace("\n", "")+"\"}");
            }
        }
  }
}
