<%--
* <p>Title: detalhar_pesquisa.jsp</p>
* <p>Description: Página de detalhe de acompanhamento de contratos comprados no leiaute v4</p>
* <p>Copyright: Copyright (c) 2006</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.ParamSvcTO"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
boolean possuiEtapaAprovacaoSaldo = (Boolean) request.getAttribute("possuiEtapaAprovacaoSaldo");
List<TransferObject> listaContratos = (List<TransferObject>) request.getAttribute("listaContratos");
	
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>

<c:set var="title">
  <hl:message key="rotulo.acompanhar.compra.contrato.titulo"/>
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
  <form action="../v3/acompanharPortabilidade?<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
    <input type="hidden" name="acao" value="detalharPesquisa" />
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.acompanhamento.compra.contrato.titulo"/></h2>
      </div>
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.consignataria.plural"/>:<br><hl:message key="rotulo.saldo.devedor.origem"/>&nbsp;/&nbsp;<hl:message key="rotulo.saldo.devedor.destino"/></th>
              <th scope="col"><hl:message key="rotulo.servico.singular"/></th>
              <th scope="col"><hl:message key="rotulo.servidor.singular"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.status"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.data.inclusao"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
              <th scope="col"><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
              <th class="d-print-none" scope="col"><hl:message key="rotulo.data.compra.abreviado"/></th>
              <th class="d-print-none" scope="col"><hl:message key="rotulo.saldo.devedor.data.abreviado"/></th>
              <% if (possuiEtapaAprovacaoSaldo) { %>
                <th class="d-print-none" scope="col"><hl:message key="rotulo.saldo.devedor.data.aprovacao.abreviado"/></th>
              <% } %>
              <th class="d-print-none" scope="col"><hl:message key="rotulo.saldo.devedor.data.pagamento.abreviado"/></th>
              <th class="d-print-none" scope="col"><hl:message key="rotulo.saldo.devedor.data.liquidacao.abreviado"/></th>
              <th class="d-print-none" scope="col"><hl:message key="rotulo.saldo.devedor.valor.abreviado"/></th>
              <th class="d-print-none" scope="col"><hl:message key="rotulo.saldo.devedor.informacoesbancarias.banco"/><br><hl:message key="rotulo.saldo.devedor.informacoesbancarias.agencia"/><br><hl:message key="rotulo.saldo.devedor.informacoesbancarias.conta"/></th>
              <th class="d-print-none" scope="col"><hl:message key="rotulo.saldo.devedor.favorecido.nome"/><br><hl:message key="rotulo.cnpj"/></th>
            </tr>
          </thead>
          <tbody>
          <%
            CustomTransferObject ade = null;
            String adeCodigo = null;
            String stcDescricao = null;
            String adeNumero = null;
            String adePrazo = null;
            String adeData = null;
            String adeVlr = null;
            String adeTipoVlr = null;
            String consignatariaOrigem = null;
            String consignatariaDestino = null;
            String servidor = null;
            String servico = null;
            String sdvValor = null;
            String sdvContaDeposito = null;
            String sdvBanco = null;
            String sdvAgencia = null;
            String sdvConta = null;
            String sdvFavorecidoDeposito = null;
            String sdvNomeFavorecido = null;
            String sdvCnpjFavorecido = null;
            String dataCompra = null;
            String dataInfSdv = null;
            String dataAprSdv = null;
            String dataPagSdv = null;
            String dataLiquid = null;
          
            Iterator it = listaContratos.iterator();
            while (it.hasNext()) {
              ade = (CustomTransferObject) it.next();
          
              adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
              stcDescricao = ade.getAttribute(Columns.STC_DESCRICAO).toString();
              adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
              adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
              adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
              consignatariaOrigem = ade.getAttribute(Columns.CSA_NOME_ABREV) != null ? ade.getAttribute(Columns.CSA_NOME_ABREV).toString() : ade.getAttribute(Columns.CSA_NOME).toString();
              consignatariaDestino = ade.getAttribute("CSA_NOME_ABREV_DESTINO") != null ? ade.getAttribute("CSA_NOME_ABREV_DESTINO").toString() : ade.getAttribute("CSA_NOME_DESTINO").toString();
              servidor = ade.getAttribute(Columns.RSE_MATRICULA) + " - " + ade.getAttribute(Columns.SER_NOME) + " - " + ade.getAttribute(Columns.SER_CPF);
              servico = (ade.getAttribute(Columns.CNV_COD_VERBA) != null && !ade.getAttribute(Columns.CNV_COD_VERBA).toString().equals("")) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
              servico += (ade.getAttribute(Columns.ADE_INDICE) != null && !ade.getAttribute(Columns.ADE_INDICE).toString().equals("")) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "";
              servico += " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString();
              sdvValor = (ade.getAttribute(Columns.SDV_VALOR) != null) ? NumberHelper.reformat(ade.getAttribute(Columns.SDV_VALOR).toString(), "en", NumberHelper.getLang()) : "";
              sdvBanco = (ade.getAttribute(Columns.SDV_BCO_CODIGO) != null) ? TextHelper.formataMensagem(ade.getAttribute(Columns.SDV_BCO_CODIGO).toString(), "0", 3, false) : "";
              sdvAgencia = (ade.getAttribute(Columns.SDV_AGENCIA) != null) ? ade.getAttribute(Columns.SDV_AGENCIA).toString() : "";
              sdvConta = (ade.getAttribute(Columns.SDV_CONTA) != null) ? ade.getAttribute(Columns.SDV_CONTA).toString() : "";
              sdvNomeFavorecido = (ade.getAttribute(Columns.SDV_NOME_FAVORECIDO) != null) ? ade.getAttribute(Columns.SDV_NOME_FAVORECIDO).toString() : "";
              sdvCnpjFavorecido = (ade.getAttribute(Columns.SDV_CNPJ) != null) ? ade.getAttribute(Columns.SDV_CNPJ).toString() : "";
              dataCompra = ade.getAttribute(Columns.RAD_DATA) != null ? DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
              dataInfSdv = ade.getAttribute(Columns.RAD_DATA_INF_SALDO)  != null ? DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA_INF_SALDO).toString(),  "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
              dataAprSdv = ade.getAttribute(Columns.RAD_DATA_APR_SALDO)  != null ? DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA_APR_SALDO).toString(),  "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
              dataPagSdv = ade.getAttribute(Columns.RAD_DATA_PGT_SALDO)  != null ? DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA_PGT_SALDO).toString(),  "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
              dataLiquid = ade.getAttribute(Columns.RAD_DATA_LIQUIDACAO) != null ? DateHelper.reformat(ade.getAttribute(Columns.RAD_DATA_LIQUIDACAO).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
          
              sdvContaDeposito = sdvBanco;
              sdvContaDeposito = (!TextHelper.isNull(sdvContaDeposito) && !TextHelper.isNull(sdvAgencia)) ? sdvContaDeposito + " / " + sdvAgencia : sdvContaDeposito;
              sdvContaDeposito = (!TextHelper.isNull(sdvContaDeposito) && !TextHelper.isNull(sdvConta)) ? sdvContaDeposito + " / " + sdvConta : sdvContaDeposito;
              
              sdvFavorecidoDeposito = sdvNomeFavorecido;
              sdvFavorecidoDeposito = (!TextHelper.isNull(sdvFavorecidoDeposito) && !TextHelper.isNull(sdvCnpjFavorecido)) ? sdvFavorecidoDeposito + " / " + sdvCnpjFavorecido : sdvFavorecidoDeposito;
              
              adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
              if (!adeVlr.equals("")) {
                adeVlr = NumberHelper.format(Double.valueOf(adeVlr).doubleValue(), NumberHelper.getLang());
                adeTipoVlr = (String) ade.getAttribute(Columns.ADE_TIPO_VLR);
              }
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(consignatariaOrigem + " / " + consignatariaDestino)%></td>
            <td><%=TextHelper.forHtmlContent(servico)%></td>
            <td><%=TextHelper.forHtmlContent(servidor)%></td>
            <td><%=TextHelper.forHtmlContent(stcDescricao)%></td>
            <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
            <td><%=TextHelper.forHtmlContent(adeData)%></td>
            <td><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr) + " " + adeVlr)%></td>
            <td><%=TextHelper.forHtmlContent(adePrazo)%></td>
            <td class="d-print-none"><%=TextHelper.forHtmlContent(dataCompra)%></td>
            <td class="d-print-none"><%=TextHelper.forHtmlContent(dataInfSdv)%></td>
            <% if (possuiEtapaAprovacaoSaldo) { %>
              <td class="d-print-none"><%=TextHelper.forHtmlContent(dataAprSdv)%></td>
            <% } %>
            <td class="d-print-none"><%=TextHelper.forHtmlContent(dataPagSdv)%></td>
            <td class="d-print-none"><%=TextHelper.forHtmlContent(dataLiquid)%></td>
            <td class="d-print-none"><%=TextHelper.forHtmlContent(!TextHelper.isNull(sdvValor) ? ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) : "" + sdvValor)%></td>
            <td class="d-print-none"><%=TextHelper.forHtmlContent(sdvContaDeposito)%></td>
            <td class="d-print-none"><%=TextHelper.forHtmlContent(sdvFavorecidoDeposito)%></td>
          </tr>
          <%
              }
          %>
          </tbody>
        </table>
      </div>
    </div>
  </form>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
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
	    #menuAcessibilidade {display: none;}
        #header-print img{width: 10%;}    
	    .opcoes-avancadas {display: none;}
		.table thead tr th, .table tbody tr td {
			border-left: 1px solid #000 !important;
			padding: 0 .75rem;
		}
	
	    table th, table td {
	      font-size: 9px;
	      line-height: 1;
	      padding: 0;
	      margin: 0;
	      color: #000 !important;
	    }
	  }
	  @page{
		margin: 0.3cm;
	  }
	
	</style>
	<script type="text/JavaScript">
		function imprimir() {
			injectDate();
			window.print();
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