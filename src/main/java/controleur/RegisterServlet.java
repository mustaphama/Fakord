package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UtilisateurDAO;
import dao.UtilisateurJPADAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.Utilisateur;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurJPADAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        BufferedReader reader = req.getReader();
        Utilisateur user = mapper.readValue(reader, Utilisateur.class);

        Map<String, Object> json = new HashMap<>();

        if (user.getPseudo() == null || user.getMdp() == null ||
                user.getNom() == null || user.getPrenom() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("error", "Champs requis manquants");
            mapper.writeValue(resp.getWriter(), json);
            return;
        }
        Utilisateur existing = null;
        try {
            existing = utilisateurDAO.findByPseudo(user.getPseudo());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (existing != null) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            json.put("error", "Pseudo déjà utilisé");
            mapper.writeValue(resp.getWriter(), json);
            return;
        }

        boolean success = false;
        try {
            success = utilisateurDAO.createUser(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (success) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            json.put("message", "Utilisateur créé avec succès");
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            json.put("error", "Erreur lors de la création");
        }
        mapper.writeValue(resp.getWriter(), json);
    }
}

