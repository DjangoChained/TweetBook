package forms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import beans.Human;

public final class ConnectionForm {
    private static final String EMAIL_FIELD  = "email";
    private static final String PASS_FIELD   = "password";

    private String              result;
    private Map<String, String> errors      = new HashMap<String, String>();

    public String getResult() {
        return result;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Human connectHuman( HttpServletRequest request ) {
        /* Récupération des champs du formulaire */
        String email = getFieldValue( request, EMAIL_FIELD );
        String password = getFieldValue( request, PASS_FIELD );

        Human human = new Human();

        /* Validation du champ email. */
        try {
            emailValidation( email );
        } catch ( Exception e ) {
            setError( EMAIL_FIELD, e.getMessage() );
        }
        human.setEmail( email );

        /* Validation du champ mot de passe. */
        try {
            passwordValidation( password );
        } catch ( Exception e ) {
            setError( PASS_FIELD, e.getMessage() );
        }
        human.setPassword(password );

        /* Initialisation du résultat global de la validation. */
        if ( errors.isEmpty() ) {
            result = "Succès de la connexion.";
        } else {
            result = "Échec de la connexion.";
        }

        return human;
    }

    /**
     * Valide l'adresse email saisie.
     */
    private void emailValidation( String email ) throws Exception {
        if ( email != null && !email.matches( "([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)" ) ) {
            throw new Exception( "Merci de saisir une adresse mail valide." );
        }
    }

    /**
     * Valide le mot de passe saisi.
     */
    private void passwordValidation( String password ) throws Exception {
        if ( password != null ) {
            if ( password.length() < 3 ) {
                throw new Exception( "Le mot de passe doit contenir au moins 3 caractères." );
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

    /*
     * Méthode utilitaire qui retourne null si un champ est vide, et son contenu
     * sinon.
     */
    private static String getFieldValue( HttpServletRequest request, String field ) {
        String valeur = request.getParameter( field );
        if ( valeur == null || valeur.trim().length() == 0 ) {
            return null;
        } else {
            return valeur;
        }
    }
}