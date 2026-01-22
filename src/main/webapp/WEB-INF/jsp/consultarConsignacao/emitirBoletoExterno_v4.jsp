<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String msgBoleto = request.getAttribute("msgBoleto").toString();
String codigoAutorizacaoSolic = (String) request.getAttribute("codigoAutorizacaoSolic");
boolean exigeCodAutSolicitacao = (request.getAttribute("exigeCodAutSolicitacao") != null);
boolean botaoVoltarPaginaInicial = (request.getAttribute("botaoVoltarPaginaInicial") != null || request.getParameter("botaoVoltarPaginaInicial") != null);

if (exigeCodAutSolicitacao && responsavel.isSer() && !TextHelper.isNull(codigoAutorizacaoSolic)) {
  session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.codigo.autorizacao", responsavel) + ": " + codigoAutorizacaoSolic);
}
%>
<c:set var="title">
  <%=ApplicationResourcesHelper.getMessage("mensagem.acao.boleto.consignacao", responsavel) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">

      <div class="row">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <button id="imprimir" aria-expanded="false" class="btn btn-primary d-print-none" type="submit" onclick="imprime();"><hl:message key="rotulo.botao.imprimir"/></button>
          </div>
        </div>
      </div>

<% out.print(msgBoleto); %>

<div class="btn-action">
 <a class="btn btn-outline-danger mt-2" href="#no-back" onclick="postData('<%=botaoVoltarPaginaInicial ? "../v3/carregarPrincipal" : TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
</div>
</c:set>
<c:set var="javascript">
<style>
  @media print {    /* for good browsers */
    body {
      margin:0;
      padding:0;
    }
    
    body p, .card-body p{
      font-size: 10px;
      margin-top: 0;
      padding-top: 0;
      line-height: 1.1;
    }
 
    h2, h3, .card .card-header .card-title{
      font-size: 12px;
      line-height: 1.2;      
      margin-rigth: .20rem;
      margin-left: .20rem;
      font-weight: bold;
      white-space: nowrap;
    } 
    
    h2, h3, .card, .card .card-header, .card .card-header .card-title{
      padding-top: 0;
      padding-bottom: 0;
      margin-top: 0;
      margin-bottom: 0;
    }

    .card-body p{
      padding-bottom: 0;
      margin: 0;
      padding-rigth: .15rem;
    }
         
    #menuAcessibilidade {
      display: none;
    }
    
    .firefox-print-fix {
      display: flex;
    }
    
    .firefox-print-fix dt {
      text-align: right;
    }

  }
  
  @page {
    margin: 0.2cm 0.2cm;
  }
</style>

<script type="text/JavaScript">
  function imprime() {
    window.print();
  }
</script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
