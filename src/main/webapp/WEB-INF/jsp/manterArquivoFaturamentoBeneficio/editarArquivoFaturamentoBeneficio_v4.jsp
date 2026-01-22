<%--
* <p>Title: listarBeneficio_v4</p>
* <p>Description: Listar benefícios v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.persistence.entity.TipoLancamento"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
TransferObject af = (TransferObject)request.getAttribute("arquivoFaturamento");
List<TipoLancamento> tiposLancamentos = (List) request.getAttribute("tiposLancamentos");

String detalhar = Boolean.valueOf(request.getAttribute("detalhar") != null && (Boolean) request.getAttribute("detalhar")).toString();

String afbCodigo = null;

String afbRseMatricula = "";
String fatPeriodo = "";
String cbeNumero = "";
String bfcCpf = "";

String tlaCodigo = "";
String tlaDescricao = "";

String benDescricao = "";
String csaNome = "";

BigDecimal afbValorSubsidio = null;
BigDecimal afbValorRealizado = null;
BigDecimal afbValorNaoRealizado = null;
BigDecimal afbValorTotal = null;

if (af != null) {
	
	afbCodigo = String.valueOf((Integer) af.getAttribute(Columns.AFB_CODIGO));
	
	afbRseMatricula = (String) af.getAttribute(Columns.AFB_RSE_MATRICULA);
	fatPeriodo = DateHelper.format((Date)af.getAttribute(Columns.FAT_PERIODO), "MM/yyyy") ;
	cbeNumero = (String) af.getAttribute(Columns.CBE_NUMERO);
	bfcCpf = (String) af.getAttribute(Columns.BFC_CPF);
	
	tlaCodigo = (String) af.getAttribute(Columns.TLA_CODIGO);
	tlaDescricao = (String) af.getAttribute(Columns.TLA_DESCRICAO);

	benDescricao = (String) af.getAttribute(Columns.BEN_DESCRICAO);
	csaNome = (String) af.getAttribute(Columns.CSA_NOME);
	
	afbValorSubsidio = (BigDecimal) af.getAttribute(Columns.AFB_VALOR_SUBSIDIO);
	afbValorRealizado = (BigDecimal) af.getAttribute(Columns.AFB_VALOR_REALIZADO);
	afbValorNaoRealizado = (BigDecimal) af.getAttribute(Columns.AFB_VALOR_NAO_REALIZADO);
	afbValorTotal = (BigDecimal) af.getAttribute(Columns.AFB_VALOR_TOTAL);
}

%>
<c:set var="title">
  <hl:message key="rotulo.faturamento.beneficios.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">

<form action="../v3/manterArquivoFaturamentoBeneficio?acao=salvar&_skip_history_=true" method="post">
<%out.print(SynchronizerToken.generateHtmlToken(request));%>
<input type="hidden" name="AFB_CODIGO" value="<%=afbCodigo%>" />
<div class="card">
	<div class="card-header">
		<h2 class="card-header-title"><hl:message key="rotulo.faturamento.beneficio.dados.registro" /></h2>
	</div>
	<div class="card-body">
		<dl class="row data-list">
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.periodo" />:</dt>
			<dd class="col-6"><%=fatPeriodo %></dd>
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.operadora" />:</dt>
			<dd class="col-6"><%=TextHelper.forHtmlContent(csaNome) %></dd>
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.beneficio" />:</dt>
			<dd class="col-6"><%=TextHelper.forHtmlContent(benDescricao) %></dd>
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.matricula" />:</dt>
			<dd class="col-6"><%=TextHelper.forHtmlContent(afbRseMatricula) %></dd>
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.numero.cliente" />:</dt>
			<dd class="col-6"><%=TextHelper.forHtmlContent(cbeNumero) %></dd>
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.cpf.beneficiario" />:</dt>
			<dd class="col-6"><%=TextHelper.forHtmlContent(bfcCpf) %></dd>
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.tipo.lancamento" />:</dt>
			<dd class="col-6"><%=TextHelper.forHtmlContent(tlaDescricao) %></dd>
		</dl>
	</div>
