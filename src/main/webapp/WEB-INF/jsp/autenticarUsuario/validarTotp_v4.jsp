<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
%>
<c:set var="bodyContent">
    <form name="form1" method="post" action="../v3/autenticarUsuario?acao=validarTotp" autocomplete="off" onload="formLoad();">
      <%= SynchronizerToken.generateHtmlToken(request) %>
      <input type="hidden" id="timeInMilliseconds" name="timeInMilliseconds" value="">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><hl:message key="mensagem.totp.informe.codigo"/></p>    
      </div>
      <div class="form-group">
        <label for="codigoTotp"><hl:message key="rotulo.codigo.validacao.totp.singular" /></label>
                <hl:htmlpassword classe="form-control" size="15" 
                     name="senha2aAutorizacao" 
                     cryptedfield="senhaRSA" 
                     cryptedPasswordFieldName="segundaSenha" 
                     placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.totp.codigo.seguranca.placeholder", responsavel)%>" 
                     nf="btnEnvia"/>
      </div>
    </form>

    <div class="mr-3 float-end">
    <div class="clearfix flex-end">
	  <button class="btn btn-outline-danger mr-2" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onclick="postData('../v3/autenticarUsuario')">
	    <hl:message key="rotulo.botao.voltar"/>
      </button>
      <button class="btn btn-primary" id="btnEnvia" type="submit" onClick="chekFields(); return false;">
        <svg width="17"> 
        <use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
        <hl:message key="rotulo.botao.validar"/>
      </button>
    </div>
  </div>
</div>
</c:set>
<c:set var="javascript">
  <script type="text/javascript">
  var f0 = document.forms[0];
  
  function formLoad() {
      focusFirstField();
  }

  function chekFields() {
      if (f0.senha2aAutorizacao != null && trim(f0.senha2aAutorizacao.value) == '') {
          alert('<hl:message key="mensagem.totp.informe.codigo"/>');
          f0.senha2aAutorizacao.focus();
          return false;
      }
      if (f0.senha2aAutorizacao != null && trim(f0.senha2aAutorizacao.value) != '') {
          if (!CriptografaSenha(f0.senha2aAutorizacao, f0.senhaRSA, false)) {
              return false;
          }
      }
      f0.timeInMilliseconds.value = (new Date()).getTime();
      f0.submit();
  }

  window.onload = formLoad;
  </script>
</c:set>
<t:empty_v4>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>