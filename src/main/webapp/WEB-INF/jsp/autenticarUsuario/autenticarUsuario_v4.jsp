<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/function-lib" prefix="fl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String nomeSistema = (String) request.getAttribute("nomeSistema");
String telaValidacao = (String) request.getAttribute("telaValidacao");
String msgUsuBloqueado = (String) request.getAttribute("msgUsuBloqueado");
String usuBloqueado = (String) request.getAttribute("usuBloqueado");
String nomeCse = (String) request.getAttribute("nomeCse");
String tituloPaginaLoginCsa = (String) request.getAttribute("tituloPaginaLoginCsa");
String mascaraNomeLogin = (String) request.getAttribute("mascaraNomeLogin");
String mensagemMascaraNomeLogin = (String) request.getAttribute("mensagemMascaraNomeLogin");
String usuNome = (String) request.getAttribute("usuNome");
String usuLogin = (String) request.getAttribute("usuLogin");
String telaLogin = (String) request.getAttribute("telaLogin");
String ajudaCampoCaptcha = (String) request.getAttribute("ajudaCampoCaptcha");
String urlCentralizadorAcesso = request.getParameter("urlCentralizadorAcesso");
boolean validacaoSeguranca = (Boolean) request.getAttribute("validacaoSeguranca");
boolean exibeCaptcha = (Boolean) request.getAttribute("exibeCaptcha");
boolean exibeCaptchaAvancado = (Boolean) request.getAttribute("exibeCaptchaAvancado");
boolean exibeCaptchaDeficiente = (Boolean) request.getAttribute("exibeCaptchaDeficiente");
boolean redirectAutoDesbloqueio = !TextHelper.isNull(request.getAttribute("redirectAutoDesbloqueio")) ? (Boolean) request.getAttribute("redirectAutoDesbloqueio") : false;

//Validação para o botão de "Mais opções":
//1 - quando tiver 0 não habilita o botão;
//2 - quando tiver 1 mostra apenas o botão com a opção;
//3 - quando tiver mais de um o botão aparece com as opções.
int quantidadeDeItensMaisOpcoes = 0;

