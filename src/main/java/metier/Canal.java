package metier;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "Canal")
public class Canal implements Serializable {

    @Id
    @Column(length = 100)
    private String nom;

    @Column(name = "statue", length = 50)
    private String statut;

    @ManyToOne
    @JoinColumn(name = "nomEspace", nullable = false)
    private EspaceTravail espaceTravail;

    @OneToMany(mappedBy = "canal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Publie> publications;

    @OneToMany(mappedBy = "nomCanal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InviteCanal> invitations;

    public Canal() {}

    // Getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public EspaceTravail getEspaceTravail() { return espaceTravail; }
    public void setEspaceTravail(EspaceTravail espaceTravail) { this.espaceTravail = espaceTravail; }
}