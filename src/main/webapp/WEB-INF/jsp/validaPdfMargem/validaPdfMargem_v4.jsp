<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page  import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String msgnErro = request.getAttribute(CodedValues.MSG_ERRO) != null ? (String) request.getAttribute(CodedValues.MSG_ERRO) : null;

String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
boolean exibeCaptcha = (Boolean) request.getAttribute("exibeCaptcha");
boolean exibeCaptchaAvancado = (Boolean) request.getAttribute("exibeCaptchaAvancado");
boolean exibeCaptchaDeficiente = (Boolean) request.getAttribute("exibeCaptchaDeficiente");
%>

<!DOCTYPE html>
<html lang="<%=LocaleHelper.getLocale()%>">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title><%=TextHelper.forHtmlContent(JspHelper.getNomeSistema(responsavel))%></title>
    <link href="<c:url value='/node_modules/bootstrap/dist/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/css/fonts/Google/family_Montserrat.css'/>" rel="stylesheet" type="text/css">
    <%-- Arquivos Especializads CSS --%>
          <% if ("v5".equals(versaoLeiaute)) { %>
          <link href="<c:url value='/css/econsig_v5.css'/>" rel="stylesheet" type="text/css">
          <link href="<c:url value='/css/custom_v5.css'/>" rel="stylesheet" type="text/css">
          <link href="<c:url value='/css/custom_v5_client.css'/>" rel="stylesheet" type="text/css">
          <% } else if ("v6".equals(versaoLeiaute)) { %>
          <link href="<c:url value='/css/econsig_v6.css'/>" rel="stylesheet" type="text/css">
          <link href="<c:url value='/css/custom_v6.css'/>" rel="stylesheet" type="text/css">
          <link href="<c:url value='/css/custom_v6_client.css'/>" rel="stylesheet" type="text/css">
          <% } else { %>
          <link href="<c:url value='/css/econsig.css'/>" rel="stylesheet" type="text/css">
          <link href="<c:url value='/css/custom_v4.css'/>" rel="stylesheet" type="text/css">
          <link href="<c:url value='/css/custom_v4_client.css'/>" rel="stylesheet" type="text/css">
          <% } %>
    <link href="<c:url value='/css/botui.min.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/css/botui-theme-default.css'/>" rel="stylesheet" type="text/css" />

  </head>
<body class="page-extern">
<div id="mensagemErro" class="alert alert-danger" style="display:none;" role="alert">
</div>
<div class="login">
<div class="login-logo">
     <img src="../img/view.jsp?nome=login/logo_cse.gif" alt="<hl:message key="rotulo.texto.alternativo.logo.sistema"/>">
</div>
  <span class="econsig"></span>
<h2 class="login-title"><%= TextHelper.forHtmlContent(JspHelper.getNomeSistema(responsavel)) %></h2>
        <%=JspHelper.msgSession(session, false)%>
        <form name="formValidar"
                  id="formValidar"
                  method="POST"
                  action="../v3/validaPdf?acao=validar"
                  autocomplete="off"
            >
<div class="form-group">
                    <label for="matricula">
                    <hl:message key="rotulo.matricula.singular"/>
                    </label>
                    <input name="matricula"
                           id="matricula"
                           type="text"
                           class="form-control"
                           placeholder='<hl:message key="mensagem.digite.matricula"/>'
                    />
                  </div>
                  <div class="form-group">
                                      <label for="cpf">
                                      <hl:message key="rotulo.cpf"/>
                                      </label>
                                      <input name="cpf"
                                             id="cpf"
                                             type="text"
                                             class="form-control"
                                             placeholder='<hl:message key="mensagem.digite.cpf"/>'
                                             oninput="mascara(this)"
                                      />
                                    </div>
                   <div class="form-group">
                            <label for="chave"><hl:message key="rotulo.chave"/></label>
                            <input type="text"
                                   class="form-control"
                                   id="chave"
                                   name="chave"
                                   placeholder='<hl:message key="mensagem.placeholder.digite.chave"/>'
                                   />
                          </div>
                                          <div class="row">
                                          <%
                                               if (!exibeCaptchaAvancado) {
                                           %>
                                            <div class="form-group col-md-12">
                                              <label for="captcha"><hl:message key="rotulo.captcha.codigo"/>:</label>
                                              <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
                                            </div>
                                          <%
                                              }
                                          %>
                                            <div class="form-group col-sm-12 pl-0">
                                              <div class="captcha pl-3">
                                                <%
                                                    if (exibeCaptcha) {
                                                %>
                                                  <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
                                                    <div>
                                                    <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
                                                    <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
                                                       data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
                                                       data-original-title=<hl:message key="rotulo.ajuda" />>
                                                      <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
                                                    </a>
                                                    </div>
                                                <%
                                                    }  else if (exibeCaptchaAvancado) {
                                                                            %>
                                                                                <hl:recaptcha />
                                                                            <%
                                                                                } else if (exibeCaptchaDeficiente) {
                                                                            %>
                                                                              <div id="divCaptchaSound"></div>
                                                                              <a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
                                                                              <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
                                                                            <%
                                                                                }
                                                                            %>

                                              </div>
                                            </div>
                                          </div>
                          <div class="row">
                                  <div class="col-sm">
                          <button id="btnOK"
                                            name="btnOK"
                                            class="btn btn-primary"
                                            form="formValidar"
                                            type="submit"
                                            >
                                      <svg width="17"><use xlink:href="../img/sprite.svg#i-consultar"></use>
                                      </svg>
                                      <hl:message key="rotulo.botao.consultar" />
                                    </button>
                                    </div>
                                    </div>
                          </form>
</div>
<% if (exibeCaptchaAvancado) { %>
     <script src='https://www.google.com/recaptcha/api.js'></script>
<% } %>
     <script src="<c:url value='/js/validalogin.js'/>?<hl:message key='release.tag'/>"></script>
     <script src="<c:url value='/js/scripts_2810.js'/>?<hl:message key='release.tag'/>"></script>
     <script src="<c:url value='/node_modules/bootstrap/dist/js/bootstrap.bundle.min.js'/>"></script>

     <script>
     function mascara(i){

        var v = i.value;

        if(isNaN(v[v.length-1])){
           i.value = v.substring(0, v.length-1);
           return;
        }

        i.setAttribute("maxlength", "14");
        if (v.length == 3 || v.length == 7) i.value += ".";
        if (v.length == 11) i.value += "-";

     }

     document.getElementById("formValidar").addEventListener("submit", function (e) {
         const matricula = document.getElementById("matricula").value.trim();
         const cpf = document.getElementById("cpf").value.trim();
         const chave = document.getElementById("chave").value.trim();
         const mensagemErro = document.getElementById("mensagemErro");

         if (!matricula || !cpf || !chave) {
           e.preventDefault();
           mensagemErro.style.display = "block";
           mensagemErro.innerText = "<hl:message key="rotulo.controle.validacao.preencha"/>";
         } else {
           mensagemErro.style.display = "none";
         }
       });

       <% if (msgnErro != null) { %>
         window.onload = function() {
                         const mensagemErro = document.getElementById("mensagemErro");
                         mensagemErro.style.display = "block";
                         mensagemErro.innerText = "<%=TextHelper.forHtmlContent(msgnErro)%>";
                         };
       <% } %>

       function formLoad() {
           <% if (exibeCaptchaDeficiente) {%>
           montaCaptchaSom();
           <% } %>
         }

         window.onload = formLoad;
     </script>
     <style>
     td {
         padding: 8px 12px;
     }
     </style>
</body>
</html>

