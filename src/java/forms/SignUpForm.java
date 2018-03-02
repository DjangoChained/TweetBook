/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import beans.Human;
import config.BCrypt;
import dao.DAOException;
import dao.HumanDao;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class SignUpForm {

    private static final String FIRSTNAME_FIELD = "firstname";
    private static final String LASTNAME_FIELD = "lastname";
    private static final String BIRTHDATE_FIELD = "birthdate";
    private static final String EMAIL_FIELD  = "email";
    private static final String PASS_FIELD   = "password";
    private static final String CONF_FIELD   = "confirmation";
    private static final String USERNAME_FIELD    = "username";
    
    private String result;
    private final Map<String, String> errors = new HashMap<>();
    private HumanDao humanDao;
    public static final String ATT_SESSION_USER = "sessionHuman";
    
    public SignUpForm(HumanDao humanDao) {
        this.humanDao = humanDao;
    }
    
    public String getResult() {
        return result;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
    
    
    public Human SignUpHuman( Properties data, HttpServletRequest request ) {
        HttpSession session = request.getSession();
        
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
        } else {
            session.setAttribute("errors", errors);
        }
    } catch ( DAOException e ) {
        result = "Échec de l'inscription : une erreur imprévue est survenue, merci de réessayer dans quelques instants.";
        e.printStackTrace();
    }

        return human;
    }

    /* Validation de l'adresse email */
    private void emailValidation( String email ) throws FormValidationException {
    if ( email != null ) {
        if ( !email.matches( "([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)" ) ) {
            throw new FormValidationException( "Merci de saisir une adresse mail valide." );
        } else if ( humanDao.get(email) != null ) {
            throw new FormValidationException( "Cette adresse email est déjà utilisée, merci d'en choisir une autre." );
        }
    } else {
        throw new FormValidationException( "Merci de saisir une adresse mail." );
    }
}

    private void passwordValidation( String password) throws Exception {
        if ( password != null ) {
            if (password.length() < 6)
                throw new Exception( "Le mot de passe doit faire au moins 6 caractères." );
        } else {
            throw new Exception( "Veuillez saisir votre mot de passe." );
        }
    }

    private void nameValidation( String name ) throws Exception {
        if ( name == null || name.length() < 3 ) {
            throw new Exception( "Le nom d'utilisateur doit contenir au moins 3 caractères." );
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
           nameValidation( firstname );
       } catch ( Exception e ) {
           setError(FIRSTNAME_FIELD, e.getMessage());
       }
       human.setFirstName( firstname );
    }

    private void lastnameProcess(String lastname, Human human) {
        try {
           nameValidation( lastname );
       } catch ( Exception e ) {
           setError( LASTNAME_FIELD, e.getMessage() );
       }
       human.setLastName( lastname );
    }
    
    private void birthdateProcess(String date, Human human) {
        LocalDateTime res = null;
        try {
            birthdateValidation(date);
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            final DateTimeFormatter formatter;
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            LocalDateTime time;
            res = LocalDateTime.from(LocalDate.parse(date, formatter).atStartOfDay());
            //res = LocalDateTime.parse(date, formatter);
        } catch (Exception e) {
            setError(BIRTHDATE_FIELD, e.getMessage());
        }
        human.setBirthDate(res);
    }
    
    /*
    * Appel à la validation de l'adresse email reçue et initialisation de la
    * propriété email du bean
    */
   private void emailProcess(String email, Human human) {
       try {
           emailValidation( email );
       } catch ( FormValidationException e ) {
           setError( EMAIL_FIELD, e.getMessage() );
       }
       human.setEmail( email );
   }
   
   private void usernameProcess(String username, Human human) {
       try {
           nameValidation( username );
       } catch ( Exception e ) {
           setError( USERNAME_FIELD, e.getMessage() );
       }
       human.setUsername(username);
   }

/*
 * Appel à la validation des mots de passe reçus, chiffrement du mot de
 * passe et initialisation de la propriété motDePasse du bean
 */
    private void passwordProcess( String password, Human human ) {
        try {
            passwordValidation( password );
        } catch ( Exception e ) {
            setError( PASS_FIELD, e.getMessage() );
            setError( CONF_FIELD, null );
        }
        human.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        }

    /*
     * Ajoute un message correspondant au champ spécifié à la map des erreurs.
     */
    private void setError( String field, String message ) {
        errors.put( field, message );
    }
}