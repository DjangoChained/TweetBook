package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Human;
import com.google.gson.Gson;
import config.BCrypt;
import dao.DAOFactory;
import dao.HumanDao;
import forms.ConnectionForm;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "Connection", urlPatterns = {"/user/login"})
public class Connection extends HttpServlet {
    public static final String ATT_SESSION_USER = "sessionHuman";
    public static final String CONF_DAO_FACTORY = "daofactory";
    
    private HumanDao humanDao;
    
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        
        //Human hu = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        ConnectionForm form = new ConnectionForm();

        form.connectHuman(data, request);

        HttpSession session = request.getSession();

        /**
         * Si aucune erreur de validation n'a eu lieu, alors ajout du bean
         * Human à la session, sinon suppression du bean de la session.
         */
        session.setAttribute( ATT_SESSION_USER, null );
        PrintWriter out = response.getWriter();
        if ( form.getErrors().isEmpty() ) {
            String email = data.getProperty("email");
            String pwd = data.getProperty("password");
            if ( email != null && email.trim().length() != 0 && pwd != null && pwd.trim().length() != 0) {
                Human testHuman = humanDao.get(email);
                if(testHuman != null){
                    if(BCrypt.checkpw(pwd, testHuman.getPassword())){
                        session.setAttribute( ATT_SESSION_USER, testHuman );
                        out.println("{\"status\": \"success\"}");
                    } else {
                        out.print("{\"status\": \"error\"\n"
                      + "\"message\": \"Email ou mot de passe incorrect.\"\n}");
                    }
                } else {
                    out.print("{\"status\": \"error\"\n"
                      + "\"message\": \"Cet email n'appartient à aucun utilisateur.\"\n}");
                }
            }
        } else {
            out.print("{\"status\": \"error\"\n"
                      + "\"message\": \"");
            String message = "";
            for (Map.Entry<String, String> entry : form.getErrors().entrySet()) {
                message += entry.getValue()+"\n";
            }
            out.println(message+"\"}");
        }         
    }
}