package beans;

public enum ActivityVisibility {
    
    AUTHOR ("author"),
    FRIENDS("friends"),
    ALL("all");
    
    private String name = "";

  ActivityVisibility(String name){
    this.name = name;
  }

  public String toString(){
    return name;
  }
}
