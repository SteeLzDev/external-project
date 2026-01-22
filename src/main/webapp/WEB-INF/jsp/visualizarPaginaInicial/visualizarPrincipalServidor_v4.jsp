<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.Short" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.web.ServicoSolicitacaoServidor"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.dto.entidade.MargemTO"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

// Só mostra a mensagem após o login do usuário ou se foi solicitado pelo menu Mensagem
boolean mostraMensagem = JspHelper.verificaVarQryStr(request, "mostraMensagem").equalsIgnoreCase("true");

// Todas as mensagens são listadas ou apenas uma quantidade X delas
boolean limitaMsg = JspHelper.verificaVarQryStr(request, "limitaMsg").equalsIgnoreCase("true");

// Um link para exibir todas as mensagens é exibido após o login
boolean mostraLink = JspHelper.verificaVarQryStr(request, "mostraLink").equalsIgnoreCase("true");

// Registra se o menu Mensagens foi selecionado
boolean menuMensagem = (request.getAttribute("menuMensagem") != null);
if (menuMensagem) {
    mostraMensagem = true;
}

//Exibição de aviso que o servidor está bloqueado mas tem permissão para visualizar margem
boolean servidorBloqueadoPermissaoVisualizarMargem = (request.getAttribute("servidorBloqueadoPermissaoVisualizarMargem") != null && ((boolean) request.getAttribute("servidorBloqueadoPermissaoVisualizarMargem")));

// Exibição do motivo do bloqueio do servidor
String servidorMotivoBloqueio = (String) request.getAttribute("servidorMotivoBloqueio");

boolean exibeBanner = (boolean) request.getAttribute("exibeBanner");
boolean haBannerNoSistema = (boolean) request.getAttribute("haBannerNoSistema");
String bannerName = (String) request.getAttribute("bannerName");

// Lista de serviços que o servidor pode solicitar
List<ServicoSolicitacaoServidor> servicosReserva = (List<ServicoSolicitacaoServidor>) request.getAttribute("servicosReserva");

// Constante que indica qtas mensagens devem ser mostradas 
int numMaxMsg = (int) request.getAttribute("numMaxMsg");
 
List<TransferObject> mensagens = (List<TransferObject>) request.getAttribute("mensagens");
int total = (int) request.getAttribute("total");

// Link para mostrar mais msgs é exibido 
if (mostraMensagem && limitaMsg && total > numMaxMsg) {
    mostraLink = true;
}
boolean exibeCaptchaAvancado = false;
boolean exibeCaptchaDeficiente = false;
boolean exigeCaptcha = false;
boolean registroServidorBloqueado = (boolean) request.getAttribute("registroServidorBloqueado");
if(ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDOR_TELA_PRINCIPAL, CodedValues.TPC_SIM, responsavel) 
        && (!registroServidorBloqueado || (registroServidorBloqueado && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_SER, CodedValues.TPC_SIM, responsavel)))){
   exigeCaptcha = (boolean) request.getAttribute("exigeCaptcha");
   exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
   exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
}

List<TransferObject> lstBannersTo = (List<TransferObject>) request.getAttribute("itensBannersCarousel");
List<TransferObject> lstNseTo = (List<TransferObject>) request.getAttribute("itensNseCarousel");

boolean exibeConfSistemaServidor = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CONFIG_SIST_SERVIDOR, responsavel);
String bannerCalculadoraMargem = !TextHelper.isNull(request.getAttribute("bannerCalculadoraMargem")) ? (String) request.getAttribute("bannerCalculadoraMargem") : null;
%>
<c:set var="imageHeader">
    <use xlink:href="#i-home"></use>  
</c:set>
<c:set var="title">
    <hl:message key="mensagem.principal.titulo"/>
