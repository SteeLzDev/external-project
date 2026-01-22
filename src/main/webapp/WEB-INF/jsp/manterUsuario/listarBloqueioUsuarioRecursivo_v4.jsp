<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.io.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String usuCodigo = (String) request.getAttribute("usuCodigo");
String tipo = (String) request.getAttribute("tipo");
String status = (String) request.getAttribute("status");
String tmoCodigo = (String) request.getAttribute("tmoCodigo");
String ousObs = (String) request.getAttribute("ousObs");
String link = (String) request.getAttribute("link");
List<TransferObject> usuarios = (List<TransferObject>) request.getAttribute("usuarios");

%>
<c:set var="title">
	<hl:message key="rotulo.usuario.bloqueio.recurso.titulo"/>
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
<div class="row justify-content-md-center">
	<div class="col-sm-12 form-check mt-2 form-group">
		<div class="card">
			<div class="card-header">
				<h2 class="card-header-title">
					<hl:message key="rotulo.usuario.bloqueio.recurso.titulo"/>
				</h2>
			</div>
			<form action="${linkBloquearUsuario}?acao=bloquearUsuarioRecursivo&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>"  method="POST" name="form1">
				<input type="hidden" name="USU_CODIGO" value="<%=TextHelper.forHtmlAttribute(usuCodigo)%>">
		      	<input type="hidden" name="tipo" value="<%=TextHelper.forHtmlAttribute(tipo)%>">
		      	<input type="hidden" name="STATUS" value="<%=TextHelper.forHtmlAttribute(status)%>">
		      	<input type="hidden" name="TMO_CODIGO" value="<%=TextHelper.forHtmlAttribute(tmoCodigo)%>">
		      	<input type="hidden" name="ADE_OBS" value="<%=TextHelper.forHtmlAttribute(ousObs)%>">
		      	<input type="hidden" name="link" value="<%=TextHelper.forHtmlAttribute(link)%>">
		      	
				<div class="card-body p-0">
					<div class="alert alert-warning m-0" role="alert">
	           			<hl:message key="mensagem.informacao.usuario.bloqueado.cadastro.outros.usuarios.v4" />
	       			</div>
	       			<div class="table-responsive">
						<table class="table table-striped table-hover" id="colunaUnicaOri">
	           				<thead>
	           					<tr >
	           						<th class="colunaUnica" scope="col" width="3%" style="display: none;">
	           							<div class="form-check">
			                    			<input type="checkbox" class="form-check-input ml-0" id="checkAll" onClick="check(this)">
			                  			</div>
			                  		</th>
		        	   				<th scope="col"><hl:message key="rotulo.usuario.singular"/></th>
					                <th scope="col"><hl:message key="rotulo.usuario.nome"/></th>
					                <th scope="col"><hl:message key="rotulo.usuario.entidade"/></th>
					                <th scope="col"><hl:message key="rotulo.usuario.situacao"/></th>
					                <th scope="col"><hl:message key="rotulo.usuario.data.criacao"/></th>
					                <th scope="col" width="15%"><hl:message key="rotulo.usuario.data.ultimo.acesso"/></th>
					                <th scope="col"><hl:message key="rotulo.usuario.data.exclusao"/></th>
					                <th scope="col"><hl:message key="rotulo.usuario.responsavel"/></th>
					                <th scope="col"><hl:message key="rotulo.acoes" /></th>
		            			</tr>
		            		</thead>
							<tbody>
								<%
									String usu_codigo, usu_nome, usu_login, usu_data_ult_acesso, stu_codigo, stu_descricao, ous_data, data_exclusao, entidade, tipo_entidade, criador;
								
									Iterator<TransferObject> it = usuarios.iterator();
									while (it.hasNext()) {
										CustomTransferObject next = (CustomTransferObject)it.next();
									  	criador = next.getAttribute("RESPONSAVEL").toString();
									  	usu_codigo = next.getAttribute(Columns.USU_CODIGO).toString();
									  	usu_nome = next.getAttribute(Columns.USU_NOME).toString();
									  	usu_login = next.getAttribute(Columns.USU_LOGIN).toString();
									  	usu_data_ult_acesso = next.getAttribute(Columns.USU_DATA_ULT_ACESSO).toString();
									  	stu_codigo = next.getAttribute(Columns.STU_CODIGO).toString();
									  	stu_descricao = next.getAttribute(Columns.STU_DESCRICAO).toString();
									  	ous_data = next.getAttribute(Columns.OUS_DATA).toString();
									  	data_exclusao = !TextHelper.isNull(next.getAttribute("DATA_EXCLUSAO")) ? DateHelper.reformat(next.getAttribute("DATA_EXCLUSAO").toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
									  	entidade = next.getAttribute("ENTIDADE").toString();
									  	tipo_entidade = next.getAttribute("TIPO_ENTIDADE").toString();
								%>
								<tr class="selecionarLinha">
									<td class="colunaUnica" aria-label="" title="" data-bs-toggle="tooltip" data-original-title="" style="display: none;">
							           	<div class="form-check">
							           		<input id="<%=TextHelper.forHtmlAttribute(usu_codigo)%>" name="USUARIO" type="checkbox" value="<%=TextHelper.forHtmlAttribute(usu_codigo+";"+tipo_entidade)%>" <%if (!stu_codigo.equals(CodedValues.STU_ATIVO)) {%>disabled="disabled"<%}%>>
							           	</div>
							        </td>
						           	<td class="selecionarColuna"><%=TextHelper.forHtmlContent(usu_login)%></td>
						            <td class="selecionarColuna"><%=TextHelper.forHtmlContent(usu_nome.toUpperCase())%></td>
						            <td class="selecionarColuna"><%=TextHelper.forHtmlContent(entidade.toUpperCase())%></td>
						            <td class="selecionarColuna"><%=TextHelper.forHtmlContent(stu_descricao)%></td>
						            <td class="selecionarColuna"><%=DateHelper.reformat(ous_data, "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern())%></td>
						            <td class="selecionarColuna"><%=DateHelper.reformat(usu_data_ult_acesso, "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern())%></td>
						            <td class="selecionarColuna"><%=TextHelper.forHtmlContent(data_exclusao)%></td>
						            <td class="selecionarColuna"><%=TextHelper.forHtmlContent(criador)%></td>
						            <td class="selecionarColuna"><%if (stu_codigo.equals(CodedValues.STU_ATIVO)) {%><a href="#no-back" onclick ="escolhechk('Selecionar',this)"><hl:message key="rotulo.acoes.selecionar" /></a><%}%></td>
						        </tr>
								<%
								}
								%>
							</tbody>
						</table>
					</div>
				</div>
			</form>
		</div>
		<div class="btn-action">
			<a class="btn btn-outline-danger" id="btnCancelar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar" /></a>
			<a class="btn btn-third" id="btnListarTodos" href="#no-back" onClick="listarTodos('<%=TextHelper.forJavaScript(usuCodigo)%>', '<%=TextHelper.forJavaScript(tipo)%>', '<%=TextHelper.forJavaScript(status)%>', '<%=TextHelper.forJavaScript(link)%>');"><hl:message key="rotulo.botao.listarTodos" /></a>
			<a class="btn btn-primary" id="btnSalvar" href="#no-back" onClick="return vf_cadastro();"><hl:message key="rotulo.botao.salvar" /></a>
		</div>
	</div>
</div>
</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
	<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
	
	<script type="text/JavaScript">

		var f0 = document.forms[0];

		function listarTodos(codigo, tipo_entidade, status, alink) {
		  	var url = "${linkBloquearUsuario}?acao=listarBloqueioUsuarioRecursivo&USU_CODIGO=" + codigo;
		  	url += "&STATUS=" + status + "&link=" + alink + "&tipo=" + tipo_entidade;
		  	url += "&TMO_CODIGO=<%=tmoCodigo%>&ADE_OBS=<%=ousObs%>";
		  	url += "&LISTAR_TODOS=listar_tudo&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>";
		  	postData(url);
		}
		
		function checkAll() {
		  	var checkObj = f0.USUARIO;
		  	if (checkObj.length == undefined) {
			    if (checkObj.disabled == false) {
					checkObj.checked = true;
		    	}
		  	}
		  	for (i=0; i<checkObj.length; i++) {
				if (checkObj[i].disabled == false) {
		      		checkObj[i].checked = true;
				}
		  	}
		}
		
		function uncheckAll() {
		  var checkObj = f0.USUARIO;
		  if (checkObj.length == undefined) {
		    if (checkObj.disabled == false) {
		      checkObj.checked = false;
		    }
		  }
		  for (i=0; i<checkObj.length; i++) {
		    if (checkObj[i].disabled == false) {
		      checkObj[i].checked = false;
		    }
		  }
		  $("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").hide();
		}
		
		function vf_cadastro() {
		  var msg = '<hl:message key="mensagem.confirmacao.usuario.bloqueio.recurso"/>';
		  var selecionado = false;
		  var checkObj = f0.USUARIO;
		  for (i=0; i<checkObj.length; i++) {
		    if (checkObj[i].disabled == false && checkObj[i].checked == true) {
		      selecionado = true;
		    }
		  }
		  if (selecionado) {
		    if (confirm(msg)) {
		      f0.submit();
		    }
		  } else {
		    alert('<hl:message key="mensagem.informe.usuario.bloqueio.recurso"/>');
		  }
		}

		function check(e){
			if(e.checked == true) {
				checkAll();
			} else {
				uncheckAll();
			}
			return false;
		}
		
		function escolhechk(idchk,e) {
		 	$(e).parents('tr').find('input[type=checkbox]').click();
		}

		$("table tbody tr td.selecionarColuna").click(function (e) {
			if(e.target.tagName != 'A') {
				$(e.target).parents('tr').find('input[type=checkbox]').click();
			}
		});

		var desabilitados = $("table tbody tr input[type=checkbox]:disabled").length;
		
		$(".selecionarColuna, table tbody tr input[type=checkbox]").click(function() {

			var total = $("table tbody tr input[type=checkbox]").length;
			var checked = $("table tbody tr input[type=checkbox]:checked").length;

			$("input[id*=checkAll]").prop('checked', checked == total-desabilitados);

			if (checked == 0) {
				$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").hide();
			} else {
				$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").show();
			}
		});
	</script>
</c:set>

<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>