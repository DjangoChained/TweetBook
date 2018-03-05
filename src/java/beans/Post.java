package beans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author pierant
 */
public abstract class Post extends Activity {
    protected String content;
    
    public Post(){}
    
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
    
    public String getJson(){
        return "{\n" +
                "    \"status\": \"success\",\n" +
                "    \"post\": {\n" +
                "    \"id\": \""+this.getId()+"\",\n" +
                "    \"authorid\": \""+this.getId_human()+"\",\n" +
                "    \"date\": \""+this.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"\",\n" +
                "    \"content\": \""+this.getContent()+"\",\n";
    }
}
