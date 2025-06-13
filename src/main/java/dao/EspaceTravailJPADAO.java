package dao;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import metier.*;
import metier.EspaceTravail;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EspaceTravailJPADAO implements EspaceTravailDAO {
    private final EntityManager em;

    public EspaceTravailJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public void create(EspaceTravail espace) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (espace.getNom() == null || espace.getNom().isEmpty()) {
                throw new IllegalArgumentException("Le nom de l'espace doit être défini.");
            }
            em.persist(espace);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace(); // <-- pour voir l’erreur exacte dans la console
            throw new RuntimeException("Erreur lors de la création de l'espace : " + e.getMessage(), e);
        }
    }
    @Override
    public void addAdmin(Utilisateur utilisateur, EspaceTravail espace) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Administre admin = new Administre();
            admin.setUtilisateur(utilisateur);
            admin.setNomEspace(espace);

            em.persist(admin);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erreur lors de l'ajout d'un administrateur", e);
        }
    }



    @Override
    public EspaceTravail findByName(String name) {
        try {
            return em.createQuery("SELECT e FROM EspaceTravail e WHERE e.nom = :name", EspaceTravail.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Aucun espace trouvé avec ce nom
        }
    }


    @Override
    public List<EspaceTravail> findByUser(int userId) {
        String jpql = """
        SELECT DISTINCT e 
        FROM EspaceTravail e
        WHERE EXISTS (
            SELECT 1 FROM Appartient a 
            WHERE a.nomEspace = e AND a.utilisateur.id = :userId
        )
        OR EXISTS (
            SELECT 1 FROM Administre ad 
            WHERE ad.nomEspace = e AND ad.utilisateur.id = :userId
        )
        """;
        System.out.println("test");
        TypedQuery<EspaceTravail> query = em.createQuery(jpql, EspaceTravail.class);
        query.setParameter("userId", userId);

        System.out.println(query);

        System.out.println(query.getResultList());
        return query.getResultList();
    }
    @Override
    public boolean isAdmin(int userId, String nomEspace) {
        String jpql = """
        SELECT COUNT(ad) 
        FROM Administre ad 
        WHERE ad.utilisateur.id = :userId AND ad.nomEspace.id = :nomEspace
    """;

        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("userId", userId);
        query.setParameter("nomEspace", nomEspace);

        Long count = query.getSingleResult();
        return count != null && count > 0;
    }
    @Override
    public boolean deleteByNom(String nom) {
        try {
            EspaceTravail espace = em.find(EspaceTravail.class, nom);
            if (espace != null) {
                em.getTransaction().begin();
                em.remove(espace);
                em.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        }
        return false;
    }



}