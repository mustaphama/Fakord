package dao;

import metier.Utilisateur;

import java.sql.SQLException;

public interface UtilisateurDAO {

    Utilisateur findByPseudo(String pseudo) throws SQLException;

    Utilisateur checkLogin(String pseudo, String motDePasse) throws SQLException;

    boolean createUser(Utilisateur user) throws SQLException;


}
