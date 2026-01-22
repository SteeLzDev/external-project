<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String msg = (String) request.getAttribute("msgAlteracaoSenha");
String serEmail = (String) request.getAttribute("serEmail");
String serTel = (String) request.getAttribute("serTel");
String serCel = (String) request.getAttribute("serCel");
Integer tamMinSenha = (Integer) request.getAttribute("tamMinSenha");
Integer tamMaxSenha = (Integer) request.getAttribute("tamMaxSenha");
Integer pwdStrengthLevel = (Integer) request.getAttribute("pwdStrengthLevel");
Integer intpwdStrength = (Integer) request.getAttribute("intpwdStrength");
String strMensagemSenha1 = (String) request.getAttribute("strMensagemSenha1");
String strMensagemSenha2 = (String) request.getAttribute("strMensagemSenha2");
String strMensagemSenha3 = (String) request.getAttribute("strMensagemSenha3");
String strMensagemErroSenha = (String) request.getAttribute("strMensagemErroSenha");
boolean ignoraSeveridade = (request.getAttribute("ignoraSeveridade") != null);
boolean senhaServidorNumerica = (request.getAttribute("senhaServidorNumerica") != null);
boolean emailUsurioSer = (request.getAttribute("emailUsurioSer") != null);
boolean exigeCadEmailSerPrimeiroAcesso = (request.getAttribute("exigeCadEmailSerPrimeiroAcesso") != null);
boolean exigeCadTelefoneSerPrimeiroAcesso = (request.getAttribute("exigeCadTelefoneSerPrimeiroAcesso") != null);
boolean primeiroAcesso = responsavel.isPrimeiroAcesso();
boolean dataNascObrigatoria = (request.getAttribute("dataNascObrigatoria") != null);
boolean cpfObrigatorio = (request.getAttribute("cpfObrigatorio") != null);
boolean emailSerEditavel = (request.getAttribute("emailSerEditavel") != null);
boolean telefoneSerEditavel = (request.getAttribute("telefoneSerEditavel") != null);
boolean celularSerEditavel = (request.getAttribute("celularSerEditavel") != null);
%>
<%
    String tamanhoSenha;
    if (responsavel.isSer()){
        tamanhoSenha = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? "8" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel);
    } else {
        tamanhoSenha = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel)) ? "8" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel);
    }
