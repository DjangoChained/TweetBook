<%@page import="java.util.Random"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>TweetBook</title>
        <link rel="stylesheet" href="resources/css/bootstrap.min.css" />
        <link rel="stylesheet" href="resources/css/login.css" />
        <script src="resources/js/jquery.slim.min.js"></script>
        <script src="resources/js/popper.min.js"></script>
        <script src="resources/js/bootstrap.min.js"></script>
        <script src="resources/js/login.js"></script>
    </head>

    <body>
        <div class="row">
            <div class="col">
                <div class="jumbotron">
                    <%! String[] things = new String[] {
                        "des photos de chats", "des théories du complot",
                        "de la crème", "de J2EE", "de la DA2I", "des mèmes",
                    }; %>
                    <img src="http://via.placeholder.com/400x80?text=Logo+TweetBook" class="logo" />
                    <h2>La crème<br /><%= things[new Random().nextInt(things.length)] %><br />à portée de main.</h2>
                </div>
            </div>
            <div class="col">
                <div class="collapse show text-center">
                    <h3>Rejoignez-nous</h3>
                    <form>
                        <fieldset>
                            <input type="text" class="form-control" placeholder="Nom de famille" autocomplete="family-name" required />
                            <input type="text" class="form-control" placeholder="Prénom" autocomplete="given-name" required />
                            <input type="date" class="form-control" placeholder="Date de naissance" autocomplete="bday" required />
                            <input type="email" class="form-control" placeholder="Adresse e-mail" required />
                            <input type="text" class="form-control" placeholder="Nom d'utilisateur" autocomplete="username" required />
                            <input type="password" class="form-control" placeholder="Mot de passe" autocomplete="new-password" required />
                        </fieldset>
                        <button type="submit" class="btn btn-primary">S'inscrire</button>
                    </form><br />
                    <p>Déjà membre ? <a href="#" class="togglecollapse"><strong>Se connecter</strong></a></p>
                </div>
                <div class="collapse text-center">
                    <h3>Bienvenue</h3>
                    <form>
                        <fieldset>
                            <input type="text" class="form-control" placeholder="Nom d'utilisateur" autocomplete="username" required />
                            <input type="password" class="form-control" placeholder="Mot de passe" autocomplete="current-password" required />
                        </fieldset>
                        <button type="submit" class="btn btn-primary">Se connecter</button>
                    </form><br />
                    <p>Pas encore membre ? <a href="#" class="togglecollapse"><strong>S'inscrire</strong></a></p>
                </div>
            </div>
        </div>
    </body>
</html>
