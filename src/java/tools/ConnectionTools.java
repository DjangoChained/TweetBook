package tools;

import dao.HumanDao;

public class ConnectionTools {
    public static void emailValidation(String email, boolean isSignUp, HumanDao humanDao) throws Exception {
        if ( email != null) {
            if(!email.matches( "([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)" ) ) {
                throw new Exception( "Merci de saisir une adresse mail valide." );
            } if(isSignUp){
                if (humanDao.get(email) != null )
                    throw new Exception("Cette adresse email est déjà utilisée, merci d'en choisir une autre.");
            }
        } else {
            throw new Exception( "Merci de saisir votre adresse mail." );
        }
    }
    
    public static void passwordValidation( String password ) throws Exception {
        if ( password != null ) {
            if ( password.length() < 6 ) {
                throw new Exception( "Le mot de passe doit contenir au moins 6 caractères." );
            }
        } else {
            throw new Exception( "Merci de saisir votre mot de passe." );
        }
    }
}
