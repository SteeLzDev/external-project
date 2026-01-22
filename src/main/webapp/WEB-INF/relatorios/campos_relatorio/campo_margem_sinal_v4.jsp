<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<% 
String obrSinalMargemPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String descSinalMargemPage = pageContext.getAttribute("descricao").toString();   
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
String desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? "true" : "false";
%>
        <hl:filtroMargemSinalTagv4 disabled="<%=TextHelper.forHtmlAttribute(desabilitado)%>" descricao="<%=TextHelper.forHtmlAttribute(descSinalMargemPage)%>" obrigatoriedade="<%=TextHelper.forHtmlAttribute(obrSinalMargemPage)%>" />
