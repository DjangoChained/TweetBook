/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.Human;
import com.google.gson.Gson;
import config.BCrypt;
import dao.DAOException;
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
@WebServlet(name = "UpdatePassword", urlPatterns = {"/user/password"})
public class UpdatePassword extends HttpServlet {
    
    public static final String CONF_DAO_FACTORY = "daofactory";
    public static final String ATT_SESSION_USER = "sessionHuman";
    private HumanDao humanDao;
    
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        
        Human human = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        
        PrintWriter out = response.getWriter();
        
        try {
            if (data.getProperty("currentPassword") != null && data.getProperty("currentPassword").trim().length() != 0 && data.getProperty("newPassword") != null && data.getProperty("newPassword").trim().length() != 0) {
                Human testHuman = humanDao.get(human.getEmail());

                if(BCrypt.checkpw(data.getProperty("currentPassword"), testHuman.getPassword())){
                    humanDao.updatePassword(human, BCrypt.hashpw(data.getProperty("newPassword"), BCrypt.gensalt()));
                    out.print("{\"status\": \"success\"}");
                } else {
                    out.println("{\"status\": \"error\",\"message\": \"mauvaise combinaison email / mot de passe\"}");
                }
            } else {
                out.println("{\"status\": \"error\"\n\"message\": \"Veuillez renseigner les mots de passe\"}");
            }
        } catch (DAOException e){
            out.println("{\"status\": \"error\",\"message\": \""+e.getMessage()+"\"}");
        }
  }
}
