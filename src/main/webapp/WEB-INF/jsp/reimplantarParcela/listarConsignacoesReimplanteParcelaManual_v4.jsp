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
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String rseCodigo = (String) request.getAttribute("rseCodigo");
List<?> consignacoesReimplanteManual = (List<?>) request.getAttribute("consignacoesReimplanteManual");

%>
<c:set var="title">
  <hl:message key="rotulo.reimplantar.parcela.manual.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <div class="row d-print-none">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button id="acoes" class="btn btn-primary" type="submit" onClick="imprimir()"><hl:message key="rotulo.botao.imprimir"/></button>
        </div>
      </div>
    </div>
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon">
          <svg width="24">
              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consignacao"></use>
          </svg>
        </span>
        <h2 class="card-header-title">
           <hl:message key="rotulo.consignacao.plural"/>
        </h2>
      </div>
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th><hl:message key="rotulo.consignataria.singular"/></th>
              <th><hl:message key="rotulo.responsavel.singular"/></th>
              <th><hl:message key="rotulo.consignacao.numero.ade.abreviado"/></th>
              <th><hl:message key="rotulo.consignacao.identificador"/></th>
              <th><hl:message key="rotulo.servico.singular"/></th>
              <th><hl:message key="rotulo.consignacao.data.inclusao"/></th>
              <th><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
              <th><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
              <th><hl:message key="rotulo.parcela.numero"/></th>
              <th><hl:message key="rotulo.consignacao.pagas"/></th>
              <th><hl:message key="rotulo.consignacao.status"/></th>
              <th><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
            <% Iterator<?> it = consignacoesReimplanteManual.iterator();
              while (it.hasNext()) {
                CustomTransferObject consignacao = (CustomTransferObject)it.next();
                String adeCodigo = (String) consignacao.getAttribute(Columns.ADE_CODIGO);
                String csaIdentificador = (String)consignacao.getAttribute(Columns.CSA_IDENTIFICADOR);
                String csaNomeAbrev = (String)consignacao.getAttribute(Columns.CSA_NOME_ABREV);
                String cnvCodVerba = (String)consignacao.getAttribute(Columns.CNV_COD_VERBA);
                String svcDescricao = (String)consignacao.getAttribute(Columns.SVC_DESCRICAO);
                String reimplanteManual = (String)consignacao.getAttribute("REIMPLANTA_MANUAL");%>
            <tr>
              <td><%=TextHelper.forHtmlContent(csaIdentificador +"-"+ csaNomeAbrev)%></td>
              <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.USU_LOGIN))%></td>
              <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.ADE_NUMERO))%></td>
              <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.ADE_IDENTIFICADOR))%></td>
              <td><%=TextHelper.forHtmlContent(cnvCodVerba +"-"+ svcDescricao)%></td>
              <td><%=TextHelper.forHtmlContent(DateHelper.format((Date)consignacao.getAttribute(Columns.ADE_DATA), "dd/MM/yyyy"))%></td>
              <td><%=TextHelper.forHtmlContent(NumberHelper.format(Double.parseDouble(consignacao.getAttribute(Columns.ADE_VLR).toString()), NumberHelper.getLang()))%></td>
              <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.ADE_PRAZO))%></td>
              <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.PRD_NUMERO))%></td>
              <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.ADE_PRD_PAGAS))%></td>
              <td><%=TextHelper.forHtmlContent(consignacao.getAttribute(Columns.SAD_DESCRICAO))%></td>
              <td>
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                      </span>
                      <hl:message key="rotulo.acoes" />
                    </div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                      <a class="dropdown-item" href="#" onClick="validaReimplanteParcela('<%=reimplanteManual%>','<%=TextHelper.forJavaScriptAttribute(adeCodigo)%>'); return false;">
                        <hl:message key="rotulo.reimplantar.parcela.manual.acao" />
                      </a> 
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=<%=TextHelper.forJavaScriptAttribute(adeCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                        <hl:message key="rotulo.acoes.editar" />
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
              <td colspan="13"><%=ApplicationResourcesHelper.getMessage("rotulo.paginacao.titulo.rotulo.paginacao.titulo.bloqueio.pendencia.saldo.devedor", responsavel) + " - " %>
                <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloReimplantarParcela"))%></span>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
      <div class="card-footer">
        <% request.setAttribute("_indice", "ReimplantarParcela"); %>
        <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>  
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    function validaReimplanteParcela(reimplanta, adeCodigo){
        if (reimplanta === "true") {
        	postData('../v3/reimplantarParcelaManual?acao=editar&ADE_CODIGO=' + adeCodigo + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
        } else {
            alert ('<hl:message key="mensagem.info.reimplantar.parcela.manual.reimplante.automatico.contrato"/>');
            return false;
        }
    }

    function imprimir() {
      window.print();
    }
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>