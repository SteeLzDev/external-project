<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	String adeCodigo = (String) request.getAttribute("adeCodigo");
	boolean obrigatorio = (boolean) request.getAttribute("obrigatorio");
%>

<c:set var="title">
	<hl:message key="mensagem.informar.pagamento.saldo.devedor.anexo.titulo.pagina"/>
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
	<form name="form1" method="POST" ACTION="<%=SynchronizerToken.updateTokenInURL("../v3/anexarPagamentoConsignacao?acao=iniciar&ADE_CODIGO=" + adeCodigo, request)%>">
    <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
		<div class="card">
			<div class="card-header">
				<h2 class="card-header-title"><hl:message key="rotulo.anexo.arquivo.titulo"/></h2>
			</div>
			<div class="card-body">
				<hl:fileUploadV4 obrigatorio="<%=obrigatorio%>" tipoArquivo="comprovante_pag_saldo"/>
				
				<% if(responsavel.isCsaCor()) { %>
					<div class="row">
						<div class="form-group col-sm-12 col-md-6">
							<label for="observacao"><hl:message key="rotulo.saldo.devedor.observacao"/><hl:message key="rotulo.campo.opcional"/></label>
							<input type="text" name="obs" id="obs" onfocus="SetarEventoMascara(this,'#*100',true);" onblur="fout(this);ValidaMascara(this);" class="form-control"/>
						</div>
						</div>
				<% } %>
			</div>
		</div>
		<div class="btn-action">
			<a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
			<a class="btn btn-primary" href="#" onclick="if(vf_upload_arquivos(<%=obrigatorio%>)){document.form1.submit();} return false;"><hl:message key="rotulo.botao.confirmar"/></a>
		</div>
		<input name="FORM" type="hidden" value="form1"></input>
	</form>
		<div class="modal fade" id="confirmarMensagem" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
			<div class="modal-dialog" role="document">
			  <div class="modal-content">
				<div class="modal-header">
				  <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="rotulo.anexo.descricao"/></h5>
				  <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
					<span aria-hidden="true"></span>
				  </button>
				</div>
				<div class="form-group modal-body m-0">
				  <label for="editfield"><hl:message key="mensagem.informe.anexo.consignacao.descricao"/></label>
				  <textarea class="form-control" id="editfield" name="editfield" rows="3" cols="28"></textarea>
				  
				</div>
				<div class="modal-footer pt-0">
				  <div class="btn-action mt-2 mb-0">
					<a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.cancelar"/></a>
					<input hidden="true"  id="aad_nome" value="">
					<input hidden="true"  id="aad_descricao" value="">
					<a class="btn btn-primary" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.confirmar"/>' onclick="show_setDescricao();" href="#"><hl:message key="rotulo.botao.confirmar"/></a>
				  </div>
				</div>
			  </div>
			</div>
		  </div>
	  <!-- Modal aguarde -->
	  <div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	   <div class="modal-dialog-upload modal-dialog" role="document">
		 <div class="modal-content">
		   <div class="modal-body">
			 <div class="row">
			   <div class="col-md-12 d-flex justify-content-center">
				 <img src="../img/loading.gif" class="loading">
			   </div>
			   <div class="col-md-12">
				 <div class="modal-body"><span><hl:message key="mensagem.upload.generico.aguarde"/></span></div>            
			   </div>
			 </div>
		   </div>
		 </div>
	   </div>
	  </div>
</c:set>

<c:set var="javascript">
	<link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
	<script type="text/javascript" src="../js/watermark.js"></script>  
	<script type="text/javascript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
	<hl:fileUploadV4 scriptOnly="true" tipoArquivo="comprovante_pag_saldo"/>
	<script type="text/javascript">
		var f0 = document.forms[0];

		function formLoad() {
			f0.FILE1.focus();
		}
			
		function vf_upload_arquivos(obr) {
			var arquivo = document.getElementById("FILE1").value;
			var descricao = document.getElementById("AAD_DESCRICAO").value;
			if (((arquivo == null) || (trim(arquivo) == "") || (arquivo.toUpperCase() == "NULL")) && obr) {
				alert('<hl:message key="mensagem.informar.pagamento.saldo.devedor.anexo.selecione"/>');
				return false;
			} else if (descricao.length > 255 && obr) {
				alert('<hl:message key="mensagem.informar.pagamento.saldo.devedor.anexo.descricao.maxima"/>');
				return false;      
			} else {
				$('#modalAguarde').modal({
					backdrop: 'static',
					keyboard: false
				});
			}

			return true;
		}

		function show_setDescricao(){
			urlSolicitacao = '<%=SynchronizerToken.updateTokenInURL("../v3/anexarPagamentoConsignacao" + "?acao=iniciar&ADE_CODIGO=" + adeCodigo + "&_skip_history_=true", request)%>';
			urlSolicitacao = urlSolicitacao + '&NOME_ARQ='+ $("#aad_nome").val();
			postData(urlSolicitacao + '&DESCRICAO=' + $('#editfield').val());
		}
  
		function show_descricao(aad_nome, aad_descricao) {
			$('#confirmarMensagem').modal('show');
			$("#aad_nome").val(aad_nome);
			$("#aad_descricao").val(aad_descricao);
			$('#editfield').val(aad_descricao);
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