<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.web.ServicoSolicitacaoServidor"%>
<%@ taglib prefix="hl" uri="/html-lib"  %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String ajudaCampoCaptcha = ApplicationResourcesHelper.getMessage("ajuda.campo.captcha", responsavel);
boolean exibeCaptcha = (boolean) request.getAttribute("exibeCaptcha");
boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
boolean exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
  %>
<c:set var="title">
  <hl:message key="rotulo.integrar.credito.trabalhador"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form name="form1" method="POST" action="integrarCreditoTrabalhador?" enctype="multipart/form-data">
    <div class="row shortcut-btns">
      <div class="col-md-6 col-lg-4 col-xl-3">
        <a class="btn" href="#integraCreditoTrabalhadorModal" data-bs-toggle="modal" onclick="setup('upload', 'creditoTrabalhador');" aria-label="<hl:message key="rotulo.integracao.orientada.dashboard.retorno.ajuda"/>">
          <svg width="51">
            <use xlink:href="#i-integrar"></use></svg>Integrar Credito <strong>Trabalhador</strong></a>
        </a>
      </div>
    </div>
    <div class="btn-action">        
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
      <!-- Modal Selecionar Arquivo -->
    <div class="modal fade" id="integraCreditoTrabalhadorModal" tabindex="-1" role="dialog" aria-labelledby="selecionarArquivoModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-wide-content" role="document">
          <div class="modal-content">
              <div class="modal-header pb-0">
                  <span class="modal-title about-title mb-0" id="selecionarArquivoModalLabel"></span>
                  <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                      <span aria-hidden="true">&times;</span>
                  </button>
              </div>
              <div class="modal-body">
                  <div class="row">
                      <div class="form-group col-sm-12">
                          <label for="arquivo"><hl:message key="rotulo.upload.arquivo.arquivo"/></label>
                          <input type="file" class="form-control" id="FILE1" name="FILE1">
                      </div>
                  </div>
      <div class="row">
        <%if (exibeCaptcha || exibeCaptchaDeficiente) {%>
        <div class="form-group col-sm-5">
          <label for="captcha"><hl:message key="rotulo.captcha.codigo" />:</label> <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
        </div>
        <%} %>
        <div class="form-group col-sm-6">
          <div class="captcha">
            <%if (exibeCaptcha) { %>
            <img name="captcha_img"
              src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>"
              alt='<hl:message key="rotulo.captcha.codigo"/>'
              title='<hl:message key="rotulo.captcha.codigo"/>' /> <a
              href="#no-back" onclick="reloadCaptcha()"><img
              src="../img/icones/refresh.png"
              alt='<hl:message key="rotulo.captcha.novo.codigo"/>'
              title='<hl:message key="rotulo.captcha.novo.codigo"/>'
              border="0" /></a> <a href="#no-back" class="btn-i-right pr-1"
              data-bs-toggle="popover"
              title="<hl:message key="rotulo.ajuda" />"
              data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
              data-original-title=<hl:message key="rotulo.ajuda" />> <img
              src="../img/icones/help.png"
              alt='<hl:message key="rotulo.ajuda" />'
              title='<hl:message key="rotulo.ajuda" />' border="0">
            </a>
            <% } else if (exibeCaptchaAvancado) { %>
            <hl:recaptcha />
            <% } else if (exibeCaptchaDeficiente) { %>
            <div id="divCaptchaSound"></div>
            <a href="#no-back" onclick="reloadSimpleCaptcha()"><img
              src="../img/icones/refresh.png"
              alt='<hl:message key="rotulo.captcha.novo.audio"/>'
              title='<hl:message key="rotulo.captcha.novo.audio"/>'
              border="0" /></a> <a href="#no-back" onclick="helpCaptcha3();"><img
              src="../img/icones/help.png"
              alt='<hl:message key="rotulo.ajuda"/>'
              title='<hl:message key="rotulo.ajuda"/>' border="0" /></a>
            <% } %>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-footer pt-0">
      <div class="btn-action mt-2 mb-0">
        <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>">
          <hl:message key="rotulo.botao.cancelar" />
            </a>
              <a class="btn btn-primary" aria-label="<hl:message key="rotulo.botao.confirmar"/>" href="#" onClick="if(vf_upload_arquivos()){ f0.submit(); } return false;">
              <svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/>
              </a>
      </div>
    </div>
   </div>
  </div>
</div>

    <!-- Modal aguarde -->
    <div class="modal fade" id="modalAguarde" tabindex="-1" role="dialog" aria-labelledby="modalAguardeLabel" aria-hidden="true">
      <div class="modal-dialog-upload modal-dialog" role="document">
          <div class="modal-content">
              <div class="modal-body">
                  <div class="row">
                      <div class="col-md-12 d-flex justify-content-center">
                          <img src="../img/loading.gif" class="loading">
                      </div>
                      <div class="col-md-12">
                          <div class="modal-body"><span id="modalAguardeLabel"><hl:message key="mensagem.integracao.orientada.aguarde.upload"/></span></div>
                      </div>
                </div>
            </div>
        </div>
      </div>
    </div>

  </form>
</c:set>
<c:set var="javascript">

<link href="../node_modules/ekko-lightbox/dist/ekko-lightbox.css" rel="stylesheet">
<script src="../node_modules/ekko-lightbox/dist/ekko-lightbox.min.js?<hl:message key="release.tag"/>"></script>
<% if (exibeCaptchaAvancado) { %>
<script src='https://www.google.com/recaptcha/api.js'></script>
<script type="text/JavaScript">
setInterval(function () {
    $("iframe[title*='recaptcha' i]").parent().parent().addClass('recaptcha_challenge');
}, 1000);
</script>
<% } %>

<%if (exibeCaptchaDeficiente) {%>
<script type="text/JavaScript">
  montaCaptchaSom();
</script>
<%}%>

<script type="text/JavaScript">

    var f0 = document.forms[0];

    var f0baseAction = f0.action;
    
    function setup(acao, tipo) {
        titulo = '<hl:message key="rotulo.integrar.credito.trabalhador"/>';
        jQuery('#integraCreditoTrabalhadorDiv').show();
        $('#integraCreditoTrabalhadorModal').modal('show');
        jQuery('#selecionarArquivoModalLabel').text(titulo);

        setAction(acao, tipo, '');
    }

    function setAction(acao, tipo, extras) {
        f0.action = f0baseAction + 'acao=' + acao + '&tipo=' + tipo + extras;
    }

    function vf_upload_arquivos() {
        var isUpload = !jQuery('#FILE1').prop("disabled");
        var ok = true;
        if (isUpload) {
            var controles = new Array("FILE1");
            var msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.arquivo", responsavel)%>',
                                  '<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.tipo.arquivo", responsavel)%>');
            ok = ValidaCampos(controles, msgs);
            if (ok) {
                $('#modalAguarde').modal({
                    backdrop: 'static',
                    keyboard: false
                });
            }
        }
        if (f0.captcha && f0.captcha.type == "text") {
            f0.captcha.type = "hidden";
        }
        return ok;
    }

</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>