package metier;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "Reagit")
@IdClass(Reagit.ReagitId.class)
public class Reagit implements Serializable{

    @Id
    @ManyToOne
    @JoinColumn(name = "idUtilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Id
    @ManyToOne
    @JoinColumn(name = "idMessage", nullable = false)
    private Message message;

    @Column(length = 50)
    private String reaction;

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