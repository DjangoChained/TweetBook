package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Human;
import forms.ConnectionForm;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "Connection", urlPatterns = {"/Connection"})
public class Connection extends HttpServlet {
    public static final String ATT_USER         = "human";
    public static final String ATT_FORM         = "form";
    public static final String ATT_SESSION_USER = "sessionHuman";
    public static final String VIEW              = "/WEB-INF/connexion.jsp";

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        /* Affichage de la page de connexion */
        this.getServletContext().getRequestDispatcher(VIEW ).forward( request, response );
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        /* Préparation de l'objet formulaire */
        ConnectionForm form = new ConnectionForm();

        /* Traitement de la requête et récupération du bean en résultant */
        Human human = form.connectHuman( request );

        /* Récupération de la session depuis la requête */
        HttpSession session = request.getSession();

        /**
         * Si aucune erreur de validation n'a eu lieu, alors ajout du bean
         * Human à la session, sinon suppression du bean de la session.
         */
        if ( form.getErrors().isEmpty() ) {
            session.setAttribute( ATT_SESSION_USER, human );
        } else {
            session.setAttribute( ATT_SESSION_USER, null );
        }

        /* Stockage du formulaire et du bean dans l'objet request */
        request.setAttribute( ATT_FORM, form );
        request.setAttribute( ATT_USER, human );

        this.getServletContext().getRequestDispatcher(VIEW ).forward( request, response );
    }
}