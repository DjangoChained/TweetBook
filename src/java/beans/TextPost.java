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
public class TextPost extends Post {
    
    public TextPost(){}
    
    public TextPost(int id, LocalDateTime date, String content, int id_human){
        super(id, date, id_human, content);
    }
}
