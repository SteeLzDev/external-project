<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.stream.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
List<String[]> conteudoRelatorio = (List<String[]>) request.getAttribute("conteudoRelatorio");
String[] tituloColunas = conteudoRelatorio.get(0);
String linkRetorno = (String) request.getAttribute("linkRetorno");
String colunasExibir = IntStream.range(0, Math.min(7, tituloColunas.length)).mapToObj(n -> String.valueOf(n)).collect(Collectors.joining(","));
char decimalSeparator = LocaleHelper.getDecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
char groupingSeparator = LocaleHelper.getDecimalFormat().getDecimalFormatSymbols().getGroupingSeparator();
%>
<c:set var="imageHeader">
  <use xlink:href="#i-relatorio"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-body table-responsive">
      <table id="dataTables" class="table table-striped table-hover" style="width:100%">
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkRetorno, request))%>');"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="style">
<link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
<link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
<link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
</c:set>
<c:set var="javascript">
<script type="text/javascript" src="../node_modules/jszip/dist/jszip.min.js"></script>
<script type="text/javascript" src="../node_modules/pdfmake/build/pdfmake.min.js"></script>
<script type="text/javascript" src="../node_modules/pdfmake/build/vfs_fonts.js"></script>
<script type="text/javascript" src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
<script type="text/javascript" src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
<script type="text/javascript" src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
<script type="text/javascript" src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
<script type="text/javascript" src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
<script  src="../node_modules/moment/min/moment.min.js"></script>
<script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
<script type="text/JavaScript">
  var columns = [
    <% for (String tituloColuna : tituloColunas) { %>
    { title: "<%= TextHelper.forHtmlContent(tituloColuna) %>" },
    <% } %>
  ];
  
  var dataSet = [
    <% for (int i = 1; i < conteudoRelatorio.size(); i++) { %>
    [<% for (int j = 0; j < tituloColunas.length; j++) { out.print("'" + TextHelper.forJavaScriptBlock(conteudoRelatorio.get(i).length > j ? conteudoRelatorio.get(i)[j] : "") + "',"); } %>],
    <% } %>
  ];
  
  $(document).ready(function() {
      $.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
      $('#dataTables').DataTable({
          "pagingType": "simple_numbers",
          "lengthMenu": [
            [20, 40, 80, -1],
            [20, 40, 80, '<hl:message key="mensagem.datatables.all"/>']
          ],
          data: dataSet,
          columns: columns,
          columnDefs: [
              { targets: [<%= colunasExibir %>], visible: true},
              { targets: '_all', visible: false }
          ],
          "dom": '<"card-body p-0" <"row pl-0 pr-4" <"col-sm-6 pl-0" B > <"col-sm-3 pl-0" l > <"col-sm-3 pr-0" f >> <"table-responsive" t> > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
          buttons: [
              {
                  extend: 'print',
                  exportOptions: {
                      columns: ':visible'
                  }
              },
              {
                  extend: 'pdfHtml5',
                  orientation: 'landscape',
                  exportOptions: {
                      columns: ':visible'
                  }
              },
              {
                  extend: 'excel',
                  exportOptions: {
                      columns: ':visible',
                      format: {
                          body: function(data, row, column, node) {
                                    return data.replace('<%=groupingSeparator%>', '').replace('<%=decimalSeparator%>', '.');
                                }
                      }
                  }
              },              
              {
                  extend: 'csv',
                  exportOptions: {
                      columns: ':visible'
                  }
              },
              'colvis'
          ],
          responsive: true,
          language: {
              search:            '_INPUT_',
              processing:        '<hl:message key="mensagem.datatables.processing"/>',
              loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
              searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder"/>',
              lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
              info:              '<hl:message key="mensagem.datatables.info"/>',
              infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
              infoFiltered:      '<hl:message key="mensagem.datatables.info.filtered"/>',
              infoPostFix:       '',
              zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
              emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
              paginate: {
                  first:         '<hl:message key="mensagem.datatables.paginate.first"/>',
                  previous:      '<hl:message key="mensagem.datatables.paginate.previous"/>',
                  next:          '<hl:message key="mensagem.datatables.paginate.next"/>',
                  last:          '<hl:message key="mensagem.datatables.paginate.last"/>'
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

      $("#dataTables_filter").addClass('pt-2 px-3');
      $('#dataTables_info').addClass('p-3');
      $("#dataTables_length").addClass('pt-3');

  });
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${tituloPagina}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:attribute name="style">${style}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>