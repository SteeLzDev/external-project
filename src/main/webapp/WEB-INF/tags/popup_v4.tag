<%@ tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@ tag import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ tag import="com.zetra.econsig.values.CodedValues"%>
<%@ tag import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ tag import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ tag import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ tag import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ attribute name="javascript" fragment="true"%>
<%@ attribute name="style" fragment="true"%>
<%@ attribute name="pageModals" fragment="true"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/function-lib" prefix="fl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

// Habilita aviso de segurança no navegador quando algum conteúdo carregado não for aceito pelas regras abaixo
response.addHeader("Content-Security-Policy-Report-Only", JspHelper.getContentSecurityPolicyHeader(request));
%>
<!DOCTYPE html>
<html lang="<%=LocaleHelper.getLocale()%>" xml:lang="<%=LocaleHelper.getLocale()%>">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title><%= (!TextHelper.isNull(request.getAttribute("tituloPagina"))) ? request.getAttribute("tituloPagina") : JspHelper.getNomeSistema(responsavel)%></title>
<%-- Bootstrap core CSS --%>
<link href="<c:url value='/node_modules/bootstrap/dist/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css">
<%-- Font Monteserrat (titulos) e Roboto (corpo) CSS --%>
<link href="<c:url value='https://fonts.googleapis.com/css?family=Montserrat:200,200i,300,300i,400,400i,500,500i,600,600i,700,700i'/>" rel="stylesheet" type="text/css">
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
<link href="<c:url value='/node_modules/jquery-ui/dist/themes/base/jquery-ui.css'/>" rel="stylesheet" type="text/css">
<link href="<c:url value='/node_modules/ekko-lightbox/dist/ekko-lightbox.css'/>" rel="stylesheet" type="text/css">
<style>
@media print {    /* for good browsers */
  .no-print, .no-print * {
    display: none !important;
  }
</style>
<jsp:invoke fragment="style" />
</head>
<body>
    <!-- Marca d'água de ambiente de teste -->
    <c:if test="<%=ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, responsavel)%>">
    <div class="watermark-ambiente-testes">
        <hl:message key="rotulo.ambiente.de.testes"/>
    </div>
    </c:if>
    <section id="no-back">
        <div class="main ml-0">
          <div class="main-content">
            <div id="mensagens"></div>
            <jsp:doBody />
          </div>
        </div>
      <jsp:invoke fragment="pageModals" />
  </section>
  <jsp:invoke fragment="javascript" />
  <%-- Placed at the end of the document so the pages load faster --%>
    <script src="<c:url value='/node_modules/@popperjs/core/dist/umd/popper.min.js'/>"></script>
    <script src="<c:url value='/node_modules/bootstrap/dist/js/bootstrap.min.js'/>"></script>
  <%-- Menu de acessibilidade para eMAG 3.1 - Criterio de Sucesso 2.2 --%>
  <noscript>
    <hl:message key="mensagem.navegador.sem.javascript" />
  </noscript>
</body>
</html>