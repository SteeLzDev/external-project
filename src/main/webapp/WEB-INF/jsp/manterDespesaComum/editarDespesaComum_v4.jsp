<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.dto.entidade.ParamSvcTO"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.values.StatusDespesaComumEnum"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	String msgErro = (String) request.getAttribute("msgErro");
	String csaNome = (String) request.getAttribute("csaNome");
	String decCodigo = (String) request.getAttribute("decCodigo");
	TransferObject despesaComum = (TransferObject) request.getAttribute("despesaComum");
	String statusDespesaComum = (String) request.getAttribute("statusDespesaComum");
	List<?> despesasIndividuais = (List<?>) request.getAttribute("despesasIndividuais");
	List<?> hist = (List<?>) request.getAttribute("hist");
%>

<c:set var="title">
	<%=TextHelper.forHtml(!responsavel.isSer() ? ApplicationResourcesHelper.getMessage("rotulo.despesa.comum.manutencao.titulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.despesa.comum.visualizacao.titulo", responsavel))%>
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
	<form action="../v3/consultarDespesaComum?acao=consultar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
		<div class="row">
			<div class="col-sm-11 col-md-12">
			  <div class="card">
				<div class="card-header hasIcon pl-3">
				  <h2 class="card-header-title"><hl:message key="rotulo.dados.despesa.comum"/></h2>
				</div>
				<div class="card-body ">
					<dl class="row data-list firefox-print-fix"	>
						<dt class="col-6"><hl:message key="rotulo.identificador.singular"/>:</dt>
						<dd class="col-6"><%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.DEC_IDENTIFICADOR) != null ? (String)despesaComum.getAttribute(Columns.DEC_IDENTIFICADOR) : "")%></dd>

						<dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt>
						<dd class="col-6"><%=TextHelper.forHtmlContent(csaNome)%></dd>

						<dt class="col-6"><hl:message key="rotulo.endereco.singular"/>:</dt>
						<dd class="col-6"><%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.ECH_IDENTIFICADOR) + " - " + (String)despesaComum.getAttribute(Columns.ECH_DESCRICAO))%></dd>

						<dt class="col-6"><hl:message key="rotulo.servico.singular"/>:</dt>
						<dd class="col-6"><%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.PLA_IDENTIFICADOR) + " - " + (String)despesaComum.getAttribute(Columns.PLA_DESCRICAO))%></dd>

						<dt class="col-6"><hl:message key="rotulo.valor.singular"/>:</dt>
						<dd class="col-6"><hl:message key="rotulo.moeda"/> <%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.DEC_VALOR) != null ? NumberHelper.format(((BigDecimal)despesaComum.getAttribute(Columns.DEC_VALOR)).doubleValue(), NumberHelper.getLang(), 2, 8) : BigDecimal.ZERO)%></dd>

						<dt class="col-6"><hl:message key="rotulo.valor.singular"/>:</dt>
						<dd class="col-6"><hl:message key="rotulo.moeda"/> <%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.DEC_VALOR) != null ? NumberHelper.format(((BigDecimal)despesaComum.getAttribute(Columns.DEC_VALOR)).doubleValue(), NumberHelper.getLang(), 2, 8) : BigDecimal.ZERO)%></dd>

						<dt class="col-6"><hl:message key="rotulo.consignacao.prazo"/>:</dt>
						<dd class="col-6"><%= despesaComum.getAttribute(Columns.DEC_PRAZO) != null ? (Integer)despesaComum.getAttribute(Columns.DEC_PRAZO) : ApplicationResourcesHelper.getMessage("rotulo.indeterminado.singular", responsavel)%></dd>

						<dt class="col-6"><hl:message key="rotulo.despesa.comum.inclusao"/>:</dt>
						<dd class="col-6"><%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.DEC_DATA) != null ? DateHelper.toDateTimeString((Date)despesaComum.getAttribute(Columns.DEC_DATA)) : "")%></dd>

						<dt class="col-6"><hl:message key="rotulo.despesa.comum.inicio"/>:</dt>
						<dd class="col-6"><%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.DEC_DATA_INI) != null ? DateHelper.toPeriodString((Date)despesaComum.getAttribute(Columns.DEC_DATA_INI)) : "" )%></dd>

						<dt class="col-6"><hl:message key="rotulo.despesa.comum.fim"/>:</dt>
						<dd class="col-6"><%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.DEC_DATA_INI) != null ? DateHelper.toPeriodString((Date)despesaComum.getAttribute(Columns.DEC_DATA_FIM)) : "")%></dd>

						<% if(despesaComum.getAttribute(Columns.POS_DESCRICAO) != null) { %>
							<dt class="col-6"><hl:message key="rotulo.posto.singular"/>:</dt>
							<dd class="col-6"><%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.POS_DESCRICAO))%></dd>
						<% } %>

						<dt class="col-6"><hl:message key="rotulo.despesa.comum.situacao"/>:</dt>
						<dd class="col-6"><%=TextHelper.forHtmlContent(despesaComum.getAttribute(Columns.SDC_DESCRICAO))%></dd>
					</dl>
				</div>
			  </div>
			</div>
			<% if (responsavel.temPermissao(CodedValues.FUN_CANCELAR_DESPESA_COMUM) && statusDespesaComum.equals(StatusDespesaComumEnum.ATIVO.getCodigo())) { %>
				<div class="btn-action">
					<a class="btn btn-primary" href="#no-back" onclick="chamaCancelamento(); return false;"><hl:message key="rotulo.botao.cancela.despesa.comum"/></a>
				</div>
			<% } %>
		</div>

		<% if(despesasIndividuais != null && despesasIndividuais.size() > 0) { %>
			<div class="row">
				<div class="col-sm-11 col-md-12">
					<div class="card">
						<div class="card-header hasIcon pl-3">
							<h2 class="card-header-title"></h2>
						</div>
						<div class="card-body table-responsive ">
							<table class="table table-striped table-hover">
								<thead>
								  <tr>
                                    <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
                                    <th scope="col"><hl:message key="rotulo.permissionario.singular"/></th>
                                    <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
                                    <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
                                    <th scope="col"><hl:message key="rotulo.permissionario.complemento"/></th>
                                    <th scope="col"><hl:message key="rotulo.permissionario.status"/></th>
                                    <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
                                    <th scope="col"><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
                                    <th scope="col"><hl:message key="rotulo.despesa.comum.inicio"/></th>
                                    <th scope="col"><hl:message key="rotulo.despesa.comum.fim"/></th>
                                    <% if (responsavel.temPermissao(CodedValues.FUN_INC_DESPESA_INDIVIDUAL)) { %>
                                      <th scope="col"><hl:message key="rotulo.acoes"/></th>
                                    <% } %>
                                  </tr>
								</thead>
								<tbody>
    								<%
    								  Iterator<?> it = despesasIndividuais.iterator();
    					              Long adeNumero;
    					              String nome, adeTipoVlr, status, cpf, matricula, complemento;
    					              BigDecimal valor;
    					              Integer prazo;
    					              Date dataInicio, dataFim;
    					              
    					              while (it.hasNext()) {
    					                CustomTransferObject despesaIndividual = (CustomTransferObject)it.next();
    					                
    					                adeNumero = (Long)despesaIndividual.getAttribute(Columns.ADE_NUMERO);
    					                nome = (String)despesaIndividual.getAttribute(Columns.SER_NOME);
    					                valor = (BigDecimal)despesaIndividual.getAttribute(Columns.ADE_VLR);
    					                prazo = (Integer)despesaIndividual.getAttribute(Columns.ADE_PRAZO);
    					                dataInicio = (Date)despesaIndividual.getAttribute(Columns.ADE_ANO_MES_INI);
    					                dataFim = (Date)despesaIndividual.getAttribute(Columns.ADE_ANO_MES_FIM);
    					                adeTipoVlr = (String) despesaIndividual.getAttribute(Columns.ADE_TIPO_VLR);
    				                    status = (String)despesaIndividual.getAttribute(Columns.SAD_DESCRICAO);
    				                    cpf = (String)despesaIndividual.getAttribute(Columns.SER_CPF);
    				                    matricula = (String)despesaIndividual.getAttribute(Columns.RSE_MATRICULA);
    				                    complemento = (String)despesaIndividual.getAttribute(Columns.PRM_COMPL_ENDERECO);
    								  %>
    								  <tr>
                                        <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
                    	                <td><%=TextHelper.forHtmlContent(nome)%></td>
                                        <td><%=TextHelper.forHtmlContent(matricula)%></td>
                                        <td><%=TextHelper.forHtmlContent(cpf)%></td>
                                        <td><%=TextHelper.forHtmlContent(complemento)%></td>
                                        <td><%=TextHelper.forHtmlContent(status)%></td>
                                        <td><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))%></td>
                                        <td><%=NumberHelper.format(valor.doubleValue(), NumberHelper.getLang(), 2, 8)%></td>
                                        <%if (prazo != null) {%>
                                          <td><%=TextHelper.forHtmlContent(prazo)%></td>
                                        <%} else{ %>
                                          <td><hl:message key="rotulo.indeterminado.abreviado"/></td>
                                        <%} %>
                                        <td><%=DateHelper.toPeriodString(dataInicio)%></td>
                                        <td><%=DateHelper.toPeriodString(dataFim)%></td>
                                        <td>
                                          <div class="actions">
                                            <div class="dropdown">
                                              <a class="dropdown-toggle ico-action" href="#" role="button" id="despesaIndividualMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                <div class="form-inline">
                                                <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                                                  <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                                                </span> <hl:message key="rotulo.botao.opcoes"/>
                                                </div>
                                              </a>
                                              <div class="dropdown-menu dropdown-menu-right" aria-labelledby="despesaIndividualMenu">
                                                <% if (responsavel.temPermissao(CodedValues.FUN_INC_DESPESA_INDIVIDUAL)) { %>
                                                  <a class="dropdown-item" href="#no-back" onclick="chamaEdicaoIndividual('<%=TextHelper.forJavaScript((despesaIndividual.getAttribute(Columns.ADE_CODIGO)))%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                                                <% } %>
                                              </div>
                                            </div>
                                          </div>
                                        </td>
    								  </tr>
    								  <%
    								    }
    								  %>
								</tbody>
							  </table>
						</div>
					</div>
				</div>
			</div>
		<% } %>	
		<% if(hist != null && hist.size() > 0) { %>
			<ul class="nav nav-tabs responsive-tabs" id="consignacaoInfo" role="tablist">
				<li class="nav-item">
        			<a class="nav-link active" id="historico-tab" data-bs-toggle="tab" href="#historico" role="tab" aria-controls="profile" aria-selected="true"><hl:message key="rotulo.ocorrencia.titulo"/></a>
				</li>
			</ul>
			<%-- Tab panes --%>
			<div class="tab-content" id="despesaComumInfo">
				<div class="tab-pane fade show active" id="historico" role="tabpanel" aria-labelledby="historico-tab">
					<%-- Utiliza a tag library ListaHistoricoDespesaComumTag.java para exibir o histórico de ocorrências da despesa comum --%>
					<% pageContext.setAttribute("historico", hist); %>
					<hl:listaHistoricoDespesaComumv4 name="historico" table="true" />
				</div>
			</div>
		<% } %>
		<div class="btn-action">
			<a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
		</div>
	</form>
</c:set>

<c:set var="javascript">
	<script type="text/javascript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
	<script type="text/javascript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
	<link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
	<script type="text/JavaScript">
		f0 = document.forms[0];

		function chamaEdicaoIndividual(codigoAde) {
			postData("../v3/consultarDespesaIndividual?acao=detalharConsignacao&ADE_CODIGO=" + codigoAde + "&<%=SynchronizerToken.generateToken4URL(request)%>");
		}
		
		function chamaCancelamento(){
			if(confirm('<hl:message key="mensagem.confirmacao.cancelamento.despesa.comum"/>')){
				f0.action += "&cancelar=1&decCodigo=<%=TextHelper.forJavaScriptBlock(decCodigo)%>"; 
				f0.submit();
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