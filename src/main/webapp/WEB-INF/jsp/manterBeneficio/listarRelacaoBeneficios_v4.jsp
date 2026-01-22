<%--
* <p>Title: listarRelacaoBeneficios</p>
* <p>Description: Listar relação benefícios v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ page import="java.util.Date"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.values.StatusContratoBeneficioEnum" %>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session"
  class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

	boolean possuiBeneficiosAtivos = (boolean) request.getAttribute("possuiBeneficiosAtivos");
	
	List<TransferObject> relacaoBeneficios = (List<TransferObject>) request.getAttribute("relacaoBeneficios");
	String ser_codigo = (String) request.getAttribute(Columns.SER_CODIGO);
  	String rse_codigo = (String) request.getAttribute(Columns.RSE_CODIGO);
	
%>
<c:set var="title">
  <hl:message key="rotulo.relacao.beneficios.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">

<%

	if (possuiBeneficiosAtivos && (responsavel.isSup() || responsavel.isSer())) {

%>

  <div class="row">
     <div class="col-sm-12 col-md-12 mb-2">
       <div class="float-end">
       <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, CodedValues.TPC_NAO, responsavel)) { %>
         <button aria-expanded="false" class="btn btn-primary d-print-none" type="submit" onClick="postData('../v3/visualizarTermoAdesaoServico?acao=iniciar&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.relacao.beneficios.termo.adesao" /></button>
       <% } %>
       </div>
     </div>
  </div>

<%
	}
