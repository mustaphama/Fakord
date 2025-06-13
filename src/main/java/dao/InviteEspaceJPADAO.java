package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import metier.InviteEspace;
import metier.Utilisateur;
import metier.EspaceTravail;

import java.util.List;

public class InviteEspaceJPADAO implements InviteEspaceDAO{
    private final EntityManager em;

    public InviteEspaceJPADAO(EntityManager em) {
        this.em = em;
    }
    @Override
    public void create(InviteEspace invitation) {
        em.getTransaction().begin();
        em.persist(invitation);
        em.getTransaction().commit();
    }
    @Override
    public List<InviteEspace> findPendingByEmail(String email) {
        String jpql = "SELECT i FROM InviteEspace i WHERE i.emailInvite = :email AND i.statut = 'en_attente'";
        TypedQuery<InviteEspace> query = em.createQuery(jpql, InviteEspace.class);
        query.setParameter("email", email);
        return query.getResultList();
    }
    @Override
    public boolean acceptInvitation(Utilisateur utilisateur, EspaceTravail espace, Utilisateur invitant) {
        try {
            em.getTransaction().begin();

            // Mettre à jour le statut de l'invitation
            String jpql = "UPDATE InviteEspace i SET i.statut = 'accepte' " +
                    "WHERE i.emailInvite = :email AND i.nomEspace = :espace AND i.utilisateurInvitant = :invitant";
            int updated = em.createQuery(jpql)
                    .setParameter("email", utilisateur.getPseudo())
                    .setParameter("espace", espace)
                    .setParameter("invitant", invitant)
                    .executeUpdate();

            if (updated > 0) {
                // Ajouter l'utilisateur à l'espace de travail
                // (Supposons que vous avez une table/entité Appartient pour gérer les appartenances)
                // Ici vous devrez implémenter la logique pour ajouter à la table Appartient
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        }
    }
}