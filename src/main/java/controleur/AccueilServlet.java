package controleur;

import dao.UtilisateurJDBCDAO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.Utilisateur;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/protected/accueil")
public class AccueilServlet extends HttpServlet {
    private final UtilisateurJDBCDAO utilisateurJDBCDAO = new UtilisateurJDBCDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        int currentUserId = claims.get("id", Integer.class);

        try {
            List<Utilisateur> utilisateurs = utilisateurJDBCDAO.findAll(currentUserId);
            req.setAttribute("utilisateurs", utilisateurs);
            req.getRequestDispatcher("/protected/accueil.jsp").forward(req, resp);
        } catch (SQLException e) {
            resp.sendError(500, "Erreur serveur : " + e.getMessage());
        }
    }
}

