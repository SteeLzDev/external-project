<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<?> motivoOperacao = (List<?>) request.getAttribute("motivoOperacao");
%>
<c:set var="title">
<hl:message key="rotulo.tipo.motivo.singular"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/motivoOperacao?acao=editar&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.motivo.novo"/></a>
        </div>
      </div>
    </div>
  </div>
  <div class="card">
    <div class="card-header hasIcon pl-3">
      <h2 class="card-header-title"><hl:message key="rotulo.tipo.motivo.singular"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.tipo.motivo.codigo"/></th>
            <th scope="col"><hl:message key="rotulo.tipo.motivo.descricao"/></th>
            <th scope="col"><hl:message key="rotulo.tipo.motivo.tipo.entidade"/></th>
            <th scope="col"><hl:message key="rotulo.tipo.motivo.tipo.situacao"/></th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
         <%=JspHelper.msgRstVazio(motivoOperacao.size()==0, 13, responsavel) %>
         <%
           Iterator<?> it = motivoOperacao.iterator();
            while (it.hasNext()) {
              CustomTransferObject tipoMotivoOperacaoCTO = (CustomTransferObject)it.next();
              String tmo_codigo = (String)tipoMotivoOperacaoCTO.getAttribute(Columns.TMO_CODIGO);
              String tmo_descricao = (String)tipoMotivoOperacaoCTO.getAttribute(Columns.TMO_DESCRICAO);
              String tmo_identificador = (String)tipoMotivoOperacaoCTO.getAttribute(Columns.TMO_IDENTIFICADOR);
              Short tmo_ativo = (Short)tipoMotivoOperacaoCTO.getAttribute(Columns.TMO_ATIVO);
              String ten_descricao = (String)tipoMotivoOperacaoCTO.getAttribute(Columns.TEN_DESCRICAO);
              String msgMotivoBloqueadoDesbloqueado = tmo_ativo.toString().equals(CodedValues.STS_ATIVO.toString()) ? ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.desbloqueado", responsavel): ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.bloqueado", responsavel);
         %>
         <tr>
          <td><%=TextHelper.forHtmlContent(tmo_identificador.toUpperCase())%></td>
          <td><%=TextHelper.forHtmlContent(tmo_descricao.toUpperCase())%></td>
          <td><%=TextHelper.forHtmlContent(ten_descricao.toUpperCase())%></td>
          <td <%=((TextHelper.forJavaScript(tmo_ativo.toString()).equals(CodedValues.STS_ATIVO.toString())) ? "" : "class=\"block\"")%> ><%=TextHelper.forHtmlAttribute(msgMotivoBloqueadoDesbloqueado)%></td>
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
                  <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade('<%=TextHelper.forJavaScript(tmo_ativo.toString())%>', '<%=TextHelper.forJavaScript(tmo_codigo)%>', 'TMO', '../v3/motivoOperacao?acao=alterarStatus&codigo=<%=TextHelper.forJavaScript(tmo_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(tmo_descricao)%>')"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/motivoOperacao?acao=editar&tmoCodigo=<%=TextHelper.forJavaScriptAttribute(tmo_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.acoes.editar"/></a>
                  <%if (responsavel.temPermissao(CodedValues.FUN_EDT_MOTIVO_OPERACAO)) {%>
                    <a class="dropdown-item" href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(tmo_codigo)%>', 'TMO', '../v3/motivoOperacao?acao=excluir&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(tmo_descricao)%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                  <%}%>
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
            <td colspan="5"><hl:message key="rotulo.lote.listagem.tipo.motivo"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>