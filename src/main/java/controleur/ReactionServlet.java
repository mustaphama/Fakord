package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ReagitJPADAO;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.fakord.ApplicationListener;

import java.io.IOException;
import java.util.Map;

@WebServlet("/protected/reaction")
public class ReactionServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = claims.get("id", Integer.class);
        Map<String, Object> data = mapper.readValue(req.getReader(), Map.class);
        int messageId = (Integer) data.get("messageId");
        String reaction = (String) data.get("reaction");

        EntityManager em = ApplicationListener.getEmf().createEntityManager();
        ReagitJPADAO dao = new ReagitJPADAO(em);

        try {
            em.getTransaction().begin();
            dao.ajouterReaction(userId, messageId, reaction);
            em.getTransaction().commit();
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur d'ajout de réaction");
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = claims.get("id", Integer.class);
        Map<String, Object> data = mapper.readValue(req.getReader(), Map.class);
        int messageId = (Integer) data.get("messageId");
        String newReaction = (String) data.get("reaction");

        EntityManager em = ApplicationListener.getEmf().createEntityManager();
        ReagitJPADAO dao = new ReagitJPADAO(em);

        try {
            em.getTransaction().begin();
            dao.modifierReaction(userId, messageId, newReaction);
            em.getTransaction().commit();
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur de modification");
        } finally {
            em.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = claims.get("id", Integer.class);
        int messageId = Integer.parseInt(req.getParameter("messageId"));

        EntityManager em = ApplicationListener.getEmf().createEntityManager();
        ReagitJPADAO dao = new ReagitJPADAO(em);

        try {
            em.getTransaction().begin();
            dao.supprimerReaction(userId, messageId);
            em.getTransaction().commit();
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur de suppression");
        } finally {
            em.close();
        }
    }
}
