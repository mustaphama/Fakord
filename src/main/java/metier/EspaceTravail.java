package metier;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "EspaceTravail")
public class EspaceTravail implements Serializable {

    @Id
    @Column(name = "nom", length = 100)
    private String nom;

    @Column(name = "description", length = 255)
    private String description;

    public EspaceTravail() {}

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
