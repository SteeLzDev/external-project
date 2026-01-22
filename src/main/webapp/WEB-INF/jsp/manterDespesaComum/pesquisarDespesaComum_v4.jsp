<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="java.math.BigDecimal"%>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	String tipo = (String) request.getAttribute("tipo");
	boolean isConsultaDespesaComum = (boolean) request.getAttribute("isConsultaDespesaComum");
	String titulo = (String) request.getAttribute("titulo");
	String echCodigo = (String) request.getAttribute("echCodigo");
	String plaCodigo = (String) request.getAttribute("plaCodigo");
	String linkRet = (String) request.getAttribute("linkRet");
	List<?> despesasComuns = (List<?>) request.getAttribute("despesasComuns");
	List<TransferObject> enderecos = (List<TransferObject>) request.getAttribute("enderecos");
	List<TransferObject> planos = (List<TransferObject>) request.getAttribute("planos");
%>

<c:set var="title">
	<%=TextHelper.forHtml(titulo)%>
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
	<form action="<%=(String)(isConsultaDespesaComum ? "../v3/consultarDespesaComum?acao=iniciar" : "../v3/lancarDespesaComum?acao=inserirDados")%>&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
		<%= SynchronizerToken.generateHtmlToken(request) %>
		<hl:htmlinput name="TIPO_LISTA" 	 type="hidden" di="TIPO_LISTA" 	   value="pesquisa"/>
		<hl:htmlinput name="FORM"            type="hidden" di="FORM"           value="form1" />
        <hl:htmlinput name="tipo"            type="hidden" di="tipo"           value="<%=TextHelper.forHtmlAttribute(tipo)%>" />
        <hl:htmlinput name="linkRet"         type="hidden" di="linkRet"        value="<%=TextHelper.forHtmlAttribute(linkRet)%>" />
        <hl:htmlinput name="SELCSACODIGO"    type="hidden" di="SELCSACODIGO"   value="" />
		<div class="card">
		  <div class="card-header hasIcon">
			<span class="card-header-icon"><svg width="26">
				<use xlink:href="../img/sprite.svg#i-consultar"></use></svg>
			</span>
			<h2 class="card-header-title"><hl:message key="mensagem.pesquisa.titulo"/></h2>
		  </div>
		  <div class="card-body">
			<fieldset>
			  <c:if test="${not empty enderecos}">
				<div class="row">
				  <div class="form-group col-sm-12">
					<label for="ECH_CODIGO"><hl:message key="rotulo.endereco.singular"/></label>
					<hl:htmlcombo listName="enderecos" name="ECH_CODIGO" fieldValue="<%=Columns.ECH_CODIGO%>" fieldLabel="<%=Columns.ECH_IDENTIFICADOR + ";" + Columns.ECH_DESCRICAO%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
				  </div>
				</div>
			  </c:if>
	
			  <c:if test="${not empty planos}">
				<div class="row">
				  <div class="form-group col-sm-12">
					<label for="PLA_CODIGO"><hl:message key="rotulo.plano.singular"/></label>
					<hl:htmlcombo listName="planos" name="PLA_CODIGO" fieldValue="<%=Columns.PLA_CODIGO%>" fieldLabel="<%=Columns.PLA_IDENTIFICADOR + ";" + Columns.PLA_DESCRICAO%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
				  </div>
				</div>
			  </c:if>
			</fieldset>
		  </div>
		</div>
	  </form>
	  <% if (isConsultaDespesaComum && despesasComuns != null && despesasComuns.size() > 0) { %>
		<div class="card">
			<div class="card-header hasIcon pl-3">
			  <h2 class="card-header-title"><hl:message key="rotulo.resultado.pesquisa"/></h2>
			</div>
			<div class="card-body table-responsive p-0">
				<table class="table table-striped table-hover">
					<thead>
						<tr>
							<th scope="col"><hl:message key="rotulo.endereco.singular"/></th>
							<th scope="col"><hl:message key="rotulo.servico.singular"/></th>
							<th scope="col"><hl:message key="rotulo.despesa.comum.inclusao"/></th>  
							<th scope="col"><hl:message key="rotulo.despesa.comum.situacao"/></th>
							<th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
							<th scope="col"><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
							<th scope="col"><hl:message key="rotulo.despesa.comum.inicio"/></th>
							<th scope="col"><hl:message key="rotulo.despesa.comum.fim"/></th>
							<th scope="col"><hl:message key="rotulo.acoes"/></th>
						</tr>
					</thead>
					<tbody>
						<%
							Iterator<?> it = despesasComuns.iterator();
							String endereco, plano, decCodigo, status, adeTipoVlr;
							BigDecimal valor;
							Integer prazo;
							Date dataInicio, dataFim, inclusao;
							
							while (it.hasNext()) {
							CustomTransferObject despesaComum = (CustomTransferObject)it.next();
							
							decCodigo = (String)despesaComum.getAttribute(Columns.DEC_CODIGO);
							endereco = (String)despesaComum.getAttribute(Columns.ECH_DESCRICAO);
							plano = (String)despesaComum.getAttribute(Columns.PLA_DESCRICAO);
							valor = (BigDecimal)despesaComum.getAttribute(Columns.DEC_VALOR);
							prazo = (Integer)despesaComum.getAttribute(Columns.DEC_PRAZO);
							dataInicio = (Date)despesaComum.getAttribute(Columns.DEC_DATA_INI);
							dataFim = (Date)despesaComum.getAttribute(Columns.DEC_DATA_FIM);
							inclusao = (Date)despesaComum.getAttribute(Columns.DEC_DATA);
							status = (String)despesaComum.getAttribute(Columns.SDC_DESCRICAO);
							adeTipoVlr = (String) despesaComum.getAttribute(Columns.ADE_TIPO_VLR);
						%>
						<tr>
							<td><%=TextHelper.forHtmlContent(endereco)%></td>
							<td><%=TextHelper.forHtmlContent(plano)%></td>
							<td><%=DateHelper.toDateTimeString(inclusao)%></td>
							<td><%=TextHelper.forHtmlContent(status)%></td>
							<td><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))%> <%=NumberHelper.format(valor.doubleValue(), NumberHelper.getLang(), 2, 8)%></td>
							<% if(prazo != null) { %>
								<td><%=TextHelper.forHtmlContent(prazo)%></td>
							<% } else { %>
								<td><hl:message key="rotulo.indeterminado.abreviado"/></td>
							<% } %>
							<td><%=DateHelper.toPeriodString(dataInicio)%></td>
							<td><%=DateHelper.toPeriodString(dataFim)%></td>
							<td>
							<div class="actions">
								<div class="dropdown">
								<a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
									<div class="form-inline">
									<span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"><svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span>
									<hl:message key="rotulo.botao.opcoes"/>
									</div>
								</a>
								<div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
									<a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarDespesaComum?acao=consultar&decCodigo=<%=TextHelper.forJavaScriptAttribute(decCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
										<hl:message key="mensagem.despesa.comum.visualizar.clique.aqui"/>
									</a>
								</div>
								</div>
							</div>
							</td>
						</tr>
						<%   
							}
						%>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="10">
								<hl:message key="mensagem.listagem.despesa.comum" arg0="<%=DateHelper.toDateTimeString(DateHelper.getSystemDatetime())%>" />
								<span class="font-italic"> - <%=request.getAttribute("_paginacaoSubTitulo")%></span>
							</td>
						</tr>
					</tfoot>
				</table>
			</div>
			<div class="card-footer">
				<%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
			</div>
		  </div>
	  <% } %>
	  <div class="btn-action">
			<a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onclick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
			<a class="btn btn-primary" id="btnPesquisar" href="#no-back" onclick="if(validaCampos()){f0.submit();} return false;"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
	  </div>
</c:set>

<c:set var="javascript">
	<script type="text/javascript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript" src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript">
		f0 = document.forms[0];

		function formLoad() {
			focusFirstField();
		}

		function validaCampos() {
			var ControlesAvancados = new Array("ECH_CODIGO", "PLA_CODIGO");
			var MsgsAvancadas = new Array('<hl:message key="mensagem.informe.endereco"/>', '<hl:message key="mensagem.informe.plano"/>');
			
			<% if(isConsultaDespesaComum) { %>
				var MsgPeloMenosUm = '<hl:message key="mensagem.informe.endereco.ou.plano"/>';
				return ValidaCamposPeloMenosUmPreenchido(ControlesAvancados, MsgPeloMenosUm);
			<% } else { %>
				return ValidaCampos(ControlesAvancados, MsgsAvancadas);
			<% } %>
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