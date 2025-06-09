package controleur;

import dao.MessageJDBCDAO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.Message;

import java.io.IOException;
import java.util.List;

@WebServlet("/protected/conversation")
public class ConversationServlet extends HttpServlet {
    private final MessageJDBCDAO dao = new MessageJDBCDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        int currentUserId = claims.get("id", Integer.class);
        if (req.getParameter("id") == null) {
            resp.sendError(500, "id inconnu");
        }
        int otherId = Integer.parseInt(req.getParameter("id"));
        String otherPseudo = req.getParameter("pseudo");
        if (otherPseudo == null) {
            resp.sendError(500, "pseudo non fournie");
        }

        try {
            List<Message> messages = dao.getMessagesPrivee(currentUserId, otherId);
            req.setAttribute("messages", messages);
            req.setAttribute("destinataireId", otherId);
            req.setAttribute("destinatairePseudo", otherPseudo);
            req.setAttribute("currentUserId", currentUserId);
            req.getRequestDispatcher("conversation.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(500, "Erreur : " + e.getMessage());
        }
    }
}

