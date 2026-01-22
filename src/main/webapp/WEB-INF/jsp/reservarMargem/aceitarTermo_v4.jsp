<%--
* <p>Title: aceitarTermo</p>
* <p>Description: Página de aceite do termo de adesão.</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: rodrigo.rosa $
* $Revision: 26667 $
* $Date: 2019-05-06 11:40:18 -0300 (seg, 06 mai 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/function-lib" prefix="fl"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
String linkRet = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "linkRet")) ? JspHelper.verificaVarQryStr(request, "linkRet") : (String) request.getAttribute("linkRet");
String link = null;

if(!TextHelper.isNull(linkRet)) {
    link = SynchronizerToken.updateTokenInURL(linkRet.replace('$','?').replace('|','&').replace('(','='), request) + "&linkRet=" + linkRet;
} else {
    link = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
}
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="title">
<hl:message key="rotulo.termo.adesao.singular"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
<div id="header-print">
	<% if ("v4".equals(versaoLeiaute)) { %>
		<img src="../img/econsig-logo.svg">
	<% } else { %>
		<img src="../img/logo_sistema_v5.png">
	<%} %>
	<p id="date-time-print"></p>
</div>
<form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="form1">
  <%= SynchronizerToken.generateHtmlToken(request) %>
  <hl:htmlinput type="hidden" name="acao" value="<%= TextHelper.forHtmlAttribute(request.getAttribute("proximaOperacao")) %>" />
  <c:if test="${rseCodigo != null}">
    <hl:htmlinput type="hidden" name="RSE_CODIGO" value="<%= TextHelper.forHtmlAttribute(request.getAttribute("rseCodigo")) %>" />
  </c:if>
  <c:forEach items="${requestParams}" var="paramName">   
     <input type="hidden" name="${fl:forHtmlAttribute(paramName)}" value="${fl:forHtmlAttribute(param[paramName])}"/>
  </c:forEach>
      <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="imprimir();"><hl:message key="rotulo.botao.imprimir"/></a>
      </div>
      <div class="row">
        <div class="col-sm-12">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.termo.adesao.singular"/></h2>
            </div>
            <div class="card-body">
              <p>${textoTermoAdesao}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="btn-action">  
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=link%>')" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
          <a class="btn btn-primary" id="btnSalvar" href="#no-back" onClick="f0.submit(); return false;"><hl:message key="rotulo.botao.confirmar"/></a>      
      </div>
</form>
<% if ("v4".equals(versaoLeiaute)) { %>
  <div id="footer-print">
	<img src="../img/footer-logo.png">
  </div>
<% } else { %>
	<div id="footer-print">
		<img src="../img/footer-logo-v5.png">
	</div>
<%} %>
</c:set>
<c:set var="javascript">
	<style>
	  @media print {
		*{
			padding: 0;
			margin: 0;
			color: #000 !important;
		}
		body{color: #000 !important}
	    #menuAcessibilidade {display: none;}
        #header-print img{width: 10%;}
        #footer-print {position: absolute; bottom: 0;}    
	  }
	  @page{
		margin: 0.5cm;
	  }	
	</style>
<SCRIPT language="javascript" id="MainScript">
  f0 = document.forms[0];
  function injectDate(){
		const dateTimePrint = document.querySelector('#date-time-print');
		const printDate = new Date();
		printDate.toLocaleString("pt-br");
		dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);	
  }
  function imprimir() {
    $("#BOTOES").css({"visibility":"hidden"});
    $("#container").css({"visibility":"hidden"});
    document.body.className = "PRINT";
    injectDate();
    window.print();
    document.body.className = "";
    $("#BOTOES").css({"visibility":"visible"});
    $("#container").css({"visibility":"visible"});
  }
</SCRIPT>
<script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
