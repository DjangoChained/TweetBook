/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.Human;
import dao.DAOFactory;
import dao.HumanDao;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet qui permet de récupérer un utilisateur
 */
@WebServlet(name = "getHuman", urlPatterns = {"/user"})
public class GetHuman extends HttpServlet {
    /**
     * Le Dao qui permet de manipuler les utilisateurs
     */
    private HumanDao humanDao;
    
    /**
     * Permet d'initialiser les Dao lors de l'instanciation de la servlet
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( "daofactory" ) ).getHumanDao();
    }

    /**
     * permet de récupérer un utilisateur par son identifiant
     * Reçois au format JSON l'identifiant de l'utilisateur qu'on souhaite récupérer ("id")
     * @param request la requête HTTP
     * @param response la réponse HTTP
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        int humanId = -1;
        PrintWriter out = response.getWriter();

        try {
           humanId = Integer.parseInt(request.getParameter("id"));
        } catch(NumberFormatException e){
            out.println("{\"status\": \"error\",\"message\": \"Veuillez entrer un entier valide\"}");
            log(e.getMessage());
        }

        if (humanId != -1){
            Human human = humanDao.get(humanId);
            if(human != null){
                out.println("{" +
                            "    \"status\": \"success\",\n" +
                            "    \"user\": {\n" +
                        "        \"id\": \""+human.getId()+"\",\n" +
                        "        \"firstName\": \""+human.getFirstName()+"\",\n" +
                        "        \"lastName\": \""+human.getLastName()+"\"\n" +
                        "    }" +
                        "}");
            } else {
                out.println("{\"status\": \"error\",\"message\": \"Il n'existe aucun utilisateur avec cet identifiant\"}");
            }
        } 
    }
}
