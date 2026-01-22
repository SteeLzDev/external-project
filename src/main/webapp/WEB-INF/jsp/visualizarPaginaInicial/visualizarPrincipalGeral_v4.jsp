<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.web.ServicoSolicitacaoServidor"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

// Mensagem na tela inicial só é mostrada depois do login.
boolean mostraMensagem = JspHelper.verificaVarQryStr(request, "mostraMensagem").equalsIgnoreCase("true");

// Todas as mensagens são listadas ou apenas uma quantidade X delas
boolean limitaMsg = JspHelper.verificaVarQryStr(request, "limitaMsg").equalsIgnoreCase("true");

// Um link para exibir todas as mensagens é exibido após o login
boolean mostraLink = JspHelper.verificaVarQryStr(request, "mostraLink").equalsIgnoreCase("true");

List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("lstConsignataria");

// Registra se o menu Mensagens foi selecionado
boolean menuMensagem = (request.getAttribute("menuMensagem") != null);
if (menuMensagem) {
    mostraMensagem = true;
}

boolean exibeBanner = (boolean) request.getAttribute("exibeBanner");
boolean haBannerNoSistema = (boolean) request.getAttribute("haBannerNoSistema");
String bannerName = (String) request.getAttribute("bannerName");

// Só exibe combo para preview de msgs se o usuario for cse, com permissao para editar msgs e tiver clicado no menu Mensagens
boolean mostraComboPreview = responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_EDITAR_MENSAGEM) && menuMensagem;

// Se o usuário for de consignante e tiver permissão de edição de msgs, inicializa as variáveis para combo de preview de mensagens de outras entidades
int filtro = (int) request.getAttribute("filtro");

// Mensagem da tela inicial do sistema
String msgTitulo = "";
String msgTelaInicial = "";
String menCodigo = "";
String arqCodigo = "";
//Lista de serviços que o servidor pode solicitar
List<ServicoSolicitacaoServidor> servicosReserva = (List<ServicoSolicitacaoServidor>) request.getAttribute("servicosReserva");

// Constante que indica qtas mensagens devem ser mostradas 
int numMaxMsg = (int) request.getAttribute("numMaxMsg");
 
List mensagens = (List) request.getAttribute("mensagens");
CustomTransferObject menTO = null;
int total = (int) request.getAttribute("total");

// Link para mostrar mais msgs é exibido 
if (mostraMensagem && limitaMsg && total > numMaxMsg) {
    mostraLink = true;
}

boolean csaTemCorrespondentes = responsavel.isCsa() && (boolean) request.getAttribute("csaTemCorrespondentes");
%>
<c:set var="configSistemaCard">
      <div class="col-sm-6">
      <hl:infoLogAuditoriav4/>
      <hl:infoUsuariov4/>

        <% 
           boolean exibeConfigFieldsPermission = ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_NIVEL_SEGURANCA, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ATUAL, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ORGAOS, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE_ORGAOS, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_NIVEL_SEGURANCA, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE_ORGAOS, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_TAXA_SERVICOS, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CONFIG_COMPRA_CONTRATO, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CONFIG_RENEGOCIACAO, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE_CSA, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CANCELAMENTO_AUTOMATICO, responsavel);
        
           if (!responsavel.isSer() && !ParamSist.paramEquals(CodedValues.TPC_EXIBE_CONFIG_SISTEMA_TELA_PRINCIPAL, CodedValues.TPC_NAO, responsavel) && exibeConfigFieldsPermission) { %>
            <hl:configsistemav4/>
        <% } %>
      </div>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-home"></use>
</c:set>
<c:set var="title">
    <hl:message key="rotulo.menu.pagina.inicial"/>
