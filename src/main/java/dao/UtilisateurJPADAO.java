package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import metier.Utilisateur;

import java.util.List;

import static org.example.fakord.ApplicationListener.getEmf;

public class UtilisateurJPADAO implements UtilisateurDAO {

    @Override
    public Utilisateur findByPseudo(String pseudo) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.pseudo = :pseudo", Utilisateur.class);
            query.setParameter("pseudo", pseudo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Utilisateur checkLogin(String pseudo, String motDePasse) {
        EntityManager em = getEmf().createEntityManager();
        try {
            Query query = em.createNativeQuery(
                    "SELECT * FROM utilisateur WHERE pseudo = :pseudo AND mdp = encode(digest(:mdp, 'sha256'), 'hex')",
                    Utilisateur.class);
            query.setParameter("pseudo", pseudo);
            query.setParameter("mdp", motDePasse);
            return (Utilisateur) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean createUser(Utilisateur utilisateur) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            // Hachage SHA-256 directement via PostgreSQL (comme dans JDBC)
            String hashedPwd = (String) em.createNativeQuery("SELECT encode(digest(?1, 'sha256'), 'hex')")
                    .setParameter(1, utilisateur.getMdp())
                    .getSingleResult();
            utilisateur.setMdp(hashedPwd);

            em.persist(utilisateur);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Utilisateur> findAll(int id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.id <> :id", Utilisateur.class);
            query.setParameter("id", id);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}