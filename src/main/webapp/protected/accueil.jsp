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
<h1>Espaces de travail</h1>
<p><a href="createEspace.html">➕ Créer espace</a></p>
<ul id="espace-list">Loading...</ul>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        const list = document.getElementById("espace-list");
        fetch("espaceTravail/")
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    afficherEspaces(data.espaces);
                } else {
                    document.getElementById("espace-list").textContent = "Erreur : " + data.message;
                }
    })});
    function afficherEspaces(espaces) {
        const container = document.getElementById("espace-list");
        container.innerHTML = "";

        espaces.forEach(espace => {
            const div = document.createElement("div");

            const lien = document.createElement("a");
            lien.href = `espaceTravail.html?nom=\${encodeURIComponent(espace.nom)}`;
            lien.textContent = espace.nom + " - " + espace.description;
            lien.style.marginRight = "1em";

            div.appendChild(lien);

            if (espace.isAdmin) {
                const btn = document.createElement("button");
                btn.textContent = "🗑️ Supprimer";
                btn.className = "btn-message";
                btn.onclick = () => deleteEspace(espace.nom);
                div.appendChild(btn);
            }

            container.appendChild(div);
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
                    location.reload();
                } else {
                    alert("Erreur : " + data.message);
                }
            })
            .catch(error => console.error('Erreur lors de la suppression :', error));
    }
</script>
<h1>Invitations en attente</h1>
<ul id="invitations-list">Loading...</ul>

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


    function afficherEspaces(espaces) {
        const container = document.getElementById("espace-list");
        container.innerHTML = "";

        espaces.forEach(espace => {
            const div = document.createElement("div");
            div.className = "espace-item";

            const lien = document.createElement("a");
            lien.href = `espaceTravail.html?nom=\${encodeURIComponent(espace.nom)}`;
            lien.textContent = `\${espace.nom} - \${espace.description}`;
            lien.style.marginRight = "10px";

            div.appendChild(lien);

            if (espace.isAdmin) {
                const btnSuppr = document.createElement("button");
                btnSuppr.textContent = "Supprimer";
                btnSuppr.className = "btn-message";
                btnSuppr.onclick = () => deleteEspace(espace.nom);
                div.appendChild(btnSuppr);
            }

            container.appendChild(div);
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
