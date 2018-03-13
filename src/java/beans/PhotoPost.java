package beans;

import java.time.LocalDateTime;

/**
 * Publication comportant une photo (non fonctionnel)
 */
public class PhotoPost extends Post{
    /**
     * le chemin vers la photo
     */
    private String photoPath;

    public PhotoPost(){}
    
    /**
     *
     * @param id identifiant en base de la publication
     * @param date date de création de la publication
     * @param id_human identifiant en base de l'utilisateur ayant produit la publication
     * @param content contenu au format texte de la publication
     * @param photoPath chemin vers la photo
     */
    public PhotoPost(int id, LocalDateTime date, int id_human, String content, String photoPath){
        super(id, date, id_human, content);
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
