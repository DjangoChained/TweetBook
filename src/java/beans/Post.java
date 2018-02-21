package beans;

import java.time.LocalDate;
/**
 *
 * @author pierant
 */
public abstract class Post extends Activity {
    private String content;
    
    public Post(){}
    
    public Post(int id, LocalDate date, int id_human, String content) {
        super(id, date, id_human);
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
