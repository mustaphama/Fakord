package controleur;

import dao.UtilisateurDAO;
import dao.UtilisateurJDBCDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.Utilisateur;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurJDBCDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pseudo = req.getParameter("pseudo");
        String mdp = req.getParameter("mdp");

        try {
            Utilisateur user = utilisateurDAO.checkLogin(pseudo, mdp);
            if (user != null) {
                String token = JwtManager.createJWT();
                resp.setContentType("application/json");
                resp.getWriter().write("{\"token\": \"" + token + "\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Pseudo ou mot de passe invalide");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erreur serveur : " + e.getMessage());
        }
    }
}