</c:set>
<c:set var="bodyContent">
    <%--DESENV-16085: Rio de Janeiro - Mostrar Margem Servidor Bloqueado - Exibir aviso para o servidor bloqueado que o parâmetro do sistema que permite a exibição de margem para servidores está ativado --%>
    <%
      if (servidorBloqueadoPermissaoVisualizarMargem) {
      %>
        <div class="alert alert-warning" role="alert">
          <span id="idMsgInfoSession"><hl:message key="mensagem.informacao.situacao.servidor.bloqueado.papel.permitido.gerar.margem"/></span>
        </div>

        <%-- DESENV-16129 - Rio de Janeiro - Mostrar Motivo de Bloqueio do Servidor --%>
        <%
        if (servidorMotivoBloqueio != null) {
        %>
          <div class="alert alert-warning" role="alert">
            <span id="idMsgInfoSession"><%= servidorMotivoBloqueio %></span>
          </div>
        <%
        }
        %>
      <%
      }
    %>
    <hl:dashBoardv4 />

    <%--DESENV-15640: Portal de Benefícios - Inserir banner de publicidade e lojas credenciadas --%>
    <%  
       if (lstBannersTo != null && !lstBannersTo.isEmpty()) {
    %>
      <div class="row">
        <div id="multi-item-banners" class="carousel carousel-dark slide" data-bs-ride="carousel">
            <!--Indicators-->
            <ol class="carousel-indicators">                    
              <%
                 int numPgCarouselBanners = (Integer) request.getAttribute("numPgCarouselBanners"); 
                 int itensPorPgCarouselBanners = (Integer) request.getAttribute("itensPorPgCarouselBanners");
                 for (int i = 0; i < numPgCarouselBanners; i++) {%>
                   <li id="carousel-indicator-button" type="button" data-bs-target="#multi-item-banners" data-bs-slide-to="<%=i%>" <%if (i == 1) {%> class="active" aria-current="true"<%} %>></li>
              <% } %>
            </ol>
            <!--/.Indicators-->
            <div class="row">
              <div class="carousel-inner" role="listbox">
                <!--First slide-->
                <%int itemCarouselCount = 0;
                  for (int i = 0; i < numPgCarouselBanners; i++) { %>
                   <div class="carousel-item <%if (i == 0) { %> active<% } %>">
                     <div class="row d-flex justify-content-center">
                       <%for (int j = 1; j <= itensPorPgCarouselBanners; j++) { %>
                         <%if (itemCarouselCount < lstBannersTo.size()) {
                           TransferObject bannersTO = lstBannersTo.get(itemCarouselCount);
                         %>
                           <div class="col-sm-3 car-banners">
                             <div class="card card-image image-2 card-carossel mb-5" onClick="javascript: window.open('<%=TextHelper.forHtmlAttribute(bannersTO.getAttribute(Columns.BPU_URL_SAIDA))%>');">
                                 <img id="img_car_banners_<%=TextHelper.forHtmlAttribute(bannersTO.getAttribute(Columns.BPU_CODIGO))%>" class="card-img-top only" src="data:image/jpeg;charset=utf-8;base64,<%=TextHelper.forHtmlAttribute(bannersTO.getAttribute(Columns.ARQ_CONTEUDO))%>" alt="<%=TextHelper.forHtmlAttribute(bannersTO.getAttribute(Columns.BPU_DESCRICAO))%>">
                                 <div class="card-body text-center">
                                    <b><%= TextHelper.forHtml(bannersTO.getAttribute(Columns.BPU_DESCRICAO))%></b>
                                 </div>
                             </div>
                           </div>
                           <%itemCarouselCount++;%>
                         <% } %>
                       <% } %>
                     </div>
                   </div>
                <% } %>
              </div>                  
            </div>                            
            <button class="carousel-control-prev" href="#multi-item-banners" role="button" data-bs-slide="prev">
              <span class="carousel-control-prev-icon" aria-hidden="true"></span>
              <span class="sr-only">Previous</span>
            </button>
            <button class="carousel-control-next" href="#multi-item-banners" role="button" data-bs-slide="next">
              <span class="carousel-control-next-icon" aria-hidden="true"></span>
              <span class="sr-only">Next</span>
            </button>
         </div>
       </div>
     <%} %>
     
    <%--DESENV-11902: carousel de benefícios do portal público --%>
    <%  
       if (lstNseTo != null && !lstNseTo.isEmpty()) {
    %>
      <div class="row">
        <div class="col">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><%=request.getAttribute("tituloConhecaBeneficios") %></h2>
            </div>
            <div class="card-body pb-0 d-flex justify-content-center">
              <div id="multi-item-nse" class="carousel carousel-dark slide carousel-multi-item" data-ride="carousel">
                  <!--Indicators-->
                  <ol class="carousel-indicators">                    
                    <%
                       int numPgCarouselNse = (Integer) request.getAttribute("numPgCarouselNse"); 
                       int itensPorPgCarouselNse = (Integer) request.getAttribute("itensPorPgCarouselNse");
                       for (int i = 0; i < numPgCarouselNse; i++) {%>
                         <li id="carousel-indicator-button" type="button" data-bs-target="#multi-item-nse" data-bs-slide-to="<%=i%>" <%if (i == 1) {%> class="active" aria-current="true"<%} %>></li>
                    <% } %>
                  </ol>
                  <!--/.Indicators-->
                <div class="row">
                  <div class="carousel-inner" role="listbox">
                    <!--First slide-->
                    <%int itemCarouselCount = 0;
                      for (int i = 0; i < numPgCarouselNse; i++) {%>
                       <div id="<%=i%>" class="carousel-item <%if (i == 0) {%> active<%} %>">
                         <div class="row d-flex justify-content-center">
                           <%for (int j = 1; j <= itensPorPgCarouselNse; j++) {%>
                           
                             <%if (itemCarouselCount < lstNseTo.size()) {
                               TransferObject nseTO = lstNseTo.get(itemCarouselCount);
                               %>
                               <div class="col-sm-3">
                                 <div class="card card-image image-2 card-carossel mb-5" onClick="javascript:postData('../v3/fluxoPortal?nse_codigo=<%=TextHelper.forJavaScriptAttribute(nseTO.getAttribute(Columns.NSE_CODIGO))%>&<%=SynchronizerToken.generateToken4URL(request)%>')">
                                     <img id="img_car_beneficios_<%=TextHelper.forHtmlAttribute(nseTO.getAttribute(Columns.NSE_CODIGO))%>" class="card-img-top only" src="" alt="<%=TextHelper.forHtmlAttribute(nseTO.getAttribute(Columns.NSE_DESCRICAO))%>">
                                     <div class="card-body text-center">
                                       <b><%= TextHelper.forHtml(nseTO.getAttribute(Columns.NSE_DESCRICAO))%></b>
                                     </div>
                                 </div>
                               </div>
                               <%itemCarouselCount++; %>
                             <%} %>
                           
                           <%} %>
                         </div>
                       </div>
                    <% }%>
                  </div>
                </div>
                <a class="carousel-control-prev" href="#multi-item-nse" role="button" data-bs-slide="prev">
                  <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                  <span class="sr-only"><hl:message key="rotulo.botao.anterior"/></span>
                </a>
                <a class="carousel-control-next" href="#multi-item-nse" role="button" data-bs-slide="next">
                  <span class="carousel-control-next-icon" aria-hidden="true"></span>
                  <span class="sr-only"><hl:message key="rotulo.botao.proximo"/></span>
                </a>
              </div>
            </div>
          </div>
         </div>
       </div>
     <%} %>
     <div class="row">
      <div class="col-sm-6">
          <% if (responsavel.isSer() && servicosReserva != null && servicosReserva.size() > 1) { %>
            <div class="card">
              <div class="card-header hasIcon">
                <span class="card-header-icon"><svg width="24">
                    <use xlink:href="#i-box"></use></svg></span>
                <h2 class="card-header-title"><hl:message key="rotulo.solicitar.servico"/></h2>
              </div>
              <div class="card-body p-0">
                <ul class="list-links">
                  <% for (ServicoSolicitacaoServidor servico: servicosReserva) { %>
                    <li><a href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(servico.getLink())%>'); return false;"> <%=TextHelper.forHtmlContent(servico.getLabel())%></a></li>
                <% } %>
                </ul>
              </div>
           </div> 
          <% } %>
           </div>
          </div>
          <div class="row">
          <%
              if (com.zetra.econsig.web.servlet.ViewImageServlet.imageNotNullOrBlank("logo_cse.gif", responsavel)) {
          %>
            <div class="col-sm-12">
                <div class="w3-panel w3-card"><p align="center"><img src="../img/view.jsp?nome=logo_cse.gif"></p></div>
            </div>
          <% } %>
          
          <% if (!TextHelper.isNull(bannerCalculadoraMargem)) {%>
              <%=bannerCalculadoraMargem%>
          <%}%>
            </div>
            <div class="row">
              <div class="col-sm-6">
            <%--DESENV-17678: configurações do sistema - dia de corte --%>
            <% 
             if (responsavel.isSer() && exibeConfSistemaServidor) { %>
	               <hl:configsistemav4/>
            <% } %>
          
              <hl:infoLogAuditoriav4/>
          <% if (responsavel.isSer()) { %>
              <hl:infoServidorv4 rseCodigo="<%=TextHelper.forHtmlAttribute(responsavel.getRseCodigo())%>" />
          <% } %>
      </div>
      <div class="col-sm-6">
        <div class="card">
            <div class="card-header hasIcon">
              <span class="card-header-icon"><svg width="24">
                  <use xlink:href="#i-mensagem"></use></svg></span>
              <h2 class="card-header-title"><hl:message key="rotulo.comunicacao.mensagem"/></h2>
            </div>
            <div class="card-body p-0">
       <% if (mostraMensagem && mensagens != null && !mensagens.isEmpty()) { %>
              <div class="messages">
            <% for (TransferObject menTO : mensagens) { 
                String msgTitulo = menTO.getAttribute(Columns.MEN_TITULO).toString(); 
                String msgTelaInicial = menTO.getAttribute(Columns.MEN_TEXTO).toString(); 
                String menCodigo = menTO.getAttribute(Columns.MEN_CODIGO) != null ? menTO.getAttribute(Columns.MEN_CODIGO).toString() : null;
                String arqCodigo = menTO.getAttribute(Columns.ARQ_CODIGO) != null ? menTO.getAttribute(Columns.ARQ_CODIGO).toString() : null;

                if (msgTelaInicial != null && !msgTelaInicial.equals("")) {
                    msgTelaInicial = msgTelaInicial.replaceAll("increment_video\\[", "<iframe src=\"https://player.vimeo.com/video/");
                    msgTelaInicial = msgTelaInicial.replaceAll("\\]final",     "\" style=\"width: 100%; height: 40em; border:0;\" title=\"vimeo video\"></iframe>");                               
                    msgTelaInicial = msgTelaInicial.replaceAll("increment_url_", " https://vimeo.com/");
                %>
                  <div class="message">
                  <h6 class="message-title"><%=msgTitulo%></h6>
                  <p><%=(String)msgTelaInicial%></p>
                  <%if (menCodigo != null && arqCodigo != null) {%>
                  <div class="form-inline mt-5">
                    <a class="ico-action" onClick="fazDownload('<%=arqCodigo %>', '<%=menCodigo%>'); return false;" href="#">
                      <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.botao.download"/>" title="" data-original-title="download">
                        <svg class="icon-download-mensagem" width="26" height="18"> <use xlink:href="../img/sprite.svg#i-download"></use></svg>
                      </span>
                      <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                    </a>
                  </div>
                  <% } %>
                  </div>
            <% } %>
          <% } %>
            </div>
          <% if (mostraLink) { %>
              <div class="justify-content-left ml-3 mb-3">
                <a  class="btn btn-primary" href="#no-back" onClick="postData('../v3/carregarPrincipal?mostraMensagem=true&mostraLink=false&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')" onMouseOver="return true;" onMouseOut="'return true'"><hl:message key="mensagem.principal.exibir.mensagens"/></a>
              </div>
          <% } %>
      <% } else if (!mostraMensagem || mensagens == null || mensagens.isEmpty()) { %>
                <div class="messages">
                  <div class="message">
                    <h6 class="message-title"><hl:message key="mensagem.informacao.instrucoes"/></h6>
                    <p><hl:message key="mensagem.informacao.instrucoes.1"/></p>
                    <ul>
                      <li><hl:message key="mensagem.informacao.instrucoes.2"/></li>
                      <li><hl:message key="mensagem.informacao.instrucoes.3"/></li>
                      <li><hl:message key="mensagem.informacao.instrucoes.4.importante"/> : <hl:message key="mensagem.informacao.instrucoes.4"/></li>
                    </ul>
                 </div>
               </div>
       <% } %>
         </div>
        </div>
      </div>
    </div>
    <%if (exibeBanner && haBannerNoSistema) { %>
      <div class="modal fade" id="bannerPropaganda" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" style="display: none">
        <div class="modal-dialog modal-lg modalBannerPropaganda" role="document">
          <div class="modal-content">
            <div class="modal-body pb-0 pt-1 mb-3 mt-3 text-center">
             <img src="<%=TextHelper.forHtmlAttribute("../img/view.jsp?nome=banner/" + bannerName)%>" alt='<hl:message key="rotulo.principal.anuncio"/>' title='<hl:message key="rotulo.principal.anuncio"/>'>
            </div>
            <div class="modal-footer pt-0">
              <div class="btn-action mt-2 mb-0">
                <a class="btn btn-outline-danger" onclick="fecharModal()" href="#no-back" aria-label="<hl:message key="rotulo.botao.fechar"/>"><hl:message key="rotulo.botao.fechar"/></a>
              </div>
            </div>
          </div>
        </div>
      </div>
    <% } %>
    <% if(exigeCaptcha) { %>
    <hl:modalCaptchaSer type="principal"/>
    <%}%>

