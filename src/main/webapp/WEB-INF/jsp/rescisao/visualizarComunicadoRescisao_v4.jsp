<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, java.io.*, java.math.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> contratosRescisaoSaldoPendente = (List<TransferObject>) request.getAttribute("contratosRescisaoSaldoPendente");
String serNome = (String) request.getAttribute("ser_nome");
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="title">
   <hl:message key="rotulo.visualizar.comunicado.rescisao.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-rescisao"></use>
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
<div class="col-sm">
<div class="page-title">
  <div class="row d-print-none">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <button id="acoes" class="btn btn-primary" type="submit"  onClick="imprimir()"><hl:message key="rotulo.botao.imprimir"/></button>
      </div>
    </div>
  </div>
</div>
<form method="post" action="../v3/listarColaboradoresVerbaRescisoria?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.visualizar.comunicado.rescisao.titulo.informacao"/></h2>
    </div> 
    <div class="card-body">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><hl:message key="mensagem.informacao.visualizar.comunicado.rescisao.topo" arg0="<%=TextHelper.forHtmlAttribute(serNome)%>"/></p>
      </div>
    </div>
  </div>
  <% if (contratosRescisaoSaldoPendente != null) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.visualizar.comunicado.rescisao.resultado.pesquisa"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
            <th scope="col"><hl:message key="rotulo.visualizar.comunicado.rescisao.data.inclusao"/></th>
            <th scope="col"><hl:message key="rotulo.visualizar.comunicado.rescisao.valor.liquidado"/></th>
            <th scope="col"><hl:message key="rotulo.visualizar.comunicado.rescisao.valor.restante"/></th>
            <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
          </tr>
        </thead>
        <tbody>
           <%=JspHelper.msgRstVazio(contratosRescisaoSaldoPendente.size() == 0, 7, responsavel)%>
           <%
           Iterator<TransferObject> it = contratosRescisaoSaldoPendente.iterator();
           while (it.hasNext()) {
             CustomTransferObject contrato = (CustomTransferObject) it.next();
             String adeNumero = contrato.getAttribute(Columns.ADE_NUMERO).toString();
             String adeData = DateHelper.reformat(contrato.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
             String csaNome = (String) contrato.getAttribute(Columns.CSA_NOME_ABREV);
             String svcDescricao = (String) contrato.getAttribute(Columns.SVC_DESCRICAO);
             // Valor total liquidado
             BigDecimal vlrTotalRealizado = new BigDecimal(0.00);
             if (!TextHelper.isNull(contrato.getAttribute(Columns.PRD_VLR_REALIZADO))) {
                 vlrTotalRealizado = ((BigDecimal) contrato.getAttribute(Columns.PRD_VLR_REALIZADO));                 
             }
             String strSomaVlrRealizado = NumberHelper.format(vlrTotalRealizado.doubleValue(), NumberHelper.getLang());             
           %>
           <tr>
             <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
             <td><%=TextHelper.forHtmlContent(adeData)%></td>
             <td><%=TextHelper.forHtmlContent(strSomaVlrRealizado)%></td>
             <td><hl:message key="rotulo.visualizar.comunicado.rescisao.valor.restante.consultar"/></td>
             <td><%=TextHelper.forHtmlContent(csaNome)%></td>
           </tr>
           <% } %>
        </tbody>
      </table>
    </div>
    <div class="card-body">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><hl:message key="mensagem.informacao.visualizar.comunicado.rescisao.rodape" arg0="<%=TextHelper.forHtmlAttribute(serNome)%>"/></p>
      </div>
    </div>
  </div>
  <% } %>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</form>
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
			color: #000 !important;
		}
		body{color: #000 !important}
	    table thead tr th, table tbody tr td {
	      font-size: 9px;
	      line-height: 1;
	      padding: 0.5rem;
	      margin: 0;
	      border: 1px solid #000 !important;
	      color: #000 !important;
	    }
	    #menuAcessibilidade {display: none;}
        #header-print img{width: 10%;}  
        #footer-print {position: absolute; bottom: 0;}  
	    .opcoes-avancadas {display: none;}
	  }
	  @page{
		margin: 0.3cm;
	  }
	
	</style>
	<script type="text/JavaScript">
	    f0 = document.forms[0];
	</script>
	<script>
		function injectDate(){
			const dateTimePrint = document.querySelector('#date-time-print');
			const printDate = new Date();
			printDate.toLocaleString("pt-br");
			dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);	
		}
		
		function imprimir(){
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