<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.InformacaoCsaServidor"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<?> informacoesServidor = (List<?>) request.getAttribute("informacaoServidor");
String nomeServidor = (String) request.getAttribute("nomeServidor");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String serCodigo = (String) request.getAttribute("serCodigo");
%>
<c:set var="title">
   <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#<%= TextHelper.forHtml(request.getAttribute("imageHeader")) != null ? TextHelper.forHtml(request.getAttribute("imageHeader")) : "i-manutencao"%>"></use>
</c:set>
<c:set var="bodyContent">
<div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterInformacaoCsaServidor?acao=editar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(serCodigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.informacao.csa.servidor.criar.novo"/></a>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header hasIcon">
          <h2 class="card-header-title"><%=TextHelper.forHtmlContent(nomeServidor)%></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table id="dataTables"  class="table table-striped table-hover w-100">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.informacao.csa.servidor.lista.usuario"/></th>
                <th scope="col"><hl:message key="rotulo.informacao.csa.servidor.lista.data"/></th>
                <th scope="col"><hl:message key="rotulo.informacao.csa.servidor.lista.valor"/></th>
                <th scope="col"><hl:message key="rotulo.botao.opcoes"/></th>
              </tr>
            </thead>
            <tbody>
              <%
              String icsCodigo, icsUsuario, icsValor;
              Date icsData, teste;
              Iterator<?> it = informacoesServidor.iterator();
              while (it.hasNext()) {
                  CustomTransferObject informacaoServidor = (CustomTransferObject)it.next();
                  icsCodigo = (String)informacaoServidor.getAttribute(Columns.ICS_CODIGO);
                  icsUsuario = (String)informacaoServidor.getAttribute(Columns.USU_NOME);
                  icsData = (Date) informacaoServidor.getAttribute(Columns.ICS_DATA);
                  icsValor = (String)informacaoServidor.getAttribute(Columns.ICS_VALOR);
              %>
              <tr>
              <td><%=TextHelper.forHtmlContent(icsUsuario)%></td>
              <td><%=DateHelper.toDateTimeString(icsData)%></td>
              <td><%=TextHelper.forHtmlContent(icsValor)%></td> 
              <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes" />" aria-label="<hl:message key="rotulo.botao.opcoes" />"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterInformacaoCsaServidor?acao=editar&ICS_CODIGO=<%=TextHelper.forJavaScriptAttribute(icsCodigo)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(serCodigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterInformacaoCsaServidor?acao=excluir&ICS_CODIGO=<%=TextHelper.forJavaScriptAttribute(icsCodigo)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(serCodigo)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                      </div>
                    </div>
                  </div>
                </td>  
              </tr>
              <% } %>
            </tbody>
          </table>
        </div>
      </div>
      <div class="btn-action">
         <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
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
  $(document).ready(function() {
	    $.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
	    $('#dataTables').DataTable({
		    "paging": true,
		  	"pageLength": 5,
	    	"lengthMenu": [
	          [5, 10, 20, -1],
	          [5, 10, 20, '<hl:message key="mensagem.datatables.all"/>']
	        ],
	        "pagingType": "simple_numbers",
	        "dom": '<"card-body" <"row pl-0 pr-4" <"col-sm-2 pl-0" B > <"col-sm-6 pl-0" l > <"col-sm-4 pr-0" f >> <"table-responsive" t> > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
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
	                  info:              '<hl:message key="mensagem.datatables.info.informacao.csa.servidor"/>',
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
	  });
  </script>
</c:set>
  <t:page_v4>
      <jsp:attribute name="header">${title}</jsp:attribute>
      <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
      <jsp:attribute name="javascript">${javascript}</jsp:attribute>
      <jsp:body>${bodyContent}</jsp:body>
  </t:page_v4>
