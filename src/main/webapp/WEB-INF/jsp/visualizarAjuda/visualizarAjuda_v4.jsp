<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@page import="com.zetra.econsig.persistence.entity.Ajuda"%>
<%@page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    Ajuda ajuda = (Ajuda) request.getAttribute("ajuda");

    String acrCodigo    =  (String) request.getAttribute("acrCodigo");
    String acrOperacao  =  (String) request.getAttribute("acrOperacao");
    String acrParametro =  (String) request.getAttribute("acrParametro");
    Boolean ajudaPopup = (Boolean) request.getAttribute("ajudaPopup");

    String rotuloBotaoEditar = (String) request.getAttribute("rotuloBotaoEditar");
%>

<c:set var="title">
  <hl:message key="rotulo.ajuda"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <div class="card">
     <div class="card-header hasIcon pl-3">
       <h3 class="card-header-title"><hl:message key="rotulo.ajuda"/></h3>
     </div>
     <div class="card-body">
      <h4 class="message-title"><%=TextHelper.forHtmlContent(ajuda.getAjuTitulo())%></h4>
      <p class="mt-2"><%=ajuda.getAjuTexto()%></p>
     </div>
  </div>
  <div class="btn-action">
     <% if(ajudaPopup) {%>
      <a class="btn btn-outline-danger"  href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getCurrentHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
     <% } else { %>
      <a class="btn btn-outline-danger"  href="#no-back" onclick="postData('../v3/visualizarAjuda?acao=visualizarAjuda&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
     <% } %>
    <% if(responsavel.temPermissao(CodedValues.FUN_EDT_MANUAL_AJUDA_SISTEMA) && !TextHelper.isNull(request.getAttribute("acrCodigo"))) { %>
      <a class="btn btn-primary"  href="#no-back" onclick="postData('../v3/editarManualAjuda?acao=listar&ajudaPopup=<%=TextHelper.forJavaScriptAttribute(ajudaPopup)%>&acrCodigo=<%=TextHelper.forJavaScriptAttribute(acrCodigo)%>&acrOperacao=<%=TextHelper.forJavaScript(acrOperacao)%>&acrParametro=<%=TextHelper.forJavaScript(acrParametro)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">&nbsp;<%=TextHelper.forHtmlContent(rotuloBotaoEditar)%>&nbsp;</a>
     <% } %>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    f0 = document.forms[0];
  </script>
</c:set>
  <t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
  </t:page_v4>
