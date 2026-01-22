<%@ tag description="Overall Page template" pageEncoding="UTF-8" %>
<%@ tag import="java.util.List" %>
<%@ tag import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ tag import="com.zetra.econsig.helper.usuario.LoginHelper" %>
<%@ tag import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ tag import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ tag import="com.zetra.econsig.values.CodedValues" %>
<%@ tag import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ tag import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ tag import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ tag import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ tag import="com.zetra.econsig.web.servlet.ViewImageServlet"%>
<%@ tag import="com.zetra.econsig.web.controller.ajuda.ChatbotRestController" %>
<%@ attribute name="javascript" fragment="true" %>
<%@ attribute name="section" fragment="true"%>
<%@ attribute name="loginServidor" %>
<%@ attribute name="loginUsuario" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/function-lib" prefix="fl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);


String logoSistema;

if ("v5".equals(versaoLeiaute)) {
  logoSistema = "../img/logo_sistema_v5.png";
} else if ("v6".equals(versaoLeiaute)) {
  logoSistema = "../img/logo_sistema_v6.svg";
} else {
  logoSistema = "../img/logo_sistema.png";
}

String mensagemTelaLoginServidor = (request.getAttribute("mensagemTelaLoginServidor") != null ? (String) request.getAttribute("mensagemTelaLoginServidor") : "" );
String mensagemTelaLoginUsuario = (request.getAttribute("mensagemTelaLoginUsuario") != null ? (String) request.getAttribute("mensagemTelaLoginUsuario") : "" );

// Habilita aviso de segurança no navegador quando algum conteúdo carregado não for aceito pelas regras abaixo

boolean exibeImgMkt = !TextHelper.isNull(ViewImageServlet.getImage("login/img-marketing.png", responsavel));
boolean exibeImgMktMobile = !TextHelper.isNull(ViewImageServlet.getImage("login/img-marketing-mobile.png", responsavel));

// Se o chatbot está habilitado para o papel do usuário, exibir o elemento na página
boolean chatbotHabilitado = !TextHelper.isNull(JspHelper.getIdAgenteChatbot(responsavel, session));

String urlAppGoogleStore = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_APLICATIVO_SER_GOOGLE_STORE, responsavel);
String urlAppAppleStore =  (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_APLICATIVO_SER_APPLE_STORE, responsavel);

%>
<!DOCTYPE html>
<html lang="<%=LocaleHelper.getLocale()%>">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title><%= (!TextHelper.isNull(request.getAttribute("tituloPagina"))) ? request.getAttribute("tituloPagina") : TextHelper.forHtmlContent(JspHelper.getNomeSistema(responsavel))%></title>
    <%-- Bootstrap core CSS --%>
    <link href="<c:url value='/node_modules/bootstrap/dist/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css">
    <%-- Font Monteserrat (titulos) e Roboto (corpo) CSS --%>
    <link href="<c:url value='/css/fonts/Google/family_Montserrat.css'/>" rel="stylesheet" type="text/css">
    <%-- Arquivos Especializads CSS --%>
      <% if ("v5".equals(versaoLeiaute)) { %>
      <link href="<c:url value='/css/welcome_v5.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/custom_v5.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/custom_v5_client.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/econsig_v5.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/chatbot_v5.css'/>" rel="stylesheet" type="text/css" />
      <% } else if ("v6".equals(versaoLeiaute)) { %>
      <link href="<c:url value='/css/welcome_v6.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/custom_v6.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/custom_v6_client.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/econsig_v6.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/chatbot_v6.css'/>" rel="stylesheet" type="text/css" />
      <% } else { %>
      <link href="<c:url value='/css/econsig.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/welcome_v4.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/custom_v4.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/custom_v4_client.css'/>" rel="stylesheet" type="text/css">
      <link href="<c:url value='/css/chatbot.css'/>" rel="stylesheet" type="text/css" />
      <% } %>
    <link href="<c:url value='/css/botui.min.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/css/botui-theme-default.css'/>" rel="stylesheet" type="text/css" />
  </head>
