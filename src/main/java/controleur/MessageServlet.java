package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.MessageJDBCDAO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.Message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MessageServlet extends HttpServlet {
    private final MessageJDBCDAO messageJDBCDAO = new MessageJDBCDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        int idSender = claims.get("id", Integer.class);
        int idReceiver = Integer.parseInt(req.getParameter("to"));
        String message = req.getParameter("contenu");

        boolean recu = false;
        try {
            recu = messageJDBCDAO.sendPrivateMessage(idSender, idReceiver, message);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("Erreur lors de l'envoi : " + e.getMessage());
            return;
        }
        if (recu) {
            resp.setStatus(201);
            resp.getWriter().write("Message envoyé !");
        }else {
            resp.setStatus(400);
            resp.getWriter().write("Échec de l'envoi");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        int userId = claims.get("id", Integer.class);

        String idOther = req.getParameter("to");
        if(idOther == null){
            resp.setStatus(400);
            resp.getWriter().write("Paramèttre id manquant");
            return;
        }

        int idOtherUser = Integer.parseInt(idOther);
        try {
            List<Message> messages = messageJDBCDAO.getMessagesPrivee(userId,idOtherUser);
            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getWriter(), messages);
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("Erreur : " + e.getMessage());
        }

    }
}
