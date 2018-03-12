package beans;

import java.time.LocalDateTime;

/**
 * Classe de base représentant une activité
 */
public abstract class Activity {
    /**
     * identifiant en bdd de l'activité
     */
    protected int id;
    /**
     * date a laquelle l'activité a été créée
     */
    protected LocalDateTime date;
    /**
     * identifiant en base de l'utilisateur ayant produit l'activité
     */
    protected int id_human;
    
    public Activity(){}
    
    public Activity(int id, LocalDateTime date, int id_human) {
        this.id = id;
        this.date = date;
        this.id_human = id_human;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public int getId_human() {
        return id_human;
    }

    public void setId_human(int id_human) {
        this.id_human = id_human;
    }
}