<body class="page-login">
<% if (exibeImgMkt) { %>
<div class="img-market">
  <img src="../img/view.jsp?nome=login/img-marketing.png">
</div>
<% } %>
<section id="no-back" class="container-flui">
<% if (exibeImgMkt) { %>
  <div class="content">
<% } else { %>
  <div class="content">
<% } %>
    <div class="login">
        <div class="login-logo">
            <img src="../img/view.jsp?nome=login/logo_cse.gif" alt="<hl:message key="rotulo.texto.alternativo.logo.sistema"/>">
            <% if (exibeImgMktMobile && !TextHelper.isNull(loginServidor) && loginServidor.equals("true")) { %>
            <img src="../img/view.jsp?nome=login/img-marketing-mobile.png" class="img-market-mobile mt-3">
            <% } %>
        </div>
        <% if ("v6".equals(versaoLeiaute)) { %>
            <span class="econsigv6"><%= TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel)) %></span>
        <% } else { %>
            <span class="econsig"><%= TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel)) %></span>
        <% } %>
        <% if (!TextHelper.isNull(mensagemTelaLoginServidor) && !TextHelper.isNull(loginServidor) && loginServidor.equals("true")) { %>
          <div class="alert alert-warning alert-primeiro-acesso p-3 mb-4">
            <%= mensagemTelaLoginServidor %>        
          </div>
        <% } else if (!TextHelper.isNull(mensagemTelaLoginUsuario) && !TextHelper.isNull(loginUsuario) && loginUsuario.equals("true")) { %>
          <div class="alert alert-warning alert-primeiro-acesso p-3 mb-4">
            <%= mensagemTelaLoginUsuario %>        
          </div>
        <% } %>
        <h2 class="login-title"><%= TextHelper.forHtmlContent(JspHelper.getNomeSistema(responsavel)) %></h2>
        <%=JspHelper.msgSession(session, false)%>
        <jsp:doBody/>
    </div>
    <% if ("v6".equals(versaoLeiaute)) { %> 
        <div class="logos-loja">
            <a class="login-loja-googleplay" href="<%=urlAppGoogleStore%>" target="_blank"></a>
            <a class="login-loja-applestore" href="<%=urlAppAppleStore%>" target="_blank"></a>
        </div>
    <% } %>
    
    <%-- Modal autodesbloqueio --%>     
      <div class="modal fade" id="dialogAutoDesbloqueio" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" >          
        <div class="modal-dialog" role="document">
         <div class="modal-content">
           <div class="modal-header pb-0">
          <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.usuarioBloqueado" />
          </span>
          <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
            <hl:message key="rotulo.auto.desbloqueio.modal.body" />
        </div>
              
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>"
              title="<hl:message key="rotulo.botao.cancelar"/>">
              <hl:message key="rotulo.botao.cancelar" />
            </a>
            <a class="btn btn-primary" id="SimLinkAutoDesbloqueio" href="../v3/autoDesbloquearUsuario?acao=iniciarUsuario&_skip_history_=true" alt="<hl:message key="rotulo.sim"/>" title="<hl:message key="rotulo.sim"/>">
              <hl:message key="rotulo.sim" />
            </a>
          </div>
        </div>
              
        </div>
       </div>
    </div>
    
    <% if (chatbotHabilitado) { %>
      <div id="chatbot_wrapper">
          <div id="chatbot_fechado" class="chatbot_fechado">
              <a onclick="openChat()" class="chatbot_link">
                  <img alt="#{rotulo.alt.icone.balao}" src="../img/chatbot/icone-balao-chatbot.png" />
                  <span class="chatbot_titulo"><hl:message key="mensagem.atendimento.chatbot.titulo"/></span>
              </a>
          </div>
          <div id="chatbot_aberto" class="chatbot_aberto hide">
            <div class="chatbot_header_home">
                <a onclick="closeChat()" class="chatbot_header_close">
                    <img alt='<hl:message key="rotulo.atendimento.texto.alt.fechar"/>' src="../img/chatbot/seta-fechar-chatbot.png" />
                </a>
                <span class="chatbot_bem_vindo"><hl:message key="mensagem.atendimento.chatbot.bem.vindo"/></span>
            </div>
            <div class="chatbot_body pt-0">
                <p class="mb-1"><hl:message key="mensagem.atendimento.chatbot.apresentacao"/></p>
                <div class="form-group col-sm mb-1">
                  <label for="chatUserName"><hl:message key="rotulo.usuario.nome" /></label>
                  <input type="text"
                         class="form-control" 
                         id="chatUserName" 
                         value="<%= !TextHelper.isNull(responsavel.getUsuNome()) ? TextHelper.forHtmlAttribute(responsavel.getUsuNome()) : "" %>" 
                         <%= !TextHelper.isNull(responsavel.getUsuNome()) ? "disabled" : "" %> 
                         aria-label='<hl:message key="mensagem.atendimento.placeholder.nome"/>' 
                         placeholder="<hl:message key="mensagem.atendimento.placeholder.nome"/>"
                  >
                </div>
                <div class="btn-action text-center">
                  <a class="btn btn-primary" href="#no-back" onclick="validateUserData()"><hl:message key="rotulo.botao.atendimento.iniciar"/></a>
                </div>
              </div>
          </div>
          <div id="chatbot_aberto_conversa" class="chatbot_aberto hide">
              <div class="chatbot_header">
                  <img alt='<hl:message key="rotulo.atendimento.texto.alt.foto.atendente"/>' src="../img/chatbot/atendente-foto-chatbot.png" class="atendente" />
                  <a onclick="closeChat()" class="chatbot_header_close">
                      <img alt='<hl:message key="rotulo.atendimento.texto.alt.fechar"/>' src="../img/chatbot/seta-fechar-chatbot.png" />
                  </a>
                  <img alt='<hl:message key="rotulo.atendimento.texto.alt.logo"/>' src="<%=logoSistema%>" class="chatbot_logo_header" />
              </div>
              <div class="chatbot_chat">
                  <div id="my-botui-app">
                    <bot-ui></bot-ui>
                  </div>
              </div>
              <div class="chatbot_enviar">
                  <input type="text" id="chatMessage" aria-label='<hl:message key="mensagem.atendimento.placeholder.mensagem"/>' placeholder='<hl:message key="mensagem.atendimento.placeholder.mensagem"/>'>
                  <a onclick="sendMessage()"><img alt='<hl:message key="rotulo.atendimento.texto.alt.enviar"/>' src="../img/chatbot/seta-enviar-chatbot.png" /></a>
              </div>
          </div>
          <div id="chatbot_tela_mensagem" class="chatbot_aberto hide">
              <div class="chatbot_header">
                  <img alt='<hl:message key="rotulo.atendimento.texto.alt.foto.atendente"/>' src="../img/chatbot/atendente-foto-chatbot.png" class="atendente" />
                  <a onclick="closeChat()" class="chatbot_header_close">
                      <img alt='<hl:message key="rotulo.atendimento.texto.alt.fechar"/>' src="../img/chatbot/seta-fechar-chatbot.png" />
                  </a>
                  <img alt='<hl:message key="rotulo.atendimento.texto.alt.logo"/>' src="<%=logoSistema%>" class="chatbot_logo_header" />
              </div>
              <div id="chatbot_especialista" class="chatbot_alerta hide">
                  <img alt='<hl:message key="rotulo.atendimento.texto.alt.balao.erro"/>' src="../img/chatbot/balao-erro-chatbot.png" />
                  <p><hl:message key="mensagem.atendimento.chatbot.sem.treinamento"/></p>
                  <a onclick="openHumanChat()" class="chatbot_btn"><hl:message key="rotulo.botao.atendimento.redirecionar"/></a>
              </div>
              <div id="chatbot_agradecimento" class="chatbot_alerta hide">
                  <img alt='<hl:message key="rotulo.atendimento.texto.alt.balao.sucesso"/>' src="../img/chatbot/balao-sucesso-chatbot.png" />
                  <p class="sucesso"><hl:message key="mensagem.atendimento.agradecimento"/></p>
                  <a onclick="closeChat()" class="chatbot_btn"><hl:message key="rotulo.botao.atendimento.encerrar"/></a>
              </div>
              <div id="chatbot_fora_horario" class="chatbot_alerta hide">
                  <img alt='<hl:message key="rotulo.atendimento.texto.alt.balao.erro"/>' src="../img/chatbot/balao-erro-chatbot.png" />
                  <p><span id="chatbot_mensagem_retorno"></span></p>
                  <a onclick="closeChat()" class="chatbot_btn"><hl:message key="rotulo.botao.fechar"/></a>
              </div>
          </div>
      </div>
    <% } %>

    <jsp:invoke fragment="section"/>
  </div> 
