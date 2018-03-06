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
        
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
    }
    
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
                        "        \"username\": \""+human.getUsername()+"\",\n" +
                        //"        \"visibility\": \""+human.getVisibility().toString()+"\"\n" +
                        "        \"visibility\": \"author\"\n" +
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
        human.setVisibility(beans.ActivityVisibility.valueOf(data.getProperty("visibility")));
        
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
