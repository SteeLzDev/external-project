<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

int pwdStrengthLevel = (Integer) request.getAttribute("pwdStrengthLevel");
String link = (String) request.getAttribute("link");
String msgSenhaServidor = (String) request.getAttribute("msgSenhaServidor");
boolean senhaAtivada = request.getAttribute("senhaAtivada") != null ? (Boolean) request.getAttribute("senhaAtivada") : false;
%>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>  
</c:set>
<c:set var="title">
    <hl:message key="rotulo.ativar.senha.servidor.titulo"/>
</c:set>
<c:set var="bodyContent">

<form name="form1" id="form1" method="post" action="../v3/ativarSenhaServidor?<%=SynchronizerToken.generateToken4URL(request)%>">
  <input name="acao" type="hidden" id="acao" value="ativarSenha">
  <input name="link" type="hidden" id="link" value="<%=TextHelper.forHtmlAttribute(link)%>">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.ativar.senha.servidor.titulo"/></h2>
    </div>
    <div class="card-body">
    <%if (!senhaAtivada) {%>
      <%if (!TextHelper.isNull(msgSenhaServidor)) {%>
      <div class="alert alert-warning" role="alert">
        <p class="mb-0"><% out.write(msgSenhaServidor);%></p>
      </div>
      <%} %>
      <%
         String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
         if (!TextHelper.isNull(mascaraLogin)) {
      %>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="serLogin"><hl:message key="rotulo.ativar.senha.servidor.usuario.autorizacao"/></label>
          <hl:htmlinput name="serLogin" 
                        type="text" 
                        classe="form-control" 
                        di="serLogin" size="15" 
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.autorizacao.servidor", responsavel)%>"
                        mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>" />
        </div>
      </div>
      <% } %>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="senha"><hl:message key="rotulo.servidor.senha.atual"/></label>
          <hl:htmlpassword name="senha"
                           di="senha"
                           cryptedfield="senhaCriptografada"
                           onFocus="SetarEventoMascara(this,'#*15',true);"
                           onBlur="fout(this);ValidaMascara(this);"
                           classe="form-control"
                           isSenhaServidor="true"
                           placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.senha.atual", responsavel) %>"
                           size="20"/>
           <input name="score"    type="hidden" id="score">
           <input name="matchlog" type="hidden" id="matchlog">
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="senhaNova"><hl:message key="rotulo.servidor.nova.senha"/></label>
          <hl:htmlpassword name="senhaNova"
                           di="senhaNova"
                           cryptedfield="senhaNovaCriptografada"
                           onFocus="SetarEventoMascara(this,'#*15',true); setanewOnKeyUp(this);" 
                           onBlur="fout(this);ValidaMascara(this);testPassword(this.value);"
                           classe="form-control"
                           isSenhaServidor="true"
                           placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.senha.nova", responsavel) %>"
                           size="20" />
          <%-- <hl:message key="rotulo.servidor.nivel.seguranca"/>:--%> 
          <input name="verdict" type="hidden" id="verdict" size="10" value="" class="disabled" readonly disabled>
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-12 col-md-6">
          <label for="senhaNovaConfirmacao"><hl:message key="rotulo.servidor.confirma.nova.senha"/></label>
          <hl:htmlpassword
                 onFocus="SetarEventoMascara(this,'#*15',true);" 
                 onBlur="fout(this);ValidaMascara(this);" 
                 name="senhaNovaConfirmacao" di="senhaNovaConfirmacao"
                 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.senha.nova.confirme", responsavel) %>" 
                 classe="form-control" size="20" isSenhaServidor="true" />
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(link)%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#" onClick="verificaForm(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>      
          

<%
  } else {
%>
<div class="btn-action">
  <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(link)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
</div>  
<%  } %>
</form>
</c:set>
<c:set var="javascript">
<script language="Javascript" type="text/JavaScript" src="../js/passwordmeter_3010.js?<hl:message key="release.tag"/>"></script>
<script>
 var f0 = document.forms[0];
</script>
<script language="JavaScript" type="text/JavaScript">
var primOnFocus = true;

function newOnKeyUp(Controle) {
  testPassword(Controle.value);
}

function setanewOnKeyUp(Controle) {
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
      testPassword(Controle.value);
    }
  } 
}

function formLoad() {
  if( f0.serLogin != null) {
    f0.serLogin.focus();
  } else if( f0.senha != null) {
    f0.senha.focus();
  }
}

function setAcao(Acao) {
  f0.Acao.value = Acao;
}

function limpaSenhas() {
  f0.senhaNova.value = '';
  f0.senhaNovaConfirmacao.value = '';
  f0.verdict.value = '';
  f0.senhaNova.focus();
}

function verificaForm () {
  if (f0.serLogin != null && f0.serLogin.value == '') {
    alert('<hl:message key="mensagem.informe.servidor.usuario.autorizacao"/>');
    f0.serLogin.focus();
    return false;
  }
  if ((f0.senha.value != null) && (f0.senhaNova.value != null) && (f0.senhaNovaConfirmacao.value != null) &&
      (f0.senha.value != "") && (f0.senhaNova.value != "") && (f0.senhaNovaConfirmacao.value != "")) {
    testPassword(f0.senhaNova.value);
    if (f0.senhaNova.value.length < 8) {
        alert('<hl:message key="mensagem.erro.servidor.ativar.senha.minimo"/>');
        limpaSenhas();
        return false;
    } else if (f0.senhaNova.value.length > 15) {
        alert('<hl:message key="mensagem.erro.servidor.ativar.senha.maximo"/>');
        limpaSenhas();
        return false;
    } else if (f0.senha.value == f0.senhaNova.value) {
      alert('<hl:message key="mensagem.erro.servidor.ativar.senha.diferente.atual"/>');
    limpaSenhas();
      return false;
    } else if (f0.senhaNova.value == f0.senhaNovaConfirmacao.value) {
      if (f0.score.value < <%=(int)(pwdStrengthLevel)%>) {
      alert('<hl:message key="mensagem.erro.servidor.ativar.senha.invalida"/>');
      limpaSenhas();
      return false;
      }

      CriptografaSenha(f0.senha, f0.senhaCriptografada, false);
      CriptografaSenha(f0.senhaNova, f0.senhaNovaCriptografada, false);
      f0.senhaNovaConfirmacao.value = '';

      cleanFields();
    } else {
      alert('<hl:message key="mensagem.erro.servidor.ativar.senha.diferente.confirmacao"/>');
      limpaSenhas();
      return false;
    }
  } else {
  alert('<hl:message key="mensagem.informe.servidor.senha"/>');
    f0.senha.focus();
    return false;
  }
}

function cleanFields() {
   if (f0.serLogin && f0.serLogin.type == "text") {
     f0.serLogin.type = "hidden";
   }
   if (f0.senha && f0.senha.type == "password") {
     f0.senha.type = "hidden";
   }
   if (f0.senhaNova && f0.senhaNova.type == "password") {
     f0.senhaNova.type = "hidden";
   }
   if (f0.senhaNovaConfirmacao && f0.senhaNovaConfirmacao.type == "password") {
     f0.senhaNovaConfirmacao.type = "hidden";
   }
   f0.submit();
      
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>