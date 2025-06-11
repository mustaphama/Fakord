package metier;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "Utilisateur")
public class Utilisateur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String pseudo;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(nullable = false, length = 255)
    private String mdp;

    @OneToMany(mappedBy = "utilisateurEmetteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ecrit> messagesEnvoyes;

    @OneToMany(mappedBy = "utilisateurRecepteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ecrit> messagesRecus;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appartient> espacesTravail;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Administre> espacesAdministres;

    @OneToMany(mappedBy = "utilisateurInvitant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InviteEspace> invitationsEspaceEnvoyees;

    @OneToMany(mappedBy = "utilisateurInvitant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InviteCanal> invitationsCanalEnvoyees;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reagit> reactions;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Publie> publications;

    public Utilisateur() {}

    public Utilisateur(String pseudo, String nom, String prenom, String mdp) {
        this.pseudo = pseudo;
        this.nom = nom;
        this.prenom = prenom;
        this.mdp = mdp;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }
}