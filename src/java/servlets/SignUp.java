package servlets;

import beans.ActivityVisibility;
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
import config.ConnectionTools;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Servlet qui permet à un utilisateur de s'inscrire
 */
@WebServlet(name = "SignUp", urlPatterns = {"/user/register"})
public class SignUp extends HttpServlet {
    
    /**
     * le Dao qui permet de manipuler les utilisateurs 
     */
    private HumanDao humanDao;
    /**
     * contiendra des erreurs si les données fournies dans le formulaire
     * d'inscription sont incorrectes
     */
    private final Map<String, String> errors = new HashMap<>();
    
    /**
     * Permet d'initialiser les Dao lors de l'instanciation de la servlet
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getHumanDao();
    }

    /**
     * Permet à un utilisateur de s'inscrire.
     * Reçois au format JSON le nom ("lastname"), prénom ("firstname"), date de naissance ("birthdate"),
     * nom d'utilisateur ("username"), l'adresse mail ("email"). La visibilité des publications sera
     * limitée aux amis par défaut
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
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
        human.setVisibility(ActivityVisibility.friends);
        
        try {
            firstnameProcess(firstname, human);
            lastnameProcess(lastname, human);
            birthdateProcess(birthdate, human);
            emailProcess( email, human );
            passwordProcess( password, human );
            usernameProcess( username, human );

            if ( errors.isEmpty() ) {
                humanDao.create(human);
                session.setAttribute( "sessionHuman", human );
                out.println("{ \"status\": \"success\"}");
            } else {
                out.print("{\"status\": \"error\",\"message\": \"");
                String message = errors.entrySet().stream().map((entry) -> entry.getValue()).collect(Collectors.joining(" - "));
                out.println(message+"\"}");
                errors.clear();
            }
        } catch (DAOException e) {
            out.print("{\"status\": \"error\",\"message\": \"Échec de l'inscription\"");
            log(e.getMessage());
        }
    }
    
    /**
     * Méthode utilitaire pour s'assurer qu'un nom fourni est non nul et fait
     * plus de 3 caractères de long
     * @param name le nom fourni par l'utilisateur
     * @param type le nom du champ qui varie en fonction des cas
     * @throws Exception lorsque le nom fournit ne répond pas aux critères
     */
    private void nameValidation( String name, String type ) throws Exception {
        if ( name == null || name.length() < 3 ) {
            throw new Exception( "Le " + type + " doit contenir au moins 3 caractères." );
        }
    }
    
    /**
     * Méthode utilitaire pour s'assurer qu'une date fournie est non nulle
     * et est au bon format
     * @param date la date fournie par l'utilisateur
     * @throws Exception lorsque la date fournie ne répond pas au critères
     */
    private void birthdateValidation(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        Date d;
        try {
            d = sdf.parse(date);
        } catch (ParseException e){
            throw new Exception("La date doit être au format yyyy-MM-dd");
        }
        if(d.after(new Date())) throw new Exception("Vous ne pouvez pas naître dans le futur.");
        if(TimeUnit.DAYS.convert(Math.abs(new Date().getTime() - d.getTime()), TimeUnit.MILLISECONDS) < 365 * 13)
            throw new Exception("Vous devez être âgé de plus 13 ans pour vous inscrire à TweetBook.");
    }
    
    /**
     * Méthode utilitaire s'assurant que le prénom fourni répond aux critères
     * et qui envoie une erreur dans le cas contraire
     * @param firstname le prénom fourni par l'utilisateur
     * @param human l'utilisateur qui souhaite s'inscrire
     */
    private void firstnameProcess(String firstname, Human human) {
        try {
           nameValidation( firstname, "prénom" );
           human.setFirstName( firstname );
       } catch ( Exception e ) {
           errors.put("firstname", e.getMessage());
       }
    }

    /**
     * Méthode utilitaire s'assurant que le nom fourni répond aux critères
     * et qui envoie une erreur dans le cas contraire
     * @param lastname le nom fourni par l'utilisateur
     * @param human l'utilisateur qui souhaite s'inscrire
     */
    private void lastnameProcess(String lastname, Human human) {
        try {
           nameValidation( lastname, "nom de famille" );
           human.setLastName( lastname );
       } catch ( Exception e ) {
           errors.put("lastname", e.getMessage() );
       }
    }
    
    /**
     * Méthode utilitaire s'assurant que la date fourni répond aux critères
     * et qui envoie une erreur dans le cas contraire
     * @param date la date fournie par l'utilisateur
     * @param human l'utilisateur qui souhaite s'inscrire
     */
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
    
    /**
     * Méthode utilitaire s'assurant que l'adresse mail fournie répond aux
     * critères et qui envoie une erreur dans le cas contraire
     * @param email adresse mail fournie par l'utilisateur
     * @param human l'utilisateur qui souhaite s'inscrire
     */
    private void emailProcess(String email, Human human) {
        try {
            ConnectionTools.emailValidation(email, true, humanDao);
            human.setEmail(email);
        } catch (Exception e ) {
            errors.put(email, e.getMessage());
        }
    }
   
    /**
     * Méthode utilitaire s'assurant que le nom d'utilisateur fourni répond 
     * aux critères et qui envoie une erreur dans le cas contraire
     * @param username le nom d'utilisateur fourni par l'utilisateur
     * @param human l'utilisateur qui souhaite s'inscrire
     */
    private void usernameProcess(String username, Human human) {
        try {
            nameValidation( username, "nom d'utilisateur" );
            human.setUsername(username);
        } catch ( Exception e ) {
            errors.put("username", e.getMessage() );
        }
    }

    /**
     * Méthode utilitaire s'assurant que le mot de passe fourni répond
     * aux critères et qui envoie une erreur dans le cas contraire
     * @param password le mot de passe fourni par l'utilisateur
     * @param human l'utilisateur qui souhaite s'inscrire
     */
    private void passwordProcess(String password, Human human) {
        try {
            ConnectionTools.passwordValidation(password);
            human.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        } catch ( Exception e ) {
            errors.put("password", e.getMessage() );
        }
    }
}