package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.*;
import dao.EspaceTravailJPADAO;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.*;
import org.example.fakord.ApplicationListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/protected/canal/*")
public class CanalServlet extends HttpServlet {
    private final EntityManager em = ApplicationListener.getEmf().createEntityManager();
    private final CanalDAO canalDAO = new CanalJPADAO(em);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nomEspace = req.getParameter("espace");
        if (nomEspace == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre 'espace' manquant");
            return;
        }

        List<Canal> canals = canalDAO.findPublicByEspace(nomEspace);
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), Map.of("success", true, "canaux", canals));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> canalData = mapper.readValue(req.getReader(), Map.class);

        String nom = canalData.get("nom");
        String statut = canalData.get("statut");
        String nomEspace = canalData.get("nomEspace");
        System.out.println(nomEspace);

        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        Integer idConnecte = claims.get("id", Integer.class);
        Utilisateur user = new Utilisateur();
        user.setId(idConnecte);

        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", "Utilisateur non connecté"));
            return;
        }

        EspaceTravailDAO espaceDAO = new EspaceTravailJPADAO(em);
        if (!espaceDAO.isAdmin(user.getId(), nomEspace)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", "Non autorisé"));
            return;
        }

        EspaceTravail espace = espaceDAO.findByName(nomEspace);
        if (espace == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", "Espace introuvable"));
            return;
        }

        Canal canal = new Canal();
        canal.setNom(nom);
        canal.setStatut(statut);
        canal.setEspaceTravail(espace);

        canalDAO.create(canal); // méthode JPADAO pour insert
        mapper.writeValue(resp.getWriter(), Map.of("success", true, "message", "Canal créé"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nom = req.getParameter("nom");

        if (nom == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre 'nom' manquant");
            return;
        }
        boolean success = canalDAO.deleteByNom(nom);
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), Map.of("success", success, "message", success ? "Canal supprimé" : "Échec de la suppression"));
    }
}


