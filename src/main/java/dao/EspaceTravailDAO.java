package dao;

import metier.*;

import java.util.List;

public interface EspaceTravailDAO {
    void create(EspaceTravail espace);
    EspaceTravail findByName(String name);
    List<EspaceTravail> findByUser(int userId);
}