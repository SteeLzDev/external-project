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
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<c:set var="bodyContent">
<%
// Responsável é o usuário do sistema 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean omiteCpf = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);
boolean autoDesbloqueio = (request.getAttribute("autodesbloqueio") != null && (Boolean) request.getAttribute("autodesbloqueio"));
String tituloPagina = (String) request.getAttribute("tituloPagina");

String action = (autoDesbloqueio ? "../v3/autoDesbloquearUsuario" : "../v3/recuperarSenhaUsuario")
              + "?acao=concluirUsuario"
              ;

String ajudaCampoCaptcha = ApplicationResourcesHelper.getMessage("mensagem.informacao.login.digite.codigo.acesso", responsavel);
%>
<form name="form1" method="post" action="<%=action%>" autocomplete="off" onload="formLoad();">
  <div class="alert alert-warning" role="alert">
    <p class="mb-0"><%=tituloPagina%></p>    
  </div>
  <div class="form-group">
    <label for="matricula"><hl:message key="rotulo.usuario.recuperar.senha.login"/></label> 
    <input class="form-control" id="matricula" name="matricula" type="text" onFocus="return (FocusNome());" onBlur="fout(this);ValidaMascara(this);" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "username"))%>" placeholder="<hl:message key="rotulo.recuperar.senha.usuario.dica.usuario"/>">
  </div>
  <% if (omiteCpf) { %>
  <div class="form-group">
    <label for="USU_EMAIL"><hl:message key="rotulo.usuario.recuperar.senha.email"/></label>
    <hl:htmlinput classe="form-control" di="USU_EMAIL" name="USU_EMAIL" type="text" 
        onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" size="32" placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.recuperar.senha.usuario.dica.email", responsavel)%>"/> 
  </div>
  <% } else { %>
  <div class="form-group">
    <label for="USU_CPF"><hl:message key="rotulo.usuario.recuperar.senha.cpf"/></label> 
    <hl:htmlinput type="text" classe="form-control" di="USU_CPF" name="USU_CPF" mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>" placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.recuperar.senha.usuario.dica.cpf", responsavel)%>"/>
  </div>
  <% } %>
  <div class="row justify-content-between mr-1">
    <div class="form-group col-sm-5">
      <label for="captcha"><hl:message key="rotulo.captcha.codigo"/></label> 
      <input class="form-control" name="captcha" id="captcha" type="text" placeholder="<%=TextHelper.forHtmlAttribute(ajudaCampoCaptcha)%>" type="text">
    </div>
    <div class="form-group col-sm-6">
      <div class="captcha">
        <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt="<hl:message key="rotulo.captcha.codigo"/>" title="<hl:message key="rotulo.captcha.codigo"/>" height="50" width="200"/>
        <div class="float-end">
          <a href="javascript:void(0);" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt="<hl:message key="mensagem.gerar.novo.captcha.clique.aqui"/>" title="<hl:message key="mensagem.gerar.novo.captcha.clique.aqui"/>" border="0"/></a>
          <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
            data-bs-content='<hl:message key="mensagem.ajuda.captcha.usuario.v3"/>'
            data-original-title=<hl:message key="rotulo.ajuda" />> 
            <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
          </a>
        </div>
      </div>
    </div>
  </div>
</form>
<div class="row justify-content-end mr-1">
  <div class="col-12 col-sm-8">
    <div class="clearfix text-right">
	  <button class="btn btn-outline-danger mr-2" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onclick="postData('../v3/autenticarUsuario')">
	    <hl:message key="rotulo.botao.voltar"/>
      </button>
      <button class="btn btn-primary" type="submit" onClick="if(verificaForm()){cleanFields();} return false;">
        <svg width="17"> 
        <use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
        <hl:message key="rotulo.botao.confirmar"/>
      </button>
    </div>
  </div>
</div>
</c:set>
<c:set var="javascript">
<script type="text/javascript">
f0 = document.forms[0];

function formLoad() {
    focusFirstField();
}

function verificaForm() {
    if (f0.matricula.value == "") {
        alert('<hl:message key="mensagem.informe.login.usuario"/>');
        f0.matricula.focus();
        return false;
    } 
    if ((f0.captcha != undefined) && (f0.captcha.value == "")) {
        alert('<hl:message key="mensagem.informe.captcha.codigo"/>');
        f0.captcha.focus();
        return false;
    }
    if (f0.USU_CPF != undefined && f0.USU_CPF.value == "") {
        alert('<hl:message key="mensagem.informe.cpf"/>');
        f0.USU_CPF.focus();
        return false;
    } else if (f0.USU_CPF != undefined && !CPF_OK(extraiNumCNPJCPF(f0.USU_CPF.value))) {
        f0.USU_CPF.focus();
        return false;
    }
    if (f0.USU_EMAIL != undefined && f0.USU_EMAIL.value == "") {
        alert('<hl:message key="mensagem.informe.email.usuario"/>');
        f0.USU_EMAIL.focus();
        return false;
    }  
    return true;
}

function cleanFields() {
    if (f0.matricula && f0.matricula.type == "text") {
        f0.matricula.type = "hidden";
    }
    if (f0.USU_CPF && f0.USU_CPF.type == "text") {
        f0.USU_CPF.type = "hidden";
    }
    if (f0.captcha && f0.captcha.type == "text") {
        f0.captcha.type = "hidden";
    }
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