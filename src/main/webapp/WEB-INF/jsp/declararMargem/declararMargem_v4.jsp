<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t"      tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"     uri="/html-lib" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String boletoText = (String) request.getAttribute("boletoText");
String voltar = (String) request.getAttribute("destinoBotaoVoltar");
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>

<c:set var="title">
  <hl:message key="rotulo.declaracao.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
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
  <div class="page-title">
    <div class="row">
      <div class="col-sm-12 col-md-12">
        <div class="float-end">
          <div class="btn-action">
            <a class="btn btn-primary" href="#no-back" onClick="imprimir();"><hl:message key="rotulo.botao.imprimir"/></a>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="card">
    <div class="card-header" id="tituloSecao">
      <h2 class="card-header-title"><hl:message key="rotulo.declaracao.margem.titulo"/></h2>
    </div>
    <div class="card-body" id="textoDeclaracao">
      <%=(String)( boletoText )%> <!-- DESENV-3879 -->
    </div>
  </div>
  <div class="btn-action">
   <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(voltar)%>')">
     <hl:message key="rotulo.botao.voltar"/>
   </a>
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
		@media print {
		  *{
		  	margin: 0;
		  	padding: 0;
		  }
          body{color: #000 !important}
          #tituloSecao {display: none;}
		  #menuAcessibilidade {display: none;}
		  #date-time-print {margin-top: 10px;}
		  #footer-print {position: absolute; bottom: 0;}
		  #header-print img {width: 10%;}
		  table thead tr th, table tbody tr td {
		    font-size: 12px;
		    padding: .5rem;
		    border: 1px solid #000 !important;
	     }
		}
		@page{margin: 0.3cm;}
	</style>
	
  <script language="JavaScript" type="text/JavaScript">  
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
  <script>
	function injectDate(){
		const dateTimePrint = document.querySelector('#date-time-print');
		const printDate = new Date();
		printDate.toLocaleString("pt-br");
		dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);	
	}
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
