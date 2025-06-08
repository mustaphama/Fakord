package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UtilisateurDAO;
import dao.UtilisateurJDBCDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.Utilisateur;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurJDBCDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Lire les paramètres (formulaire ou JSON)
        String pseudo = req.getParameter("pseudo");
        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String mdp = req.getParameter("mdp");

        resp.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = new HashMap<>();

        if (pseudo == null || nom == null || prenom == null || mdp == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            json.put("error", "Champs requis manquants");
            mapper.writeValue(resp.getWriter(), json);
            return;
        }

        Utilisateur user = new Utilisateur(pseudo, nom, prenom, mdp);

        try {
            boolean created = utilisateurDAO.createUser(user);
            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                json.put("message", "Utilisateur créé avec succès");
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                json.put("error", "Échec lors de la création");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate key")) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                json.put("error", "Pseudo déjà utilisé");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                json.put("error", "Erreur serveur : " + e.getMessage());
            }
        }

        mapper.writeValue(resp.getWriter(), json);
    }
}

