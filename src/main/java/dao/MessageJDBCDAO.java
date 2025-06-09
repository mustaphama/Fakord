package dao;

import metier.Message;
import metier.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageJDBCDAO implements MessageDAO{
    private final DbConnectionManager dbManager = DbConnectionManager.getInstance();

    @Override
    public boolean sendPrivateMessage(int idSender, int idReceiver, String message) {
        String insertMessage = "INSERT INTO Message (contenu, temps) VALUES (?, CURRENT_TIMESTAMP) RETURNING id";
        String insertEcrit = "INSERT INTO Ecrit (idUtilisateurEmetteur, idMessage, idUtilisateurRecepteur) VALUES (?, ?, ?)";

        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(insertMessage)) {
                stmt1.setString(1, message);
                ResultSet rs = stmt1.executeQuery();
                if (rs.next()) {
                    int idMessage = rs.getInt("id");

                    try (PreparedStatement stmt2 = conn.prepareStatement(insertEcrit)) {
                        stmt2.setInt(1, idSender);
                        stmt2.setInt(2, idMessage);
                        stmt2.setInt(3, idReceiver);
                        stmt2.executeUpdate();
                    }
                    conn.commit();
                    return true;
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public List<Message> getMessagesPrivee(int idSender, int idReceiver) throws SQLException{
        String query = """
        SELECT m.id, m.contenu, m.temps, e.idUtilisateurEmetteur, e.idUtilisateurRecepteur
        FROM Message m
        JOIN Ecrit e ON m.id = e.idMessage
        WHERE (e.idUtilisateurEmetteur = ? AND e.idUtilisateurRecepteur = ?)
           OR (e.idUtilisateurEmetteur = ? AND e.idUtilisateurRecepteur = ?)
        ORDER BY m.temps
        """;

        List<Message> messages = new ArrayList<>();
        try (
                Connection conn = DbConnectionManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, idSender);
            stmt.setInt(2, idReceiver);
            stmt.setInt(3, idReceiver);
            stmt.setInt(4, idSender);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setId(rs.getInt("id"));
                m.setContenu(rs.getString("contenu"));
                m.setTemps(rs.getTimestamp("temps").toLocalDateTime());
                Utilisateur Emetteur = new Utilisateur();
                Emetteur.setId(rs.getInt("idUtilisateurEmetteur"));
                m.setUtilisateurEmetteur(Emetteur);
                Utilisateur Recepteur = new Utilisateur();
                Recepteur.setId(rs.getInt("idUtilisateurRecepteur"));
                m.setUtilisateurRecepteur(Recepteur);
                messages.add(m);
            }
        }
        return messages;
    }
}
