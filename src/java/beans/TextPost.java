package beans;

import java.time.LocalDateTime;


/**
 * Publication contenant uniquement du texte
 */
public class TextPost extends Post {

    public TextPost(){}
    
    /**
     *
     * @param id identifiant en base de la publication
     * @param date date de création de la publication
     * @param content contenu au format texte de la publication
     * @param id_human identifiant en base de l'utilisateur ayant produit la publication
     */
    public TextPost(int id, LocalDateTime date, String content, int id_human){
        super(id, date, id_human, content);
    }
}
