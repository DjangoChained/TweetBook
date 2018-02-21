<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Inscription</title>
        <link type="text/css" rel="stylesheet" href="form.css" />
    </head>
    <body>
        <form method="post" action="Signup">
            <fieldset>
                <legend>Inscription</legend>
                <p>Vous pouvez vous inscrire via ce formulaire.</p>

                <label for="firstname">Prénom <span class="requis">*</span></label>
                <input type="text" id="firstname" name="firstname" size="20" maxlength="60" />
                <span class="erreur">${form.errors['firstname']}</span>
                <br />
                
                <label for="lastname">Nom de famille<span class="requis">*</span></label>
                <input type="text" id="lastname" name="lastname" size="20" maxlength="60" />
                <span class="erreur">${form.errors['lastname']}</span>
                <br />
                
                <label for="birthdate">Anniversaire<span class="requis">*</span></label>
                <input type="date" id="birthdate" name="birthdate" size="20" maxlength="60" />
                <span class="erreur">${form.errors['birthdate']}</span>
                <br />
                
                <label for="email">Adresse email <span class="requis">*</span></label>
                <input type="email" id="email" name="email" value="<c:out value="${human.email}"/>" size="20" maxlength="60" />
                <span class="erreur">${form.errors['email']}</span>
                <br />

                <label for="password">Mot de passe <span class="requis">*</span></label>
                <input type="password" id="password" name="password" value="" size="20" maxlength="20" />
                <span class="erreur">${form.errors['password']}</span>
                <br />

                <label for="confirmation">Confirmation du mot de passe <span class="requis">*</span></label>
                <input type="password" id="confirmation" name="confirmation" value="" size="20" maxlength="20" />
                <span class="erreur">${form.errors['confirmation']}</span>
                <br />

                <label for="username">Nom d'utilisateur</label>
                <input type="text" id="username" name="username" value="<c:out value="${human.username}"/>" size="20" maxlength="20" />
                <span class="erreur">${form.errors['username']}</span>
                <br />

                <input type="submit" value="Signup" class="sansLabel" />
                <br />
                
                <p class="${empty form.errors ? 'succes' : 'erreur'}">${form.result}</p>
                <p>${form.errors}</p>
            </fieldset>
        </form>
    </body>
</html>