if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_LINK_LOGIN_SERVIDOR_LOGIN_GERAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
  quantidadeDeItensMaisOpcoes++;
}
if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_USU, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
  quantidadeDeItensMaisOpcoes++;
}
if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_CADASTRA_SENHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
  quantidadeDeItensMaisOpcoes++;
}
if (ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) ||
ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) ||
ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_SUP, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
  quantidadeDeItensMaisOpcoes++;
}
%>
<c:set var="bodyContent">
    <% if (urlCentralizadorAcesso == null && validacaoSeguranca) { %>
      <form name="enviar" method="post" action="../v3/autenticarUsuario?acao=autenticar" onSubmit="if(ValidaCampos()){cleanFields();} return false;" autocomplete="off">
          <div class="form-group">
              <label for="username"><hl:message key="rotulo.usuario.tela.login"/></label>
              <input type="text" class="form-control" id="username" name="username" placeholder='<hl:message key="mensagem.informacao.digite.usuario" />'>
          </div>
          <div class="row">
            <% if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_LINK_LOGIN_SERVIDOR_LOGIN_GERAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
              <div class="col-7 float-start mb-2">
                <a class="btn btn-outline-dark ml-0" onClick="postData('../v3/autenticar  ')"><hl:message key="rotulo.botao.portal.servidor"/></a>
              </div>
            <% } %> 
            <div class="col justify-content-end">
              <button class="btn btn-primary" type="submit"><svg width="17"><use xlink:href="#i-avancar"></use></svg><hl:message key="rotulo.botao.proxima" /></button>
            </div>
          </div>
          <input type="hidden" name="telaValidacao" value="<%=(!TextHelper.isNull(telaValidacao)) ? TextHelper.forHtmlAttribute(telaValidacao) : ""%>"/>
      </form>
    <% } else if (!validacaoSeguranca) { %>

    <form method="post" action="../v3/autenticarUsuario?acao=autenticar" onSubmit="if(ValidaLogin(f0.senha, f0.senhaRSA)){cleanFields();} return false;" autocomplete="off">
        <div class="form-group">
            <label for="username"><hl:message key="rotulo.usuario.singular"/></label>
            <input type="text" class="form-control" id="username" name="username" placeholder='<hl:message key="mensagem.informacao.digite.usuario" />'>
        </div>
        <div class="form-group mb-0">
            <label for="loginSenha"><hl:message key="rotulo.usuario.senha"/></label>
            <hl:htmlpassword classe="form-control mb-2" name="senha" cryptedfield="senhaRSA" nf='<%=TextHelper.forHtmlAttribute( exibeCaptcha ? "captcha" : exibeCaptchaAvancado ? "recaptcha-checkbox-checkmark" : "btnOK" )%>' placeHolder="${ajudaSenha}"/>
       <% if (quantidadeDeItensMaisOpcoes <= 3) { %>
        <div class="row">
          <% if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_LINK_LOGIN_SERVIDOR_LOGIN_GERAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
            <div class="col-sm-12">
              <a href="#no-back" onClick="postData('../v3/autenticar?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>')">
                <hl:message key="mensagem.clique.acessar.portal.servidor.v4"/>
              </a>
            </div>
          <% } if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_USU, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
            <div class="col-sm-12">
              <a href="#no-back" onClick="postData('../v3/recuperarSenhaUsuario?acao=iniciarUsuario')" id="linkRecuperaSenha">
                <hl:message key="mensagem.senha.usuario.recuperar.login.v4"/>
              </a>
            </div>
          <% } if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_CADASTRA_SENHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
            <div class="col-sm-12">
              <a href="#no-back" onClick="postData('../v3/cadastrarSenhaServidor?acao=iniciar')">
                <hl:message key="mensagem.senha.servidor.cadastrar.login.v3"/>
              </a>
            </div>
          <% } if (ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) ||
                        ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) ||
                        ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_SUP, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>        
            <div class="col-sm-12">
              <a href="#no-back" onClick="postData('../v3/autoDesbloquearUsuario?acao=iniciarUsuario')" id="linkAutoDesbloqueio">
                <hl:message key="mensagem.senha.usuario.auto.desbloqueio.login"/>
              </a>        
            </div>
          <% } %>
        </div>
        <% } %>
        </div>
        <div class="row">
            <% if (exibeCaptcha) { %>
            <div class="form-group col-sm-5">
                <label for="loginCodigo"><hl:message key="rotulo.captcha.codigo"/></label>
                <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
            </div>
            <% } %>
            <div class="form-group col-sm-6">
                <div class="captcha">
                <% if (exibeCaptcha) { %>
                    <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
                    <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
                    <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
                      data-bs-content='<hl:message key="mensagem.ajuda.captcha.usuario.v3"/>'
                      data-original-title=<hl:message key="rotulo.ajuda" />>
                      <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
                    </a>
                    <% } else if (exibeCaptchaAvancado && (!validacaoSeguranca || (!TextHelper.isNull(telaValidacao) && telaValidacao.equals("2")))) { %>
                    <hl:recaptcha />
                <% } else if (exibeCaptchaDeficiente && (!validacaoSeguranca || (!TextHelper.isNull(telaValidacao) && telaValidacao.equals("2")))) {%>
                <label for="loginCodigo"><hl:message key="rotulo.captcha.codigo"/></label>
                <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
                    <div class="mt-3" id="divCaptchaSound"></div>
                    <a class="ml-2" href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
                    <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
                <% } %>
                </div>
            </div>
        </div>
        <% if (quantidadeDeItensMaisOpcoes > 3) { %>
          <div class="row justify-content-between">
            <div class="col-sm-2 col-12 mb-3">
                <button type="button" class="btn btn-primary btn-mais-opcoes" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <hl:message key="rotulo.botao.mais.opcoes"/>
                </button>
                <ul class="dropdown-menu dropdown-menu-right">
                    <% if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_LINK_LOGIN_SERVIDOR_LOGIN_GERAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
                    <li>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/autenticar?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>')"><hl:message key="mensagem.clique.acessar.portal.servidor.v4"/></a>
                    </li>
                    <% } %>
                    <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_USU, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
                    <li>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/recuperarSenhaUsuario?acao=iniciarUsuario')" id="linkRecuperaSenha"><hl:message key="mensagem.senha.usuario.recuperar.login.v4"/></a>
                    </li>
                    <% } %>
                    <% if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_CADASTRA_SENHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
                    <li>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/cadastrarSenhaServidor?acao=iniciar')"><hl:message key="mensagem.senha.servidor.cadastrar.login.v3"/></a>
                    </li>
                    <% } %>
                    <% if (ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) ||
                           ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) ||
                           ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_SUP, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>        
                    <li>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/autoDesbloquearUsuario?acao=iniciarUsuario')" id="linkAutoDesbloqueio"><hl:message key="mensagem.senha.usuario.auto.desbloqueio.login"/></a>
                    </li>        
                    <% } %>
                </ul>
            </div>
          <div class="mr-3">
            <div class="clearfix text-right">
              <button id="btnOK" class="btn btn-primary" type="submit"><svg width="17"><use xlink:href="#i-avancar"></use></svg><hl:message key="rotulo.botao.entrar" /></button>
            </div>
          </div>
        </div>
        <% } if (quantidadeDeItensMaisOpcoes <= 3) { %>
        <div class="clearfix text-right">
          <button id="btnOK" class="btn btn-primary" type="submit"><svg width="17"><use xlink:href="#i-avancar"></use></svg><hl:message key="rotulo.botao.entrar" /></button>
        </div>
        <% } %>
    </form>  
    <% } else { %>
      <div class="row">
        <div class="col justify-content-end">
          <a class="btn btn-primary" href="<%=urlCentralizadorAcesso %>/select-company"><hl:message key="rotulo.botao.voltar" /></a>
        </div>
      </div>
    <% } %>
    
</c:set>
<c:set var="javascript">
<style id="antiClickjack">body{display:none !important;}</style>
<script src="../node_modules/jquery-ui/dist/jquery-ui.min.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
if (self === top) {
    var antiClickjack = document.getElementById("antiClickjack");
    antiClickjack.parentNode.removeChild(antiClickjack);
} else {
    top.location = self.location;
}

function ValidaCampos() {
  var Nome = f0.username.value;

  if ((Nome == "") || (Nome == null)) {
    alert(mensagem('mensagem.informe.login.usuario'));
    f0.username.focus();
    return false;
  }

  // Habilita campos desabilitados
  enableAll();
  return true;
}

function formLoad() {
  FocusNome();
  f0.username.focus();
  <% if (redirectAutoDesbloqueio) { %>
    $("#dialogAutoDesbloqueio").modal("show");
  <%} else if (!TextHelper.isNull(msgUsuBloqueado) && usuBloqueado.equals("true")) {%>       
       alert('<%=TextHelper.forJavaScript(msgUsuBloqueado)%>');
  <%} else {%>
    $("#dialogAutoDesbloqueio").modal("hide");
  <%} %>
}

window.onload = formLoad;

function cleanFields() {
  if (f0.username && f0.username.type == "text") {
    f0.username.type = "hidden";
  }
  if (f0.senha && f0.senha.type == "password") {
    f0.senha.type = "hidden";
  }
  f0.submit();
}

f0 = document.forms[0];
</script>
</c:set>
<t:empty_v4>
    <jsp:attribute name="loginUsuario">true</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>
