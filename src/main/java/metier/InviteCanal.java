package metier;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "InviteCanal")
@IdClass(InviteCanal.InviteCanalId.class)
public class InviteCanal implements Serializable{

    @Id
    @ManyToOne
    @JoinColumn(name = "idUtilisateurInvitant", nullable = false)
    private Utilisateur utilisateurInvitant;

    @Id
    @Column(length = 100)
    private String emailInvite;

    @Id
    @ManyToOne
    @JoinColumn(name = "nomCanal", nullable = false)
    private Canal nomCanal;

    @Column(length = 50)
    private String statut;

    public static class InviteCanalId implements Serializable {
        private Integer utilisateurInvitant;
        private String emailInvite;
        private String nomCanal;
    }

    public InviteCanal() {}

    public String getEmailInvite() {
        return emailInvite;
    }

    public Canal getNomCanal() {
        return nomCanal;
    }

    public String getStatut() {
        return statut;
    }

    public Utilisateur getUtilisateurInvitant() {
        return utilisateurInvitant;
    }

    public void setEmailInvite(String emailInvite) {
        this.emailInvite = emailInvite;
    }
    public void setNomCanal(Canal nomCanal) {
        this.nomCanal = nomCanal;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setUtilisateurInvitant(Utilisateur utilisateurInvitant) {
        this.utilisateurInvitant = utilisateurInvitant;
    }
}