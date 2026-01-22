<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String titulo = (String) request.getAttribute("titulo");
String subTitulo = (String) request.getAttribute("subTitulo");
String csa_codigo = (String) request.getAttribute("csa_codigo");
String cancel = (String) request.getAttribute("cancel");
String org_codigo = (String) request.getAttribute("org_codigo");
List<?> servicos = (List<?>) request.getAttribute("servicos");

boolean podeEditarCnvCor = (boolean) request.getAttribute("podeEditarCnvCor");
boolean podeConsultarCnvCor = (boolean) request.getAttribute("podeConsultarCnvCor");
%>
<c:set var="title">
   <%=TextHelper.forHtmlContent(titulo)%>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-11 col-md-12">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><%=TextHelper.forHtml(subTitulo)%></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.codigo.servico"/></th>
                <th scope="col"><hl:message key="rotulo.descricao.servico"/></th>
                <th scope="col"><hl:message key="rotulo.lista.servico.status"/></th>
            <% if (podeEditarCnvCor || podeConsultarCnvCor) { %>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
            <% } %>                    
              </tr>
            </thead>
            <tbody>
            <%=JspHelper.msgRstVazio(servicos.size()==0, "13", "lp")%>
            <%
              Iterator<?> it = servicos.iterator();
              while (it.hasNext()) {
                CustomTransferObject servico = (CustomTransferObject)it.next();
                String svc_codigo = (String)servico.getAttribute(Columns.SVC_CODIGO);
                String svc_descricao = (String)servico.getAttribute(Columns.SVC_DESCRICAO);
                String svc_identificador = (String)servico.getAttribute(Columns.SVC_IDENTIFICADOR);
                String scv_codigo = servico.getAttribute("STATUS").toString();
                String status = ((scv_codigo.equals(CodedValues.SCV_ATIVO)) ? ApplicationResourcesHelper.getMessage("rotulo.lista.servico.desbloqueado", responsavel) : (scv_codigo.equals(CodedValues.SCV_INATIVO) ? ApplicationResourcesHelper.getMessage("rotulo.lista.servico.bloqueado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.lista.servico.p.desbloqueado", responsavel)));
                String statusClass = (scv_codigo.equals(CodedValues.SCV_ATIVO) ? "" : "class=\"block\""); 
              %>
              <tr>
                <td><%=TextHelper.forHtmlContent(svc_identificador.toUpperCase())%></td>
                <td><%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%></td>
                <td <%=statusClass%>><%=TextHelper.forHtmlAttribute(status)%></td>
                <td>
                  <% if (podeEditarCnvCor) { %>
                  <a href="#no-back" onClick="postData('../v3/listarServicoConvenios?acao=consultarConvenio&org_codigo=<%=TextHelper.forJavaScript(org_codigo)%>&svc_codigo=<%=TextHelper.forJavaScript(svc_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                  <hl:message key="rotulo.acao.bloquear.desbloquear"/></a>
                  <% } else if (podeConsultarCnvCor) { %>
                  <a href="#no-back" onClick="postData('../v3/listarServicoConvenios?acao=consultarConvenio&org_codigo=<%=TextHelper.forJavaScript(org_codigo)%>&svc_codigo=<%=TextHelper.forJavaScript(svc_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                  <hl:message key="rotulo.acoes.visualizar"/></a>
                  <% } %>
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
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
