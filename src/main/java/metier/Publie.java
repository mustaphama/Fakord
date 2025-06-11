package metier;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Publie")
@IdClass(Publie.PublieId.class)
public class Publie implements Serializable{

    @Id
    @ManyToOne
    @JoinColumn(name = "idUtilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Id
    @ManyToOne
    @JoinColumn(name = "idMessage", nullable = false)
    private Message message;

    @Id
    @ManyToOne
    @JoinColumn(name = "nomCanal", nullable = false)
    private Canal canal;

    public static class PublieId implements Serializable {
        private Integer utilisateur;
        private Integer message;
        private String canal;
        public Integer getUtilisateur() {
            return utilisateur;
        }
        public void setUtilisateur(Integer utilisateur) {
            this.utilisateur = utilisateur;
        }
        public Integer getMessage() {
            return message;
        }
        public void setMessage(Integer message) {
            this.message = message;
        }
        public String getCanal() {
            return canal;
        }
        public void setCanal(String canal) {
            this.canal = canal;
        }
    }
    public Publie() {}
    public Publie(Utilisateur utilisateur, Message message, Canal canal) {
        this.utilisateur = utilisateur;
        this.message = message;
        this.canal = canal;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public Message getMessage() {
        return message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }
    public Canal getCanal() {
        return canal;
    }
    public void setCanal(Canal canal) {
        this.canal = canal;
    }
}