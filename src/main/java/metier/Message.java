package metier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Message")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(nullable = false)
    private LocalDateTime temps;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ecrit> ecrits=new HashSet<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Publie> publications=new HashSet<>();

    @JsonIgnoreProperties({"messages", "message"})
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Reagit> reactions = new HashSet<>();

    public Message() {}

    public Message(String contenu, LocalDateTime temps) {
        this.contenu = contenu;
        this.temps = temps;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDateTime getTemps() { return temps; }
    public void setTemps(LocalDateTime temps) { this.temps = temps; }
    public Set<Ecrit> getEcrits() {
        return ecrits;
    }
    public Set<Reagit> getReactions() {
        return reactions;
    }
    public Set<Publie> getPublications() {return publications; }
}