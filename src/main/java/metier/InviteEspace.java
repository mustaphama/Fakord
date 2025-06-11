package metier;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "InviteEspace")
@IdClass(InviteEspace.InviteEspaceId.class)
public class InviteEspace implements Serializable{

    @Id
    @ManyToOne
    @JoinColumn(name = "idUtilisateurInvitant", nullable = false)
    private Utilisateur utilisateurInvitant;

    @Id
    @Column(length = 100)
    private String emailInvite;

    @Id
    @ManyToOne
    @JoinColumn(name = "nomEspace", nullable = false)
    private EspaceTravail nomEspace;

    @Column(length = 50)
    private String statut = "en_attente";

    public static class InviteEspaceId implements Serializable {
        private Integer utilisateurInvitant;
        private String emailInvite;
        private String nomEspace;
        public InviteEspaceId(Integer utilisateurInvitant, String emailInvite, String nomEspace) {
            this.utilisateurInvitant = utilisateurInvitant;
            this.emailInvite = emailInvite;
            this.nomEspace = nomEspace;
        }
    }
    public InviteEspace() {}

    public String getEmailInvite() {
        return emailInvite;
    }

    public void setEmailInvite(String emailInvite) {
        this.emailInvite = emailInvite;
    }

    public Utilisateur getUtilisateurInvitant() {
        return utilisateurInvitant;
    }

    public void setUtilisateurInvitant(Utilisateur utilisateurInvitant) {
        this.utilisateurInvitant = utilisateurInvitant;
    }
    public EspaceTravail getNomEspace() {
        return nomEspace;
    }
    public void setNomEspace(EspaceTravail nomEspace) {
        this.nomEspace = nomEspace;
    }
    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }
}