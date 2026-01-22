<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.web.RegistrarOcorrenciaDTO"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

RegistrarOcorrenciaDTO registrarOcorrenciaDTO = (RegistrarOcorrenciaDTO) request.getAttribute("registrarOcorrenciaDTO");
List<TransferObject> motivoOperacaoLst = (List<TransferObject>) request.getAttribute("motivoOperacaoLst");

%>
<c:set var="imageHeader">
    <use xlink:href="#i-beneficios"></use>  
</c:set>
<c:set var="title">
    <hl:message key="rotulo.registrar.ocorrencia.contrato.beneficio"/>
</c:set>
<c:set var="bodyContent">
<form method="post" action="../v3/registrarOcorrenciaContratoBeneficio?acao=salvar&_skip_history_&<%=SynchronizerToken.generateToken4URL(request)%>">
  <input type="hidden" name="cbeCodigo" value="<%=registrarOcorrenciaDTO.getCbeCodigo()%>"> 
  <div class="row">
    <div class="col-sm-5">
      <div class="card">
      	<div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.registrar.ocorrencia.contrato.beneficio.dados.contrato"/></h2>
        </div>
        <div class="card-body">
          <dl class="row data-list">
          	<dt class="col-6"><hl:message key="rotulo.registrar.ocorrencia.contrato.nome.beneficiario" />: </dt>
          	<dd class="col-6"><%=TextHelper.forHtmlContent(registrarOcorrenciaDTO.getBfcNome())%></dd>
          	<dt class="col-6"><hl:message key="rotulo.registrar.ocorrencia.contrato.nome.servidor" />: </dt>
          	<dd class="col-6"><%=TextHelper.forHtmlContent(registrarOcorrenciaDTO.getSerNome())%></dd>
            <dt class="col-6"><hl:message key="rotulo.beneficio.codigo.plano" />: </dt>
            <dd class="col-6"><%=registrarOcorrenciaDTO.getBenCodigo()%></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.beneficios.descricao"/>: </dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(registrarOcorrenciaDTO.getBenDescricao())%></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.beneficios.operadora"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(registrarOcorrenciaDTO.getCsaNome())%></dd>
            <dt class="col-6"><hl:message key="rotulo.beneficio.codigo.contrato" />: </dt>
            <dd class="col-6"><%=registrarOcorrenciaDTO.getBenCodigoContrato()%></dd>
            <dt class="col-6"><hl:message key="rotulo.beneficio.codigo.registro" />: </dt>
            <dd class="col-6"><%=registrarOcorrenciaDTO.getBenCodigoRegistro()%></dt>
          </dl>
        </div>
      </div>
    </div>
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.validar.servidor.dados.operacao"/></h2>
        </div>
        <div class="card-body">
        	<div class="row">
			  	<div class="form-group col-12">
					<label for="TMO_CODIGO"><hl:message key="rotulo.registrar.ocorrencia.contrato.motivo.operacao"/></label>
					<SELECT CLASS="form-control" NAME="tmoCodigo" ID="tmoCodigo" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
						<OPTION VALUE=""><hl:message key="rotulo.registrar.ocorrencia.contrato.selecione" /></OPTION>
						<%
						for (TransferObject transferObject : motivoOperacaoLst) {
						%>
						<OPTION VALUE="<%=transferObject.getAttribute(Columns.TMO_CODIGO)%>"><%=TextHelper.forHtmlContent(transferObject.getAttribute(Columns.TMO_DESCRICAO))%></OPTION>
						<%
						}
						%>
					</SELECT>
				</div>
			</div>
			<div class="row">
				<div class="form-group col-12">
					<label for="ocbObs"><hl:message key="rotulo.registrar.ocorrencia.contrato.observacao"/></label>
					<textarea name="ocbObs" id="ocbObs" rows="5" class="form-control"></textarea>
				</div>
			</div>
        </div>
      </div>
    </div> 
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.registrar.ocorrencia.contrato.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" onclick="if(validarForm()){f0.submit();} return false;"><hl:message key="rotulo.registrar.ocorrencia.contrato.confirmar"/></a>
  </div> 
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
	var f0 = document.forms[0];

	function validarForm() {

		if (!f0.tmoCodigo || !f0.tmoCodigo.value) {
			alert('<hl:message key="mensagem.registrar.ocorrencia.contrato.motivo.operacao.obrigatorio"/>')
			return false;
		}

		if (!f0.ocbObs || !f0.ocbObs.value.trim()) {
			alert('<hl:message key="mensagem.registrar.ocorrencia.contrato.observacao.obrigatorio"/>')
			return false;
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
