package dao;

import metier.EspaceTravail;
import metier.InviteEspace;
import metier.Utilisateur;

import java.util.List;

public interface InviteEspaceDAO {
    void create(InviteEspace invite);

    List<InviteEspace> findPendingByEmail(String email);

    boolean acceptInvitation(Utilisateur utilisateur, EspaceTravail espace, Utilisateur invitant);
}
