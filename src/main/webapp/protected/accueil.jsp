<%--
  Created by IntelliJ IDEA.
  User: chabc
  Date: 09/06/2025
  Time: 13:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Accueil</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f0f2f5;
            margin: 0;
            padding: 20px;
            color: #333;
        }

        h1 {
            text-align: center;
            color: #3b5998;
            margin-bottom: 30px;
        }

        .welcome {
            text-align: center;
            margin-bottom: 30px;
        }

        .contact-list {
            max-width: 600px;
            margin: 0 auto;
            background-color: #fff;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        }

        .contact {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 15px;
            border-bottom: 1px solid #eee;
        }

        .contact:last-child {
            border-bottom: none;
        }

        .contact-name {
            font-size: 1.1em;
            color: #333;
        }

        .btn-message {
            padding: 6px 12px;
            background-color: #3b5998;
            color: white;
            border: none;
            border-radius: 20px;
            cursor: pointer;
            transition: background-color 0.3s;
            text-decoration: none;
        }

        .btn-message:hover {
            background-color: #2d4373;
        }

        .logout {
            text-align: center;
            margin-top: 30px;
        }

        .logout a {
            color: #e53935;
            text-decoration: none;
            font-weight: bold;
        }

        .logout a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<h1>Bienvenue sur Fakord 👋</h1>

<div class="welcome">
    <p>Choisissez un contact pour commencer à discuter :</p>
</div>

<div class="contact-list">
    <%
        List<metier.Utilisateur> utilisateurs = (List<metier.Utilisateur>) request.getAttribute("utilisateurs");
        Integer currentUserId = (Integer) request.getAttribute("currentUserId");
        for (metier.Utilisateur u : utilisateurs) {
            if (!u.getId().equals(currentUserId)) {
    %>
    <div class="contact">
        <div class="contact-name"><%= u.getPseudo() %></div>
        <a class="btn-message" href="conversation.html?id=<%= u.getId() %>">Discuter 💬</a>
    </div>
    <%
            }
        }
    %>
</div>
<div class="logout">
    <a href="../login.jsp">Se déconnecter</a>
</div>
</body>
</html>
