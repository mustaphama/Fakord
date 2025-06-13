package dao;

import metier.Canal;
import metier.EspaceTravail;
import metier.Utilisateur;

import java.util.List;

public interface CanalDAO {

    List<Canal> findPublicByEspace(String nomEspace);

    void create(Canal canal);

    boolean deleteByNom(String nom);
}