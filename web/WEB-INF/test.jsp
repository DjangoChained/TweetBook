%<-- 
    Document   : test
    Created on : 2 févr. 2018, 09:41:47
    Author     : pierant
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <c:forEach var="human"  items="${requestScope['humans']}" >
   <p value="<c:out value='${human.firstname}'/>"><c:out value="${human.firstname}"/></p>
</c:forEach>
    </body>
</html>
