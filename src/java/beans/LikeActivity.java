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
public class LikeActivity extends ReactionActivity {
    
    public LikeActivity(){
        this.reaction = beans.Reaction.LIKE;
    }
    
    public LikeActivity(int id, LocalDateTime date, int id_human, Reaction reaction, int id_post){
        super(id, date, id_human, id_post);
        this.reaction = beans.Reaction.LIKE;
    }
}
