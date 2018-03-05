package beans;

import java.time.LocalDateTime;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pierant
 */
public class ReactionActivity extends Activity{
    
    protected Reaction reaction;
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
