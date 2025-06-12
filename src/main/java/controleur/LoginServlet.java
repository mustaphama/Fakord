package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UtilisateurDAO;
import dao.UtilisateurJPADAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.Utilisateur;

import java.io.BufferedReader;
import java.io.IOException;
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
                Cookie cookie = new Cookie("token", token);
                cookie.setPath("/");
                resp.addCookie(cookie);

                Map<String, Object> json = new HashMap<>();
                json.put("message", "Connexion réussie");
                json.put("token", token);
                mapper.writeValue(resp.getWriter(), json);
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp?error=1");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Pour le débogage
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }
}

