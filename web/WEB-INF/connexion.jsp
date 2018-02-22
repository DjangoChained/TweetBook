<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Connexion</title>
        <link type="text/css" rel="stylesheet" href="form.css" />
    </head>
    <body>
        <form method="post" action="login">
            <fieldset>
                <legend>Connexion</legend>
                <p>Vous pouvez vous connecter via ce formulaire.</p>

                <label for="email">Adresse email <span class="requis">*</span></label>
                <input type="email" id="email" name="email" size="20" maxlength="60" />
                <span class="erreur">${form.errors['email']}</span>
                <br />

                <label for="password">Mot de passe <span class="requis">*</span></label>
                <input type="password" id="password" name="password" value="" size="20" maxlength="20" />
                <span class="erreur">${form.errors['password']}</span>
                <br />

                <input type="submit" value="login" class="sansLabel" />
                <br />
                
                <p class="${empty form.errors ? 'succes' : 'erreur'}">${form.result}</p>
                 <c:if test="${!empty sessionScope.sessionUtilisateur}">
                    <p class="succes">Vous êtes connecté(e) avec l'adresse : ${sessionScope.sessionHuman.email}</p>
                </c:if>
            </fieldset>
        </form>
    </body>
</html>