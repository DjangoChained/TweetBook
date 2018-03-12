package beans;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 *
 */
public class Human {
    /**
     * identifiant en base de l'utilisateur
     */
    private int id;
    /**
     * nom de famille de l'utilisateur
     */
    private String lastname;
    /**
     * prénom de l'utilisateur
     */
    private String firstname;
    /**
     * date de naissance de l'utilisateur
     */
    private LocalDateTime birthDate;
    /**
     * adresse mail de l'utilisateur
     */
    private String email;
    /**
     * nom d'utilisateur
     */
    private String username;
    /**
     * mot de passe de l'utilisateur
     */
    private String password;
    /**
     * visibilité des publications  de l'utilisateur
     */
    private ActivityVisibility visibility;
 
    public Human(int id, String lastname, String firstname, LocalDateTime birthDate, String email, String username, String visibility) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthDate = birthDate;
        this.email = email;
        this.username = username;
        this.visibility = ActivityVisibility.fromString(visibility);
    }
    
    public Human(){}
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }
    
    /**
     * Permet de convertir une chaîne de caractère en date pour définir la date d'anniversaire de l'utilisateur
     * @param birthdate chaîne de caractères représentant une date et devant être au format "yyyy-MM-dd"
     */
    public void setBirthDate(String birthdate) {
        final DateTimeFormatter formatter;
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        this.birthDate = LocalDateTime.from(LocalDate.parse(birthdate, formatter).atStartOfDay());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setVisibility(ActivityVisibility visibility) {
        this.visibility = visibility;
    }
    
    public void setVisibility(String visibility) {
        this.visibility = ActivityVisibility.fromString(visibility);
    }

    public ActivityVisibility getVisibility() {
        return visibility;
    }
}
