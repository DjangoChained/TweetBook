package beans;

public enum ActivityVisibility {
    
    authoronly ("vous"),
    friends("amis"),
    all("tout le monde");
    
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
