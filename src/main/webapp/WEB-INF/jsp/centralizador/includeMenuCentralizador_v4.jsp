<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.web.v3.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String urlConsig =  (String) (session.getAttribute("attrSessionAcessoUrl") != null ? session.getAttribute("attrSessionAcessoUrl") : "");
String urlCentral = (String) (session.getAttribute("urlCentralizador") != null ? session.getAttribute("urlCentralizador") : "");
String parametrosCentral = (String) (session.getAttribute("parametrosCentral") != null ? session.getAttribute("parametrosCentral") : "");
String msgAcessoCentral = ApplicationResourcesHelper.getMessage("mensagem.centralizador.sistemaAcesso.titulo", responsavel);
Boolean pingCentralizador = (Boolean) request.getAttribute("executePingCentralizador"); 
%>


    <div class="dropdown-divider"></div>
        <div class="user-info pl-1">
          <span class="user-name"><%=TextHelper.forHtmlContent(msgAcessoCentral)%></span>
        </div>
      <a class="dropdown-item" href="<%=urlCentral %>escolherSistema.page" onClick="encerra(); resetTimer();"><%=ApplicationResourcesHelper.getMessage("mensagem.centralizador.escolher.outro.sistema", responsavel)%></a>
      <a class="dropdown-item" href="<%=urlCentral %>alterarSenhas.page" onClick="resetTimer();"><%=ApplicationResourcesHelper.getMessage("mensagem.centralizador.alterar.senhas", responsavel)%></a>
      <a class="dropdown-item" href="#sairModalCentral" data-bs-toggle="modal"><%=ApplicationResourcesHelper.getMessage("rotulo.centralizador.sair", responsavel)%></a>

<script type="text/JavaScript">
  function encerra() {
    url = '<%=TextHelper.forHtmlAttribute(urlConsig).toString().replace("acesso","v3/sairSistema").replace("/index.jsp", "?acao=sair&").replace("protocolo=2", "")%><%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>';
    $.ajax({
        type: "POST",
        url: url,
        async: false
      });
  }
       
/*
 * Funções e variáveis auxiliares para fazer as chamadas ao servidor web para manter a sessão aberta.
 */
<% if(pingCentralizador!= null && pingCentralizador){%>
document.addEventListener("DOMContentLoaded", function(){ 
  function makeRequest(url) {
    $.ajax({
        type: "GET",
        url: url,
        async: false,
        beforeSend: function(request) {
      	    request.setRequestHeader('Accept','message/x-formresult');
    	  }
      });
    return false;
  }
  makeRequest("<%=urlCentral%>keepSession");
});
<%}%>
</script>