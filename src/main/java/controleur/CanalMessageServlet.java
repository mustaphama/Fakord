package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.MessageJPADAO;
import dao.CanalJPADAO;
import io.jsonwebtoken.Claims;
import metier.*;
import org.example.fakord.ApplicationListener;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/protected/messageCanal/*")
public class CanalMessageServlet extends HttpServlet {

    private EntityManager em;
    private MessageJPADAO messageDAO;
    private CanalJPADAO canalDAO;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        em = ApplicationListener.getEmf().createEntityManager();
        messageDAO = new MessageJPADAO(em);
        canalDAO = new CanalJPADAO(em);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // URL attendue : /protected/messageCanal/{nomCanal}
        String pathInfo = req.getPathInfo(); // /nomCanal
        resp.setContentType("application/json;charset=UTF-8");
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        Integer idConnecte = claims.get("id", Integer.class);
        Utilisateur user = new Utilisateur();
        user.setId(idConnecte);
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(),
                    new ApiResponse(false, "Nom du canal manquant dans l'URL"));
            return;
        }

        String nomCanal = pathInfo.substring(1); // enlever le "/"

        try {
            // Vérifier si canal existe
            Canal canal = canalDAO.findByName(nomCanal);
            if (canal == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(resp.getWriter(), new ApiResponse(false, "Canal introuvable"));
                return;
            }

            List<Message> messages = messageDAO.getMessagesByCanal(nomCanal);
            List<MessageDTO> dtos = messages.stream().map(m -> {
                Utilisateur auteur = m.getPublications().stream()
                        .findFirst()
                        .map(Publie::getUtilisateur) // ou p -> p.getUtilisateur()
                        .orElse(null); // ou un Utilisateur vide par défaut
                return new MessageDTO(m, auteur);
            }).collect(Collectors.toList());
            mapper.writeValue(resp.getWriter(), Map.of("success", true, "messages", dtos));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Claims claims = (Claims) req.getAttribute("claims");
        System.out.println("hello");
        if (claims == null) {
            System.out.println("claims null");
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        Integer idUtilisateur = claims.get("id", Integer.class);

        String pathInfo = req.getPathInfo(); // /Hi
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new ApiResponse(false, "Nom du canal manquant dans l'URL"));
            return;
        }
        String nomCanal = pathInfo.substring(1);
        System.out.println("trying2");
        Canal canal = canalDAO.findByName(nomCanal);
        System.out.println(nomCanal);
        if (canal == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            System.out.println("Canal introuvable");
            mapper.writeValue(resp.getWriter(), new ApiResponse(false, "Canal introuvable"));
            return;
        }

        System.out.println("trying3");
        String rawJson = new BufferedReader(new InputStreamReader(req.getInputStream()))
                .lines().collect(Collectors.joining("\n"));
        System.out.println("Corps brut reçu : " + rawJson);

        MessageRequest messageReq;
        try {
            messageReq = mapper.readValue(rawJson, MessageRequest.class);
        } catch (Exception e) {
            System.out.println("Erreur de parsing JSON: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new ApiResponse(false, "JSON invalide"));
            return;
        }



        System.out.println("simple");
        if (messageReq == null || messageReq.contenu == null || messageReq.contenu.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Contenu introuvable");
            mapper.writeValue(resp.getWriter(), new ApiResponse(false, "Contenu du message manquant"));
            return;
        }

        // Créer message lié au canal
        try {
            System.out.println(messageReq.contenu);
            messageDAO.createMessageInCanal(new Message(messageReq.contenu.trim(), LocalDateTime.now()), idUtilisateur, nomCanal);
            System.out.println("message created");
            mapper.writeValue(resp.getWriter(), new ApiResponse(true, "Message envoyé"));
        } catch (Exception e) {
            System.out.println("idk man");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Supprimer un message par id, paramètre idMessage
        resp.setContentType("application/json;charset=UTF-8");
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        Integer idConnecte = claims.get("id", Integer.class);
        Utilisateur user = new Utilisateur();
        user.setId(idConnecte);
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String idMessageStr = req.getParameter("idMessage");
        if (idMessageStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new ApiResponse(false, "Paramètre idMessage manquant"));
            return;
        }

        try {
            int idMessage = Integer.parseInt(idMessageStr);
            boolean deleted = messageDAO.supprimerMessage(idMessage, user.getId());
            if (deleted) {
                mapper.writeValue(resp.getWriter(), new ApiResponse(true, "Message supprimé"));
            } else {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                mapper.writeValue(resp.getWriter(), new ApiResponse(false, "Suppression non autorisée"));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new ApiResponse(false, "idMessage invalide"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), new ApiResponse(false, "Erreur: " + e.getMessage()));
        }
    }

    // Classe interne simple pour uniformiser les réponses JSON
    private static class ApiResponse {
        public boolean success;
        public String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
    public static class MessageRequest {
        public String contenu;
        public MessageRequest() {} // constructeur vide nécessaire à Jackson

        public String getContenu() {
            return contenu;
        }
    }

}
