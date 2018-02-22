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
public class PhotoPost extends Post{
    
    String photoPath;
    
    public PhotoPost(){}
    
    public PhotoPost(int id, LocalDateTime date, int id_human, String content, String photoPath){
        super(id, date, id_human, content);
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
