<%--
* <p>Title: edt_proposta.jsp</p>
* <p>Description: Página de edição de propostas de pagamento do saldo devedor</p>
* <p>Copyright: Copyright (c) 2002-2014</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: alexandre $
* $Revision: 31521 $
* $Date: 2021-03-24 19:29:19 -0300 (qua, 24 mar 2021) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String adeCodigo = (String) request.getAttribute("adeCodigo");
int qtdMinPropostas = (int) request.getAttribute("qtdMinPropostas");
int qtdMaxPropostas = (int) request.getAttribute("qtdMaxPropostas");

// Busca os dados do contrato
TransferObject ade = (TransferObject) request.getAttribute("ade");

// Obtém os prazos obrigatórios
List<Integer> prazosObrigatorios = (List<Integer>) request.getAttribute("prazosObrigatorios");

// Busca as propostas de pagamento
Map<Object, TransferObject> propostas = (Map<Object, TransferObject>) request.getAttribute("propostas");
%>
<c:set var="title">
  <hl:message key="rotulo.editar.propostas.pagamento.divida.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
  <form action="../v3/acompanharFinanciamentoDivida?acao=salvar" method="post" name="form1">
    <input type="hidden" name="ade" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" />
    <% SynchronizerToken.saveToken(request); %>
    <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
    <div class="row">
    <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
    <% pageContext.setAttribute("autdes", ade); %>
    <hl:detalharADEv4 name="autdes" table="false" type="edt_proposta"/>
    <%-- Fim dos dados da ADE --%>
    </div>

    <hl:editaPropostaPagamentoDividav4
        qtdMinPropostas="<%=(int)qtdMinPropostas%>" 
        qtdMaxPropostas="<%=(int)qtdMaxPropostas%>" 
        lstPropostas="<%=(Map)propostas%>" 
        prazosObrigatorios="<%=(List)prazosObrigatorios%>"
    />
    <div class="btn-action">
      <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(paramSession.getLastHistory())%>')"><hl:message key="rotulo.botao.voltar"/></a>
      <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="if(validForm()){ f0.submit();} return false;"><hl:message key="rotulo.botao.confirmar"/></a>
    </div>
  </form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
  function formLoad() {
  }
  function validForm() {
    return true;
  }      
</script>
<script type="text/JavaScript">
  window.onload = formLoad;
  f0 = document.forms[0];
</script>
</html>
</c:set>
<t:page_v4>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
