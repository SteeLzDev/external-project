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
  String tipo      = (String) request.getAttribute("tipo");
  List<ArquivoDownload> arquivos = (List<ArquivoDownload>) request.getAttribute("arquivos");
%>
<c:set var="title">
  <hl:message key="rotulo.lst.arq.copia.seguranca.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-download"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header hasIcon">
      <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
      <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("rotulo.download.arquivo.copia.seguranca.titulo", responsavel) %></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover" id="dataTables">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.upload.arquivo.nome"/></th>
            <th scope="col"><hl:message key="rotulo.upload.arquivo.tamanho"/></th>
            <th scope="col"><hl:message key="rotulo.upload.arquivo.data"/></th>
            <th scope="col" width="15%"><hl:message key="rotulo.acoes.upload.arquivo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
        <%
        if (arquivos == null || arquivos.size() == 0) {
        %>
          <tr class="Lp">
            <td colspan="7"><hl:message key="mensagem.erro.upload.arquivo.nenhum.encontrado"/></td>
          </tr>
        <%
        } else {
          for (ArquivoDownload arquivo : arquivos) {
      %>
            <tr>
              <td><%=TextHelper.forHtmlContent(arquivo.getNomeOriginal())%></td>
              <td><%=TextHelper.forHtmlContent(arquivo.getTamanho())%></td>
              <td><%=TextHelper.forHtmlContent(arquivo.getData())%></td>
              <td>
                  <div class="actions">
                    <a class="ico-action" href="#">
                      <div class="form-inline" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(arquivo.getNome())%>'),'<%=TextHelper.forJavaScript(tipo)%>'); return false;">
                        <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.botao.download"/> <%=arquivo.getNomeOriginal()%>" title="" data-original-title="download">
                          <svg> <use xlink:href="../img/sprite.svg#i-download"></use></svg>
                        </span>
                        <hl:message key="rotulo.acoes.upload.arquivo.download"/>
                      </div>
                    </a>
                  </div>
              </td>
            </tr>
      <%
          }
        }
      %>
        </tbody>
      </table>
    </div>
  </div>
  <div class="btn-action mr-1">
    <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>  
<c:set var="javascript">
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
  <script  src="../node_modules/moment/min/moment.min.js"></script>
  <script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
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
    function downloadArquivo(arquivo,tipo) {
         postData("../v3/downloadArquivo?arquivo_nome=" + arquivo +"&tipo=" + tipo + "&<%=SynchronizerToken.generateToken4URL(request)%>",'download');
    }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>