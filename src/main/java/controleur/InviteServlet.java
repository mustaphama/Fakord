package controleur;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.*;
import jakarta.persistence.EntityManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import dao.*;
import metier.*;
import org.example.fakord.ApplicationListener;

@WebServlet(name = "InviteServlet", value = "/api/invite/*")
public class InviteServlet extends HttpServlet {
    private InviteEspaceDAO inviteEspaceDAO;
    private InviteCanalDAO inviteCanalDAO;

    @Override
    public void init() throws ServletException {
        EntityManager em = ApplicationListener.getEmf().createEntityManager();
        this.inviteEspaceDAO = new InviteEspaceJPADAO(em);
        this.inviteCanalDAO = new InviteCanalJPADAO(em);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else if (pathInfo.equals("/workspace")) {
            generateWorkspaceInvite(request, response);
        } else if (pathInfo.equals("/channel")) {
            generateChannelInvite(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void generateWorkspaceInvite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Parse JSON input
            String json = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            ObjectMapper mapper = new ObjectMapper();
            var input = mapper.readValue(json,WorkspaceInviteRequest.class);

            // Get current user from session
            HttpSession session = request.getSession();
            Utilisateur currentUser = (Utilisateur) session.getAttribute("user");

            // Create EspaceTravail reference with just the name
            EspaceTravail espace = new EspaceTravail();
            espace.setNom(input.workspaceName);

            InviteEspace invite = new InviteEspace();
            invite.setUtilisateurInvitant(currentUser);
            invite.setNomEspace(espace);
            invite.setEmailInvite("");
            invite.setStatut("pending");

            inviteEspaceDAO.create(invite);

            // Generate invite link
            String inviteLink = request.getRequestURL().toString()
                    .replace("/api/invite/workspace", "/join?type=workspace");

            mapper.writeValue(response.getWriter(), Map.of(
                    "success", true,
                    "inviteLink", inviteLink
            ));
        } catch (Exception e) {
            new ObjectMapper().writeValue(response.getWriter(), Map.of(
                    "success", false,
                    "message", "Error generating invite: " + e.getMessage()
            ));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void generateChannelInvite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Parse JSON input
            String json = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            ObjectMapper mapper = new ObjectMapper();
            var input = mapper.readValue(json,ChannelInviteRequest.class);

            // Get current user from session
            HttpSession session = request.getSession();
            Utilisateur currentUser = (Utilisateur) session.getAttribute("user");

            // Create Canal reference with just the name
            Canal canal = new Canal();
            canal.setNom(input.channelName);

            // Create invitation
            InviteCanal invite = new InviteCanal();
            invite.setUtilisateurInvitant(currentUser);
            invite.setNomCanal(canal); // Set the Canal entity
            invite.setEmailInvite(""); // Will be filled when invite is accepted
            invite.setStatut("pending");

            inviteCanalDAO.create(invite);

            // Generate invite link
            String inviteLink = request.getRequestURL().toString()
                    .replace("/api/invite/channel", "/join?type=channel");

            // Return success with link
            mapper.writeValue(response.getWriter(),  Map.of(
                    "success", true,
                    "inviteLink", inviteLink
            ));
        } catch (Exception e) {
            new ObjectMapper().writeValue(response.getWriter(),  Map.of(
                    "success", false,
                    "message", "Error generating invite: " + e.getMessage()
            ));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Inner classes for request parsing
    private static class WorkspaceInviteRequest {
        public String workspaceName;
    }

    private static class ChannelInviteRequest {
        public String workspaceName;
        public String channelName;
    }
}