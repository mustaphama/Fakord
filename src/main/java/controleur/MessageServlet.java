package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.*;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.Ecrit;
import metier.Message;
import org.example.fakord.ApplicationListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/protected/message")
public class MessageServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = claims.get("id", Integer.class);
        String toParam = req.getParameter("to");
        if (toParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre manquant");
            return;
        }

        int otherId = Integer.parseInt(toParam);
        EntityManager em = ApplicationListener.getEmf().createEntityManager();
        MessageJPADAO dao = new MessageJPADAO(em);
        List<Map<String, Object>> jsonMessages = new ArrayList<>();

        for (Message m : dao.getMessagesPrivee(userId, otherId)) {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", m.getId());
            messageData.put("contenu", m.getContenu());
            messageData.put("temps", m.getTemps().toString()); // LocalDateTime to String

            // On récupère le premier auteur (émission unique par message)
            Ecrit ecrit = m.getEcrits().iterator().next();
            Map<String, Object> auteurData = new HashMap<>();
            auteurData.put("id", ecrit.getUtilisateurEmetteur().getId());
            auteurData.put("pseudo", ecrit.getUtilisateurEmetteur().getPseudo());

            messageData.put("utilisateurEmetteur", auteurData);
            jsonMessages.add(messageData);
        }
        resp.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(resp.getWriter(), jsonMessages);
        em.close();

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = claims.get("id", Integer.class);
        Map<String, Object> data = mapper.readValue(req.getReader(), Map.class);
        int receiverId = (Integer) data.get("to");
        String contenu = (String) data.get("contenu");

        EntityManager em = ApplicationListener.getEmf().createEntityManager();
        MessageJPADAO dao = new MessageJPADAO(em);

        try {
            dao.sendPrivateMessage(userId, receiverId, contenu);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de l'envoi");
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

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        int messageId = Integer.parseInt(idParam);
        int userId = claims.get("id", Integer.class);

        EntityManager em = ApplicationListener.getEmf().createEntityManager();
        MessageJPADAO dao = new MessageJPADAO(em);

        try {
            boolean success = dao.supprimerMessage(messageId, userId);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Pas autorisé à supprimer ce message");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la suppression");
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
        Map<String, Object> data = new ObjectMapper().readValue(req.getReader(), Map.class);
        Integer messageId = (Integer) data.get("id");
        String nouveauContenu = (String) data.get("contenu");

        if (messageId == null || nouveauContenu == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Champs manquants");
            return;
        }

        EntityManager em = ApplicationListener.getEmf().createEntityManager();
        MessageJPADAO dao = new MessageJPADAO(em);

        try {
            boolean success = dao.modifierMessage(messageId, userId, nouveauContenu);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Pas autorisé à modifier ce message");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la modification");
        } finally {
            em.close();
        }
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req,resp);
        }else {
            super.service(req,resp);
        }
    }
    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = claims.get("id", Integer.class);
        Map<String, Object> data = mapper.readValue(req.getReader(), Map.class);

        Integer messageId = (Integer) data.get("id");
        String nouveauContenu = (String) data.get("contenu");

        if (messageId == null || nouveauContenu == null || nouveauContenu.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
            return;
        }

        EntityManager em = ApplicationListener.getEmf().createEntityManager();
        MessageJPADAO dao = new MessageJPADAO(em);

        try {
            boolean success = dao.modifierContenuMessage(messageId, userId, nouveauContenu.trim());
            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Message modifié");
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous ne pouvez modifier que vos messages");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la modification");
        } finally {
            em.close();
        }

    }
}
