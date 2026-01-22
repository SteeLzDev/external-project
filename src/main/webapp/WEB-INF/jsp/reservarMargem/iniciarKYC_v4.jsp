<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
%>

<c:set var="title">
<hl:message key="rotulo.solicitar.consignacao.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
    <form action="../v3/executarKYC" method="post" name="form1">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><%= ApplicationResourcesHelper.getMessage("mensagem.kyc.cadastrar.pan.number", responsavel)%>
       </p>
      </div>
      <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="26"><use xlink:href="#i-consignacao"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.servidor.dados" /></h2>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="form-group col-sm-12">
              <label for="panNumber"><hl:message key="rotulo.kyc.pan.number"/></label>
              <hl:htmlinput name="panNumber" type="text" classe="form-control" di="panNumber" size="32" mask="#*32" nf="btnEnvia" value="" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.pan.number", responsavel)%>'/>
            </div>
          </div>
        </div>
        <%= SynchronizerToken.generateHtmlToken(request) %>
        <hl:htmlinput type="hidden" name="acao" value="salvarPanNumber" />
        <hl:htmlinput type="hidden" name="CSA_CODIGO" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" />
        <hl:htmlinput type="hidden" name="SVC_CODIGO" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>" />
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="if(campos()) { f0.submit(); } return false;" alt="<hl:message key="rotulo.botao.confirmar"/>" title="<hl:message key="rotulo.botao.confirmar"/>"><svg width="17"><use xlink:href="#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
      </div>
    </form>
</c:set>
<c:set var="javascript">
<script>
f0 = document.forms[0];

function campos() {
  if (f0.panNumber != null && f0.panNumber.disabled == false) {
    if (f0.panNumber.value == '') {
      alert('<hl:message key="mensagem.erro.kyc.informe.pan.number"/>');
      f0.panNumber.focus();
      return false;
    }
  }
  return true;
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>