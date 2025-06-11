<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion</title>
</head>
<body>
<h2>Connexion</h2>

<form id="loginForm">
    <label>Pseudo : <input type="text" name="pseudo" required></label><br><br>
    <label>Mot de passe : <input type="password" name="mdp" required></label><br><br>
    <button type="submit">Se connecter</button>
</form>
<p id="error" style="color:red;">pseudo ou mot de passe incorrecte</p>
<p>Pas encore inscrit ? <a href="register.jsp">Créer un compte</a></p>
<script>
    document.getElementById('loginForm').addEventListener('submit', async function (e) {
        e.preventDefault();
        const pseudo = sanitizeInput(e.target.pseudo.value);
        const mdp = e.target.mdp.value;
        if(!pseudo) {
            document.getElementById("error").textContent = "Pseudo invalide";
            return;
        }
        const data = {
            pseudo: pseudo,
            mdp: mdp
        };

        const response = await fetch("/Fakord/api/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const resData = await response.json();
            localStorage.setItem("token", resData.token);
            window.location.href = "accueil.html";
        } else {
            const errMsg = await response.text();
            document.getElementById("error").textContent = errMsg;
        }
    });
    function sanitizeInput(input) {
        return input.trim()
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;");
    }

    function escapeHtml(unsafe) {
        return unsafe?.toString()
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
</script>
</body>
</html>
