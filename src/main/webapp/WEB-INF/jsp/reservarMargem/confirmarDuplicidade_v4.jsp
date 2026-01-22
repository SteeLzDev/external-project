<%--
* <p>Title: autorizar.jsp</p>
* <p>Description: Página que autoriza a reserva da margem do servidor através de sua senha</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.math.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.web.ResultadoSimulacao"%>
<%@ page import="java.util.Map.Entry" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<c:set var="title">
<%=TextHelper.forHtmlContent(request.getAttribute("tituloPagina"))%>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="form1">
  <%= SynchronizerToken.generateHtmlToken(request) %>
  <hl:htmlinput type="hidden" name="acao" value="confirmarDuplicidade" />
  <hl:htmlinput type="hidden" name="telaConfirmacaoDuplicidade" value="S" />
  
  <% 

	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  	Map<String, String[]> map = new HashMap<String, String[]>();
  	map.putAll(request.getParameterMap());
  	map.remove("eConsig.page.token");
	
	for (Entry<String, String[]> entry : map.entrySet()) {
		
		String key = entry.getKey();
		String[] values = entry.getValue();
		
		for(int count = 0; count < values.length; count++ ) {
		%>
  			<input type="hidden" name="<%=key%>" value="<%=values[count]%>"/>
		<%
		}
  	}
  %>
  
	<div class="alert alert-warning">
	 	<hl:message key="mensagem.alerta.ade.duplicidade.permitir.motivada.usuario.web"/>
	</div>
	<div class="row">
		<div class="col-sm-5">
			<div class="card">
				<div class="card-header hasIcon">
					<span class="card-header-icon">
						<svg width="26">
							<use xlink:href="#i-operacional"></use>
						</svg>
					</span>
					<h2 class="card-header-title">
						<hl:message key="rotulo.duplicidade.confirme.duplicidade" />
					</h2>
				</div>
				<div class="card-body">
					<div class="row">
						<div class="col">
							<div class="form-check">
								<label class="form-check-label"> 
									<input id="chkConfirmarDuplicidade" name="chkConfirmarDuplicidade" type="checkbox" class="form-check-input" onchange="verificarConfirmacaoDuplicidade(this);">
									<hl:message key="mensagem.alerta.ade.duplicidade.confirmacao.checkbox" />
								</label>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-sm">
			<div class="card">
				<div class="card-header hasIcon">
					<span class="card-header-icon">
						<svg width="26">
							<use xlink:href="#i-operacional"></use>
						</svg>
					</span>
					<h2 class="card-header-title">
						<hl:message key="rotulo.duplicidade.informe.motivo" />
					</h2>
				</div>
				<div class="card-body">
					<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.autorizacao", responsavel)%>" inputSizeCSS="col-sm-12"/>
				</div>
			</div>
		</div>
	</div>
	<div class="btn-action">
		<a class="btn btn-outline-danger" href="#no-back"  onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
		<a class="btn btn-primary" id="btnEnvia" href="#no-back" onClick="validarEntradaDados(); return false;"><hl:message key="rotulo.acoes.confirmar"/></a>
	</div>
</form>
</c:set>
<c:set var="javascript">
<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" scriptOnly="true" />
<script language="JavaScript" type="text/JavaScript">
f0 = document.forms[0];

function verificarConfirmacaoDuplicidade (e) {
	if (e.checked) {
		document.getElementById('TMO_CODIGO').removeAttribute('disabled');
		document.getElementById('ADE_OBS').removeAttribute('disabled');
	} else {
		document.getElementById('TMO_CODIGO').setAttribute('disabled', '');
		document.getElementById('ADE_OBS').setAttribute('disabled', '');
		document.getElementById('TMO_CODIGO').value = '';
		document.getElementById('ADE_OBS').value = '';
	}
}

function validarEntradaDados () {

	if (!document.getElementById('chkConfirmarDuplicidade').checked) {
		alert('<hl:message key="mensagem.alerta.ade.duplicidade.marcar.checkbox"/>');
		document.getElementById('chkConfirmarDuplicidade').focus();
		return false;
	}

	if (!document.getElementById('TMO_CODIGO').value) {
		alert('<hl:message key="mensagem.alerta.ade.duplicidade.motivo"/>');
		document.getElementById('TMO_CODIGO').focus();
		return false;
	}

	if (!document.getElementById('ADE_OBS').value || document.getElementById('ADE_OBS').value.trim() == '') {
		alert('<hl:message key="mensagem.alerta.ade.duplicidade.observacao"/>');
		document.getElementById('ADE_OBS').focus();
		return false;
	}

	f0.submit();
	
}

// Inicia com os campos desabilitados
document.getElementById('TMO_CODIGO').setAttribute('disabled', '');
document.getElementById('ADE_OBS').setAttribute('disabled', '');

</script>

</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
