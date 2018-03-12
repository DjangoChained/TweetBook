package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Le filtre qui permet de restreindre l'accès à certaines pages aux utilisateurs connectés
 */
public class RestrictionFilter implements Filter {

    /**
     * Autorise l'accès aux utilisateurs connectés et renvoie une erreur HTTP 403 aux autres
     * @param req la requête HTTP
     * @param resp la réponse HTTP
     * @param chain la chaîne de filtres
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter( ServletRequest req, ServletResponse resp, FilterChain chain ) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("sessionHuman") != null;
        if (loggedIn) {
            chain.doFilter( request, response );
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}