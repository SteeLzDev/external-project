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
boolean redirectAutoDesbloqueio = !TextHelper.isNull(request.getAttribute("redirectAutoDesbloqueio")) ? (Boolean) request.getAttribute("redirectAutoDesbloqueio") : false;

int quantidadeDeItensMaisOpcoes = (Integer) request.getAttribute("quantidadeDeItensMaisOpcoes");

String estOrgCodigo = (String) request.getAttribute("estOrgCodigo");
String estOrgNome = (String) request.getAttribute("estOrgNome");
String campoLabel = (String) request.getAttribute("campoLabel");
String campoValor = (String) request.getAttribute("campoValor");
List<TransferObject> entidades = (List<TransferObject>) request.getAttribute("entidades");

// Parâmetros para definir URL para instalação do aplicativo nas lojas
String urlAppGoogleStore = (String) request.getAttribute("urlAppGoogleStore");
String urlAppAppleStore = (String) request.getAttribute("urlAppAppleStore");

boolean loginComCfp = (boolean) request.getAttribute("loginComCfp");
boolean omiteEstOrgLogin = (boolean) request.getAttribute("omiteEstOrgLogin");

String rotuloCampoUsername = "";
String placeholderUsername = "";

if (!loginComCfp) {
    rotuloCampoUsername = ApplicationResourcesHelper.getMessage("rotulo.login.servidor.singular", responsavel);
    placeholderUsername = ApplicationResourcesHelper.getMessage("mensagem.informacao.login.digite.matricula", responsavel);
} else {
    rotuloCampoUsername = ApplicationResourcesHelper.getMessage("rotulo.cpf", responsavel);
    placeholderUsername = ApplicationResourcesHelper.getMessage("mensagem.informacao.login.digite.cpf", responsavel);
}

String nomeCse = LoginHelper.getCseNome(responsavel);
String nomeSistema = JspHelper.getNomeSistema(responsavel);
boolean exibeCaptcha = (Boolean) request.getAttribute("exibeCaptcha");
boolean exibeCaptchaAvancado = (Boolean) request.getAttribute("exibeCaptchaAvancado");
boolean exibeCaptchaDeficiente = (Boolean) request.getAttribute("exibeCaptchaDeficiente");
boolean exibeVk = (Boolean) request.getAttribute("exibeVk");
String ajudaCampoCaptcha = ApplicationResourcesHelper.getMessage("ajuda.campo.captcha", responsavel);

// parâmetro para definir se o login é realizado em duas etapas
boolean validacaoSeguranca = ParamSist.paramEquals(CodedValues.TPC_VALIDACAO_SEGURANCA_TELA_LOGIN_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
String mascaraNomeLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_NOME_LOGIN, responsavel);
String mensagemMascaraNomeLogin = LoginHelper.getMensagemMascaraNomeLogin(responsavel);
String telaLogin = LoginHelper.getPaginaLoginServidor();

