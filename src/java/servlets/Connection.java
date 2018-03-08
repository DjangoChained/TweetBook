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
import tools.ConnectionTools;

@WebServlet(name = "Connection", urlPatterns = {"/user/login"})
public class Connection extends HttpServlet {
    public static final String ATT_SESSION_USER = "sessionHuman";
    public static final String CONF_DAO_FACTORY = "daofactory";
    
    private HumanDao humanDao;
    private final Map<String, String> errors = new HashMap<>();
    
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
    }
    
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
            tools.ConnectionTools.passwordValidation( password );
        } catch ( Exception e ) {
            errors.put("password", e.getMessage());
        }

        /**
         * Si aucune erreur de validation n'a eu lieu, alors ajout du bean
         * Human à la session, sinon suppression du bean de la session.
         */
        session.setAttribute( ATT_SESSION_USER, null );
        PrintWriter out = response.getWriter();
        if ( errors.isEmpty() ) {
            Human testHuman = humanDao.get(email);
            if(testHuman != null){
                if(BCrypt.checkpw(password, testHuman.getPassword())){
                    session.setAttribute( ATT_SESSION_USER, testHuman );
                    out.println("{\"status\": \"success\"}");
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
        }         
    }
}