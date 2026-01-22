<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
Boolean usaCalculoCom365Dias = (Boolean) request.getAttribute("usaCalculoCom365Dias");
Boolean usaDiasUtiasRepasse = (Boolean) request.getAttribute("usaDiasUtiasRepasse");
Integer diaRepasse = (Integer) request.getAttribute("diaRepasse");
Integer carenciaRepasse = (Integer) request.getAttribute("carenciaRepasse");
Double periodoCarencia = (Double) request.getAttribute("periodoCarencia");
String taxaJuros = (String) request.getAttribute("taxaJuros");
String repasse = (String) request.getAttribute("repasse");
%>
<c:set var="title">
   <hl:message key="mensagem.demonstrar.taxa.juros.titulo.pagina"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <form action="../v3/demonstrarTaxaJuros?acao=demonstrar" method="post" name="form1">
    <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
    <div class="row">
      <div class="col-sm-6">
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="mensagem.demonstrar.taxa.juros.titulo.card"/></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-4">
                <label for="prazo"><hl:message key="rotulo.demonstrar.taxa.juros.prazo"/></label>
                <input class="form-control" type="text" id="prazo" name="prazo" value="<%= !TextHelper.isNull(request.getParameter("prazo")) ? request.getParameter("prazo") : "" %>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);">
              </div>
              <div class="form-group col-sm-4">
                <label for="vlrLiberado"><hl:message key="rotulo.demonstrar.taxa.juros.valor.financiado"/></label>
                <input class="form-control" type="text" id="vlrLiberado" name="vlrLiberado" value="<%= !TextHelper.isNull(request.getParameter("vlrLiberado")) ? request.getParameter("vlrLiberado") : "" %>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);formataValorDecimal(this);ValidaMascara(this);">
              </div>
              <div class="form-group col-sm-4">
                <label for="vlrParcela"><hl:message key="rotulo.demonstrar.taxa.juros.valor.parcela"/></label>
                <input class="form-control" type="text" id="vlrParcela" name="vlrParcela" value="<%= !TextHelper.isNull(request.getParameter("vlrParcela")) ? request.getParameter("vlrParcela") : "" %>" onFocus="SetarEventoMascara(this,'#F11',true);" onBlur="fout(this);formataValorDecimal(this);ValidaMascara(this);">
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="dataContrato"><hl:message key="rotulo.demonstrar.taxa.juros.data.inclusao"/> (<%= LocaleHelper.getDatePlaceHolder() %>)</label>
                <input class="form-control" type="text" id="dataContrato" name="dataContrato" value="<%= !TextHelper.isNull(request.getParameter("dataContrato")) ? request.getParameter("dataContrato") : "" %>" onFocus="SetarEventoMascara(this,'<%= LocaleHelper.getDateJavascriptPattern() %>',true);" onBlur="fout(this);ValidaMascara(this);">
              </div>
              <div class="form-group col-sm-6">
                <label for="dataIni"><hl:message key="rotulo.demonstrar.taxa.juros.data.desconto"/> (<%= LocaleHelper.getDatePlaceHolder() %>)</label>
                <input class="form-control" type="text" id="dataIni" name="dataIni" value="<%= !TextHelper.isNull(request.getParameter("dataIni")) ? request.getParameter("dataIni") : "" %>" onFocus="SetarEventoMascara(this,'<%= LocaleHelper.getDateJavascriptPattern() %>',true);" onBlur="fout(this);ValidaMascara(this);">
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="diaRepasse"><hl:message key="rotulo.demonstrar.taxa.juros.param.dia.repasse"/> (<%= CodedValues.TPC_USA_DIAS_UTEIS_DIA_PAGTO_PRIMEIRA_PARCELA %>)</label>
                <input class="form-control" type="text" id="diaRepasse" name="diaRepasse" value="<%= usaDiasUtiasRepasse ? ApplicationResourcesHelper.getMessage("rotulo.demonstrar.taxa.juros.param.dia.repasse.dia.util", responsavel, String.valueOf(diaRepasse)) : String.valueOf(diaRepasse) %>" disabled="disabled"> 
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="carenciaRepasse"><hl:message key="rotulo.demonstrar.taxa.juros.param.carencia.repasse"/> (<%= CodedValues.TPC_QTD_MESES_PARA_PAGTO_PRIMEIRA_PARCELA %>)</label>
                <input class="form-control" type="text" id="carenciaRepasse" name="carenciaRepasse" value="<%= carenciaRepasse %>" disabled="disabled"> 
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="usaCalculoCom365Dias"><hl:message key="rotulo.demonstrar.taxa.juros.calculo.365.dias"/> (<%= CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS %>)</label>
                <input class="form-control" type="text" id="carenciaRepasse" name="carenciaRepasse" value="<%= ApplicationResourcesHelper.getMessage(usaCalculoCom365Dias ? "rotulo.sim" : "rotulo.nao", responsavel) %>" disabled="disabled">
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <a class="btn btn-primary" href="#no-back" onClick="validar(); return false;"><hl:message key="rotulo.botao.demonstrar"/></a>
        </div>
      </div>

      <% if (taxaJuros != null) { %>      
        <div class="col-sm-6">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="mensagem.demonstrar.taxa.juros.titulo.card.resultado"/></h2>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="form-group col-sm">
                  <label for="periodoCarencia"><hl:message key="rotulo.demonstrar.taxa.juros.periodo.carencia"/></label>
                  <input class="form-control" type="text" id="periodoCarencia" name="periodoCarencia" value="<%= periodoCarencia %>" disabled="disabled"> 
                </div>
              </div>
              <div class="row">
                <div class="form-group col-sm">
                  <label for="repasse"><hl:message key="rotulo.demonstrar.taxa.juros.repasse.csa"/></label>
                  <input class="form-control" type="text" id="repasse" name="repasse" value="<%= repasse %>" disabled="disabled">
                </div>
              </div>
              <div class="row">
                <div class="form-group col-sm">
                  <label for="taxaJuros"><hl:message key="rotulo.demonstrar.taxa.juros.taxa.calculada"/></label>
                  <input class="form-control" type="text" id="taxaJuros" name="taxaJuros" value="<%= taxaJuros %>" disabled="disabled">
                </div>
              </div>
            </div>
          </div>
        </div>
      <% } %>
      
    </div>
  </form>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">
f0 = document.forms[0];
window.onload = new function() {
    focusFirstField();
}

function validar() {
    var Controles = new Array("prazo", "vlrLiberado", "vlrParcela", "dataContrato", "dataIni");
    var Msgs = new Array(
    		'<hl:message key="mensagem.demonstrar.taxa.juros.informe.prazo"/>',
    		'<hl:message key="mensagem.demonstrar.taxa.juros.informe.valor.financiado"/>',
    		'<hl:message key="mensagem.demonstrar.taxa.juros.informe.valor.parcela"/>',
    		'<hl:message key="mensagem.demonstrar.taxa.juros.informe.data.inclusao"/>',
    		'<hl:message key="mensagem.demonstrar.taxa.juros.informe.data.desconto"/>');

    if (ValidaCampos(Controles, Msgs)) {
        f0.submit();
    }
}

function formataValorDecimal(e) {
    if (e.value != '') { 
        e.value = FormataContabil(parse_num(e.value), 2);
    }
}
</script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
