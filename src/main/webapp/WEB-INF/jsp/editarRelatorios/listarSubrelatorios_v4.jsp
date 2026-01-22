<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.Subrelatorio"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<?> subrelatorios = (List<?>) request.getAttribute("subrelatorios");
String tituloRelatorio = (String) request.getAttribute("tituloRelatorio");
String relCodigo = (String) request.getAttribute("relCodigo");
int offset = (int) request.getAttribute("offset");
%>
<c:set var="title">
   <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#<%= TextHelper.forHtml(request.getAttribute("imageHeader")) != null ? TextHelper.forHtml(request.getAttribute("imageHeader")) : "i-manutencao"%>"></use>
</c:set>
<c:set var="bodyContent">
<div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/editarSubrelatorio?acao=iniciarEdicao&tipo=inserir&relCodigo=<%=relCodigo%>&_skip_history_=true&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.subrelatorio.criar.novo"/></a>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header hasIcon">
          <h2 class="card-header-title"><hl:message key="rotulo.subrelatorio.lista"/> - <%=TextHelper.forHtmlContent(tituloRelatorio)%></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.subrelatorio.nome.arquivo"/></th>
                <th scope="col"><hl:message key="rotulo.subrelatorio.nome.parametro"/></th>
                <th scope="col"><hl:message key="rotulo.subrelatorio.fonte.original"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <%
              String sreCodigo, sreTemplateJasper, sreNomeParametro, fonteDados; 
              Iterator<?> it = subrelatorios.iterator();
              while (it.hasNext()) {
                  CustomTransferObject subrelatorio = (CustomTransferObject)it.next();
                  sreCodigo = (String)subrelatorio.getAttribute(Columns.SRE_CODIGO);
                  sreTemplateJasper = (String)subrelatorio.getAttribute(Columns.SRE_TEMPLATE_JASPER);
                  sreNomeParametro = (String)subrelatorio.getAttribute(Columns.SRE_NOME_PARAMETRO);
                  fonteDados = TextHelper.isNull((String)subrelatorio.getAttribute(Columns.SRE_TEMPLATE_SQL)) ? "Sim" : "Não";
              %>
              <tr>
              <td><%=TextHelper.forHtmlContent(sreTemplateJasper)%></td>
              <td><%=TextHelper.forHtmlContent(sreNomeParametro)%></td>
              <td><%=TextHelper.forHtmlContent(fonteDados)%></td> 
              <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes" />" aria-label="<hl:message key="rotulo.botao.opcoes" />"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarSubrelatorio?acao=iniciarEdicao&tipo=editar&sreCodigo=<%=TextHelper.forJavaScriptAttribute(sreCodigo)%>&relCodigo=<%=TextHelper.forJavaScriptAttribute(relCodigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarSubrelatorio?acao=excluir&sreCodigo=<%=TextHelper.forJavaScriptAttribute(sreCodigo)%>&relCodigo=<%=TextHelper.forJavaScriptAttribute(relCodigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                      </div>
                    </div>
                  </div>
                </td>  
              </tr>
              <% } %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="4"><hl:message key="rotulo.subrelatorio.listagem"/> - <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
              </tr>
            </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
      </div>
      <div class="btn-action">
         <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
</c:set>
  <t:page_v4>
      <jsp:attribute name="header">${title}</jsp:attribute>
      <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
      <jsp:attribute name="javascript">${javascript}</jsp:attribute>
      <jsp:body>${bodyContent}</jsp:body>
  </t:page_v4>