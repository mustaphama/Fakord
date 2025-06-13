package dao;

import metier.*;
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
        String jpql =  """
            SELECT DISTINCT m FROM Message m
            LEFT JOIN FETCH m.reactions
            JOIN FETCH m.ecrits e
            JOIN FETCH e.utilisateurEmetteur
            WHERE (e.utilisateurEmetteur.id = :idSender AND e.utilisateurRecepteur.id = :idReceiver)
               OR (e.utilisateurEmetteur.id = :idReceiver AND e.utilisateurRecepteur.id = :idSender)
            ORDER BY m.temps
        """;
        TypedQuery<Message> query = em.createQuery(jpql, Message.class);
        query.setParameter("idSender", idSender);
        query.setParameter("idReceiver", idReceiver);
        List<Message> messages = query.getResultList();
        for (Message m : messages) {
            m.getReactions().size();
        }
        return messages;
    }

    public boolean supprimerMessage(int idMessage, int idUser) {
        return false;
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
        @Override
        public List<Message> getMessagesByCanal(String nomCanal) {
            String jpql = """
            SELECT m FROM Message m
            WHERE m.id IN (
                SELECT p.message.id FROM Publie p WHERE p.canal.nom = :nomCanal
            )
            ORDER BY m.temps ASC
        """;
            TypedQuery<Message> query = em.createQuery(jpql, Message.class);
            query.setParameter("nomCanal", nomCanal);
            return query.getResultList();
        }

    public void createMessageInCanal(Message message, Integer idUtilisateur, String nomCanal) throws Exception {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Persiste le message
            em.persist(message);
            em.flush(); // pour avoir l'ID généré

            // Récupère l'utilisateur et le canal depuis la base
            Utilisateur utilisateur = em.find(Utilisateur.class, idUtilisateur);
            if (utilisateur == null) throw new Exception("Utilisateur introuvable");

            Canal canal = em.find(Canal.class, nomCanal);
            if (canal == null) throw new Exception("Canal introuvable");

            // Crée l'objet Publie et set les relations
            Publie publication = new Publie();
            publication.setMessage(message);
            publication.setUtilisateur(utilisateur);
            publication.setCanal(canal);

            // Persiste la publication
            em.persist(publication);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }



}