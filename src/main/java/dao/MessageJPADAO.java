package dao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import metier.Message;
import metier.Utilisateur;

import java.time.LocalDateTime;
import java.util.List;

import static org.example.fakord.ApplicationListener.getEmf;

public class MessageJPADAO implements MessageDAO {

    @Override
    public boolean sendPrivateMessage(int idSender, int idReceiver, String contenu) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();

            Message message = new Message();
            message.setContenu(contenu);
            message.setTemps(LocalDateTime.now());

            Utilisateur emetteur = em.find(Utilisateur.class, idSender);
            Utilisateur recepteur = em.find(Utilisateur.class, idReceiver);

            message.setUtilisateurEmetteur(emetteur);
            message.setUtilisateurRecepteur(recepteur);

            em.persist(message);
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Message> getMessagesPrivee(int idSender, int idReceiver) {
        EntityManager em = getEmf().createEntityManager();
        try {
            String jpql = """
                SELECT m FROM Message m
                WHERE (m.utilisateurEmetteur.id = :idSender AND m.utilisateurRecepteur.id = :idReceiver)
                   OR (m.utilisateurEmetteur.id = :idReceiver AND m.utilisateurRecepteur.id = :idSender)
                ORDER BY m.temps
                """;

            TypedQuery<Message> query = em.createQuery(jpql, Message.class);
            query.setParameter("idSender", idSender);
            query.setParameter("idReceiver", idReceiver);

            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
