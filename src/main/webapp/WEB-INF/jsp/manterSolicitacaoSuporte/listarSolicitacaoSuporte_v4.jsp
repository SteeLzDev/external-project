<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditarSos = (Boolean) request.getAttribute("podeEditarSos");
List<TransferObject> lstSolicitacao = (List <TransferObject>) request.getAttribute("lstSolicitacao");

%>
<c:set var="title">
  <hl:message key="rotulo.listar.solicitacao.suporte.footer"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
<% if(podeEditarSos) { %>
  <div class="row">
    <div class="col-sm-12 col-md-12">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/inserirSolicitacaoSuporte?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.nova.solicitacao.suporte"/></a>
        </div>
      </div>
    </div>
  </div>
<% } %>
<div class="card">
  <div class="card-header">
    <h2 class="card-header-title"><hl:message key="rotulo.listar.solicitacao.suporte.footer"/></h2>
  </div>
  <div class="card-body table-responsive p-0">
    <table class="table table-striped table-hover">
      <thead>
        <tr>
          <th scope="col"><hl:message key="rotulo.solicitacao.suporte.chave"/></th>
          <th scope="col"><hl:message key="rotulo.solicitacao.suporte.assunto"/></th>
          <th scope="col"><hl:message key="rotulo.solicitacao.suporte.data.criacao"/></th>
          <th scope="col"><hl:message key="rotulo.solicitacao.suporte.prioridade"/></th>
          <th scope="col"><hl:message key="rotulo.acoes"/></th>
        </tr>
      </thead>
      <tbody>
        <%=JspHelper.msgRstVazio(lstSolicitacao.size()==0, "5", "lp")%>
        <%
        Iterator it = lstSolicitacao.iterator();
        while (it.hasNext()) {
          CustomTransferObject sosTO = (CustomTransferObject)it.next();
          String sos_codigo = (String) sosTO.getAttribute(Columns.SOS_CODIGO);
          String sos_chave = (String) sosTO.getAttribute(Columns.SOS_CHAVE);
          String sos_sumario = (String) sosTO.getAttribute(Columns.SOS_SUMARIO);
          Date sos_data_cadastro = (Date) sosTO.getAttribute(Columns.SOS_DATA_CADASTRO);
          String sos_prioridade = sosTO.getAttribute(Columns.SOS_PRIORIDADE) != null ? (String) sosTO.getAttribute(Columns.SOS_PRIORIDADE) : "";
   //       String sos_sla_indicator = (String) sosTO.getAttribute(Columns.SOS_SLA_INDICATOR);
        %>
        <tr>
          <td><%=TextHelper.forHtmlContent(sos_chave.toUpperCase())%></td>
          <td><%=TextHelper.forHtmlContent(sos_sumario)%></td>
          <td><%=DateHelper.toDateTimeString(sos_data_cadastro)%></td>
          <td><%=TextHelper.forHtmlContent(sos_prioridade)%></td>
          <td><a href="#no-back" onClick="postData('../v3/listarSolicitacaoSuporte?acao=consultar&sosCodigo=<%=TextHelper.forJavaScriptAttribute(sos_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.detalhar"/></a></td>                                
        </tr>
        <%
        }
        %>
      </tbody>
      <tfoot>
        <tr>
          <td colspan="4"><hl:message key="rotulo.listar.solicitacao.suporte.footer"/></td>
        </tr>
      </tfoot>
    </table>
  </div>
</div>
<div class="btn-action">
  <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a> 
</div>
</body>
</c:set>
<c:set var="javascript">
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>