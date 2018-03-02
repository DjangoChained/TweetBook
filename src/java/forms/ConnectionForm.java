package forms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import beans.Human;
import java.util.Properties;

public final class ConnectionForm {
    private static final String EMAIL_FIELD  = "email";
    private static final String PASS_FIELD   = "password";

    private String              result;
    private Map<String, String> errors      = new HashMap<>();

    public String getResult() {
        return result;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Human connectHuman( Properties data, HttpServletRequest request ) {
        String email = data.getProperty("email");
        String password = data.getProperty("password");
        Human human = new Human();

        try {
            emailValidation( email );
        } catch ( Exception e ) {
            setError( EMAIL_FIELD, e.getMessage() );
        }
        human.setEmail( email );

        try {
            passwordValidation( password );
        } catch ( Exception e ) {
            setError( PASS_FIELD, e.getMessage() );
        }
        human.setPassword(password );

        return human;
    }

    /**
     * Valide l'adresse email saisie.
     */
    private void emailValidation( String email ) throws Exception {
        if ( email != null) {
            if(!email.matches( "([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)" ) ) {
                throw new Exception( "Merci de saisir une adresse mail valide." );
            }
        } else {
            throw new Exception( "Merci de saisir votre adresse mail." );
        }
    }

    /**
     * Valide le mot de passe saisi.
     */
    private void passwordValidation( String password ) throws Exception {
        if ( password != null ) {
            if ( password.length() < 6 ) {
                throw new Exception( "Le mot de passe doit contenir au moins 6 caractères." );
            }
        } else {
            throw new Exception( "Merci de saisir votre mot de passe." );
        }
    }

    /*
     * Ajoute un message correspondant au champ spécifié à la map des erreurs.
     */
    private void setError( String field, String message ) {
        errors.put( field, message );
    }
}