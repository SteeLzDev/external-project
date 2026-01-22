<%@ tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@ tag import="java.io.UnsupportedEncodingException"%>
<%@ tag import="java.net.URLEncoder"%>
<%@ tag import="java.nio.charset.StandardCharsets"%>
<%@ tag import="java.util.Iterator"%>
<%@ tag import="java.util.List"%>
<%@ tag import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ tag import="com.zetra.econsig.helper.seguranca.AcessoRecursoHelper.AcessoRecurso" %>
<%@ tag import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ tag import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ tag import="com.zetra.econsig.values.CodedValues"%>
<%@ tag import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ tag import="com.zetra.econsig.values.ItemMenuEnum"%>
<%@ tag import="com.zetra.econsig.values.MetodoSenhaExternaEnum" %>
<%@ tag import="com.zetra.econsig.values.ParamSenhaExternaEnum" %>
<%@ tag import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ tag import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ tag import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ tag import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ tag import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ tag import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@ tag import="com.zetra.econsig.dto.entidade.MargemTO" %>
<%@ tag import="com.zetra.econsig.dto.entidade.ParamSvcTO" %>
<%@ tag import="com.zetra.econsig.web.controller.ajuda.ChatbotRestController" %>
<%@ tag import="com.zetra.econsig.helper.web.ParamSession" %>
<%@ attribute name="menu" fragment="true"%>
<%@ attribute name="header" fragment="true"%>
<%@ attribute name="imageHeader" fragment="true"%>
<%@ attribute name="javascript" fragment="true"%>
<%@ attribute name="style" fragment="true"%>
<%@ attribute name="pageModals" fragment="true"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/function-lib" prefix="fl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
ParamSession paramSession = ParamSession.getParamSession(session);

String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

String logoSistema;

if ("v5".equals(versaoLeiaute)) {
  logoSistema = "../img/logo_sistema_v5.png";
} else if ("v6".equals(versaoLeiaute)) {
  logoSistema = "../img/logo_sistema_v6.svg";
} else {
  logoSistema = "../img/logo_sistema.png";
}

AcessoRecurso acesso = (AcessoRecurso) session.getAttribute("acesso_recurso");

final String msgnExpiraSenha = session.getAttribute(CodedValues.MSG_EXPIRACAO_SENHA) != null ? (String) session.getAttribute(CodedValues.MSG_EXPIRACAO_SENHA) : null;

String urlCentral = (String) (session.getAttribute("urlCentralizador") != null ? session.getAttribute("urlCentralizador") : "");
String urlCentralAcesso = (String) (session.getAttribute("urlCentralizadorAcesso") != null ? session.getAttribute("urlCentralizadorAcesso") : "");

// Habilita aviso de segurança no navegador quando algum conteúdo carregado não for aceito pelas regras abaixo

// Tutorial de primeiro acesso
List<String> imgsTutorial = (List<String>) session.getAttribute("tutorialList");
boolean exibirTutorialAoAbrir = (session.getAttribute("tutorialPrimeiroAcesso") != null);
session.removeAttribute("tutorialPrimeiroAcesso");

// Verifica se exibe passo a passo
boolean exibePassoAPasso = ParamSist.getBoolParamSist(CodedValues.TPC_PASSO_A_PASSO_OPERACOES_ECONSIG, responsavel);

//mostra link chat (parametros 633(cseOrg) ou/e 634 (csaCor) habilitado ou/e 635(ser) habilitado(s)
boolean mostraChat = false;
if ( (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_CSE_ORG, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) && responsavel.isCseSupOrg())
    || (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_CSA_COR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) && responsavel.isCsaCor())
    || ((ParamSist.paramEquals(CodedValues.TPC_HABILITA_CHAT_SER, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) && responsavel.isSer())
   ) {
  mostraChat = true;
}

boolean scriptOculta = false;
boolean exigeCaptcha = false;
boolean exibeCaptchaDeficiente = false;
// Verifica se deve exibir a margem do servidor no portal
boolean exibeMargem = false;
    if(responsavel.isSer()
            && ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS, responsavel)
            && ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_MARGEM_SERVIDOR_TELA_PRINCIPAL, responsavel)) {
        scriptOculta = true;
        exibeMargem = session.getAttribute("exibeMargemTopo") != null && (boolean) session.getAttribute("exibeMargemTopo");
        exigeCaptcha = session.getAttribute("exigeCaptchaTopo") != null && (boolean) session.getAttribute("exigeCaptchaTopo");
        exibeCaptchaDeficiente = session.getAttribute("exibeCaptchaDeficiente") != null && (boolean) session.getAttribute("exibeCaptchaDeficiente");
        session.setAttribute("validado", "N");
    }

// Se o chatbot está habilitado para o papel do usuário, exibir o elemento na página
boolean chatbotHabilitado = !TextHelper.isNull(JspHelper.getIdAgenteChatbot(responsavel, session));

String urlWhatsApp = "";
if (responsavel.isSer()
        && !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_URL_WHATSAPP, responsavel))
        && !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_NUMERO_WHATSAPP, responsavel))) {
    urlWhatsApp = ParamSist.getInstance().getParam(CodedValues.TPC_URL_WHATSAPP, responsavel).toString() + ParamSist.getInstance().getParam(CodedValues.TPC_NUMERO_WHATSAPP, responsavel).toString();
}

boolean permiteAlterarLogin = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_LOGIN_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
int qtdeRegistroServidor = (session.getAttribute("qtdeRegistroServidor") != null) ? (int) session.getAttribute("qtdeRegistroServidor") : 0;

