<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<?> lstPostos = (List<?>) request.getAttribute("lstPostos");

String csaCodigo = (String) request.getAttribute("csaCodigo");
String csaNome = (String) request.getAttribute("csaNome");
String svcCodigo = (String) request.getAttribute("svcCodigo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
%>
<c:set var="title">
  <hl:message key="rotulo.bloquear.posto.csa.svc.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/bloquearPostoCsaSvc?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.bloquear.posto.csa.svc.titulo.arg0.arg1" arg0="<%=TextHelper.forHtmlContent(svcDescricao)%>" arg1="<%=TextHelper.forHtmlContent(csaNome)%>"/></h2>
    </div>
    <div class="card-body table-responsive ">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th><hl:message key="rotulo.bloquear.posto.csa.svc.titulo.tabela.posto.identificador"/></th>
            <th><hl:message key="rotulo.bloquear.posto.csa.svc.titulo.tabela.posto.descricao"/></th>
            <th class="text-center"><hl:message key="rotulo.bloquear.posto.csa.svc.titulo.tabela.solicitacao"/></th>
            <th class="text-center"><hl:message key="rotulo.bloquear.posto.csa.svc.titulo.tabela.reserva"/></th>
          </tr>         
        </thead>
        <tbody>
  <%
    Iterator<?> it = lstPostos.iterator();
    String posCodigo, posDescricao, posIdentificador;
    boolean bloqSolicitacao = false;
    boolean bloqReserva = false;
    
    TransferObject posto = null;
    while (it.hasNext()) {
      posto = (TransferObject)it.next();
      posCodigo = (String)posto.getAttribute(Columns.POS_CODIGO);
      posDescricao = (String)posto.getAttribute(Columns.POS_DESCRICAO);
      posIdentificador = (String)posto.getAttribute(Columns.POS_IDENTIFICADOR);
      bloqSolicitacao = (posto.getAttribute(Columns.BPC_BLOQ_SOLICITACAO) != null && posto.getAttribute(Columns.BPC_BLOQ_SOLICITACAO).equals("S"));
      bloqReserva = (posto.getAttribute(Columns.BPC_BLOQ_RESERVA) != null && posto.getAttribute(Columns.BPC_BLOQ_RESERVA).equals("S"));
  %> 
        <tr>
          <td><%=TextHelper.forHtmlContent(posIdentificador)%></td>
          <td><%=TextHelper.forHtmlContent(posDescricao.toUpperCase())%></td>
          <td class="text-center"><input type="checkbox" name="solicitacao_<%=TextHelper.forHtmlAttribute(posCodigo)%>" id="solicitacao_<%=TextHelper.forHtmlAttribute(posCodigo)%>" value="S" <%= bloqSolicitacao ? " checked " : "" %> /></td>
          <td class="text-center"><input type="checkbox" name="reserva_<%=TextHelper.forHtmlAttribute(posCodigo)%>" id="reserva_<%=TextHelper.forHtmlAttribute(posCodigo)%>" value="S" <%= bloqReserva ? " checked " : "" %> /></td>
        </tr>
  <% } %>        
        </tbody>
        <tfoot>
          <tr>
            <td colspan="4"><hl:message key="rotulo.bloquear.posto.csa.svc.titulo.listagem"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnCancela" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" id="btnEnvia" href="#no-back" onClick="validaForm(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    <input name="csa" type="hidden" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
    <input name="svc" type="hidden" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>">
  </div>
  </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  var f0 = document.forms[0];

  function validaForm() {
    f0.submit();
  }
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>