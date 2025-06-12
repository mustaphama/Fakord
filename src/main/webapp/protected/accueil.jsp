<%--
  Created by IntelliJ IDEA.
  User: chabc
  Date: 09/06/2025
  Time: 13:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="metier.Utilisateur" %>
<%
    List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs");
    if (utilisateurs == null) {
        utilisateurs = new java.util.ArrayList<>();
    }
%>
<html>
<head>
    <title>Bienvenue sur Fakord</title>
</head>
<body>
<h1>Messagerie Privée</h1>
<p>Liste des contactes</p>
<ul>
    <% for (Utilisateur u : utilisateurs) { %>
    <li><a href="conversation?id=<%= u.getId() %>&pseudo=<%= u.getPseudo() %>"><%= u.getPseudo() %></a></li>
    <% } %>
</ul>
<form action="../login.jsp" method="get">
    <input type="hidden" name="action" value="logout">
    <button type="submit">Se déconnecter</button>
</form>
</body>
</html>