%>

  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message key="rotulo.relacao.beneficios.ativos" />
          </h2>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th><hl:message
                    key="rotulo.relacao.beneficios.descricao" /></th>
                <th><hl:message
                    key="rotulo.relacao.beneficios.tipo" /></th>
                <th><hl:message
                    key="rotulo.beneficio.operadora.singular" /></th>
                <th><hl:message
                    key="rotulo.relacao.beneficios.acoes" /></th>
              </tr>
            </thead>
            <tbody>
              <%
              	Iterator it1 = relacaoBeneficios.iterator();
              		while (it1.hasNext()) {
              			CustomTransferObject relacaoBeneficio = (CustomTransferObject) it1.next();
              			String scb_codigo = (String) relacaoBeneficio.getAttribute(Columns.SCB_CODIGO);
              			String ben_descricao = (String) relacaoBeneficio.getAttribute(Columns.BEN_DESCRICAO);
              			String nse_descricao = (String) relacaoBeneficio.getAttribute(Columns.NSE_DESCRICAO);
              			String nse_codigo = (String) relacaoBeneficio.getAttribute(Columns.NSE_CODIGO);
                        String acao = "";
                        if (nse_codigo.equals(CodedValues.NSE_PLANO_DE_SAUDE)) {
                            acao = "planoSaude"; 
                        } else if (nse_codigo.equals(CodedValues.NSE_PLANO_ODONTOLOGICO)) {
                            acao = "planoOdontologico"; 
                        }
                                                            
              			String ben_codigo = (String) relacaoBeneficio.getAttribute(Columns.BEN_CODIGO);
              			String csa_nome = (String) relacaoBeneficio.getAttribute(Columns.CSA_NOME);

              			if (!scb_codigo.equals(StatusContratoBeneficioEnum.CANCELADO.getCodigo())) {
              %>
              <tr>
                <td><%=ben_descricao != null ? TextHelper.forHtmlContent(ben_descricao) : ""%></td>
                <td><%=nse_descricao != null ? TextHelper.forHtmlContent(nse_descricao) : ""%></td>
                <td><%=csa_nome != null ? TextHelper.forHtmlContent(csa_nome) : ""%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#"
                        role="button" id="userMenu"
                        data-bs-toggle="dropdown" aria-haspopup="true"
                        aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip"
                            aria-label='<hl:message key="rotulo.relacao.beneficios.opcoes" />' title=""
                            data-original-title='<hl:message key="rotulo.relacao.beneficios.opcoes" />'><svg>
                          <use
                                xmlns:xlink="http://www.w3.org/1999/xlink"
                                xlink:href="#i-engrenagem"></use></svg></span>
                          <hl:message
                            key="rotulo.relacao.beneficios.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right"
                        aria-labelledby="userMenu">
                        <a class="dropdown-item"
                          style="cursor: pointer;"
                          onClick="postData('../v3/relacaoBeneficios?acao=consultar&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&contratosAtivos=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"
                          aria-label='<hl:message key="rotulo.relacao.beneficios.detalhar.beneficio" />'><hl:message
                            key="rotulo.relacao.beneficios.detalhar.beneficio" /></a>
                        <% if (responsavel.temPermissao(CodedValues.FUN_SIMULACAO_CONTRATO_BENEFICIO) && !TextHelper.isNull(acao)) { %>
                        <a class="dropdown-item"
                          style="cursor: pointer;"
                          onClick="postData('../v3/incluirBeneficiarioSimulacaoBeneficios?acao=<%=acao%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rse_codigo)%>&reiniciarLocalSession=false'); return false;"
                          aria-label='<hl:message key="rotulo.relacao.beneficios.detalhar.beneficio" />'><hl:message
                            key="rotulo.botao.novo.beneficiario"/></a> 
                        <% } %>
                       <% if (responsavel.temPermissao(CodedValues.FUN_SIMULACAO_ALTERACAO_CONTRATO_BENEFICIO)) { %>
                        <a class="dropdown-item"
                          style="cursor: pointer;"
                          onClick="postData('../v3/simulacaoAlteracaoBeneficios?acao=simular&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&rseCodigo=<%=TextHelper.forJavaScript(rse_codigo)%>&nseCodigo=<%=TextHelper.forJavaScript(nse_codigo)%>'); return false;"
                         aria-label='<hl:message key="rotulo.relacao.beneficios.detalhar.beneficio" />'><hl:message
                            key="rotulo.relacao.beneficios.detalhar.alterar.plano" /></a>
                      <% } %>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
              <%
              		}
              	}
              %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="4"><hl:message
                    key="rotulo.relacao.beneficios.listagem.beneficios.ativos" />
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>
  <%
  	if (responsavel.isSup() || responsavel.isSer()) {
  %>
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message key="rotulo.relacao.beneficios.cancelados" />
          </h2>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th><hl:message
                    key="rotulo.relacao.beneficios.descricao" /></th>
                <th><hl:message
                    key="rotulo.relacao.beneficios.tipo" /></th>
                <th><hl:message
                    key="rotulo.beneficio.operadora.singular" /></th>
                <th><hl:message
                    key="rotulo.relacao.beneficios.acoes" /></th>
              </tr>
            </thead>
            <tbody>
              <%
              Iterator it2 = relacaoBeneficios.iterator();
              				while (it2.hasNext()) {
              					CustomTransferObject relacaoBeneficio = (CustomTransferObject) it2.next();
                                String scb_codigo = (String) relacaoBeneficio.getAttribute(Columns.SCB_CODIGO);
              					String ben_descricao = (String) relacaoBeneficio.getAttribute(Columns.BEN_DESCRICAO);
              					String nse_descricao = (String) relacaoBeneficio.getAttribute(Columns.NSE_DESCRICAO);
              					String ben_codigo = (String) relacaoBeneficio.getAttribute(Columns.BEN_CODIGO);
              					String csa_nome = (String) relacaoBeneficio.getAttribute(Columns.CSA_NOME);
                        
              					if(scb_codigo.equals(StatusContratoBeneficioEnum.CANCELADO.getCodigo())) {
              %>
              <tr>
                <td><%=ben_descricao != null ? TextHelper.forHtmlContent(ben_descricao) : ""%></td>
                <td><%=nse_descricao != null ? TextHelper.forHtmlContent(nse_descricao) : ""%></td>
                <td><%=csa_nome != null ? TextHelper.forHtmlContent(csa_nome) : ""%></td>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#"
                        role="button" id="userMenu"
                        data-bs-toggle="dropdown" aria-haspopup="true"
                        aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip"
                            aria-label='<hl:message key="rotulo.relacao.beneficios.opcoes" />' title=""
                            data-original-title='<hl:message key="rotulo.relacao.beneficios.opcoes" />'><svg>
                          <use
                                xmlns:xlink="http://www.w3.org/1999/xlink"
                                xlink:href="#i-engrenagem"></use></svg></span>
                          <hl:message
                            key="rotulo.relacao.beneficios.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right"
                        aria-labelledby="userMenu">
                        <a class="dropdown-item"
                          style="cursor: pointer;"
                          onClick="postData('../v3/relacaoBeneficios?acao=consultar&<%=Columns.getColumnName(Columns.SER_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&contratosAtivos=false&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"
                          aria-label='<hl:message key="rotulo.relacao.beneficios.detalhar.beneficio" />'><hl:message
                            key="rotulo.relacao.beneficios.detalhar.beneficio" /></a>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
              <%
                        }
              	}
              %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="4"><hl:message
                    key="rotulo.relacao.beneficios.listagem.beneficios.cancelados" />
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>
  <%
  	}
  %>
  <div class="float-end">
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back"
        onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"
        id="btnVoltar"><hl:message key="rotulo.botao.voltar" /></a>
    </div>
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>