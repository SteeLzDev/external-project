<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.helper.restricaoacesso.RegraRestricaoAcessoViewHelper" %>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	List<?> enderecos = (List<?>) request.getAttribute("enderecos");
	String filtro = (String) request.getAttribute("filtro");
	int filtro_tipo = (int) request.getAttribute("filtro_tipo");

	int qtdColunas = 5;
%>

<c:set var="title">
	<hl:message key="rotulo.consultar.endereco.titulo.pagina"/>
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
	<div class="row">
		<div class="col-sm-5 col-md-4">
			<div class="card">
			  <div class="card-header hasIcon pl-3">
				<h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
			  </div>
			  <div class="card-body">
				<form name="form1" method="post" action="../v3/editarEnderecoConjHab?acao=iniciar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>">
				  <div class="row">
					<div class="form-group col-sm">
					  <label for="FILTRO"><hl:message key="rotulo.acoes.filtrar"/></label>
					  <input type="text" class="form-control" id="FILTRO" name="FILTRO" value="<%=TextHelper.forHtmlAttribute(filtro)%>" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>">
					</div>
				  </div>
				  <div class="row">
					<div class="form-group col-sm">
					  <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
					  <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO" onfocus="SetarEventoMascara(this,'#*200',true);" onblur="fout(this);ValidaMascara(this);">
						<option value=""  <%=(String)((filtro_tipo == -1) ? "selected" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></option>
						<option value="0" <%=(String)((filtro_tipo ==  0) ? "selected" : "")%>><hl:message key="rotulo.consultar.endereco.filtro.codigo"/></option>
						<option value="1" <%=(String)((filtro_tipo ==  1) ? "selected" : "")%>><hl:message key="rotulo.consultar.endereco.filtro.endereco"/></option>
						<option value="2" <%=(String)((filtro_tipo ==  2) ? "selected" : "")%>><hl:message key="rotulo.consultar.endereco.filtro.condominio.sim"/></option>
						<option value="3" <%=(String)((filtro_tipo ==  3) ? "selected" : "")%>><hl:message key="rotulo.consultar.endereco.filtro.condominio.nao"/></option>
					  </select>
					</div>
				  </div>
				</form>
			  </div>
			</div>
			<div class="btn-action">
			  <a class="btn btn-primary" href="#no-back" onclick="filtrar();">
				<svg width="20">
				  <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg> <hl:message key="rotulo.botao.pesquisar"/>
			  </a>
			</div>
		</div>
		<div class="col-sm-7 col-md-8">
			<div class="card">
				<div class="card-header">
					<h2 class="card-header-title"><hl:message key="rotulo.endereco.plural"/></h2>
				</div>
				<div class="card-body table-responsive p-0">
					<table class="table table-striped table-hover">
						<thead>
							<tr>
								<th><hl:message key="rotulo.endereco.codigo"/></th>
								<th><hl:message key="rotulo.endereco.singular"/></th>
								<th><hl:message key="rotulo.endereco.condominio"/></th>
								<th><hl:message key="rotulo.endereco.unidades"/></th>
								<th><hl:message key="rotulo.acoes"/></th>
							</tr>
						</thead>
						<tbody>
							<%
								Iterator<?> it = enderecos.iterator();
								String ech_codigo, ech_descricao, ech_condominio, ech_identificador;
								Short ech_unidades;
								
								while (it.hasNext()) {
									CustomTransferObject endereco = (CustomTransferObject)it.next();
									ech_codigo = (String)endereco.getAttribute(Columns.ECH_CODIGO);
									ech_descricao = (String)endereco.getAttribute(Columns.ECH_DESCRICAO);
									ech_condominio = (String)endereco.getAttribute(Columns.ECH_CONDOMINIO);
									ech_unidades = (Short)endereco.getAttribute(Columns.ECH_QTD_UNIDADES);
									ech_identificador = (String)endereco.getAttribute(Columns.ECH_IDENTIFICADOR);
							%>
								<tr>
									<td><%=TextHelper.forHtmlContent(ech_identificador)%></td>
									<td><%=TextHelper.forHtmlContent(ech_descricao.toUpperCase())%></td>
									<td><%=TextHelper.forHtmlContent(ech_condominio)%></td>
									<td><%=TextHelper.forHtmlContent(ech_unidades)%></td>
									<td>
										<div class="actions">
											<div class="dropdown">
												<a class="dropdown-toggle ico-action" href="#" role="button" id="addressMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
													<div class="form-inline">
													<span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
														<use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
													</span> <hl:message key="rotulo.botao.opcoes"/>
													</div>
												</a>
												<div class="dropdown-menu dropdown-menu-right" aria-labelledby="addressMenu">
													<% if (responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO)) { %>
														<a class="dropdown-item" href="#no-back" onclick="postData('../v3/editarEnderecoConjHab?acao=iniciarEdicao&echCodigo=<%=TextHelper.forJavaScriptAttribute(ech_codigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
													<% } else{ %>
														<a class="dropdown-item" href="#no-back" onclick="postData('../v3/editarEnderecoConjHab?acao=iniciarEdicao&echCodigo=<%=TextHelper.forJavaScriptAttribute(ech_codigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.visualizar"/></a>
													<% } %>
													<% if (responsavel.temPermissao(CodedValues.FUN_EXCLUIR_ENDERECO)) { %>
														<a class="dropdown-item" href="#no-back" onclick="ExcluirEntidade('<%=TextHelper.forJavaScript(ech_codigo)%>', 'END', '../v3/editarEnderecoConjHab?acao=excluir&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(ech_descricao.toUpperCase())%>')"><hl:message key="rotulo.acoes.excluir"/></a>
													<% } %>
												</div>
											</div>
										</div>
									</td>
								</tr>
							<% } %>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="<%=qtdColunas%>"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.enderecos", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
							</tr>
						</tfoot>
					</table>
				</div>
				<div class="card-footer">
					<%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
				</div>
			</div>
		</div>
	</div>
	<div class="btn-action">
		<a class="btn btn-outline-danger" href="#no-back" onclick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
		<% if (responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO)) { %>
			<a class="btn btn-primary" onclick="postData('../v3/editarEnderecoConjHab?acao=iniciarEdicao&_skip_history_=true&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')" href="#no-back" id="btnNovo">Novo</a>
		<% } %>
	</div>
</c:set>

<c:set var="javascript">
	<script type="text/javascript" src="../js/scripts_2810.js"></script>
	<script type="text/javascript" src="../js/xbdhtml.js"></script>
	<script type="text/javascript">
		f0 = document.forms[0];

		function filtrar() {
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