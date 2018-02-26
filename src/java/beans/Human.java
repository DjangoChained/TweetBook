package beans;


import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pierant
 */
public class Human {
    private int id;
    private String lastname;
    private String firstname;
    private LocalDateTime birthDate;
    private String email;
    private String username;
    private String password;
    private ActivityVisibility visibility;
    
    public Human(int id, String lastname, String firstname, LocalDateTime birthDate, String email, String username, String visibility) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthDate = birthDate;
        this.email = email;
        this.username = username;
        this.visibility = ActivityVisibility.valueOf(visibility);
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
        this.visibility = ActivityVisibility.valueOf(visibility);
    }

    public ActivityVisibility getVisibility() {
        return visibility;
    }
}
