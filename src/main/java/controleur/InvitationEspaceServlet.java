package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.InviteEspaceJPADAO;
import dao.UtilisateurJPADAO;
import dao.EspaceTravailJPADAO;
import metier.*;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import org.example.fakord.ApplicationListener;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/protected/invitationEspace/*")
public class InvitationEspaceServlet extends HttpServlet {
    private EntityManager em;
    private InviteEspaceJPADAO invitationDAO;
    private UtilisateurJPADAO userDAO;
    private EspaceTravailJPADAO espaceDAO;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        em = ApplicationListener.getEmf().createEntityManager();
        invitationDAO = new InviteEspaceJPADAO(em);
        userDAO = new UtilisateurJPADAO();
        espaceDAO = new EspaceTravailJPADAO(em);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String email = claims.get("email", String.class);
        List<InviteEspace> invitations = invitationDAO.findPendingByEmail(email);

        List<InvitationDTO> dtos = invitations.stream().map(i -> {
            InvitationDTO dto = new InvitationDTO();
            dto.nomEspace = i.getNomEspace().getNom();
            dto.utilisateurInvitantId = i.getUtilisateurInvitant().getId();
            dto.utilisateurInvitantPseudo = i.getUtilisateurInvitant().getPseudo();
            return dto;
        }).collect(Collectors.toList());
        mapper.writeValue(resp.getWriter(), Map.of(
                "success", true,
                "invitations", dtos
        ));
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Claims claims = (Claims) req.getAttribute("claims");
        if (claims == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/accept")) {
            // Accepter une invitation
            Integer idUtilisateur = claims.get("id", Integer.class);
            String nomEspace = req.getParameter("espace");
            Integer idInvitant = Integer.parseInt(req.getParameter("invitant"));

            Utilisateur utilisateur = userDAO.findById(idUtilisateur);
            Utilisateur invitant = userDAO.findById(idInvitant);
            EspaceTravail espace = espaceDAO.findByName(nomEspace);

            if (invitationDAO.acceptInvitation(utilisateur, espace, invitant)) {
                // Ajouter l'utilisateur à l'espace
                // (Implémentez cette partie selon votre modèle Appartient)
                mapper.writeValue(resp.getWriter(), Map.of("success", true));
            } else {
                mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", "Échec de l'acceptation"));
            }
        } else {
            // Créer une nouvelle invitation
            Integer idAdmin = claims.get("id", Integer.class);
            Utilisateur admin = userDAO.findById(idAdmin);

            try {
                Map<String, String> body = mapper.readValue(req.getInputStream(), Map.class);
                String nomEspace = body.get("espace");
                String emailInvite = body.get("email");

                EspaceTravail espace = espaceDAO.findByName(nomEspace);
                if (espace == null) {
                    mapper.writeValue(resp.getWriter(),
                            Map.of("success", false, "message", "Espace introuvable"));
                    return;
                }

                // Vérifier que l'admin est bien admin de cet espace
                // (Implémentez cette vérification selon votre modèle)

                InviteEspace invitation = new InviteEspace();
                invitation.setUtilisateurInvitant(admin);
                invitation.setEmailInvite(emailInvite);
                invitation.setNomEspace(espace);
                invitation.setStatut("en_attente");

                invitationDAO.create(invitation);

                mapper.writeValue(resp.getWriter(),
                        Map.of("success", true, "message", "Invitation envoyée"));
            } catch (Exception e) {
                mapper.writeValue(resp.getWriter(),
                        Map.of("success", false, "message", "Erreur: " + e.getMessage()));
            }
        }
    }

    private static class InvitationDTO {
        public String nomEspace;
        public Integer utilisateurInvitantId;
        public String utilisateurInvitantPseudo;
    }
}