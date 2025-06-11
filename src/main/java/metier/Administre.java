package metier;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "Administre")
@IdClass(Administre.AdministreId.class)
public class Administre implements Serializable{

    @Id
    @ManyToOne
    @JoinColumn(name = "idUtilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Id
    @ManyToOne
    @JoinColumn(name = "nomEspace", nullable = false)
    private EspaceTravail nomEspace;

    public static class AdministreId implements Serializable {
        private Integer utilisateur;
        private String nomEspace;
        // Getters/Setters, equals(), hashCode()
    }


}