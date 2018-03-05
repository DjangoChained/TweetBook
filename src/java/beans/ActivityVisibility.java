package beans;

public enum ActivityVisibility {
    
<<<<<<< HEAD
    authoronly ("vous"),
    friends("amis"),
    all("tout le monde");
=======
    AUTHOR ("author"),
    FRIENDS("friends"),
    ALL("all");
>>>>>>> a01e5892ec9b85ff5a76842f89b0805dc8b763f0
    
    private String name = "";

  ActivityVisibility(String name){
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
  
  public static ActivityVisibility fromString(String name) {
      for(ActivityVisibility a : ActivityVisibility.values()) {
          if(a.toString().equalsIgnoreCase(name)) return a;
      }
      throw new IllegalArgumentException("Cette valeur n'est pas une visibilité valide.");
  }
}
