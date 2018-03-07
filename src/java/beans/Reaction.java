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
  
  public static Reaction fromString(String name) {
      for(Reaction r : Reaction.values()) {
          if(r.toString().equalsIgnoreCase(name)) return r;
      }
      throw new IllegalArgumentException("Cette valeur n'est pas une réaction valide.");
  }
}
