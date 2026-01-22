<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.job.process.ProcessaDesfazUltimoRetorno" %>
<%@ page import="com.zetra.econsig.job.process.ControladorProcessos" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
String estCodigo = (String) request.getAttribute("estCodigo");
String orgCodigo = (String) request.getAttribute("orgCodigo");
List<TransferObject> lstEstabelecimentos = (List<TransferObject>) request.getAttribute("lstEstabelecimentos");
List<TransferObject> lstOrgaos = (List<TransferObject>) request.getAttribute("lstOrgaos");
List<TransferObject> historicoParcelas = (List<TransferObject>) request.getAttribute("historicoParcelas");
Date ultPeriodoDate = (Date) request.getAttribute("ultPeriodoDate");
boolean existePeriodoExportado = (boolean) request.getAttribute("existePeriodoExportado");
%>
<c:set var="title">
  <hl:message key="rotulo.folha.desfazer.ultimo.retorno.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"><hl:message key="rotulo.efetiva.acao.consignacao.confDesliquidacao"/></use>
</c:set>

<c:set var="bodyContent">
<form name="form1" method="post" action="../v3/desfazerRetorno?acao=desfazer">
<%= SynchronizerToken.generateHtmlToken(request) %>
<% if (!temProcessoRodando) { %>
<% if (responsavel.isCseSup()) { %>
<div class="card">
  <div class="card-header">
    <h2 class="card-header-title"><hl:message key="rotulo.calendario.folha.selecione.entidade"/></h2>
  </div>
  <div class="card-body">
    <div class="row">
      <span id="descricao"><hl:message key="rotulo.calendario.folha.entidade"/></span>
      <div class="form-group col-sm-12" role="radio-group" area-labeldbay="iEntidade">
        <div class="form-check form-check-inline mt-2" >
          <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeGeral" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_CSE%>" <% if (TextHelper.isNull(estCodigo) && TextHelper.isNull(orgCodigo)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
          <label class="form-check-label labelSemNegrito ml-1 pr-4" for="tipoEntidadeGeral"><hl:message key="rotulo.geral.singular"/></label>
        </div>
        <div class="form-check form-check-inline mt-2" >
          <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeEstabelecimento" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_EST%>" <% if (!TextHelper.isNull(estCodigo)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
          <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="tipoEntidadeEstabelecimento"><hl:message key="rotulo.estabelecimento.singular"/></label>
        </div>
        <div class="form-check form-check-inline mt-2" >
          <input class="form-check-input mt-1 ml-1" type="radio" name="tipoEntidade" id="tipoEntidadeOrgao" onChange="alterarTipoEntidade()" value="<%=(String)AcessoSistema.ENTIDADE_ORG%>" <% if (!TextHelper.isNull(orgCodigo)) {%>checked<% } %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
          <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="tipoEntidadeOrgao"><hl:message key="rotulo.orgao.singular"/></label>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="form-group col-sm-12">
        <label for="estCodigo"><hl:message key="rotulo.estabelecimento.singular"/></label>
       <%=JspHelper.geraCombo(lstEstabelecimentos, "estCodigo", Columns.EST_CODIGO, Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) , "onChange=\"carregaDadosFiltro()\"", false, 1, estCodigo, null, TextHelper.isNull(estCodigo), "form-control")%>
      </div>
    </div>
    <div class="row">
      <div class="form-group col-sm-12">
        <label for="orgCodigo"><hl:message key="rotulo.orgao.singular"/></label>
        <%=JspHelper.geraCombo(lstOrgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "onChange=\"carregaDadosFiltro()\"", false, 1, orgCodigo, null, TextHelper.isNull(orgCodigo), "form-control")%>
      </div>
    </div>
  </div>
</div>
<% } %>

<div class="card">
  <div class="card-header">
    <h2 class="card-header-title"><hl:message key="rotulo.folha.informacoes.periodo.anterior"/></h2>
  </div>
  <div class="card-body">
  <% if (existePeriodoExportado) { %>
  <div class="row">
    <span id="descricao"><hl:message key="rotulo.folha.desfazer.ultimo.movimento"/></span>
    <div class="form-group ml-3" role="radiogroup" aria-labelledby="iDesfazerUltimoMovimento">
      <div class="form-check form-check-inline pt-2">
        <input class="form-check-input ml-1" type="radio" name="desfazerMovimento" id="desfazerMovimentoS" value="true" checked="checked">
        <label class="form-check-label labelSemNegrito pr-4" for="desfazerMovimentoS"><hl:message key="rotulo.sim"/></label>
      </div>
      <div class="form-check form-check-inline pt-2">
      <input class="form-check-input ml-1" type="radio" name="desfazerMovimento" id="desfazerMovimentoN" value="false">
        <label class="form-check-label labelSemNegrito pr-4" for="desfazerMovimentoN"><hl:message key="rotulo.nao"/></label>
      </div>
    </div>
  </div>
  <% } %>
  <div class="row">
    <span id="descricao"><hl:message key="rotulo.folha.recalcular.margem"/></span>
    <div class="form-group ml-3" role="radiogroup" aria-labelledby="IncluirUsuariosDeSuporte">
      <div class="form-check form-check-inline pt-2">
        <input class="form-check-input ml-1" type="radio" name="recalcularMargem" id="recalcularMargemS" value="true">
        <label class="form-check-label labelSemNegrito pr-4" for="recalcularMargemS"><hl:message key="rotulo.sim"/></label>
      </div>
      <div class="form-check form-check-inline pt-2">
      <input class="form-check-input ml-1" type="radio" name="recalcularMargem" id="recalcularMargemN" value="false" checked="checked">
        <label class="form-check-label labelSemNegrito pr-4" for="recalcularMargemN"><hl:message key="rotulo.nao"/></label>
      </div>
    </div>
  </div>  
  <% if (historicoParcelas == null || historicoParcelas.isEmpty()) { %>
    <div class="row">
      <div class="form-group col-sm-12 col-md-6">
        <label for="periodoAnterior"><hl:message key="rotulo.folha.periodo.anterior"/></label>
        <input type="text" name="periodoAnterior" id="periodoAnterior" value="<%=TextHelper.forHtmlContent(DateHelper.toPeriodString(ultPeriodoDate))%>" class="form-control" disabled>
      </div>
    </div>
  <% } else { %>
    <div class="legend">
      <span><hl:message key="rotulo.folha.periodo"/></span>
    </div>
    <div class="table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.parcela.data.desconto"/></th>
            <th scope="col"><hl:message key="rotulo.parcela.data.realizado"/></th>
            <th scope="col"><hl:message key="rotulo.ocorrencia.descricao"/></th>
            <th scope="col"><hl:message key="rotulo.folha.desfazer.ultimo.retorno.qtd.parcelas"/></th>
            <th scope="col"><hl:message key="rotulo.acoes.desfazer"/></th>
          </tr>
        </thead>
        <tbody>
        <%
          int i = 0;
          Date prdDataRealizadoAtual = null;
          Iterator<TransferObject> it = historicoParcelas.iterator();
          while (it.hasNext()) {
              TransferObject historico = it.next();
              Date prdDataDesconto = (Date) historico.getAttribute(Columns.PRD_DATA_DESCONTO);
              Date prdDataRealizado = (Date) historico.getAttribute(Columns.PRD_DATA_REALIZADO);
              String tocCodigo = (String) historico.getAttribute(Columns.TOC_CODIGO);
              String tocDescricao = (String) historico.getAttribute(Columns.TOC_DESCRICAO);
              String qtdParcelas = historico.getAttribute("QTDE").toString();
              
              String value = DateHelper.format(prdDataDesconto, "yyyy-MM-dd") + ";" + DateHelper.format(prdDataRealizado, "yyyy-MM-dd") + ";" + tocCodigo;
              boolean disabled = (prdDataDesconto.compareTo(ultPeriodoDate) == 0 && 
                      (tocCodigo.equals(CodedValues.TOC_RETORNO_FERIAS) || tocCodigo.equals(CodedValues.TOC_RETORNO_PARCIAL_FERIAS)));
              boolean checked = (!disabled && (prdDataRealizadoAtual == null || prdDataRealizadoAtual.compareTo(prdDataRealizado) == 0));
              prdDataRealizadoAtual = (prdDataRealizadoAtual == null && !disabled ? prdDataRealizado : prdDataRealizadoAtual);
          %>
            <tr CLASS="<%=TextHelper.forHtmlAttribute(i++%2==0?"Li":"Lp")%>">
              <td ALIGN="center"><%=TextHelper.forHtmlContent(DateHelper.toPeriodString(prdDataDesconto))%></td>
              <td ALIGN="center"><%=TextHelper.forHtmlContent(DateHelper.toDateString(prdDataRealizado))%></td>
              <td ALIGN="center"><%=TextHelper.forHtmlContent(tocDescricao)%></td>
              <td ALIGN="right"><%=TextHelper.forHtmlContent(qtdParcelas)%></td>
              <td ALIGN="center"><input type="checkbox" name="parcelas" value="<%=TextHelper.forHtmlAttribute(value)%>" <%= disabled ? "disabled" : "" %> <%= checked ? "checked" : "" %>></td>
            </tr>
          <%
          }
          %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="5"><hl:message key="rotulo.folha.listagem.retorno"/> </td>
          </tr>
        </tfoot>
      </table>
    </div>
  <% } %>
  </div>
</div>
<% } %>
<% if (!temProcessoRodando) { %>
</form>
<div class="btn-action">
  <a class="btn btn-outline-danger" href="#no-back" onclick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
  <a class="btn btn-primary" href="#no-back" onClick="desfazer(); return false;"><hl:message key="rotulo.botao.confirmar"/></a>
</div> 
<% } %>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript">
  var f0 = document.forms[0];

  window.onload = doLoad(<%=(boolean)temProcessoRodando%>);
    
  function doLoad(reload) {
    if (reload) {
      setTimeout("refresh()", 15*1000);
    }
  }
  
  function refresh() {
    postData("../v3/desfazerRetorno?acao=iniciar");
  }
  
  function desfazer() {
    msg = '<hl:message key="mensagem.folha.confirmacao.desfazer.ultimo.retorno"/>';
    if (getCheckedRadio('form1', 'desfazerMovimento') == 'false') {
      msg = '<hl:message key="mensagem.folha.confirmacao.desfazer.ultimo.retorno.sem.desfazer.movimento"/>';
    }
    if (confirm(msg)) {
      f0.submit();
      return true;
    }
    return false;
  }

  function alterarTipoEntidade() {
    var tipoEntidade = getCheckedRadio('form1', 'tipoEntidade');

    if (tipoEntidade == null || tipoEntidade == '') {
      alert('<hl:message key="mensagem.calendario.folha.selecione.entidade"/>');
      return;
    }

    if (tipoEntidade == 'CSE') {
      f0.estCodigo.disabled = true;
      f0.orgCodigo.disabled = true;
      recarregar();
    } else if (tipoEntidade == 'EST') {
      f0.estCodigo.disabled = false;
      f0.orgCodigo.disabled = true;
    } else if (tipoEntidade == 'ORG') {
      f0.estCodigo.disabled = true;
      f0.orgCodigo.disabled = false;
    }
  }

  function carregaDadosFiltro() {
	var link = "../v3/desfazerRetorno?acao=iniciar";

	if (f0.estCodigo != null && f0.estCodigo.value != null && f0.estCodigo.value != "") {
        link+='&estCodigo=' + f0.estCodigo.value;
    }

    if (f0.orgCodigo != null && f0.orgCodigo.value != null && f0.orgCodigo.value != "") {
        link+='&orgCodigo=' + f0.orgCodigo.value;
    }

    postData(link);
  }
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>