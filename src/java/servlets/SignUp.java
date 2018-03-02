package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Human;
import com.google.gson.Gson;
import dao.DAOFactory;
import dao.HumanDao;
import forms.SignUpForm;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "SignUp", urlPatterns = {"/register"})
public class SignUp extends HttpServlet {
    public static final String CONF_DAO_FACTORY = "daofactory";
    public static final String ATT_SESSION_USER = "sessionHuman";
    
    private HumanDao humanDao;
    
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
    }
	
    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
        response.setContentType("application/json");
        
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        Properties data = gson.fromJson(reader, Properties.class);
        
        SignUpForm form = new SignUpForm(humanDao);
		
        form.SignUpHuman(data, request);
		
        Human hu = (Human)request.getSession(false).getAttribute(ATT_SESSION_USER);
        PrintWriter out = response.getWriter();
        if (hu != null){
            out.println("{ \"status\": \"success\"}");
        } else {
            Map<String, String> errors = (Map<String, String>)request.getSession(false).getAttribute("errors");
            out.print("{\"status\": \"error\"\n"
                      + "\"message\": \"");
            String message = "";
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                message += entry.getValue()+"\n";
            }
            out.println(message+"\"}");

        }
    }
}