<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.persistence.entity.InformacaoCsaServidor"%>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
InformacaoCsaServidor infoServidor = (InformacaoCsaServidor) request.getAttribute("infoServidor");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String serCodigo = (String) request.getAttribute("serCodigo");
%>
<c:set var="title">
  <hl:message key="<%= infoServidor != null ? "rotulo.informacao.csa.servidor.editar.informacao" : "rotulo.informacao.csa.servidor.adicionar.informacao" %>" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">
      <form method="post" action="../v3/manterInformacaoCsaServidor?acao=salvar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
        <input type="hidden" name="CSA_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" />
        <input type="hidden" name="SER_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(serCodigo)%>" />
        <input type="hidden" name="ICS_CODIGO" VALUE="<%=infoServidor != null ? TextHelper.forHtmlAttribute(infoServidor.getIcsCodigo()) : ""%>" />
        <!-- Dados básicos relatório -->
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title">
              <hl:message key="<%= infoServidor != null ? "rotulo.informacao.csa.servidor.editar.informacao" : "rotulo.informacao.csa.servidor.adicionar.informacao" %>" /></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-12 col-md-12 mt-12">
                <label for="ICS_VALOR"><hl:message key="rotulo.informacao.csa.servidor.lista.valor" /></label>
                <hl:htmlinput name="ICS_VALOR" di="ICS_VALOR" onFocus="SetarEventoMascara(this,'#*65000',true);" type="textarea" classe="form-control" onBlur="fout(this);ValidaMascara(this);" rows="15" cols="80" value="<%=TextHelper.forHtmlAttribute(infoServidor != null ? infoServidor.getIcsValor() : "")%>" />
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar" /></a>
          <a class="btn btn-primary" id="btnConfirmar" href="#no-back" onClick="if(verificaCampos()){f0.submit();} return false;"><hl:message key="rotulo.botao.confirmar" /></a>
        </div>
      </form>
    </div>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
    function formLoad() {
    	f0 = document.forms[0];
    }
    function verificaCampos() {
    	var controles = new Array("ICS_VALOR");
    	var msgs = new Array("<hl:message key='rotulo.informacao.csa.servidor.campo.valor'/>");
    
    	if (!ValidaCampos(controles, msgs)) {
    		return false;
    	}
    
    f0.submit();
    	return false;
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
