package controleur;

import dao.UtilisateurDAO;
import dao.UtilisateurJDBCDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metier.Utilisateur;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurJDBCDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        if ("logout".equals(action)) {
            Cookie cookie = new Cookie("token", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            resp.addCookie(cookie);
            resp.sendRedirect("login.jsp");
        } else {
            // Rediriger vers le formulaire de login
            resp.sendRedirect("login.jsp");
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pseudo = req.getParameter("pseudo");
        String mdp = req.getParameter("mdp");

        try {
            Utilisateur user = utilisateurDAO.checkLogin(pseudo, mdp);
            if (user != null) {
                String token = JwtManager.createJWT(user.getId(), user.getPseudo());
                Cookie cookie = new Cookie("token", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                resp.addCookie(cookie);

                resp.sendRedirect("protected/accueil");
            } else {
                resp.sendRedirect("login.jsp?error=1");
            }
        } catch (Exception e) {
            resp.sendRedirect("login.jsp?error=2");
        }
    }
}


