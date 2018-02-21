package beans;

public enum ActivityVisibility {
    
    AUTHOR ("Vous"),
    FRIENDS("Amis"),
    ALL("tout le monde");
    
    private String name = "";

  ActivityVisibility(String name){
    this.name = name;
  }

  public String toString(){
    return name;
  }
}
