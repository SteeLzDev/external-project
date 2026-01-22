<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.entidade.MargemTO"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> servidores = (List<TransferObject>) request.getAttribute("servidores");
List<TransferObject> descricoesMargens = (List<TransferObject>) request.getAttribute("descricoesMargens");
%>
<c:set var="title">
   <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#<%= TextHelper.forHtml(request.getAttribute("imageHeader")) != null ? TextHelper.forHtml(request.getAttribute("imageHeader")) : "i-manutencao"%>"></use>
</c:set>
<c:set var="bodyContent">
    <form method="post" action="../v3/confirmarMargemFolhaServidor?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
      <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-manutencao"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.confirmar.margem.folha.listar.servidor"/></h2>
        </div>
        <div class="card-body table-responsive ">
          <table id="dataTables" class="table table-striped table-hover w-100">
            <thead>
              <tr>
                <th scope="col" width="10%">
                  <div class="form-check">                  
                   <input type="checkbox" class="form-check-input ml-0" onClick="checkUnCheckAll();" id="checkAll" name="checkAll" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.liquidar.parcela.selecione.todas", responsavel) %>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("mensagem.liquidar.parcela.selecione.todas", responsavel) %>">
                  </div>                  
                </th>
                <th><hl:message key="rotulo.servidor.nome"/></th>
                <th><hl:message key="rotulo.cpf"/></th>
                <th><hl:message key="rotulo.matricula.singular"/></th>
                <th><hl:message key="rotulo.orgao.singular"/></th>
                <th><hl:message key="rotulo.estabelecimento.singular"/></th>
                <% 
                   for (TransferObject descricaoMargem : descricoesMargens) {
                       Short marCodigo = Short.valueOf(descricaoMargem.getAttribute(Columns.MAR_CODIGO).toString());
                       String descricao = descricaoMargem.getAttribute(Columns.MAR_DESCRICAO).toString();

                       // Não exibe se não incide na margem
                       if (CodedValues.INCIDE_MARGEM_NAO.equals(marCodigo)) {
                           continue;
                       }
                %>
                <th><%=TextHelper.forHtmlContent(descricao)%></th>
                <th><%=TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage("rotulo.confirmar.margem.folha.servidor.media.margem", responsavel, descricao))%></th>
                <th><%=TextHelper.forHtmlContent(ApplicationResourcesHelper.getMessage("rotulo.confirmar.margem.folha.servidor.variacao.margem", responsavel, descricao))%></th>
                <% } %>
                <th><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
            <c:choose>
              <c:when test="${empty servidores}">
              <tr>
                <td colspan="6"><hl:message key="mensagem.nenhumServidorEncontrado"/></td>
              </tr>
              </c:when>
              <c:otherwise>
               <%
               for (TransferObject servidor : servidores) {
                   String serNome = servidor.getAttribute(Columns.SER_NOME).toString();
                   String serCpf = servidor.getAttribute(Columns.SER_CPF).toString();
                   String rseCodigo = servidor.getAttribute(Columns.RSE_CODIGO).toString();
                   String rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
                   String orgao = servidor.getAttribute(Columns.ORG_IDENTIFICADOR).toString() + " - " + servidor.getAttribute(Columns.ORG_NOME).toString();
                   String estabelecimento = servidor.getAttribute(Columns.EST_IDENTIFICADOR).toString() + " - " + servidor.getAttribute(Columns.EST_NOME).toString();
                   Map<Short, MargemTO> margens = (Map<Short, MargemTO>) servidor.getAttribute("MARGENS");
                   List<Short> margensAcimaMedia = (List<Short>) servidor.getAttribute("MARGENS_ACIMA_MEDIA");
               %>
                <tr class="selecionarLinha">
                  <td class="ocultarColuna" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.clique.aqui", responsavel) %>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.clique.aqui", responsavel) %>">
                     <div class="form-check">
                      <input type="checkbox" name="chkConfirmarMargem" class="form-check-input ml-0" id="chkConfirmarMargem<%=TextHelper.forHtmlAttribute(rseCodigo)%>" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">
                     </div>
                  </td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serNome)%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serCpf)%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(rseMatricula)%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(orgao)%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(estabelecimento)%></td>
                  <% 
                     for (TransferObject descricaoMargem: descricoesMargens) {
                         Short marCodigo = Short.valueOf(descricaoMargem.getAttribute(Columns.MAR_CODIGO).toString());

                         // Não exibe se não incide na margem
                         if (CodedValues.INCIDE_MARGEM_NAO.equals(marCodigo)) {
                             continue;
                         }
                         MargemTO margem = margens.get(marCodigo);
                  %>
                  <td class="selecionarColuna <%=margensAcimaMedia.contains(marCodigo) ? "erro" : ""%>"><%=TextHelper.forHtmlContent(margem != null && !TextHelper.isNull(margem.getMrsMargem()) ? NumberHelper.format(Double.valueOf(margem.getMrsMargem().toString()).doubleValue(), NumberHelper.getLang()) : "")%></td>
                  <td class="selecionarColuna <%=margensAcimaMedia.contains(marCodigo) ? "erro" : ""%>"><%=TextHelper.forHtmlContent(margem != null && !TextHelper.isNull(margem.getMrsMediaMargem()) ? NumberHelper.format(Double.valueOf(margem.getMrsMediaMargem().toString()).doubleValue(), NumberHelper.getLang()) : "")%></td>
                  <td class="selecionarColuna <%=margensAcimaMedia.contains(marCodigo) ? "erro" : ""%>"><%=TextHelper.forHtmlContent(margem != null && !TextHelper.isNull(margem.getVariacaoMediaMargem()) ? NumberHelper.format(Double.valueOf(margem.getVariacaoMediaMargem().toString()).doubleValue(), NumberHelper.getLang()) : "")%></td>
                  <% 
                     } 
                  %>
                  <td class="selecionarColuna"><a href="#no-back" aria-label="<hl:message key="mensagem.selecionar.servidor.clique.aqui"/>"><hl:message key="rotulo.botao.selecionar"/></a></td>
                </tr>
               <%
               }
               %>
              </c:otherwise>
            </c:choose>
            </tbody>
          </table>
        </div>
      </div>
      <input type="hidden" name="operacao">
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
        <a class="btn btn-outline-danger" href="#" onClick="javascript:f0.operacao.value='rejeitar'; vf_submit('rejeitadas'); return false;"><hl:message key="rotulo.botao.zerar.margem"/></a>
        <a class="btn btn-primary" href="#" onClick="javascript:f0.operacao.value='confirmar'; vf_submit('liquidadas'); return false;"><hl:message key="rotulo.acoes.desbloquear"/></a>
      </div>
    </form>
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
	// Ocultar coluna de seleção
	ocultarColuna();

	$.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
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

function vf_submit(acao) {
  var checked = false;

  for (i=0; i < f0.elements.length; i++) {
    var e = f0.elements[i];
    if (((e.type == 'check') || (e.type == 'checkbox')) && (e.checked == true) && e.id != 'checkAll') {
      checked = true;
    }
  }
  if (!checked) {
    alert('<%=ApplicationResourcesHelper.getMessage("mensagem.informe.pelo.menos.um.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel))%>');
  } else {
    if (f0.operacao.value == 'confirmar' && confirm('<hl:message key="mensagem.confirmar.margem.servidor"/>')) {
      f0.submit();
    } else if (f0.operacao.value == 'rejeitar' && confirm('<hl:message key="mensagem.rejeitar.margem.servidor"/>')) {
        f0.submit();
    } else if (f0.operacao.value != 'confirmar' && f0.operacao.value != 'rejeitar') {
    	alert('<hl:message key="mensagem.operacaoInvalida"/>');
    }
  }
}

function checkUnCheckAll() {
  if (f0.checkAll.checked) {
    checkAll(f0, 'chkConfirmarMargem');
  }	else {
    uncheckAll(f0, 'chkConfirmarMargem');
  }    
}

var f0 = document.forms[0];
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>