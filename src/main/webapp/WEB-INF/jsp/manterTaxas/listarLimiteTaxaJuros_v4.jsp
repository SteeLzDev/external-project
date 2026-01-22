<%--
* <p>Title: lst_limite_taxa_juros</p>
* <p>Description: Lista os limites de taxa de juros cadastrados para um serviço</p>
* <p>Copyright: Copyright (c) 2008</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: $
* $Revision: $
* $Date: $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
String titulo = (String) request.getAttribute("titulo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String rotuloLimitePrazoRef = (String) request.getAttribute("rotuloLimitePrazoRef");
String rotuloLimiteJurosMax = (String) request.getAttribute("rotuloLimiteJurosMax");
List<?> limites = (List<?>) request.getAttribute("limites");
String svcCodigo = (String) request.getAttribute("svcCodigo");
String msgNovoLimiteCliqueAqui = (String) request.getAttribute("msgNovoLimiteCliqueAqui");
String msgEditarLimiteCliqueAqui = (String) request.getAttribute("msgEditarLimiteCliqueAqui");
String msgExcluirLimiteCliqueAqui = (String) request.getAttribute("msgExcluirLimiteCliqueAqui"); 
boolean temLimiteTaxaJurosComposicaoCET = (boolean) request.getAttribute("temLimiteTaxaJurosComposicaoCET");
%>
<c:set var="title">
  <%=TextHelper.forHtmlContent(titulo)%>
</c:set>

<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <div class="page-title">
    <div class="row d-print-none">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <% if (responsavel.temPermissao(CodedValues.FUN_EDT_LIMITE_TAXA)) { %>
 	            <button id="acoes" class="btn btn-primary" type="submit" onClick="postData('../v3/editarLimiteTaxas?acao=incluir&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svcCodigo)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScriptAttribute(svcDescricao)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.nova.taxa"/></button>
           <% } %>
        </div>
      </div>
    </div>
  </div>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(svcDescricao)%></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>   
        <% if (responsavel.temPermissao(CodedValues.FUN_EDT_LIMITE_TAXA)) { %>
              <th scope="col" width="<%=temLimiteTaxaJurosComposicaoCET ? "30%" : "60%" %>"><%=TextHelper.forHtmlContent(rotuloLimitePrazoRef)%></th>
              <th scope="col" width="30%"><%=TextHelper.forHtmlContent(rotuloLimiteJurosMax)%></th>
              <% if(temLimiteTaxaJurosComposicaoCET) {%>
                <th scope="col" width="30%"><%=ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.cet.juros.max", responsavel)%></th>
              <% } %>
              <th scope="col" width="10%"><hl:message key="rotulo.acoes"/></th>
       <% } else {%>
              <th scope="col" width="<%=temLimiteTaxaJurosComposicaoCET ? "30%" : "60%" %>"><%=TextHelper.forHtmlContent(rotuloLimitePrazoRef)%></th>
              <th scope="col" width="20%"><%=TextHelper.forHtmlContent(rotuloLimiteJurosMax)%></th>
              <% if(temLimiteTaxaJurosComposicaoCET) {%>
                <th scope="col" width="30%"><%=ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.cet.juros.max", responsavel)%></th>
              <% } %>
       <% } %>
          </tr>
        </thead>
        <tbody>            
       <%=JspHelper.msgRstVazio(limites.size()==0, 13, responsavel)%>
       <%
         Iterator<?> it = limites.iterator();
         String ltjCodigo = null;
         Short ltjPrazoRef = null;
         BigDecimal ltjJurosMax = null;
         BigDecimal ltjVlrRef = null;
         int rowCount = 0;
         
         while (it.hasNext()) {
            CustomTransferObject limite = (CustomTransferObject)it.next();
            ltjCodigo = limite.getAttribute(Columns.LTJ_CODIGO).toString();
            ltjPrazoRef = (Short) limite.getAttribute(Columns.LTJ_PRAZO_REF);
            ltjJurosMax = (BigDecimal) limite.getAttribute(Columns.LTJ_JUROS_MAX);
            ltjVlrRef = (BigDecimal) limite.getAttribute(Columns.LTJ_VLR_REF);
       %>
         <tr>
          <td align="left"><%=(String)(ltjPrazoRef.toString() + " " + (ltjPrazoRef.toString().equals("1") ? ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.juros.prazo.singular", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.juros.prazo.plural", responsavel)))%></td>
          <td align="right"><%=NumberHelper.format(ltjJurosMax.doubleValue(), NumberHelper.getLang(), 2, 8)%></td>
          <% if(temLimiteTaxaJurosComposicaoCET) {%>
                <td align="right"><%=ltjVlrRef != null ? NumberHelper.format(ltjVlrRef.doubleValue(), NumberHelper.getLang(), 2, 8) : "-"%></td>
              <% } %>
          <% if (responsavel.temPermissao(CodedValues.FUN_EDT_LIMITE_TAXA)) { %>
          <td>
            <div class="actions">
              <div class="dropdown">
                <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <div class="form-inline">
                    <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes" />" aria-label="<hl:message key="rotulo.botao.opcoes" />"> <svg>
                        <use xlink:href="#i-engrenagem"></use></svg>
                    </span> <hl:message key="rotulo.botao.opcoes"/>
                  </div>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                   <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarLimiteTaxas?acao=incluir&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svcCodigo)%>&LTJ_CODIGO=<%=TextHelper.forJavaScript(ltjCodigo)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScriptAttribute(svcDescricao)%>&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')">
                    <hl:message key="rotulo.acoes.editar"/>
                   </a>
                   <a class="dropdown-item" href="#no-back" onClick="excluirLimiteTaxaJuros('<%=TextHelper.forJavaScript(ltjCodigo)%>', '<%=TextHelper.forJavaScript((ltjPrazoRef))%>');">
                    <hl:message key="rotulo.acoes.excluir"/>
                   </a>
                </div>
              </div>
            </div>
          </td>
          <% } %>
         </tr>
         <%           
          }
         %>
         </tbody>
         <tfoot>
          <tr>
            <td colspan="5">
              <hl:message key="rotulo.taxa.juros.listagem"/>
            </td>
          </tr>
        </tfoot>  
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    function excluirLimiteTaxaJuros(ltjCodigo, ltjPrazoRef) {
      var url = "../v3/editarLimiteTaxas?acao=excluir&_skip_history_=true&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svcCodigo)%>&titulo=<%=TextHelper.forJavaScriptAttribute(svcDescricao)%>&ltjCodigo=" + ltjCodigo + "&<%=SynchronizerToken.generateToken4URL(request)%>";
      var descPrazo = (ltjPrazoRef == "1" ? '<hl:message key="rotulo.limite.taxa.juros.prazo.singular"/>' : '<hl:message key="rotulo.limite.taxa.juros.prazo.plural"/>');
      var msgConfirmacao = '<hl:message key="mensagem.confirmacao.exclusao.limite.taxa.juros"/>';
      msgConfirmacao = msgConfirmacao.replace("{0}", ltjPrazoRef);
      msgConfirmacao = msgConfirmacao.replace("{1}", descPrazo);
  
      if (confirm(msgConfirmacao)) {
        postData(url);
      } else {
          return false;
      }
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
