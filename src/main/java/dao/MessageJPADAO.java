package dao;

import metier.Message;
import metier.Utilisateur;
import metier.Ecrit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class MessageJPADAO implements MessageDAO {

    private final EntityManager em;

    public MessageJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean sendPrivateMessage(int idSender, int idReceiver, String message) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Message newMessage = new Message();
            newMessage.setContenu(message);
            newMessage.setTemps(LocalDateTime.now());
            em.persist(newMessage);

            Utilisateur sender = em.find(Utilisateur.class, idSender);
            Utilisateur receiver = em.find(Utilisateur.class, idReceiver);

            if (sender == null || receiver == null) {
                tx.rollback();
                return false;
            }

            Ecrit ecrit = new Ecrit();
            ecrit.setUtilisateurEmetteur(sender);
            ecrit.setMessage(newMessage);
            ecrit.setUtilisateurRecepteur(receiver);
            em.persist(ecrit);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Failed to send private message", e);
        }
    }

    @Override
    public List<Message> getMessagesPrivee(int idSender, int idReceiver) {
        String jpql = """
            SELECT m FROM Message m
            JOIN Ecrit e ON m = e.message
            WHERE (e.utilisateurEmetteur.id = :idSender AND e.utilisateurRecepteur.id = :idReceiver)
               OR (e.utilisateurEmetteur.id = :idReceiver AND e.utilisateurRecepteur.id = :idSender)
            ORDER BY m.temps
            """;
        TypedQuery<Message> query = em.createQuery(jpql, Message.class);
        query.setParameter("idSender", idSender);
        query.setParameter("idReceiver", idReceiver);

        return query.getResultList();
    }

    public boolean supprimerMessage(int idMessage, int idUser) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Message message = em.find(Message.class, idMessage);

            if (message == null || message.getEcrits().stream().noneMatch(e -> e.getUtilisateurEmetteur().getId() == idUser)) {
                tx.rollback();
                return false;
            }

            em.remove(message);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erreur suppression message", e);
        }
    }

    public boolean modifierMessage(int idMessage, int idUser, String nouveauContenu) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Message message = em.find(Message.class, idMessage);

            if (message == null || message.getEcrits().stream().noneMatch(e -> e.getUtilisateurEmetteur().getId() == idUser)) {
                tx.rollback();
                return false;
            }

            message.setContenu(nouveauContenu);
            em.merge(message);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erreur modification message", e);
        }
    }

    public boolean modifierContenuMessage(int messageId, int userId, String nouveauContenu) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Message message = em.find(Message.class, messageId);
            if (message == null) {
                tx.rollback();
                return false;
            }

            boolean estAuteur = message.getEcrits().stream()
                    .anyMatch(e -> e.getUtilisateurEmetteur().getId() == userId);

            if (!estAuteur) {
                tx.rollback();
                return false;
            }

            message.setContenu(nouveauContenu);
            em.merge(message);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erreur lors de la modification du message", e);
        }
    }
}