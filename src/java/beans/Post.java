package beans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** *
 * Activité de base représentant une publication
 */
public abstract class Post extends Activity {

    /**
     * le contenu au format texte de la publication
     */
    protected String content;

    public Post(){}
    
    /**
     *
     * @param id identifiant en base de la publication
     * @param date date de création de la publication
     * @param id_human identifiant en base de l'utilisateur ayant produit la publication
     * @param content * le contenu au format texte de la publication
     */
    public Post(int id, LocalDateTime date, int id_human, String content) {
        super(id, date, id_human);
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Permet de récupérer les propriétés d'une publication au format Json
     * @return une chaîne de caractères avec les propriétés d'une publication au format Json
     */
    public String getJson(){
        return "{\n" +
                "    \"status\": \"success\",\n" +
                "    \"post\": {\n" +
                "    \"id\": \""+this.getId()+"\",\n" +
                "    \"authorid\": \""+this.getId_human()+"\",\n" +
                "    \"date\": \""+this.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"\",\n" +
                "    \"content\": \""+this.getContent().replaceAll("\n", "\\n")+"\",\n";
    }
}
