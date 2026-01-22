<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String rotuloBotaoVisualizar = (String) request.getAttribute("rotuloBotaoVisualizar");
String rotuloBotaoMarcarTodos = (String) request.getAttribute("rotuloBotaoMarcarTodos");
String rotuloBotaoDesmarcarTodos = (String) request.getAttribute("rotuloBotaoDesmarcarTodos");
String rotuloBotaoEditarTodas = (String) request.getAttribute("rotuloBotaoEditarTodas");
String rotuloBotaoExcluirTodas = (String) request.getAttribute("rotuloBotaoExcluirTodas");
String rotuloBotaoVisualizarTodas = (String) request.getAttribute("rotuloBotaoVisualizarTodas");
String rotuloBotaoVoltar = (String) request.getAttribute("rotuloBotaoVoltar");
String rotuloCheckboxTodos = (String) request.getAttribute("rotuloCheckboxTodos");

String acrCodigo = (String) request.getAttribute("acrCodigo");			
String funcao = (String) request.getAttribute("funCodigo");
String acrParametro = (String) request.getAttribute("acrParametro");
String acrOperacao = (String) request.getAttribute("acrOperacao");
Boolean clicouListarTodos = (Boolean) request.getAttribute("clicouListarTodos");

List<?> funAcessoRecurso = (List<?>) request.getAttribute("funAcessoRecurso");
%>

<c:set var="title">
<hl:message key="rotulo.editar.ajuda.titulo"/>
</c:set>

<c:set var="imageHeader">
   <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.ajuda.exibir.ajuda.para" /></h2>
    </div>
    <div class="card-body">
		<form
			method="post"
			action="../v3/editarManualAjuda?acao=editar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>"
			name="form1"
		>
			<div class="row">
				<div class="col-sm-12 col-md-12">
					<div class="form-check">
						<div role="group">
							<%
								Iterator<?> ite = funAcessoRecurso.iterator();
								while (ite.hasNext()) {
									CustomTransferObject cto = (CustomTransferObject) ite.next();
									String codigo = cto.getAttribute(Columns.ACR_CODIGO).toString();
									String papCodigo = cto.getAttribute(Columns.ACR_PAP_CODIGO).toString();
									String papDescricao = cto.getAttribute(Columns.PAP_DESCRICAO).toString();
									if (papCodigo.equals("1")) {
										papDescricao = ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel);
									} else if (papCodigo.equals("2")) {
										papDescricao = ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel);
									} else if (papCodigo.equals("3")) {
										papDescricao = ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel);
									} else if (papCodigo.equals("4")) {
										papDescricao = ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel);
									} else if (papCodigo.equals("6")) {
										papDescricao = ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);
									}
									String funDescricao = !TextHelper.isNull(cto.getAttribute(Columns.FUN_DESCRICAO))
											? cto.getAttribute(Columns.FUN_DESCRICAO).toString()
											: "";
									boolean possuiAjuda = cto.getAttribute("possui_ajuda") != null
											&& cto.getAttribute("possui_ajuda").toString().equals("1") ? true : false;
								
							%>
								<div class="col-sm-12 col-md-6">
									<input class="form-check-input ml-1" type="checkbox" name="acrCodigos" id="<%=TextHelper.forHtmlAttribute(codigo)%>" value="<%=TextHelper.forHtmlAttribute(codigo)%>">
									<label class="form-check-label" for="<%=TextHelper.forHtmlAttribute(codigo)%>">
										<span class="text-nowrap align-text-top"><%=TextHelper.forHtmlContent(funDescricao)%> - <%=TextHelper.forHtmlContent(papDescricao)%></span>
									</label>
								<% if(possuiAjuda) { %>
									<a href="#no-back" onclick="postData('../v3/visualizarAjudaContexto?acao=visualizar&acrCodigo=<%=TextHelper.forJavaScriptAttribute(codigo)%>&_skip_history_=true&<%=(String) SynchronizerToken.generateToken4URL(request)%>')">
                                        <img src="../img/icones/help.png" border="0"
                                        alt="<%=TextHelper.forHtmlAttribute(rotuloBotaoVisualizar)%>"
                                        title="<%=TextHelper.forHtmlAttribute(rotuloBotaoVisualizar)%>">
                                    </a>
								<% } %>
								</div>
							<% } %>
							<div class="col-sm-12 col-md-6">
								<input class="form-check-input ml-1" name="checkGrupo" type="checkbox" id="Todos" onclick="check_uncheck_grupo(this);">
								<label class="form-check-label" for="Todos">
								  <span class="text-nowrap align-text-top font-weight-bold"><%=TextHelper.forHtmlContent(rotuloCheckboxTodos)%></span>
								</label>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="btn-action">
				<a class="btn btn-outline-danger mt-3" onclick="postData('../v3/visualizarAjudaContexto?acao=visualizar&acrCodigo=<%=acrCodigo%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');" href="#no-back" id="btnVoltar"><%=TextHelper.forHtmlContent(rotuloBotaoVoltar)%></a>
				<a class="btn btn-third mt-3" onclick="if(vf_submit()) {f0.submit();} return false;" href="#no-back" id="btnEditar"><%=TextHelper.forHtmlContent(rotuloBotaoEditarTodas)%></a>
				<a class="btn btn-fourth mt-3" onclick="if(vf_submit()) {f0.acaoPosterior.value='excluir'; f0.submit();} return false;" href="#no-back" id="btnExcluir"><%=TextHelper.forHtmlContent(rotuloBotaoExcluirTodas)%></a>
				<% if(!clicouListarTodos) { %>
					<a class="btn btn-primary mt-3" onclick="postData('../v3/editarManualAjuda?acao=listar&acrCodigo=<%=TextHelper.forJavaScriptAttribute(acrCodigo)%>&funCodigo=<%=TextHelper.forJavaScriptAttribute(funcao)%>&clicouListarTodos=S&refresh=true&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" id="btnListarTodos" href="#no-back"><%=TextHelper.forHtmlContent(rotuloBotaoVisualizarTodas)%></a>
				<% } %>
			</div>
          <INPUT TYPE="hidden" NAME="acaoPosterior" VALUE="">
    <INPUT TYPE="hidden" NAME="acrCodigoOriginal" VALUE="<%=acrCodigo%>">
		</form>
    </div>
  </div>
</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" charset="iso-8859-1"	src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
	<script type="text/JavaScript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
	<script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
	<script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
	<script id="MainScript">
		var f0 = document.forms[0];

		function formLoad() {
		}

		function check_uncheck_grupo(source) {
			let checkboxes = document.querySelectorAll('input[type="checkbox"]');
			for(let i = 0; i < checkboxes.length; i++) {
				checkboxes[i].checked = source.checked
			}
		}

		function vf_submit() {
			var checked = false;
			for (i=0; i < f0.elements.length; i++) {
				var e = f0.elements[i];
				if (((e.type == 'check') || (e.type == 'checkbox')) && (e.checked == true)) {
					checked = true;
					break;
				}
			}
			if (!checked) {
				alert('<hl:message key="mensagem.informe.recurso.ajuda"/>');
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