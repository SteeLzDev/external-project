<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> motivoReclamacao = (List<TransferObject>) request.getAttribute("motivoReclamacao");
%>

<c:set var="title">
  <hl:message key="<%=TextHelper.forHtml("rotulo.tipo.motivo.reclamacao.titulo")%>"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <div class="btn-action">
    <% if (responsavel.temPermissao(CodedValues.FUN_EDT_MOTIVO_RECLAMACAO)) { %>
      <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/motivoReclamacao?acao=editar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')">Novo motivo de reclamação</a>
    <% } %>
  </div>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title">Motivos de reclamações</h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col" class="col-sm-8"><hl:message key="rotulo.tipo.motivo.reclamacao.descricao"/></th>
            <th scope="col" class="col-sm-4 text-center"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <%
            Iterator<TransferObject> it = motivoReclamacao.iterator();
            while (it.hasNext()) {
                TransferObject tipoMotivoReclamacaoCTO = it.next();
                String tmr_codigo = (String)tipoMotivoReclamacaoCTO.getAttribute(Columns.TMR_CODIGO);
                String tmr_descricao = (String)tipoMotivoReclamacaoCTO.getAttribute(Columns.TMR_DESCRICAO);
          %>
            <tr>
              <td><%=TextHelper.forHtmlContent(tmr_descricao.toUpperCase())%></td>
              <td class="text-center">
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="Mais ações" aria-label="Mais ações">
                          <svg>
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use>
                          </svg>
                        </span>Opções
                      </div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/motivoReclamacao?acao=editar&tmrCodigo=<%=TextHelper.forJavaScriptAttribute(tmr_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                        <hl:message key="rotulo.acoes.editar"/>
                      </a>
                      <% if (responsavel.temPermissao(CodedValues.FUN_EDT_MOTIVO_RECLAMACAO)) { %>
                        <a class="dropdown-item" href="#no-back" onClick="excluiTipoMotivoReclamacao('<%=TextHelper.forJavaScript(tmr_codigo)%>', '<%=TextHelper.forJavaScript(tmr_descricao)%>')">
                          <hl:message key="rotulo.acoes.excluir"/>
                        </a>
                      <% } %>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          <% } %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="8">
              <hl:message key="rotulo.reclamacao.listagem"/> - 
              <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript">
    function excluiTipoMotivoReclamacao(codigo, desc) {
      var url = '../v3/motivoReclamacao?acao=excluir&tmrCodigo=' + codigo + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>';
      return ConfirmaUrl('<hl:message key="mensagem.confirmacao.exclusao.tipo.motivo.reclamacao"/>'.replace("{0}", desc), url);
    }
  </script>
</c:set>

<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>