String telaValidacao = (String) request.getAttribute("telaValidacao");
String usuLogin = (String) request.getAttribute("usuLogin");
String usuNome = (String) request.getAttribute("usuNome");
String loginValido = (String) request.getAttribute("loginValido");
String loginDefVisual = (String) request.getAttribute("loginDefVisual");
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="bodyContent">

    <form name="enviar" 
          id="formLogin" 
          method="post" 
          action="../v3/autenticar?acao=autenticar"            
          autocomplete="off"
    >                
      <% if (!loginComCfp && !omiteEstOrgLogin) { %>
      <div class="form-group ">
          <label for="codigo_orgao">
            <hl:message key="rotulo.orgao.singular"/>
          </label>
          <%=JspHelper.geraCombo(entidades, "codigo_orgao", campoValor, campoLabel, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel),"class=\"form-control form-select\"", true, 1, (!TextHelper.isNull(estOrgCodigo) ? estOrgCodigo: null)) %>
      </div>     
      <% } %> 
      <div class="form-group">      
        <label for="username">
          <%=TextHelper.forHtmlContent(rotuloCampoUsername) %>
        </label>      
        <input name="username" 
               id="username" 
               type="text"                
               class="form-control" 
               placeholder='<%=TextHelper.forHtmlAttribute(placeholderUsername)%>'                              
               onFocus="SetarEventoMascaraV4(this,'<%=TextHelper.forJavaScript(LoginHelper.getMascaraLoginServidor())%>',true);return(FocusNome());" 
               onBlur="fout(this);ValidaMascaraV4(this);"              
        />                                               
        <hl:message key="mensagem.pagina.login.ser.matricula"/>                
      </div>

      <% if (!validacaoSeguranca || (!TextHelper.isNull(telaValidacao) && telaValidacao.equals("2"))) { %>
      <div class="form-group">      
        <label for="senha"><hl:message key="rotulo.usuario.senha"/></label>      
        <hl:htmlpassword classe="form-control" 
                di="senha" 
                name="senha" 
                placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.login.digite.senha.acesso", responsavel)%>' 
                cryptedfield="senhaRSA" 
                isSenhaServidor="true" 
                nf="<%=TextHelper.forHtmlAttribute( exibeCaptcha ? "captcha" : exibeCaptchaAvancado ? "recaptcha-checkbox-checkmark" : "btnOK" )%>"
                />
      </div>
      <% } %>

      <% if (!validacaoSeguranca || (!TextHelper.isNull(telaValidacao) && telaValidacao.equals("2"))) { %>
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
      <% } %>

      <div class="row">
        <div class="col-sm">
        <% if ((!TextHelper.isNull(telaValidacao) && telaValidacao.equals("1")) || 
               (!exibeCaptcha && !exibeCaptchaAvancado && !exibeCaptchaDeficiente)) { %>
          <button id="btnOK"
                  name="btnOK"
                  class="btn btn-primary" 
                  form="formLogin" 
                  formaction="../v3/autenticar?acao=autenticar"
                  type="submit" 
                  onclick="if(ValidaCampos(f0.senha, f0.senhaRSA)){cleanFields();} return false;">
            <hl:message key="rotulo.botao.entrar" />
          </button>
        <% } else if (!validacaoSeguranca || (!TextHelper.isNull(telaValidacao) && telaValidacao.equals("2"))) { %>
          <% if (exibeCaptcha) { %>
          <button id="btnOK"
                  name="btnOK"
                  class="btn btn-primary" 
                  form="formLogin" 
                  formaction="../v3/autenticar?acao=autenticar"
                  type="submit" 
                  onclick="if(ValidaCampos(f0.senha, f0.senhaRSA)){cleanFields();} return false;">
            <svg width="17"><use xlink:href="#i-avancar"></use></svg>
            <hl:message key="rotulo.botao.proxima" />
          </button>
          <%
             } else if (exibeCaptchaAvancado) {
          %>
          <button id="btnOK"
                  name="btnOK"
                  class="btn btn-primary" 
                  form="formLogin" 
                  formaction="../v3/autenticar?acao=autenticar"
                  type="submit" 
                  onclick="if(ValidaLogin(f0.senha, f0.senhaRSA)){cleanFields();} return false;">
            <svg width="17"><use xlink:href="#i-avancar"></use></svg>
            <hl:message key="rotulo.botao.proxima" />
          </button>
          <% } else if (exibeCaptchaDeficiente && (!validacaoSeguranca || telaValidacao.equals("2"))) { %>
          <button id="btnOK"
                  name="btnOK"
                  class="btn btn-primary" 
                  form="formLogin" 
                  formaction="../v3/autenticar?acao=autenticar"
                  type="submit" 
                  onclick="if(ValidaCampos(f0.senha, f0.senhaRSA)){cleanFields();} return false;">
            <svg width="17"><use xlink:href="#i-avancar"></use></svg>
            <hl:message key="rotulo.botao.proxima" />
          </button>
          <% } %>
        <% } else { %>
          <button id="btnOK"
                  name="btnOK"
                  class="btn btn-primary" 
                  form="formLogin" 
                  formaction="../v3/autenticar?acao=autenticar"
                  type="submit" 
                  onclick="if(ValidaCampos()){cleanFields();} return false;">
            <svg width="17"><use xlink:href="#i-avancar"></use></svg>
            <hl:message key="rotulo.botao.proxima" />
          </button>
        <% } %>
          <%out.print(SynchronizerToken.generateHtmlToken(request));%>       
        </div>
      </div>
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

  <style id="antiClickjack">body{display:none !important;}</style>
  <script type="text/JavaScript">

  f0 = document.forms[0];

  if (self === top) {
      var antiClickjack = document.getElementById("antiClickjack");
      antiClickjack.parentNode.removeChild(antiClickjack);
  } else {
      top.location = self.location;
  }
  
  function formLoad() {      
    $("#codigo_orgao").focus();
    setTimeout('location.reload(true)', 19*60*1000);

    $("#SimLinkAutoDesbloqueio").attr("href", "../v3/autoDesbloquearServidor?acao=iniciarServidor");
    <% if (redirectAutoDesbloqueio) { %>
        $("#dialogAutoDesbloqueio").modal("show");  
    <%} else {%>
        $("#dialogAutoDesbloqueio").modal("hide");
    <%} %>
  }

  function ValidaCampos(plainPasswordField, cryptedPasswordField) 
  {
    var Nome, Orgao;
    <% if (!loginComCfp && !omiteEstOrgLogin) { %>
    Orgao = f0.codigo_orgao.value;
    <% } %>
    Nome  = f0.username.value;

    if ((Nome == "") || (Nome == null)) {
      <% if (!loginComCfp) { %>
      alert('<hl:message key="mensagem.informe.matricula"/>');
      <% } else { %>
      alert('<hl:message key="mensagem.informe.cpf"/>');
      <% } %>
      f0.username.focus();
      focusSenha = false;
      return false;
    }
   
    <%if (!validacaoSeguranca || (!TextHelper.isNull(telaValidacao) && telaValidacao.equals("2"))) { %>
      <%if (exibeVk) { %>
        if ((plainPasswordField.value == "") || (plainPasswordField.value == null)) {
          alert('<hl:message key="mensagem.informe.login.senha"/>');
          return false; 
        } 
      <%} %>
  
      if (!CriptografaSenha(plainPasswordField, cryptedPasswordField, true, null)) {
        return false;
      }
	<%} %>
  
    <% if (!loginComCfp && !omiteEstOrgLogin) { %>
    if ((Orgao == "") || (Orgao == null)) {
      alert('<hl:message key="mensagem.informe.estabelecimento"/>');
      f0.codigo_orgao.focus();
      return false;
    }
    if ((f0.captcha != undefined) && (f0.captcha.value == "")) {
      alert('<hl:message key="mensagem.informe.login.captcha"/>');
      f0.captcha.focus();
      return false;
    }
    <% } %>
    return true;
  }

  function cleanFields() 
  {
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

