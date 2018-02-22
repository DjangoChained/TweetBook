package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Human;
import dao.DAOFactory;
import dao.HumanDao;
import forms.SignUpForm;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "SignUp", urlPatterns = {"/register"})
public class SignUp extends HttpServlet {
    public static final String CONF_DAO_FACTORY = "daofactory";
    public static final String USER_ATT = "human";
    public static final String FORM_ATT = "form";
    public static final String VIEW = "/WEB-INF/signup.jsp";
    
    private HumanDao humanDao;
    
    public void init() throws ServletException {
        /* Récupération d'une instance de notre DAO Utilisateur */
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
    }
		
    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
        /* Affichage de la page d'inscription */
        this.getServletContext().getRequestDispatcher(VIEW).forward( request, response );
    }
	
    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
        /* Préparation de l'objet formulaire */
        SignUpForm form = new SignUpForm(humanDao);
		
        /* Appel au traitement et à la validation de la requête, et récupération du bean en résultant */
        Human human = form.SignUpHuman(request);
		
        /* Stockage du formulaire et du bean dans l'objet request */
        request.setAttribute(FORM_ATT, form );
        request.setAttribute(USER_ATT, human );
		
        this.getServletContext().getRequestDispatcher(VIEW).forward( request, response );
    }
}