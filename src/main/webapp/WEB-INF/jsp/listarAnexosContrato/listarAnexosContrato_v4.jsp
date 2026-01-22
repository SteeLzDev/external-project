<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String csaCodigo = (String) JspHelper.verificaVarQryStr(request, "csaCodigo");
String tipo = (String) JspHelper.verificaVarQryStr(request, "tipo");
List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
String fieldValueCsa = Columns.CSA_CODIGO;
String fieldLabelCsa = Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR;
String fieldValueSvc = Columns.SVC_CODIGO;
String fieldLabelSvc = Columns.SVC_DESCRICAO + ";" + Columns.SVC_IDENTIFICADOR;
String fieldValueSad = Columns.SAD_CODIGO;
String rotuloNenhum = ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum", responsavel);
String svcCodigo = (String) JspHelper.verificaVarQryStr(request, "svcCodigo");
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("servicos");
String periodo = (String) JspHelper.verificaVarQryStr(request, "periodo");
String periodoIni = (String) JspHelper.verificaVarQryStr(request, "periodoIni");
String periodoFim = (String) JspHelper.verificaVarQryStr(request, "periodoFim");
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

String valuePeriodo = "";
String others = "";
if (!TextHelper.isNull(periodo)) {
    valuePeriodo = periodo;
    others = "disabled";
}  

String valueIni = "";
if (!TextHelper.isNull(periodoIni)) {
    valueIni = periodoIni;
    others = "disabled";
}
      
String valueFim = "";
if (!TextHelper.isNull(periodoFim)) {
    valueFim = periodoFim;
}
%>
<c:set var="title">
	<hl:message key="rotulo.listar.arquivos.download.anexo.contrato.titulo" />
</c:set>
<c:set var="imageHeader">
	<use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
	<form method="post"
		action="../v3/downloadAnexosContrato?acao=anexoContratoZip&<%=SynchronizerToken.generateToken4URL(request)%>"
		name="form1" id="form1">
		<div class="card">
			<div class="card-header">
				<h2 class="card-header-title">
					<hl:message key="rotulo.acao.filtrar.por" />
				</h2>
			</div>
			<div class="card-body">
				<div class="row">
					<div class="form-group col-sm-12 col-md-6">
						<label id="lblConsignataria" for="csaCodigo"><hl:message key="rotulo.consignataria.singular" /></label>
						<%=JspHelper.geraCombo(consignatarias, "csaCodigo", fieldValueCsa, fieldLabelCsa, rotuloNenhum, "", (TextHelper.isNull(csaCodigo) && !desabilitado), 3, csaCodigo, null, desabilitado, "form-control")%>
					</div>
					<div class="form-group col-sm-12 col-md-6">
						<label id="lblServicoSvcPage" for="svcCodigo"><hl:message key="rotulo.servico.singular" /></label>
						<%=JspHelper.geraCombo(servicos, "svcCodigo", fieldValueSvc, fieldLabelSvc, rotuloNenhum, "", (TextHelper.isNull(svcCodigo) && !desabilitado), 3, svcCodigo, null, desabilitado, "form-control")%>
					</div>
				</div>
				<div class="row">
					<div class="form-group col-sm-12 col-md-6">
						<span id="dataInclusao"><%=ApplicationResourcesHelper.getMessage("rotulo.relatorio.periodo", responsavel)%></span>
						<div class="row" role="group" aria-labelledby="dataInclusao">
							<div class="col-sm-6">
								<div class="row">
									<div class="form-check col-sm-2 col-md-2">
										<div class="float-left align-middle mt-4 form-control-label">
											<label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.data.de" /></label>
										</div>
									</div>
									<div class="form-check col-sm-10 col-md-10">
										<hl:htmlinput name="periodoIni" di="periodoIni" type="text"
											value="<%=TextHelper.forHtmlAttribute(valueIni )%>"
											classe="form-control w-100" size="10"
											mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
											placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
									</div>
								</div>
							</div>
							<div class="col-sm-6">
								<div class="row">
									<div class="form-check col-sm-2 col-md-2">
										<div class="float-left align-middle mt-4 form-control-label">
											<label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate" /></label>
										</div>
									</div>
									<div class="form-check col-sm-10 col-md-10">
										<hl:htmlinput name="periodoFim" di="periodoFim" type="text"
											value="<%=TextHelper.forHtmlAttribute(valueFim )%>"
											classe="form-control w-100" size="10"
											mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
											placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12 col-md-12">
						<h3 class="legend">
							<span id="situacaoContrato"><hl:message key="rotulo.consignacao.status.contrato" /></span>
						</h3>
						<hl:filtroStatusAdeTagv4 />
					</div>
				</div>
			</div>
		</div>
		<div class="btn-action d-print-none">
			<a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar" /></a> <a class="btn btn-primary" href="#no-back" onClick="gerarArquivo()"> <svg width="20">
                <use xlink:href="#i-consultar"></use></svg> <hl:message key="rotulo.botao.gerar.arquivo" />
			</a>
		</div>
		<div class="card">
			<div class="card-header hasIcon">
				<span class="card-header-icon"><svg width="25">
						<use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
				<h2 class="card-header-title">
					<hl:message key="mensagem.listar.arquivos.download.anexo.contrato.disponiveis.download" />
				</h2>
			</div>
			<div class="card-body table-responsive ">
				<table class="table table-striped table-hover">
					<thead>
						<tr>
							<th scope="col"><hl:message key="rotulo.arquivo.nome" /></th>
							<th scope="col"><hl:message key="rotulo.arquivo.tamanho" /></th>
							<th scope="col"><hl:message key="rotulo.arquivo.data" /></th>
							<th scope="col" width="10%"><hl:message key="rotulo.acoes" /></th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${empty arquivos}">
								<tr>
									<td colspan="7"><hl:message key="mensagem.listar.arquivos.download.anexo.contrato.nenhum.arquivo.encontrado" /></td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach items="${arquivos}" var="arquivo">
									<tr>
										<td>${fl:forHtmlContent(arquivo.nomeOriginal)}</td>
										<td>${fl:forHtmlContent(arquivo.tamanho)}</td>
										<td>${fl:forHtmlContent(arquivo.data)}</td>
										<td><a href="#no-back" onClick="postData('../v3/downloadArquivo?arquivo_nome='+encodeURIComponent('${fl:forJavaScriptAttribute(arquivo.nome)}') + '&tipo=${fl:forJavaScriptAttribute(tipo)}&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;" aria-label='<hl:message key="rotulo.botao.aria.download.arquivo" arg0="${arquivo.nome}"/>'> <hl:message key="rotulo.acoes.download" />
										</a></td>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="5"><hl:message key="rotulo.listar.arquivos.download.anexo.contrato.titulo.paginacao" />
								<span class="font-italic"> - <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}" />
							</span></td>
						</tr>
					</tfoot>
				</table>
				<div class="card-footer">
					<%@ include file="../paginador/incluirBarraPaginacao_v4.jsp"%>
				</div>
			</div>
		</div>
	</form>
</c:set>
<c:set var="javascript">
	<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
	<script type="text/JavaScript">
   var f0 = document.forms[0];
  
    function gerarArquivo() {
  	   f0.submit();
	}
  </script>
</c:set>
<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