%>
<c:set var="title">
   <hl:message key="rotulo.menu.alterar.senha"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.alterar.senha.titulo"/></h2>
    </div>
    <div class="card-body">
    
      <% 
        if ((responsavel.isSer() && exigeCadEmailSerPrimeiroAcesso) || 
            (responsavel.isSer() && emailUsurioSer) ||
            (!ignoraSeveridade)) {
      %>
      <div class="alert alert-warning" role="alert">
      <% if (responsavel.isSer() && exigeCadEmailSerPrimeiroAcesso) { %>
          <p class="mb-0"><hl:message key="rotulo.ajuda.alteracaoEmail.servidor.obrigatorio"/></p>
      <% } else if (responsavel.isSer() && emailUsurioSer) { %>
          <p class="mb-0"><hl:message key="rotulo.ajuda.alteracaoEmail.servidor"/></p>
      <% } %>

      <% if (!ignoraSeveridade) { %>
          <p class="mb-0">
              <%=strMensagemSenha1%>
          </p>
          <p class="mb-0">
              <%=strMensagemSenha2%>
          </p>
          <p class="mb-0">
              <%=strMensagemSenha3%>
          </p>
      <% } %>
      </div>
      <%
        }
      %>

      <form name="form1" id="form1" method="post" action="../v3/alterarSenha" autocomplete="off">
        <input type="hidden" name="acao" value="alterar" />
        <%=SynchronizerToken.generateHtmlToken(request)%>
        <h3 class="legend">
          <span><%=TextHelper.forHtmlContent(responsavel.getUsuNome())%></span>
        </h3>

        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="senha"><hl:message key="rotulo.usuario.senha.atual"/></label>
            <hl:htmlpassword name="senha" di="senha" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.senha.atual", responsavel)%>" cryptedfield="senhaRSA" onFocus="setMascaraSenhaAntiga(this,true);" onBlur="fout(this);ValidaMascara(this);" classe="form-control" isSenhaServidor="<%=(String)((responsavel.isSer()) ? "true" : "false")%>"/>
            <input type="hidden" name="score" id="score" />
            <input type="hidden" name="matchlog" id="matchlog" />
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <% if (!ignoraSeveridade) { %> 
              <div id="divSeveridade" class="alert alert-danger divSeveridade" role="alert">
                <p class="mb-0"><hl:message key="rotulo.usuario.nivel.seguranca"/>: <span id="verdict"><hl:message key="rotulo.nivel.senha.muito.baixo"/></span></p>
              </div>
            <% } %>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="senhaNova"><hl:message key="rotulo.usuario.nova.senha"/></label>
            <hl:htmlpassword name="senhaNova" di="senhaNova" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.senha.nova", responsavel)%>" cryptedfield="senhaNovaRSA" onFocus="setMascaraSenha(this,true); setanewOnKeyUp(this);" onBlur="fout(this);ValidaMascara(this);newOnKeyUp(this);" classe="form-control" isSenhaServidor="<%=(String)((responsavel.isSer()) ? "true" : "false")%>"/> 
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="senhaNovaConfirmacao"><hl:message key="rotulo.usuario.confirma.nova.senha"/></label>
            <hl:htmlpassword name="senhaNovaConfirmacao" di="senhaNovaConfirmacao" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.confirmacao.senha", responsavel)%>" cryptedfield="senhaNovaConfirmacaoRSA" onFocus="setMascaraSenha(this,true);" onBlur="fout(this);ValidaMascara(this);" classe="form-control" isSenhaServidor="<%=(String)((responsavel.isSer()) ? "true" : "false")%>"/>
          </div>
        </div>
        
        <% if (responsavel.isSer()) { %> 
          <% if (dataNascObrigatoria) { %>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="dataNasc"><hl:message key="rotulo.servidor.dataNasc"/></label>
                <input type="text" name="dataNasc" id="dataNasc" placeholder="<hl:message key="mensagem.informacao.alterar.senha.digite.data.nasc"/>" onFocus="SetarEventoMascara(this,'<%=LocaleHelper.getDateJavascriptPattern()%>',true);" onBlur="fout(this);ValidaMascara(this);" class="form-control"/>
              </div>
            </div>
          <% } %>
          <% if (cpfObrigatorio) { %>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <hl:campoCPFv4 name="cpf" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.cpf", responsavel)%>"/>
              </div>
            </div>
          <% } %>
          <% if (emailUsurioSer || exigeCadEmailSerPrimeiroAcesso || emailSerEditavel) { %>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="email"><hl:message key="rotulo.servidor.email"/><% if (!exigeCadEmailSerPrimeiroAcesso) { %> <hl:message key="rotulo.campo.opcional"/><% } %></label>
                <input type="text" name="email" id="email"  placeholder="<hl:message key="mensagem.informacao.alterar.senha.digite.email"/>" value="<%=TextHelper.forHtmlAttribute( TextHelper.isNull(serEmail) ? "" : serEmail )%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" class="form-control"/>
                <% if (exigeCadEmailSerPrimeiroAcesso && primeiroAcesso && TextHelper.isNull(serEmail)) { %>
                  <label for="confirmacao_email"><hl:message key="rotulo.servidor.confirma.email"/></label>
                  <input type="text" name="confirmacao_email" id="confirmacao_email" placeholder="<hl:message key="mensagem.informacao.alterar.senha.digite.confirmacao.email"/>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" class="form-control"/>
                <% } %>
              </div>
            </div>
          <% } %>
          <% if (exigeCadTelefoneSerPrimeiroAcesso || telefoneSerEditavel) { 
              // Quebra o telefone em DDD + número.
              String serTelDdd = "";
              if (!TextHelper.isNull(serTel)) {
                  serTel = TextHelper.dropSeparator(serTel);
                  if (serTel.length() >= 2) {
                      serTelDdd = serTel.substring(0, 2);
                      serTel = serTel.substring(2, serTel.length());
                  }
              }
          %> 
            <div class="row">
                <div class="form-group col-sm-2">
                  <label for="ddd"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
                  <hl:htmlinput name="ddd"
                          di="ddd"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(serTelDdd)%>"
                          mask="<%=LocaleHelper.getDDDMask()%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>"  />
                  
                </div>
              <div class="form-group col-sm-6 col-md-6">
                  <label for="telefone"><hl:message key="rotulo.servidor.telefone"/></label>
                  <hl:htmlinput name="telefone"
                         di="telefone"
                         type="text"
                         classe="form-control"
                         value="<%=TextHelper.forHtmlAttribute(serTel)%>"
                         mask="<%=LocaleHelper.getTelefoneMask()%>"
                         placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.telefone", responsavel) %>" />
                  
              </div>
            </div> 
          <% } %>
          <% if (celularSerEditavel) { 
            // Quebra o celular em DDD + número.
            String serCelDdd = "";
            if (!TextHelper.isNull(serCel)) {
                serCel = TextHelper.dropSeparator(serCel);
                if (serCel.length() >= 2) {
                    serCelDdd = serCel.substring(0, 2);
                    serCel = serCel.substring(2, serCel.length());
                }
            }
          %>
            <div class="row">
                <div class="form-group col-sm-2">
                  <label for="dddcel"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
                  <hl:htmlinput name="dddcel"
                          di="dddcel"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(serCelDdd)%>"
                          mask="<%=LocaleHelper.getDDDMask()%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>"  />
                  
                </div>
              <div class="form-group col-sm-6 col-md-6">
                  <label for="telefone"><hl:message key="rotulo.servidor.celular"/></label>
                  <hl:htmlinput name="celular"
                         di="celular"
                         type="text"
                         classe="form-control"
                         value="<%=TextHelper.forHtmlAttribute(serCel)%>"
                         mask="<%=LocaleHelper.getCelularMask()%>"
                         placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.celular", responsavel) %>" />
                  
              </div>
            </div> 
          
          <% } %>
        <% } %>
        
        <% if (!TextHelper.isNull(msg)) { %>
          <div class="alert alert-info" role="alert">
            <p class="mb-0">
              <%= msg %>
            </p>
          </div>
        <% } %>

      </form>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/carregarPrincipal", request))%>');">
      <hl:message key="rotulo.botao.voltar"/>
    </a>
    <a class="btn btn-primary" href="#no-back" onclick="if(verificaForm()){cleanFields();} return false;">
      <svg width="17">
        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-confirmar"></use>
      </svg>
      <hl:message key="rotulo.botao.salvar"/>
    </a>
  </div>
  <%--   Modal tamanha maximo da senha --%>
  <div class="modal fade" id="limiteSenhaModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header pb-0">
          <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.aviso" />
          </span>
          <button type="button" class="logout mr-1 d-print-none" onclick="limpaCampoSenha()" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <hl:message key="rotulo.mensagem.senha.limite.caracter.arg0" arg0="<%= tamanhoSenha %>" />
        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-outline-danger" onclick="limpaCampoSenha()" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>"
              title="<hl:message key="rotulo.botao.cancelar"/>">
              <hl:message key="rotulo.botao.cancelar" />
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
<script src="../js/validaemail.js?<hl:message key="release.tag"/>"></script>
<script src="../js/passwordmeter_3010.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
var f0 = document.forms['form1'];
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

