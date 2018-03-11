/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.ActivityVisibility;
import beans.Human;
import beans.Post;
import beans.TextPost;
import dao.DAOFactory;
import dao.FriendshipActivityDao;
import dao.HumanDao;
import dao.HumanDao;
import dao.TextPostDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 *
 */
@WebServlet(name = "Test", urlPatterns = {"/Test"})
public class Test extends HttpServlet {

    /**
     *
     */
    public static final String CONF_DAO_FACTORY = "daofactory";

    /**
     *
     */
    public static final String ATT_HUMAN         = "human";

    /**
     *
     */
    public static final String VUE              = "/WEB-INF/test.jsp";

    private HumanDao humanDao;
    private TextPostDao textPostDao;
    private FriendshipActivityDao friendshipDao;

    /**
     *
     * @throws ServletException
     */
    public void init() throws ServletException {
        this.humanDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getHumanDao();
        this.textPostDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getTextPostDao();
        this.friendshipDao = ( (DAOFactory) getServletContext().getAttribute( CONF_DAO_FACTORY ) ).getFriendshipActivityDao();
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void service ( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        log("entré dans test");
        System.out.println("entré dans test");
        /*Human human = new Human();
        human.setId(3);
        human.setFirstName("Hervé");
        human.setLastName("Cantineau");
        human.setBirthDate("20/05/1994");
        human.setEmail("ok@ok.fr");
        human.setUsername("MrC");
        human.setPassword("");
        humanDao.update(human);*/
        
        /*ArrayList<Human> humans = humanDao.getAll();
        request.setAttribute("humans", humans);*/
        
        /*TextPost post = new TextPost();
        post.setContent("salut");
        java.util.Date ourJavaDateObject = new java.util.Date(Calendar.getInstance().getTime().getTime());
        post.setDate(ourJavaDateObject);
        post.setId_human(2);
        textPost.create(post);*/
        
        //ArrayList<Post> posts = textPostDao.getAll();
        /*Human human = humanDao.get(10);
        System.out.println(human.getBirthDate());
        
        this.getServletContext().getRequestDispatcher( VUE ).forward( request, response );*/
        PrintWriter out = response.getWriter();
        
        //out.println(friendshipDao.getByFriends(2, 1));
        //TextPost test = textPostDao.get(1);
        out.println("entré dans test");
        Human human = new Human();
        human.setFirstName("Hervé");
        human.setLastName("Cantineau");
        human.setBirthDate("1994-05-05");
        human.setEmail("canti@macon.fr");
        human.setUsername("MrC");
        human.setPassword("sdfqsdmlfkjqsdml");
        human.setVisibility(ActivityVisibility.authoronly);
        humanDao.create(human);
    }
}
