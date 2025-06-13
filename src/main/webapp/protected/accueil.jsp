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
            line-height: 1.6;
        }

        h1, h2 {
            color: #3b5998;
            margin-bottom: 20px;
        }

        h1 {
            text-align: center;
            margin-bottom: 30px;
        }

        .welcome {
            text-align: center;
            margin-bottom: 30px;
        }

        .contact-list, .espace-container, .invitations-container {
            max-width: 800px;
            margin: 0 auto 30px;
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

        .btn-message, button {
            padding: 8px 16px;
            background-color: #3b5998;
            color: white;
            border: none;
            border-radius: 20px;
            cursor: pointer;
            transition: background-color 0.3s;
            text-decoration: none;
            font-size: 0.9em;
        }

        .btn-message:hover, button:hover {
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

        #espace-list, #invitations-list {
            list-style-type: none;
            padding: 0;
            margin: 0;
        }

        .espace-item, .invitation-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 12px 0;
            border-bottom: 1px solid #eee;
        }

        .espace-item:last-child, .invitation-item:last-child {
            border-bottom: none;
        }

        .espace-item a, .invitation-item a {
            color: #3b5998;
            text-decoration: none;
            margin-right: 15px;
        }

        .espace-item a:hover, .invitation-item a:hover {
            text-decoration: underline;
        }

        #admin-section {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin: 20px auto;
            max-width: 800px;
        }

        #invite-form {
            display: flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }

        #invite-form select, #invite-form input[type="email"] {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1em;
        }

        #invite-form button {
            padding: 8px 16px;
        }

        .action-buttons {
            display: flex;
            gap: 10px;
        }

        .btn-danger {
            background-color: #e53935;
        }

        .btn-danger:hover {
            background-color: #c62828;
        }

        .btn-accept {
            background-color: #43a047;
        }

        .btn-accept:hover {
            background-color: #2e7d32;
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

<div class="espace-container">
    <h1>Espaces de travail</h1>
    <p><a href="createEspace.html" class="btn-message">➕ Créer espace</a></p>
    <ul id="espace-list">Loading...</ul>
</div>

<div class="invitations-container">
    <h1>Invitations en attente</h1>
    <ul id="invitations-list">Loading...</ul>
</div>

<!-- Section Admin (seulement visible si admin) -->
<div id="admin-section" style="display: none;">
    <h2>Inviter un utilisateur</h2>
    <form id="invite-form">
        <select id="espace-select" required>
            <option value="">Choisir un espace</option>
        </select>
        <input type="email" id="email-invite" placeholder="Email de l'invité" required>
        <button type="submit">Envoyer l'invitation</button>
    </form>
</div>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        // Vérifier si l'utilisateur est admin
        fetch("../protected/userInfo")
            .then(res => res.json())
            .then(user => {
                if (user.isAdmin) {
                    document.getElementById("admin-section").style.display = "block";
                    loadAdminEspaces();
                }
            });

        loadEspaces();
        loadInvitations();
    });

    function loadEspaces() {
        fetch("espaceTravail/")
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    afficherEspaces(data.espaces);
                } else {
                    document.getElementById("espace-list").textContent = "Erreur : " + data.message;
                }
            });
    }

    function loadAdminEspaces() {
        fetch("espaceTravail/admin")
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    const select = document.getElementById("espace-select");
                    data.espaces.forEach(espace => {
                        const option = document.createElement("option");
                        option.value = espace.nom;
                        option.textContent = espace.nom;
                        select.appendChild(option);
                    });
                }
            });
    }

    function loadInvitations() {
        fetch("invitationEspace/")
            .then(res => res.json())
            .then(data => {
                const container = document.getElementById("invitations-list");
                container.innerHTML = "";

                if (data.success && data.invitations.length > 0) {
                    data.invitations.forEach(invitation => {
                        const item = document.createElement("li");
                        item.className = "invitation-item";

                        const text = document.createElement("span");
                        text.textContent = `Invitation pour l'espace: \${invitation.espaceNom}`;

                        const buttons = document.createElement("div");
                        buttons.className = "action-buttons";

                        const acceptBtn = document.createElement("button");
                        acceptBtn.className = "btn-message btn-accept";
                        acceptBtn.textContent = "Accepter";
                        acceptBtn.onclick = () => respondToInvitation(invitation.id, true);

                        const declineBtn = document.createElement("button");
                        declineBtn.className = "btn-message btn-danger";
                        declineBtn.textContent = "Refuser";
                        declineBtn.onclick = () => respondToInvitation(invitation.id, false);

                        buttons.appendChild(acceptBtn);
                        buttons.appendChild(declineBtn);

                        item.appendChild(text);
                        item.appendChild(buttons);
                        container.appendChild(item);
                    });
                } else {
                    container.textContent = "Aucune invitation en attente";
                }
            });
    }

    function respondToInvitation(invitationId, accept) {
        fetch(`invitationEspace/?id=\${invitationId}&accept=\${accept}`, {
            method: 'POST'
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(accept ? "Invitation acceptée !" : "Invitation refusée.");
                    loadInvitations();
                    if (accept) loadEspaces();
                } else {
                    alert("Erreur: " + data.message);
                }
            });
    }

    function afficherEspaces(espaces) {
        const container = document.getElementById("espace-list");
        container.innerHTML = "";

        if (espaces.length === 0) {
            container.textContent = "Aucun espace disponible";
            return;
        }

        espaces.forEach(espace => {
            const item = document.createElement("li");
            item.className = "espace-item";

            const lien = document.createElement("a");
            lien.href = `espaceTravail.html?nom=\${encodeURIComponent(espace.nom)}`;
            lien.textContent = `\${espace.nom} - \${espace.description}`;

            item.appendChild(lien);

            if (espace.isAdmin) {
                const btnContainer = document.createElement("div");
                btnContainer.className = "action-buttons";

                const btnSuppr = document.createElement("button");
                btnSuppr.textContent = "🗑️ Supprimer";
                btnSuppr.className = "btn-message btn-danger";
                btnSuppr.onclick = () => deleteEspace(espace.nom);

                btnContainer.appendChild(btnSuppr);
                item.appendChild(btnContainer);
            }

            container.appendChild(item);
        });
    }

    function deleteEspace(nom) {
        if (!confirm(`Voulez-vous vraiment supprimer l'espace "\${nom}" ?`)) return;

        fetch(`espaceTravail?nom=\${encodeURIComponent(nom)}`, {
            method: 'DELETE'
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("Espace supprimé !");
                    loadEspaces();
                    if (document.getElementById("admin-section").style.display === "block") {
                        loadAdminEspaces();
                    }
                } else {
                    alert("Erreur : " + data.message);
                }
            });
    }

    document.getElementById("invite-form").addEventListener("submit", e => {
        e.preventDefault();
        const espace = document.getElementById("espace-select").value;
        const email = document.getElementById("email-invite").value;

        fetch("invitationEspace/", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ espace, email })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("Invitation envoyée avec succès !");
                    document.getElementById("email-invite").value = "";
                } else {
                    alert("Erreur : " + data.message);
                }
            });
    });
</script>

<div class="logout">
    <a href="../login.jsp">Se déconnecter</a>
</div>
</body>
</html>