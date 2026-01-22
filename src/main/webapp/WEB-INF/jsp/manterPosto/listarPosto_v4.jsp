<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String cse_codigo = (String) request.getAttribute("cse_codigo");
String titulo = (String) request.getAttribute("titulo");
List<?> postos = (List<?>) request.getAttribute("postos");
String linkRet = (String) request.getAttribute("linkRet");
String linkEdicao = (String) request.getAttribute("linkEdicao");
boolean podeEditarPosto = (Boolean) request.getAttribute("podeEditarPosto");
%>
<c:set var="title">
  <hl:message key="rotulo.consultar.posto.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.consultar.posto.titulo"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th><hl:message key="rotulo.posto.codigo"/></th>
            <th><hl:message key="rotulo.posto.singular"/></th>
            <th><hl:message key="rotulo.posto.soldo.abreviado"/></th>
            <th><hl:message key="rotulo.posto.taxa.uso.percentual.abreviado"/></th>
            <th><hl:message key="rotulo.posto.taxa.uso.abreviado"/></th>
            <th><hl:message key="rotulo.posto.taxa.uso.condominio.percentual.abreviado"/></th>
            <th><hl:message key="rotulo.posto.taxa.uso.condominio.abreviado"/></th>
  <% if (podeEditarPosto) { %>            
            <th><hl:message key="rotulo.acoes"/></th>
  <% } %>            
          </tr>         
        </thead>
        <tbody>
  <%
    Iterator<?> it = postos.iterator();
    String pos_codigo, pos_descricao,pos_identificador, pos_valor_soldo, pos_perc_tx_uso, pos_perc_tx_uso_cond;
    
    TransferObject posto = null;
    while (it.hasNext()) {
      posto = (TransferObject)it.next();
      //recuperando colunas - utlizando o columns
      pos_codigo = (String)posto.getAttribute(Columns.POS_CODIGO);
      pos_descricao = (String)posto.getAttribute(Columns.POS_DESCRICAO);
      pos_identificador = (String)posto.getAttribute(Columns.POS_IDENTIFICADOR);
      pos_valor_soldo = (String)posto.getAttribute(Columns.POS_VALOR_SOLDO).toString();
      pos_perc_tx_uso = (String)posto.getAttribute(Columns.POS_PERC_TAXA_USO).toString();
      pos_perc_tx_uso_cond = (String)posto.getAttribute(Columns.POS_PERC_TAXA_USO_COND).toString();
      
      Double tx_uso, tx_uso_cond;
      tx_uso = Double.parseDouble(pos_perc_tx_uso) * Double.parseDouble(pos_valor_soldo) * 0.01;
      tx_uso_cond = Double.parseDouble(pos_perc_tx_uso_cond) * Double.parseDouble(pos_valor_soldo) * 0.01;    
  %> 
        <tr>
          <td><%=TextHelper.forHtmlContent(pos_identificador)%></td>
          <td><%=TextHelper.forHtmlContent(pos_descricao.toUpperCase())%></td>
          <td><hl:message key="rotulo.moeda"/>&nbsp;<%=NumberHelper.reformat(pos_valor_soldo, "en", NumberHelper.getLang())%></td>
          <td><%=NumberHelper.reformat(pos_perc_tx_uso, "en", NumberHelper.getLang())%></td>
          <td><hl:message key="rotulo.moeda"/>&nbsp;<%=NumberHelper.reformat(tx_uso.toString(), "en", NumberHelper.getLang())%></td>
          <td><%=NumberHelper.reformat(pos_perc_tx_uso_cond, "en", NumberHelper.getLang())%></td>
          <td><hl:message key="rotulo.moeda"/>&nbsp;<%=NumberHelper.reformat(tx_uso_cond.toString(), "en", NumberHelper.getLang())%></td>
    <% if (podeEditarPosto) { %>          
          <td>
            <a href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkEdicao)%>&pos=<%=TextHelper.forJavaScript(pos_codigo)%>')"><hl:message key="rotulo.acoes.editar"/></a>
          </td>
    <% } %>
        </tr>
  <% } %>        
        </tbody>
        <tfoot>
          <tr>
            <td colspan="12">
              <%=ApplicationResourcesHelper.getMessage("mensagem.listagem.plano.desconto", responsavel) + " - "%>
              <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
            </td>                
          </tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>                      
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="javascript:postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
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