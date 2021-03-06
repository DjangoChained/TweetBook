/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet qui permet de déconnecter un utilisateur existant
 */
@WebServlet(name = "Logout", urlPatterns = {"/user/logout"})
public class Logout extends HttpServlet {

    /**
     * Il suffit d'acceder à /user/logout pour se déconnecter
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        /* Récupération et destruction de la session en cours */
        HttpSession session = request.getSession();
        session.invalidate();
        response.setContentType("application/json");
        response.getWriter().println("{\"status\": \"success\"}");
    }
}
