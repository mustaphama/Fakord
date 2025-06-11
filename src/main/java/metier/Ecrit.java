package metier;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Ecrit")
@IdClass(Ecrit.EcritId.class)
public class Ecrit implements Serializable{

    @Id
    @ManyToOne
    @JoinColumn(name = "idUtilisateurEmetteur", nullable = false)
    private Utilisateur utilisateurEmetteur;

    @Id
    @ManyToOne
    @JoinColumn(name = "idMessage", nullable = false)
    private Message message;

    @Id
    @ManyToOne
    @JoinColumn(name = "idUtilisateurRecepteur", nullable = false)
    private Utilisateur utilisateurRecepteur;

    public static class EcritId implements Serializable {
        private Integer utilisateurEmetteur;
        private Integer message;
        private Integer utilisateurRecepteur;
        public Integer getUtilisateurEmetteur() {
            return utilisateurEmetteur;
        }
        public void setUtilisateurEmetteur(Integer utilisateurEmetteur) {
            this.utilisateurEmetteur = utilisateurEmetteur;
        }
        public Integer getMessage() {
            return message;
        }
        public void setMessage(Integer message) {
            this.message = message;
        }
        public Integer getUtilisateurRecepteur() {
            return utilisateurRecepteur;
        }
        public void setUtilisateurRecepteur(Integer utilisateurRecepteur) {
            this.utilisateurRecepteur = utilisateurRecepteur;
        }
    }
    public Ecrit() {}

    public Message getMessage() {
        return message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }
    public Utilisateur getUtilisateurEmetteur() {
        return utilisateurEmetteur;
    }

    public void setUtilisateurEmetteur(Utilisateur utilisateurEmetteur) {
        this.utilisateurEmetteur = utilisateurEmetteur;
    }

    public void setUtilisateurRecepteur(Utilisateur utilisateurRecepteur) {
        this.utilisateurRecepteur = utilisateurRecepteur;
    }

    public Utilisateur getUtilisateurRecepteur() {
        return utilisateurRecepteur;
    }
}