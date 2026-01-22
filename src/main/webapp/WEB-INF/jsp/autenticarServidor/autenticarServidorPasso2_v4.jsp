<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean exibeCaptcha = (Boolean) request.getAttribute("exibeCaptcha");
boolean exibeCaptchaAvancado = (Boolean) request.getAttribute("exibeCaptchaAvancado");
boolean exibeCaptchaDeficiente = (Boolean) request.getAttribute("exibeCaptchaDeficiente");
boolean exibeVk = (Boolean) request.getAttribute("exibeVk");

int quantidadeDeItensMaisOpcoes = (Integer) request.getAttribute("quantidadeDeItensMaisOpcoes");

String mascaraNomeLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_NOME_LOGIN, responsavel);
String mensagemMascaraNomeLogin = LoginHelper.getMensagemMascaraNomeLogin(responsavel);
String telaLogin = LoginHelper.getPaginaLoginServidor();

String usuLogin = (String) request.getAttribute("usuLogin");
String usuNome = (String) request.getAttribute("usuNome");

String estOrgCodigo = (String) request.getAttribute("estOrgCodigo");
String estOrgNome = (String) request.getAttribute("estOrgNome");

boolean loginComCfp = (boolean) request.getAttribute("loginComCfp");

// Parâmetros para definir URL para instalação do aplicativo nas lojas
String urlAppGoogleStore = (String) request.getAttribute("urlAppGoogleStore");
String urlAppAppleStore = (String) request.getAttribute("urlAppAppleStore");

