/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author lucidiot
 */
public class HeaderChangerServletWrapper extends HttpServletRequestWrapper {
    private final String headername, headervalue;
    
    /**
     *
     * @param request
     * @param headername
     * @param headervalue
     */
    public HeaderChangerServletWrapper(HttpServletRequest request, String headername, String headervalue) {
        super(request);
        this.headername = headername;
        this.headervalue = headervalue;
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public String getHeader(String name) {
        String header = super.getHeader(name);
        return (header != null) ? header : (name.equals(headername) ? headervalue : null);
    }

    /**
     *
     * @return
     */
    @Override
    public Enumeration getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        names.add(headername);
        return Collections.enumeration(names);
    }
}
