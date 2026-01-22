<%--
* <p>Title: listarComposicaoMargemServidor_v4.jsp</p>
* <p>Description: Página de listagem de composição de margem do servidor.</p>
* <p>Copyright: Copyright (c) 2002-2014</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> composicoes = (List) request.getAttribute("composicoes");
String linkRetorno = (String) request.getAttribute("linkRetorno");
String rseCodigo = (String) request.getAttribute("rseCodigo");
ServidorTransferObject servidor = (ServidorTransferObject) request.getAttribute("servidor");
RegistroServidorTO registroServidor = (RegistroServidorTO) request.getAttribute("registroServidor");
%>
<c:set var="title">
  <hl:message key="rotulo.listar.composicao.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterComposicaoMargemServidor?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
              <hl:message key="rotulo.listar.composicao.margem.criar.novo"/>
          </a>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-sm-12 col-ms-12">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.listar.composicao.margem.resultado.consulta"/></h2>
        </div>
        <dl class= "row data-list firefox-print-fix pt-2">
          <dt class="col-6"><hl:message key="rotulo.servidor.singular"/>:</dt><dd class="col-6"><%=TextHelper.forHtmlContent(registroServidor.getRseMatricula())%> - <%=TextHelper.forHtmlContent(servidor.getSerNome())%></dd>
        </dl>
      </div>
    </div>
    <div class="col-sm-12 col-ms-12">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.listar.composicao.margem.titulo"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table id="dataTables" class="table table-striped table-hover">
              <thead>
                 <tr>
                   <th scope="col"><hl:message key="rotulo.editar.composicao.margem.vencimento"/></th>
                   <th scope="col"><hl:message key="rotulo.editar.composicao.margem.valor"/></th>
                   <th scope="col"><hl:message key="rotulo.editar.composicao.margem.quantidade"/></th>
                   <th scope="col"><hl:message key="rotulo.servidor.vinculo"/></th>
                   <th scope="col"><hl:message key="rotulo.servidor.cargo"/></th>
                   <th scope="col"><hl:message key="rotulo.acoes"/></th>
                 </tr>
               </thead>
               <tbody>
               <%=JspHelper.msgRstVazio((composicoes == null || composicoes.isEmpty()), 6, responsavel)%>
<%
            if (!composicoes.isEmpty()) {
                Iterator<TransferObject> it = composicoes.iterator();
                while (it.hasNext()) {
                    TransferObject cto = it.next();

                    cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);

                    String cmaCodigo = cto.getAttribute(Columns.CMA_CODIGO).toString();
                    String vctDescricao = !TextHelper.isNull(cto.getAttribute(Columns.VCT_DESCRICAO)) ? cto.getAttribute(Columns.VCT_DESCRICAO).toString() : "";
                    String vrsDescricao = !TextHelper.isNull(cto.getAttribute(Columns.VRS_DESCRICAO)) ? cto.getAttribute(Columns.VRS_DESCRICAO).toString() : "";
                    String crsDescricao = !TextHelper.isNull(cto.getAttribute(Columns.CRS_DESCRICAO)) ? cto.getAttribute(Columns.CRS_DESCRICAO).toString() : "";
                    String cmaValor = !TextHelper.isNull(cto.getAttribute(Columns.CMA_VLR)) ? NumberHelper.format(((BigDecimal) cto.getAttribute(Columns.CMA_VLR)).doubleValue(), NumberHelper.getLang()) : "";
                    String cmaVinculo = !TextHelper.isNull(cto.getAttribute(Columns.CMA_VINCULO)) ? cto.getAttribute(Columns.CMA_VINCULO).toString() : "";
                    String cmaQuantidade = !TextHelper.isNull(cto.getAttribute(Columns.CMA_QUANTIDADE)) ? cto.getAttribute(Columns.CMA_QUANTIDADE).toString() : "";
%>
                   <tr>
                     <td><%=TextHelper.forHtmlContent(vctDescricao)%></td>
                     <td><%=JspHelper.formataMsgOca(cmaValor)%></td>
                     <td><%=TextHelper.forHtmlContent(cmaQuantidade)%></td>
                     <td><%=TextHelper.forHtmlContent(vrsDescricao)%></td>
                     <td><%=TextHelper.forHtmlContent(crsDescricao)%></td>
                     <td>
                       <div class="actions">
                         <div class="dropdown">
                           <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                             <div class="form-inline">
                               <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                                 <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                               </span> <hl:message key="rotulo.botao.opcoes"/>
                             </div>
                           </a>
                           <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterComposicaoMargemServidor?acao=iniciar&CMA_CODIGO=<%=TextHelper.forJavaScript(cmaCodigo)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterComposicaoMargemServidor?acao=excluir&CMA_CODIGO=<%=TextHelper.forJavaScript(cmaCodigo)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                           </div>
                         </div>
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
      </div>
   </div>
   <div class="btn-action" aria-label='<hl:message key="rotulo.botoes.acao.pagina"/>'>  
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
      <hl:htmlinput name="RSE_CODIGO"  type="hidden" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />
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
<script src="../js/colunaCheckbox.js?<hl:message key="release.tag"/>"></script>
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
