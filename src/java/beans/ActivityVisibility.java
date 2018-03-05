package beans;

public enum ActivityVisibility {
    
    authoronly ("vous"),
    friends("amis"),
    all("tout le monde");
    
    private String name = "";

  ActivityVisibility(String name){
    this.name = name;
  }

  public String toString(){
    return name;
  }
}
