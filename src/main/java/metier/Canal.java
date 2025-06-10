package metier;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Canal")
public class Canal implements Serializable {

    @Id
    @Column(name = "nom", length = 100)
    private String nom;

    @Column(name = "statue", length = 50)
    private String statue;

    @ManyToOne
    @JoinColumn(name = "nomEspace", referencedColumnName = "nom", nullable = false)
    private EspaceTravail espaceTravail;

    public Canal() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getStatue() { return statue; }
    public void setStatue(String statue) { this.statue = statue; }

    public EspaceTravail getEspaceTravail() { return espaceTravail; }
    public void setEspaceTravail(EspaceTravail espaceTravail) { this.espaceTravail = espaceTravail; }
}
