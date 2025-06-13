package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import metier.Canal;

import java.util.List;


    public class CanalJPADAO implements CanalDAO {
        private final EntityManager em;

        public CanalJPADAO(EntityManager em) {
            this.em = em;
        }

        @Override
        public List<Canal> findPublicByEspace(String nomEspace) {
            String jpql = "SELECT c FROM Canal c WHERE c.espaceTravail.nom = :nomEspace AND c.statut = 'Public'";
            TypedQuery<Canal> query = em.createQuery(jpql, Canal.class);
            query.setParameter("nomEspace", nomEspace);
            return query.getResultList();
        }

        @Override
        public void create(Canal canal) {
            em.getTransaction().begin();
            em.persist(canal);
            em.getTransaction().commit();
        }

        @Override
        public boolean deleteByNom(String nom) {
            return false;
        }

        public Canal findByName(String nomCanal) {
            try {
                String jpql = "SELECT c FROM Canal c WHERE c.nom = :nom";
                return em.createQuery(jpql, Canal.class)
                        .setParameter("nom", nomCanal)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }
    }

