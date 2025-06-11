package metier;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "Appartient")
@IdClass(Appartient.AppartientId.class)
public class Appartient implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "idUtilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Id
    @ManyToOne
    @JoinColumn(name = "nomEspace", nullable = false)
    private EspaceTravail nomEspace;

    // Classe embarquée pour la clé composite
    public static class AppartientId implements Serializable {
        private Integer utilisateur;
        private String nomEspace;

        // Getters/Setters, equals(), hashCode()
    }

    // Getters/Setters
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public EspaceTravail getNomEspace() { return nomEspace; }
    public void setNomEspace(EspaceTravail nomEspace) { this.nomEspace = nomEspace; }
}