String estCodigo = session.getAttribute("codigo_estabelecimento") != null ? (String) session.getAttribute("codigo_estabelecimento") : "";
String estIdentificador = session.getAttribute("estabelecimento") != null ? (String) session.getAttribute("estabelecimento") : "";
String orgCodigo = session.getAttribute("codigo_orgao") != null ? (String) session.getAttribute("codigo_orgao") : "";
String orgIdentificador = session.getAttribute("orgao") != null ? (String) session.getAttribute("orgao") : "";
String rseMatricula = session.getAttribute("rse_matricula") != null ? (String) session.getAttribute("rse_matricula") : "";
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="bodyContent">
    <form name="enviar" 
          method="post" 
          action="../v3/autenticar?acao=autenticar"  
          autocomplete="off">
      <div class="form-group ">      
        <% if (!TextHelper.isNull(estOrgNome) && !loginComCfp) { %>    
          <label for="nome_orgao">
            <hl:message key="rotulo.orgao.singular"/>
          </label>          
          <input type="text" class="form-control" name="nome_orgao" value="<%=TextHelper.forHtmlAttribute(estOrgNome)%>" disabled>
          <input type="hidden" class="form-control" name="codigo_orgao" value="<%=TextHelper.forHtmlAttribute(estOrgCodigo)%>" >           
        <% } %>        
      </div>
      <div class="form-group">
          <label for="loginMatricula">
            <hl:message key="rotulo.usuario.singular"/>
          </label>
          <input type="text" class="form-control" id="loginMatricula" name="loginMatricula" value="<%=TextHelper.forHtmlAttribute(usuNome)%>" disabled>
      </div>
      <div class="alert alert-warning" role="alert">
            <a href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(telaLogin)%>')" >
              <%=TextHelper.forHtmlContentComTags(mensagemMascaraNomeLogin)%>
            </a>
      </div>      
      <div class="form-group">        
          <label for="senha"><hl:message key="rotulo.usuario.senha"/></label>            
          <% if (exibeVk) { %>
          <div class="row">
          <div class="col-10">
          <% } %>
          <hl:htmlpassword classe="form-control mb-2" 
                           di="senha"
                           name="senha"
                           placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.login.digite.senha.acesso", responsavel)%>'                            
                           cryptedfield="senhaRSA" 
                           isSenhaServidor="true" 
                           nf="<%=TextHelper.forHtmlAttribute( exibeCaptcha ? "captcha" : exibeCaptchaAvancado ? "recaptcha-checkbox-checkmark":"btnOK")%>"
                            />
          <% if (exibeVk) { %>
          </div>
          <div class="col-2"><img src="../img/keyboard/keyboard.svg" /></div>     
          </div> 
          <% } %>              
        <% if (quantidadeDeItensMaisOpcoes <= 3) { %>
        <div class="row">
        <% if (ParamSist.paramEquals(CodedValues.TPC_VALIDA_OTP_PRIMEIRO_ACESSO_USUARIO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
          <div class="col-sm-12">
            <a href="#no-back" onClick="postData('../v3/efetivarPrimeiroAcesso?acao=iniciar&usu=servidor')">
              <hl:message key="mensagem.senha.servidor.primeiro.acesso.v3"/>
            </a>      
          </div>                          
        <% } if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
          <div class="col-sm-12">
            <a href="#no-back" onClick="postData('../v3/recuperarSenhaServidor?acao=iniciarServidor&username=<%=TextHelper.forHtmlAttribute(usuLogin)%>')">
              <hl:message key="mensagem.senha.servidor.recuperar.login.v3"/>
            </a>
          </div>
        <% } if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_CADASTRA_SENHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
          <div class="col-sm-12">
            <a href="#no-back" onClick="postData('../v3/cadastrarSenhaServidor?acao=iniciar&username=<%=TextHelper.forHtmlAttribute(usuLogin)%>')">
              <hl:message key="mensagem.senha.servidor.cadastrar.login.v3"/>
            </a>
          </div>
        <% } if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>        
          <div class="col-sm-12">
            <a href="#no-back" onClick="postData('../v3/abrirChatSuporte')" >
              <hl:message key="rotulo.menu.suporte.online"/>
            </a>
          </div>
        <% } if (!ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_DESABILITADO, AcessoSistema.getAcessoUsuarioSistema())) { %>        
          <div class="col-sm-12">
            <a href="#no-back" onClick="postData('../v3/autoDesbloquearServidor?acao=iniciarServidor')">
              <hl:message key="mensagem.senha.servidor.auto.desbloqueio.login"/>
            </a>
          </div>
        <% } %>
        </div>
      <% } %>
      </div>         
      <div class="row">
      <% if (exibeCaptcha) { %>
        <div class="form-group col-sm-5">
          <label for="captcha"><hl:message key="rotulo.captcha.codigo"/></label>
          <input type="text" 
                 class="form-control" 
                 id="captcha" 
                 name="captcha" 
                 placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'                 
                 />
        </div>
        <div class="form-group col-sm-6">
          <div class="captcha">
            <img name="captcha_img" 
               src='../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>'
               alt='<hl:message key="rotulo.captcha.codigo"/>' 
               title='<hl:message key="rotulo.captcha.codigo"/>'
            />
            <a href="#no-back" onclick="reloadCaptcha()">
            <img src="../img/icones/refresh.png" 
                 alt='<hl:message key="rotulo.captcha.novo.codigo"/>' 
                 title='<hl:message key="rotulo.captcha.novo.codigo"/>' 
                 border="0"/>
            </a>
            <a href="#no-back" onclick="helpCaptcha2()">
            <img src="../img/icones/help.png" 
                 alt='<hl:message key="rotulo.ajuda"/>' 
                 title='<hl:message key="rotulo.ajuda"/>' 
                 border="0"/>
            </a>
          </div>
        </div> 
        <% } else if (exibeCaptchaAvancado) { %>
          <div class="form-group col-sm-6">
            <hl:recaptcha />
          </div> 
        <% } else if (exibeCaptchaDeficiente) { %>
         <div class="form-group col-sm-6">
          <label for="loginCodigo"><hl:message key="rotulo.captcha.codigo"/></label>
          <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
              <div class="mt-3" id="divCaptchaSound"></div>
              <a class="ml-2" href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
              <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
         </div>
        <% } %>
    </div>
    <% if (quantidadeDeItensMaisOpcoes > 3) { %>
      <div class="row-edit">
        <div class="col-4">
            <button type="button" class="btn btn-primary btn-mais-opcoes h-auto" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <hl:message key="rotulo.botao.mais.opcoes"/>
            </button>
            <ul class="dropdown-menu dropdown-menu-right">                
                <% if (ParamSist.paramEquals(CodedValues.TPC_VALIDA_OTP_PRIMEIRO_ACESSO_USUARIO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
                <li>
                  <a  href="#no-back" class="dropdown-item" onClick="postData('../v3/efetivarPrimeiroAcesso?acao=iniciar&usu=servidor')">
                    <hl:message key="mensagem.senha.servidor.primeiro.acesso.v3"/>
                  </a>
                </li>                                
                <% } %>
                <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
                <li>
                  <a  href="#no-back" class="dropdown-item" onClick="postData('../v3/recuperarSenhaServidor?acao=iniciarServidor&username=<%=TextHelper.forHtmlAttribute(usuLogin)%>')">
                    <hl:message key="mensagem.senha.servidor.recuperar.login.v3"/>
                  </a>
                </li>                                
                <% } %>
                <% if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_CADASTRA_SENHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>
                <li>
                  <a  href="#no-back" class="dropdown-item" onClick="postData('../v3/cadastrarSenhaServidor?acao=iniciar&username=<%=TextHelper.forHtmlAttribute(usuLogin)%>')">
                    <hl:message key="mensagem.senha.servidor.cadastrar.login.v3"/>
                  </a>
                </li>                                
                <% } %>
                <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) { %>        
                  <li>
                  <a  href="#no-back" class="dropdown-item" onClick="postData('../v3/abrirChatSuporte')" >
                      <hl:message key="rotulo.menu.suporte.online"/>
                  </a>
                  </li>        
                <% } %>
                <% if (!ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_DESABILITADO, AcessoSistema.getAcessoUsuarioSistema())) { %>        
                  <li>
                  <a  href="#no-back" class="dropdown-item" onClick="postData('../v3/autoDesbloquearServidor?acao=iniciarServidor')">
                    <hl:message key="mensagem.senha.servidor.auto.desbloqueio.login"/>
                  </a>
                  </li>        
                <% } %>
            </ul>
        </div>
        
          
        <div class="col-8">
          <div class="clearfix p-0 text-end">
            <button type="button" class="btn btn-outline-danger mr-2" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onclick="postData('../v3/autenticar'); return false;">
              <hl:message key="rotulo.botao.voltar"/>
            </button>
            <button id="btnOK" name="btnOK" class="btn btn-primary" type="submit" onclick="if(ValidaLogin(f0.senha, f0.senhaRSA)){cleanFields();} return false;" >
                    <svg width="17"><use xlink:href="#i-avancar"></use></svg>
                    <hl:message key="rotulo.botao.entrar" />                
            </button>
          </div>       
        </div>     
      </div>
    <%} if (quantidadeDeItensMaisOpcoes <= 3) {%>
      <div class="clearfix text-end">
        <button type="button" class="btn btn-outline-danger mr-2" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onclick="postData('../v3/autenticar'); return false;">
          <hl:message key="rotulo.botao.voltar"/>
        </button>
        <button id="btnOK" name="btnOK" class="btn btn-primary" type="submit" onclick="if(ValidaLogin(f0.senha, f0.senhaRSA)){cleanFields();} return false;" >
                <svg width="17"><use xlink:href="#i-avancar"></use></svg>
                <hl:message key="rotulo.botao.entrar" />                
        </button>
      </div>       
     <% } %>
     <input type="hidden" name="username" value="<%=TextHelper.forHtmlAttribute(usuLogin)%>">
     <input type="hidden" name="codigo_estabelecimento" value="<%=TextHelper.forHtmlAttribute(estCodigo)%>">
     <input type="hidden" name="estabelecimento" value="<%=TextHelper.forHtmlAttribute(estIdentificador)%>">
     <input type="hidden" name="codigo_orgao" value="<%=TextHelper.forHtmlAttribute(orgCodigo)%>">
     <input type="hidden" name="orgao" value="<%=TextHelper.forHtmlAttribute(orgIdentificador)%>">
     <input type="hidden" name="rseMatricula" value="<%=TextHelper.forHtmlAttribute(rseMatricula)%>">
     
     <%out.print(SynchronizerToken.generateHtmlToken(request));%>      
  </form>
