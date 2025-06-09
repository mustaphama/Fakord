<%--
  Created by IntelliJ IDEA.
  User: chabc
  Date: 09/06/2025
  Time: 13:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="metier.Message" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    List<Message> messages = (List<Message>) request.getAttribute("messages");
    int destinataireId = (Integer) request.getAttribute("destinataireId");
    int currentUserId = (Integer) request.getAttribute("currentUserId");
    String destinatairePseudo = (String) request.getAttribute("destinatairePseudo");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
%>

<h2>Conversation</h2>
<a href="accueil">⬅ Retour</a>

<div>
    <% for (Message m : messages) {
        String auteur = m.getUtilisateurEmetteurId() == currentUserId ? "moi" : destinatairePseudo; // pseudo envoyé par la servlet
    %>
    <p><%= m.getTemps().format(formatter) %> - <%= auteur %> : <%= m.getContenu() %></p>
    <% } %>
</div>

<form method="post" action="sendMessage">
    <input type="hidden" name="to" value="<%= destinataireId %>">
    <input type="hidden" name="pseudo" value="<%= destinatairePseudo %>">
    <input type="text" name="contenu" placeholder="Votre message" required>
    <button type="submit">Envoyer</button>
</form>

