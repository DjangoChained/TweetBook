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
public class LinkPost extends Post {
    
    String url;
    String title;
    
    public LinkPost(){}
    
    public LinkPost(int id, LocalDateTime date, int id_human, String content, String url, String title){
        super(id, date, id_human, content) ;
        this.url = url;
        this.title = title;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
