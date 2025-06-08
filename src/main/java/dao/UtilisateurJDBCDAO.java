package dao;

import metier.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilisateurJDBCDAO implements UtilisateurDAO {
    private final DbConnectionManager dbManager = DbConnectionManager.getInstance();

    public Utilisateur findByPseudo(String pseudo) throws SQLException {
        String query = "SELECT * FROM Utilisateur WHERE pseudo = ?";
        try (
                Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, pseudo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setPseudo(rs.getString("pseudo"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setMdp(rs.getString("mdp"));
                return user;
            }
        }
        return null;
    }

    public Utilisateur checkLogin(String pseudo, String motDePasse) throws SQLException {
        String query = "SELECT * FROM Utilisateur WHERE pseudo = ? AND mdp = encode(digest(?, 'sha256'), 'hex')";
        try (
                Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, pseudo);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setPseudo(rs.getString("pseudo"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setMdp(rs.getString("mdp")); // haché
                return user;
            }
        }
        return null;
    }

    public boolean createUser(Utilisateur user) throws SQLException {
        String query = "INSERT INTO Utilisateur (pseudo, nom, prenom, mdp) VALUES (?, ?, ?, encode(digest(?, 'sha256'), 'hex'))";

        try (
                Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, user.getPseudo());
            stmt.setString(2, user.getNom());
            stmt.setString(3, user.getPrenom());
            stmt.setString(4, user.getMdp()); // mot de passe en clair (hashé dans la requête)
            int rows = stmt.executeUpdate();
            return rows == 1;
        }
    }


}
