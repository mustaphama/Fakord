package metier;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 255)
    private String contenu;
    @Column(nullable = false)
    private LocalDateTime temps;
    @ManyToOne
    @JoinColumn(name = "idUtilisateurEmetteur")
    private Utilisateur utilisateurEmetteur;
    @ManyToOne
    @JoinColumn(name = "idUtilisateurRecepteur")
    private Utilisateur utilisateurRecepteur;

    public Message() {}
    public Message(String contenu, LocalDateTime temps) {
        this.contenu = contenu;
        this.temps = temps;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getContenu() {
        return contenu;
    }
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getTemps() {
        return temps;
    }
    public void setTemps(LocalDateTime temps) {
        this.temps = temps;
    }
    public Utilisateur getUtilisateurEmetteur() {
        return utilisateurEmetteur;
    }
    public void setUtilisateurEmetteur(Utilisateur utilisateurEmetteur) {
        this.utilisateurEmetteur = utilisateurEmetteur;
    }
    public Utilisateur getUtilisateurRecepteur() {
        return utilisateurRecepteur;
    }
    public void setUtilisateurRecepteur(Utilisateur utilisateurRecepteur) {
        this.utilisateurRecepteur = utilisateurRecepteur;
    }
    public int getUtilisateurEmetteurId() {
        return utilisateurEmetteur.getId();
    }
}
