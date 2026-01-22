<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@page import="com.zetra.econsig.values.Columns"%>
<%@page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>

<c:set var="title">
  <hl:message key="rotulo.ajuda"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    String filtro = (String) request.getAttribute("filtro");
  %>
  <jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
  <div class="card">
   <div class="card-header hasIcon pl-3">
     <h2 class="card-header-title"><hl:message key="rotulo.ajuda"/></h2>
   </div>
   <div class="card-body p-0">
    <div class="p-4">
     <% if ((boolean) request.getAttribute("mostraComboPreview")) { %>
      <div class="dropdown float-end d-print-none">
       <button type="button" class="btn btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        <hl:message key="rotulo.acoes.visualizar"/>
       </button>
       <ul class="dropdown-menu dropdown-menu-right">
		<% if (responsavel.isSup()) { %>
		<li><a class="dropdown-item <%=!TextHelper.isNull(filtro) && filtro.equals("7") ? "active" : "" %>" onclick="trocarVisualizacao(7)" href="#no-back"><hl:message key="rotulo.suporte.singular"/></a></li>
		<% }%>
        <li><a class="dropdown-item <%=!TextHelper.isNull(filtro) && filtro.equals("1") ? "active" : "" %>" onclick="trocarVisualizacao(1)" href="#no-back"><hl:message key="rotulo.consignante.singular"/></a></li>
        <li><a class="dropdown-item <%=!TextHelper.isNull(filtro) && filtro.equals("2") ? "active" : "" %>" onclick="trocarVisualizacao(2)" href="#no-back"><hl:message key="rotulo.consignataria.singular"/></a></li>
        <li><a class="dropdown-item <%=!TextHelper.isNull(filtro) && filtro.equals("3") ? "active" : "" %>" onclick="trocarVisualizacao(3)" href="#no-back"><hl:message key="rotulo.orgao.singular"/></a></li>
        <li><a class="dropdown-item <%=!TextHelper.isNull(filtro) && filtro.equals("4") ? "active" : "" %>" onclick="trocarVisualizacao(4)" href="#no-back"><hl:message key="rotulo.correspondente.singular"/></a></li>
        <li><a class="dropdown-item <%=!TextHelper.isNull(filtro) && filtro.equals("6") ? "active" : "" %>" onclick="trocarVisualizacao(6)" href="#no-back"><hl:message key="rotulo.servidor.singular"/></a></li>
       </ul>
      </div>
     <% } %>
     <p class="pt-3"><hl:message key="rotulo.servidor.email"/>: <a href="mailto:<%=TextHelper.forHtmlAttribute(request.getAttribute("emailSuporte"))%>"><%=TextHelper.forHtmlAttribute(request.getAttribute("emailSuporte"))%></a></p>
    </div>
    <ul class="list-links">
    <% String nomeComponente = "";
       List ajudas = (List) request.getAttribute("ajudas");
       for (int i=0; i < ajudas.size(); i++) {
          CustomTransferObject ajuda = (CustomTransferObject)ajudas.get(i);
          String acrCodigo = ajuda.getAttribute(Columns.ACR_CODIGO).toString();
          String ajuTitulo = ajuda.getAttribute(Columns.AJU_TITULO).toString();
          String ajuTexto = ajuda.getAttribute(Columns.AJU_TITULO).toString();
          nomeComponente = "FAQ_"+i;
          if (ajuTexto != null && !ajuTexto.equals("")) {%>
            <li>
               <a onclick="postData('../v3/visualizarAjudaContexto?acao=visualizar&acrCodigo=<%=TextHelper.forJavaScriptAttribute(acrCodigo)%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>');" href="#no-back"><%=TextHelper.forHtmlContent(ajuTitulo.toUpperCase())%></a>
            </li>
        <%} %>
    <% } %>
    </ul>
   </div>
  </div>
  <div class="btn-action">
   <a class="btn btn-outline-danger" href="#no-back" onclick="postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true');"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    f0 = document.forms[0];

    function trocarVisualizacao(value) {
    	postData("../v3/visualizarAjuda?acao=visualizarAjuda&_skip_history_=true&FILTRO_TIPO=" + value);
    }
  </script>
</c:set>

<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>