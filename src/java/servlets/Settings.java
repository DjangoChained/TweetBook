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
 *
 */
@WebServlet(name = "Settings", urlPatterns = {"/user/settings"})
public class Settings extends HttpServlet {

    /**
     * Le dao qui permet de manipuler les utilisateurs
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
     * Permet de récupérer les information personnelles d'un utilisateur au format JSON.
     * @param request la requête HTTP
     * @param response la réponse HTTP
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        
        Human human = (Human)request.getSession(false).getAttribute("sessionHuman");
        
        PrintWriter out = response.getWriter();
        if (human != null){
            out.println("{\n" +
                        "    \"status\": \"success\",\n" +
                        "    \"user\": {\n" +
                        "        \"id\": \""+human.getId()+"\",\n" +
                        "        \"firstName\": \""+human.getFirstName()+"\",\n" +
                        "        \"lastName\": \""+human.getLastName()+"\",\n" +
                        "        \"birthdate\": \""+human.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"\",\n" +
                        "        \"email\": \""+human.getEmail()+"\",\n" +
                        "        \"username\": \""+human.getUsername()+"\",\n" +
                        "        \"visibility\": \""+human.getVisibility().toString()+"\"\n" +
                        "    }\n" +
                        "}");
        } else {
            out.println("{\"status\": \"error\",\n\"message\": \"Erreur lors de la récupération des informations personnelles.\"}");
        }
    }
    
    /**
     * Permet de mettre à jour les informations personnelles de l'utilisateur connecté
     * Reçois au format JSON le nom ("lastname"), prénom ("firstname"), date de naissance ("birthdate"),
     * nom d'utilisateur ("username"), l'adresse mail ("email") et la visibilité des publications ("visibility")
     * @param request la requête HTTP
     * @param response la réponse HTTP
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
        
        PrintWriter out = response.getWriter();
        
        Human human = (Human)request.getSession(false).getAttribute("sessionHuman");
        human.setFirstName(data.getProperty("firstname"));
        human.setLastName(data.getProperty("lastname"));
        human.setBirthDate(data.getProperty("birthdate"));
        human.setUsername(data.getProperty("username"));
        human.setEmail(data.getProperty("email"));
        human.setVisibility(beans.ActivityVisibility.valueOf(data.getProperty("visibility")));
        
        try {
            humanDao.update(human);
            out.println("{\"status\": \"success\",\n\"id\": \""+human.getId()+"\"}");
        } catch (DAOException e){
            out.println("{\"status\": \"error\",\n\"message\": \"Erreur lors de la modifications des informations personnelles.\"}");
            log(e.getMessage().replace("\"", "\\\"").replace("\n", ""));
        }
  }
}
