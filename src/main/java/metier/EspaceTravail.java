package metier;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "EspaceTravail")
public class EspaceTravail implements Serializable {

    @Id
    @Column(name = "nom", length = 100)
    private String nom;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "espaceTravail", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Canal> canaux;

    @OneToMany(mappedBy = "nomEspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appartient> membres;

    @OneToMany(mappedBy = "nomEspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Administre> administrateurs;

    @OneToMany(mappedBy = "nomEspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InviteEspace> invitations;

    public EspaceTravail() {}

    public EspaceTravail(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}