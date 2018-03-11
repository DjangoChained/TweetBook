package beans;

import java.time.LocalDateTime;

/**
 * Activité représentant un lien d'amitié entre deux utilisateurs
 */
public class FriendshipActivity extends Activity {
    
    /**
     * l'identifiant en base de l'ami
     */
    private int id_second_human;
    
    /**
     * 
     * @param id identifiant en base de l'activité
     * @param date date de création de l'activité
     * @param id_human identifiant en base de l'utilisateur ayant produit l'activité
     * @param id_second_human identifiant en base de l'ami
     */
    public FriendshipActivity(int id, LocalDateTime date, int id_human, int id_second_human){
        super(id, date, id_human);
        this.id_second_human = id_second_human;
    }
    
    public FriendshipActivity() {}

    public int getId_second_human() {
        return id_second_human;
    }
    
    public void setId_second_human(int id_second_human) {
        this.id_second_human = id_second_human;
    }
}
