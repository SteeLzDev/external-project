<%--
* <p>Title: listarFaturamento_v4</p>
* <p>Description: Página de lista de Faturamento de Beneficios</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: luiz.consani $
* $Revision: 23998 $
* $Date: 2018-03-28 12:41:10 -0300 (Qua, 28 mar 2018) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	List<TransferObject> faturamentoBeneficio = (List<TransferObject>) request.getAttribute("faturamentoBeneficio");
	List consignatarias = (List) request.getAttribute("consignatarias");
    String csaSelecionada = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="title">
	<hl:message key="rotulo.faturamento.beneficios.titulo" />
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-beneficios"></use>
</c:set>

<c:set var="bodyContent">
	<form name="form1" method="post" action="../v3/consultarFaturamentos?acao=iniciar">
		<div class="row">
			<div class="col-sm-4">
				<div class="card mb-2">
					<div class="card-header">
						<h2 class="card-header-title">
							<hl:message key="rotulo.faturamento.beneficios.filtro" />
						</h2>
					</div>
						<div class="card-body">
							<div class="row">
								<div class="col-sm">
									<div class="form-group">
										<label for="FAT_PERIODO"><hl:message key="rotulo.faturamento.beneficios.periodo" /></label>
										<hl:htmlinput name="FAT_PERIODO" di="FAT_PERIODO" type="text" classe="form-control" size="10" 
	                                mask="DD/DDDD" placeHolder="MM/AAAA" ariaLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.faturamento.beneficios.periodo", responsavel)%>'
	                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "FAT_PERIODO"))%>" />
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-sm">
								  <div class="form-group">
                	                <label for="CSA_CODIGO"><hl:message key="rotulo.faturamento.beneficios.operadora"/></label>
                	                <select name="CSA_CODIGO" id="CSA_CODIGO" class="form-control"
                                       onfocus="SetarEventoMascaraV4(this,'#*200',true);" onblur="fout(this);ValidaMascaraV4(this);" 
                                       style="background-color: white; color: black;">
                                       <option value="" <%=(!TextHelper.isNull(csaSelecionada) ? "" : "selected")%>><%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%></option> 
                	                <%
                	                Iterator itCsa = consignatarias.iterator();
                	                while (itCsa.hasNext()) {
                	                    CustomTransferObject csa = (CustomTransferObject) itCsa.next();
                                        String csaCodigo = csa.getAttribute(Columns.CSA_CODIGO).toString();
                                        String csaIdentificador = csa.getAttribute(Columns.CSA_IDENTIFICADOR).toString();
                                        String csaNome = !TextHelper.isNull(csa.getAttribute(Columns.CSA_NOME_ABREV)) ? csa.getAttribute(Columns.CSA_NOME_ABREV).toString() : csa.getAttribute(Columns.CSA_NOME).toString();
                                    %>
                                       <option value="<%=csaCodigo%>" <%=(!TextHelper.isNull(csaSelecionada) && csaCodigo.equals(csaSelecionada) ? "selected" : "")%>><%=csaIdentificador%> - <%=csaNome%></option>
                                    <%
                    	                }
                	                %>
                	                </select>
                	              </div>
								</div>
							</div>
						</div>
				</div>
  				<div class="row float-end">
  					<div class="col-sm">
  						<div class="btn-action">
  			    		<button class="btn btn-primary">
                  <svg width="20">
                    <use xlink:href="#i-consultar"></use>
                  </svg>
                  <hl:message key="rotulo.acao.pesquisar"/>
                </button>
  			  		</div>
  					</div>
  				</div>
			</div>
			<div class="col-sm">
				<div class="card mb-2">
					<div class="card-header">
						<h2 class="card-header-title">
		        	<hl:message key="rotulo.faturamento.beneficios.titulo" />
		        </h2>
					</div>
					<div class="card-body table-responsive ">
						<table class="table table-striped table-hover">
							<thead>
								<tr>
									<th><hl:message key="rotulo.faturamento.beneficios.periodo" /></th>
									<th><hl:message key="rotulo.faturamento.beneficios.operadora.identificador" /></th>
									<th><hl:message key="rotulo.faturamento.beneficios.operadora.nome" /></th>
									<th><hl:message key="rotulo.faturamento.beneficios.data.faturamento" /></th>
									<th><hl:message key="rotulo.faturamento.beneficios.acoes" /></th>
								</tr>
							</thead>
							<tbody>
	              <%
						Iterator it = faturamentoBeneficio.iterator();
	              		while (it.hasNext()) {
	              			CustomTransferObject fat = (CustomTransferObject) it.next();
	              			
	              			String fat_codigo = (String) fat.getAttribute(Columns.FAT_CODIGO);
	              			String fat_periodo = DateHelper.format((Date)fat.getAttribute(Columns.FAT_PERIODO), "MM/yyyy");
	              			String csa_identificador = (String) fat.getAttribute(Columns.CSA_IDENTIFICADOR);
	              			String csa_nome = (String) fat.getAttribute(Columns.CSA_NOME);
	              			String fat_data = DateHelper.format((Date)fat.getAttribute(Columns.FAT_DATA), "dd/MM/yyyy");
	              			
	              			String csa_codigo = (String) fat.getAttribute(Columns.CSA_CODIGO);
	              			
	              %>
							<tr>
								<td><%=fat_periodo != null ? TextHelper.forHtmlContent(fat_periodo) : ""%></td>
            	                <td><%=csa_identificador != null ? TextHelper.forHtmlContent(csa_identificador) : ""%></td>
            	                <td><%=csa_nome != null ? TextHelper.forHtmlContent(csa_nome) : ""%></td>
            	                <td><%=fat_data != null ? TextHelper.forHtmlContent(fat_data) : ""%></td>
            	                <td>
            	                	<div class="actions">
            	                    <div class="dropdown">
            	                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            	                        <div class="form-inline">
            	                          <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.faturamento.beneficios.opcoes" />' title="" data-original-title='<hl:message key="rotulo.faturamento.beneficios.opcoes" />'><svg>
            	                          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span>
            	                          <hl:message key="rotulo.faturamento.beneficios.opcoes" />
            	                        </div>
            	                      </a>
            	                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
            	                        <a class="dropdown-item" style="cursor: pointer;" onClick="postData('../v3/consultarFaturamentos?acao=consultar&FAT_CODIGO=<%=TextHelper.forJavaScript(fat_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"
            	                          aria-label='<hl:message key="rotulo.faturamento.beneficios.detalhar.faturamento" />'><hl:message key="rotulo.faturamento.beneficios.detalhar.faturamento" /></a>
                                        <a class="dropdown-item" style="cursor: pointer;" onClick="postData('../v3/manterNotaFiscalBeneficio?acao=iniciar&faturamentoCodigo=<%=TextHelper.forJavaScript(fat_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"
                                        aria-label='<hl:message key="rotulo.notafiscal.faturamento.beneficio.menu" />'><hl:message key="rotulo.notafiscal.faturamento.beneficio.menu" /></a>
            	                      </div>
            	                    </div>
            	                  </div>
            	                </td>
							</tr>
					<% } %>
							</tbody>
	            <tfoot>
	              <tr>
	                <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.faturamento.beneficios.listagem", responsavel)%></td>
	              </tr>
	            </tfoot>
						</table>
					</div>
				</div>
          	    <div class="float-end">
                  <div class="btn-action">
                    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')" id="btnVoltar"><hl:message key="rotulo.botao.voltar" /></a>
                  </div>
                </div>
			</div>
		</div>
	</form>
</c:set>

<c:set var="javascript">
</c:set>

<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>