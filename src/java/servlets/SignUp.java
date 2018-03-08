package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Human;
import com.google.gson.Gson;
import config.BCrypt;
import dao.DAOException;
import dao.DAOFactory;
import dao.HumanDao;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import tools.ConnectionTools;

@WebServlet(name = "SignUp", urlPatterns = {"/user/register"})
public class SignUp extends HttpServlet {
    public static final String CONF_DAO_FACTORY = "daofactory";
    public static final String ATT_SESSION_USER = "sessionHuman";
    
    private HumanDao humanDao;
    private final Map<String, String> errors = new HashMap<>();
    
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
    }

    @Override
    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        
        HttpSession session = request.getSession();
        
        PrintWriter out = response.getWriter();
        
        String firstname = data.getProperty("firstname");
        String lastname = data.getProperty("lastname");
        String birthdate = data.getProperty("birthdate");
        String email = data.getProperty("email");
        String password = data.getProperty("password");
        String username = data.getProperty("username");
        Human human = new Human();
        
        try {
            firstnameProcess(firstname, human);
            lastnameProcess(lastname, human);
            birthdateProcess(birthdate, human);
            emailProcess( email, human );
            passwordProcess( password, human );
            usernameProcess( username, human );

            if ( errors.isEmpty() ) {
                humanDao.create(human);
                session.setAttribute( ATT_SESSION_USER, human );
                out.println("{ \"status\": \"success\"}");
            } else {
                out.print("{\"status\": \"error\",\"message\": \"");
                String message = errors.entrySet().stream().map((entry) -> entry.getValue()).collect(Collectors.joining(" - "));
                out.println(message+"\"}");
            }
        } catch (DAOException e) {
            out.print("{\"status\": \"error\",\"message\": \"Échec de l'inscription : une erreur imprévue est survenue, merci de réessayer dans quelques instants.\"");
        }
    }
    
    private void nameValidation( String name, String type ) throws Exception {
        if ( name == null || name.length() < 3 ) {
            throw new Exception( "Le " + type + " doit contenir au moins 3 caractères." );
        }
    }
    
    private void birthdateValidation(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            Date d = sdf.parse(date);
        } catch (ParseException e){
            throw new Exception("La date doit être au format yyyy-MM-dd");
        }
    }
    
    private void firstnameProcess(String firstname, Human human) {
        try {
           nameValidation( firstname, "prénom" );
           human.setFirstName( firstname );
       } catch ( Exception e ) {
           errors.put("firstname", e.getMessage());
       }
    }

    private void lastnameProcess(String lastname, Human human) {
        try {
           nameValidation( lastname, "nom de famille" );
           human.setLastName( lastname );
       } catch ( Exception e ) {
           errors.put("lastname", e.getMessage() );
       }
    }
    
    private void birthdateProcess(String date, Human human) {
        LocalDateTime res = null;
        try {
            birthdateValidation(date);
            final DateTimeFormatter formatter;
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            res = LocalDateTime.from(LocalDate.parse(date, formatter).atStartOfDay());
            human.setBirthDate(res);
        } catch (Exception e) {
            errors.put("birthdate", e.getMessage());
        }
    }
    
    private void emailProcess(String email, Human human) {
        try {
            ConnectionTools.emailValidation(email, true, humanDao);
            human.setEmail(email);
        } catch (Exception e ) {
            errors.put(email, e.getMessage());
        }
    }
   
    private void usernameProcess(String username, Human human) {
        try {
            nameValidation( username, "nom d'utilisateur" );
            human.setUsername(username);
        } catch ( Exception e ) {
            errors.put("username", e.getMessage() );
        }
    }

    private void passwordProcess(String password, Human human) {
        try {
            ConnectionTools.passwordValidation(password);
            human.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        } catch ( Exception e ) {
            errors.put("password", e.getMessage() );
        }
    }
}