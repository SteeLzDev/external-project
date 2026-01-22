<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
// Responsável é o usuário do sistema 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String ajudaCampoCaptcha = ApplicationResourcesHelper.getMessage("mensagem.informacao.login.digite.codigo.acesso", responsavel);

Integer tamMinSenhaUsuario = (Integer) request.getAttribute("tamMinSenhaUsuario");
Integer tamMaxSenhaUsuario = (Integer) request.getAttribute("tamMaxSenhaUsuario");
Integer intpwdStrength = (Integer) request.getAttribute("intpwdStrength");
Integer pwdStrengthLevel = (Integer) request.getAttribute("pwdStrengthLevel");
String strMensagemSenha = (String) request.getAttribute("strMensagemSenha");
String strMensagemErroSenha = (String) request.getAttribute("strMensagemErroSenha");
Boolean ignoraSeveridade = (Boolean) request.getAttribute("ignoraSeveridade");
Boolean autoDesbloqueio = (request.getAttribute("autodesbloqueio") != null && (Boolean) request.getAttribute("autodesbloqueio"));
Boolean geraOtp = (request.getAttribute("geraOtp") != null && (Boolean) request.getAttribute("geraOtp"));

String action = (autoDesbloqueio ? "../v3/autoDesbloquearUsuario" : "../v3/recuperarSenhaUsuario")
              + "?acao=recuperarUsuario"
              + "&enti=" + TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "enti"))
              + (geraOtp ? "" : "&cod_recuperar=" + TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "cod_recuperar"))) 
              ;

%>
<c:set var="bodyContent">
<form name="form1" method="post" action="<%=action%>" autocomplete="off" onload="formLoad();">
  <div class="alert alert-warning" role="alert">
    <p class="mb-0"><%=strMensagemSenha%></p>
  </div>
  <% if(geraOtp) { %>
  <div class="form-group">
    <label for="otp"><hl:message key="rotulo.otp"/></label> 
    <input class="form-control" id="otp" name="otp" type="text" onFocus="SetarEventoMascara(this,'<%=TextHelper.forJavaScript(LoginHelper.getMascaraMatriculaServidor())%>',true);" onBlur="fout(this);ValidaMascara(this);" placeholder="<hl:message key="rotulo.recuperar.senha.usuario.otp"/>" maxlength="6">
  </div>
  <%}%>
  <div class="form-group">
    <label for="matricula"><hl:message key="rotulo.usuario.singular"/></label> 
    <input class="form-control" id="matricula" name="matricula" type="text" onFocus="return (FocusNome());" onBlur="fout(this);ValidaMascara(this);" placeholder="<hl:message key="rotulo.recuperar.senha.usuario.dica.usuario"/>">
    <input name="score" type="hidden" id="score">
    <input name="matchlog" type="hidden" id="matchlog">
  </div>
  <div class="form-group">
    <label for="senhaNova"><hl:message key="rotulo.usuario.nova.senha"/></label> 
    <hl:htmlpassword name="senhaNova" di="senhaNova" cryptedfield="senhaNovaRSA" 
    onFocus="setMascaraSenha(this,true); setanewOnKeyUp(this);" onBlur="fout(this);ValidaMascara(this);newOnKeyUp(this);" 
    classe="form-control" size="20" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.senha.nova", responsavel)%>"/>
  </div>
  <% if (!ignoraSeveridade) { %>
  <div class="form-group">
    <div id="divSeveridade" class="alert alert-danger divSeveridade" role="alert">
      <p class="mb-0"><hl:message key="rotulo.usuario.nivel.seguranca"/>: <span id="verdict"><hl:message key="rotulo.nivel.senha.muito.baixo"/></span></p>
    </div>
  </div>
  <% } %>
  <div class="form-group">
    <label for="senhaNovaConfirmacao"><hl:message key="rotulo.usuario.confirma.nova.senha"/></label> 
    <hl:htmlpassword di="senhaNovaConfirmacao" name="senhaNovaConfirmacao" cryptedfield="senhaNovaConfirmacaoRSA" 
    onFocus="setMascaraSenha(this,true);" onBlur="fout(this);ValidaMascara(this);" classe="form-control" size="20"
    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.confirmacao.senha", responsavel)%>"/>
  </div>
  <div class="row">
    <div class="form-group col-sm-5">
      <label for="captcha"><hl:message key="rotulo.captcha.codigo"/></label> 
      <input name="captcha" id="captcha" type="text" class="form-control" placeholder="<%=TextHelper.forHtmlAttribute(ajudaCampoCaptcha)%>"/>
    </div>
    <div class="form-group col-sm-6">
      <div class="captcha">
        <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt="<hl:message key="rotulo.captcha.codigo"/>" height="50" width="200"/>
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
<div class="row justify-content-end">
  <div class="btn-action mt-2 mb-0">
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
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/validacoes.js?<hl:message key="release.tag"/>"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
<script language="Javascript" type="text/JavaScript" src="../js/passwordmeter_3010.js?<hl:message key="release.tag"/>"></script>
<script>
f0 = document.forms[0];

