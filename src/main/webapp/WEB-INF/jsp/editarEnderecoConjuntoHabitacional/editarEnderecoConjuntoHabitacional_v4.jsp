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
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.helper.restricaoacesso.RegraRestricaoAcessoViewHelper" %>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	String ech_codigo = (String) request.getAttribute("ech_codigo");
	EnderecoTransferObject endereco = (EnderecoTransferObject) request.getAttribute("endereco");
	String msgErro = (String) request.getAttribute("msgErro");
	boolean podeEditarEndereco = (boolean) request.getAttribute("podeEditarEndereco");
%>

<c:set var="title">
	<hl:message key="rotulo.manutencao.endereco.titulo.pagina"/>
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
	<div class="col-sm-12">
		<form action="../v3/editarEnderecoConjHab?acao=salvarEdicao&echCodigo=<%=TextHelper.forJavaScriptAttribute(ech_codigo)%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>"  method="POST" name="form1">
			<div class="card">
				<div class="card-header">
					<h2 class="card-header-title">
						<% if(TextHelper.isNull(ech_codigo)) {%>
							<hl:message key="rotulo.novo.endereco"/>
						<% } else { %>
							<hl:message key="rotulo.consultar.endereco.dados.endereco"/>
						<% } %>
					</h2>
				</div>
				<div class="card-body">
					<input type="hidden" name="codigo" value="<%=TextHelper.forHtmlAttribute(ech_codigo)%>">
					<div class="row">
						<div class="form-group col-sm-12">
							<label for="endCodigo"><hl:message key="rotulo.endereco.codigo"/></label>
							<input type="text"
								class="form-control"
								id="endCodigo"
								name="ECH_IDENTIFICADOR"
								placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.codigo", responsavel)%>"
								onfocus="SetarEventoMascaraV4(this,'#*50',true);" onblur="fout(this);ValidaMascaraV4(this);"
								value="<%=TextHelper.forHtmlAttribute(endereco != null ? (endereco.getEchIdentificador() != null ? endereco.getEchIdentificador() : JspHelper.verificaVarQryStr(request, "ECH_IDENTIFICADOR")) : "" )%>"
								size="32"
								<%if(!podeEditarEndereco){%> disabled <%}%>
							/>
							<%=JspHelper.verificaCampoNulo(request, "ECH_IDENTIFICADOR")%>
						</div>
					</div>
					<div class="row">
						<div class="form-group col-sm-12">
							<label for="endDescricao"><hl:message key="rotulo.endereco.singular"/></label>
							<input type="text"
								class="form-control"
								id="endDescricao"
								name="ECH_DESCRICAO"
								placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.endereco", responsavel)%>"
								value="<%=TextHelper.forHtmlAttribute(endereco != null ? (endereco.getEchDescricao() != null ? endereco.getEchDescricao() : JspHelper.verificaVarQryStr(request, "ECH_DESCRICAO")) : "" )%>"
								size="32"
								<%if(!podeEditarEndereco){%> disabled <%}%>
							/>
							<%=JspHelper.verificaCampoNulo(request, "ECH_DESCRICAO")%>
						</div>
					</div>
					<div class="row">
						<div class="form-group col-sm-12">
							<label for="endQtdUnidades"><hl:message key="rotulo.endereco.unidades"/></label>
							<input type="text"
								class="form-control"
								id="endQtdUnidades"
								name="ECH_QTD_UNIDADES"
								placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.quantidade.unidades", responsavel)%>"
								value="<%=TextHelper.forHtmlAttribute(endereco != null ? (endereco.getEchQtdUnidades() != null ? (endereco.getEchQtdUnidades()) : JspHelper.verificaVarQryStr(request, "ECH_QTD_UNIDADES")) : "" )%>"
								size="5"
								<%if(!podeEditarEndereco){%> disabled <%}%>
							/>
							<%=JspHelper.verificaCampoNulo(request, "ECH_QTD_UNIDADES")%>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12 col-md-6">
							<input
								class="form-check-input ml-1"
								type="checkbox"
								name="ECH_CONDOMINIO"
								id="endCondominio"
								value="SIM"
								<%
									boolean checked = false;
									if(endereco != null && endereco.getEchCondominio() != null){
										if(endereco.getEchCondominio().equals("S")){
											checked = true;
										}
									} else if(JspHelper.verificaVarQryStr(request, "ECH_CONDOMINIO").toString().equals("S")) {
										checked = true;
									}
								%>
								<%if(checked) { %> checked <% } %>
								<%if(!podeEditarEndereco) { %> disabled <% } %>
							/>
							<label class="form-check-label" for="endCondominio">
								<span class="text-nowrap align-text-top"><hl:message key="rotulo.endereco.condominio"/></span>
							</label>
						</div>
					</div>
				</div>
			</div>
			<div class="btn-action">
				<a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.cancelar"/></a>
				<% if (podeEditarEndereco) { %>
				<a class="btn btn-primary" href="#no-back" onclick="habilitaCampos(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
				<% } %>
			</div>
		</form>
	</div>
</c:set>

<c:set var="javascript">
	<script type="text/javascript" src="../js/scripts_2810.js"></script>
	<script type="text/javascript" src="../js/validacoes.js"></script>
	<script type="text/javascript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript">
		var f0 = document.forms[0];
		function habilitaCampos() {
			if (vf_cadastro_end()) {
				if (f0.ECH_QTD_UNIDADES.value == '0') {
					alert('<hl:message key="mensagem.informe.ech.qtd.unidade"/>');
					f0.ECH_QTD_UNIDADES.focus();
					return false;
				} else {
					enableAll();
					f0.submit();  	
				}
			}
		}
	</script>
</c:set>

<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>