package beans;

import java.time.LocalDateTime;

/**
 *
 * @author pierant
 */
public class FriendshipActivity extends Activity {
     
    private int id_second_human;
    
    public FriendshipActivity(int id, LocalDateTime date, int id_human, int id_second_human){
        super(id, date, id_human);
        this.id_second_human = id_second_human;
    }

    public FriendshipActivity() {
    }
    
    public int getId_second_human() {
        return id_second_human;
    }

    public void setId_second_human(int id_second_human) {
        this.id_second_human = id_second_human;
    }
}