function formLoad() {
  if( f0.senha != null) {
    f0.senha.focus();
  }
}

function setMascaraSenha(ctrl, AutoSkip) {
  SetarEventoMascara(ctrl,'<%=(String)("#" + ((responsavel.isSer() && senhaServidorNumerica) ? "D" : "*") + tamMaxSenha)%>', AutoSkip);
}

function setMascaraSenhaAntiga(ctrl, AutoSkip) {
  SetarEventoMascara(ctrl,'<%=(String)("#*" + tamMaxSenha)%>', AutoSkip);
}

function limpaSenhas() {
  f0.senhaNova.value = '';
  f0.senhaNovaConfirmacao.value = '';
  f0.senhaNova.focus();
  <% if (!ignoraSeveridade) { %>
  document.getElementById("verdict").innerText = '<hl:message key="rotulo.nivel.senha.muito.baixo"/>';
  try {
      $('#divSeveridade').removeClass('alert-success alert-danger').addClass('alert-danger');  
  } catch(e) {}
  <% } %>  
}

function verificaForm () {
  if ((f0.senha.value != null) && (f0.senhaNova.value != null) && (f0.senhaNovaConfirmacao.value != null) &&
      (f0.senha.value != "") && (f0.senhaNova.value != "") && (f0.senhaNovaConfirmacao.value != "")) {
    newOnKeyUp(f0.senhaNova);
    if (f0.senhaNova.value.length < <%=tamMinSenha%>) {
        alert('<hl:message key="mensagem.erro.nova.senha.deve.ter.pelo.menos.arg0.caracteres" arg0="<%=String.valueOf(tamMinSenha)%>"/>');
        limpaSenhas();
        return false;
    } else if (f0.senhaNova.value.length > <%=tamMaxSenha%>) {
        alert('<hl:message key="mensagem.erro.nova.senha.deve.ter.no.maximo.arg0.caracteres" arg0="<%=String.valueOf(tamMaxSenha)%>"/>');
        limpaSenhas();
        return false;
    } else if (f0.senha.value == f0.senhaNova.value) {
        alert ('<hl:message key="mensagem.erro.nova.senha.deve.diferir.atual"/>');
        limpaSenhas();
        return false;
    } else if (f0.senhaNova.value == f0.senhaNovaConfirmacao.value) {
    <% if (!ignoraSeveridade) { %>    
       if (f0.score.value < <%=(int)pwdStrengthLevel%>) {
         alert ('<%=strMensagemErroSenha%>');
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
    alert ('<hl:message key="mensagem.informe.campos.senha"/>');
    f0.senha.focus();
    return false;
  }

  if (f0.dataNasc != null) {
    if (f0.dataNasc.value == '') {
      alert('<hl:message key="mensagem.informe.data.nascimento"/>');
      f0.dataNasc.focus();
      return false;
    }
    if(!verificaData(f0.dataNasc.value)) {
      f0.dataNasc.focus();
      return false;
    }
  }

  if (f0.cpf != null) {
    if (f0.cpf.value == '') {
      alert('<hl:message key="mensagem.informe.cpf"/>');
      f0.cpf.focus();
      return false;
    }
    if (!CPF_OK(extraiNumCNPJCPF(f0.cpf.value))) {
      f0.cpf.focus();
      return false;
    }
  }

  <% if (exigeCadEmailSerPrimeiroAcesso) { %>
  if (f0.email != null && (f0.email.value == null || f0.email.value.trim() == '')) {
    alert('<hl:message key="mensagem.informe.ser.email"/>');
    f0.email.focus();
    return false;
  }

  if (f0.confirmacao_email != null && (f0.confirmacao_email.value == null || f0.confirmacao_email.value.trim() == '')) {
    alert('<hl:message key="mensagem.informe.ser.email.confirmacao"/>');
    f0.confirmacao_email.focus();
    return false;
  }

  if (f0.email != null && f0.confirmacao_email != null && f0.email.value != f0.confirmacao_email.value) {
    alert('<hl:message key="mensagem.erro.email.diverge.email.confirmacao"/>');
    f0.email.focus();
    return false;
  }
  <% } %>

  if (f0.email != null && f0.email.value != null && f0.email.value != '' && !isEmailValid(f0.email.value)) {
    alert('<hl:message key="mensagem.erro.email.invalido"/>');
    f0.email.focus();
    return false;
  }

  <% if (exigeCadTelefoneSerPrimeiroAcesso) { %>

      if (f0.ddd != null && (f0.ddd.value == null || f0.ddd.value.trim() == '')) {
          alert('<hl:message key="mensagem.informe.ser.telefone"/>');
          f0.ddd.focus();
          return false;
      }

      if (f0.telefone != null && (f0.telefone.value == null || f0.telefone.value.trim() == '')) {
          alert('<hl:message key="mensagem.informe.ser.telefone"/>');
          f0.telefone.focus();
          return false;
      }
  
  <% } %>
  
  // A validação passou.
  f0.senhaRSA.value = criptografaRSA(f0.senha.value);
  f0.senhaNovaRSA.value = criptografaRSA(f0.senhaNova.value);
  f0.senha.value = '';
  f0.senhaNova.value = '';
  f0.senhaNovaConfirmacao.value = '';

  return true;
}

jQuery(function () {
    // Pensar em forma de desabilitar o copy/paste usando outro valor ao invés de class
    var controls = jQuery("input[name$='email']") // Seleciona os campos input com name terminado em 'email'
    controls.bind("paste", function () {
        return false;
    });
    controls.bind("cut", function () {
        return false;
    });
    controls.bind("copy", function () {
        return false;
    });
});

function cleanFields() {
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
$("#senhaNova").keypress(function( event ) {
    if (event.target.value.length >= <%= tamanhoSenha %>){
      $('#limiteSenhaModal').modal('show');
    }
});

$("#senhaNovaConfirmacao").keypress(function( event ) {
  if (event.target.value.length >= <%= tamanhoSenha %> ){
      $('#limiteSenhaModal').modal('show');
  }
});

function limpaCampoSenha(){
  document.getElementById("senhaNova").value = ""; 
  document.getElementById("senhaNovaConfirmacao").value = "";  
}

</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