</c:set>
<c:set var="javascript">
  <link rel="stylesheet" href="<c:url value='/css/jquery.gridstrap.min.css'/>">
  <script src="<c:url value='/js/jquery.gridstrap.min.js'/>"></script>
  <script src="<c:url value='/node_modules/js-cookie/dist/js.cookie.min.js'/>"></script>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
    <script  src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
    <script  src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
    <script  src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
    <script  src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
    <script  src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
    <script  src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
    <script  src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
    <%if(exibeCaptchaAvancado){ %>
    <script src='https://www.google.com/recaptcha/api.js'></script>
    <% } %>
    <script type="text/JavaScript">
      if ('ontouchstart' in window) {
          $('#containerFavoritos').css('display','none');
      } else {
          $('#containerFavoritos').css('display','');
      }

    $(document).ready(function() {
  	    let idImgNse = '';
  		<% if (lstNseTo != null && !lstNseTo.isEmpty()) {		     
  		     for (TransferObject nse: lstNseTo) {
          %>
                  idImgNse = <%=nse.getAttribute(Columns.NSE_CODIGO)%>;
                  $.ajax({
                  	url: "../v3/imagemCarousel?nseCodigo=<%=nse.getAttribute(Columns.NSE_CODIGO)%>&_skip_history_=true",
              	    type: "POST",
              	    contentType: "image/jpeg",
              	    success: function(data) {
              	    	  $('#img_car_beneficios_<%=nse.getAttribute(Columns.NSE_CODIGO)%>').attr("src","data:image/jpeg;base64," + data); },
                  });
          <%   }
            } %>
    });

      // Módulos do sistema
      var tableModulosSistema;
      var columnsModulosSistema = [
          { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.plural", responsavel)%>" }
      ];

      tableModulosSistema = $(document).ready(function() {
          tableModulosSistema =  $('#tableModulosSistema').DataTable({
              "autoWidth": false,
              "order": [[ 0, 'asc' ]],
              "ajax": {
                  "url": "../v3/listarModulosSistema?acao=listar&_skip_history_=true",
                  "type": "POST",
                  "dataSrc": "tableDataSrc"
              },
              "pageLength": 5,
              "lengthMenu": [
                  [5, 10, 20, -1],
                  [5, 10, 20, '<hl:message key="mensagem.datatables.all"/>']
              ],
              "pagingType": "simple_numbers",
              columns: columnsModulosSistema,
              responsive: true,
              language: {
                  processing:        '<hl:message key="mensagem.datatables.processing"/>',
                  loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
                  lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
                  info:              '<hl:message key="mensagem.datatables.info"/>',
                  infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
                  infoFiltered:      '<hl:message key="mensagem.datatables.info.filtered"/>',
                  infoPostFix:       '',
                  zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
                  emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
                  paginate: {
                      first:         '<hl:message key="mensagem.datatables.paginate.first.padrao.econsig"/>',
                      previous:      '<hl:message key="mensagem.datatables.paginate.previous.padrao.econsig"/>',
                      next:          '<hl:message key="mensagem.datatables.paginate.next.padrao.econsig"/>',
                      last:          '<hl:message key="mensagem.datatables.paginate.last.padrao.econsig"/>'
                  },
                  aria: {
                      sortAscending: '<hl:message key="mensagem.datatables.aria.sort.ascending"/>',
                      sortDescending:'<hl:message key="mensagem.datatables.aria.sort.descending"/>'
                  },
                  buttons: {
                      print :        '<hl:message key="mensagem.datatables.buttons.print"/>',
                      colvis :       '<hl:message key="mensagem.datatables.buttons.colvis"/>'
                  },
                  decimal: ",",
              },
              initComplete: function () {
                  var btns = $('.dt-button');
                  btns.addClass('btn btn-primary btn-sm');
                  btns.removeClass('dt-button');
              }
          });

          $('#tableModulosSistema').append("<tfoot><tr><td colspan=\"4\">" + "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.listagem.modulos.sistema", responsavel)%>" + "</td></tr></tfoot>")
          $('#tableModulosSistema_paginate').addClass('p-3');
          $('#tableModulosSistema_length').addClass('p-3').addClass('mt-2');
          $('#tableModulosSistema_length').find('select').addClass('p-3');
          $('#tableModulosSistema_length').parent().addClass('col-md-12').addClass('col-lg-6');
          $("#tableModulosSistema_filter").hide();
          $('#tableModulosSistema_length').find('input').addClass('col-md-6').addClass('col-sm-8').addClass('col-lg-8');
          $('#tableModulosSistema_info').addClass('p-3');
          $('#tableModulosSistema_info').addClass('mt-2');
      });

    function fazDownload(codigo, menCodigo) {
        postData('../v3/manterMensagem?acao=downloadArquivo&arqCodigo=' + codigo + '&menCodigo=' + menCodigo + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
    }

      <% if(!exigeCaptcha) { %>
      function ocultaMargemCard() {
          var classe = document.getElementById('ocultaMargemCard').className
          if (classe == 'd-none'){
              document.getElementById('ocultaMargemCard').className = 'card-body';
              document.getElementById('exibeMargemCard').className = 'd-none';
              document.getElementById('olhoCard').className = 'd-none';
              document.getElementById('olhoCardOculto').className = '';
              Cookies.set("ocultaMargemCard", true);
          } else {
              document.getElementById('ocultaMargemCard').className = 'd-none';
              document.getElementById('exibeMargemCard').className = 'card-body';
              document.getElementById('olhoCardOculto').className = 'd-none';
              document.getElementById('olhoCard').className = '';
              Cookies.set("ocultaMargemCard", false);
          }
      }

      function ocultaMargemCardCookie() {
          var ocultaMargemCard = Cookies.get("ocultaMargemCard");
          if(ocultaMargemCard != null){
              if (ocultaMargemCard == "false"){
                  document.getElementById('ocultaMargemCard').className = 'card-body';
                  document.getElementById('exibeMargemCard').className = 'd-none';
                  document.getElementById('olhoCard').className = 'd-none';
                  document.getElementById('olhoCardOculto').className = '';
                  Cookies.set("ocultaMargemCard", true);
              } else {
                  document.getElementById('ocultaMargemCard').className = 'd-none';
                  document.getElementById('exibeMargemCard').className = 'card-body';
                  document.getElementById('olhoCardOculto').className = 'd-none';
                  document.getElementById('olhoCard').className = '';
                  Cookies.set("ocultaMargemCard", false);
              }
          }
      }
      <% } else { %>
      function ocultaMargemCard() {
          <% if (exibeCaptchaDeficiente) { %>
          montaCaptchaSomSer('principal');
          <% } %>
          $('#modalCaptcha_principal').modal('show');
      }

      function ocultaMargemCardCookie() {
          document.getElementById('olhoCard').className = 'd-none';
          document.getElementById('ocultaMargemCard').className = 'card-body';
          document.getElementById('olhoCardOculto').className = '';
          Cookies.remove('ocultaMargemCard');
      }
      <% } %>

      window.onload = function formLoad() {
          ocultaMargemCardCookie();
      }
   
    if (<%=exibeBanner%> && <%=haBannerNoSistema%>) {
  	  $(function () {
  		$('#bannerPropaganda').modal('show');
  	  });
  	  <%session.setAttribute("BANNER_EXIBIDO","true");%>
    }

     function fecharModal(){
         $(function () {
             $('#bannerPropaganda').modal('hide');
         });
     }
       
    function acionaCombo(envia) {     
      var csa = document.form1.FILTRO_TIPO[document.form1.FILTRO_TIPO.selectedIndex].value;
      if (csa==02 || csa==03) {   
        if (document.form1.CSA_CODIGO_AUX.disabled=true)        
          document.form1.CSA_CODIGO_AUX.disabled=false;
      } else {        
          document.form1.CSA_CODIGO_AUX.disabled=true;            
      }
      if (envia) {
        document.form1.submit();     
      }               
    }
  
    function vr_csa_selecionada() {
      var csaCodigo = "";
      var f0 = document.form1;
      if (f0.CSA_CODIGO_AUX != null) {
        for (i = 0 ; i < f0.CSA_CODIGO_AUX.length ; i++) {
          if (f0.CSA_CODIGO_AUX.options[i].selected) {
            csaCodigo = (f0.CSA_CODIGO_AUX.options[i].value);
          }
        }
        f0.CSA_SELECIONADA.value = csaCodigo;
      }
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
