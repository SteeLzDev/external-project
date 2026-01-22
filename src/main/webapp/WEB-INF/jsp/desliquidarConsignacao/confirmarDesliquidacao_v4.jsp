<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> autdesList = (List<TransferObject>) request.getAttribute("autdesList");
boolean exigeMotivo = (Boolean) request.getAttribute("exigeMotivo");
boolean exportacaoInicial = (Boolean) request.getAttribute("exportacaoInicial");
boolean marFicaraNegativa = (Boolean) request.getAttribute("marFicaraNegativa");
boolean adeJaEnviadaFolha = (Boolean) request.getAttribute("adeJaEnviadaFolha");
%>
<c:set var="title">
  <hl:message key="rotulo.desliquidar.consignacao.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"><hl:message key="rotulo.efetiva.acao.consignacao.confDesliquidacao"/></use>
</c:set>

<c:set var="bodyContent">
  <form action="../v3/desliquidarConsignacao" method="post" name="form1">
    <input type="hidden" name="acao" value="desliquidar">
    <input type="hidden" name="_skip_history_" value="true">
    <%=SynchronizerToken.generateHtmlToken(request)%>
    <div class="row firefox-print-fix">
    <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
    <% pageContext.setAttribute("autdes", autdesList); %>
    <hl:detalharADEv4 name="autdes" table="false" type="desliquidar" divSizeCSS="col-sm-12" />
      <%-- Fim dos dados da ADE --%>     
    </div>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.efetiva.acao.consignacao.dados.operacao"/></h2>
      </div>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-12 col-md-6" aria-labelledby="validaMargem">
          <label for="validaMargem"><hl:message key="rotulo.desliquidar.consignacao.validar.margem"/></label>
          <INPUT TYPE="radio" NAME="VALIDA_MARGEM" id="simValidaMargem" VALUE="true" CHECKED><label for="simValidaMargem"><hl:message key="rotulo.sim"/></label>
          <INPUT TYPE="radio" NAME="VALIDA_MARGEM" id="naoValidaMargem" VALUE="false"><label for="naoValidaMargem"><hl:message key="rotulo.nao"/></label>
        </div>
        <% if (exportacaoInicial) { %>
        <div class="form-group col-sm-12 col-md-6" aria-labelledby="REIMPLANTAR">
          <label for="REIMPLANTAR"><hl:message key="rotulo.desliquidar.consignacao.reimplantar"/></label>
          <INPUT TYPE="radio" NAME="REIMPLANTAR" id="simReimplantar" VALUE="true" CHECKED><label for="simReimplantar"><hl:message key="rotulo.sim"/></label>
          <INPUT TYPE="radio" NAME="REIMPLANTAR" id="naoReimplantar" VALUE="false"><label for="naoReimplantar"><hl:message key="rotulo.nao"/></label>
        </div>
        <% } %>
      </div>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6" aria-labelledby="COMPRA">
          <label for="COMPRA"><hl:message key="rotulo.desliquidar.consignacao.ignorar.origem.compra"/></label>
          <INPUT TYPE="radio" NAME="COMPRA" id="simCompra" VALUE="true" CHECKED><label for="simCompra"><hl:message key="rotulo.sim"/></label>
          <INPUT TYPE="radio" NAME="COMPRA" id="naoCompra" VALUE="false"><label for="naoCompra"><hl:message key="rotulo.nao"/></label>
        </div>
      </div>
     <div class="row">
      <div class="form-group col-sm-12 col-md-6 mt-2">
            <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.desliquidacao", responsavel)%>" inputSizeCSS="col-sm-12"/>
      </div>
     </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" onclick="if (vfChecks()) { f0.submit();} return false;"><hl:message key="rotulo.botao.confirmar"/></a>
    </div>
    <% for (int i = 0; i < autdesList.size(); i++) { %>
      <hl:htmlinput name="chkADE" type="hidden" value="<%=TextHelper.forHtmlAttribute(autdesList.get(i).getAttribute(Columns.ADE_CODIGO))%>"/>
    <% } %>
  </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">
  var f0 = document.forms[0];
  function vfChecks() {
    var radioVldMargem = document.getElementById("valida_false");
    var comboTMO = f0.TMO_CODIGO.value; 
    var msg = '<hl:message key="mensagem.confirmacao.desliquidacao"/>';
    
    <% if (exigeMotivo || adeJaEnviadaFolha) { %>       
        if (comboTMO == '' || comboTMO == null ) {
        alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
        f0.TMO_CODIGO.focus();
        return false;
      }    
      <% } %>
    
    if (radioVldMargem != null && radioVldMargem.checked) {
      if (comboTMO == '' || comboTMO == null ) {
          alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
          f0.TMO_CODIGO.focus();
          return false;
      }
    }

    if (!confirmaAcaoConsignacao()) {
        return false;
    }

    <% if (exportacaoInicial) { %>
        //var radioReimplante = f0.REIMPLANTAR.value;
        var radioReimplante = document.getElementById("reimplante_true");
        
        if (radioReimplante != null && radioReimplante.checked) {
        if (comboTMO == '' || comboTMO == null ) {
            alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
            f0.TMO_CODIGO.focus();
            return false;
        }
      }
    <% } %>
    
    <% if (marFicaraNegativa) { %>
        if (radioVldMargem != null && radioVldMargem.checked) {
           msg += ' <hl:message key="mensagem.desliquidar.consignacao.margem.negativa"/>'; 
        }
    <% } %>
    
    <% if (adeJaEnviadaFolha) { %>
        msg += ' <hl:message key="mensagem.desliquidar.consignacao.liquidacao.enviada"/>';
    <% } %>
    
    if (confirm(msg)) {
      return true;
    } else {
      return false;
    }
  }
  </script>
  <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" scriptOnly="true" />
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>