String oAuth2UriLogout = "";
String oAuth2MsgLogout = "";
if (responsavel.isSer() &&
    ParamSist.paramEquals(CodedValues.TPC_SENHA_EXTERNA, CodedValues.TPC_SIM, responsavel) &&
    MetodoSenhaExternaEnum.OAUTH2.getMetodo().equals(ParamSenhaExternaEnum.METODO.getValor())) {
    oAuth2UriLogout = ParamSenhaExternaEnum.OAUTH2_URI_LOGOUT.getValor();
    oAuth2MsgLogout = ParamSenhaExternaEnum.OAUTH2_MSG_LOGOUT.getValor();

    String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
    String oAuth2UriLogin = urlSistema + (urlSistema.endsWith("/") ? "" : "/") + "servidor";

    if (!TextHelper.isNull(request.getSession().getAttribute(CodedValues.OAUTH2_ID_TOKEN))) {
	    oAuth2UriLogout += "?id_token_hint=" + request.getSession().getAttribute(CodedValues.OAUTH2_ID_TOKEN).toString();
    }

    if (!TextHelper.isNull(oAuth2UriLogin)) {
        try {
           	oAuth2UriLogout += "&post_logout_redirect_uri=" + URLEncoder.encode(oAuth2UriLogin, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
        }
    }
}

boolean exibeCaptchaAvancado = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

boolean reconhecimentoFacialServidor = request.getAttribute("exigeReconhecimentoFacil") != null && request.getAttribute("exigeReconhecimentoFacil").equals("true");
boolean reconhecimentoFacialHabilitado = ParamSist.paramEquals(CodedValues.TPC_EXIGE_RECONHECIMENTO_FACIL_SOLICITACAO_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) || ParamSist.paramEquals(CodedValues.TPC_RECONHECIMENTO_FACIAL_ACESSO_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
boolean simularConsignacaoComReconhecimentoFacialELiveness = request.getAttribute("simularConsignacaoComReconhecimentoFacialELiveness") != null && request.getAttribute("simularConsignacaoComReconhecimentoFacialELiveness").equals("true");
boolean reconhecimentoFacialLivenssGeracaoSenhaAutorizacao = ParamSist.paramEquals(CodedValues.TPC_RECONHECIMENTO_FACIAL_ACESSO_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel);
boolean mostraTermoAdesao = session.getAttribute("listaTermoAdesaoSemFunCodigoExibeServidor") != null;

%>
<c:choose>
  <c:when test="${responsavel.cseSup}">
    <c:set var="nomeCse" value="${responsavel.nomeEntidade}" />
  </c:when>
  <c:when test="${responsavel.org}">
    <c:set var="nomeCse" value="<%=LoginHelper.getCseNome(responsavel)%>" />
    <c:set var="nomeOrg" value="${responsavel.nomeEntidade}" />
  </c:when>
  <c:otherwise>
    <c:set var="nomeCse" value="<%=LoginHelper.getCseNome(responsavel)%>" />
  </c:otherwise>
</c:choose>
<!DOCTYPE html>
<html lang="<%=LocaleHelper.getLocale()%>" xml:lang="<%=LocaleHelper.getLocale()%>">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title><%= (!TextHelper.isNull(request.getAttribute("tituloPagina"))) ? request.getAttribute("tituloPagina") : JspHelper.getNomeSistema(responsavel)%></title>
<%-- Bootstrap core CSS --%>
<link href="<c:url value='/node_modules/bootstrap/dist/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css">
<%-- Font Awesome CSS --%>
<link href="<c:url value='/node_modules/font-awesome/css/font-awesome.min.css'/>" rel="stylesheet" type="text/css">
<%-- Font Monteserrat (titulos) e Roboto (corpo) CSS --%>
<link href="<c:url value='/css/fonts/Google/family_Montserrat.css'/>" rel="stylesheet" type="text/css">
<%-- Arquivos Especializads CSS --%>
    <% if ("v5".equals(versaoLeiaute)) { %>
    <link href="<c:url value='/css/econsig_v5.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v5.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v5_client.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/chatbot_v5.css'/>" rel="stylesheet" type="text/css" />
    <% } else if ("v6".equals(versaoLeiaute)) { %>
    <link href="<c:url value='/css/econsig_v6.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v6.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v6_client.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/chatbot_v6.css'/>" rel="stylesheet" type="text/css" />
    <% } else { %>
    <link href="<c:url value='/css/econsig.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v4.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/custom_v4_client.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/chatbot.css'/>" rel="stylesheet" type="text/css" />
    <% } %>
<link href="<c:url value='/node_modules/driver.js/dist/driver.css'/>" rel="stylesheet" type="text/css">
<link href="<c:url value='/node_modules/jquery-ui/dist/themes/base/jquery-ui.css'/>" rel="stylesheet" type="text/css">
<link href="<c:url value='/node_modules/ekko-lightbox/dist/ekko-lightbox.css'/>" rel="stylesheet" type="text/css">
<link href="<c:url value='/css/botui.min.css'/>" rel="stylesheet" type="text/css" />
<link href="<c:url value='/css/botui-theme-default.css'/>" rel="stylesheet" type="text/css" />
<style>
@media print {    /* for good browsers */
  .no-print, .no-print * {
    display: none !important;
  }
</style>
<jsp:invoke fragment="style" />
</head>
<body>
    <!-- Marca d'água de ambiente de teste -->
    <c:if test="<%=ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, responsavel)%>">
    <div class="watermark-ambiente-testes">
        <hl:message key="rotulo.ambiente.de.testes"/>
    </div>
    </c:if>
<section id="no-back">
    <% if (session.getAttribute("tutorialList") != null) { %>
    <a class="rum_sst_tab rum_sst_contents no-print" href="#no-back" onclick="abrirTutorialPrimeiroAcesso()" title="<%= ApplicationResourcesHelper.getMessage("rotulo.menu.acesso.inicial.title", responsavel) %>">
      <%= ApplicationResourcesHelper.getMessage("rotulo.menu.acesso.inicial", responsavel) %>
    </a>
    <% } %>
    <%-- Menu --%>
    <div class="header-logo">
      <span class="zetra"><hl:message key="rotulo.zetrasoft" /></span>
      <div class="ico-menu">
        <span><hl:message key="rotulo.menu.singular" /></span>
      </div>
    </div>
    <div class="nav-bar">
      <div class="scroll-pane">
        <div class="nav-bar-user">
          <div class="econsig">
            <hl:message key="rotulo.nome.sistema" />
          </div>
          <div class="user-info">
            <span class="user-avatar">${fl:retornaIniciaisNome(responsavel.usuNome, 2)}</span>
            <span class="user-name">${fl:forHtmlContent(responsavel.usuNome)}</span>
            <c:choose>
              <c:when test="${responsavel.cseSup}">
                <span class="user-company">${fl:forHtmlContent(nomeCse)}</span>
              </c:when>
              <c:when test="${responsavel.org}">
                <span class="user-company">${fl:forHtmlContent(nomeOrg)}</span>
              </c:when>
            </c:choose>
            <span class="user-id">${fl:forHtmlContent(responsavel.usuLogin)}</span>
          </div>
          <div class="time">
            <span id="clock" class="tempoExpiracao"></span>
          </div>
          <ul>
            <% if (session.getAttribute(ItemMenuEnum.ALTERAR_SENHA.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.ALTERAR_SENHA.getDescricao())) { %>
            <li><a href="#no-back" onClick="postData('../v3/alterarSenha')" alt="<hl:message key="rotulo.menu.alterar.senha"/>" title="<hl:message key="rotulo.menu.alterar.senha"/>">
                <hl:message key="rotulo.menu.alterar.senha" />
              </a></li>
            <% } %>
            <li><a href="#" alt="<hl:message key="rotulo.mensagem.plural"/>" title="<hl:message key="rotulo.mensagem.plural"/>">
                <hl:message key="rotulo.mensagem.plural" />
              </a></li>
            <% if (session.getAttribute(ItemMenuEnum.SOBRE.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.SOBRE.getDescricao())) { %>
            <li><a href="#no-back" onClick="postData('../v3/visualizarSobre?acao=iniciar')" alt="<hl:message key="rotulo.menu.sobre"/>" title="<hl:message key="rotulo.menu.sobre"/>">
                <hl:message key="rotulo.menu.sobre" />
              </a></li>
            <% } %>
            <% if (session.getAttribute(ItemMenuEnum.TERMO_DE_USO.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.TERMO_DE_USO.getDescricao())) { %>
            <li><a href="#no-back" onClick="postData('../v3/visualizarTermoUso?acao=iniciar')" alt="<hl:message key="rotulo.termo.de.uso"/>" title="<hl:message key="rotulo.termo.de.uso"/>">
                <hl:message key="rotulo.termo.de.uso" />
              </a></li>
            <% } %>
            <% if (session.getAttribute(ItemMenuEnum.TERMO_DE_ADESAO.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.TERMO_DE_ADESAO.getDescricao())) { %>
            <li><a href="#no-back" onClick="postData('../v3/informarTermoAdesao?acao=listar')" alt="<hl:message key="rotulo.termo.adesao"/>" title="<hl:message key="rotulo.termo.adesao"/>">
                <hl:message key="rotulo.termo.adesao" />
              </a></li>
            <% } %>
            <% if (session.getAttribute(ItemMenuEnum.SAIR_DO_SISTEMA.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.SAIR_DO_SISTEMA.getDescricao())) { %>
            <li><a href="#sairModal" data-bs-toggle="modal" alt="<hl:message key="rotulo.menu.sair"/>" title="<hl:message key="rotulo.menu.sair"/>">
                <hl:message key="rotulo.menu.sair" />
              </a></li>
            <% } %>
          </ul>
        </div>
        <hl:sidebarv4 />
      </div>
    </div>
    <%-- Fim menu --%>
    <div class="main">
      <%-- Header --%>
      <header class="main-header">
        <div class="row">
          <div class="col-sm-6 pr-0">
            <div class="page-header">
              <span class="page-header-tab"> <svg width="42"><jsp:invoke fragment="imageHeader" /></svg>
              </span>
              <h2 class="company">${fl:forHtmlContent(nomeCse)}</h2>
              <h1 class="page-title"><jsp:invoke fragment="header" /></h1>
            </div>
          </div>
          <div class="col-sm-6 pr-0">
            <div class="header-nav pr-4">
              <c:if test="<%=exibeMargem%>">
              <div class="dropdown user-menu margem-menu">
                  <%
                  List<MargemTO> margensRse = (List<MargemTO>) session.getAttribute("margensRse");
                  if (margensRse != null && !margensRse.isEmpty()) {
                      Iterator<MargemTO> itMargens = margensRse.iterator();
                      MargemTO margem = null;
                      boolean exibeIcone = true;
                      while (itMargens.hasNext()) {
                          margem = itMargens.next();
                          if (margem.getMrsMargemRest() != null) {
                              String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(margem.getMarTipoVlr() != null ? margem.getMarTipoVlr().toString() : CodedValues.TIPO_VLR_FIXO);
                              String vlrMargem = NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang());
                              if (exibeIcone) {
                                  exibeIcone = false;
                                  %>
                                  <a class="alert-messages nav-margem dropdown-toggle"
                                  href="#" role="button" id="userMargemMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                  <div class="margem mr-4">
                                    <svg class="ml-auto mr-auto">
                                      <use xlink:href="#i-menu-margem"></use>
                                    </svg>
                                    <b id="margemExibida" class="olho-margem-manu"><%=labelTipoVlr + " " + TextHelper.forHtmlContent(vlrMargem)%></b>
                                    <b id="margemOculta" class="olho-margem-manu d-none"><%=labelTipoVlr + " ********"%></b>
                                  </div>
                                  </a>
                                  <a href="#" onclick="ocultaMargem()" id="olhoMargem">
                                    <svg width="25" height="25" class="icon-oculta-margem">
                                      <use xlink:href="#i-eye-regular"></use>
                                    </svg>
                                  </a>
                                  <a href="#" onclick="ocultaMargem()" class="d-none" id="olhoMargemOculto">
                                    <svg width="25" height="25" class="icon-oculta-margem">
                                      <use xlink:href="#i-eye-slash-regular"></use>
                                    </svg>
                                  </a>
                                  <div id="margemDropDown" class="dropdown-menu dropdown-menu-right" aria-labelledby="userMargemMenu">
                                  <%
                              }
                              %>
                              <div class="user-info">
                                  <span><%=TextHelper.forHtmlContent(margem.getMarDescricao())%>:</span>
                                  <b><%=labelTipoVlr + " " + TextHelper.forHtmlContent(vlrMargem)%></b>
                              </div>
                              <%
                              if (itMargens.hasNext()) {
                              %>
                                  <div class="dropdown-divider"></div>
                              <%
                              }
                          }
                      }
                  }
                  %>
                  </div>
              </div>
              </c:if>
                <c:if test="<%=!exibeMargem && exigeCaptcha && responsavel.isSer()%>">
               <hl:modalCaptchaSer type="topo"/>
                    <div class="dropdown user-menu margem-menu">
                    <a class="alert-messages nav-margem dropdown-toggle"
                       href="#" role="button" id="userMargemMenu" onclick="ocultaMargem()" aria-haspopup="true" aria-expanded="false">
                        <div class="margem mr-4">
                            <svg class="ml-auto mr-auto">
                                <use xlink:href="#i-menu-margem"></use>
                            </svg>
                            <b id="margemOculta" class="olho-margem-manu"><%=ApplicationResourcesHelper.getMessage("rotulo.margem.moeda", responsavel) + " ********"%></b>
                        </div>
                    </a>
                    <a href="#" id="olhoMargemOculto" onclick="ocultaMargem()">
                        <svg width="25" height="25" class="icon-oculta-margem">
                            <use xlink:href="#i-eye-slash-regular"></use>
                        </svg>
                    </a>
                </div>
                </c:if>
              <c:set var="qtdeMsgSemLeitura" value="${fl:qtdeMsgSemLeitura(sessionScope.usu_data_cad, responsavel)}" />
              <c:choose>
                <c:when test="${qtdeMsgSemLeitura > 0}">
                  <a class="alert-messages" href="#no-back" onClick="postData('../v3/confirmarMensagem?acao=iniciar')">
                    <svg>
                  <use xlink:href="#i-mensagem"></use></svg>
                    <span class="alert-messages-qtd">${fl:forHtmlContent(qtdeMsgSemLeitura)}</span>
                  </a>
                </c:when>
                <c:otherwise>
                  <a class="alert-messages" href="#no-back">
                    <svg>
                  <use xlink:href="#i-mensagem"></use></svg>
                    <span class="alert-messages-qtd alert-no-messages">${fl:forHtmlContent(qtdeMsgSemLeitura)}</span>
                  </a>
                </c:otherwise>
              </c:choose>
              <div class="econsig">
                <hl:message key="rotulo.nome.sistema" />
              </div>
              <div class="time">
                <span id="clock" class="tempoExpiracao"></span>
              </div>
              <div class="dropdown user-menu">
                <a class="dropdown-toggle" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <span class="user-avatar">${fl:retornaIniciaisNome(responsavel.usuNome, 2)}</span>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                  <div class="user-info">
                    <span class="user-name">${fl:forHtmlContent(responsavel.usuNome)}</span>
                    <c:choose>
                      <c:when test="${responsavel.cseSup}">
                        <span class="user-company">${fl:forHtmlContent(nomeCse)}</span>
                      </c:when>
                      <c:when test="${responsavel.org}">
                        <span class="user-company">${fl:forHtmlContent(nomeOrg)}</span>
                      </c:when>
                    </c:choose>
                    <span class="user-id">${fl:forHtmlContent(responsavel.usuLogin)}</span>
                  </div>
                  <div class="dropdown-divider"></div>
                  <div class="user-info">
                    <span class="user-name">${fl:forHtmlContent(responsavel.papDescricao)}</span>
                    <span class="user-company">${fl:forHtmlContent(responsavel.perDescricao)}</span>
                  </div>
                  <div class="dropdown-divider"></div>
                  <% if (session.getAttribute(ItemMenuEnum.ALTERAR_SENHA.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.ALTERAR_SENHA.getDescricao())) { %>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/alterarSenha')" alt="<hl:message key="rotulo.menu.alterar.senha"/>"
                    title="<hl:message key="rotulo.menu.alterar.senha"/>">
                    <hl:message key="rotulo.menu.alterar.senha" />
                  </a>
                  <div class="dropdown-divider"></div>
                  <% } %>
                  <% if (session.getAttribute(ItemMenuEnum.SOBRE.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.SOBRE.getDescricao())) { %>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarSobre?acao=iniciar')" alt="<hl:message key="rotulo.menu.sobre"/>"
                    title="<hl:message key="rotulo.menu.sobre"/>">
                    <hl:message key="rotulo.menu.sobre" />
                  </a>
                  <% } %>
                  <% if (session.getAttribute(ItemMenuEnum.TERMO_DE_USO.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.TERMO_DE_USO.getDescricao())) { %>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarTermoUso?acao=iniciar')" alt="<hl:message key="rotulo.termo.de.uso"/>"
                    title="<hl:message key="rotulo.termo.de.uso"/>">
                    <hl:message key="rotulo.termo.de.uso" />
                  </a>
                  <% } %>
                  <% if (mostraTermoAdesao && session.getAttribute(ItemMenuEnum.TERMO_DE_ADESAO.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.TERMO_DE_ADESAO.getDescricao())) { %>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/informarTermoAdesao?acao=listar')" alt="<hl:message key="rotulo.termo.adesao"/>"
                     title="<hl:message key="rotulo.termo.adesao"/>">
                    <hl:message key="rotulo.termo.de.adesao.titulo" />
                  </a>
                  <% } %>
                  <%if (permiteAlterarLogin) {
                      if (qtdeRegistroServidor > 1) { %>
                        <div class="dropdown-divider"></div>
                           <a class="dropdown-item" href="#no-back" onClick="postData('../v3/autenticar?acao=iniciarAlteracaoLoginSer')" alt="<hl:message key="rotulo.menu.trocar.registro"/>" title="<hl:message key="rotulo.menu.trocar.registro"/>">
                            <hl:message key="rotulo.menu.trocar.registro"/>
                           </a>
                     <%} %>
                  <%} %>
                  <% if (session.getAttribute(ItemMenuEnum.SAIR_DO_SISTEMA.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.SAIR_DO_SISTEMA.getDescricao())) { %>
                  <div class="dropdown-divider"></div>
                  <a class="dropdown-item" href="#sairModal" data-bs-toggle="modal" alt="<hl:message key="rotulo.menu.sair"/>" title="<hl:message key="rotulo.menu.sair"/>">
                    <hl:message key="rotulo.menu.sair" />
                  </a>
                  <%} %>
                  <%if(!urlCentral.isEmpty()) { %>
                    <jsp:include page="../../jsp/centralizador/includeMenuCentralizador_v4.jsp" />
                  <%} %>
                  <%if(!urlCentralAcesso.isEmpty()) { %>
                    <jsp:include page="../../jsp/centralizador/includeMenuCentralizadorAcesso_v4.jsp" />
                  <%} %>
                </div>
              </div>
            </div>
          </div>
        </div>
      </header>
      <%-- Fim header --%>
      <div class="main-content">
          <% if(!TextHelper.isNull(msgnExpiraSenha)) { %>
          <div class="alert alert-warning" role="alert">
            <span id="msgnAlertSenha"><%=msgnExpiraSenha%></span>
          </div>
          <% } %>
        <%=JspHelper.msgSession(session, false)%>
        <div id="mensagens"></div>
          <!-- botão para exibir passo a passo -->
          <% if (exibePassoAPasso) { %>
             <a id="btnPassoAPasso" class="rum_sst_tab2 rum_sst_contents" href="#no-back" onclick="startIntro()" alt='<hl:message key="rotulo.botao.passo.a.passo" />' title='<hl:message key="rotulo.botao.passo.a.passo" />' style="display: none;">
               <hl:message key="rotulo.botao.passo.a.passo" />
             </a>
          <% } %>
        <jsp:doBody />
        <div id="menuAcessibilidade" class="right access-class d-print-none" tabindex="0">
          <div class="hide" id="opcoesAcessibilidadeV4">
            <header class="menu-title">
              <strong><hl:message key="rotulo.acessibilidade.menu.acessivel"/></strong>
            </header>
            <a tabindex="0" onClick="changeFontSizev4(1)">
              <i class="fa fa-plus" aria-hidden="true"><span class="d-none">.</span></i>
              <span><hl:message key="rotulo.acessibilidade.letra.aumentar"/></span>
            </a>
            <a tabindex="0" onClick="changeFontSizev4(-1)">
              <i class="fa fa-minus" aria-hidden="true"><span class="d-none">.</span></i>
              <span><hl:message key="rotulo.acessibilidade.letra.diminuir"/></span>
            </a>
            <a tabindex="0" onClick="toogleBold()">
              <i class="fa fa-bold" aria-hidden="true"><span class="d-none">.</span></i>
              <span><hl:message key="rotulo.acessibilidade.negrito"/></span>
            </a>
            <a tabindex="0" onClick="toggleContrastv4()">
              <i class="fa fa-adjust" aria-hidden="true"><span class="d-none">.</span></i>
              <span><hl:message key="rotulo.acessibilidade.contraste"/></span>
            </a>
            <a id="opcaoAcessibilidadeMover" tabindex="0" onClick="toggleMenuAcessibilidadeV4Side()">
             <i class="fa fa-long-arrow-left" aria-hidden="true"><span class="d-none">.</span></i>
               <span><hl:message key="rotulo.acessibilidade.mover"/></span>
            </a>
          </div>
          <a class="fill-div" onClick="toggleMenuAcessibilidadeV4()">
            <i class="fa fa-universal-access" aria-hidden="true"><span class="d-none">.</span></i>
          </a>
        </div>
      </div>
    </div>
    <!-- Modal sair do sistema -->
    <div class="modal fade" id="sairModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.encerrar.sessao" />
            </span>
            <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <hl:message key="mensagem.confirmacao.logout" />
          </div>
          <div class="modal-footer pt-0">
            <div class="btn-action mt-2 mb-0">
              <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>"
                title="<hl:message key="rotulo.botao.cancelar"/>">
                <hl:message key="rotulo.botao.cancelar" />
              </a>
              <% if (session.getAttribute(ItemMenuEnum.SAIR_DO_SISTEMA.getDescricao()) != null && (Boolean) session.getAttribute(ItemMenuEnum.SAIR_DO_SISTEMA.getDescricao())) { %>
              <a class="btn btn-primary" href="#no-back" onClick="sairSistema()"
                alt="<hl:message key="rotulo.menu.sair"/>" title="<hl:message key="rotulo.menu.sair"/>">
                <hl:message key="rotulo.botao.sair" />
              </a>
              <% } %>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Modal sair do sistema oauth -->
    <div class="modal fade" id="sairOauthModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.encerrar.sessao" />
            </span>
            <button type="button" class="logout mr-1 d-print-none" data-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <%=!TextHelper.isNull(oAuth2MsgLogout) ? TextHelper.forHtmlContent(oAuth2MsgLogout) : ""%>
          </div>
        </div>
      </div>
    </div>
    <%-- Modal de confirmação de operação sensível --%>
    <div id="dialogAutorizacao" class="dialog-autorizacao-tag" title='<hl:message key="mensagem.confirmacao.operacao.sensivel.titulo.modal"/>' style="display: none;">
      <form>
        <div id="dialogAutorizacaoErro" class="alert alert-danger" role="alert" style="display: none;"></div>
        <div id="dialogAutorizacaoSenha" class="form-group mb-0" style="display: none;">
          <div class="form-check">
            <label for="username">
              <hl:message key="mensagem.confirmacao.operacao.sensivel.usuario" />
            </label>
            <input id="username" name="username" type="text" class="form-control" value="" placeholder='<hl:message key="mensagem.confirmacao.operacao.sensivel.digite.usuario"/>'>
          </div>
          <div class="form-check">
            <% if (responsavel.isValidaTotp(true)) { %>
            <label for="senha2aAutorizacao">
              <hl:message key="rotulo.codigo.validacao.totp.singular" />
            </label>
            <hl:htmlpassword classe="form-control" di="senha2aAutorizacao" name="senha2aAutorizacao" cryptedfield="senha2aAutorizacaoRSA" cryptedPasswordFieldName="segundaSenha"
              placeHolder='<%= ApplicationResourcesHelper.getMessage("mensagem.confirmacao.operacao.sensivel.digite.codigo", responsavel) %>' />
            <% } else { %>
            <label for="senha2aAutorizacao">
              <hl:message key="mensagem.confirmacao.operacao.sensivel.senha" />
            </label>
            <hl:htmlpassword classe="form-control" di="senha2aAutorizacao" name="senha2aAutorizacao" cryptedfield="senha2aAutorizacaoRSA" cryptedPasswordFieldName="segundaSenha"
              placeHolder='<%= ApplicationResourcesHelper.getMessage("mensagem.confirmacao.operacao.sensivel.digite.senha", responsavel) %>' />
            <% } %>
          </div>
        </div>

        <!-- Captcha -->
        <div id="dialogAutorizacaoCaptcha" class="form-group mb-0" style="display: none;">
          <div class="form-check">
            <label for="captchaAutorizacao">
              <hl:message key="rotulo.captcha.codigo"/>
            </label>
            <input type="text" class="form-control" id="captchaAutorizacao" name="captchaAutorizacao" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>' autocomplete="off">
            <br>
            <img name="captcha_aut_img" id="captcha_aut_img" src="" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
            <div>
              <a href="#no-back" onclick="reloadCaptchaAutorizacao()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
              <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
                data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
                data-original-title=<hl:message key="rotulo.ajuda" />>
                <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
              </a>
            </div>
          </div>
        </div>

        <!-- Captcha avançado -->
        <div id="dialogAutorizacaoCaptchaAvancado" class="form-group mb-0" style="display: none;">
          <div class="form-check">
          <% if (exibeCaptchaAvancado) { %>
            <hl:recaptcha />
          <% } %>
          </div>
        </div>

        <!-- Captcha deficiente -->
        <div id="dialogAutorizacaoCaptchaDeficiente" class="form-group mb-0" style="display: none;">
          <div class="form-check">
            <label for="captchaAutorizacaoDef">
              <hl:message key="rotulo.captcha.codigo"/>
            </label>
            <input type="text" class="form-control" id="captchaAutorizacaoDef" name="captchaAutorizacaoDef" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>' autocomplete="off">
            <br>
            <div id="divCaptchaSound"></div>
            <a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
            <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
          </div>
        </div>

        <input type="hidden" id="timeInMilliseconds" name="timeInMilliseconds" value="">
      </form>
    </div>
    <!-- Modal BI -->
    <div class="modal fade" id="biModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.aviso" />
            </span>
            <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <hl:message key="mensagem.url.acesso.bi" />
          </div>
          <div class="modal-footer pt-0">
            <div class="btn-action mt-2 mb-0">
              <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>"
                title="<hl:message key="rotulo.botao.fechar"/>">
                <hl:message key="rotulo.botao.fechar" />
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal fade" id="biEmBrancoModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.aviso" />
            </span>
            <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <hl:message key="mensagem.url.acesso.bi.em.branco" />
          </div>
          <div class="modal-footer pt-0">
            <div class="btn-action mt-2 mb-0">
              <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>"
                title="<hl:message key="rotulo.botao.fechar"/>">
                <hl:message key="rotulo.botao.fechar" />
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
    <%if(!urlCentral.isEmpty()) {
        urlCentral = urlCentral.replace("/view/", "/acesso.do?action=sair");
    %>
   <%-- Modal Centralizador --%>
   <div class="modal fade" id="sairModalCentral" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header pb-0">
          <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.encerrar.sessao" />
          </span>
          <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <hl:message key="mensagem.confirmacao.logout" />
        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>"
              title="<hl:message key="rotulo.botao.cancelar"/>">
              <hl:message key="rotulo.botao.cancelar" />
            </a>
            <a class="btn btn-primary" href="<%=urlCentral%>" alt="<hl:message key="rotulo.menu.sair"/>" title="<hl:message key="rotulo.menu.sair"/>">
              <hl:message key="rotulo.centralizador.sair" />
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
  <%} %>
  <%if(!urlCentralAcesso.isEmpty()) { %>
   <%-- Modal Centralizador --%>
   <div class="modal fade" id="sairModalCentralAcesso" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header pb-0">
          <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.encerrar.sessao" />
          </span>
          <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <hl:message key="mensagem.confirmacao.logout" />
        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>"
              title="<hl:message key="rotulo.botao.cancelar"/>">
              <hl:message key="rotulo.botao.cancelar" />
            </a>
            <a class="btn btn-primary" href="<%=urlCentralAcesso%>/logout" alt="<hl:message key="rotulo.menu.sair"/>" title="<hl:message key="rotulo.menu.sair"/>">
              <hl:message key="rotulo.centralizador.sair" />
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
  <%} %>

  <% if (chatbotHabilitado) { %>
    <div id="chatbot_wrapper" class="d-print-none">
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
                <div class="form-group col-sm">
                  <label for="chatUserEmail"><hl:message key="rotulo.usuario.email" /></label>
                  <input type="text"
                         class="form-control"
                         id="chatUserEmail"
                         value="<%= !TextHelper.isNull(responsavel.getUsuEmail()) ? TextHelper.forHtmlAttribute(responsavel.getUsuEmail()) : "" %>"
                         <%= !TextHelper.isNull(responsavel.getUsuEmail()) ? "disabled" : "" %>
                         aria-label='<hl:message key="mensagem.atendimento.placeholder.email"/>'
                         placeholder='<hl:message key="mensagem.atendimento.placeholder.email"/>'
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

  <% if (imgsTutorial != null && !imgsTutorial.isEmpty()) { %>
    <% for(int i =0 ;i < imgsTutorial.size();i++){ %>
      <div <%if(i == 0){%>id="tutorial"<%}%> data-bs-toggle="lightbox" data-gallery="tutorial-gallery" data-remote="<%=TextHelper.forHtmlAttribute("../img/view.jsp?nome=tutorial/"+imgsTutorial.get(i))%>"></div>
    <% } %>
  <% } %>

  <% if (!TextHelper.isNull(urlWhatsApp)) { %>
    <a href="<%=TextHelper.forHtmlAttribute(urlWhatsApp)%>" target="_blank">
      <img src="../img/whatsapp/whatsapp-logo.png" class="<%= chatbotHabilitado ? "btn-whatsapp-with-chatbot" : (mostraChat ? "btn-whatsapp-with-chat-zendesk" : "btn-whatsapp-without-chat") %>">
    </a>
  <% } %>

<%if(simularConsignacaoComReconhecimentoFacialELiveness || reconhecimentoFacialLivenssGeracaoSenhaAutorizacao){ %>
<div class="modal fade modal-top -modalReconhecimentoFacial-35" id="modalInformativoReconhecimentoFacial" tabindex="-1" aria-labelledby="faceModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content-ReconhcimentoFacial">
				<div class="modal-body">
					<div class="row">
						<div class="col d-flex flex-column align-items-center">
							<div class="modal-ReconhecimentoFacial-titulo">
								<img class="img-fluid" src="../img/reconhecimentoFacial/icon_reconhecimento_facial.png">
								<h2 class="modal-grid-title"><hl:message key="rotulo.reconhecimento.facial.instrucoes.liveness" /></h2>
							</div>
							<div class="modal-ReconhecimentoFacial-recomendacoes">
								<h5><hl:message key="mensagem.info.reconhecimento.facial.instrucoes"/></h5>
								<p><i class="fa fa-sun-o fa-stack" aria-hidden="true"></i><hl:message key="mensagem.info.reconhecimento.facial.primeira.instrucao" /></p>
								<p><i class="fa fa-smile-o fa-stack" aria-hidden="true"></i><hl:message key="mensagem.info.reconhecimento.facial.segunda.instrucao" /></p>
							</div>
							<div class="btn-action">
								<a class="btn btn-primary" href="#no-back" onClick="iniciarCapturaExpressaoFacial();"><hl:message key="rotulo.reconhecimento.facial.botao.iniciar" /></a>
								<a class="btn btn-outline-danger" href="#no-back" onClick="fecharModalExpressaoFacial();" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar" /></a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<div class="modal fade modal-top -modalReconhecimentoFacial-65" id="faceModalExpressao" tabindex="-1" aria-labelledby="faceModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content-ReconhcimentoFacial">
				<div class="modal-body">
					<div class="row">
						<div class="col d-flex flex-column align-items-center">
							<div class="my-auto">
								<h1 class="modal-grid-title"><hl:message key="rotulo.reconhecimento.facial.instrucoes.liveness" /></h1>
								<h5><p id="textoInformativoExpressao" class="modal-grid-message"><hl:message key="mensagem.info.reconhecimento.facial.instrucao.liveness" /></p></h5>
								<h5><p id="textoExpressaoCaptura" class="modal-grid-message"></p></h5>
								<img id="foto-expressaoFacial" class="img-fluid">
								<div id="retentativaExpressaoFacial">
									<h5><p id="retentativaTexto" class="modal-grid-message"><hl:message key="rotulo.reconhecimento.facial.expressao.retentativa.texto" /></p></h5>
									<a id="retentativaBotao" class="btn btn-primary" href="#no-back" onClick="reiniciarCapturaExpressaoFacial();" alt="<hl:message key="rotulo.botao.sair"/>" title="<hl:message key="rotulo.botao.sair"/>"><hl:message key="rotulo.reconhecimento.facial.expressao.retentativa.botao"/></a>
								</div>
							</div>
							
							<div>
								<a class="btn btn-outline-danger" href="#no-back" onClick="fecharModalExpressaoFacial();" alt="<hl:message key="rotulo.botao.sair"/>" title="<hl:message key="rotulo.botao.sair"/>"><hl:message key="rotulo.botao.sair"/></a>
							</div>
						</div>
						<div class="col">
							<div id="video-container-modalReconhecimentoFacialExpressao">
								<video id="video-modalReconhecimentoFacialExpressao" autoplay></video>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
<% } %>
  <%if(reconhecimentoFacialServidor || reconhecimentoFacialHabilitado) {%>
      <div class="modal fade modal-top -modalReconhecimentoFacial-65" id="faceModal" tabindex="-1" aria-labelledby="faceModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content-ReconhcimentoFacial">
            <div class="modal-body">
              <div class="row">
                <div class="col d-flex flex-column align-items-center">
                  <div class="my-auto">
                    <h1 class="modal-grid-title"><hl:message key="mensagem.info.reconhecimento.facial.titulo"/></h1>
                    <h5><p id="textoInformativo" class="modal-grid-message"><hl:message key="mensagem.info.reconhecimento.facial.posicionamento.rosto"/></p></h5>
                    <div id="countdown"><h2></h2></div>
                    <div id="loading-icon" class="text-center"></div>
                  </div>
                  <div class="btn-action">
                     <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
                     <a class="btn btn-primary" id="botaoTentarNovamente" href="#no-back" onClick="executarReconhecimentoFacial()"><hl:message key="rotulo.reconhecimento.facial.botao.tentar.novamente"/></a>
                  </div>
                </div>
                <div class="col">
                  <div id="video-container-modalReconhecimentoFacial">
                    <video id="video-modalReconhecimentoFacial" autoplay></video>
                    <img id="foto-modalReconhecimentoFacial" class="img-fluid">
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
  <%} %>
  <%--Modal Contato por QRCode --%>
  <div class="modal fade" id="contatoModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" >
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <span class="modal-title about-title mb-0" id="exampleModalLabel"><hl:message key="rotulo.titulo.ranking.contato.consignataria.modal"/></span>
            <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
         <div class="modal-body">
            <div id="qrcode-container">
              <div id="qrcode"></div>
            </div>
              <p><div id="valorContato"></div>
         </div>
         <div class="modal-footer pt-0">
           <div class="btn-action mt-2 mb-0">
             <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>">
               <hl:message key="rotulo.botao.cancelar" />
             </a>
           </div>
         </div>
       </div>
     </div>
   </div>

  <jsp:invoke fragment="pageModals" />
  </section>
  <script src="<c:url value='/node_modules/driver.js/dist/driver.js.iife.js'/>?<hl:message key='release.tag'/>"></script>
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
  <script src="<c:url value='/js/validaemail.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/js/validaform.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/js/xbdhtml.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/js/econsig.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/js/jscrollpane.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/js/validaMascara_v4.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/node_modules/jquery-ui/dist/jquery-ui.min.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/js/listutils.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/node_modules/ekko-lightbox/dist/ekko-lightbox.min.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/node_modules/mobile-detect/mobile-detect.min.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/node_modules/html-screen-capture-js/dist/html-screen-capture.min.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/node_modules/jszip/dist/jszip.min.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/node_modules/echarts/dist/echarts.js'/>?<hl:message key='release.tag'/>"></script>
  <script src="<c:url value='/js/cnpj.js'/>?<hl:message key='release.tag'/>"></script>
  
    <% if (exibeCaptchaAvancado) { %>
    <script src='https://www.google.com/recaptcha/api.js'></script>
    <% } %>
  <script>
    const btnNavbar = document.querySelector("#btn-navbar");
    const arrowImage = document.querySelector("#arrow-image");
    const btnToggle = document.querySelector(".btn-toggle-menu");
    const navbar = document.querySelector(".nav-bar");
    const headerLogo = document.querySelector(".header-logo");
    const zetra = document.querySelector(".zetra");
    const mainMenu = document.querySelector(".main-menu");
    const subMenu = document.querySelector(".submenu");
    const main = document.querySelector(".main");
    const navbarThin = document.querySelector(".nav-bar.nav-bar-thin");
    const headerLogoThin = document.querySelector(".header-logo.header-logo-thin");
    const zetraThin = document.querySelector(".zetra.zetra-thin");
    const mainMenuThin = document.querySelector(".main.main-menu-thin");
    const subMenuThin = document.querySelector(".submenu.submenu-thin");
    const mainThin = document.querySelector(".main.main-thin");
    const navbarLarge = document.querySelector(".nav-bar.nav-bar-large");
    const headerLogoLarge = document.querySelector(".header-logo.header-logo-large");
    const zetraLarge = document.querySelector(".zetra.zetra-large");
    const mainMenuLarge = document.querySelector(".main.main-menu-large");
    const subMenuLarge = document.querySelector(".submenu.submenu-large");
    const mainLarge = document.querySelector(".main.main-large");
    let menuFavoritos = document.querySelector('#menuFavoritos');
    let menuOperacional = document.querySelector('#menuOperacional');
    let menuRelatorio = document.querySelector('#menuRelatorio');
    let menuManutencao = document.querySelector('#menuManutencao');
    let menuRescisao = document.querySelector('#menuRescisao');
    let menuSistema = document.querySelector('#menuSistema');
    let menuBeneficios = document.querySelector('#menuBeneficios');

    window.addEventListener('load', function() {
      setMenuChange();
      zetra.style.display = 'block';
    });

    function clickBtnMenu(){
      btnNavbar.classList.toggle('btn-toggle-menu-active');
      setMenuChange();
      reloadSidebar();
    }

    function setMenuChange(){
      btnMenuState = btnToggle.classList.contains('btn-toggle-menu-active');

      if(btnMenuState){
        btnMenuState = true;
        btnNavbar.classList.add('btn-toggle-menu-active');
        setMenuThin();
      } else {
        btnMenuState = false;
        btnNavbar.classList.remove('btn-toggle-menu-active');
        setMenuLarge();
      }
    }

    function setMenuThin(){
      btnNavbar.classList.add('btn-toggle-menu-active');
      arrowImage.classList.replace('left-arrow', 'rigth-arrow');
      Cookies.set("menu", true);
      navbar.classList.remove('nav-bar-large');
      headerLogo.classList.remove('header-logo-large');
      zetra.classList.remove('zetra-large');
      mainMenu.classList.remove('main-menu-large');
      subMenu.classList.remove('submenu-large');
      main.classList.remove('main-large');
      navbar.classList.add('nav-bar-thin');
      headerLogo.classList.add('header-logo-thin');
      zetra.classList.add('zetra-thin');
      mainMenu.classList.add('main-menu-thin');
      subMenu.classList.add('submenu-thin');
      main.classList.add('main-thin');
    }

    function setMenuLarge(){
      arrowImage.classList.replace('rigth-arrow', 'left-arrow');
      Cookies.set("menu", false);
      navbar.classList.remove('nav-bar-thin');
      headerLogo.classList.remove('header-logo-thin');
      zetra.classList.remove('zetra-thin');
      mainMenu.classList.remove('main-menu-thin');
      subMenu.classList.remove('submenu-thin');
      main.classList.remove('main-thin');
      navbar.classList.add('nav-bar-large');
      headerLogo.classList.add('header-logo-large');
      zetra.classList.add('zetra-large');
      mainMenu.classList.add('main-menu-large');
      subMenu.classList.add('submenu-large');
      main.classList.add('main-large');
    }

    function setMenuMatchMedia() {
      navbar.classList.remove('nav-bar-thin');
      headerLogo.classList.remove('header-logo-thin');
      zetra.classList.remove('zetra-thin');
      mainMenu.classList.remove('main-menu-thin');
      subMenu.classList.remove('submenu-thin');
      main.classList.remove('main-thin');
      navbar.classList.remove('nav-bar-large');
      headerLogo.classList.remove('header-logo-large');
      zetra.classList.remove('zetra-large');
      mainMenu.classList.remove('main-menu-large');
      subMenu.classList.remove('submenu-large');
      main.classList.remove('main-large');
    }

    function applyMatchMedia(){
      let mediaSize = matchMedia("(max-width:1140px)").matches;
      let mediaSmall = matchMedia("(max-width:970px)").matches;
      if(mediaSize && !mediaSmall){
        setMenuThin();
        btnNavbar.style.visibility = "hidden";
      } else if(mediaSmall){
        setMenuMatchMedia();
        btnNavbar.style.visibility = "hidden";
      } else {
        let menuThin = Cookies.get('menu') === 'true';
        btnNavbar.style.visibility= "visible";
        if(menuThin){
            setMenuThin();
        } else {
            setMenuLarge();
        }
      }
    }

    function hideSubmenu(){
      if (navbar.offsetWidth < 230){
        menuFavoritos != null ? menuFavoritos.classList.remove('show') : "";
        menuOperacional != null ? menuOperacional.classList.remove('show') : "";
        menuRelatorio != null ? menuRelatorio.classList.remove('show') : "";
        menuManutencao != null ? menuManutencao.classList.remove('show') : "";
        menuRescisao != null ? menuRescisao.classList.remove('show') : "";
        menuSistema != null ? menuSistema.classList.remove('show') : "";
        menuBeneficios != null ? menuBeneficios.classList.remove('show') : "";
      }
    }

    setInterval(hideSubmenu, 300);
    window.addEventListener('resize', applyMatchMedia, false);

  </script>

  <script type="text/JavaScript">
    <% if (imgsTutorial != null && !imgsTutorial.isEmpty()) { %>
        function abrirTutorialPrimeiroAcesso() {
            $('#tutorial').ekkoLightbox({
                wrapping: false,
                alwaysShowClose: true
            });
        }
        if (<%= exibirTutorialAoAbrir %>) {
            $(function () {
              abrirTutorialPrimeiroAcesso();
            });
        }
    <% } %>

    <% if(exibeMargem){ %>
    function ocultaMargemCookie() {
        var ocultaMargem = Cookies.get("ocultaMargem");
        if(ocultaMargem != null){
            if(ocultaMargem == "true") {
                document.getElementById('margemExibida').className = 'olho-margem-manu d-none';
                document.getElementById('margemOculta').className = 'olho-margem-manu';
                document.getElementById('userMargemMenu').className = 'alert-messages nav-margem dropdown-toggle cursor-d';
                document.getElementById('margemDropDown').className = 'd-none';
                document.getElementById('olhoMargem').className = 'd-none';
                document.getElementById('olhoMargemOculto').className = '';
            } else {
                document.getElementById('margemOculta').className = 'olho-margem-manu d-none';
                document.getElementById('margemExibida').className = 'olho-margem-manu';
                document.getElementById('userMargemMenu').className = 'alert-messages nav-margem dropdown-toggle';
                document.getElementById('margemDropDown').className = 'dropdown-menu dropdown-menu-right';
                document.getElementById('olhoMargem').className = '';
                document.getElementById('olhoMargemOculto').className = 'd-none';
            }
        }
    }

    function ocultaMargem() {
        classe = document.getElementById('margemExibida').className;
        if(classe == 'olho-margem-manu') {
            document.getElementById('margemExibida').className = 'olho-margem-manu d-none';
            document.getElementById('margemOculta').className = 'olho-margem-manu';
            document.getElementById('userMargemMenu').className = 'alert-messages nav-margem dropdown-toggle cursor-d';
            document.getElementById('margemDropDown').className = 'd-none';
            document.getElementById('olhoMargem').className = 'd-none';
            document.getElementById('olhoMargemOculto').className = '';
            Cookies.set("ocultaMargem", true);
        } else {
            document.getElementById('margemOculta').className = 'olho-margem-manu d-none';
            document.getElementById('margemExibida').className = 'olho-margem-manu';
            document.getElementById('userMargemMenu').className = 'alert-messages nav-margem dropdown-toggle';
            document.getElementById('margemDropDown').className = 'dropdown-menu dropdown-menu-right';
            document.getElementById('olhoMargem').className = '';
            document.getElementById('olhoMargemOculto').className = 'd-none';
            Cookies.set("ocultaMargem", false);
        }
    }

    <% } else { %>
    function ocultaMargem() {
       <% if (exibeCaptchaDeficiente) { %>
        montaCaptchaSomSer('topo');
        <% } %>
        $('#modalCaptcha_topo').modal('show');
    }

    function ocultaMargemCookie() {
    	if (document.getElementById('margemOculta') != null) {
    		document.getElementById('margemOculta').className = 'olho-margem-manu';
    	}
    	if (document.getElementById('userMargemMenu') != null) {
    		document.getElementById('userMargemMenu').className = 'alert-messages nav-margem dropdown-toggle cursor-d';
    	}
    	if (document.getElementById('olhoMargemOculto') != null) {
    		document.getElementById('olhoMargemOculto').className = '';
    	}
    	Cookies.remove('ocultaMargem');
    }
    <% } %>

    <%if(scriptOculta){ %>
      document.addEventListener("load", ocultaMargemCookie());
    <%}%>

      function sairSistema() {
        <% if (TextHelper.isNull(oAuth2UriLogout)) { %>
          postData('../v3/sairSistema?acao=sair&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>','_top');
        <% } else { %>
        var urlLOA2 = '<%=oAuth2UriLogout%>';

        var isLogoutIframe = <%=!TextHelper.isNull(oAuth2MsgLogout)%>;
        if (!isLogoutIframe) {
	        $('#logoutOauth2').attr('src', urlLOA2);
	        setTimeout(function() {
	          postData('../v3/sairSistema?acao=sair&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>','_top');
	        }, 1000);
        } else {
	        $.post('../v3/sairSistema?acao=sair&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>', function(data, status) {
	            // Esconde modal sair sistema para exibir mensagem
	        	$('#sairModal').modal('toggle');
	        	// Exibe mensagem de redirecionamento para logout externo
				$("#sairOauthModal").modal('toggle');
		        setTimeout(function() {
		            window.location.href = urlLOA2;
		        }, 1500);
	        });
        }
        <% } %>
      }
 </script>
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
  <script type="text/JavaScript">
if (!String.prototype.endsWith) {
  String.prototype.endsWith = function(search, this_len) {
    if (this_len === undefined || this_len > this.length) {
      this_len = this.length;
    }
    return this.substring(this_len - search.length, this_len) === search;
  };
}

focusSenha = false;
HTMLFormElement.prototype._submit = HTMLFormElement.prototype.submit;
HTMLFormElement.prototype.submit = function () {
   var formulario = this;
   var formActionArray = formulario.action.split(/[?&]/);
   var param = "uri=" + formActionArray[0];
   for (i = 1; i < formActionArray.length; i++) {
     param += "&" + formActionArray[i];
   }
   for (i = 0; i < formulario.elements.length; i++) {
       var el = formulario.elements[i];
       if (el.name && el.type.toUpperCase() != 'FILE') {
           param += "&" + encodeURIComponent(el.name) + "=" + encodeURIComponent(el.value);
       }
   }

   $.post("../v3/verificarOperacao?_skip_history_=true", param, function(data) {
       try {
         var trimData = $.trim(JSON.stringify(data));
           var obj = JSON.parse(trimData);
           var autTotpHabilitado = <%=responsavel.isPermiteTotp() ? "true" : "false"%>;
           var validarCaptcha = obj.requerCaptcha == 'S' || obj.requerCaptchaAvancado == 'S' || obj.requerCaptchaDeficiente == 'S';
           var validarSenha = obj.requerAutorizacao == 'S' || obj.requerAutorizacao == 'P' || (obj.requerAutorizacao == 'F' && autTotpHabilitado);

           if (obj.requerAutorizacao != 'N' || validarCaptcha) {
             if (validarSenha) {
               $('#dialogAutorizacaoSenha').show();
             }
             if (obj.requerCaptcha == 'S') {
               // a imagem do captcha deve ser carregada somente ao exibir o modal para evitar conflito com outras páginas que exibem captcha
               var srcCaptcha = '../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>';
               $('#captcha_aut_img').attr('src',srcCaptcha);
               $('#dialogAutorizacaoCaptcha').show();
             } else if (obj.requerCaptchaAvancado == 'S') {
               var script = document.createElement("script");
               script.src = 'https://www.google.com/recaptcha/api.js';
               document.head.appendChild(script);

               setInterval(function () {
                   $("iframe[title*='recaptcha' i]").parent().parent().addClass('recaptcha_challenge');
               }, 1000);

               $('#dialogAutorizacaoCaptchaAvancado').show();
             } else if (obj.requerCaptchaDeficiente == 'S') {
               montaCaptchaSom();
               $('#dialogAutorizacaoCaptchaDeficiente').show();
             }

             if (obj.requerAutorizacao == 'P' || (obj.requerAutorizacao == 'F' && autTotpHabilitado)) {
               $('#username').prop('disabled', true);
               $('#username').val('<%=responsavel.getUsuLogin()%>');
               focusSenha = true;
             }

             if (obj.requerAutorizacao != 'F' || autTotpHabilitado || validarCaptcha) {
                 $("#dialogAutorizacao").dialog({
                     modal: true,
                     autoOpen: true,
                     position: { my: "center", at: "center", of: window },
                     classes: {
                         "ui-dialog": "no-close",
                         "ui-dialog-titlebar": "",
                      },
                      buttons: [
                      {
                         text: '<hl:message key="rotulo.botao.cancelar"/>',
                         class: "btn btn-outline-danger",
                         click: function() {
                             $('#dialogAutorizacaoErro').empty();
                             $('#dialogAutorizacaoErro').hide();
                             $( this ).dialog("close");
                         }
                      },{
                         text: '<hl:message key="rotulo.botao.confirmar"/>',
                         class: "btn btn-primary",
                         click: function(clickEvent) {
                             $('#dialogAutorizacaoErro').empty();
                             $('#dialogAutorizacaoErro').hide();
                             if (validarSenha) {
                                 var plainPasswordField = document.getElementsByName("senha2aAutorizacao")[0];
                                 var cryptedPasswordField = document.getElementsByName("senha2aAutorizacaoRSA")[0];
                                 if (!CriptografaSenha(plainPasswordField, cryptedPasswordField, true, null)) {
                                    return false;
                                 }
                             }

                             var username = $('#username').val();
                             var password = $('input[name=senha2aAutorizacaoRSA]').val();
                             var param2 = param;
                             param2 += "&username=" + encodeURIComponent(username)
                                    + "&password=" + encodeURIComponent(password)
                                    + "&timeInMilliseconds=" + (new Date()).getTime()
                                    + "&validarSenha=" + encodeURIComponent(validarSenha)
                                    + "&validarCaptcha=" + encodeURIComponent(validarCaptcha);

                             if (validarCaptcha) {
                               if (obj.requerCaptcha == 'S') {
                                 var captcha = $('#captchaAutorizacao').val();
                                 param2 += "&captcha=" + encodeURIComponent(captcha);
                               } else if (obj.requerCaptchaAvancado == 'S') {
                                 var gCaptchaResponse = $('#g-recaptcha-response').val();
                                 param2 += "&g-recaptcha-response=" + encodeURIComponent(gCaptchaResponse);
                               } else if (obj.requerCaptchaDeficiente == 'S') {
                                 var captcha = $('#captchaAutorizacaoDef').val();
                                 param2 += "&captcha=" + encodeURIComponent(captcha);
                               }
                             }

                             $.post("../v3/autorizarOperacao?_skip_history_=true", param2, function(data) {
                                 try {
                                     var trimData = $.trim(JSON.stringify(data));
                                     var resultAutorizacao = JSON.parse(trimData);
                                     if (resultAutorizacao.prosseguir == 'S') {
                                        if (obj.requerAutorizacao == 'F' && (autTotpHabilitado || validarCaptcha)) {
                                           enviarOperacaoFilaAutorizacao(formulario);
                                        } else {
                                          formulario._submit();
                                          clickEvent.target.disabled = true;
                                        }
                                     } else {
                                        $('#dialogAutorizacaoErro').show();
                                        $('#dialogAutorizacaoErro').append("<p class=\"mb-0\">" + resultAutorizacao.mensagem + "</p>");
                                        if (obj.requerCaptcha == 'S') {
                                          $('#captchaAutorizacao').val('');
                                          reloadCaptchaAutorizacao();
                                        } else if (obj.requerCaptchaDeficiente == 'S') {
                                          $('#captchaAutorizacaoDef').val('');
                                          reloadSimpleCaptcha();
                                        }
                                     }
                                 } catch(err) {
                                 }
                             }, "json");
                         }
                      }]
                  });
               } else {
                 enviarOperacaoFilaAutorizacao(formulario);
               }
           } else {
             formulario._submit();
           }
       } catch(err) {
       }
   }, "json");
}

function reloadCaptchaAutorizacao() {
  var randomNumber = Math.floor(Math.random() * 1000);
  document['captcha_aut_img'].src='../captcha.jpg?_=' + randomNumber;
}

function enviarOperacaoFilaAutorizacao(customForm) {
    if (<%=responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_OP_FILA_AUTORIZACAO)%>) {
        return;
    }

  const jsZip = new JSZip();
  $(".modal:visible").modal('toggle');
  $(".main-content").find(".collapse").show();
    // desabilita todos os inputs para evitar que estes fiquem editáveis na visualização para aprovação
  $( "form :input" ).prop("disabled", true);

  $(".main").css("margin-left","0px");
  $("span").css("width","auto");

  var captchaDiv = $(".captcha").html();
  jQuery('#modalAguarde').html("");
  $(".captcha").html("");
  $(".g-recaptcha").html("");
  $("#simpleAudio").html("");

  const htmlDocStr = htmlScreenCaptureJs.capture('string', document, { tagsOfIgnoredDocBodyElements: [
                                                                                                        'a', //remove links do snapshot
                                                                                                        'script',
                                                                                                        'modal-backdrop',
                                                                                                       ],
                                                                         classesOfIgnoredDocBodyElements: ['dialog-autorizacao-tag',
                                                                                                         'access-class',
                                                                                                         'header-logo',
                                                                                                         'nav-bar',
                                                                                                         'chatbot_fechado',
                                                                                                         'chatbot_aberto'],

                                                                        tagsOfSkippedElementsForChildTreeCssHandling: ['div']});

  $( "form :input" ).prop("disabled", false);
  $(".main").css("margin-left","230px");
  $(".modal:visible").modal('toggle');
    jsZip.remove('screen-capture.html');
  jsZip.file('screen-capture.html', htmlDocStr);

  jsZip.generateAsync({ type: "base64", compression: "DEFLATE", platform: "UNIX" })
  .then(function(screenCaptureZipFile) {
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'htmlSnapshot';
    input.id = 'htmlSnapshot';
    input.value = screenCaptureZipFile;
    customForm.appendChild(input);

    jsZip.remove('screen-capture.html');

    customForm._submit();
  });

}

function toggleMenuAcessibilidadeV4() {
  let opcoesAcessibilidade = document.getElementById('opcoesAcessibilidadeV4');
  let menuAcessibilidadeIcon = document.querySelector('#menuAcessibilidade i.fa-universal-access');
  let menuAcessibilidadeLink = document.querySelector('#menuAcessibilidade a.fill-div');

  let menuWhatsAppChatbot = document.querySelector('img.btn-whatsapp-with-chatbot');
  let menuWhatsAppZendesk = document.querySelector('img.btn-whatsapp-with-chat-zendesk');
  let menuWhatsApp = document.querySelector('img.btn-whatsapp-without-chat');
  let menuChatZendesk = document.querySelector('iframe#launcher');
  let menuChatbotFechado = document.querySelector('div.chatbot_fechado');
  let menuChatbotAberto = document.querySelector('div.chatbot_aberto');

  if(opcoesAcessibilidade != null && menuAcessibilidadeIcon != null && menuAcessibilidadeLink != null) {
    menuAcessibilidadeIcon.classList.toggle('expanded');
    menuAcessibilidadeLink.classList.toggle('open');

    if (menuWhatsAppChatbot) {
      menuWhatsAppChatbot.classList.toggle('btn-whatsapp-with-chatbot-open');
    }
    if (menuWhatsAppZendesk) {
      menuWhatsAppZendesk.classList.toggle('btn-whatsapp-with-chat-zendesk-open');
    }
    if (menuWhatsApp) {
      menuWhatsApp.classList.toggle('btn-whatsapp-without-chat-open');
    }
    if (menuChatZendesk) {
      menuChatZendesk.classList.toggle('iframe-launcher-open');
    }
    if (menuChatbotFechado) {
      menuChatbotFechado.classList.toggle('chatbot_acessibilidade_open');
    }
    if (menuChatbotAberto) {
      menuChatbotAberto.classList.toggle('chatbot_acessibilidade_open');
    }

    let transitionDurationStr = getComputedStyle(menuAcessibilidadeIcon)['transition-duration'];
    const transitionDurationMiliseconds = transitionDurationStr.endsWith('ms') ? parseFloat(transitionDurationStr) : parseFloat(transitionDurationStr) * 1000;

    /* Some e aparece junto com a animação de expansão da aba */
    setTimeout(function() {
      opcoesAcessibilidade.classList.toggle('hide');
    }, transitionDurationMiliseconds);
  }
}

// DESENV-13174: Ao apertar tab, vai primeiro para o Menu de Acessibilidade V4
window.addEventListener('keydown', function(evt) {
  const menu = document.getElementById('menuAcessibilidade');
  if(menu != null) {
    const menuLink = menu.querySelector('a.fill-div');
    const key = evt.key;
    const target = evt.target;

    if(key === 'Tab' && sessionStorage.getItem('tabHasBeenPressed') === null &&
      (document.activeElement === null || document.activeElement.tagName.toLowerCase() === 'body')) {
      menu.focus();
      evt.preventDefault();
      sessionStorage.setItem('tabHasBeenPressed', 'true');
    }

    if(key === 'Enter') {
      if(menu.isEqualNode(target))
        menuLink.click();  // Abre o menu propriamente dito, com todas as opções
      else if(menu.contains(target))
        target.click();    // Realiza a ação de uma dada opção do menu
    }
  }
  if (evt.key==='F5') {
    evt.preventDefault();
  }
  if ((evt.ctrlKey || evt.metaKey) && evt.key.toLowerCase() === 'r') {
    evt.preventDefault();
  }
});

$(document).ready(function() {
    // DESENV-13174
    sessionStorage.removeItem('tabHasBeenPressed');

    //DESENV-12751: força o atributo checked nos inputs check e radio para correto snapshot da página nas operações sensíveis com aprovação posterior
    $("[type=checkbox]").click(function() {
        if($(this).prop('checked')) {
          $(this).attr("checked", "checked");
        } else {
          $(this).removeAttr("checked");
        }
  });

    $("[type=radio]").click(function() {
       if($(this).prop('checked')) {
         var idCorrente = $(this).attr('id');
         var nomeElemRadio = $(this).attr('name');
         $(this).attr("checked", "checked");

         var outerRadiosGroup = document.getElementsByName(nomeElemRadio);
           for (var i = 0; i < outerRadiosGroup.length; i++) {
               if (outerRadiosGroup[i].getAttribute('id') != idCorrente) {
                  var idFor = outerRadiosGroup[i].getAttribute('id');
                  $("#" + idFor).removeAttr("checked");
               }
           }
       }
     });
});

</script>
<% if (exibePassoAPasso) { %>


<script>
  $(document).ready(function() {
    carregarAjudaRecurso('<%=acesso.getAcrCodigo()%>');
  });

  var steps;

  function startIntro() {
	  const driver = window.driver.js.driver;
	  const driverObj = driver({
		  animate: true,
		  showProgress: true,
		  showButtons: ['next', 'previous', 'close'],
		  nextBtnText: '<hl:message key="rotulo.botao.proximo" />',
		  prevBtnText: '<hl:message key="rotulo.botao.anterior" />',
		  doneBtnText: '<hl:message key="rotulo.botao.concluido" />',
		  steps: steps,
	  });
	  driverObj.drive();
  }

  function carregarAjudaRecurso(acrCodigo) {
     <% if (!exibirTutorialAoAbrir) { %> 
        return $.ajax({
              type: 'POST',
              url: '../v3/visualizarAjudaRecurso?acao=visualizarAjudaRecurso&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_',
              data: {
                  'acrCodigo': acrCodigo
              },
              success: function (data) {
                  steps = JSON.parse(data).ajudaRecursos;
                  var exibeAjudaRecurso = JSON.parse(data).exibeAjudaRecurso;
                  if (steps.length > 0) {
                      $('#btnPassoAPasso').show();
                  }
                  if (exibeAjudaRecurso && steps.length > 0) {
                      startIntro();
                  }
              },
              error: function (request, status, error) {
              }
          }).done(function () {
        });
    <% } else { %>
        return false;
    <% } %>
    }
</script>
<% } %>

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
                var usermail = $('#chatUserEmail').val();
                startChat(username, usermail);
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
            var usermail = $('#chatUserEmail').val();
            if (!isEmailValid(usermail)) {
                $('#chatUserEmail').css({ "border": "1px solid #ff0000" });
                alert('<hl:message key="mensagem.atendimento.digite.email.valido"/>');
                $('#chatUserEmail').focus();
            } else if (!username) {
                $('#chatUserName').css({ "border": "1px solid #ff0000" });
                alert('<hl:message key="mensagem.atendimento.digite.nome.valido"/>');
                $('#chatUserName').focus();
            } else {
                startChat(username, usermail);
            }
        }
        function startChat(username, usermail) {
            $('#chatbot_aberto').hide();
            $('#chatbot_aberto_conversa').show();
            startSession(username, usermail);
        }
        function startSession(username, usermail) {
            $.ajax({
                type: 'post',
                url: "../v3/iniciarChatbot",
                data: {
                    nome: username,
                    email: usermail,
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
  <% } else if (mostraChat) { %>
    <!-- Start of Zendesk Widget script -->
    <script id="ze-snippet" src="<%= TextHelper.forHtmlAttribute(ParamSist.getInstance().getParam(CodedValues.TPC_URL_CHAT_SUPORTE_WIDGET, responsavel)) %>"></script>
    <!-- End of Zendesk Widget script -->
  <% } %>

  <jsp:invoke fragment="javascript" />
  <%-- Placed at the end of the document so the pages load faster --%>
  <script src="<c:url value='/node_modules/@popperjs/core/dist/umd/popper.min.js'/>"></script>
  <script src="<c:url value='/node_modules/bootstrap/dist/js/bootstrap.min.js'/>"></script>
  <script src="../js/qrcode.min.js"></script>
  <%-- Menu de acessibilidade para eMAG 3.1 - Criterio de Sucesso 2.2 --%>
  <script>
    $(document).ready(function() {
    	if(Cookies.get("bold-v4")) {
	    	$("*").addClass("bold-v4");
	    	$("i").removeClass("bold-v4");
    	}
    });
 </script>
 <% if(simularConsignacaoComReconhecimentoFacialELiveness || reconhecimentoFacialLivenssGeracaoSenhaAutorizacao){ %>
	<script src="<c:url value='/js/faceapi/face-api.min.js'/>"></script>
	<script type="text/Javascript">
		 const localModels = '<%=request.getContextPath()%>' + '/js/faceapi/models';

		  const videoExpressao = document.getElementById('video-modalReconhecimentoFacialExpressao');
		  const textoExpressaoCaptura = document.getElementById('textoExpressaoCaptura');
		  const fotoInstrucaoReconhecimento = document.getElementById('foto-expressaoFacial');
		  const retentativaExpressaoFacial = document.getElementById('retentativaExpressaoFacial');
		  const retentativaTexto = document.getElementById('retentativaTexto');
		  const retentativaBotao = document.getElementById('retentativaBotao');
		  const limSuperiorExpressao = 1;
		  let envioUnico = false;
		  let limInferiorExpressao = 0.70;
		  let retentativas = 0;
		  let primeiraExpressaoAprovada = false;
		  let segundaExpressaoAprovada = false;
		  let terceiraExpressaoAprovada = false;
		  let sorteioExpressao = {
		  	happy: false,
		  	angry: false,
		  	surprised: false,
		  	disgusted: false,
		  	fearful: false,
		  	sad: false
		  };

		  let aprovacaoExpressao = {
		  	happy: false,
		  	angry: false,
		  	surprised: false,
		  	disgusted: false,
		  	fearful: false,
		  	sad: false
		  }
		  let streamExpressao;
		  let captura;
		  let temporizadorExpressao;
		  
		  forcaFechamentoModalExpressa = function(event) {
			  	clearInterval(captura);
			  	clearTimeout(temporizadorExpressao);
			  	desligarVideo();
			  	resetaValoresIniciais();
			};
			document.getElementById("faceModalExpressao").addEventListener('hide.bs.modal', forcaFechamentoModalExpressa);

		  function iniciarInstrucoes() {
		  	$('#modalInformativoReconhecimentoFacial').modal('show');
		  }

		  function iniciarCapturaExpressaoFacial() {
		  	$('#modalInformativoReconhecimentoFacial').modal('hide');
		  	$('#faceModalExpressao').modal('show');
		  	carregarModels();
		  }

		  function carregarModels() {
		  	Promise.all([
		  		faceapi.nets.tinyFaceDetector.loadFromUri(localModels),
		  		faceapi.nets.faceLandmark68Net.loadFromUri(localModels),
		  		faceapi.nets.faceRecognitionNet.loadFromUri(localModels),
		  		faceapi.nets.faceExpressionNet.loadFromUri(localModels)
		  	]).then(iniciarVideo());
		  }

		  async function iniciarVideo() {
		  	streamExpressao = await navigator.mediaDevices.getUserMedia({ video: true });

		  	videoExpressao.srcObject = streamExpressao;
		  	videoExpressao.addEventListener('loadedmetadata', () => {
		  		videoExpressao.play();
		  		sortearExpressoes();
		  		carregarExpressao();
		  		esconderTextoRetentativa();
		  		capturarExpressoes();
		  	});
		  }

		  async function capturarExpressoes() {
		  	temporizadorExpressao = setTimeout(conferirDificuldadeReconhecimento, 20000);
		  	captura = setInterval(async () => {
		  		const detections = await faceapi.detectAllFaces(videoExpressao, new faceapi.TinyFaceDetectorOptions()).withFaceLandmarks().withFaceExpressions();
		  		if (detections.length == 1) {
		  			if ((sorteioExpressao.happy == true) && (limInferiorExpressao < detections[0].expressions.happy && detections[0].expressions.happy < limSuperiorExpressao)) {
		  				aprovacaoExpressao.happy = true;
		  			} else if ((sorteioExpressao.angry == true) && (limInferiorExpressao < detections[0].expressions.angry && detections[0].expressions.angry < limSuperiorExpressao)) {
		  				aprovacaoExpressao.angry = true;
		  			} else if ((sorteioExpressao.surprised) && (limInferiorExpressao < detections[0].expressions.surprised && detections[0].expressions.surprised < limSuperiorExpressao)) {
		  				aprovacaoExpressao.surprised = true;
		  			} else if ((sorteioExpressao.disgusted) && (limInferiorExpressao < detections[0].expressions.disgusted && detections[0].expressions.disgusted < limSuperiorExpressao)) {
		  				aprovacaoExpressao.disgusted = true;
		  			} else if ((sorteioExpressao.fearful) && (limInferiorExpressao < detections[0].expressions.fearful && detections[0].expressions.fearful < limSuperiorExpressao)) {
		  				aprovacaoExpressao.fearful = true;
		  			} else if ((sorteioExpressao.sad) && (limInferiorExpressao < detections[0].expressions.sad && detections[0].expressions.sad < limSuperiorExpressao)) {
		  				aprovacaoExpressao.sad = true;
		  			}

		  			if (verificaExpressoesAprovadas() == true && !envioUnico) {
		  				clearInterval(captura);
		  				fecharModalExpressaoFacial();
		  				executarReconhecimentoFacial();
		  				envioUnico = true;
		  			}
		  		}
		  	}, 100);
		  }

		  function verificaExpressoesAprovadas() {
		  	for (const expressao in aprovacaoExpressao) {
		  		if (!primeiraExpressaoAprovada && aprovacaoExpressao[expressao]) {
		  			primeiraExpressaoAprovada = true;
		  			aprovacaoExpressao[expressao] = false;
		  			sorteioExpressao[expressao] = false;
		  			carregarExpressao();
		  		} else if (primeiraExpressaoAprovada && !segundaExpressaoAprovada && aprovacaoExpressao[expressao]) {
		  			segundaExpressaoAprovada = true;
		  			aprovacaoExpressao[expressao] = false;
		  			sorteioExpressao[expressao] = false;
		  			carregarExpressao();
		  		} else if (primeiraExpressaoAprovada && segundaExpressaoAprovada && !terceiraExpressaoAprovada && aprovacaoExpressao[expressao]) {
		  			terceiraExpressaoAprovada = true;
		  			carregarExpressao();
		  			aprovacaoExpressao[expressao] = false;
		  			sorteioExpressao[expressao] = false;
		  		}
		  	}

		  	if (primeiraExpressaoAprovada && segundaExpressaoAprovada && terceiraExpressaoAprovada) {
		  		return true;
		  	} else {
		  		return false;
		  	}
		  }

		  function sortearExpressoes() {
		  	let expressoes = ["happy", "angry", "surprised", "disgusted", "fearful", "sad"];
		  	let indice = Math.floor(Math.random() * 6);
		  	let expressoesSorteadas = [];
		  	while (expressoesSorteadas.length < 3) {
		  		if (!expressoesSorteadas.includes(expressoes[indice])) {
		  			expressoesSorteadas.push(expressoes[indice]);
		  		}
		  		indice = Math.floor(Math.random() * 6);
		  	}

		  	for (var i = 0; i < expressoesSorteadas.length; i++) {
		  		sorteioExpressao[expressoesSorteadas[i]] = true;
		  	}
		  }

		  function carregarExpressao() {
		  	if (sorteioExpressao.happy) {
		  		textoExpressaoCaptura.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.expressao.liveness", responsavel, ApplicationResourcesHelper.getMessage("rotulo.reconhecimento.facial.expressao.felicidade", responsavel))%>';
		  		fotoInstrucaoReconhecimento.src = '../img/reconhecimentoFacial/expressoes/happy.jpeg';
		  	} else if (sorteioExpressao.angry) {
		  		textoExpressaoCaptura.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.expressao.liveness", responsavel, ApplicationResourcesHelper.getMessage("rotulo.reconhecimento.facial.expressao.nervoso", responsavel))%>';
		  		fotoInstrucaoReconhecimento.src = '../img/reconhecimentoFacial/expressoes/angry.jpeg';
		  	} else if (sorteioExpressao.surprised) {
		  		textoExpressaoCaptura.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.expressao.liveness", responsavel, ApplicationResourcesHelper.getMessage("rotulo.reconhecimento.facial.expressao.surpreso", responsavel))%>';
		  		fotoInstrucaoReconhecimento.src = '../img/reconhecimentoFacial/expressoes/surprised.jpeg';
		  	} else if (sorteioExpressao.disgusted) {
		  		textoExpressaoCaptura.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.expressao.liveness", responsavel, ApplicationResourcesHelper.getMessage("rotulo.reconhecimento.facial.expressao.nojo", responsavel))%>';
		  		fotoInstrucaoReconhecimento.src = '../img/reconhecimentoFacial/expressoes/disgusted.jpeg';
		  	} else if (sorteioExpressao.fearful) {
		  		textoExpressaoCaptura.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.expressao.liveness", responsavel, ApplicationResourcesHelper.getMessage("rotulo.reconhecimento.facial.expressao.medo", responsavel))%>';
		  		fotoInstrucaoReconhecimento.src = '../img/reconhecimentoFacial/expressoes/fearful.jpeg';
		  	} else if (sorteioExpressao.sad) {
		  		textoExpressaoCaptura.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.expressao.liveness", responsavel, ApplicationResourcesHelper.getMessage("rotulo.reconhecimento.facial.expressao.tristeza", responsavel))%>';
		  		fotoInstrucaoReconhecimento.src = '../img/reconhecimentoFacial/expressoes/sad.jpeg';
		  	}

		  	if (textoExpressaoCaptura.style.display == 'none' && fotoInstrucaoReconhecimento.style.display == 'none') {
		  		textoExpressaoCaptura.style.display = '';
		  		fotoInstrucaoReconhecimento.style.display = '';
		  	}
		  }

		  function conferirDificuldadeReconhecimento() {
		  	if (retentativas >= 3) {
		  		retentativaTexto.innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.expressao.validacao.reprovada", responsavel)%>';
		  		retentativaBotao.style.display = 'none';
		  		retentativaExpressaoFacial.style.display = 'block';
		  		retentativaTexto.style.display = 'block';
		  	} else {
		  		retentativaExpressaoFacial.style.display = 'block';
		  	}
		  }

		  function reiniciarCapturaExpressaoFacial() {
		  	retentativas++;
		  	textoExpressaoCaptura.style.display = 'none';
		  	fotoInstrucaoReconhecimento.style.display = 'none';
		  	limInferiorExpressao = 0.85
		  	fecharModalExpressaoFacial();
		  	esconderTextoRetentativa();
		  	iniciarInstrucoes();
		  }

		  function fecharModalExpressaoFacial() {
		  	clearInterval(captura);
		  	clearTimeout(temporizadorExpressao);
		  	desligarVideo();
		  	resetaValoresIniciais();
		  	$('#faceModalExpressao').modal('hide');
		  	$('#modalInformativoReconhecimentoFacial').modal('hide');
		  }

		  function desligarVideo() {
		  	if (streamExpressao) {
		  		streamExpressao.getTracks().forEach(track => track.stop());
		  	}
		  }


		  function resetaValoresIniciais() {
		  	primeiraExpressaoAprovada = false;
		  	segundaExpressaoAprovada = false;
		  	terceiraExpressaoAprovada = false;
		  	envioUnico = false;
		  	sorteioExpressao = {
		  		happy: false,
		  		angry: false,
		  		surprised: false,
		  		disgusted: false,
		  		fearful: false,
		  		sad: false
		  	};

		  	aprovacaoExpressao = {
		  		happy: false,
		  		angry: false,
		  		surprised: false,
		  		disgusted: false,
		  		fearful: false,
		  		sad: false
		  	}
		  }

		  function esconderTextoRetentativa() {
		  	retentativaExpressaoFacial.style.display = 'none';
		  }
	</script>
 <% } %>
 <%if(reconhecimentoFacialHabilitado){ %>
 <script src="<c:url value='/js/faceapi/face-api.min.js'/>"></script>
 <script type="text/Javascript">
 let quantidadeTentativas = 0;
 let contagemRegressivaAtiva = true;
 	 function executarReconhecimentoFacial(){
		      $(document).ready(function() {
		      	  $('#faceModal').modal('show');
		      	});

		      	forcaFechamentoModal = function(event) {
		      	    event.preventDefault();
		      	  };
		  		
		      	document.getElementById("faceModal").addEventListener('hide.bs.modal', forcaFechamentoModal);

		      	const localModels = '<%=request.getContextPath()%>' + '/js/faceapi/models';
		      	const video = document.getElementById('video-modalReconhecimentoFacial');
		      	let foto = document.getElementById('foto-modalReconhecimentoFacial');
		      	const videoContainer = document.getElementById('video-container-modalReconhecimentoFacial');
		      	const countdownElement = document.querySelector('#countdown h2');

		      	const ovalAspectRatio = 4 / 3;
		      	const botaoTentarNovamente = document.getElementById("botaoTentarNovamente");
		      	botaoTentarNovamente.style.display = 'none';
		      	
		      	let intervaloContagemRegressiva;
		      	let contagemRegressivaIniciada = false;
		      	let ocorreuErroRequisicao = false;
		      	let stream;
		      	let base64DataRecortada = null;
		      	
		      	if(quantidadeTentativas > 0){
		      		reiniciarCaptura();
		      	}

		      	async function iniciarVideo() {
		      	  stream = await navigator.mediaDevices.getUserMedia({ video: true });

		      	  video.srcObject = stream;
		      	  video.addEventListener('loadedmetadata', () => {
		      	    video.play();
		      	    rastrearRosto();
		      	  });
		      	}

		      	Promise.all([
		      	  faceapi.nets.tinyFaceDetector.loadFromUri(localModels),
		      	  faceapi.nets.faceLandmark68Net.loadFromUri(localModels),
		      	  faceapi.nets.faceRecognitionNet.loadFromUri(localModels),
		      	  faceapi.nets.faceExpressionNet.loadFromUri(localModels)
		      	]).then(iniciarVideo);

		      	async function rastrearRosto() {
		      	  const faceDetector = new faceapi.TinyFaceDetectorOptions();
		      	  const canvas = faceapi.createCanvasFromMedia(video);
		      	  const displaySize = { width: video.videoWidth, height: video.videoHeight };
		      	  faceapi.matchDimensions(canvas, displaySize);

		      	  videoContainer.style.width = `${displaySize.width}px`;
		      	  videoContainer.style.height = `${displaySize.height}px`;

		      	  let fotoTirada = false;
		      	  let tempoAteFoto = 5000;

		      	  intervaloContagemRegressiva = setInterval(async () => {
		      		  if (!contagemRegressivaAtiva) {
		      			    clearInterval(intervaloContagemRegressiva);
		      			    return;
		    			  }

		      	    const detections = await faceapi.detectAllFaces(video, faceDetector)
		      	      .withFaceLandmarks()
		      	      .withFaceExpressions();

		      	    if (detections.length > 0) {
		      	      const landmarks = detections[0].landmarks;

		      	      const olhosEsquerdo = landmarks.getLeftEye();
		      	      const olhosDireito = landmarks.getRightEye();
		      	      const boca = landmarks.getMouth();
		      	      const nariz = landmarks.getNose();

		      	      if (olhosEsquerdo && olhosDireito && boca && nariz && !fotoTirada) {
		      	        if (tempoAteFoto <= 0) {
		      	          const fotoDataUrl = await capturarFoto();

		      	          // E necessario esta logica para garantir o recorte correto do rosto com a qualidade exigida pela aplicação externa
		      	          if (fotoDataUrl == null){
		      	        	document.getElementById("textoInformativo").innerText ='<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.retentativa", responsavel)%>'
		      	    		video.style.display = 'block';
		      	          	foto.style.display = 'none';

		                      if (stream) {
		                          stream.getTracks().forEach(track => track.stop());
		                      }

		      	            clearInterval(intervaloContagemRegressiva);
		      	          	contagemRegressivaAtiva = true;
		      	          	contagemRegressivaIniciada = true;
		      	            base64DataRecortada = null;
		      	          	tempoAteFoto = 5000;
		  	        	  	iniciarVideo();
		      	          } else {
		        	        foto.src = fotoDataUrl;
		        	        foto.style.display = 'block';
		        	        foto.style.borderRadius = '50%';
		        	        foto.style.objectFit = 'cover';
		        	        fotoTirada = true;
		        	        video.style.display = 'none';
		        	        contagemRegressivaAtiva = false;
		        	        if(base64DataRecortada != null){
		  		    			await enviarFotoReconhecimento(fotoDataUrl.replace(/^data:image\/[a-z]+;base64,/, ''));
		        	        }
		        	        clearInterval(intervaloContagemRegressiva);
		      	          }
		      	        } else {
		      	          countdownElement.textContent = Math.ceil(tempoAteFoto / 1000);
		      	          tempoAteFoto -= 100;
		      	        }
		      	      }
		      	      videoContainer.classList.remove('red-border-modalReconhecimentoFacial');
		      	      videoContainer.classList.add('green-border-modalReconhecimentoFacial');
		      	    } else {
		      	      videoContainer.classList.remove('green-border-modalReconhecimentoFacial');
		      	      videoContainer.classList.add('red-border-modalReconhecimentoFacial');
		      	    }
		      	  }, 100);
		      	}

		      	async function capturarFoto() {
		  		  const canvas = document.createElement('canvas');
		  		  const context = canvas.getContext('2d');
		  		  const { videoWidth, videoHeight } = video;

		  		  const scaleFactor = 1.2;

		  		  const targetWidth = scaleFactor * videoWidth;
		  		  const targetHeight = scaleFactor * videoHeight;

		  		  const offsetX = (targetWidth - videoWidth) / 2;
		  		  const offsetY = (targetHeight - videoHeight) / 2;

		  		  canvas.width = targetWidth;
		  		  canvas.height = targetHeight;
		  		  context.drawImage(video, -offsetX, -offsetY, targetWidth, targetHeight);

		  		  const base64Data = canvas.toDataURL('image/jpeg');

		  		  const detections = await faceapi.detectSingleFace(canvas, new faceapi.TinyFaceDetectorOptions({
		          })).withFaceLandmarks();
		  		
		  		  if (detections) {
		  		    const faceBoundingBox = detections.detection.box;
		  		    const { x, y, width, height } = faceBoundingBox;

		  		    const centerX = x + width / 2;
		  		    const centerY = y + height / 2;

		  		    const recorteWidth = width * scaleFactor;
		  		    const recorteHeight = height * scaleFactor;

		  		    const recorteX = centerX - recorteWidth / 2;
		  		    const recorteY = centerY - recorteHeight / 2;

		  		    const canvasRecorte = document.createElement('canvas');
		  		    const contextRecorte = canvasRecorte.getContext('2d');

		  		    canvasRecorte.width = recorteWidth;
		  		    canvasRecorte.height = recorteHeight;

		  		    contextRecorte.drawImage(canvas, recorteX, recorteY, recorteWidth, recorteHeight, 0, 0, recorteWidth, recorteHeight);

		  		    base64DataRecortada = canvasRecorte.toDataURL('image/jpeg');
		  		    return base64DataRecortada;
		  		  } else {
					return null;
				  }
		  		}

		      	function SugerirTentativa(textoHtml) {
		    		if (ocorreuErroRequisicao) {
		    		    document.getElementById("textoInformativo").innerText = textoHtml;
		    		    botaoTentarNovamente.style.display = '';
		    		  } else {
		    		    document.getElementById("textoInformativo").innerText = "";
		    		    botaoTentarNovamente.style.display = 'none';
		    		  }

		    		  videoContainer.classList.remove('green-border-modalReconhecimentoFacial');
		    		  videoContainer.classList.add('red-border-modalReconhecimentoFacial');

		    		  const loadingIconDiv = document.getElementById("loading-icon");

		    		  loadingIconDiv.innerText = "";

		    		  if (contagemRegressivaIniciada) {
		    		      contagemRegressivaIniciada = false;
		    		  }
		    		  quantidadeTentativas++;
		    		  return;
		      	}

		      	async function reiniciarCaptura() {
		      		if (contagemRegressivaIniciada) {
		    		    return;
		    		  }

		            if(quantidadeTentativas == 1) {
		    			document.getElementById("textoInformativo").innerText ='<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.tentativa", responsavel)%>'
		            } else if(quantidadeTentativas >= 2) {
		            	document.getElementById("textoInformativo").innerText ='<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.tentativa.final", responsavel)%>'
		                clearInterval(intervaloContagemRegressiva);
		               	botaoTentarNovamente.style.display = 'none'; 	
		            	return;
		            }
		            botaoTentarNovamente.style.display = 'none'; 	
		            clearInterval(intervaloContagemRegressiva);
		            contagemRegressivaAtiva = true;
		            contagemRegressivaIniciada = true;
		            video.style.display = 'block';
		            foto.style.display = 'none';
		            iniciarVideo();
		  		}

		      	function fecharModal() {
		      	  clearInterval(intervaloContagemRegressiva);
		      	  document.getElementById("faceModal").removeEventListener('hide.bs.modal', forcaFechamentoModal);
		      	  $('#faceModal').modal('hide');
			      if (stream) {
	                  stream.getTracks().forEach(track => track.stop());
	              }
		      	}

		      	async function enviarFotoReconhecimento(fotoData) {
		      	  countdownElement.textContent = '';
		      	  document.getElementById("textoInformativo").innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.iniciando", responsavel)%>'
		      	  const loadingIconDiv = document.getElementById("loading-icon");
		      	  const spinnerDiv = document.createElement("div");
		      	  spinnerDiv.classList.add("spinner-border");
		      	  spinnerDiv.setAttribute("role", "status");

		      	  loadingIconDiv.appendChild(spinnerDiv);

		      	  try {
		      	    const url = '../v3/reconhecimentoFacial?acao=registrar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_';

		      	    const response = await fetch(url, {
		      	      method: 'POST',
		      	      headers: {
		      	        'Content-Type': 'application/json'
		      	      },
		      	      body: JSON.stringify({
		      	        fotoFace: fotoData,
		      	        cpf: '<%=responsavel.getSerCpf()%>'
		      	      })
		      	    });

		      	    if (response.status === 200) {
		      	    	verificarReconhecimentoFacial();
		      	    } else {
		      	      ocorreuErroRequisicao = true;
		      	    }
		      	  } catch (error) {
		      	      ocorreuErroRequisicao = true;
		      	    SugerirTentativa('<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.erro", responsavel)%>');
		      	  } finally {
		      	    loadingIconDiv.removeChild(spinnerDiv);
		      	  }
		      	}
		      	
		      	async function verificarReconhecimentoFacial() {
		      	  let continuarAguardandoVerificacao = false;
		      	  while (!continuarAguardandoVerificacao) {
		      	    try {
						  const url = '../v3/reconhecimentoFacial?acao=verificar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_';

		            	  const response = await fetch(url, {
		          	        method: 'POST',
		          	        headers: {
		          	          'Accept': 'application/json'
		          	        }
		          	      });

		          	      if (response.status === 202) {
		            	      continuarAguardandoVerificacao = true;
		            	      document.getElementById("textoInformativo").innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.aprovado", responsavel)%>';
		            	      videoContainer.classList.remove('red-border-modalReconhecimentoFacial');
		            		  videoContainer.classList.add('green-border-modalReconhecimentoFacial');
		            	      setTimeout(fecharModal, 1000);
		            	      <%if(!simularConsignacaoComReconhecimentoFacialELiveness && reconhecimentoFacialLivenssGeracaoSenhaAutorizacao){%>
		            	      	postData('../v3/modificarSenhaAutorizacao?acao=alterar&' + '<%=SynchronizerToken.generateToken4URL(request)%>');
		            	      <%}%>
		            	      
		            	      <%if(simularConsignacaoComReconhecimentoFacialELiveness){%>
		            	      if (stream) {
		                          stream.getTracks().forEach(track => track.stop());
		                      }
		            	      f0.senha.value = document.getElementById('otpGeradoParaReconhecimentoFacial').value;
		            	  	
			            	  if (!vf_upload_arquivos() || !vf_valida_dados() || !verificaEmail()) {
		            	         return false;
		            	      }

		            	  	  mensagemConfirmacao();
		            	      <%}%>
		          	      } else if (response.status === 200) {
		          	    	  continuarAguardandoVerificacao = true;      	    	
		                      ocorreuErroRequisicao = true;
		                      contagemRegressivaIniciada = true;
		              	      SugerirTentativa('<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.reprovado", responsavel)%>');
		          	      } else if (response.status === 406) {
		            	      document.getElementById("textoInformativo").innerText = '<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.aguarde", responsavel)%>';
		          	          ocorreuErroRequisicao = true;
		                      contagemRegressivaIniciada = true;
		                      await ajudaDimuirQntVerificacao(6000);
		          	      }
		      	    } catch (error) {
     	      	      continuarAguardandoVerificacao = true;      	    	
		      	      ocorreuErroRequisicao = true;
		              contagemRegressivaIniciada = true;
		      	      SugerirTentativa('<%=ApplicationResourcesHelper.getMessage("mensagem.info.reconhecimento.facial.erro", responsavel)%>');
		      	    }
		      	  }
		      	}
		      	
		      	function ajudaDimuirQntVerificacao(ms) {
		      	  return new Promise(resolve => setTimeout(resolve, ms));
		      	}
	 }
 </script>
 <% } %>
 
 <%if(reconhecimentoFacialServidor && !simularConsignacaoComReconhecimentoFacialELiveness){ %>
    <script type="text/JavaScript">
    $(document).ready(function() {
   	  executarReconhecimentoFacial();
	});
    </script>
 <%} %>
 
 <script type="text/JavaScript">
   var modalContatoAberto = false;
   function openModalQRCode(tipoContato, vlrContato) {
            var modalContato = new bootstrap.Modal(document.getElementById('contatoModal'), {
              keyboard: false
            });

            if(modalContatoAberto){
              document.getElementById("qrcode").innerText = "";
              var node = document.getElementById("valorContatoText");
              if (node.parentNode) {
                node.parentNode.removeChild(node);
              }
              modalContatoAberto = false;
            }

            modalContato.show();
            modalContatoAberto = true;

            var urlContato = "";
            if(tipoContato === 1){
              urlContato = "https://wa.me/" + vlrContato;
            } else if (tipoContato === 2){
              urlContato = "mailto:" + vlrContato;
            } else {
              urlContato = "tel:+" + vlrContato;
            }

            var qrcode = new QRCode(document.getElementById("qrcode"), {
              text: urlContato,
              width: 128,
              height: 128
            });

            var textoCentralizado = document.createElement("div");
            textoCentralizado.className = "text-center";
            textoCentralizado.id = "valorContatoText";
            if(tipoContato === 1){
              textoCentralizado.textContent = "<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.whatsapp", responsavel)%>" + ": " + vlrContato;
          } else if (tipoContato === 2){
              textoCentralizado.textContent = "<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.email.contato", responsavel)%>" + ": " + vlrContato;
            } else {
              textoCentralizado.textContent = "<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.telefone", responsavel)%>" + ": " + vlrContato;
            }

            document.getElementById("valorContato").appendChild(textoCentralizado);
          }

   	<% if (ParamSist.getBoolParamSist(CodedValues.TPC_ENCERRA_SESSAO_NAVEGACAO_NOVA_ABA, responsavel) && !responsavel.isSup()) {%>
	   	<%-- A lógica abaixo forço o logout no sistema quando o usuário fechou o sistema via aba e tenta novamente acessar por outra aba
	   	para isso criamos uma chave na sessão do navegador e também um identificador da aba para garantimos que em uma nova aba, ele refaça o login --%>

	   	const SESSION_KEY = "uniqueSession";
	   	const TAB_ID = "tabId";
	   	const SESSION_INITIALIZED_FLAG = "sessionInitialized";
	   	const LAST_TAB_ID = "lastTabId";
	
	   	if (!sessionStorage.getItem(TAB_ID)) {
	   	    const novoTabId = Date.now();
	   	    sessionStorage.setItem(TAB_ID, novoTabId);
	   	    localStorage.setItem(LAST_TAB_ID, novoTabId);
	   	}
	
	   	const tabId = sessionStorage.getItem(TAB_ID);
	   	const sessaoAtiva = localStorage.getItem(SESSION_KEY);
	   	const sessaoInicializada = localStorage.getItem(SESSION_INITIALIZED_FLAG);
	   	const lastTabId = localStorage.getItem(LAST_TAB_ID);
	
	   	if (!sessaoInicializada) {
	   	    localStorage.setItem(SESSION_KEY, tabId);
	   	    localStorage.setItem(SESSION_INITIALIZED_FLAG, "true");
	   	} else {
	   	    if (!tabId && lastTabId) {
	   	        sessionStorage.setItem(TAB_ID, lastTabId);
	   	    }
	
	   	    if (sessaoAtiva !== tabId) {
	   	        sessionStorage.removeItem(TAB_ID);
	   	        localStorage.removeItem(SESSION_KEY);
	   	        localStorage.removeItem(SESSION_INITIALIZED_FLAG);
	   	        localStorage.removeItem(LAST_TAB_ID);
	
	   	        $.ajax({
	   	            url: "../v3/expirarSistemaAjax",
	   	            type: "POST"
	   	        });
	
	   	        postData('../v3/expirarSistema?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>', '_top');
	   	    }
	   	}
   	<% }%>
  </script>
  <noscript>
    <hl:message key="mensagem.navegador.sem.javascript" />
  </noscript>
  <iframe id="logoutOauth2" frameBorder="0"></iframe>
</body>
</html>
