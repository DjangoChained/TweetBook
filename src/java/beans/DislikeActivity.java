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
public class DislikeActivity extends ReactionActivity {
    
    public DislikeActivity(){}
    
    public DislikeActivity(int id, LocalDateTime date, int id_human, Reaction reaction, int id_post){
        super(id, date, id_human, reaction, id_post);
    }
}
