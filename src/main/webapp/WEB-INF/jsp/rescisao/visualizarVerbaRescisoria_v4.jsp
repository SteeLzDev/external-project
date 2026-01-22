<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, java.io.*, java.math.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
List<TransferObject> detalheRetencaoVerbaRescisoria = (List<TransferObject>) request.getAttribute("detalheRetencaoVerbaRescisoria");
%>
<c:set var="title">
   <hl:message key="rotulo.visualizar.verba.rescisoria.titulo"/>
</c:set>

<c:set var="imageHeader">
   <use xlink:href="#i-rescisao"></use>
</c:set>

<c:set var="bodyContent">
<div class="col-sm">
<form method="post" action="../v3/listarColaboradoresVerbaRescisoria?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.visualizar.verba.rescisoria.dados.servidor.titulo"/></h2>
    </div> 
    <div class="card-body">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><hl:message key="mensagem.informacao.visualizar.verba.rescisoria"/></p>
      </div>
      <dl class="row data-list">
        <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
        <% pageContext.setAttribute("servidor", servidor); %>
        <hl:detalharServidorv4 name="servidor"/>
        <%-- Fim dos dados do servidor --%>
      </dl>
    </div>
  </div>
  <% if (detalheRetencaoVerbaRescisoria != null) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.editar.verba.rescisoria.resultado.pesquisa"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
            <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
            <th scope="col"><hl:message key="rotulo.servico.singular"/></th>
            <th scope="col"><hl:message key="rotulo.visualizar.verba.rescisoria.valor.retido"/></th>
          </tr>
        </thead>
        <tbody>
           <%=JspHelper.msgRstVazio(detalheRetencaoVerbaRescisoria.size() == 0, 7, responsavel)%>
           <%
           Iterator<TransferObject> it = detalheRetencaoVerbaRescisoria.iterator();
           while (it.hasNext()) {
             CustomTransferObject contrato = (CustomTransferObject) it.next();
             String adeNumero = contrato.getAttribute(Columns.ADE_NUMERO).toString();
             String csaNome = (String) contrato.getAttribute(Columns.CSA_NOME_ABREV);
             String svcDescricao = (String) contrato.getAttribute(Columns.SVC_DESCRICAO);
             // Valor do contrato
             BigDecimal adeVlr = new BigDecimal(0.00);
             if (!TextHelper.isNull(contrato.getAttribute(Columns.ADE_VLR))) {
                 adeVlr = ((BigDecimal) contrato.getAttribute(Columns.ADE_VLR));
             }
             String strAdeVlr = NumberHelper.format(adeVlr.doubleValue(), NumberHelper.getLang());             
           %>
           <tr>
             <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
             <td><%=TextHelper.forHtmlContent(csaNome)%></td>
             <td><%=TextHelper.forHtmlContent(svcDescricao)%></td>
             <td><%=TextHelper.forHtmlContent(strAdeVlr)%></td>
           </tr>
           <% } %>
        </tbody>
      </table>
    </div>
  </div>
  <% } %>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</form>
</div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>