package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Human;
import com.google.gson.Gson;
import config.BCrypt;
import dao.DAOFactory;
import dao.HumanDao;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import config.ConnectionTools;

/**
 * Servlet qui permet de connecter un utilisateur existant
 */
@WebServlet(name = "Login", urlPatterns = {"/user/login"})
public class Login extends HttpServlet {
    
    /**
     * le Dao permettant de manipuler les utilisateurs
     */
    private HumanDao humanDao;
    /**
     * contiendra des erreurs si les données fournies dans le formulaire de
     * connexion sont incorrectes
     */
    private final Map<String, String> errors = new HashMap<>();
    
    /**
     * Permet d'initialiser les Dao lors de l'instanciation de la servlet
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute("daofactory")).getHumanDao();
    }
    
    /**
     * Permet à un utilisateur de se connecter.
     * Reçois au format JSON l'email de l'utilisateur ("email") et son mot de passe ("password")
     * @param request la requête HTTP
     * @param response la réponse HTTP
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        
        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();
        Properties data = gson.fromJson(reader, Properties.class);

        HttpSession session = request.getSession();
        
        String email = data.getProperty("email").trim();
        String password = data.getProperty("password");
        Human human = new Human();
        
        try {
            ConnectionTools.emailValidation(email, false, null );
            human.setEmail(email);
        } catch ( Exception e ) {
            errors.put("email", e.getMessage());
        }

        try {
            config.ConnectionTools.passwordValidation( password );
        } catch ( Exception e ) {
            errors.put("password", e.getMessage());
        }

        /**
         * Si aucune erreur de validation n'a eu lieu, alors ajout du bean
         * Human à la session, sinon suppression du bean de la session.
         */
        session.setAttribute("sessionHuman", null );
        PrintWriter out = response.getWriter();
        if ( errors.isEmpty() ) {
            Human testHuman = humanDao.get(email);
            if(testHuman != null){
                if(BCrypt.checkpw(password, testHuman.getPassword())){
                    session.setAttribute("sessionHuman", testHuman );
                    out.println("{\"status\": \"success\", \"name\":\""+testHuman.getFirstName()+" "+testHuman.getLastName()+"\"}");
                } else {
                    out.print("{\"status\": \"error\",\"message\": \"Email ou mot de passe incorrect.\"\n}");
                }
            } else {
                out.print("{\"status\": \"error\",\"message\": \"Email ou mot de passe incorrect.\"\n}");
            }
        } else {
            out.print("{\"status\": \"error\",\"message\": \"");
            String message = errors.entrySet().stream().map((entry) -> entry.getValue()).collect(Collectors.joining(" - "));
            out.println(message+"\"}");
            errors.clear();
        }         
    }
}