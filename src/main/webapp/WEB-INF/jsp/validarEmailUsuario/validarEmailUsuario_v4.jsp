<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<c:set var="bodyContent">
<%
// Responsável é o usuário do sistema 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String usuCodigo = (String) request.getAttribute("usuCodigo");
String usuEmail = (String) request.getAttribute("usuEmail");
boolean editarEmail = (boolean) request.getAttribute("editarEmail");

String msgValidacao = (String) request.getAttribute("msgValidacao");
String retornoEnvio = (String) request.getAttribute("retornoEnvio");
boolean retornoErro = (boolean) request.getAttribute("retornoErro");
%>
<%if (retornoErro) { %>
<%} else if (!TextHelper.isNull(retornoEnvio)){ %>
    <div class="alert alert-success" role="alert">
      <p class="mb-0"><%=retornoEnvio%></p>    
    </div>
<%} else if (!retornoErro && TextHelper.isNull(retornoEnvio)) { %>
    <form name="form1" method="post" action="../v3/validarEmailUsuario?acao=enviarVerificacao" autocomplete="off" onload="formLoad();">
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><%=msgValidacao%></p>    
      </div>
      <div class="form-group">
        <label for="USU_EMAIL"><hl:message key="rotulo.validaremail.email"/></label>
        <hl:htmlinput 
            classe="form-control" 
            di="USU_EMAIL" 
            name="USU_EMAIL" 
            type="text" 
            value="<%=TextHelper.forHtmlAttribute(usuEmail)%>"
            onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" size="32" 
            placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.validaremail.digite.email.placeholder", responsavel)%>"
            others="<%=(String)(editarEmail ? "disabled" : "")%>"
            /> 
      </div>
      <hl:htmlinput name="usuCodigo"     type="hidden" di="usuCodigo" value="<%=TextHelper.forHtmlAttribute(usuCodigo)%>"/>
      <hl:htmlinput name="editarEmail"     type="hidden" di="editarEmail" value="<%=(String)(editarEmail ? "true" : "false")%>"/>
    </form>
<%} %>
    <div class="clearfix text-end">
	  <button class="btn btn-outline-danger mr-2" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onclick="postData('../v3/autenticarUsuario')">
	    <hl:message key="rotulo.botao.voltar"/>
      </button>
    <%if (!retornoErro && TextHelper.isNull(retornoEnvio)){ %>
        <button class="btn btn-primary" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">
          <svg width="17"> 
          <use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
          <hl:message key="rotulo.botao.validar"/>
        </button>
      <%} %>
</div>
</c:set>
<c:set var="javascript">
<script type="text/javascript">
f0 = document.forms[0];

function formLoad() {
    focusFirstField();
}

function verificaForm() {
    if (f0.USU_EMAIL != undefined && f0.USU_EMAIL.value == "") {
        alert('<hl:message key="mensagem.informe.email.usuario"/>');
        f0.USU_EMAIL.focus();
        return false;
    }  
    return true;
}

function cleanFields() {
    if (f0.USU_EMAIL && f0.USU_EMAIL.type == "text") {
        f0.USU_EMAIL.type = "hidden";
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