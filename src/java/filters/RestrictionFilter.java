package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(servletNames = {"getHuman", "getPost", "Logout", "Settings", "UpdatePassword", "Wall"})
public class RestrictionFilter implements Filter {
    public static final String ATT_SESSION_USER = "sessionHuman";

    @Override
    public void doFilter( ServletRequest req, ServletResponse resp, FilterChain chain ) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        
        if(request.getHeader("X-Session") != null)
            request = new HeaderChangerServletWrapper(request, "Cookie", "JSESSIONID="+request.getHeader("X-Session")+"; httpOnly");

        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute(ATT_SESSION_USER) != null;
        if (loggedIn) {
            chain.doFilter( request, response );
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}