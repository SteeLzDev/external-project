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
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String fat_periodo = (String) request.getAttribute(Columns.FAT_PERIODO);
String csa_nome = (String) request.getAttribute(Columns.CSA_NOME);
String fat_data = (String) request.getAttribute(Columns.FAT_DATA);

String fat_codigo = (String) request.getAttribute(Columns.FAT_CODIGO);
String csa_codigo = (String) request.getAttribute(Columns.CSA_CODIGO);


List<TransferObject> arquivosFaturamento = (List<TransferObject>) request.getAttribute("arquivosFaturamento");

%>
<c:set var="title">
  <hl:message key="rotulo.faturamento.beneficios.consultar.arquivos.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
<form action="../v3/manterArquivoFaturamentoBeneficio?acao=listar&_skip_history_=true" method="post">
	<%out.print(SynchronizerToken.generateHtmlToken(request));%>
	<input type="hidden" name="FAT_CODIGO" value="<%=fat_codigo != null ? TextHelper.forHtmlContent(fat_codigo) : ""%>">
	<input type="hidden" name="CSA_CODIGO" value="<%=csa_codigo != null ? TextHelper.forHtmlContent(csa_codigo) : ""%>">
	<input type="hidden" name="FAT_PERIODO" value="<%=fat_periodo != null ? TextHelper.forHtmlContent(fat_periodo) : ""%>">
	<input type="hidden" name="CSA_NOME" value="<%=csa_nome != null ? TextHelper.forHtmlContent(csa_nome) : ""%>">
	<input type="hidden" name="FAT_DATA" value="<%=fat_data != null ? TextHelper.forHtmlContent(fat_data) : ""%>">
	<div class="row">
		<div class="col-sm">
			<div class="card">
				<div class="card-header">
					<h2 class="card-header-title"><hl:message key="rotulo.faturamento.beneficios.detalhes.faturamento" /></h2>
				</div>
				<div class="card-body">
					<div class="row">
						<div class="col">
							<div class="col"><hl:message key="rotulo.faturamento.beneficios.periodo" />:</div>
							<div class="col"><b><%=fat_periodo != null ? TextHelper.forHtmlContent(fat_periodo) : ""%></b></div>
						</div>
						<div class="col">
							<div class="col"><hl:message key="rotulo.faturamento.beneficios.operadora" />:</div>
							<div class="col"><b><%=csa_nome != null ? TextHelper.forHtmlContent(csa_nome) : ""%></b></div>
						</div>
						<div class="col">
							<div class="col"><hl:message key="rotulo.faturamento.beneficios.data.faturamento" />:</div>
							<div class="col"><b><%=fat_data != null ? TextHelper.forHtmlContent(fat_data) : ""%></b></div>
						</div>
					</div>
					
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-sm">
			<div class="card">
				<div class="card-header">
					<h2 class="card-header-title"><hl:message key="rotulo.faturamento.beneficio.arquivos.filtro.pesquisa" /></h2>
				</div>
				<div class="card-body">
					<div class="row">
						<div class="col">
							<div class="form-group">
								<label for="matricula"><hl:message key="rotulo.faturamento.beneficio.arquivos.matricula"/></label>
								<% String maskMatricula = (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel) ? "#D20" : "#*20"); %>
								<hl:htmlinput name="matricula" di="matricula"
                          			type="text"
                          			classe="form-control"
                          			value="${param.matricula}"
                          			mask="<%=TextHelper.forHtmlAttribute(maskMatricula)%>"
                          			placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.beneficiario.informacao.matricula", responsavel) %>"/>
							</div>
						</div>
						<div class="col">
							<div class="form-group">
								<label for="cbeNumero"><hl:message key="rotulo.faturamento.beneficio.arquivos.numero.cliente"/></label>
								<hl:htmlinput name="cbeNumero" di="cbeNumero"
                          			type="text"
                          			classe="form-control"
                          			value="${param.cbeNumero}"
                          			mask="#D20"
                          			placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.beneficiario.informacao.numero.cliente", responsavel) %>"/>
							</div>
						</div>
						<div class="col">
							<div class="form-group">
								<hl:campoCPFv4 name="cpf" 
                                        description='<%=ApplicationResourcesHelper.getMessage("rotulo.faturamento.beneficio.arquivos.cpf.beneficiario", responsavel)%>'
                   						placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.beneficiario.informacao.cpf", responsavel)%>" 
                   						classe="form-control"
                   						configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_CPF)%>"
                   						value="${param.cpf}"  />
							</div>
						</div>
						<div class="col">
							<div class="form-group">
								<label></label>
								<div>
							      <a class="btn btn-outline-danger" href="#no-back" onclick="listar();"><hl:message key="rotulo.faturamento.beneficio.arquivos.listar"/></a>
	    						</div>
							</div>
						
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<% if (arquivosFaturamento != null) { %>
	<div class="row">
		<div class="col-sm">
			<table class="table table-striped table-hover">
				<thead>
					<tr>
						<th><hl:message key="rotulo.faturamento.beneficio.arquivos.matricula"/></th>
						<th><hl:message key="rotulo.faturamento.beneficio.arquivos.numero.cliente"/></th>
						<th><hl:message key="rotulo.faturamento.beneficio.arquivos.cpf.beneficiario"/></th>
						<th><hl:message key="rotulo.faturamento.beneficio.arquivos.tipo.lancamento"/></th>
						<th><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.subsidio"/></th>
						<th><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.realizado"/></th>
						<th><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.nao.realizado"/></th>
						<th><hl:message key="rotulo.faturamento.beneficio.arquivos.valor.total"/></th>
						<th><hl:message key="rotulo.faturamento.beneficios.acoes" /></th>
					</tr>
				</thead>
				<tbody>
				<%
	    			for (TransferObject arqFaturamento : arquivosFaturamento) {
	    			
	    				Integer id = (Integer) arqFaturamento.getAttribute(Columns.AFB_CODIGO);
		    			String matricula = (String) arqFaturamento.getAttribute(Columns.AFB_RSE_MATRICULA);
		    			String numCliente = (String) arqFaturamento.getAttribute(Columns.CBE_NUMERO);
		    			String cpf = (String) arqFaturamento.getAttribute(Columns.BFC_CPF);
		    			String tipoLancamento = (String) arqFaturamento.getAttribute(Columns.TLA_DESCRICAO);
		    			BigDecimal valorSubsidio = (BigDecimal) arqFaturamento.getAttribute(Columns.AFB_VALOR_SUBSIDIO);
		    			BigDecimal valorRealizado = (BigDecimal) arqFaturamento.getAttribute(Columns.AFB_VALOR_REALIZADO);
		    			BigDecimal valorNaoRealizado = (BigDecimal) arqFaturamento.getAttribute(Columns.AFB_VALOR_NAO_REALIZADO);
		    			BigDecimal valorTotal = (BigDecimal) arqFaturamento.getAttribute(Columns.AFB_VALOR_TOTAL);
	    			
				%>
					<tr>
						<td><%=TextHelper.forHtmlContent(matricula)%></td>
						<td><%=TextHelper.forHtmlContent(numCliente)%></td>
						<td><%=TextHelper.forHtmlContent(cpf)%></td>
						<td><%=TextHelper.forHtmlContent(tipoLancamento)%></td>
						<td><%=valorSubsidio != null ? TextHelper.forHtmlContent(NumberHelper.format(valorSubsidio.doubleValue(), NumberHelper.getLang())) : ""%></td>
						<td><%=valorRealizado != null ? TextHelper.forHtmlContent(NumberHelper.format(valorRealizado.doubleValue(), NumberHelper.getLang())) : ""%></td>
						<td><%=valorNaoRealizado != null ? TextHelper.forHtmlContent(NumberHelper.format(valorNaoRealizado.doubleValue(), NumberHelper.getLang())) : ""%></td>
						<td><%=valorTotal != null ? TextHelper.forHtmlContent(NumberHelper.format(valorTotal.doubleValue(), NumberHelper.getLang())) : ""%></td>
						<td>
		                	<div class="actions">
			                    <div class="dropdown">
			                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			                        <div class="form-inline">
			                          <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.faturamento.beneficios.opcoes" />" title="" data-original-title="<hl:message key="rotulo.faturamento.beneficios.opcoes" />"><svg>
			                          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span>
			                          <hl:message key="rotulo.faturamento.beneficios.opcoes" />
			                        </div>
			                      </a>
			                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
			                      	<%
			                      		if (responsavel.temPermissao(CodedValues.FUN_ALTERA_ARQ_FATURAMENTO_BENEFICIO)) {
			                      	%>
			                        <a class="dropdown-item" style="cursor: pointer;" onclick="postData('../v3/manterArquivoFaturamentoBeneficio?acao=editar&<%=SynchronizerToken.generateToken4URL(request)%>&<%="AFB_CODIGO"%>=<%=id%>')"
	                          		  aria-label="<hl:message key="rotulo.faturamento.beneficio.arquivos.editar.arquivo" />"><hl:message key="rotulo.faturamento.beneficio.arquivos.editar.arquivo" /></a>
	                          		<a class="dropdown-item" style="cursor: pointer;" onclick="excluir('<%=id%>');"
	                          		  aria-label="<hl:message key="rotulo.faturamento.beneficio.arquivos.excluir.arquivo" />"><hl:message key="rotulo.faturamento.beneficio.arquivos.excluir.arquivo" /></a>
	                          		<%
			                      		} else {
	                          		%>
	                          		<a class="dropdown-item" style="cursor: pointer;" onclick="postData('../v3/manterArquivoFaturamentoBeneficio?acao=detalhar&<%=SynchronizerToken.generateToken4URL(request)%>&<%="AFB_CODIGO"%>=<%=id%>')"
	                          		  aria-label="<hl:message key="rotulo.faturamento.beneficio.arquivos.detalhar.arquivo" />"><hl:message key="rotulo.faturamento.beneficio.arquivos.detalhar.arquivo" /></a>
	                          		<%  } %>
			                      </div>
			                    </div>
		                    </div>
	                	</td>
					</tr>
				<%	
					}
				%>
			</table>
		</div>
	</div>
	<div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
	<%
	}
	%>
	<div class="float-end">
	    <div class="btn-action">
	        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar" /></a>
      
	    </div>
  	</div>
</form>
  	
</c:set>
<c:set var="javascript">

<script type="text/javascript">

var f0 = document.forms[0];

function listar(){
	f0.submit();	
}

function excluir(id) {

	if (confirm('<hl:message key="mensagem.faturamento.beneficio.arquivos.deseja.excluir" />')) {
		f0.action = '../v3/manterArquivoFaturamentoBeneficio?acao=excluir&<%=SynchronizerToken.generateToken4URL(request)%>&<%="AFB_CODIGO"%>=' + id;
		f0.submit();
	}
	
	return false;
	
}

</script>

</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>