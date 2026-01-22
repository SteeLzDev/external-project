<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.TermoAdesaoServicoTO"%>
<%@ page import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  String acao = (String) request.getAttribute("acao");
  String csaCodigo = (String) request.getAttribute("csaCodigo");
  String csaNome = (String) request.getAttribute("csaNome");
  String titulo = (String) request.getAttribute("titulo");
  String svcCodigo = (String) request.getAttribute("svcCodigo");
  String svcDescricao = (String) request.getAttribute("svcDescricao");
  String terAdsTexto = (String) request.getAttribute("terAdsTexto");
  String voltar = (String) request.getAttribute("voltar");
  String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="title">
<%=titulo %>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
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
  <div class="card">
    <div class="card-header hasIcon pl-3">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(titulo)%></h2>
    </div>
    <div class="main-content">
    <div class="row">
    </div>
      <div class="row">
        <div class="card-body">
          <p><%=new Markdown4jProcessorExtended().process(TextHelper.forHtmlContent(terAdsTexto)).toString()%></p>
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action d-print-none">
    <a class="btn btn-primary" HREF="#no-back" onClick="imprimir();"><hl:message key="rotulo.botao.imprimir"/></a>
    <a class="btn btn-outline-danger" HREF="#no-back" onClick="postData('<%=voltar%>'); return false;">
    <hl:message key="rotulo.botao.cancelar"/></A>
  </DIV>
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
		  	color: #OOO !important;
		  	font-size: 11px;
		  }
		  #menuAcessibilidade {display: none;}
		  #header-print img{width: 10%;}
		  #footer-print {position: absolute; bottom: 0 !important;}
		}
		@page{margin: 0.5cm;}
	</style>
<script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
<script>
	function injectDate(){
		const dateTimePrint = document.querySelector('#date-time-print');
		const printDate = new Date();
		printDate.toLocaleString("pt-br");
		dateTimePrint.innerText = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);
	}
</script>
<SCRIPT language="javascript" id="MainScript">
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

</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>