</section>
<script src="<c:url value='/node_modules/jquery/dist/jquery.min.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/node_modules/js-cookie/dist/js.cookie.min.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/jquery-impromptu.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/jquery-nostrum.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/intl/numeral.min.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/intl/languages.min.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/mensagens.jsp'/>?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>"></script>
<script src="<c:url value='/js/SimpleAjaxUploader.min.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/UploadAnexo.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/sidebar.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/countdown.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/validalogin.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/scripts_2810.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/validacoes.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/validaform.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/validaemail.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/xbdhtml.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/econsig.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/jscrollpane.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/js/validaMascara_v4.js'/>?<hl:message key='release.tag'/>"></script>
<script src="<c:url value='/node_modules/mobile-detect/mobile-detect.min.js'/>?<hl:message key='release.tag'/>"></script>
<script type="text/javascript">
//<![CDATA[
navigator.sayswho= (function(){
    var ua= navigator.userAgent, tem, 
    M= ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*([\d\.]+)/i) || [];
    if(/trident/i.test(M[1])){
        tem=  /\brv[ :]+(\d+(\.\d+)?)/g.exec(ua) || [];
        return 'IE '+(tem[1] || '');
    }
    M= M[2]? [M[1], M[2]]:[navigator.appName, navigator.appVersion, '-?'];
    if((tem= ua.match(/version\/([\.\d]+)/i))!= null) M[2]= tem[1];
    return M[0];
})();

