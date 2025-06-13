package metier;

import java.time.LocalDateTime;

public class MessageDTO {
    public Integer id;
    public String contenu;
    public String temps;
public Utilisateur utilisateur;

    public MessageDTO(Message m, Utilisateur utilisateur ){
        this.id = m.getId();
        this.contenu = m.getContenu();
        this.temps = m.getTemps().toString();
        this.utilisateur = utilisateur;
    }
}

