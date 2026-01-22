<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
boolean sucesso = request.getAttribute("sucesso") != null && (Boolean) request.getAttribute("sucesso");
boolean recarregarMenu = request.getAttribute("recarregarMenu") != null && (Boolean) request.getAttribute("recarregarMenu");
boolean recarregarDashboard = request.getAttribute("recarregarDashboard") != null && (Boolean) request.getAttribute("recarregarDashboard");

if (sucesso) {
    out.print("{\"success\":\"true\"}");
}
if (recarregarMenu) {
    %><hl:sidebarv4/><%  
}
if (recarregarDashboard) {
    %><hl:dashBoardv4 acessoInicial="${modoIntegrarFolha == 'acessoInicial'}" /><%  
}
%>
