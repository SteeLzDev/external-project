<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %><%@ page import="java.util.*"%>
<%@ page import="java.lang.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%
boolean possuiDesblAutCsaPrazoPenalidade = (Boolean) request.getAttribute("possuiDesblAutCsaPrazoPenalidade");
boolean temPermissaoEditar = (Boolean) request.getAttribute("temPermissaoEditar");
List<TransferObject> penalidades = (List<TransferObject>) request.getAttribute("penalidades");
%>
<c:set var="title">
  <hl:message  key="rotulo.tipo.penalidade.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <% if (temPermissaoEditar) { %>
    <div class="page-title">
      <div class="row">
        <div class="col-sm mb-2">
          <div class="float-end">
            <div class="btn-action">
              <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/editarTipoPenalidade?acao=editar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.penalidade.novo"/></a>
            </div>
          </div>
        </div>
      </div>
    </div>  
  <% } %>
      <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.tipo.penalidade.disponiveis"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.penalidade.descricao"/></th>
                <% if (possuiDesblAutCsaPrazoPenalidade) { %>
                  <th scope="col"><hl:message key="rotulo.penalidade.prazo"/></th>
                <% } %>
                <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
             <%
               Iterator it = penalidades.iterator();
               String tpeCodigo = null;
               String tpeDescricao = null;
               String tpePrazoPenalidade = null;
               
               while (it.hasNext()) {
                  CustomTransferObject penalidade = (CustomTransferObject)it.next();
                  tpeCodigo = penalidade.getAttribute(Columns.TPE_CODIGO).toString();
                  tpeDescricao = penalidade.getAttribute(Columns.TPE_DESCRICAO).toString();
                  tpePrazoPenalidade = !TextHelper.isNull(penalidade.getAttribute(Columns.TPE_PRAZO_PENALIDADE)) ? penalidade.getAttribute(Columns.TPE_PRAZO_PENALIDADE).toString() : "";
             %>
               <tr>
                  <td><%=TextHelper.forHtml(tpeDescricao.toUpperCase())%></td>
                  <% if (possuiDesblAutCsaPrazoPenalidade) { %>
                  <td><%=TextHelper.forHtml(tpePrazoPenalidade)%></td>
                  <% } %>
                    
                  <td>
                    <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" aria-label='<hl:message key="rotulo.botao.opcoes"/>' title='<hl:message key="rotulo.botao.opcoes"/>'>
                              <svg><use xlink:href="../img/sprite.svg#i-engrenagem"></use></svg>
                            </span>
                            <hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarTipoPenalidade?acao=editar&TPE_CODIGO=<%=TextHelper.forJavaScriptAttribute(tpeCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.penalidade.editar"/></a>
                          <a class="dropdown-item" href="#no-back" onClick="excluirTipoPenalidade('<%=TextHelper.forJavaScriptAttribute(tpeCodigo)%>', '<%=TextHelper.forJavaScriptAttribute(tpeDescricao.toUpperCase())%>');"><hl:message key="rotulo.penalidade.excluir"/></a>
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
                <td>
                  <hl:message key="rotulo.penalidade.listagem"/>
                </td>
              </tr>
            </tfoot>        
          </table>
        </div>
      </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  function excluirTipoPenalidade(tpeCodigo, tpeDescricao) {
    var url = "../v3/editarTipoPenalidade?acao=excluir&tpeCodigo=" + tpeCodigo + "&tpeDescricao=" + tpeDescricao + "&<%=SynchronizerToken.generateToken4URL(request)%>";
    return ConfirmaUrl('<hl:message key="mensagem.penalidade.confirma.exclusao"/>'.replace('{0}',tpeDescricao), url);
  }
  </script>
</c:set>  
  <t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
