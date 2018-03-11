package beans;

import java.time.LocalDateTime;

/**
 * Activité représentant une réaction à une publication
 */
public class ReactionActivity extends Activity{
    /**
     * la réaction à la publication
     */
    protected Reaction reaction;
    /**
     * l'identifiant en base du post auquel se réfère la réaction
     */
    protected int id_post;
    
    public ReactionActivity(){}
    
    public ReactionActivity(int id, LocalDateTime date, int id_human, int id_post){
        super(id, date, id_human);
        this.id_post = id_post;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }

    public int getId_post() {
        return id_post;
    }

    public void setId_post(int id_post) {
        this.id_post = id_post;
    }
    
    
}
