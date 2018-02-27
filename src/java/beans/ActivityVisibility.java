package beans;

public enum ActivityVisibility {
    
    AUTHOR ("vous"),
    FRIENDS("amis"),
    ALL("tout le monde");
    
    private String name = "";

  ActivityVisibility(String name){
    this.name = name;
  }

  public String toString(){
    return name;
  }
}
