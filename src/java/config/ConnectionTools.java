package config;

import dao.HumanDao;

/**
 * Classe utilitaire regroupant des fonctions de contrôle des données
 */
public class ConnectionTools {

    /**
     * Permet de s'assurer qu'une adresse mail est non nulle, valide et non présente en base
     * @param email l'adresse mail à valider
     * @param isSignUp permet de déterminer si la validation de l'adresse se fait lors de la connection ou de l'inscription (qui nécessite l'unicité de l'adresse mail)
     * @param humanDao le Dao permettant de manipuler les utilisateurs
     * @throws Exception lorsque l'adresse mail fournie ne répond pas aux critères susmentionnés
     */
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
    
    /**
     * Permet de s'assurer qu'un mot de passe est non nul et fait au moins 6 caractères de long
     * @param password le mot de passe à valider
     * @throws Exception lorsque le mot de passe est null ou fait moins de 6 caractères
     */
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
