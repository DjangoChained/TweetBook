/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

/**
 * Enumération représentant une réaction à une publication
 */
public enum Reaction {

    /**
     * permet à un utilisateur de dire qu'il aime une publication
     */
    like ("like"),

    /**
     * permet à un utilisateur de dire qu'il n'aime pas une publication
     */
    dislike ("dislike");

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
