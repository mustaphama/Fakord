package controleur;

import dao.UtilisateurDAO;
import dao.UtilisateurJPADAO;
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

    private UtilisateurDAO utilisateurDAO = new UtilisateurJPADAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        // Par ex. récupérer tous les utilisateurs sauf celui connecté
        Integer idConnecte = claims.get("id", Integer.class);
        List<Utilisateur> utilisateurs = null;
        try {
            utilisateurs = utilisateurDAO.findAll(idConnecte);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        req.setAttribute("utilisateurs", utilisateurs);
        req.getRequestDispatcher("/protected/accueil.jsp").forward(req, resp);
    }
}

