package metier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "Reagit")
@IdClass(Reagit.ReagitId.class)
public class Reagit implements Serializable{

    @Id
    @ManyToOne
    @JoinColumn(name = "idUtilisateur", nullable = false)
    @JsonIgnoreProperties({"reactions","messages","ecrits"})
    private Utilisateur utilisateur;

    @Id
    @ManyToOne
    @JoinColumn(name = "idMessage", nullable = false)
    @JsonIgnore
    private Message message;

    @Column(name="reaction",length = 50)
    private String reaction;

    public Reagit(Utilisateur u, Message m, String reaction) {
        this.utilisateur = u;
        this.message = m;
        this.reaction = reaction;
    }

    public Reagit() {

    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public static class ReagitId implements Serializable {
        private Integer utilisateur;
        private Integer message;

        public Integer getMessage() {
            return message;
        }

        public Integer getUtilisateur() {
            return utilisateur;
        }
    }
    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }
}