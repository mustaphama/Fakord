<%--
  Created by IntelliJ IDEA.
  User: chabc
  Date: 11/06/2025
  Time: 12:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Inscription</title>
</head>
<body>
<h2>Créer un compte</h2>

<form id="registerForm">
    <label>Pseudo : <input type="text" name="pseudo" required></label><br>
    <label>Nom : <input type="text" name="nom" required></label><br>
    <label>Prénom : <input type="text" name="prenom" required></label><br>
    <label>Mot de passe : <input type="password" name="mdp" required></label><br>
    <button type="submit">S'inscrire</button>
</form>
<p>Vous avez déjà un compte ? <a href="register.jsp">Connectez-vous</a></p>
<p id="result" style="color: red;"></p>

<script>
    document.getElementById('registerForm').addEventListener('submit', async function (e) {
        e.preventDefault();

        const form = e.target;
        const data = {
            pseudo: form.pseudo.value,
            nom: form.nom.value,
            prenom: form.prenom.value,
            mdp: form.mdp.value
        };

        const res = await fetch("./api/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data),
            credentials: "include"
        });

        const result = await res.json();
        if (res.ok) {
            alert(result.message);
            window.location.href = "login.jsp";
        } else {
            document.getElementById("result").textContent = result.error;
        }
    });
</script>
</body>
</html>

