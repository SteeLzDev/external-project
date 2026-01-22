<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csaCodigo = (String) request.getAttribute("csaCodigo");
List convenios = (List) request.getAttribute("convenios");
boolean exigeMotivo = (boolean) request.getAttribute("exigeMotivo");
%>
<c:set var="title">
  <hl:message key="rotulo.servidor.listar.convenios.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/listarConvenioServidor?acao=desbloquear&<%=SynchronizerToken.generateToken4URL(request)%>&_skip_history_=true" name="form1">
    <input type="hidden" name="csaCodigo" id="csaCodigo" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.folha.lista.convenios.titulo.bloqueados"/></h2>
      </div>
      <div class="table-responsive ">
        <table id="dataTables" class="table table-striped table-hover">
          <thead>
            <tr class="selecionarColuna">
              <th class="colunaUnica" scope="col" title='<hl:message key="rotulo.auditoria.selecionar.multiplos.registros"/>' style="display: none;" width="10%">
                  <div class="form-check">
                    <input type="checkbox" class="form-check-input ml-0" name="checkAll" id="checkAll">
                  </div>
                </th>
              <th class="selecionarColuna"><hl:message key="rotulo.servidor.listar.convenios.verba"/></th>
              <th class="selecionarColuna"><hl:message key="rotulo.servidor.listar.convenios.descricao"/></th>
              <th class="selecionarColuna"><hl:message key="rotulo.auditoria.acoes"/></th>
            </tr>
          </thead>
          <tbody>
          <%=JspHelper.msgRstVazio(convenios.size()==0, 13, responsavel)%>
          <%
            boolean primeiro = true;
            CustomTransferObject convenio = null;
            String cnvCodigo, cnvCodVerba, svcDescricao;
            Iterator it = convenios.iterator();
            while (it.hasNext()) {
              convenio = (CustomTransferObject)it.next();
              cnvCodigo = (String)convenio.getAttribute(Columns.CNV_CODIGO);
              cnvCodVerba = (convenio.getAttribute(Columns.CNV_COD_VERBA) != null) ? (String)convenio.getAttribute(Columns.CNV_COD_VERBA) : "";
              svcDescricao = (String)convenio.getAttribute(Columns.SVC_DESCRICAO);
          %>
          
            <tr>
              <td class="colunaUnica" aria-label="" title="" data-bs-toggle="tooltip" data-original-title="" style="display: none;">
               <div class="form-check">
                 	<input type="checkbox" class="form-check-input ml-0" name="codigoVerba" id="codigoVerba_<%=(String)cnvCodigo%>" value="<%=TextHelper.forHtmlAttribute(cnvCodigo)%>">
             	</div>
              </td>
              <td><%=TextHelper.forHtmlContent(cnvCodVerba.toUpperCase())%></td>
              <td><%=TextHelper.forHtmlContent(svcDescricao.toUpperCase())%></td>
              <td class="selecioneCheckBox"><a href="javascript:void(0)" id="selecioneCheckBox"  onclick ="escolhechk('Selecionar',this)"><hl:message key="rotulo.acoes.selecionar"/></a></td>
            </tr>
    <% } %>
    	</tbody>
        </table>
      </div>
    </div>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular"/></h2>
      </div>
      <div class="card-body">
        <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.desbloqueio.verba", responsavel)%>" tmoSempreObrigatorio="<%=exigeMotivo%>" inputSizeCSS="col-sm-12"/>
      </div>
    </div>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
        <a class="btn btn-primary" id="btnEnvia" href="#no-back" onClick="validaForm(); return false;"><hl:message key="rotulo.botao.desbloquear"/></a>
      <INPUT NAME="convenios" id="convenios" TYPE="hidden" VALUE="">
    </div>
  </form>
  <div class="modal fade" id="alertModal" tabindex="-1" aria-labelledby="alertModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
        	<h5 class="modal-title about-title mb-0" id="alertModalLabel"><hl:message key="rotulo.atencao"/></h5>
          </div>
	      <div class="modal-body">
	        <hl:message key="mensagem.alerta.desbloqueio.verba"/>
	      </div>
	      <div class="modal-footer">
	        <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#"><hl:message key="rotulo.botao.fechar"/></a>
	      </div>
	    </div>
	  </div>
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
  
	<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.desbloqueio.verba", responsavel)%>" scriptOnly="true" />
	<script language="JavaScript" type="text/JavaScript">
	  $(document).ready(function() {
			$.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
			$('#dataTables').DataTable({
				"paging": false,
		        "dom": '<"row" <"col-sm-2"> <"col-sm-6" l > <"col-sm-4" f >> <"table-responsive" t > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
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
              }
		    });

		      $("#dataTables_filter").addClass('pt-2 px-3');
		      $('#dataTables_info').addClass('p-3');
		      $("#dataTables_length").addClass('pt-3');
	  });
		      
	var clicklinha = false;

	$(".selecionarColuna").click(function() {
	  var checked = $("table tbody tr input[type=checkbox]:checked").length;
	  if (checked == 0) {
	    if (clicklinha) {
	      $("table th:nth-child(-n+1)").hide();
	      $(".colunaUnica").hide();
	    } else {
	      $("table th:nth-child(-n+1)").show();
	      $(".colunaUnica").show();
	    }
	    clicklinha = !clicklinha;
	  }
	});

	var verificarCheckbox = function () {
	  var checked = $("table tbody tr input[type=checkbox]:checked").length;
	  var total = $("table tbody tr input[type=checkbox]").length;
	  $("input[id*=checkAll]").prop('checked', checked == total);
	  if (checked == 0) {
	    $("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").hide();
	  } else {
	    $("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").show();
	  }
	};

	$("table tbody tr td").not("td.colunaUnica, td.selecioneCheckBox").click(function (e) {
	  $(e.target).parents('tr').find('input[type=checkbox]').click();
	});

	function escolhechk(idchk,e) {
	  $(e).parents('tr').find('input[type=checkbox]').click();
	}

	$("table tbody tr input[type=checkbox]").click(function (e) {
	  verificarCheckbox();
	  var checked = e.target.checked;
	  if (checked) {
	    $(e.target).parents('tr').addClass("table-checked");
	  } else {
	    $(e.target).parents('tr').removeClass("table-checked");
	  }
	});

	$("input[id*=checkAll").click(function (e){
	  var checked = e.target.checked;
	  $('table tbody tr input[type=checkbox]').prop('checked', checked);
	  if (checked) {
	    $("table tbody tr").addClass("table-checked");
	  } else {
	    $("table tbody tr").removeClass("table-checked");
	  }
	  verificarCheckbox();
	});

	function adicionaInputHidden() {
	    var checkboxes = document.querySelectorAll('input[name="codigoVerba"]:checked');
	    if (checkboxes.length === 0) {
	        $('#alertModal').modal('show');
	        return false;
	    }
	    var values = [];
	    checkboxes.forEach(function(checkbox) {
	        values.push(checkbox.value);
	    });
	    var hiddenInput = document.getElementById('convenios');
	    hiddenInput.value = values.join(',');
	    return true;
	}
	
	function validaForm(){
		var f0 = document.forms[0];
	    var tmoCodigo = getElt('TMO_CODIGO');
		if (<%=exigeMotivo %>) {
	        if(tmoCodigo.value && confirmaAcaoConsignacao() && adicionaInputHidden()) {
	              f0.submit();
	        } else if (!tmoCodigo.value){
	           	  alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
    	          return false;
            }
		} else if (((tmoCodigo.value && confirmaAcaoConsignacao()) || !tmoCodigo.value) && adicionaInputHidden()) {
	               f0.submit();
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
