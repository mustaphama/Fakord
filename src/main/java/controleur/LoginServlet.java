package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UtilisateurDAO;
import dao.UtilisateurJDBCDAO;
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

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurJPADAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        BufferedReader reader = req.getReader();
        Utilisateur loginInput = mapper.readValue(reader, Utilisateur.class);
        if (loginInput.getPseudo() == null || loginInput.getMdp() == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Champs manquants");
            return;
        }
        try {
            Utilisateur utilisateur = utilisateurDAO.checkLogin(loginInput.getPseudo(), loginInput.getMdp());

            if (utilisateur != null) {
                String token = JwtManager.createJWT(utilisateur.getId(), utilisateur.getPseudo());
                Map<String, Object> json = new HashMap<>();
                json.put("message", "Connexion réussie");
                json.put("token", token);
                mapper.writeValue(resp.getWriter(), json);
            } else {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Identifiants incorrects");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Pour le débogage
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }
}

