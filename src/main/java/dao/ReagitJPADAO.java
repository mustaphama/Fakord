package dao;

import jakarta.persistence.EntityManager;
import metier.Message;
import metier.Reagit;
import metier.Utilisateur;

public class ReagitJPADAO {
    private final EntityManager em;

    public ReagitJPADAO(EntityManager em) {
        this.em = em;
    }

    public void ajouterReaction(int userId, int messageId, String reaction) {
        Utilisateur u = em.find(Utilisateur.class, userId);
        Message m = em.find(Message.class, messageId);
        Reagit r = new Reagit(u, m, reaction);
        em.persist(r);
    }

    public void modifierReaction(int userId, int messageId, String reaction) {
        Reagit r = em.createQuery("SELECT r FROM Reagit r WHERE r.utilisateur.id = :uid AND r.message.id = :mid", Reagit.class)
                .setParameter("uid", userId)
                .setParameter("mid", messageId)
                .getSingleResult();
        r.setReaction(reaction);
    }

    public void supprimerReaction(int userId, int messageId) {
        Reagit r = em.createQuery("SELECT r FROM Reagit r WHERE r.utilisateur.id = :uid AND r.message.id = :mid", Reagit.class)
                .setParameter("uid", userId)
                .setParameter("mid", messageId)
                .getSingleResult();
        em.remove(r);
    }
}