</c:set>

<% if (!"v6".equals(versaoLeiaute)) { %>
  <c:set var="secao">
      <section class="download-app-section">
        <% if (!TextHelper.isNull(urlAppAppleStore)) { %>
          <a class="app-store" target='_blank' rel='noopener noreferrer' href='<%=urlAppAppleStore%>' alt='<hl:message key="mensagem.aplicativo.ser.disponivel.apple.store"/>' title='<hl:message key="mensagem.aplicativo.ser.disponivel.apple.store"/>'></a>
        <% } %>
        <% if (!TextHelper.isNull(urlAppGoogleStore)) { %>
          <a class="play-store" target='_blank' rel='noopener noreferrer' href='<%=urlAppGoogleStore%>' alt='<hl:message key="mensagem.aplicativo.ser.disponivel.google.store"/>' title='<hl:message key="mensagem.aplicativo.ser.disponivel.google.store"/>'></a>
        <% } %>
      </section>
  </c:set>
<% } %>


<c:set var="javascript">
<% if (exibeCaptchaAvancado) { %>
<script src='https://www.google.com/recaptcha/api.js'></script>
<script type="text/JavaScript">
setInterval(function () {
    $("iframe[title*='recaptcha' i]").parent().parent().addClass('recaptcha_challenge');
}, 1000);
</script>
<% } %>

  <!-- Keyboard -->
  <% if (exibeVk) { %>       
     <script src="../js/keyboard/jquery.keyboard.js?<hl:message key="release.tag"/>"></script>
     <script src="../js/keyboard/keyboard.js?<hl:message key="release.tag"/>"></script>
     <link href="../css/keyboard/keyboard.css" rel="stylesheet">
  <% } %>

  <style id="antiClickjack">body{display:none !important;}</style>
  <script type="text/JavaScript" src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">
  f0 = document.forms[0];  
  var focusSenha = true;

  if (self === top) {
      var antiClickjack = document.getElementById("antiClickjack");
      antiClickjack.parentNode.removeChild(antiClickjack);
  } else {
      top.location = self.location;
  }
  
  function formLoad() {          
    $("div.form-group input#senha").focus();
    setTimeout('location.reload(true)', 19*60*1000);
    var randomNumber = Math.floor(Math.random() * 1000);
    
    <%if (exibeCaptchaDeficiente) {%>
    montaCaptchaSom();
     <%}%>
          
  }

  function ValidaCampos(plainPasswordField, cryptedPasswordField) {
    if (!CriptografaSenha(plainPasswordField, cryptedPasswordField, true, null)) {
      return false;
    }
            
    if ((f0.captcha != undefined) && (f0.captcha.value == "")) {
      alert('<hl:message key="mensagem.informe.login.captcha"/>');        
      return false;
    }

    // Habilita campos desabilitados
    enableAll();
    return true;
  }

  function cleanFields() {
      if (f0.username && f0.username.type == "text") {
        f0.username.type = "hidden";
      }
      if (f0.senha && f0.senha.type == "password") {
        f0.senha.type = "hidden";
      }
      f0.submit();
  }

  window.onload = formLoad;
  </script>
</c:set>

<t:empty_v4>    
    <jsp:attribute name="loginServidor">true</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:attribute name="section">${secao}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>

