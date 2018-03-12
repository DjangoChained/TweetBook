package beans;

import java.time.LocalDateTime;

/**
 * Publication comportant un lien
 */
public class LinkPost extends Post {
    /**
     * le lien mis en avant pas la publication
     */
    private String url;
    /**
     * le titre donné au contenu référé par le lien
     */
    private String title;
    
    public LinkPost(){}
    
    public LinkPost(int id, LocalDateTime date, int id_human, String content, String url, String title){
        super(id, date, id_human, content) ;
        this.url = url;
        this.title = title;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
