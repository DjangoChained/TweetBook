/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

/**
 *
 * @author pierant
 */
public enum Reaction {
    LIKE ("like"),
    DISLIKE("dislike");
    
    private String name = "";

  Reaction(String name){
    this.name = name;
  }

  public String toString(){
    return name;
  }
}
