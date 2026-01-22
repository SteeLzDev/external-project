<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
String indice = (request.getAttribute("_indice") != null ? request.getAttribute("_indice").toString() : "");

int qtdPorPagina = (request.getAttribute("_paginacaoQtdPorPagina" + indice) != null ? (Integer) request.getAttribute("_paginacaoQtdPorPagina" + indice) : 20);
int qtdPagina = (request.getAttribute("_paginacaoQtdPagina" + indice) != null ? (Integer) request.getAttribute("_paginacaoQtdPagina" + indice) : 1);
int ultimo = (request.getAttribute("_paginacaoUltimo" + indice) != null ? (Integer) request.getAttribute("_paginacaoUltimo" + indice) : 20);
int paginaAtual = (request.getAttribute("_paginacaoPaginaAtual" + indice) != null ? (Integer) request.getAttribute("_paginacaoPaginaAtual" + indice) : 1);
int offsetPaginaAnterior = (request.getAttribute("_paginacaoPaginaAnterior" + indice) != null ? (Integer) request.getAttribute("_paginacaoPaginaAnterior" + indice) : 0);
int qtdAtalhos = (request.getAttribute("_paginacaoQtdAtalhos" + indice) != null ? (Integer) request.getAttribute("_paginacaoQtdAtalhos" + indice) : 10);
String linkPaginacao = request.getAttribute("_linkPaginacao" + indice).toString();
String tituloPaginacao = request.getAttribute("_paginacaoTitulo" + indice).toString();
String offsetParam = request.getAttribute("_paginacaoOffsetParam" + indice).toString();
%>
<nav aria-label="<%=TextHelper.forHtmlAttribute(tituloPaginacao)%>">
  <ul class="pagination justify-content-end">
    <li class="page-item <%= (paginaAtual > 1) ? "" : "disabled" %>">
      <a class="page-link" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkPaginacao + "&" + offsetParam + "=" + offsetPaginaAnterior, request))%>')" aria-label='<hl:message key="rotulo.paginacao.anterior"/>'>«</a>
    </li>
    <% if (paginaAtual - qtdAtalhos > 1) { %>
      <% String linkPagina = SynchronizerToken.updateTokenInURL(linkPaginacao + "&" + offsetParam + "=" + 0, request); %>
       <li class="page-item"><a class="page-link" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkPagina)%>')">1</a></li>
       <li class="page-item disabled"><a class="page-link" href="#no-back">...</a></li>
    <% } %>
    <% for (int contador = Math.max(1, paginaAtual - qtdAtalhos); contador <= Math.min(qtdPagina, paginaAtual + qtdAtalhos); contador++) { %>
        <% String linkPagina = SynchronizerToken.updateTokenInURL(linkPaginacao + "&" + offsetParam + "=" + ((contador - 1) * qtdPorPagina), request); %>
        <% if (contador == paginaAtual) { %>
            <li class="page-item active"><a class="page-link" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkPagina)%>')"><%=contador%><span class="sr-only"> (<hl:message key="rotulo.paginacao.atual"/>)</span></a></li>
        <% } else { %>
            <li class="page-item"><a class="page-link" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkPagina)%>')"><%=contador%></a></li>
        <% } %>
    <% } %>
    <% if (qtdPagina > paginaAtual + qtdAtalhos) { %>
      <% String linkPagina = SynchronizerToken.updateTokenInURL(linkPaginacao + "&" + offsetParam + "=" + ((qtdPagina - 1) * qtdPorPagina), request); %>
       <li class="page-item disabled"><a class="page-link" href="#no-back">...</a></li>
       <li class="page-item"><a class="page-link" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkPagina)%>')"><%=qtdPagina%></a></li>
    <% } %>
    <li class="page-item <%=(paginaAtual < qtdPagina) ? "" : "disabled"%>">
      <a class="page-link" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkPaginacao + "&" + offsetParam + "=" + (paginaAtual * qtdPorPagina), request))%>')" aria-label='<hl:message key="rotulo.paginacao.proxima"/>'>»</a>
    </li>
  </ul>
</nav>
