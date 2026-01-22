<%@ page contentType="text/html" pageEncoding="UTF-8" %>
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

//Pega dados vindo do webController
String csa_codigo = (String) request.getAttribute("csaCodigo");
String cor_codigo = (String) request.getAttribute("corCodigo");
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("servicos");
boolean podeEditarCnvCor = (boolean) request.getAttribute("podeEditarCnvCor");
boolean podeConsultarCnvCor = (boolean) request.getAttribute("podeConsultarCnvCor");
boolean podeEditarCor = (boolean) request.getAttribute("podeEditarCor");

String titulo = ApplicationResourcesHelper.getMessage("rotulo.lista.servico.titulo", responsavel);
String subTitulo = (String) request.getAttribute("corNome");
if (!subTitulo.equals("")) {
  titulo += " - "  + subTitulo;
}
%>
<c:set var="title">
   <%=ApplicationResourcesHelper.getMessage("rotulo.lista.servico.titulo", responsavel) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
    <form method="post" name="form1" action="../v3/manterConvenioCorrespondente?_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>">
    	<input type="hidden" name="acao" value="iniciar">
      <input type="hidden" name="cor" value="<%=TextHelper.forHtmlAttribute(cor_codigo)%>">
      <input type="hidden" name="csa" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
      <input type="hidden" name="blockAll" value="false">
    </form>
    <div class="row">
      <div class="col-sm-11 col-md-12">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><%=TextHelper.forHtml(titulo)%></h2>
          </div>
          <div class="card-body table-responsive">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.codigo.servico"/></th>
                  <th scope="col"><hl:message key="rotulo.descricao.servico"/></th>
                  <th scope="col"><hl:message key="rotulo.lista.servico.status"/></th>
                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
              </thead>
              <tbody>
                <%=JspHelper.msgRstVazio(servicos.size()==0, 13, responsavel)%>
                <%
                Iterator<TransferObject> it = servicos.iterator();
                while (it.hasNext()) {
                  CustomTransferObject servico = (CustomTransferObject)it.next();
                  String svc_codigo = (String)servico.getAttribute(Columns.SVC_CODIGO);
                  String svc_descricao = (String)servico.getAttribute(Columns.SVC_DESCRICAO);
                  String svc_identificador = (String)servico.getAttribute(Columns.SVC_IDENTIFICADOR);
                  String scv_codigo = servico.getAttribute("STATUS").toString();
                  String status = ((scv_codigo.equals(CodedValues.SCV_ATIVO)) ? ApplicationResourcesHelper.getMessage("rotulo.lista.servico.desbloqueado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.lista.servico.bloqueado", responsavel));
                %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(svc_identificador.toUpperCase())%></td>
                  <td><%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%></td>
                  <td><%=TextHelper.forHtmlContent(status)%></td>
                  <td>
                    <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                            </span> 
                            <hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <% if (podeEditarCnvCor) { %>        
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConvenioCorrespondente?acao=editar&cor=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&svc_codigo=<%=TextHelper.forJavaScript(svc_codigo)%>&csa=<%=TextHelper.forJavaScript(csa_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.bloquear.desbloquear"/></a>
                        <% } else if (podeConsultarCnvCor) { %>        
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConvenioCorrespondente?acao=consultar&cor=<%=TextHelper.forJavaScriptAttribute(cor_codigo)%>&svc_codigo=<%=TextHelper.forJavaScript(svc_codigo)%>&csa=<%=TextHelper.forJavaScript(csa_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.consultar"/></a>
                        <% } %>
                        <% if (podeEditarCor) { %>
                          <% if (CodedValues.SCV_ATIVO.equals(scv_codigo)) { %>
                            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterCorrespondente?acao=editarServico&svc=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&SVC_IDENTIFICADOR=<%=TextHelper.forJavaScript(svc_identificador)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(svc_descricao)%>&csa_codigo=<%=TextHelper.forJavaScript(csa_codigo)%>&cor_codigo=<%=TextHelper.forJavaScript(cor_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.convenio.parametros"/></a>
                          <% } else { %>
                            <a class="dropdown-item" onclick="alerta_param()"><hl:message key="rotulo.convenio.parametros"/></a>
                          <% } %>
                        <% } %>
                        </div>
                      </div>
                    </div>
                </tr>
              <% } %>
              </tbody>
              <tfoot>
                <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.servicos", responsavel)%></td></tr>
              </tfoot>
            </table>
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      <% if (podeEditarCnvCor) { %>
      <a class="btn btn-primary" href="#no-back" onClick="blockAll('false'); return false;"><hl:message key="rotulo.lista.servico.desbloquear.tudo"/></a>
      <a class="btn btn-primary" href="#no-back" onClick="blockAll('true'); return false;"><hl:message key="rotulo.lista.servico.bloquear.tudo"/></a>
      <% } %>
    </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
    function blockAll(block) {
        f0 = document.forms[0];
    	f0.blockAll.value = block;
    	f0.acao.value = 'alterarTodos';
    	f0.submit();
    }
    
    function alerta_param() {
        alert('<hl:message key="mensagem.convenio.desbloquear.para.editar.parametros"/>');
    }
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
