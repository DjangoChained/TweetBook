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
 * Servlet qui permet à l'utilisateur de modifier son mot de passe
 */
@WebServlet(name = "UpdatePassword", urlPatterns = {"/user/password"})
public class UpdatePassword extends HttpServlet {
    
    /**
     * Le Dao permettant de manipuler les utilisateurs
     */
    private HumanDao humanDao;
    
    /**
     * Permet d'initialiser les Dao lors de l'instanciation de la servlet
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getHumanDao();
    }
    
    /**
     * Permet à un utilisateur de modifier son mot de passe
     * Reçois au format JSON le mot de passe actuel de l'utilisateur ("currentPassword") et le nouveau ("newPassword")
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        
        Human human = (Human)request.getSession(false).getAttribute("sessionHuman");
        
        PrintWriter out = response.getWriter();
        
        try {
            if (data.getProperty("currentPassword") != null && data.getProperty("currentPassword").trim().length() != 0 && data.getProperty("newPassword") != null && data.getProperty("newPassword").trim().length() != 0) {
                Human testHuman = humanDao.get(human.getEmail());

                if(BCrypt.checkpw(data.getProperty("currentPassword"), testHuman.getPassword())){
                    humanDao.updatePassword(human, BCrypt.hashpw(data.getProperty("newPassword"), BCrypt.gensalt()));
                    out.print("{\"status\": \"success\"}");
                } else {
                    out.println("{\"status\": \"error\",\"message\": \"Mauvaise combinaison email / mot de passe\"}");
                }
            } else {
                out.println("{\"status\": \"error\"\n\"message\": \"Veuillez renseigner les mots de passe\"}");
            }
        } catch (DAOException e){
            out.println("{\"status\": \"error\",\"message\": \"Erreur lors de la modification du mot de passe\"}");
            log(e.getMessage());
        }
  }
}
