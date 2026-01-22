<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="java.util.Date"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String simboloPagina = request.getAttribute("termoAdesaoBeneficio") != null ? "i-beneficios" : "i-sistema";
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="imageHeader">
    <use xlink:href="#<%=simboloPagina%>"></use>
</c:set>
<c:set var="title">
   <%= request.getAttribute("tituloPagina") %>
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
    <div class="row">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button id="imprimir" aria-expanded="false" class="btn btn-primary d-print-none" type="submit" onclick="imprimir()"><hl:message key="rotulo.botao.imprimir"/></button>
        </div>
      </div>
    </div>
   <div class="mt-4 mb-4 pb-4">
   		${msgTermoAdesao}
   </div>
   
   <div class="btn-action">
 		<a class="btn btn-outline-danger mt-2" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
   </div>
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
		table.tabela_adesao, table.tabela_adesao th, table.tabela_adesao td {
			border: 1px solid black;
			margin: 5px 0px 5px 0px;
		    padding: 0px 5px 0px 5px;;
		}
		@media print {
		  *{
		  	margin: 0;
		  	padding: 0;
		  	color: #000 !important;
		  	font-size: 11px;
		  }
		  #menuAcessibilidade {display: none;}
		  #header-print{position: absolute; top: 0 !important;}
		  #header-print img{width: 10%;}
		  #footer-print {position: absolute; bottom: 0 !important;}
		}
		@page{
			margin: 0.5cm;
		}
	</style>
<script>
	function injectDate(){
		const dateTimePrint = document.querySelector('#date-time-print');
		const printDate = new Date();
		printDate.toLocaleString("pt-br");
		dateTimePrint.innerText = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);
	}
</script>
<script language="javascript" id="MainScript">
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
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>