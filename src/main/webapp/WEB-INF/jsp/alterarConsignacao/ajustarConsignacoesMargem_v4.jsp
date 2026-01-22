<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> autdesList = (List<TransferObject>) request.getAttribute("autdesList");
List<String> adeCodigos = (List<String>) request.getAttribute("adeCodigos");
BigDecimal totalAtual = (BigDecimal) request.getAttribute("totalAtual");
List<MargemTO> margens = (List<MargemTO>) request.getAttribute("margens");
String rseCodigo = (String) request.getAttribute("rseCodigo");
String periodoIni = (String) request.getAttribute("periodoIni");
String periodoFim = (String) request.getAttribute("periodoFim");
Boolean permiteReverterValor = (Boolean) request.getAttribute("permiteReverterValor");
String processoExistente = (String) request.getAttribute("processoExistente");
%>
<c:set var="title">
<hl:message key="rotulo.ajustar.consignacoes.a.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form action="../v3/ajustarConsignacoesMargem" method="post" name="form1">
     <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
    <% pageContext.setAttribute("autdes", autdesList); %>
    <hl:detalharADEv4 name="autdes" table="false" type="alterar" />
    <%-- Fim dos dados da ADE --%>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="mensagem.ajustar.consignacoes.a.margem.informe"/></h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-md-6">
            <label for="ajustarMargem"><hl:message key="rotulo.ajustar.consignacoes.a.margem"/></label>
            <select name="ajustarMargem" id="ajustarMargem" class="form-control" onfocus="SetarEventoMascara(this,'#*200',true);" onblur="fout(this);ValidaMascara(this);">
              <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
              <%
              if (margens != null) {
                 Iterator<MargemTO> it = margens.iterator();
                 while (it.hasNext()) {
                     MargemTO margem = it.next();
                     if (margem.getMrsMargem() != null) {
              %>
                      <option value="<%= margem.getMarCodigo()%>;<%=margem.getMrsMargem()%>"><%= TextHelper.forHtmlContent(margem.getMarDescricao())%></option>
              <%     } 
                 }
              } 
              %>
            </select>
          </div>
          <div class="form-group col-md-6">
            <label for="totalAtual"><hl:message key="rotulo.alterar.multiplo.consignacao.valor.total.atual"/> (<hl:message key="rotulo.moeda"/>)</label>
            <input type="text" class="form-control" id="totalAtual" name="totalAtual" value="<%=NumberHelper.format(totalAtual.doubleValue(), LocaleHelper.getLanguage())%>" disabled/>
          </div>
          
          <% if (permiteReverterValor) { %>
            <div class="form-check form-group col-md-12 mt-2" role="radiogroup" aria-labelledby="restaurarValor">
              <div class="form-group my-0">
                <span id="restaurarValor"><hl:message key="rotulo.alterar.multiplo.consignacao.restaurar.valor.ultima.alteracao"/></span>
              </div>
              <div class="form-check mt-2">
                <input class="form-check-input ml-1" type="radio" name="restaurarValor" id="restaurarValorSim" value="true" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" onChange="desableMargem('true')">
                <label class="form-check-label pr-3" for="restaurarValorSim">
                  <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                </label>
                <input class="form-check-input ml-1" type="radio" name="restaurarValor" id="restaurarValorNao" value="false" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" onChange="desableMargem()">
                <label class="form-check-label" for="restaurarValorNao">
                  <span class="text-nowrap align-text-top"><hl:message key="rotulo.nao"/></span>
                </label>
              </div>
            </div>
          <% } %>
        </div>
      </div>
    </div>
    <%= SynchronizerToken.generateHtmlToken(request) %>     
    <hl:htmlinput name="acao"          type="hidden" di="acao"         value="validar" />
    <hl:htmlinput name="autdesList"    type="hidden" di="autdesList"   value="<%=TextHelper.forHtmlAttribute(adeCodigos)%>" /> 
    <hl:htmlinput name="rseCodigo"     type="hidden" di="rseCodigo"    value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" /> 
    <hl:htmlinput name="periodoIni"     type="hidden" di="periodoIni"    value="<%=periodoIni%>" /> 
    <hl:htmlinput name="periodoFim"     type="hidden" di="periodoFim"    value="<%=periodoFim%>" /> 
     
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" id="validar" onClick="if(validarCampos()){f0.submit();} return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consultar"></use></svg><hl:message key="rotulo.botao.validar"/></a>
    </div>
  </form>
</body>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">
f0 = document.forms[0];

function validarCampos(){
	var margemSelecionada = document.getElementById("ajustarMargem").value;
	var restauraValor = document.getElementById("restaurarValor");
	if (restauraValor != null && $('#restaurarValorSim').is(':checked')) {
        return true;
	} else if(margemSelecionada == "" || margemSelecionada == 'undefined'){
        alert('<hl:message key="mensagem.ajustar.consignacao.a.margem.selecione.margem"/>');
        return false;
    } else if(<%=!TextHelper.isNull(processoExistente)%> && $('#restaurarValorNao').is(':checked')){
        if (!confirm('<%=TextHelper.forJavaScriptBlock(processoExistente)%>')) {
            return false;
        }
      }
    return true;
}

function formLoad() {
   focusFirstField();
}

function desableMargem(valida){
    if(valida == 'true'){
    	$("#ajustarMargem").prop("disabled", true);
    } else {
    	$("#ajustarMargem").prop("disabled", false);
    }
}

window.onload = formLoad;
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>