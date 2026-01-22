<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("listaServico");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String corCodigo = (String) request.getAttribute("corCodigo");
String rseCodigo = responsavel.getRseCodigo();
String rseMatricula = responsavel.getRseMatricula();
String orgCodigo = responsavel.getOrgCodigo();
%>

<c:set var="title">
<hl:message key="rotulo.solicitar.reserva.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><%= ApplicationResourcesHelper.getMessage("mensagem.solicitar.reserva.margem.escolha.servico", responsavel, ApplicationResourcesHelper.getMessage("rotulo.botao.selecionar", responsavel))%>
        </p>
      </div>
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.resultado.pesquisa"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th> <hl:message key="rotulo.servico.codigo"/></th>
                <th> <hl:message key="rotulo.servico.singular"/></th>
                <th> <hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
            <% if (servicos == null || servicos.size() == 0) { %>
                <tr>
                  <td colspan="3"><hl:message key="mensagem.erro.solicitar.reserva.margem.nenhum.servico"/></td>
                </tr>
                <% } else { %>
                <%
                for (TransferObject servico : servicos) {
                  String svcCodigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
                  String svcIdentificador = servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                  String svcNome = servico.getAttribute(Columns.SVC_DESCRICAO).toString();
                %>
              <tr>
                <td onClick="reservar('<%=TextHelper.forJavaScript(svcCodigo)%>'); return false;"> <%=TextHelper.forHtmlContent(svcIdentificador)%></td>
                <td onClick="reservar('<%=TextHelper.forJavaScript(svcCodigo)%>'); return false;"> <%=TextHelper.forHtmlContent(svcNome.toUpperCase())%></td>
                <td onClick="reservar('<%=TextHelper.forJavaScript(svcCodigo)%>'); return false;"> <a href="#no-back" alt="<hl:message key="rotulo.botao.selecionar"/>" title="<hl:message key="rotulo.botao.selecionar"/>"><hl:message key="rotulo.botao.selecionar"/></a></td>
              </tr>
               <% } %> 
               <% } %> 
              </tbody>
            <tfoot>
              <tr>
                <td colspan="3"><hl:message key="rotulo.lote.listagem.servicos"/>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
</c:set>
<c:set var="javascript">
<script>
function reservar(svcCodigo) {
  var URL = '../v3/reservarMargem?acao=<%= TextHelper.forJavaScriptBlock(request.getAttribute("proximaAcao")) %>'
                <% if(!TextHelper.isNull(corCodigo)){%>
                    + '&COR_CODIGO=<%=corCodigo%>&PORTAL_BENEFICIO=true'
                <% }else { %>
                    + '&CSA_CODIGO=<%=csaCodigo%>'
                <%} %>                
                + '&SVC_CODIGO=' + svcCodigo
                + '&RSE_CODIGO=<%=TextHelper.forJavaScriptBlock(rseCodigo)%>'
                + '&RSE_MATRICULA=<%=TextHelper.forJavaScriptBlock(rseMatricula)%>'
                + '&<%= SynchronizerToken.generateToken4URL(request) %>';
  postData(URL);
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>