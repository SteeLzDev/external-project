<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.dto.web.ExtratoConsolidadoServidor" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
ExtratoConsolidadoServidor extractConsolidadeoSer = (ExtratoConsolidadoServidor) request.getAttribute("extractConsolidadeoSer");

Map<String, Integer> mapAdes = new HashMap<String, Integer>();
Map<String, BigDecimal> mapSumTotalAde = new HashMap<String, BigDecimal>();
Map<Date, BigDecimal> mapSumPagoMes = new HashMap<Date, BigDecimal>();
Map<Date, BigDecimal> mapSumDevidoMes = new HashMap<Date, BigDecimal>();
BigDecimal totalPagoMes = BigDecimal.ZERO;
BigDecimal totalDevidoMes = BigDecimal.ZERO;
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="title">
  <hl:message key="rotulo.extrato.consolidado.servidor.titulo"/>
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
  <div class="main-content">
    <div class="page-title">
      <div class="row d-print-none">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <button id="acoes" class="btn btn-primary" type="submit" onClick="imprimir()"><hl:message key="rotulo.botao.imprimir"/></button>
          </div>
        </div>
      </div>
    </div>
    <div class="justify-content-center mb-4">
        <img src="../img/view.jsp?nome=logo_cse_detalhe.gif" border="0">
    </div>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.extrato.consolidado.servidor.subtitulo"/></h2>
      </div>
      <div class="card-body">
        <dl class="row data-list firefox-print-fix mt-4 mb-3">
          <dt class="col-6"><hl:message key="rotulo.servidor.extrato.consolidado.data.hora"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(extractConsolidadeoSer.getData())%></dd>
          <dt class="col-6"><hl:message key="rotulo.servidor.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(extractConsolidadeoSer.getSerNome())%></dd>
          <dt class="col-6"><hl:message key="rotulo.servidor.matricula"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(extractConsolidadeoSer.getRseMatricula())%></dd>
          <dt class="col-6"><hl:message key="rotulo.servidor.cpf"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(extractConsolidadeoSer.getSerCpf())%></dd>
          <dt class="col-6"><hl:message key="rotulo.convenio.singular"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(extractConsolidadeoSer.getCseNome())%></dd>
         <% if (!TextHelper.isNull(extractConsolidadeoSer.getConsignatarias())) { %>     
            <dt class="col-6"><hl:message key="rotulo.consignataria.plural"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(extractConsolidadeoSer.getConsignatarias())%></dd>     
        <% } %>          
        <% if (!extractConsolidadeoSer.getSvcCodigos().isEmpty()) { %>         
            <dt class="col-6"><hl:message key="rotulo.servico.plural"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(extractConsolidadeoSer.getServicos())%></dd>    
        <% } %>          
       </dl>
     </div>
   </div>
   <div class="table-responsive ">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
        <%  // monta cabeçalho da tabela  %>
            <th scope="col" width="10%"><hl:message key="rotulo.servidor.extrato.consolidado.mes.desconto"/></th>
          <% 
             List<TransferObject> ades = extractConsolidadeoSer.getAdes();
             for (int j = 0; j < ades.size(); j++) { 
              TransferObject to = ades.get(j);
              mapAdes.put(to.getAttribute(Columns.ADE_NUMERO).toString(), Integer.valueOf(j));
              mapSumTotalAde.put(to.getAttribute(Columns.ADE_NUMERO).toString(), BigDecimal.ZERO);                  
          %>
            <th scope="col" width="10%"></th>
            <% if ((responsavel.isCseSup() || responsavel.isSer()) && to.getAttribute(Columns.CSA_NOME_ABREV) != null) {%>
              <th scope="col" width="10%"><hl:message key="rotulo.servidor.extrato.consolidado.ade"/><br/><%=to.getAttribute(Columns.ADE_NUMERO)%><br/><%=TextHelper.forHtmlContent(to.getAttribute(Columns.CSA_NOME_ABREV))%></th>
            <% } else { %>
              <th scope="col" width="10%"><hl:message key="rotulo.servidor.extrato.consolidado.ade"/><br/><%=TextHelper.forHtmlContent(to.getAttribute(Columns.ADE_NUMERO))%></th>
            <% } %>
          <% } %>
            <th scope="col" width="10%"></th>
            <th scope="col" width="10%"><hl:message key="rotulo.servidor.extrato.consolidado.total.pago"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.servidor.extrato.consolidado.valor.devido"/></th>
          </tr>
          <%  // FIM monta cabeçalho da tabela  %>
        </thead>
        <tbody>    
            <% if (ades.size() == 0) { %>
            <tr>
              <td colspan="4"><hl:message key="mensagem.erro.servidor.extrato.nao.encontrado"/></td>
            </tr>          
            <% } else {
                 // lista parcelas por data de desconto
                 Date ultimaData = null;
                 Map<Integer, TransferObject> adesDataDesconto = new HashMap<Integer, TransferObject>();
                 Iterator<TransferObject> itdt = extractConsolidadeoSer.getParcelas().iterator();
                 while(itdt.hasNext()) {
                     // recupera dos dados das parcelas
                     TransferObject to = itdt.next();
                     Date dataDesconto = (Date) to.getAttribute(Columns.PRD_DATA_DESCONTO);
                             
                     if (ultimaData != null) {
                         // monta uma lista com os TOs das ADEs da mesma data de desconto
                         if (!dataDesconto.equals(ultimaData)) {
                             // imprime a linha
                             %>
                             <tr>
                               <td class="RESULT" align="center"><%=DateHelper.toPeriodString(ultimaData)%></td>
                             <%
                             for (int j = 0; j < ades.size(); j++) {
                                 // Verifica se a ADE tem parcela na data atual
                                 if (adesDataDesconto.get(j) != null) {
                                     TransferObject toColuna = adesDataDesconto.get(j);
                                     String adeNumeroColuna = toColuna.getAttribute(Columns.ADE_NUMERO).toString();
                                     Short numParcela = (Short) toColuna.getAttribute(Columns.PRD_NUMERO);
                                     BigDecimal valorRealizado = (BigDecimal) toColuna.getAttribute(Columns.PRD_VLR_REALIZADO);
                                     BigDecimal valorDevido = valorRealizado != null? ((BigDecimal) toColuna.getAttribute(Columns.PRD_VLR_PREVISTO)).subtract(valorRealizado): BigDecimal.ZERO;
                                     // Somatorios de cada contrato e de cada mes
                                     BigDecimal totalAde = ((BigDecimal) mapSumTotalAde.get(adeNumeroColuna)).add(valorRealizado);
                                     mapSumTotalAde.put(adeNumeroColuna, totalAde);
                                     BigDecimal pagoMes = ((BigDecimal) mapSumPagoMes.get(ultimaData)).add(valorRealizado);
                                     mapSumPagoMes.put(ultimaData, pagoMes);                        
                                     BigDecimal devidoMes = ((BigDecimal) mapSumDevidoMes.get(ultimaData)).add(valorDevido);
                                     mapSumDevidoMes.put(ultimaData, devidoMes); 
                                     // imprime a coluna caso tenha parcela na data
                                     %>
                                     <td class="THIN" align="center"><%=TextHelper.forHtmlContent(numParcela)%></td>
                                     <td class="RESULT" align="right"><%=NumberHelper.reformat(valorRealizado.toString(), "en", NumberHelper.getLang())%></td>
                                     <%
                                 } else {
                                     // imprime vazio caso ADE não tenha parcela na data
                                     %>
                                     <td class="THIN"></td>
                                     <td class="RESULT"></td>
                                     <%  
                                 }          
                             }
                             //imprime o total da linha
                             %>    
                               <td class="THIN"></td>                            
                               <td class="RESULT" align="right"><%=NumberHelper.reformat(mapSumPagoMes.get(ultimaData).toString(), "en", NumberHelper.getLang())%></td>
                               <td class="RESULT text-danger font-weight-bold" align="right"><%=(BigDecimal.ZERO.compareTo((BigDecimal)mapSumDevidoMes.get(ultimaData)) == 0)? "" : "(" + NumberHelper.reformat(mapSumDevidoMes.get(ultimaData).toString(), "en", NumberHelper.getLang()) + ")"%></td>
                             </tr>
                             <%
                             // zera o map 
                             adesDataDesconto = new HashMap<Integer, TransferObject>();
                             // Valor total pago e devido
                             totalPagoMes = totalPagoMes.add((BigDecimal) mapSumPagoMes.get(ultimaData));                   
                             totalDevidoMes = totalDevidoMes.add((BigDecimal) mapSumDevidoMes.get(ultimaData));
                         }
                     }
                     // inclui coluna
                     adesDataDesconto.put(mapAdes.get(to.getAttribute(Columns.ADE_NUMERO).toString()), to);
                     // última data percorrida
                     ultimaData = dataDesconto;                  
                     // zera totais por linha
                     mapSumPagoMes.put(dataDesconto, BigDecimal.ZERO);                       
                     mapSumDevidoMes.put(dataDesconto, BigDecimal.ZERO);

                     // imprime a última linha 
                     if (!itdt.hasNext()) {
                         %>
                         <tr>
                           <td class="RESULT" align="center"><%=DateHelper.toPeriodString(ultimaData)%></td>
                         <%
                         for (int j = 0; j < ades.size(); j++) {
                             // Verifica se a ADE tem parcela na data atual
                             if (adesDataDesconto.get(j) != null) {
                                 TransferObject toColuna = adesDataDesconto.get(j);
                                 String adeNumeroColuna = toColuna.getAttribute(Columns.ADE_NUMERO).toString();
                                 Short numParcela = (Short) toColuna.getAttribute(Columns.PRD_NUMERO);
                                 BigDecimal valorRealizado = (BigDecimal) toColuna.getAttribute(Columns.PRD_VLR_REALIZADO);
                                 BigDecimal valorDevido = valorRealizado != null? ((BigDecimal) toColuna.getAttribute(Columns.PRD_VLR_PREVISTO)).subtract(valorRealizado): BigDecimal.ZERO;
                                 // Somatorios de cada contrato e de cada mes
                                 BigDecimal totalAde = ((BigDecimal) mapSumTotalAde.get(adeNumeroColuna)).add(valorRealizado);
                                 mapSumTotalAde.put(adeNumeroColuna, totalAde);
                                 BigDecimal pagoMes = ((BigDecimal) mapSumPagoMes.get(ultimaData)).add(valorRealizado);
                                 mapSumPagoMes.put(ultimaData, pagoMes);                        
                                 BigDecimal devidoMes = ((BigDecimal) mapSumDevidoMes.get(ultimaData)).add(valorDevido);
                                 mapSumDevidoMes.put(ultimaData, devidoMes); 
                                 // imprime a coluna caso tenha parcela na data
                                 %>
                                 <td class="THIN" align="center"><%=TextHelper.forHtmlContent(numParcela)%></td>
                                 <td class="RESULT" align="right"><%=NumberHelper.reformat(valorRealizado.toString(), "en", NumberHelper.getLang())%></td>
                                 <%
                             } else {
                                 // imprime vazio caso ADE não tenha parcela na data
                                 %>
                                 <td class="THIN"></td>
                                 <td class="RESULT"></td>
                                 <%  
                             }          
                         }
                         // imprime o total da linha
                         %>    
                           <td class="THIN"></td>                            
                           <td class="RESULT" align="right"><%=NumberHelper.reformat(mapSumPagoMes.get(ultimaData).toString(), "en", NumberHelper.getLang())%></td>
                           <td class="RESULT text-danger font-weight-bold" align="right"><%=(BigDecimal.ZERO.compareTo((BigDecimal)mapSumDevidoMes.get(ultimaData)) == 0)? "" : "(" + NumberHelper.reformat(mapSumDevidoMes.get(ultimaData).toString(), "en", NumberHelper.getLang()) + ")"%></td>
                         </tr>
                         <%
                         // Valor total pago e devido
                         totalPagoMes = totalPagoMes.add((BigDecimal) mapSumPagoMes.get(ultimaData));                   
                         totalDevidoMes = totalDevidoMes.add((BigDecimal) mapSumDevidoMes.get(ultimaData));
                     }
                 }
                 
                 // imprime os totalizados por coluna
                 %>
                </tbody>
                <tfoot>
                   <tr>
                   <td class="FOOTER" align="center"><hl:message key="rotulo.servidor.extrato.consolidado.total"/></td>
                   <% 
                      // Exibe o total pago em cada contrato
                      for (int j = 0; j < ades.size(); j++) { 
                         TransferObject to = (TransferObject) ades.get(j);                  
                   %>
                   <td class="FOOTER"></td>              
                   <td class="FOOTER" align="right"><hl:message key="rotulo.moeda"/><%=NumberHelper.reformat(mapSumTotalAde.get(to.getAttribute(Columns.ADE_NUMERO).toString()).toString(), "en", NumberHelper.getLang())%> </td>
                   <%   }
                      // Exibe o total de valores pagos e devidos em todos os meses
                   %>
                   <td class="FOOTER"></td>              
                   <td class="FOOTER" align="right"><hl:message key="rotulo.moeda"/><%=TextHelper.forHtmlContent( NumberHelper.reformat(totalPagoMes.toString(), "en", NumberHelper.getLang()))%></td>
                   <td class="FOOTER text-danger font-weight-bold" align="right"><%=(BigDecimal.ZERO.compareTo(totalDevidoMes) == 0)? "" : "(" + ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + NumberHelper.reformat(totalDevidoMes.toString(), "en", NumberHelper.getLang()) + ")"%></td>                
                 </tr>
               </tfoot>      
           </table>  
          <% } %>
    </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL((responsavel.isSer() ? "../v3/carregarPrincipal" : paramSession.getLastHistory()), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
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
		color: #000;
	}
	img {
	  -webkit-filter: grayscale(100%);
	  filter: grayscale(100%);
	  filter: gray;
	}
	body{color: #000 !important}
    #dataTables th:last-child {display: none;}
    #dataTables td:last-child {display: none;}
	#dataTables_length {display: none;}
	#dataTables_paginate {display: none;}
	#dataTables_filter {display: none;}	
	#dataTables_info {display: none;}
    #active-buttons {display: none;}
    #menuAcessibilidade {display: none;}
    #header-print img{width: 10%;} 
    #footer-print {position: absolute; bottom: 0;}   
    .opcoes-avancadas {display: none;}
	.table thead th {
		padding: 0 .75rem;
	}

    .table thead tr th, .table tbody tr td {
      font-size: 12px;
      line-height: 1.25;
      padding-top: 0;
      padding-bottom: 0;
      padding-left: .25rem;
      padding-left: .25rem;
      color: #000 !important;
	  border-left: 1px solid #000 !important;
    }
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
		dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);	
	}
</script>
<script type="text/JavaScript">
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