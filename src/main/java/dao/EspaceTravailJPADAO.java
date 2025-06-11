package dao;

import metier.EspaceTravail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class EspaceTravailJPADAO implements EspaceTravailDAO {
    private final EntityManager em;

    public EspaceTravailJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public void create(EspaceTravail espace) {
        em.persist(espace);
    }

    @Override
    public EspaceTravail findByName(String name) {
        return null;
    }

    @Override
    public List<EspaceTravail> findByUser(int userId) {
        return List.of();
    }

}