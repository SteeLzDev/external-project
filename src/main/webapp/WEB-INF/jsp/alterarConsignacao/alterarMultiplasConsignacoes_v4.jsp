<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> autdesList = (List<TransferObject>) request.getAttribute("autdesList");
BigDecimal totalAtual = (BigDecimal) request.getAttribute("totalAtual");
String chkAdeCodigo = (String) request.getAttribute("chkAdeCodigo");
List<MargemTO> margens = (List<MargemTO>) request.getAttribute("margens");
List<MargemTO> margensCheia = (List<MargemTO>) request.getAttribute("margensCheia");
Boolean permiteReverterValor = (Boolean) request.getAttribute("permiteReverterValor");
Boolean exibirRestaurarIncidencia = (Boolean) request.getAttribute("exibirRestaurarIncidencia");
Boolean exibirDesbloquearServidor = (Boolean) request.getAttribute("exibirDesbloquearServidor");
Boolean registroServidorDesbloqueado = (Boolean) request.getAttribute("registroServidorDesbloqueado");
Boolean omitirAlterarIncidencia = (Boolean) request.getAttribute("omitirAlterarIncidencia");
String msgAlertaAlteracaoPosterior = (String) request.getAttribute("msgAlertaAlteracaoPosterior");
String cemPorCento = NumberHelper.reformat("100.00", "en", LocaleHelper.getLanguage());
String adeNaoSelecionadas = (String) request.getAttribute("adeNaoSelecionadas");
String marCodigos = !TextHelper.isNull(request.getAttribute("marCodigos")) ? (String) request.getAttribute("marCodigos") : "";
%>
<c:set var="title">
<hl:message key="rotulo.alterar.multiplo.consignacao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form action="../v3/alterarMultiplasConsignacoes" method="post" name="form1">
    <% if (ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_NAO_SELECIONADOS_PARTICIPAM_ALT_MULT_CONTRATOS, responsavel)){ %>
        <input type="hidden" name="marCodigos" id="marCodigos" value="<%=marCodigos%>">
    <% } %>
     <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
    <% pageContext.setAttribute("autdes", autdesList); %>
    <hl:detalharADEv4 name="autdes" table="false" type="alterar" />
    <%-- Fim dos dados da ADE --%>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="mensagem.alterar.multiplo.consignacao.informe.valores"/></h2>
      </div>
      <div class="card-body">
        <div class="row">

          <% if (responsavel.isCseSupOrg()) { %>
            <div class="form-group col-md-3">
              <label for="totalAdeVlr"><hl:message key="rotulo.alterar.multiplo.consignacao.valor.total.novo"/> (<hl:message key="rotulo.moeda"/>)</label>
              <hl:htmlinput name="totalAdeVlr" di="totalAdeVlr" type="text" classe="form-control" size="8" mask="#F11" value="" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" onChange="ajustarCampos('1')"/>
            </div>
  
            <div class="form-group col-md-3">
              <label for="percentualMargem"><hl:message key="rotulo.alterar.multiplo.consignacao.percentual.margem"/> (%)</label>
              <hl:htmlinput name="percentualMargem" di="percentualMargem" type="text" classe="form-control" size="8" mask="#F11" value="<%=cemPorCento%>" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" onChange="ajustarCampos('2')"/>
            </div>
  
            <div class="form-group col-md-6">
              <label for="margemLimite"><hl:message key="rotulo.alterar.multiplo.consignacao.margem.limite"/></label>
              <select name="margemLimite" id="margemLimite" class="form-control form-select" onfocus="SetarEventoMascara(this,'#*200',true);" onblur="fout(this);ValidaMascara(this);" onChange="ajustarCampos('3')">
                <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                <%
                if (margensCheia != null) {
                   Iterator<MargemTO> it = margensCheia.iterator();
                   while (it.hasNext()) {
                       MargemTO margem = it.next();
                       if (margem.getMrsMargem() != null) {
                %>
                <option value="<%= margem.getMarCodigo() %>"><%= TextHelper.forHtmlContent(margem.getMarDescricao()) + " : " + ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + " " + NumberHelper.format(margem.getMrsMargem().doubleValue(), LocaleHelper.getLanguage()) %></option>
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
  
            <div class="form-group col-md-6">
              <label for="totalNovo"><hl:message key="rotulo.alterar.multiplo.consignacao.valor.total.novo.curto"/> (<hl:message key="rotulo.moeda"/>)</label>
              <input type="text" class="form-control" id="totalNovo" name="totalNovo" value="" disabled/>
            </div>

          <% } else { %>
            <div class="form-group col-md-6">
              <label for="totalAdeVlr"><hl:message key="rotulo.alterar.multiplo.consignacao.valor.total.novo"/> (<hl:message key="rotulo.moeda"/>)</label>
              <hl:htmlinput name="totalAdeVlr" di="totalAdeVlr" type="text" classe="form-control" size="8" mask="#F11" value="" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" onChange="ajustarCampos('1')"/>
              <input type="hidden" id="totalNovo" name="totalNovo" value="" />
            </div>

            <div class="form-group col-md-6">
              <label for="totalAtual"><hl:message key="rotulo.alterar.multiplo.consignacao.valor.total.atual"/> (<hl:message key="rotulo.moeda"/>)</label>
              <input type="text" class="form-control" id="totalAtual" name="totalAtual" value="<%=NumberHelper.format(totalAtual.doubleValue(), LocaleHelper.getLanguage())%>" disabled/>
            </div>
          <% } %>

          <div class="form-check form-group col-md-12 mt-2" role="radiogroup" aria-labelledby="alterarPrazo">
            <div class="form-group my-0">
              <span id="alterarPrazo"><hl:message key="rotulo.alterar.multiplo.consignacao.alterar.prazo.proporcional.capital.devido"/></span>
            </div>
            <div class="form-check form-check-inline mt-2">
              <input class="form-check-input ml-1" type="radio" name="alterarPrazo" id="alterarPrazoSim" value="true" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label pr-3" for="alterarPrazoSim">
                <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.sim"/></span>
              </label>
            </div>
            <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="alterarPrazo" id="alterarPrazoNao" value="false" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label" for="alterarPrazoNao">
                <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.nao"/></span>
              </label>
            </div>
          </div>

          <% if (responsavel.isCseSupOrg()) { %>
            <% if (!omitirAlterarIncidencia) { %>
              <div class="form-check form-group col-md-12 mt-2" role="radiogroup" aria-labelledby="alterarIncidencia" id="divAlterarIncidencia" style="display: none">
                <div class="form-group my-0">
                  <span id="alterarIncidencia"><hl:message key="rotulo.alterar.multiplo.consignacao.alterar.incidencia.margem"/></span>
                </div>
                <div class="form-check form-check-inline mt-2">
                  <input class="form-check-input ml-1" type="radio" name="alterarIncidencia" id="alterarIncidenciaSim" value="true" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label pr-3" for="alterarIncidenciaSim">
                    <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.sim"/></span>
                  </label>
                </div>
                <div class="form-check form-check-inline mt-2">
                <input class="form-check-input ml-1" type="radio" name="alterarIncidencia" id="alterarIncidenciaNao" value="false" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label" for="alterarIncidenciaNao">
                    <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.nao"/></span>
                  </label>
                </div>
              </div>
            <% } %>

            <% if (exibirRestaurarIncidencia) { %>
              <div class="form-check form-group col-md-12 mt-2" role="radiogroup" aria-labelledby="restaurarIncidencia" id="divRestaurarIncidencia" style="display: none">
                <div class="form-group my-0">
                  <span id="restaurarIncidencia"><hl:message key="rotulo.alterar.multiplo.consignacao.restaurar.incidencia.margem"/></span>
                </div>
                <div class="form-check form-check-inline mt-2">
                  <input class="form-check-input ml-1" type="radio" name="restaurarIncidencia" id="restaurarIncidenciaSim" value="true" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label pr-3" for="restaurarIncidenciaSim">
                    <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.sim"/></span>
                  </label>
                </div>
                <div class="form-check form-check-inline mt-2">
                  <input class="form-check-input ml-1" type="radio" name="restaurarIncidencia" id="restaurarIncidenciaNao" value="false" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label" for="restaurarIncidenciaNao">
                    <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.nao"/></span>
                  </label>
                </div>
              </div>
            <% } %>

            <div class="form-check form-group col-md-12 mt-2" role="radiogroup" aria-labelledby="bloquearServidor" id="divBloquearServidor">
              <div class="form-group my-0">
                <span id="bloquearServidor"><hl:message key="rotulo.alterar.multiplo.consignacao.bloquear.emprestimos.servidor"/></span>
              </div>
              <div class="form-check form-check-inline mt-2">
                <input class="form-check-input ml-1" type="radio" name="bloquearServidor" id="bloquearServidorSim" value="true" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label pr-3" for="bloquearServidorSim">
                  <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.sim"/></span>
                </label>
              </div>
              <div class="form-check form-check-inline mt-2">
                <input class="form-check-input ml-1" type="radio" name="bloquearServidor" id="bloquearServidorNao" value="false" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label" for="bloquearServidorNao">
                  <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.nao"/></span>
                </label>
              </div>
            </div>

            <% if (exibirDesbloquearServidor) { %>
              <div class="form-check form-group col-md-12 mt-2" role="radiogroup" aria-labelledby="desbloquearServidor" id="divDesbloquearServidor" style="display: none">
                <div class="form-group my-0">
                  <span id="desbloquearServidor"><hl:message key="rotulo.alterar.multiplo.consignacao.debloquear.emprestimos.servidor"/></span>
                </div>
                <div class="form-check form-check-inline mt-2">
                  <input class="form-check-input ml-1" type="radio" name="desbloquearServidor" id="desbloquearServidorSim" value="true" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label pr-3" for="desbloquearServidorSim">
                    <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.sim"/></span>
                  </label>
                </div>
                <div class="form-check form-check-inline mt-2">
                  <input class="form-check-input ml-1" type="radio" name="desbloquearServidor" id="desbloquearServidorNao" value="false" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                  <label class="form-check-label" for="desbloquearServidorNao">
                    <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.nao"/></span>
                  </label>
                </div>
              </div>
            <% } %>
            
            <% if (registroServidorDesbloqueado) { %>
                <div class="form-check form-group col-md-12 mt-2" role="radiogroup" aria-labelledby="bloquearRegistroServidor" id="divBloquearRegistroServidor">
                  <div class="form-group my-0">
                    <span id="bloquearRegistroServidor"><hl:message key="rotulo.alterar.multiplo.consignacao.bloquear.registro.servidor"/></span>
                  </div>
                  <div class="form-check form-check-inline mt-2">
                    <input class="form-check-input ml-1" type="radio" name="bloquearRegistroServidor" id="bloquearRegistroServidorSim" onClick="exibeCampoMotivoBloqueio(true)" value="true" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                    <label class="form-check-label pr-3" for="bloquearRegistroServidorSim">
                      <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.sim"/></span>
                    </label>
                  </div>
                  <div class="form-check form-check-inline mt-2">
                    <input class="form-check-input ml-1" type="radio" name="bloquearRegistroServidor" id="bloquearRegistroServidorNao" onClick="exibeCampoMotivoBloqueio(false)" value="false" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                    <label class="form-check-label" for="bloquearRegistroServidorNao">
                      <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.nao"/></span>
                    </label>
                  </div>
                </div>
                <div class="form-group col-sm" id="divMotivoBloqueio">
                   <label for="motivoBloqueioRegistroServidor"><hl:message key="rotulo.registro.servidor.motivo.bloqueio"/><hl:message key="rotulo.campo.opcional"/>:</label>
                   <textarea class="form-control" id="motivoBloqueioRegistroServidor" name="motivoBloqueioRegistroServidor"></textarea>
                </div>
            <% } else { %>
                <div class="form-check form-group col-md-12 mt-2" role="radiogroup" aria-labelledby="desbloquearRegistroServidor" id="divDesbloquearRegistroServidor">
                  <div class="form-group my-0">
                    <span id="desbloquearRegistroServidor"><hl:message key="rotulo.alterar.multiplo.consignacao.desbloquear.registro.servidor"/></span>
                  </div>
                  <div class="form-check form-check-inline mt-2">
                    <input class="form-check-input ml-1" type="radio" name="desbloquearRegistroServidor" id="desbloquearRegistroServidorSim" value="true" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                    <label class="form-check-label pr-3" for="desbloquearRegistroServidorSim">
                      <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.sim"/></span>
                    </label>
                  </div>
                  <div class="form-check form-check-inline mt-2">
                    <input class="form-check-input ml-1" type="radio" name="desbloquearRegistroServidor" id="desbloquearRegistroServidorNao" value="false" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                    <label class="form-check-label" for="desbloquearRegistroServidorNao">
                      <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.nao"/></span>
                    </label>
                  </div>
                </div>
            <%} %>
          <% } %>

          <% if (permiteReverterValor) { %>
            <div class="form-check form-group col-md-12 mt-2" role="radiogroup" aria-labelledby="restaurarValor">
              <div class="form-group my-0">
                <span id="restaurarValor"><hl:message key="rotulo.alterar.multiplo.consignacao.restaurar.valor.ultima.alteracao"/></span>
              </div>
              <div class="form-check form-check-inline mt-2">
                <input class="form-check-input ml-1" type="radio" name="restaurarValor" id="restaurarValorSim" value="true" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" onChange="ajustarCampos('4')">
                <label class="form-check-label pr-3" for="restaurarValorSim">
                  <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.sim"/></span>
                </label>
              </div>
              <div class="form-check form-check-inline mt-2">
                <input class="form-check-input ml-1" type="radio" name="restaurarValor" id="restaurarValorNao" value="false" checked onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" onChange="ajustarCampos('4')">
                <label class="form-check-label" for="restaurarValorNao">
                  <span class="text-nowrap labelSemNegrito align-text-top"><hl:message key="rotulo.nao"/></span>
                </label>
              </div>
            </div>
          <% } %>

        </div>
      </div>
    </div>
    <%= SynchronizerToken.generateHtmlToken(request) %>     
    <hl:htmlinput name="adeList" type="hidden" di="adeList"  value="<%=TextHelper.forHtmlAttribute(chkAdeCodigo)%>" />    
    <hl:htmlinput name="adeNaoSelecionadas" type="hidden" di="adeNaoSelecionadas"  value="<%=!TextHelper.isNull(adeNaoSelecionadas) ? TextHelper.forHtmlAttribute(adeNaoSelecionadas) : ""%>" />    
    <hl:htmlinput name="acao"    type="hidden" di="acao"     value="validar" /> 
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" id="validar" onClick="alterarContratos(); return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consultar"></use></svg><hl:message key="rotulo.botao.validar"/></a>
    </div>
  </form>
</body>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript">
f0 = document.forms[0];

<%
if (margens != null) {
    out.println("var margemMap = new Map();");
    Iterator<MargemTO> it = margens.iterator();
    while (it.hasNext()) {
        MargemTO margem = it.next();
        if (margem.getMrsMargem() != null) {
            out.println("margemMap.set('" + margem.getMarCodigo() + "', " + margem.getMrsMargem() + ");");
        }
    }
}
%>
</script>
<script type="text/JavaScript">
function ajustarCampos(campo) {
    if (campo == '1') {
        if ($('#totalAdeVlr').val()) {
            $('#percentualMargem').val('');
            $('#margemLimite').val('');
            $('#totalNovo').val(FormataContabil($('#totalAdeVlr').val(), 2));
        } else {
            $('#percentualMargem').val('<%=cemPorCento%>');
            $('#totalNovo').val('');
        }
    } else if (campo == '2' || campo == '3') {
        if ($('#percentualMargem').val() && $('#margemLimite').val()) {
            $('#totalNovo').val(FormataContabil(parse_num($('#percentualMargem').val()) * margemMap.get($('#margemLimite').val()) / 100.00, 2));
        } else {
            $('#totalNovo').val('');
        }
        <% if (!omitirAlterarIncidencia) { %>
        if ($('#margemLimite').val()) {
            $("#divAlterarIncidencia").show();
        } else {
            $("#divAlterarIncidencia").hide();
        }
        <% } %>
    } else if (campo == '4') {
    	if ($('#restaurarValorSim').is(':checked')) {
    		$("#totalAdeVlr").val('');
            $('#percentualMargem').val('');
            $('#margemLimite').val('');
            $("#totalAdeVlr").prop("disabled", true);
            $("#margemLimite").prop("disabled", true);
            $("#percentualMargem").prop("disabled", true);
            $("#divBloquearServidor").hide();
            $("#divDesbloquearServidor").show();
            <% if (!omitirAlterarIncidencia) { %>
            $("#divAlterarIncidencia").hide();
            <% } %>
            $("#divRestaurarIncidencia").show();
    	} else {
            $("#totalAdeVlr").prop("disabled", false);
            $("#margemLimite").prop("disabled", false);
            $("#percentualMargem").prop("disabled", false);
            $("#divBloquearServidor").show();
            $("#divDesbloquearServidor").hide();
            $("#divRestaurarIncidencia").hide();
    	}
    }
}

function alterarContratos(acao) {
    if (!f0.totalAdeVlr.disabled && !getFieldValue(f0.totalNovo)) {
        alert('<hl:message key="mensagem.informe.valor.total.novo"/>');
        return false;
    }
   
    <% if (permiteReverterValor && !TextHelper.isNull(msgAlertaAlteracaoPosterior)) { %>
    if (!confirm('<%= TextHelper.forJavaScriptBlock(msgAlertaAlteracaoPosterior) %>')) {
        return false;
    }
    <% } %>
    
    <%if(responsavel.isCseSupOrg() && !registroServidorDesbloqueado){ %>
    	if(document.getElementById("desbloquearRegistroServidorSim").checked){
          if (!confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.alterar.multiplo.consignacao.desbloquear.registro.servidor", responsavel)%>')) {
              return false;
          }
    	}
    <%}%>
    
    f0.totalNovo.disabled = false;
    f0.submit();
}

function exibeCampoMotivoBloqueio(acao) {
	if(acao){
		$("#divMotivoBloqueio").show();
	} else {
		$("#divMotivoBloqueio").hide();
	}
}

function formLoad() {
	<%if(responsavel.isCseSupOrg() && registroServidorDesbloqueado){ %>
		$("#divMotivoBloqueio").hide();
	<%}%>
    focusFirstField();
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