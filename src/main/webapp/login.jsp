<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion</title>
</head>
<body>
<h2>Connexion</h2>
<form method="post" action="login">
    <label for="pseudo">Pseudo :</label>
    <input type="text" name="pseudo" placeholder="Pseudo" required><br>

    <label for="mdp">Mot de passe :</label>
    <input type="password" name="mdp" placeholder="Mot de passe" required><br>

    <button type="submit">Se connecter</button>
</form>

<p>Pas encore inscrit ? <a href="register.html">Créer un compte</a></p>
</body>
</html>
