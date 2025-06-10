package metier;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Message")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "contenu")
    private String contenu;

    @Column(name = "temps")
    private LocalDateTime temps;

    // Constructeur vide
    public Message() {}

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getTemps() { return temps; }
    public void setTemps(LocalDateTime temps) { this.temps = temps; }
}
