package beans;

import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author pierant
 */
public abstract class Activity {
    private int id;
    private LocalDate date;
    private int id_human;
    
    public Activity(){}
    
    public Activity(int id, LocalDate date, int id_human) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public int getId_human() {
        return id_human;
    }

    public void setId_human(int id_human) {
        this.id_human = id_human;
    }
}
