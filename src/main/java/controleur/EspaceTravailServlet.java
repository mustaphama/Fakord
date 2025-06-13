package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.*;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import metier.EspaceTravail;
import metier.Utilisateur;
import org.example.fakord.ApplicationListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/protected/espaceTravail/*")
public class EspaceTravailServlet extends HttpServlet {

    EntityManager em = ApplicationListener.getEmf().createEntityManager();
    private final EspaceTravailDAO espaceDAO = new EspaceTravailJPADAO(em);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            Claims claims = (Claims) req.getAttribute("claims");
            if (claims == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
            Integer idConnecte = claims.get("id", Integer.class);
            Utilisateur currentUser = new Utilisateur();
            currentUser.setId(idConnecte);
            if (currentUser == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Map<String, String> data = mapper.readValue(req.getInputStream(), Map.class);
            System.out.println(data);
            String nom = data.get("nom");
            System.out.println(nom);
            String description = data.get("description");
            System.out.println(description);

            EspaceTravail espace = new EspaceTravail(nom, description);
            espaceDAO.create(espace);
            espace = em.find(EspaceTravail.class, nom);
            currentUser = em.find(Utilisateur.class, currentUser.getId());
            espaceDAO.addAdmin(currentUser, espace);

            mapper.writeValue(resp.getWriter(), Map.of("success", true));
        } catch (Exception e) {
            resp.setStatus(500);
            mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");
        System.out.println("ouaiii");
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                //HttpSession session = req.getSession();
                //Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
                System.out.println("jessaie");
                Claims claims = (Claims) req.getAttribute("claims");
                if (claims == null) {
                    System.out.println("claims null");
                    resp.sendRedirect(req.getContextPath() + "/login.jsp");
                    return;
                }
                System.out.println("jsp");
                System.out.println(claims.get("id"));
                // Par ex. récupérer tous les utilisateurs sauf celui connecté
                Integer idConnecte = claims.get("id", Integer.class);
                Utilisateur currentUser = new Utilisateur();
                currentUser.setId(idConnecte);
                if (currentUser == null) {
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // Récupérer tous les espaces de l'utilisateur
                List<EspaceTravail> espaces = espaceDAO.findByUser(currentUser.getId());

                // Convertir en format JSON
                List<Map<String, Object>> espacesJson = espaces.stream()
                        .map(espace -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("nom", espace.getNom());
                            map.put("description", espace.getDescription());
                            map.put("isAdmin", espaceDAO.isAdmin(currentUser.getId(), espace.getNom()));
                            return map;
                        })
                        .collect(Collectors.toList());


                mapper.writeValue(resp.getWriter(), Map.of(
                        "success", true,
                        "espaces", espacesJson
                ));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            ));
        }
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String nomEspace = req.getParameter("nom");

        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        Integer idConnecte = claims.get("id", Integer.class);
        Utilisateur currentUser = new Utilisateur();
        currentUser.setId(idConnecte);

        try {
            if (!espaceDAO.isAdmin(currentUser.getId(), nomEspace)) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", "Non autorisé"));
                return;
            }
            String nom = req.getParameter("nom");
            boolean success = espaceDAO.deleteByNom(nom);
            resp.setContentType("application/json");
            if (success) {
                mapper.writeValue(resp.getWriter(), Map.of("success", true, "message", "Espace supprimé avec succès"));
            } else {
                mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", "Échec de la suppression"));
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", e.getMessage()));
        }
    }

}

