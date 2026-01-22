<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/function-lib" prefix="fl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
int tempo = (int) request.getAttribute("tempo");
%>
<c:set var="bodyContent">
<form method="post" action="../v3/autenticarUsuarioCertificadoDigital?acao=iniciar">
<input type="hidden" id="tempoAtual" name="tempoAtual" value="<%=TextHelper.forJavaScriptBlock(tempo)%>">
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">

window.onload = reLoad;

var time = <%=TextHelper.forJavaScriptBlock(tempo)%>;
var f0 = document.forms[0]; 
function reLoad() {
 if (time <= 4) {
   setTimeout("refresh()", 15000); // faz o refresh a cada 15 segundos.
 } else {
   refresh(); // ultimo refresh apos tempo total de 1 minuto.
 }
}
function refresh() {
  f0.submit();
}
</script>
</c:set>
<t:empty_v4>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>