<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String tituloColunas = (String) request.getAttribute("tituloColunas");
String conteudoLinhas = (String) request.getAttribute("conteudoLinhas");
%>
<c:set var="title">
   <hl:message key="mensagem.solicitar.portabilidade.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<div class="card">
  <div class="card-header hasIcon">
    <span class="card-header-icon">
      <svg width="24">
          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consignacao"></use>
      </svg>
    </span>
    <h2 class="card-header-title">
       <hl:message key="rotulo.consignacao.plural"/>
    </h2>
  </div>
  <div class="tab-content table-responsive">
    <div role="tabpanel" class="tab-pane fade show active">
      <table id="dataTables" class="table table-striped table-hover w-100">
      </table>
    </div>
  </div>
</div>
<div class="float-end">
  <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
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
  <script type="text/javascript">
  
  var columns = [
      <%=tituloColunas%>
  ];
  
  var dataSet = [
      <%=conteudoLinhas%>
  ];

  $(document).ready(function() {
      $.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
      $('#dataTables').DataTable({
  	    "paging": true,
  	  	"pageLength": 20,
      	"lengthMenu": [
            [20, 50, 100, -1],
            [20, 50, 100, '<hl:message key="mensagem.datatables.all"/>']
          ],
          "pagingType": "simple_numbers",
          columns: columns,
          data: dataSet,
          processing: true,
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

      $("#dataTables_filter").addClass('pt-2 px-3');
      $('#dataTables_info').addClass('p-3');
      $("#dataTables_length").addClass('pt-3');
  });
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>