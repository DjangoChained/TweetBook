package beans;

/**
 * Enumération représentant la visibilité des publications d'un utilisateur
 */
public enum ActivityVisibility {
    
    /**
     * les activités de l'utilisateur ne sont visibles que par lui même
     */
    authoronly ("authoronly"),

    /**
     * les activités de l'utilisateur sont visibles par lui même et ses amis
     */
    friends("friends"),

    /**
     * les activités de l'utilisateur sont visibles par tout le monde
     */
    all("all");
    
    private String name = "";

    ActivityVisibility(String name){
      this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
  
    /**
     * Permet de déterminer un type de visiblité à partir d'une chaîne de caractères
     * @param name la chaîne de caractère représentant la visibilité de l'activité
     * @return la visiblité sous forme d'énumération
     * @throws IllegalArgumentException si la chaîne de caractère fournie ne représente pas une visibilité valable
     */
    public static ActivityVisibility fromString(String name) {
      for(ActivityVisibility a : ActivityVisibility.values()) {
          if(a.toString().equalsIgnoreCase(name)) return a;
      }
      throw new IllegalArgumentException("Cette valeur n'est pas une visibilité valide.");
  }
}
