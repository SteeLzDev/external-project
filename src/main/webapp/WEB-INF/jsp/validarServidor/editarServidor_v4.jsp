<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validaform.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

ServidorTransferObject servidor = (ServidorTransferObject) request.getAttribute("servidor");
RegistroServidorTO registroServidor = (RegistroServidorTO) request.getAttribute("registroServidor");

request.setAttribute("msgConfirmacaoAprovacao", ApplicationResourcesHelper.getMessage("mensagem.confirmacao.edicao.aprovacao.servidor", responsavel));
request.setAttribute("msgConfirmacaoExclusao", ApplicationResourcesHelper.getMessage("mensagem.confirmacao.edicao.rejeicao.servidor", responsavel));
%>
<c:set var="imageHeader">
   <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <%= request.getAttribute("tituloPagina") %>
</c:set>
<c:set var="bodyContent">
 <form method="post" action="../v3/validarServidor" name="form1">
   <%= SynchronizerToken.generateHtmlToken(request) %>
   <input type="hidden" name="MM_update" value="true">
   <input type="hidden" name="acao" value="salvar">
   <input type="hidden" name="offset" value="<%= TextHelper.forHtmlAttribute(request.getParameter("offset")) %>">
   <input type="hidden" name="rseCodigo" value="<%= TextHelper.forHtmlAttribute(registroServidor != null && registroServidor.getRseCodigo() != null ? registroServidor.getRseCodigo() : "") %>">
   <%if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_DETALHES_EXCL_BLOQ_SER, CodedValues.TPC_SIM, responsavel)) {%>
        <hl:htmlinput name="srsOriginal" type="hidden" value="<%=TextHelper.forHtmlAttribute((registroServidor != null && registroServidor.getSrsCodigo() != null ? registroServidor.getSrsCodigo() : ""))%>"/>
   <%}%>
 
 
   <div class="card">
     <div class="card-header hasIcon">
      <span class="card-header-icon">
        <svg width="26"><use xlink:href="../img/sprite.svg#i-servidor"></use></svg></span>
        <h2 class="card-header-title"><hl:message key="rotulo.servidor.dados"/></h2>
     </div>
     <div class="card-body">
        <% if (servidor != null) { %>
          <jsp:include page="../editarServidor/include_campos_servidor_v4.jsp" />
        <% } %>
        <% if (registroServidor != null) { %>
          <% request.setAttribute("validarObsOperacao",true ); %>
             <jsp:include page="../editarServidor/include_campos_registro_servidor_v4.jsp" />
        <% } %>
     </div>
   </div>
   
   <div class="btn-action">
     <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
     <a class="btn btn-primary" href="#no-back" onClick="if(validarDadosObrigatoriosServidor() && confirmaAcaoConsignacao() && enviar()){document.forms[0].submit();}return false;"><hl:message key="rotulo.botao.salvar"/></a>
   </div>
 </form>
</c:set>
<c:set var="javascript">
   <script type="text/JavaScript">
   var arrayBancos = <%=(String)JspHelper.geraArrayBancos(responsavel)%>;

   function formLoad() {
     <%
     // Remove os zeros à esquerda, e garante que o valor será 
     // um numero inteiro
     String rseBancoSal = "";
     String rseBancoSalAlt = "";
     try {
       rseBancoSal = new Integer(registroServidor.getRseBancoSal()).toString();
       rseBancoSalAlt = new Integer(registroServidor.getRseBancoSalAlternativo()).toString();
     } catch (NumberFormatException ex) {
     }
     %>
     var banco = '<%=TextHelper.forJavaScriptBlock(rseBancoSal)%>';
     var bancoAlt = '<%=TextHelper.forJavaScriptBlock(rseBancoSalAlt)%>';

     if (document.forms[0].RSE_BANCOS != null) {
       AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS, arrayBancos, '', '', banco, false, false, '', '');
     }
     if (document.forms[0].RSE_BANCOS_2 != null) {
       AtualizaFiltraComboExt(document.forms[0].RSE_BANCOS_2, arrayBancos, '', '', bancoAlt, false, false, '', '');
     }

     focusFirstField();
     estabilidade();
   }
   var f0 = document.forms[0];
   window.onload = formLoad;
   </script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4> 