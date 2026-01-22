<%@page import="com.zetra.econsig.values.TipoArquivoEnum"%>
<%@page import="com.zetra.econsig.helper.upload.UploadHelper"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean aprovados = (boolean) request.getAttribute("aprovados");
boolean reprovados = (boolean) request.getAttribute("reprovados");
boolean pendentes = (boolean) request.getAttribute("pendentes");
boolean pendentesTodasCsa = (boolean) request.getAttribute("pendentesTodasCsa");
boolean auditoria = (boolean) request.getAttribute("auditoria");
boolean auditoriaUsuarios = (boolean) request.getAttribute("auditoriaUsuarios");
List<TransferObject> lstAuxiliar = (List<TransferObject>) request.getAttribute("lstSituacaoContratos");
HashMap<String,String> hashAnexos = (HashMap<String,String>) request.getAttribute("hashAnexos");
List<String> tituloColunas = (List<String>) request.getAttribute("tituloColunas");
String linhasColunas = (String) request.getAttribute("linhasColunas");
String filtroTable = (String) request.getAttribute("filtroTable");

// DESENV-18924 - Ocultar colunas utilizando o state do datatable
// por utilizarmos essa funcionalidade é necessário um DataTabe por aba na página para o state funcionar corretamente de acordo com aba escolhida
String idDataTable ="";
if(pendentesTodasCsa){
    idDataTable = "pendentesTodasCsa";
} else if (pendentes){
    idDataTable = "pendente";
} else if (aprovados){
    idDataTable = "aprovado";
} else if (reprovados){
    idDataTable = "reprovado";
} else if (auditoria){
    idDataTable = "auditoria";
} else if (auditoriaUsuarios){
    idDataTable = "auditoriaUsuarios";
}
%>
<c:set var="title">
   <hl:message key="rotulo.validar.documentos.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <ul class="nav nav-tabs" role="tablist">
      <%if(responsavel.isSup()){ %>
          <li class="nav-item">
            <a class="nav-link <%=pendentesTodasCsa ? "active" : ""%>" href="#pendentesTodasCsa" role="tab"  data-bs-toggle="tab" onClick="postData('../v3/validarDocumentos?acao=iniciar&pendentesTodasCsa=true&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.validar.documentos.aba.pendentes.todas.csa"/></a>
          </li>
      <%} %>
      <li class="nav-item">
        <a class="nav-link <%=pendentes || (responsavel.isCsaCor() && reprovados) ? "active" : ""%>" href="#pendente" role="tab"  data-bs-toggle="tab" onClick="postData('../v3/validarDocumentos?acao=iniciar&pendentes=true&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.validar.documentos.aba.pendente"/></a>
      </li>
      <li class="nav-item">
        <a class="nav-link <%=aprovados ? "active" : ""%>" href="#aprovado" role="tab"  data-bs-toggle="tab" onClick="postData('../v3/validarDocumentos?acao=iniciar&aprovados=true&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.validar.documentos.aba.aprovado"/></a>
      </li>
      <%if(!responsavel.isCsaCor()){ %>
          <li class="nav-item">
            <a class="nav-link <%=reprovados ? "active" : ""%>" href="#reprovado" role="tab"  data-bs-toggle="tab" onClick="postData('../v3/validarDocumentos?acao=iniciar&reprovados=true&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.validar.documentos.aba.reprovado"/></a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%=auditoria ? "active" : ""%>" href="#auditoria" role="tab"  data-bs-toggle="tab" onClick="postData('../v3/validarDocumentos?acao=iniciar&auditoria=true&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.validar.documentos.aba.auditoria"/></a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%=auditoriaUsuarios ? "active" : ""%>" href="#auditoriaUsuarios" role="tab"  data-bs-toggle="tab" onClick="postData('../v3/validarDocumentos?acao=iniciar&auditoriaUsuarios=true&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.validar.documentos.aba.usuarios"/></a>
          </li>
      <%} %>
  </ul>
  <div class="tab-content table-responsive">
    <div class="card">
     <div  role="tabpanel" class="tab-pane fade show active" id="<%=pendentes ? "pendente" : aprovados ? "aprovado" : reprovados ? "reprovado" : pendentesTodasCsa ? "pendentesTodasCsa" : auditoria ? "auditoria" : "auditoriaUsuarios" %>">
        <%if(lstAuxiliar.isEmpty()) { %>
           <hl:message key="rotulo.validar.documentos.nenhuma.consignacao"/>
        <%} else { %>
            <table id="dataTables<%=idDataTable%>" class="table table-striped table-hover w-100">
            </table>
         <%} %>
     </div>
    </div>
  </div>
  <div class="btn-action">
      <a class="btn btn-outline-danger mt-2" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
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
  <script  src="../node_modules/moment/min/moment.min.js"></script>
  <script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
  <script  src="../node_modules/datatables.net-fixedheader/js/dataTables.fixedHeader.js"></script>
  <script type="text/javascript">
  
  var columns = [
      <% for (String tituloColuna : tituloColunas) { %>
      { title: "<%= TextHelper.forHtmlContent(tituloColuna) %>" },
      <% } %>
    ];
  
  var dataSet = [
      <%=linhasColunas%>
  ];

  var dataSetSemOrgao = [
      <%=linhasColunas%>
  ];

  $(document).ready(function() {
      $.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
      $('#dataTables<%=idDataTable%>').DataTable({
        "paging": true,
        "pageLength": 20,
        "lengthMenu": [
          [5, 10, 20, 50, 100, -1],
          [5, 10, 20, 50, 100, '<hl:message key="mensagem.datatables.all"/>']
        ],
        "pagingType": "simple_numbers",
        columns: columns,
        data: dataSet,
        processing: true,
        scrollY: '50vh',
        scrollX: true,
        fixedHeader:{
            header: true
        },
        scrollCollapse: true,
        "dom": '<"card-body p-0" <"row pl-0 pr-4" <"col-sm-2 pl-0" B > <"col-sm-6 pl-0" l > <"col-sm-4 pr-0" f >> <"table-responsive" t> > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
        buttons: [
            'colvis'
         ],
        stateSave: true,
        stateSaveParams: function (settings, data) {
      	    data.search.search = "";
      	  },
        language: {
              search:            '_INPUT_',
              searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder"/>',
                  processing:        '<hl:message key="mensagem.datatables.processing"/>',
                  loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
                  lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
                  info:              '<hl:message key="mensagem.datatables.info.consignatarias"/>',
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

      $("#dataTables<%=idDataTable%>_filter").addClass('pt-3');
      $('#dataTables<%=idDataTable%>_info').addClass('p-3');
      $("#dataTables<%=idDataTable%>_length").addClass('pt-3');

      <%if(!TextHelper.isNull(filtroTable)){%>
      		var tabelaDataTable = $('#dataTables<%=idDataTable%>').DataTable();
      		tabelaDataTable.search('<%=filtroTable%>').draw();
      <%}%>
	  
      if(document.getElementById("auditoria") != null) {
          removeOrgao(dataSetSemOrgao);
          verificaVisibilidadeOrgao();
      }
    });
  
  $('#dataTables<%=idDataTable%>').on('search.dt', function() {
	    var table = $('#dataTables<%=idDataTable%>').DataTable(); 
	    var linha = table.rows({search:'applied'} ).count();
	    if(linha == 1){
	    	$('.dataTables_scrollBody').css('height', '200px'); 
	    }
	}); 
  
  function validaReprovacao(soaCodigo, adeCodigo) {
    var valorObs = document.getElementById(soaCodigo).value;
    if(valorObs == '' || valorObs == 'undefined' || valorObs == null || valorObs.trim().length < 1){
        alert('<hl:message key="mensagem.erro.validar.documentos.reprovar.obs"/>');
        return false;
    } else {
  	  const valorSearch = $('.dataTables_filter input').val();
      postData('../v3/validarDocumentos?acao=aprovarReprovar&ADE_CODIGO='+adeCodigo+'&SOA_CODIGO='+soaCodigo+'&SOA_OBS='+valorObs+'&filtroTable='+valorSearch+'&_skip_history_=true&aprovar=false&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
    }
    return true;
  }

  function aprovar(adeCodigo,soaCodigo,soaObs){
	  const valorSearch = $('.dataTables_filter input').val();
	  postData('../v3/validarDocumentos?acao=aprovarReprovar&ADE_CODIGO='+adeCodigo+'&SOA_CODIGO='+soaCodigo+'&SOA_OBS='+soaObs+'&filtroTable='+valorSearch+'&_skip_history_=true&aprovar=true&<%=SynchronizerToken.generateToken4URL(request)%>');
  }
  function editarAnexos(adeCodigo){
	  const valorSearch = $('.dataTables_filter input').val();
	  postData('../v3/editarAnexosConsignacao?acao=exibir&validarDocumentos=true&ADE_CODIGO='+adeCodigo+'&filtroTable='+valorSearch+'&<%=SynchronizerToken.generateToken4URL(request)%>');
  }
  function downloads(adeCodigo,adeData){
	  postData('../v3/downloadAnexoValidarDocumentos?tipo=anexo&ADE_CODIGO='+adeCodigo+'&ADE_DATA='+adeData+'&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>');
  }
  function visualizar(adeCodigo,soaCodigo,soaObs){
	  const valorSearch = $('.dataTables_filter input').val();
	  postData('../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO='+adeCodigo+'&filtroTable='+valorSearch+'&<%=SynchronizerToken.generateToken4URL(request)%>');
  }
  
  function verificarDownload(adeCodigo, dataReserva, aadNome) {
  	$.post("../v3/verificarAnexoContratoConsignacao?arquivo_nome=" + aadNome + "&tipo=anexo&entidade=" + adeCodigo + "&data=" + dataReserva + "&_skip_history_=true", function(data) {
  	    try {
  	    	var jsonResult = $.trim(JSON.stringify(data));
  	        var obj = JSON.parse(jsonResult);
  	        var statusArquivo = obj.statusArquivo;
  
  	        if (statusArquivo) {
  	        	postData("../v3/downloadAnexoContratoConsignacao?arquivo_nome=" + aadNome + "&tipo=anexo&entidade=" + adeCodigo + "&data=" + dataReserva + "&_skip_history_=true");
  	        } else {
  	        	postData("../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=" + adeCodigo + "&<%out.print(SynchronizerToken.generateToken4URL(request));%>");
  	        }
  	     } catch (err) {
  	     }
  	}, "json");
  }
  
  function loadPdf(codigo, data, nome) {
     window.open('', codigo+nome, 'height=800,width=1000,status=no,toolbar=no,menubar=no,location=no,left=200,top=200'); 
     postData('../v3/carregarStream?acao=pdf&adeCodigo='+codigo+'&adeData='+data+'&aadNome='+nome+'&_skip_history_=true',codigo+nome);
  }

  function loadImg(codigo, data, nome) {
	  window.open('', codigo+nome, 'height=800,width=1000,status=no,toolbar=no,menubar=no,location=no,left=200,top=200'); 
	  postData('../v3/carregarStream?acao=visualizar&adeCodigo='+codigo+'&adeData='+data+'&aadNome='+nome+'&_skip_history_=true',codigo+nome);
  }
  function loadAudio(codigo, data, nome) {
	  window.open('', codigo+nome, 'height=800,width=1000,status=no,toolbar=no,menubar=no,location=no,left=200,top=200'); 
	  postData('../v3/carregarStream?acao=ouvir&adeCodigo='+codigo+'&adeData='+data+'&aadNome='+nome+'&_skip_history_=true',codigo+nome);
  }
  function loadVideo(codigo, data, nome) {
	  window.open('', codigo+nome, 'height=800,width=1000,status=no,toolbar=no,menubar=no,location=no,left=200,top=200'); 
	  postData('../v3/carregarStream?acao=visualizar&video=true&adeCodigo='+codigo+'&adeData='+data+'&aadNome='+nome+'&_skip_history_=true',codigo+nome);
  }
  function verificaVisibilidadeOrgao() {
      if(document.querySelectorAll('[aria-label="Dependencia: ordenar ascendente"]').length > 0
          || document.querySelectorAll('[aria-label="Dependencia: ordenar descendente"]').length > 0) {
          $('#dataTables<%=idDataTable%>').DataTable().clear().rows.add(dataSet).draw();
      } else {
          $('#dataTables<%=idDataTable%>').DataTable().clear().rows.add(dataSetSemOrgao).draw();
      }
      configuraEventoVisibilidadeOrgao();
  }
  function configuraEventoVisibilidadeOrgao() {
      let botaoColunas = document.getElementsByClassName("buttons-colvis")[0];
      botaoColunas.onclick = function fun() {
          verificaAlteracaoVisibilidadeOrgao();
      }
  }
  function verificaAlteracaoVisibilidadeOrgao() {
      let botaoOrgao = document.querySelectorAll('[data-cv-idx="1"]')[0];
      if(botaoOrgao.textContent === "Dependencia") {
          botaoOrgao.onclick = function fun() {
              verificaVisibilidadeOrgao();
          }
      }
  }
  function removeOrgao(dados) {
      for (let i = 0; i < dados.length - 1; i++) {
          if (dados[i][0] === dados[i + 1][0]) {
              dados[i][2] = String(parseInt(dados[i][2]) + parseInt(dados[i + 1][2]));
              dados[i][3] = String(parseInt(dados[i][3]) + parseInt(dados[i + 1][3]));
              dados[i][4] = String(parseInt(dados[i][4]) + parseInt(dados[i + 1][4]));
              dados.splice(i + 1, 1);
              i--;
          }
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
