package dao;

import metier.*;

import java.util.List;

public interface EspaceTravailDAO {
    void create(EspaceTravail espace);

    void addAdmin(Utilisateur utilisateur, EspaceTravail espace);

    EspaceTravail findByName(String name);
    List<EspaceTravail> findByUser(int userId);
    boolean isAdmin(int userId, String nomEspace);

    boolean deleteByNom(String nom);
}