<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String adeCodigo = (String) request.getAttribute("adeCodigo");
Boolean exigeMotivo = (Boolean) request.getAttribute("exigeMotivo");
Boolean permiteAlterarNumeroAde = (Boolean) request.getAttribute("permiteAlterarNumeroAde");
Boolean permiteReducaoValorParcela = (Boolean) request.getAttribute("permiteReducaoValorParcela");
String labelTipoVlr = (String) request.getAttribute("labelTipoVlr");
String adeVlrPrevisto = (String) request.getAttribute("adeVlrPrevisto");
Set<Date> periodos = (Set<Date>) request.getAttribute("periodos");
%>
<c:set var="title">
  <hl:message key="rotulo.efetiva.acao.consignacao.reimplantar"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form action="../v3/reimplantarConsignacao?acao=reimplantar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
    <div class="row">
      <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
      <hl:detalharADEv4 name="autdes" table="false" type="confirmar" scope="request" />
      <%-- Fim dos dados da ADE --%>
    </div>
    <div class="col-sm p-0">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message key="rotulo.efetiva.acao.consignacao.dados.operacao" />
          </h2>
        </div>
        <div class="card-body">
    <% if (permiteAlterarNumeroAde) { %>
			<div class="form-group mb-1 col-sm-12 col-md-12" role="radiogroup">
			  <span for="alterarNumeroAde"><hl:message key="mensagem.confirmacao.reimplantacao.alterar.ade.numero"/></span><br>
			  <div class='form-check form-check-inline'>
			    <input type="radio" class="form-check-input" id="alterarNumeroAdeSim" name="alterarNumeroAde" value="true"/>
			    <label for="alterarNumeroAdeSim" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.sim"/></label>
			  </div>
			  <div class='form-check form-check-inline'>  
				<input type="radio" class="form-check-input" id="alterarNumeroAdeNao" name="alterarNumeroAde" value="false" checked/> 
			    <label for="alterarNumeroAdeNao" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.nao"/></label>
			  </div>  
			</div>
    <% } %>
    <% if (permiteReducaoValorParcela) { %>   
		  <div class="form-group mb-1 col-sm-12 col-md-12" role="radiogroup">
            <span for="reduzirValorAde"><hl:message key="mensagem.confirmacao.reimplantacao.reduzir.ade.valor"/></span><br>
			<div class='form-check form-check-inline'>
              <input type="radio" class="form-check-input ml-1" id="reduzirValorAdeSim" name="reduzirValorAde" value="true"/>
              <label for="reduzirValorAdeSim" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.sim"/></label>
            </div>
			<div class='form-check form-check-inline'>
			  <input type="radio" class="form-check-input ml-1" id="reduzirValorAdeNao" name="reduzirValorAde" value="false" checked/> 
              <label for="reduzirValorAdeNao" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.nao"/></label>
              <span class="rotulo"><hl:message key="mensagem.confirmacao.reimplantacao.ade.valor.estimado"/>(<%=TextHelper.forHtmlContent(labelTipoVlr)%>): </span> <%=TextHelper.forHtmlContent(adeVlrPrevisto)%>                              
            </div>  
          </div>
    <% } %>
    <% if (exigeMotivo) { %>           
        <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
        <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reimplantacao", responsavel)%>" />
        <%-- Fim dos dados do Motivo da Operação --%>
    <% }  else { %>
        <div class="row">
          <div class="form-group col-sm">
            <label for="obs"><hl:message key="rotulo.efetiva.acao.consignacao.dados.observacao"/></label>
            <textarea class="form-control" 
                    placeholder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs", responsavel)%>'
                    id="obs" 
                    name="obs" 
                    rows="6"></textarea>
          </div>
        </div>
  <% } %> 
  <% if (periodos != null && !periodos.isEmpty()) { %>
        <div class="row">
          <div class="form-group col-sm">
            <label for="iNumPrestaRestante"><hl:message key="rotulo.folha.periodo"/></label>
            <select class="form-control" id="ocaPeriodo" name="ocaPeriodo" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
              <% for (Date periodo : periodos) { %>
                <option value="<%=TextHelper.forHtmlAttribute(periodo)%>"><%=TextHelper.forHtmlContent(DateHelper.toPeriodString(periodo))%></option>
              <% } %>
            </select>
          </div>
        </div>
  <% } %>    
      </div>
     </div>
    </div>            
    <div class="btn-action"> 
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" id="btnEnvia" href="#no-back" onClick="if (validaOperacao()) { f0.submit();} return false;"><hl:message key="rotulo.botao.confirmar"/></a> 
    </div>
    <hl:htmlinput name="MM_update" type="hidden" value="form1" />    
    <hl:htmlinput name="ADE_CODIGO" type="hidden" di="ADE_CODIGO" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" />  
  </form>
</c:set>
<c:set var="javascript">
	<% if (exigeMotivo) { %>
	    <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reimplantacao", responsavel)%>" scriptOnly="true" />
	<% } %>
  <script type="text/JavaScript">
    function validaOperacao() {
      var msg = '<hl:message key="mensagem.confirmacao.reimplantacao"/>';
      <% if (exigeMotivo) { %>
      var comboTMO = f0.TMO_CODIGO.value;   
      if (comboTMO == null || comboTMO == '') {
        alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
        f0.TMO_CODIGO.focus();
        return false;
      }    
      <% } %>
    
      return confirm(msg);
    }
  
    f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>