function formLoad() {
  focusFirstField();
}

var primOnFocus = true;

function newOnKeyUp(Controle) {
  <% if (!ignoraSeveridade) { %>
   testPassword(Controle.value, 'divSeveridade', <%=intpwdStrength%>);
  <% } %>
}

function setanewOnKeyUp(Controle) {
  <% if (!ignoraSeveridade) { %>
  if(!primOnFocus) {
    return false;
  }
  primOnFocus = false;
  var oldonkeyup = Controle.onkeyup;
  if (typeof Controle.onkeyup != 'function') {
    Controle.onkeyup = newOnKeyUp(Controle);
  } else {
    Controle.onkeyup = function() {
      if (oldonkeyup) {
        oldonkeyup(Controle);
      }
      testPassword(Controle.value, 'divSeveridade', <%=intpwdStrength%>);
    }
  } 
  <% } %>
}

function setMascaraSenha(ctrl, AutoSkip) {
  SetarEventoMascara(ctrl,'<%=(String)("#" + ("*")  + (tamMaxSenhaUsuario))%>', AutoSkip);
}

function limpaSenhas() {
  f0.senhaNova.value = '';
  f0.senhaNovaConfirmacao.value = '';
  f0.senhaNova.focus();
  <% if (!ignoraSeveridade) { %>
  document.getElementById('verdict').innetText = '<hl:message key="rotulo.nivel.senha.muito.baixo"/>';
  try {
      $('#divSeveridade').removeClass('alert-success alert-danger').addClass('alert-danger');  
  } catch(e) {}
  <% } %>
}

function verificaForm() {
	
<%if (geraOtp) {%>
    if (f0.otp.value == "") {
  	    alert('<hl:message key="mensagem.informe.otp.codigo"/>');
  	    f0.otp.focus();
  	    return false;
  	} 
<%}%>
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
  if ((f0.senhaNova != undefined) && (f0.senhaNova != null)) {
    if ((f0.matricula.value != null) && (f0.senhaNova.value != null) && (f0.senhaNovaConfirmacao.value != null) &&
      (f0.matricula.value != "") && (f0.senhaNova.value != "") && (f0.senhaNovaConfirmacao.value != "")) {
        newOnKeyUp(f0.senhaNova);
      
       if (f0.senhaNova.value == f0.senhaNovaConfirmacao.value) {
      <% if (!ignoraSeveridade) { %>    
            if (f0.score.value < <%=(int)(pwdStrengthLevel)%>) {
              alert('<%=strMensagemErroSenha%>');
              limpaSenhas();
              return false;
            }
      <% } %>      
      } else {
          alert('<hl:message key="mensagem.erro.campo.nova.senha.diverge.confirma"/>');
          limpaSenhas();
          return false;
      }
    } else {
      alert('<hl:message key="mensagem.informe.servidor.recuperar.senha.usuario"/>');
      limpaSenhas();
      return false;
    }
    // A validação passou.
    f0.senhaNovaRSA.value = criptografaRSA(f0.senhaNova.value);
    f0.senhaNova.value = '';
    f0.senhaNovaConfirmacao.value = '';
  }
  return true;
}

function cleanFields() {
    if (f0.matricula && f0.matricula.type == "text") {
      f0.matricula.type = "hidden";
    }
    if (f0.captcha && f0.captcha.type == "text") {
        f0.captcha.type = "hidden";
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