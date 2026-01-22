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
  List<String> tutorialList = (List<String>) request.getAttribute("tutorialList");
  String ajudaCampoCaptcha = ApplicationResourcesHelper.getMessage("ajuda.campo.captcha", responsavel);
  boolean exibeCaptcha = (boolean) request.getAttribute("exibeCaptcha");
  boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
  boolean exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
    <hl:message key="rotulo.integracao.orientada.titulo"/>
</c:set>
<c:set var="bodyContent">
    <c:if test="${temProcessoRodando}">
    <div class="alert alert-info" role="alert"><hl:message key="mensagem.integracao.orientada.aguarde.reload"/> <b><span id="timeoutTempoRestante"></span></b></div>
    </c:if>
    <form name="form1" method="POST" action="integrarFolha?" enctype="multipart/form-data">
        <%=SynchronizerToken.generateHtmlToken(request)%>
        <input name="tipo" id="tipo" type="hidden" value="">
        <c:if test="${not empty arqManual}">
        <div class="dashboard-header">
            <c:choose>
               <c:when test="${modoIntegrarFolha == 'acessoInicial'}">
                   <div class="dashboard-header-title"><hl:message key="rotulo.integracao.orientada.dashboard.comeceAqui.cabecalho.titulo"/></div>
                   <div class="dashboard-header-content"><hl:message key="rotulo.integracao.orientada.dashboard.comeceAqui.cabecalho"/></div>
               </c:when>
               <c:otherwise>
                   <div class="dashboard-header-title"><hl:message key="rotulo.integracao.orientada.dashboard.cabecalho.titulo"/></div>
                   <div class="dashboard-header-content"><hl:message key="rotulo.integracao.orientada.dashboard.cabecalho"/></div>
               </c:otherwise>
            </c:choose>
        </div>
        <!-- Manual de orientacao -->
        <div class="btn-action">
            <a class="btn btn-primary ${temProcessoRodando ? 'disabled' : ''}" aria-label="<hl:message key="rotulo.integracao.orientada.dashboard.download.manual.ajuda"/>" href="#" onClick="downloadManual('${arqManual}'); return false;">
                <svg width="17"><use xlink:href="#i-download"></use></svg>
                <span data-bs-toggle="tooltip" title="<hl:message key="rotulo.integracao.orientada.dashboard.download.manual.ajuda"/>">
                    <hl:message key="rotulo.integracao.orientada.dashboard.download.manual"/>
                </span>
            </a>
        </div>
        </c:if>
        <!-- Redireciona para a pagina de lista de arquivos de download de integracao -->
        <div class="row shortcut-btns">
            <c:if test="${modoIntegrarFolha != 'acessoInicial'}">
            <div class="col-md-6 col-lg-4 col-xl-3">
                <a class="btn ${temProcessoRodando ? 'disabled' : ''}" href="#no-back" onClick="postData('../v3/listarArquivosDownloadIntegracao?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')">
                    <svg width="51"><use xlink:href="#i-arquivo-eventos"></use></svg>
                    <span data-bs-toggle="tooltip" title="<hl:message key="rotulo.integracao.orientada.dashboard.download.movimento.ajuda"/>">
                        <hl:message key="rotulo.integracao.orientada.dashboard.download.movimento"/>
                    </span>
                </a>
            </div>
            </c:if>

            <!-- Colaborador -->
            <div class="col-md-6 col-lg-4 col-xl-3">
              <% if (tutorialList != null && !tutorialList.isEmpty()) { %>
                <a class="btn ${temProcessoRodando ? 'disabled' : ''}" href="#manualModal" data-bs-toggle="modal" onclick="setup('upload', 'margem', '${margem_arquivoPendente}', 'colaborador');" aria-label="<hl:message key="rotulo.integracao.orientada.dashboard.margem.ajuda"/>">
              <%}else{ %>
                <a class="btn ${temProcessoRodando ? 'disabled' : ''}" href="#selecionarArquivoModal" data-bs-toggle="modal" onclick="setup('upload', 'margem', '${margem_arquivoPendente}', 'colaborador');" aria-label="<hl:message key="rotulo.integracao.orientada.dashboard.margem.ajuda"/>">
              <%}%>
                    <svg width="43"><use xlink:href="#i-colaborador"></use></svg>
                    <span data-bs-toggle="tooltip" title="<hl:message key="rotulo.integracao.orientada.dashboard.margem.ajuda"/>">
                       <hl:message key="rotulo.integracao.orientada.dashboard.margem"/><c:if test="${not empty margem_arquivoPendente}"><span class="rotulo-pendente"><hl:message key="rotulo.integracao.orientada.pendente"/></span></c:if>
                    </span>
                 </a>
            </div>

            <c:if test="${modoIntegrarFolha != 'acessoInicial'}">
            <!-- Desconto -->
            <div class="col-md-6 col-lg-4 col-xl-3">
                <a class="btn ${temProcessoRodando ? 'disabled' : ''}" href="#selecionarArquivoModal" data-bs-toggle="modal" onclick="setup('upload', 'retorno', '${retorno_arquivoPendente}', 'desconto');" aria-label="<hl:message key="rotulo.integracao.orientada.dashboard.retorno.ajuda"/>">
                    <svg width="51"><use xlink:href="#i-arquivo-desconto"></use></svg>
                    <span data-bs-toggle="tooltip" title="<hl:message key="rotulo.integracao.orientada.dashboard.retorno.ajuda"/>">
                        <hl:message key="rotulo.integracao.orientada.dashboard.retorno"/><c:if test="${not empty retorno_arquivoPendente}"><span class="rotulo-pendente"><hl:message key="rotulo.integracao.orientada.pendente"/></span></c:if>
                    </span>
                </a>
            </div>
            </c:if>
        </div>
        <c:if test="${habilitaAmbienteDeTestes and modoIntegrarFolha != 'acessoInicial'}">
        <div class="row shortcut-btns ambiente-testes">
            <div class="col-md-6 col-lg-4 col-xl-3">
                <a class="btn ${temProcessoRodando ? 'disabled' : ''}" href="#" onclick="gerarHistorico(${temHistorico});"
                   aria-label="<hl:message key="rotulo.integracao.orientada.dashboard.historico.ajuda"/>">
                    <svg width="51"><use xlink:href="#i-card-id"></use></svg>
                    <span data-bs-toggle="tooltip" title="<hl:message key="rotulo.integracao.orientada.dashboard.historico.ajuda"/>">
                        <hl:message key="rotulo.integracao.orientada.dashboard.historico"/>
                    </span>
                </a>
            </div>
            <div class="col-md-6 col-lg-4 col-xl-3">
                <a class="btn ${temProcessoRodando ? 'disabled' : ''}" href="#" onclick="if(confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.exportar.movimento", responsavel)%>')){ setup('processar', 'movimento', ''); f0.submit(); } return false;"
                   aria-label="<hl:message key="rotulo.integracao.orientada.dashboard.movimento.ajuda"/>">
                    <svg width="51"><use xlink:href="#i-simular"></use></svg>
                    <span data-bs-toggle="tooltip" title="<hl:message key="rotulo.integracao.orientada.dashboard.movimento.ajuda"/>">
                        <hl:message key="rotulo.integracao.orientada.dashboard.movimento"/>
                    </span>
                </a>
            </div>
        </div>
        </c:if>
        <div class="dashboard-footer">
            <c:choose>
               <c:when test="${modoIntegrarFolha == 'acessoInicial'}">
                   <div class="dashboard-footer-title"><hl:message key="rotulo.integracao.orientada.dashboard.comeceAqui.rodape.titulo"/></div>
                   <div class="dashboard-footer-content"><hl:message key="rotulo.integracao.orientada.dashboard.comeceAqui.rodape"/></div>
               </c:when>
               <c:otherwise>
                  <div class="dashboard-footer-title"><hl:message key="rotulo.integracao.orientada.dashboard.rodape.titulo"/></div>
                  <div class="dashboard-footer-content"><hl:message key="rotulo.integracao.orientada.dashboard.rodape"/></div>
               </c:otherwise>
            </c:choose>
        </div>

        <!-- Modal Selecionar Arquivo -->
        <div class="modal fade" id="selecionarArquivoModal" tabindex="-1" role="dialog" aria-labelledby="selecionarArquivoModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-wide-content" role="document">
                <div class="modal-content">
                    <div class="modal-header pb-0">
                        <span class="modal-title about-title mb-0" id="selecionarArquivoModalLabel"></span>
                        <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                      <div id='usarMargemDiv'>
                        <div class="row" id="manualModalTexto">
                          <div class="form-group col-sm-12">
                            <hl:message key="rotulo.integracao.orientada.servidor.tooltip.manual.ajuda"/>
                            <a aria-label="<hl:message key="rotulo.integracao.orientada.servidor.dashboard.download.manual"/>" href="#" id='arquivoManual' onClick="downloadManual('<hl:message key="rotulo.integracao.orientada.servidor.nome.manual"/>'); return false;">
                              <hl:message key="rotulo.integracao.orientada.servidor.tooltip.manual.ajuda.link"/>
                            </a>
                          </div>
                        </div>
                      </div>
                      <div id='usarRetornoDiv'>
                        <div class="row" id="manualModalTexto">
                          <div class="form-group col-sm-12">
                            <hl:message key="rotulo.integracao.orientada.desconto.tooltip.manual.ajuda"/>
                            <a aria-label="<hl:message key="rotulo.integracao.orientada.desconto.dashboard.download.manual"/>" href="#" id='arquivoManual' onClick="downloadManual('<hl:message key="rotulo.integracao.orientada.desconto.nome.manual"/>'); return false;">
                              <hl:message key="rotulo.integracao.orientada.desconto.tooltip.manual.ajuda.link"/>
                            </a>
                          </div>
                        </div>
                      </div>
                        <div class="row" id="usarArquivoPrevioDiv">
                          <div class="form-group col-sm">
                            <div><hl:message key="mensagem.integracao.orientada.usar.arquivo.previo"/></div>
                            <hl:htmlinput classe="form-check-input ml-1" type="checkbox" name="usarArquivoPrevio" di="usarArquivoPrevio" onClick="toggleUploadProcessar(this.checked);" mask="#*200" others="CHECKED" />
                            <label class="form-check-label" for="usarArquivoPrevio"><hl:message key="rotulo.integracao.orientada.usar.arquivo.previo"/></label>
                          </div>
                        </div>
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

        <!--  Modal Manuais -->
        <div class="modal fade" id="manualModal" tabindex="-1" role="dialog" aria-labelledby="manualModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-wide-content" role="document">
                <div class="modal-content">
                    <div class="modal-header pb-0">
                        <span class="modal-title about-title mb-0" id="manualModalLabel"><hl:message key="rotulo.integracao.orientada.historico"/></span>
                        <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                      <div id='usarHistoricoDiv'>
                        <div class="row" id="manualModalTexto"><div class="form-group col-sm-12"><hl:message key="rotulo.integracao.orientada.historico.tooltip.manual.ajuda"/></div></div>
                        <div class="row">
                           <div class="form-group col-sm-12">
                              <div class="btn-action" style='text-align:center'>
                                  <span data-bs-toggle="tooltip" title="<hl:message key="rotulo.integracao.orientada.historico.dashboard.download.manual"/>">
                                    <a class="btn btn-primary" aria-label="<hl:message key="rotulo.integracao.orientada.historico.dashboard.download.manual"/>" href="#" id='arquivoManual' onClick="downloadManual('<hl:message key="rotulo.integracao.orientada.historico.nome.manual"/>'); return false;">
                                      <hl:message key="rotulo.integracao.orientada.baixe.agora"/>
                                    </a>
                                  </span>
                              </div>
                            </div>
                        </div>
                      </div>
                    </div>
                </div>
            </div>
        </div>

    </form>
    <%if (tutorialList != null && !tutorialList.isEmpty()) { %>
    <% for(int i =0 ;i < tutorialList.size();i++){ %>
      <div <%if(i == 0){%>id="tutorial"<%}%> data-bs-toggle="lightbox" data-gallery="tutorial-gallery" data-remote="<%=TextHelper.forHtmlAttribute("../img/view.jsp?nome=historicocontratos/tutorial/"+tutorialList.get(i))%>"></div>
    <% } %>
    <%} %>

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
    var tipoAux = '', nomeArquivoPrevioAux = '';

	function setup(acao, tipo, nomeArquivoPrevio, modal) {
    	nomeArquivoPrevioAux = nomeArquivoPrevio;
        var arquivoPrevio = (nomeArquivoPrevio != '');
        var titulo = 'erro';
        var tmodal = 'titulo';
        if (tipo === 'historico'){
            jQuery('#usarHistoricoDiv').show();
        } else if (tipo === 'margem') {
        	//servidores/colaborador
            titulo = '<hl:message key="rotulo.integracao.orientada.margem"/>';
            jQuery('#usarMargemDiv').show();
            jQuery('#usarRetornoDiv').hide();
        } else if (tipo === 'retorno') {
        	//retorno da folha
            titulo = '<hl:message key="rotulo.integracao.orientada.retorno"/>';
            jQuery('#usarRetornoDiv').show();
            jQuery('#usarMargemDiv').hide();
        }
        jQuery('#selecionarArquivoModalLabel').text(titulo);
        if (arquivoPrevio) {
            jQuery('#usarArquivoPrevioDiv').show();
        } else {
            jQuery('#usarArquivoPrevioDiv').hide();
        }
        setAction(acao, tipo, '');
        if (acao == 'upload') {
            toggleUploadProcessar(arquivoPrevio);
        }
    	$("#manualModal").on("hidden.bs.modal", function () {
    	    if (modal === 'colaborador'){
    	    	setup('upload', 'margem', '${margem_arquivoPendente}', '');
                $('#selecionarArquivoModal').modal('show');
    	    }
    	});
    }

    function setAction(acao, tipo, extras) {
        tipoAux = tipo;
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
    function toggleUploadProcessar(valor) {
        jQuery('#FILE1').prop("disabled", valor);
        if (valor) {
            setAction('selecionarArquivo', tipoAux, '&nomeArquivo=' + nomeArquivoPrevioAux);
        } else {
            setAction('upload', tipoAux, '');
        }
    }
    function selecionarArquivo(tipo, nomeArquivo) {
        if (confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.selecionar.arquivo", responsavel)%>')) {
            f0.action = f0baseAction + 'acao=selecionarArquivo&tipo=' + tipo + '&nomeArquivo=' + nomeArquivo;
            f0.submit();
        }
    }
    function excluirArquivo(tipo, nomeArquivo) {
        if (confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.excluir.arquivo", responsavel)%>\n\n' + nomeArquivo)) {
            f0.action = '../v3/excluirArquivo?&tipo=' + tipo + '&arquivo_nome=' + nomeArquivo + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>';
            f0.submit();
        }
    }
    function downloadManual(nmArquivo) {
    	var tipo = tipo;
    	var nmArquivo = nmArquivo;
    	$('#manualModal').modal('hide');
        postData('../v3/downloadArquivo?tipo=manualFolha&arquivo_nome=' + encodeURIComponent(nmArquivo) + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
    }
    <c:if test="${habilitaAmbienteDeTestes}">
    function gerarHistorico(temHistorico) {
        if (!temHistorico) {
            if (confirm('<hl:message key="mensagem.confirmacao.gerar.historico.aleatorio"/>')) {
                setAction('processar', 'historico', '');
                f0.submit();
            }
        } else {
            if (confirm('<hl:message key="mensagem.confirmacao.gerar.historico.aleatorio.apagar.existente"/>')) {
                setAction('processar', 'historico', '&reset=all');
                f0.submit();
            }
        }
    }
    </c:if>
    <c:if test="${temProcessoRodando}">
    var timeout = 30;
    function reload() {
        if (timeout <= 0) {
          f0.action = f0baseAction + 'acao=iniciar';
          f0.submit();
        } else {
          jQuery('#timeoutTempoRestante').text(timeout);
          timeout--;
          setTimeout(reload, 1000);
        }
    }
    setTimeout(reload, 1000);
    </c:if>

    <% if (tutorialList != null && !tutorialList.isEmpty()) { %>
    $(function () {
        $('#tutorial').ekkoLightbox({
        	wrapping: false,
            alwaysShowClose: true
        });
	});
	<% } %>

	function cleanFields() {
        if (f0.captcha && f0.captcha.type == "text") {
            f0.captcha.type = "hidden";
        }
        f0.submit();
    }


</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
