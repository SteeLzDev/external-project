<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  List<TransferObject> formulariosPesquisa = (List) request.getAttribute("formulariosPesquisa");
  String filtro = (String) request.getAttribute("filtro");
  String licencaSurveyJS = (String) request.getAttribute("licencaSurveyJS");
%>
<c:set var="title">
  <hl:message key="rotulo.lst.pesquisa.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-bi"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row firefox-print-fix">
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
        <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("rotulo.lst.pesquisa.titulo.card", responsavel) %></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover" id="dataTables">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.lst.form.pesquisa.nome"/></th>
              <th scope="col"><hl:message key="rotulo.lst.form.pesquisa.data.criacao"/></th>
              <th scope="col" width="15%"><hl:message key="rotulo.lst.form.pesquisa.acoes"/></th>
            </tr>
          </thead>
          <tbody>
          <%
          if (formulariosPesquisa == null || formulariosPesquisa.size() == 0) {
          %>
            <tr class="Lp">
              <td colspan="7"><hl:message key="mensagem.erro.upload.arquivo.nenhum.encontrado"/></td>
            </tr>
          <%
          } else {
            for (TransferObject formularioPesquisa : formulariosPesquisa) {
              String fpeNome = (String) formularioPesquisa.getAttribute(Columns.FPE_NOME);
              String fpeCodigo = (String) formularioPesquisa.getAttribute(Columns.FPE_CODIGO);
        %>
              <tr>
                <td><%=TextHelper.forHtmlContent(formularioPesquisa.getAttribute(Columns.FPE_NOME))%></td>
                <td><%=TextHelper.forHtmlContent(DateHelper.toDateTimeString((java.util.Date) formularioPesquisa.getAttribute(Columns.FPE_DT_CRIACAO)))%></td>
                <td>
                    <a href="#no-back" onclick="abrirModalComSurvey('<%=fpeNome%>', '<%=fpeCodigo%>')">
                      <hl:message key="rotulo.acoes.visualizar"/>
                    </a>
                </td>
              </tr>
        <%
            }
          }
        %>
          </tbody>
        </table>
      </div>

    <div class="modal fade" id="modalDashboardPesquisa" tabindex="-1" aria-labelledby="modalDashboardPesquisaLabel" aria-hidden="true">
      <div class="modal-dialog modal-content-dash">
        <div class="modal-content ">
          <div class="modal-header">
            <h5 class="modal-title about-title mb-0" id="tituloModal"></h5>
            <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
              <span aria-hidden="true"></span>
            </button>
	        </div>
          <div class="modal-body">
            <div id="surveyVizPanel"></div>
          </div>
          <div class="modal-footer pt-0">
            <div class="btn-action mt-2 mb-0">
              <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>' href="#"><hl:message key="rotulo.botao.fechar"/></a>
            </div>
				</div>
      </div>
    </div>

    </div>
  </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
</c:set>  
<c:set var="javascript">
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/survey-analytics/survey.analytics.min.css" rel="stylesheet">
  <script  src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
  <script  src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
  <script  src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
  <script  src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
  <script  src="../node_modules/moment/min/moment.min.js"></script>
  <script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
  <script src="../node_modules/survey-core/survey.core.min.js"></script>
  <script src="../node_modules/plotly.js-dist-min/plotly.min.js"></script>
  <script src="../node_modules/survey-analytics/survey.analytics.min.js"></script>
  <script type="text/JavaScript">
    $(document).ready(function() {
  		$('#dataTables').DataTable({
  			"paging": true,
  		  	"pageLength": 20,
  	    	"lengthMenu": [
  	          [20, 50, 100, -1],
  	          [20, 50, 100, '<hl:message key="mensagem.datatables.all"/>']
  	        ],
  	        "pagingType": "simple_numbers",
  	        "dom": '<"card-body p-0" <"row pl-0 pr-4" <"col-sm-8 pl-1" l > <"col-sm-4 pr-0" f >> <"table-responsive" t> > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
  	        stateSave: true,
  	        stateSaveParams: function (settings, data) {
  	      	    data.search.search = "";
  	      	  },
  	        language: {
  	        		  search:            '_INPUT_',
  	        		  searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder"/>',
  	        		  processing:        '<hl:message key="mensagem.datatables.processing"/>',
  	                  loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
  	                  info:              '<hl:message key="mensagem.datatables.info"/>',
  	                  lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
  	                  infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
  	                  infoFiltered:      '<hl:message key="mensagem.datatables.info.filtered"/>',
  	                  infoPostFix:       '',
  	                  zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
  	                  emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
  	                  aria: {
  	                      sortAscending: '<hl:message key="mensagem.datatables.aria.sort.ascending"/>',
  	                      sortDescending:'<hl:message key="mensagem.datatables.aria.sort.descending"/>'
  	                  },
  	                  paginate: {
  	                    first:         '<hl:message key="mensagem.datatables.paginate.first.padrao.econsig"/>',
  	                    previous:      '<hl:message key="mensagem.datatables.paginate.previous.padrao.econsig"/>',
  	                    next:          '<hl:message key="mensagem.datatables.paginate.next.padrao.econsig"/>',
  	                    last:          '<hl:message key="mensagem.datatables.paginate.last.padrao.econsig"/>'
  	                },
  	                decimal: ",",
  	              },
  	              initComplete: function () {
  	                  var btns = $('.dt-button');
  	                  btns.addClass('btn btn-primary btn-sm');
  	                  btns.removeClass('dt-button');
  	              }
  	    });
        $("#dataTables_filter").addClass('pt-2 px-3');
        $('#dataTables_info').addClass('p-3');
        $("#dataTables_length").addClass('pt-3');
    });

    function abrirModalComSurvey(nome, fpeCodigo) {
      const modalElement = document.getElementById('modalDashboardPesquisa');
      const panelContainer = document.getElementById("surveyVizPanel");
      panelContainer.innerHTML = "";

      $.ajax({
          url: '../v3/formularioPesquisa?acao=exibir&fpeCodigo='+ fpeCodigo,
          method: 'POST',
          data: null,
          contentType: 'application/json',
          success: function(data) {
              const surveyJson = data.fpe;
              const surveyResults = data.fpr;

              Survey.slk("<%=licencaSurveyJS %>");
              const survey = new Survey.Model(surveyJson);

              const vizPanelOptions = {
                  allowHideQuestions: false
              };

              const vizPanel = new SurveyAnalytics.VisualizationPanel(
                  survey.getAllQuestions(),
                  surveyResults,
                  vizPanelOptions
              );

              vizPanel.locale = "pt";

              const tituloModal = document.getElementById("tituloModal");
              tituloModal.innerHTML = nome;

              const modal = new bootstrap.Modal(modalElement);
              modal.show();

              modalElement.addEventListener('shown.bs.modal', function () {
                  vizPanel.render(panelContainer);
              }, { once: true });
          },
          error: function(err) {
              console.error(err);
          }
      });
    } 
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>