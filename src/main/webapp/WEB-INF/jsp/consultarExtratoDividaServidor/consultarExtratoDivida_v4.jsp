<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String serNome = (String) request.getAttribute("serNome");
String rseMatricula = (String) request.getAttribute("rseMatricula");
Map<Short, MargemTO> margens = (Map<Short, MargemTO>) request.getAttribute("lstMargens");
Map<Short, List<ExtratoDividaServidor>> extratoDividaPorMargem = (Map<Short, List<ExtratoDividaServidor>>) request.getAttribute("extratoDivida");
Map<Short, BigDecimal> vlrTotalPorMargem = (Map<Short, BigDecimal>) request.getAttribute("vlrTotalPorMargem");
%>
<c:set var="title">
   <hl:message key="rotulo.extrato.divida.servidor.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.extrato.divida.servidor.subtitulo"/></h2>
      </div>
      <div class="card-body">
        <dl class="row data-list firefox-print-fix">
          <dt class="col-6"><hl:message key="rotulo.servidor.extrato.divida.posicao"/>:<hl:message key="rotulo.servidor.extrato.divida.data.hora"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(DateHelper.toDateTimeString(DateHelper.getSystemDatetime()))%></dd>
          <dt class="col-6"><hl:message key="rotulo.servidor.extrato.divida.matricula"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(rseMatricula)%> - <%=TextHelper.forHtmlContent(serNome)%></dd>
        </dl>
      </div>
    </div>
    
    <ul class="nav nav-tabs responsive-tabs" id="consignacaoInfo" role="tablist">
    <%
    for (short i = 1; i <= 4; i++) {
      List<ExtratoDividaServidor> lstExtratoDivida = extratoDividaPorMargem.get(i);
      BigDecimal vlrTotal = vlrTotalPorMargem.get(i);
      
      // Se tem contratos, ou se é margem 1 exibe a tabela de resultado
      if (!lstExtratoDivida.isEmpty() || i == 1) {

        MargemTO margemTO = margens.get(i);
        if (margemTO != null) {
            String descricao = (margemTO.getMarDescricao() != null ? margemTO.getMarDescricao() : "Margem");
    %>
      <li class="nav-item">
        <a <%=(i == 1 ? "class=\"nav-link active\"" : "class=\"nav-link\"") %> data-bs-toggle="tab" href="#tab_<%= margemTO.getMarCodigo().toString() %>" role="tab" aria-controls="profile" aria-selected="true"><%=TextHelper.forHtmlContent(descricao)%></a>
      </li>
    <% 
        }  else { %>
     <li class="nav-item">
        <a <%=(i == 1 ? "class=\"nav-link active\"" : "class=\"nav-link\"") %> data-bs-toggle="tab" href="#margem" role="tab" aria-controls="profile" aria-selected="true"><hl:message key="rotulo.margem.singular"/></a>
      </li>    
    <%        
        }
      }
    }
    %>
    </ul>
    <%-- Tab panes --%>
    <div class="tab-content" id="consignacaoInfo">
    <%
    for (short i = 1; i <= 4; i++) {
        List<ExtratoDividaServidor> lstExtratoDivida = extratoDividaPorMargem.get(i);
        BigDecimal vlrTotal = vlrTotalPorMargem.get(i);
        // Se tem contratos, ou se é margem 1 exibe a tabela de resultado
        if (!lstExtratoDivida.isEmpty() || i == 1) {
          MargemTO margemTO = margens.get(i);
          if (margemTO != null) {
    %>  
      <div <%=(i == 1 ? "class=\"tab-pane fade show print active\"" : "class=\"tab-pane fade show print\"") %> id="tab_<%= margemTO.getMarCodigo().toString() %>" role="tabpanel" aria-labelledby="<%= margemTO.getMarCodigo().toString() %>">
    <% } else { %>
      <div <%=(i == 1 ? "class=\"tab-pane fade show print active\"" : "class=\"tab-pane fade show print\"") %> id="margem" role="tabpanel" aria-labelledby="margem">    
    <% } %>
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th id="data" scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase()%></th>
              <th id="responsavel" scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel).toUpperCase()%></th>
              <th id="tipo" scope="col"><hl:message key="rotulo.servidor.extrato.divida.nre.ade"/></th>
              <th id="descricao" scope="col"><hl:message key="rotulo.servidor.extrato.divida.inclusao"/></th>
              <th id="valorDaPrestacao" class="text-nowrap" scope="col"><hl:message key="rotulo.servidor.extrato.divida.vlr_prestacao"/></th>
              <th id="numeroPrestacao" class="text-nowrap" scope="col"><hl:message key="rotulo.servidor.extrato.divida.nro.prestacao"/></th>
              <th id="prestacoesPagas" class="text-nowrap" scope="col"><hl:message key="rotulo.servidor.extrato.divida.pagas"/></th>
              <th id="situacao" class="text-nowrap" scope="col"><hl:message key="rotulo.servidor.extrato.divida.situacao"/></th>
            </tr>
          </thead>
          <tbody>
              <% if (lstExtratoDivida.isEmpty()) { %>
              <tr>
                <td colspan="8"><hl:message key="mensagem.erro.servidor.extrato.nao.encontrado"/></td> 
              </tr>
              <% } else { %>
                <% for (ExtratoDividaServidor extratoDivida : lstExtratoDivida) { %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(extratoDivida.getConsignataria())%></td>
                  <td><%=TextHelper.forHtmlContent(extratoDivida.getServico())%></td>
                  <td><%=TextHelper.forHtmlContent(extratoDivida.getAdeNumero())%></td>
                  <td><%=TextHelper.forHtmlContent(extratoDivida.getAdeData())%></td>
                  <td><%=TextHelper.forHtmlContent(extratoDivida.getAdeVlr())%></td>
                  <td><%=TextHelper.forHtmlContent(extratoDivida.getAdePrazo())%></td>
                  <td><%=TextHelper.forHtmlContent(extratoDivida.getAdePrdPagas())%></td>
                  <td><%=TextHelper.forHtmlContent(extratoDivida.getSadDescricao())%></td>
                </tr>
                <% } %>
              <% } %>
            <tr>
              <td colspan="8"><hl:message key="rotulo.servidor.extrato.divida.total"/>: <span class="font-weight-bold"><%=NumberHelper.format(vlrTotal.doubleValue(), NumberHelper.getLang())%></span></td>
            </tr> 
          </tbody>
        </table>
        <% if (margemTO != null) { 
            String descricao = (margemTO.getMarDescricao() != null ? margemTO.getMarDescricao() : "Margem");
            String margemInicial = (margemTO.getMrsMargem() != null ? NumberHelper.format(margemTO.getMrsMargem().doubleValue(), NumberHelper.getLang()) : "-");
            String margemFinal = (margemTO.getMrsMargemRest() != null ? NumberHelper.format(margemTO.getMrsMargemRest().doubleValue(), NumberHelper.getLang()) : "-");
            String obsMargem = (!TextHelper.isNull(margemTO.getObservacao()) ? " (" + margemTO.getObservacao() + ")" : "");
        %>
        <dl class="row data-list firefox-print-fix">
          <dt class="col-6"><%=TextHelper.forHtmlContent(descricao)%> <hl:message key="rotulo.servidor.extrato.divida.margem.inicial"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemTO.getMarTipoVlr() != null ? margemTO.getMarTipoVlr().toString() : null))%> <%=TextHelper.forHtmlContent(margemInicial)%></dd>
          <dt class="col-6"><%=TextHelper.forHtmlContent(descricao)%> <hl:message key="rotulo.servidor.extrato.divida.margem.final"/></dt>
          <dd class="col-6"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemTO.getMarTipoVlr() != null ? margemTO.getMarTipoVlr().toString() : null))%> <%=TextHelper.forHtmlContent(margemFinal + obsMargem)%></dd>
        </dl>
        <% } %>
      </div>
    <% 
        }
      }
    %>
    </div>
    <div class="btn-action" aria-label="<hl:message key="rotulo.botoes.acao.pagina"/>">
      <a class="btn btn-outline-danger mt-2" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(responsavel.isSer() ? "../v3/carregarPrincipal" : SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" ><hl:message key="rotulo.botao.voltar"/></a>
      <a class="btn btn-primary mt-2" href="#no-back" onClick="imprime();" ><hl:message key="rotulo.botao.imprimir"/></a>
    </div>
    
</c:set>
<c:set var="javascript">
<script src="../node_modules/responsive-bootstrap-tabs/jquery.responsivetabs.js"></script>
<script type="text/JavaScript">
function imprime() {
	$("#CABECA").css({"visibility":"hidden"});
    $("#BOTOES").css({"visibility":"hidden"});
    $("#container").css({"visibility":"hidden"});
    document.body.className = "PRINT";
    window.print();
    document.body.className = "";
    $("#CABECA").css({"visibility":"visible"});
    $("#BOTOES").css({"visibility":"visible"});
    $("#container").css({"visibility":"visible"});
}

$(function() {
    $('.nav-tabs').responsiveTabs();
});
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
