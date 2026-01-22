<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>

<%
  Boolean ajudaPopup = (Boolean) request.getAttribute("ajudaPopup");
  String acrCodigo    =  (String) request.getAttribute("acrCodigo");
  String acrOperacao  =  (String) request.getAttribute("acrOperacao");
  String acrParametro =  (String) request.getAttribute("acrParametro");
%>

<c:set var="title">
  ${tituloPagina}
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
    <%
      AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    %>
    <div class="alert alert-warning" role="alert">
      <p class="mb-0 font-weight-bold"></p>
      <p class="mb-0 font-weight-bold"><hl:message key="ajuda.ajudaNaoCadastrada"/></p>
    </div>
   <div class="btn-action">
      <% if(ajudaPopup) { %>
            <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getCurrentHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      <% } else {%>
            <a class="btn btn-third"  href="#no-back" onclick="postData('../v3/visualizarAjuda?acao=visualizarAjuda&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.listarTodos"/></a>
      <% } %>
    <% if(responsavel.temPermissao(CodedValues.FUN_EDT_MANUAL_AJUDA_SISTEMA) && !TextHelper.isNull(request.getAttribute("acrCodigo"))) { %>
      <a class="btn btn-primary"  href="#no-back" onclick="postData('../v3/editarManualAjuda?acao=listar&ajudaPopup=<%=TextHelper.forJavaScriptAttribute(ajudaPopup)%>&acrCodigo=<%=TextHelper.forJavaScriptAttribute(acrCodigo)%>&acrOperacao=<%=TextHelper.forJavaScript(acrOperacao)%>&acrParametro=<%=TextHelper.forJavaScript(acrParametro)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">&nbsp;<hl:message key="rotulo.criar.ajuda.titulo"/>&nbsp;</a>
     <% } %>
   </div>
</c:set>
  <t:page_v4>
      <jsp:attribute name="header">${tituloPagina}</jsp:attribute>
      <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
      <jsp:body>${bodyContent}</jsp:body>
  </t:page_v4>