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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@WebServlet("/protected/userInfo")
public class userInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        Integer userId = claims.get("id", Integer.class);
        // Implémentez la logique pour vérifier si l'utilisateur est admin
        boolean isAdmin = checkIfAdmin(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("isAdmin", isAdmin);
        response.put("userId", userId);

        resp.setContentType("application/json");
        new ObjectMapper().writeValue(resp.getWriter(), response);
    }

    private boolean checkIfAdmin(Integer userId) {
        // Implémentez votre logique pour vérifier les droits admin
        // Par exemple, vérifier dans une table Administrateur
        return false; // À adapter
    }
}