</c:set>
<c:set var="bodyContent">
    <hl:dashBoardv4 acessoInicial="${modoIntegrarFolha == 'acessoInicial'}" />
        
   <%if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_EXTRATO_CSA_COR, responsavel) && responsavel.isCsaCor()) { %>
     <div class="row">
        <div class="col-sm-12">
          <div class="card">
             <div id="card_header_extrato_csa" class="card-header hasIcon">
               <span class="card-header-icon"><svg width="26">
                  <use xlink:href="../img/sprite.svg#i-consignacao"></use></svg>
               </span>
               <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.extrato.dia", responsavel)%></h2>
               <div class="float-end height-0">
                 <span class="card-header-icon-click" >
                   <a href="#no-back" id="btnAtualiza" onClick="refreshStatus()">
                     <svg data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.clique.atualizar.vendas", responsavel)%>" width="42" height="42">
                     <use xlink:href="../img/sprite.svg#i-atualiza"></use></svg>
                   </a>
                 </span>
               </div>
              </div>
              <div class="card-body table-responsive p-0">
                 <div class="alert alert-warning m-0" role="alert">
                   <p class="mb-0"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.total.vendas", responsavel)%>: <span id="somaExtrato" class="font-weight-bold"> <hl:message key="rotulo.moeda"/></span></p>
                 </div>
                 <table id="tableTransacoes" class="table table-striped table-hover">
                         
                 </table>
              </div>
              <div class="card-footer">
                <div class="row">
                   <div class="col-sm-12 text-right">
                      <button type="button" class="btn btn-primary btn-mais-opcoes" aria-haspopup="true" aria-expanded="false" onClick="postData('../v3/listarExtrato?<%out.print(SynchronizerToken.generateToken4URL(request));%>'); return false;">
                         <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.consignacao.detalhar.vendas"/>" aria-label="<hl:message key="rotulo.consignacao.detalhar.vendas"/>"> 
                           <svg width="20" height="25"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg>
                         </span> 
                         <hl:message key="rotulo.consignacao.detalhar.vendas"/>
                      </button>
                   </div>
                </div>
              </div>            
          </div>
        </div>        
     </div>
     
     <!-- DESENV-12004: Modal sair do sistema no time out do refresh ajax -->
     <div class="modal fade" id="expirarModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" style="display: none" data-backdrop="static" data-keyboard="false">
       <div class="modal-dialog" role="document">
         <div class="modal-content">
           <div class="modal-header pb-0">
             <span class="modal-title about-title mb-0" id="exampleModalLabel"> <hl:message key="mensagem.informacao.sessao.expirada" />
             </span>
             <a  href="#no-back" onClick="postData('../v3/sairSistema?acao=sair&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>','_top')">
               <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                 <span aria-hidden="true">&times;</span>
               </button>
             </a>
           </div>
           <div class="modal-body">
             <hl:message key="mensagem.informacao.sessao.desativada.inatividade" />
           </div>
           <div class="modal-footer pt-0">
             <div class="btn-action mt-2 mb-0">
               <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/sairSistema?acao=sair&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>','_top')"
                 alt="<hl:message key="rotulo.botao.novo.login"/>" title="<hl:message key="rotulo.botao.novo.login"/>">
                 <hl:message key="rotulo.botao.novo.login" />
               </a>              
             </div>
           </div>
         </div>
       </div>
     </div>
     
   <%} %>    
      <div class="row">      
           <div class="col-sm-6">
             <hl:infoLogAuditoriav4/>
             <hl:infoUsuariov4/>
      
             <% 
                  boolean exibeConfigFieldsPermission = ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_NIVEL_SEGURANCA, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ATUAL, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ORGAOS, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE_ORGAOS, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_NIVEL_SEGURANCA, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE_ORGAOS, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_TAXA_SERVICOS, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CONFIG_COMPRA_CONTRATO, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CONFIG_RENEGOCIACAO, responsavel) ||
                                                 ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CANCELAMENTO_AUTOMATICO, responsavel);
        
             if (!responsavel.isSer() && !ParamSist.paramEquals(CodedValues.TPC_EXIBE_CONFIG_SISTEMA_TELA_PRINCIPAL, CodedValues.TPC_NAO, responsavel) && exibeConfigFieldsPermission) { %>
             <hl:configsistemav4/>
            <% } %>
           </div>
           <div class="col-sm-6">
          <div class="card">
            <div class="card-header hasIcon">
                <span class="card-header-icon"><svg width="24"><use xlink:href="#i-mensagem"></use></svg></span>
                <h2 class="card-header-title"><hl:message key="rotulo.mensagem.plural"/></h2>
            </div>
            <div class="card-body p-0">
                <div class="messages">
                <%
                  // Só mostra a mensagem após o login do usuário ou se foi solicitado pelo menu Mensagem
                  Iterator it = null;
                  boolean temMensagem = false;
                  if (mostraMensagem) {
                %>
                <%
                 it = mensagens.iterator();
                 if (it.hasNext()) {
                     temMensagem = true;
                     while (it.hasNext()) { 
                        menTO = (CustomTransferObject)it.next();
                        msgTitulo = menTO.getAttribute(Columns.MEN_TITULO).toString(); 
                        msgTelaInicial = menTO.getAttribute(Columns.MEN_TEXTO).toString(); 
                        menCodigo = menTO.getAttribute(Columns.MEN_CODIGO) != null ? menTO.getAttribute(Columns.MEN_CODIGO).toString() : null;
                        arqCodigo = menTO.getAttribute(Columns.ARQ_CODIGO) != null ? menTO.getAttribute(Columns.ARQ_CODIGO).toString() : null;
                        
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
                  <% if (mostraLink) { %>
                      <div class="btn-action">
                        <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/carregarPrincipal?mostraMensagem=true&mostraLink=false&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')" onMouseOver="return true;" onMouseOut="'return true'"><hl:message key="mensagem.principal.exibir.mensagens"/></a>
                      </div>
                  <% } %>
                <% } %>
                <% } %>
                <% if (!mostraMensagem || !temMensagem) { %>
                  <div class="message">
                      <h6 class="message-title"><hl:message key="mensagem.informacao.instrucoes"/></h6>
                      <p><hl:message key="mensagem.informacao.instrucoes.1"/>:</p>
                      <ul>
                          <li><hl:message key="mensagem.informacao.instrucoes.2"/></li>
                          <li><hl:message key="mensagem.informacao.instrucoes.3"/></li>
                          <li><hl:message key="mensagem.informacao.instrucoes.4.importante"/> : <hl:message key="mensagem.informacao.instrucoes.4"/></li>
                      </ul>
                  </div>
                 <% } %>
                </div>
            </div>
           </div>
        </div>      
      
    </div>
</c:set>
<c:set var="javascript">
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
  <link rel="stylesheet" href="<c:url value='/css/jquery.gridstrap.min.css'/>">
  <script src="<c:url value='/js/jquery.gridstrap.min.js'/>"></script>
  <script src="<c:url value='/node_modules/js-cookie/dist/js.cookie.min.js'/>"></script>
  <script  src="../node_modules/jszip/dist/jszip.min.js"></script>
  <script  src="../node_modules/pdfmake/build/pdfmake.min.js"></script>
  <script  src="../node_modules/pdfmake/build/vfs_fonts.js"></script>
  <script  src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
  <script  src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
  <script  src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
 <script type="text/JavaScript">
     if ('ontouchstart' in window) {
         $('#containerFavoritos').css('display','none');
     } else {
         $('#containerFavoritos').css('display','');
     }

      function fazDownload(codigo, menCodigo) {
	    postData('../v3/manterMensagem?acao=downloadArquivo&arqCodigo=' + codigo + '&menCodigo=' + menCodigo + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
	  }
     
     <%-- DESENV-16263: Foi desabilitado o alert do DataTable para não ficar pulando o erro para o usuário, podemos investigar pelo console. --%>
     $.fn.dataTable.ext.errMode = 'none';
     <%if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_EXTRATO_CSA_COR, responsavel) && responsavel.isCsaCor()) { %>
     var table;
  
     var columns = [	    
  	    { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero.ade.abreviado", responsavel)%>" },
  	    <%if (responsavel.isCsa() && csaTemCorrespondentes) { %>
          { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)%>" },
        <% } %>
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.e.hora", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.contrato", responsavel)%>" }	    
  	  ];
  
     table = $(document).ready(function() {
  		  table =  $('#tableTransacoes').DataTable({
  			  "autoWidth": false,
  			  "order": [[ 1, 'desc' ]],
  	    	  "ajax": {
  	    		  "url": "../v3/atualizarExtratoDiaAjax?_skip_history_=true",
  	    		  "type": "POST",
  	    		  "dataSrc": "tableDataSrc"
  	    	  },
  	    	  "pageLength": 5,
  	    	  "lengthMenu": [
  	              [5, 10, 20, -1],
  	              [5, 10, 20, '<hl:message key="mensagem.datatables.all"/>']
  	            ],
  	            "pagingType": "simple_numbers",
  	          columns: columns,
  	          responsive: true,
  	          language: {
  	              search:            '_INPUT_',
  	              processing:        '<hl:message key="mensagem.datatables.processing"/>',
  	              loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
  	              searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder.extrato.csa"/>',
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
  		  
  		  $('#tableTransacoes_paginate').addClass('p-3');
  		  $('#tableTransacoes_length').addClass('p-3').addClass('mt-2');
  		  $('#tableTransacoes_length').find('select').addClass('p-3');
  		  $('#tableTransacoes_length').parent().addClass('col-md-12').addClass('col-lg-6');
  		  $('#tableTransacoes_filter').parent().addClass('col-md-12').addClass('col-lg-6');		            
  		  $('#tableTransacoes_filter').find('input').appendTo('#tableTransacoes_filter').addClass('p-3').addClass('mt-2').addClass('col-md-8').addClass('col-sm-8').addClass('col-lg-8');
  		  $('#tableTransacoes_filter').find('label').remove();
  		  $('#tableTransacoes_filter').addClass('p-3');
  		  $('#tableTransacoes_length').find('input').addClass('col-md-6').addClass('col-sm-8').addClass('col-lg-8');
  		  $('#tableTransacoes_info').addClass('p-3');
  		  $('#tableTransacoes_info').addClass('mt-2');
  
  	  });

 	  var intervalFunction = setInterval( function () {
		    table.ajax.reload(function ( json ) {
              $('#somaExtrato').html('<%=ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)%> ' + json.somaTransDia );
            });
	  }, 180000 );

	  function refreshStatus() {
		  $('#btnAtualiza').on('click', function (e) {
		      e.preventDefault();
		  });
		  table.ajax.reload(function ( json ) {
              $('#somaExtrato').html('<%=ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)%> ' + json.somaTransDia );
          });
      }
  
      function limpaRefreshAjax() {
    	  clearInterval(intervalFunction);
    	  $('#expirarModal').modal('show');
          $.ajax({
          	url: "../v3/expirarSistemaAjax",
      	    type: "POST"
          });
      }
  
  	  setTimeout(limpaRefreshAjax, <%=session.getMaxInactiveInterval() * 1000%>);
  
      $('#tableTransacoes').on( 'init.dt', function (e, settings, json) {
    	  $('#somaExtrato').html('<%=ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)%> ' + json.somaTransDia );
      } );
     <%} %>  
  
     // Cadastro de taxas
     var tableCadastroTaxas;
     var columnsCadastroTaxas = [
  	    { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.configuracoes.data", responsavel)%>" },
        <% if (ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel)) { %>
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.limite.cet", responsavel)%>" }
        <% } else { %>
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.limite.taxa", responsavel)%>" }
        <% } %>
  	  ];

     tableCadastroTaxas = $(document).ready(function() {
    	 tableCadastroTaxas =  $('#tableCadastroTaxas').DataTable({
    		  "autoWidth": false,
    		  "order": [[ 1, 'asc' ]],
        	  "ajax": {
        		  "url": "../v3/listarCadastroTaxas?acao=listar&_skip_history_=true",
        		  "type": "POST",
        		  "dataSrc": "tableDataSrc"
        	  },
        	  "pageLength": 5,
        	  "lengthMenu": [
                  [5, 10, 20, -1],
                  [5, 10, 20, '<hl:message key="mensagem.datatables.all"/>']
                ],
                "pagingType": "simple_numbers",
              columns: columnsCadastroTaxas,
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
    	  $('#tableCadastroTaxas').append("<tfoot><tr><td colspan=\"4\">" + "<%=ApplicationResourcesHelper.getMessage("rotulo.cet.listagem", responsavel)%>" + "</td></tr></tfoot>")
    	  $('#tableCadastroTaxas_paginate').addClass('p-3');
    	  $('#tableCadastroTaxas_length').addClass('p-3').addClass('mt-2');
    	  $('#tableCadastroTaxas_length').find('select').addClass('p-3');
    	  $('#tableCadastroTaxas_length').parent().addClass('col-md-12').addClass('col-lg-6');
    	  $("#tableCadastroTaxas_filter").hide();
    	  $('#tableCadastroTaxas_length').find('input').addClass('col-md-6').addClass('col-sm-8').addClass('col-lg-8');
    	  $('#tableCadastroTaxas_info').addClass('p-3');
    	  $('#tableCadastroTaxas_info').addClass('mt-2');     
	  
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

     // Cancelamento automático
     var tableCancelamentoAutomatico;
     var columnsCancelamentoAutomatico = [
  	    { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.solicitacao", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.pre.reserva", responsavel)%>" }
        <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel)) { %>
        ,{ title: "<%=ApplicationResourcesHelper.getMessage("rotulo.compra.contrato.abreviado", responsavel)%>" }
        <% } %>
  	  ];

     tableCancelamentoAutomatico = $(document).ready(function() {
    	 tableCancelamentoAutomatico =  $('#tableCancelamentoAutomatico').DataTable({
    		  "autoWidth": false,
    		  "order": [[ 1, 'asc' ]],
        	  "ajax": {
        		  "url": "../v3/listarServicosCancelamentoAutomatico?acao=listar&_skip_history_=true",
        		  "type": "POST",
        		  "dataSrc": "tableDataSrc"
        	  },
        	  "pageLength": 5,
        	  "lengthMenu": [
                  [5, 10, 20, -1],
                  [5, 10, 20, '<hl:message key="mensagem.datatables.all"/>']
                ],
                "pagingType": "simple_numbers",
              columns: columnsCancelamentoAutomatico,
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
    	  
    	  $('#tableCancelamentoAutomatico_paginate').addClass('p-3');
    	  $('#tableCancelamentoAutomatico_length').addClass('p-3').addClass('mt-2');
    	  $('#tableCancelamentoAutomatico_length').find('select').addClass('p-3');
    	  $('#tableCancelamentoAutomatico_length').parent().addClass('col-md-12').addClass('col-lg-6');
    	  $("#tableCancelamentoAutomatico_filter").hide();
    	  $('#tableCancelamentoAutomatico_length').find('input').addClass('col-md-6').addClass('col-sm-8').addClass('col-lg-8');
    	  $('#tableCancelamentoAutomatico_info').addClass('p-3');
    	  $('#tableCancelamentoAutomatico_info').addClass('mt-2');     
	  
	  });

	 // Modulo avançado de compra
     var tableModuloAvancadoCompra;
     var columnsModuloAvancadoCompra = [
  	    { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.informacao.saldo", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.pagamento.saldo", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.liquidacao.saldo", responsavel)%>" }	    
  	  ];

     tableModuloAvancadoCompra = $(document).ready(function() {
    	 tableModuloAvancadoCompra =  $('#tableModuloAvancadoCompra').DataTable({
 			  "autoWidth": false,
 			  "order": [[ 1, 'asc' ]],
 	    	  "ajax": {
 	    		  "url": "../v3/listarServicosModuloAvancadoCompra?acao=listar&_skip_history_=true",
 	    		  "type": "POST",
 	    		  "dataSrc": "tableDataSrc"
 	    	  },
 	    	  "pageLength": 5,
 	    	  "lengthMenu": [
 	              [5, 10, 20, -1],
 	              [5, 10, 20, '<hl:message key="mensagem.datatables.all"/>']
 	            ],
 	            "pagingType": "simple_numbers",
 	          columns: columnsModuloAvancadoCompra,
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
 		  
 		  $('#tableModuloAvancadoCompra_paginate').addClass('p-3');
 		  $('#tableModuloAvancadoCompra_length').addClass('p-3').addClass('mt-2');
 		  $('#tableModuloAvancadoCompra_length').find('select').addClass('p-3');
 		  $('#tableModuloAvancadoCompra_length').parent().addClass('col-md-12').addClass('col-lg-6');
		  $("#tableModuloAvancadoCompra_filter").hide();
 		  $('#tableModuloAvancadoCompra_length').find('input').addClass('col-md-6').addClass('col-sm-8').addClass('col-lg-8');
 		  $('#tableModuloAvancadoCompra_info').addClass('p-3');
 		  $('#tableModuloAvancadoCompra_info').addClass('mt-2');
 
 	  });
 
	 // Modulo avançado de compra
     var tableCompraContrato;
     var columnsCompraContrato = [
  	    { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.minimo.parcelas.pagas", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.minimo.vigencia.contrato", responsavel)%>" }	    
  	  ];

     tableCompraContrato = $(document).ready(function() {
    	 tableCompraContrato =  $('#tableCompraContrato').DataTable({
 			  "autoWidth": false,
 			  "order": [[ 1, 'asc' ]],
 	    	  "ajax": {
 	    		  "url": "../v3/listarServicosCompraContrato?acao=listar&_skip_history_=true",
 	    		  "type": "POST",
 	    		  "dataSrc": "tableDataSrc"
 	    	  },
 	    	  "pageLength": 5,
 	    	  "lengthMenu": [
 	              [5, 10, 20, -1],
 	              [5, 10, 20, '<hl:message key="mensagem.datatables.all"/>']
 	            ],
 	            "pagingType": "simple_numbers",
 	          columns: columnsCompraContrato,
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
 		  
	   	  $('#tableCompraContrato').append("<tfoot><tr><td colspan=\"4\">" + "<%=ApplicationResourcesHelper.getMessage("rotulo.restricoes.compra.listagem", responsavel)%>" + "</td></tr></tfoot>")
 		  $('#tableCompraContrato_paginate').addClass('p-3');
 		  $('#tableCompraContrato_length').addClass('p-3').addClass('mt-2');
 		  $('#tableCompraContrato_length').find('select').addClass('p-3');
 		  $('#tableCompraContrato_length').parent().addClass('col-md-12').addClass('col-lg-6');
		  $("#tableCompraContrato_filter").hide();
 		  $('#tableCompraContrato_length').find('input').addClass('col-md-6').addClass('col-sm-8').addClass('col-lg-8');
 		  $('#tableCompraContrato_info').addClass('p-3');
 		  $('#tableCompraContrato_info').addClass('mt-2');
 
 	  });

	 // Renegociação de compra
     var tableRenegociacaoContrato;
     var columnsRenegociacaoContrato = [
  	    { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.minimo.parcelas.pagas", responsavel)%>" },
        { title: "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.minimo.vigencia.contrato", responsavel)%>" }	    
  	  ];

     tableRenegociacaoContrato = $(document).ready(function() {
    	 tableRenegociacaoContrato =  $('#tableRenegociacaoContrato').DataTable({
 			  "autoWidth": false,
 			  "order": [[ 1, 'asc' ]],
 	    	  "ajax": {
 	    		  "url": "../v3/listarServicosRenegociacaoContrato?acao=listar&_skip_history_=true",
 	    		  "type": "POST",
 	    		  "dataSrc": "tableDataSrc"
 	    	  },
 	    	  "pageLength": 5,
 	    	  "lengthMenu": [
 	              [5, 10, 20, -1],
 	              [5, 10, 20, '<hl:message key="mensagem.datatables.all"/>']
 	            ],
 	            "pagingType": "simple_numbers",
 	          columns: columnsRenegociacaoContrato,
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
 		  
	   	  $('#tableRenegociacaoContrato').append("<tfoot><tr><td colspan=\"4\">" + "<%=ApplicationResourcesHelper.getMessage("rotulo.sistema.restricoes.renegociacao", responsavel)%>" + "</td></tr></tfoot>")
 		  $('#tableRenegociacaoContrato_paginate').addClass('p-3');
 		  $('#tableRenegociacaoContrato_length').addClass('p-3').addClass('mt-2');
 		  $('#tableRenegociacaoContrato_length').find('select').addClass('p-3');
 		  $('#tableRenegociacaoContrato_length').parent().addClass('col-md-12').addClass('col-lg-6');
		  $("#tableRenegociacaoContrato_filter").hide();
 		  $('#tableRenegociacaoContrato_length').find('input').addClass('col-md-6').addClass('col-sm-8').addClass('col-lg-8');
 		  $('#tableRenegociacaoContrato_info').addClass('p-3');
 		  $('#tableRenegociacaoContrato_info').addClass('mt-2');
 
 	  });
	  
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>    
</t:page_v4>
