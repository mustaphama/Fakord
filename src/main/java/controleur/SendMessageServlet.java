package controleur;

import dao.MessageJDBCDAO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/protected/sendMessage")
public class SendMessageServlet extends HttpServlet {
    private final MessageJDBCDAO dao = new MessageJDBCDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        int senderId = claims.get("id", Integer.class);
        int to = Integer.parseInt(req.getParameter("to"));
        String pseudo = req.getParameter("pseudo");
        String contenu = req.getParameter("contenu");

        try {
            dao.sendPrivateMessage(senderId, to, contenu);
            resp.sendRedirect("conversation?id=" + to + "&pseudo=" + pseudo);
        } catch (Exception e) {
            resp.sendError(500, "Erreur : " + e.getMessage());
        }
    }
}

