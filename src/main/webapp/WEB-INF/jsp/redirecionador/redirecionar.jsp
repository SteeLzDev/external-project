<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.UsuarioHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%
response.setHeader("redirect", "true");

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
response.setContentType("text/html");
response.setCharacterEncoding("UTF-8");

boolean doGet = request.getAttribute("doGet") != null && (Boolean) request.getAttribute("doGet");
String url;
if (request.getAttribute("url64") != null) {
    url = TextHelper.decode64(request.getAttribute("url64").toString());
} else {
    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
    url = "/v3/exibirMensagem?acao=exibirMsgSessao";
}
%>
<html>
<head>
<%@ include file="../../../geral/head.jsp"%>
</head>
<body>
<script>
<% if (doGet) { %>
window.location.replace('<%=TextHelper.forJavaScriptBlock(url)%>');
<% } else { %>
postData(encodeURI('<%=TextHelper.forJavaScriptBlock(url)%>'));
<% } %>
</script>
</body>
</html>