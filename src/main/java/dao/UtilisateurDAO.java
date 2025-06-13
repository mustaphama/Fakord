package dao;

import metier.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public interface UtilisateurDAO {

    Utilisateur findByPseudo(String pseudo) throws SQLException;

    Utilisateur checkLogin(String pseudo, String motDePasse) throws SQLException;

    boolean createUser(Utilisateur user) throws SQLException;

    List<Utilisateur> findAll(int id) throws SQLException;


    Utilisateur findById(int idUtilisateur);

}