window.history.forward();
if (navigator.sayswho.toLowerCase() == "msie") {
  window.location.hash="no-back";
  window.onhashchange = function(){ window.location.hash = "no-back"; }
} else {
  window.history.pushState({}, "", "#no-back");
  window.onhashchange = function(){ window.location.hash = "no-back"; }
}

numeral.language(locale());

if ("function" == typeof startCountdown) {
    startCountdown(<%=session.getMaxInactiveInterval()%>);
    continueCountdown(<%=responsavel.isSessaoValida()%>);
}
//]]>
</script>
<% if (chatbotHabilitado) { %>
  <script src="<c:url value='/node_modules/vue/dist/vue.global.prod.js'/>"></script>
  <script src="<c:url value='/js/botui.min.js'/>"></script>
  <script>
      var botui = new BotUI('my-botui-app');
      var chatStarted = <%= session.getAttribute(ChatbotRestController.CHATBOT_SESSION_ID) != null %>;

      function openChat() {
          $('#chatbot_fechado').hide();
          if (!chatStarted) {
              $('#chatbot_aberto').show();
              $('#chatUserName').focus();
          } else {
              var username = $('#chatUserName').val();
              startChat(username);
          }
      }
      function closeChat() {
          $('#chatbot_aberto').hide();
          $('#chatbot_aberto_conversa').hide();
          $('#chatbot_tela_mensagem').hide();
          $('#chatbot_fechado').show();
          botui.message.removeAll();
      }
      function validateUserData() {
          var username = $('#chatUserName').val();
          if (!username) {
              $('#chatUserName').css({ "border": "1px solid #ff0000" });
              alert('<hl:message key="mensagem.atendimento.digite.nome.valido"/>');
              $('#chatUserName').focus();
          } else {
              startChat(username);
          }
      }
      function startChat(username) {
          $('#chatbot_aberto').hide();
          $('#chatbot_aberto_conversa').show();
          startSession(username);
      }
      function startSession(username) {
          $.ajax({
              type: 'post',
              url: "../v3/iniciarChatbot",
              data: {
                  nome: username,
                  sessionId: Cookies.get("chatbot-session-id")
              }
          }).done(function (data) {
        	  Cookies.set("chatbot-session-id", data.sessionId, { expires: 30, path: "/" });
              if (data.mensagens) {
                  for (i = 0; i < data.mensagens.length; i++) {
                      botui.message.add({
                          content: data.mensagens[i].texto,
                          human: !data.mensagens[i].bot
                      });
                  }
              }

              if (!chatStarted) {
                  chatStarted = true;
                  botui.message.bot({
                      delay: 500,
                      loading: true,
                      content: '<hl:message key="mensagem.atendimento.pergunta.ajuda"/>',
                  }).then(function () {
                      scrollBottom();
                      $('#chatMessage').prop('disabled', false);
                      $('#chatMessage').focus();
                  });
              }
          });
      }
      function sendMessage() {
          var message = $('#chatMessage').val();
          if (!message) return;

          $('#chatMessage').val('');
          $('#chatMessage').prop('disabled', true);

          botui.message.add({
              human: true,
              content: message
          }).then(function () {
              scrollBottom();
              botui.message.add({
                  loading: true
              }).then(function (index) {
                  scrollBottom();
                  $.ajax({
                      type: 'post',
                      url: "../v3/enviarMensagemChatbot",
                      data: {
                          mensagem: message
                      }
                  }).done(function (data) {
                      botui.message.update(index, {
                          loading: false,
                          content: data.message
                      }).then(function () {
                          scrollBottom();
                          if (data.result == "true") {
                              $('#chatMessage').prop('disabled', false);
                              $('#chatMessage').focus();
                          } else {
                              $('#chatbot_aberto_conversa').hide();
                              $('#chatbot_tela_mensagem').show();
                              if (data.redirect == "true") {
                                  $('#chatbot_especialista').show();
                              } else {
                                  $('#chatbot_mensagem_retorno').text(data.message);
                                  $('#chatbot_fora_horario').show();
                              }
                          }
                      });
                  });
              });
          });
      }
      function openHumanChat() {
          $('#chatbot_especialista').hide();
          $('#chatbot_agradecimento').show();
          window.open('../v3/abrirChatSuporte', 'chat', 'height=650,width=1000,status=no,toolbar=no,menubar=no,location=no,scrollbars=yes,left=100,top=100');
      }
      function scrollBottom() {
          $("div.botui-message").last().attr("tabindex", -1).focus();
      }

      $('#chatMessage').on('keypress', function (e) {
          if (e.which === 13) {
              sendMessage()
          }
      });
  </script>        
<% } %>
<jsp:invoke fragment="javascript"/>
<%-- Placed at the end of the document so the pages load faster --%>
<script src="<c:url value='/node_modules/@popperjs/core/dist/umd/popper.min.js'/>"></script>
<script src="<c:url value='/node_modules/bootstrap/dist/js/bootstrap.min.js'/>"></script>
<%-- Menu de acessibilidade para eMAG 3.1 - Criterio de Sucesso 2.2 --%>
<noscript><hl:message key="mensagem.navegador.sem.javascript"/></noscript>
</body>
</html>