<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
// Responsável é o usuário do sistema 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String usuCodigo = (String) request.getAttribute("usuCodigo");
String msgOtp = (String) request.getAttribute("msgOtp");
%>
<c:set var="bodyContent">
  <form name="form1" method="post" action="../v3/autoDesbloquearServidor?acao=validarOtp" autocomplete="off">
    <div class="alert alert-warning" role="alert">
      <p><hl:message key="rotulo.auto.desbloqueio.servidor.titulo"/></p>
      <p><%=TextHelper.forHtmlContent(msgOtp.toUpperCase())%></p>
    </div>
    <div class="form-group">
      <label for="SER_OTP"><hl:message key="rotulo.servidor.recupera.senha.otp"/></label> 
      <hl:htmlinput type="text" classe="form-control" di="SER_OTP" name="SER_OTP" value=""/>
    </div>
    <input type="hidden" name="usuCodigo" value="<%=usuCodigo%>"/>
  </form>
  <div class="row justify-content-end">
    <div class="btn-action mt-2 mb-0">        
      <button class="btn btn-outline-danger mr-2" aria-label="<hl:message key="rotulo.botao.voltar"/>" onClick="postData('<%=TextHelper.forJavaScriptAttribute(LoginHelper.getPaginaLoginServidor())%>'); return false;">
        <hl:message key="rotulo.botao.voltar"/>
      </button>
      <button class="btn btn-primary" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">
        <svg width="17"> 
          <use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
        <hl:message key="rotulo.botao.concluir"/>
      </button>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/passwordmeter_3010.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">
    f0 = document.forms[0];
    
    function formLoad() {
        focusFirstField();
    }

    function verificaForm() {
        if (f0.SER_OTP != undefined && f0.SER_OTP.value == "") {
            alert('<hl:message key="mensagem.auto.desbloqueio.insercao.otp"/>');
            f0.SER_OTP.focus();
            return false;
        }
        return true;
    }

    function cleanFields() {
        if (f0.SER_OTP && f0.SER_OTP.type == "text") {
            f0.SER_OTP.type = "hidden";
        }
        f0.submit();
    }
    
    window.onload = formLoad;
  </script>
</c:set>
<t:empty_v4>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>