</div>
<div class="card">
	<div class="card-header">
		<h2 class="card-header-title"><hl:message key="rotulo.faturamento.beneficio.dados.valores" /></h2>
	</div>
	<div class="card-body">
		<% if ("true".equals(detalhar)) { %>
		
		<dl class="row data-list">
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.subsidio" />:</dt>
			<dd class="col-6"><%=afbValorSubsidio != null ? TextHelper.forHtmlContent(NumberHelper.format(afbValorSubsidio.doubleValue(), NumberHelper.getLang())) : ""%></dd>
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.realizado" />:</dt>
			<dd class="col-6"><%= afbValorRealizado != null ? TextHelper.forHtmlContent(NumberHelper.format(afbValorRealizado.doubleValue(), NumberHelper.getLang())) : ""%></dd>
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.nao.realizado" />:</dt>
			<dd class="col-6"><%= afbValorNaoRealizado != null ? TextHelper.forHtmlContent(NumberHelper.format(afbValorNaoRealizado.doubleValue(), NumberHelper.getLang())) : ""%></dd>
			<dt class="col-6"><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.total" />:</dt>
			<dd class="col-6"><%= afbValorTotal != null ? TextHelper.forHtmlContent(NumberHelper.format(afbValorTotal.doubleValue(), NumberHelper.getLang())) : ""%></dd>
		</dl>
		
		<% } else {  %>
		<div class="row">
			<div class="col">
				<div class="form-group">
					<label><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.subsidio" /></label>
					<hl:htmlinput name="AFB_VALOR_SUBSIDIO" 
                                  di="AFB_VALOR_SUBSIDIO" 
                                  type="text" 
                                  classe="form-control"
                                  mask="#F11" 
                                  size="8"
                                  readonly="<%=detalhar%>" 
                                  value="<%=afbValorSubsidio != null ? TextHelper.forHtmlContent(NumberHelper.format(afbValorSubsidio.doubleValue(), NumberHelper.getLang())) : ""%>"
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.faturamento.beneficio.arquivos.informe.valor.subsidio", responsavel) %>"/>    
				</div>
			</div>
			<div class="col">
				<div class="form-group">
					<label><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.realizado" /></label>
					<hl:htmlinput name="AFB_VALOR_REALIZADO" 
                                  di="AFB_VALOR_REALIZADO" 
                                  type="text" 
                                  classe="form-control"
                                  mask="#F11" 
                                  size="8" 
                                  readonly="<%=detalhar%>"
                                  value="<%= afbValorRealizado != null ? TextHelper.forHtmlContent(NumberHelper.format(afbValorRealizado.doubleValue(), NumberHelper.getLang())) : ""%>"
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.faturamento.beneficio.arquivos.informe.valor.realizado", responsavel) %>"/>
				</div>
			</div>
			<div class="col">
				<div class="form-group">
					<label><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.nao.realizado" /></label>
					<hl:htmlinput name="AFB_VALOR_NAO_REALIZADO"
                                  di="AFB_VALOR_NAO_REALIZADO" 
                                  type="text" 
                                  classe="form-control"
                                  mask="#F11" 
                                  size="8"
                                  readonly="<%=detalhar%>" 
                                  value="<%= afbValorNaoRealizado != null ? TextHelper.forHtmlContent(NumberHelper.format(afbValorNaoRealizado.doubleValue(), NumberHelper.getLang())) : ""%>"
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.faturamento.beneficio.arquivos.informe.valor.nao.realizado", responsavel) %>"/>
				</div>
			</div>
			<div class="col">
				<div class="form-group">
					<label><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.total" /></label>
					<hl:htmlinput name="AFB_VALOR_TOTAL"
                                  di="AFB_VALOR_TOTAL"
                                  type="text" 
                                  classe="form-control"
                                  mask="#F11" 
                                  size="8" 
                                  readonly="<%=detalhar%>"
                                  value="<%= afbValorTotal != null ? TextHelper.forHtmlContent(NumberHelper.format(afbValorTotal.doubleValue(), NumberHelper.getLang())) : ""%>"
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.faturamento.beneficio.arquivos.informe.valor.total", responsavel) %>"/>
				</div>
			</div>
		</div>
		<% } %>
	</div>
</div>

</form>

<div class="btn-action">
    <a class="btn btn-outline-danger" name="btnEnvia" id="btnEnvia" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')">
      <hl:message key="rotulo.botao.cancelar"/>
    </a>
    <% if (!"true".equals(detalhar)) { %>
    <a class="btn btn-primary" href="#no-back" onClick="validaSubmit(); return false;">
      <svg width="17">
        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-confirmar"></use>
      </svg>
      <hl:message key="rotulo.botao.salvar"/>
    </a>
    <% } %>
</div>

</c:set>
<c:set var="javascript">

<script type="text/javascript">

var f0 = document.forms[0];

<% if (!"true".equals(detalhar)) { %>
function validaSubmit () {
    var valorSubsidio = document.getElementById("AFB_VALOR_SUBSIDIO").value;
    var valorRealizado = document.getElementById("AFB_VALOR_REALIZADO").value;
    var valorNaoRealizado = document.getElementById("AFB_VALOR_NAO_REALIZADO").value;
    var valorTotal = document.getElementById("AFB_VALOR_TOTAL").value;
    
	if (valorSubsidio == "") {
	      alert("<hl:message key='mensagem.beneficio.subsidio.valor'/>");
	      return false;
	
}
	if (valorRealizado == "") {
	      alert("<hl:message key='mensagem.erro.faturamento.valor.realizado'/>");
	      return false;
	}

	if (valorNaoRealizado == "") {
	      alert("<hl:message key='mensagem.erro.faturamento.valor.naorealizado'/>");
	      return false;
	}
	if (valorTotal == "") {
	      alert("<hl:message key='mensagem.erro.faturamento.valor.total'/>");
	      return false;
	}
	f0.submit();
}
<% } %>
</script>

</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>