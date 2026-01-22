<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String voltar = (String) request.getAttribute("destinoBotaoVoltar");
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="title">
  <hl:message key="rotulo.extrato.margem.titulo"/>
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
      <div class="row d-print-none">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <button id="acoes" class="btn btn-primary" type="submit"  onClick="imprimir()"><hl:message key="rotulo.botao.imprimir"/></button>
          </div>
        </div>
      </div>
    </div>
    <!-- Dados iniciais do servidor -->
    <div class="card">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title"><hl:message key="rotulo.consultar.margem.resultado"/></h2>
      </div>
      <div class="card-body">
           <dl class="row data-list">
           <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
           <hl:detalharServidorv4 name="servidor" margem="lstMargens" scope="request"/>
          <%-- Fim dos dados da ADE --%>
          </dl>   
      </div>
    </div>
    
    <!-- Extratos -->
    <hl:extratoMargemV4 margem="lstMargens" extrato="lstExtrato" scope="request"/>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="return postData('<%=TextHelper.forJavaScriptAttribute(voltar)%>');"><hl:message key="rotulo.botao.voltar"/></A>
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
		padding: 0;
		margin: 0;
	}
	body{color: #000 !important}
	table th:last-child {display: none;}
    table td:last-child {display: none;}
	tfoot{display: none;}
    #menuAcessibilidade {display: none;}
    #footer-print {position: absolute !important;}
    #header-print img{width: 10%;}
    .table thead tr th, .table tbody tr td {
	    font-size: 12px;
	    line-height: 1.25;
	    padding-top: 0;
	    padding-bottom: 0;
	    padding-left: .25rem;
	    padding-right: .25rem;
	    border: 1px solid #000 !important;
	    color: #000 !important;
     }
  }
	  @page{
		margin: 1cm !important;
	  }
	
	</style>
    <script type="text/JavaScript">
	    function doIt(opt, ade) {
	        if (opt == 'hi') {
	          postData('../v3/consultarExtratoMargem?acao=detalharConsignacao&ADE_CODIGO=' + ade + '&<%=SynchronizerToken.generateToken4URL(request)%>');
	        }
	    }
		f0 = document.forms[0];
		
		function injectDate(){
			const dateTimePrint = document.querySelector('#date-time-print');
			const printDate = new Date();
			printDate.toLocaleString("pt-br");
			dateTimePrint.innerText = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);
		}
		
		function imprimir() {
			injectDate();
	    	window.print();
		}
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>