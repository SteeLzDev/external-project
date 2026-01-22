<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%
String include = (String) request.getAttribute("include");
if (include != null) {
%>
    <jsp:forward page="<%= include %>">
        <jsp:param name="jsp_precompile" value="true"/>
    </jsp:forward>      
<% 
} else {
    String result = (String) request.getAttribute("result");
    Integer fileCount = (Integer) request.getAttribute("fileCount");
    Integer errorCount = (Integer) request.getAttribute("errorCount");

    out.print("<pre>");
    out.print(result);
    out.print("</pre>");
    out.print("<BR/>OK"); 
    out.print("<BR/>" + ApplicationResourcesHelper.getMessage("mensagem.compilacao.total", null) + " " + fileCount + " (" + errorCount + ")");
}
%>
