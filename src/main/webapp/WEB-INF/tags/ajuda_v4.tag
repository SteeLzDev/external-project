<%@ tag description="Overall Page template" pageEncoding="UTF-8" %>
<%@ tag import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ tag import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ tag import="com.zetra.econsig.values.CodedValues" %>
<%@ tag import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ tag import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ tag import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ tag import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ attribute name="javascript" fragment="true" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/function-lib" prefix="fl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<!DOCTYPE html>
<html lang="<%=LocaleHelper.getLocale()%>">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title><%= (!TextHelper.isNull(request.getAttribute("tituloPagina"))) ? request.getAttribute("tituloPagina") : JspHelper.getNomeSistema(responsavel)%></title>
    <%-- Bootstrap core CSS --%>
    <link href="<c:url value='/node_modules/bootstrap/dist/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css">
    <%-- Font Monteserrat (titulos) e Roboto (corpo) CSS --%>
    <link href="<c:url value='/css/fonts/Google/family_Montserrat.css'/>" rel="stylesheet" type="text/css">
    <%-- Arquivos Especializads CSS --%>
    <% if ("v5".equals(versaoLeiaute)) { %>
    <link href="<c:url value='/css/welcome_v5.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v5.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v5_client.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/econsig_v5.css'/>" rel="stylesheet" type="text/css">
    <% } else if ("v6".equals(versaoLeiaute)) { %>
    <link href="<c:url value='/css/welcome_v6.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v6.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v6_client.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/econsig_v6.css'/>" rel="stylesheet" type="text/css">
    <% } else { %>
    <link href="<c:url value='/css/econsig.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/welcome_v4.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v4.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v4_client.css'/>" rel="stylesheet" type="text/css">
    <% } %>
  </head>
<body class="page-popup">
<section id="no-back" class="container-fluid">
  	<div class="popup">
   	    <jsp:doBody/>
    </div>
</section>
<script src="<c:url value='/node_modules/jquery/dist/jquery.min.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/jquery-impromptu.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/jquery-nostrum.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/intl/numeral.min.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/intl/languages.min.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/mensagens.jsp'/>?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>"></script>
<script src="<c:url value='/js/SimpleAjaxUploader.min.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/UploadAnexo.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/sidebar.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/countdown.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/validalogin.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/scripts_2810.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/validacoes.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/validaform.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/xbdhtml.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/econsig.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/jscrollpane.js'/>?<hl:message key='release.tag'/>"></script>
<script type="text/javascript">
//<![CDATA[
navigator.sayswho= (function(){
    var ua= navigator.userAgent, tem, 
    M= ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*([\d\.]+)/i) || [];
    if(/trident/i.test(M[1])){
        tem=  /\brv[ :]+(\d+(\.\d+)?)/g.exec(ua) || [];
        return 'IE '+(tem[1] || '');
    }
    M= M[2]? [M[1], M[2]]:[navigator.appName, navigator.appVersion, '-?'];
    if((tem= ua.match(/version\/([\.\d]+)/i))!= null) M[2]= tem[1];
    return M[0];
})();

window.history.forward();
if (navigator.sayswho.toLowerCase() == "msie") {
  window.location.hash="no-back";
  window.onhashchange = function(){ window.location.hash = "no-back"; }
} else {
  window.history.pushState({}, "", "#no-back");
  window.onhashchange = function(){ window.location.hash = "no-back"; }
}

numeral.language(locale());

if ("function" == typeof startCountdown) {
    startCountdown(<%=session.getMaxInactiveInterval()%>);
    continueCountdown(<%=responsavel.isSessaoValida()%>);
}
//]]>
</script>
<jsp:invoke fragment="javascript"/>
<%-- Placed at the end of the document so the pages load faster --%>
<script src="<c:url value='/node_modules/@popperjs/core/dist/umd/popper.min.js'/>"></script>
<script src="<c:url value='/node_modules/bootstrap/dist/js/bootstrap.min.js'/>"></script>
<%-- Menu de acessibilidade para eMAG 3.1 - Criterio de Sucesso 2.2 --%>
<noscript><hl:message key="mensagem.navegador.sem.javascript"/></noscript>
</body>
</html>