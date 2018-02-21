package beans;

import java.time.LocalDate;

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
    
    private Reaction reaction;
    private int id_post;
    
    public ReactionActivity(){}
    
    public ReactionActivity(int id, LocalDate date, int id_human, Reaction reaction, int id_post){
        super(id, date, id_human);
        this.reaction = reaction;
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
