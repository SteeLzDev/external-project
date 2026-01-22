<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.ConsultaMargemSemSenha" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
Map<String, ConsultaMargemSemSenha> hashConsulta = (Map<String, ConsultaMargemSemSenha>) request.getAttribute("hashConsulta");

%>
<c:set var="title">
  <hl:message key="rotulo.autorizar.margem.consignataria.titulo"/>
</c:set>
<c:set var="dashboardTitle">
	<hl:message key="rotulo.dashboard.autorizar.margem.consignataria.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
	<div class="alert alert-warning" role="alert">
       <hl:message key="mensagem.informacao.autorizar.margem.consignataria" />
	</div>
  <form action="../v3/autorizarMargemConsignataria?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="formLista" id="form1">   
   <div class="card">
      <div class="card-header">
         <h2 class="card-header-title">${dashboardTitle}</h2>
      </div>
      <div class="card-body" style="padding-top: 0rem;">
        <div class="pt-2 table-responsive">
           <table id="dataTables" class="table table-striped table-hover w-100">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.dashboard.autorizar.margem.consignataria.alt"/></th>
                  <th scope="col"><hl:message key="rotulo.dashboard.autorizar.margem.consignataria.consignataria"/></th>
                  <th scope="col"><hl:message key="rotulo.dashboard.autorizar.margem.consignataria.data.inicio"/></th>
                  <th scope="col"><hl:message key="rotulo.dashboard.autorizar.margem.consignataria.data.fim"/></th>
               </tr>
             </thead>
             <tbody>
               <%=JspHelper.msgRstVazio(consignatarias.size()==0, 6, responsavel)%>
               <%
                  for (TransferObject consignataria: consignatarias) {
                      String csaCodigo = (String) consignataria.getAttribute(Columns.CSA_CODIGO);
                      ConsultaMargemSemSenha consultaMargem = hashConsulta.get(csaCodigo);
                      String cssCodigo = consultaMargem != null ? consultaMargem.getCssCodigo() : null;
                      String dataIni = consultaMargem != null ? DateHelper.toDateString(consultaMargem.getCssDataIni()) : "";
                      String dataFim = consultaMargem != null ? DateHelper.toDateString(consultaMargem.getCssDataFim()) : "";
               %>
               <tr class="selecionarLinha">                 
                  <td class="selecionarColuna">	              	 
	                 <input type="checkbox" <%=consultaMargem != null ? "checked" : "" %> class="checkbox-tabela form-check-input ml-0" id="chkConsignataria<%=csaCodigo%>" value="<%=csaCodigo + "_" + (consultaMargem != null ? "S" : "N") + (cssCodigo != null ? "_" + cssCodigo : "")%>">
              	  </td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(consignataria.getAttribute(Columns.CSA_IDENTIFICADOR))%> - <%=!TextHelper.isNull(consignataria.getAttribute(Columns.CSA_NOME_ABREV)) ? TextHelper.forHtmlContent(consignataria.getAttribute(Columns.CSA_NOME_ABREV)) : TextHelper.forHtmlContent(consignataria.getAttribute(Columns.CSA_NOME))%></td>
                  <td class="selecionarColuna"><%=dataIni%></td>
                  <td class="selecionarColuna"><%=dataFim%></td>   
               </tr> 
               <%
                 }
               %>
             </tbody>
           </table>
         </div>
      </div>
   </div>
   
  </form>
  <div class="btn-action">
     <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal');"><hl:message key="rotulo.botao.voltar"/></a>        
     <a class="btn btn-primary" href="#confirmarSenha" data-bs-toggle="modal" onClick="openModal();">
        <svg width="17">
          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar" onClick="openModal();"></use>
        </svg><hl:message key="rotulo.botao.confirmar"/>
     </a>        
  </div> 
  
  <%-- Modal de autorização de consignatárias --%>
  <div id="dialogAutorizarConsignataria" style="display: none;" class="modal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div id="modal-autorizacao" class="modal-body"> 
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-danger" data-bs-dismiss="modal"><hl:message key="rotulo.botao.voltar"/></button>
         <button id="botao-autorizar" type="button" class="btn btn-primary" data-bs-dismiss="modal" onClick="enviar()"><hl:message key="rotulo.botao.autorizar"/></button>
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
 <script type="text/JavaScript">
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
        "dom": '<"row" <"col-sm-2" B > <"col-sm-6" l > <"col-sm-4" f >> <"table-responsive" t > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
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

	 $('.selecionarColuna').css('text-align', 'center');
  });  
  	
  function openModal() {
	  verificaExistenciaOperacoesEmAndamento();
  }
     
  function enviar() {
	 //pegar todos os input checkbox checked da tabela
	 let consignatariasCheck = [];
	 $('.checkbox-tabela:checkbox').each(function () {
		 consignatariasCheck.push((this.checked ? "S_" : "N_") + this.value);
	 });

	//colocar input hidden no form com o value com os códigos concatenados separados por ;
	$('#form1').append('<input id="consignatariasCheck" type="hidden" name="consignatariasCheck"/>');
	document.getElementById('consignatariasCheck').value = consignatariasCheck.join(";");
	
	//submit
	document.getElementById('form1').submit();
   }  
  
  function verificaExistenciaOperacoesEmAndamento() {
		 //pegar todos os input checkbox checked da tabela
		 let consignatariasCheck = [];
         let autorizaCsa = false;
         let desautorizaCsa = false;
		 $('.checkbox-tabela:checkbox').each(function () {
			 consignatariasCheck.push((this.checked ? "S_" : "N_") + this.value + (this.disabled ? "_S" : "_N"));
             this.checked ? autorizaCsa = true : desautorizaCsa = true;
		 });
		 $.ajax({
	        type: 'POST',
	        url: '../v3/autorizarMargemConsignataria?acao=validarPermissaoDesautorizacao&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>',
	        data: {
	        	'consignatariasCheck': consignatariasCheck.join(";")
	        },
	        success: function (data, status, error) {
	        	const csasSemPermissaoDesautorizacao = JSON.parse(data);
	        	const modalAutorizacao = document.getElementById("modal-autorizacao");
	        	const botaoAutorizar = document.getElementById("botao-autorizar");
                const diasValidadeAutorizacao = <%=ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 30, responsavel)%>;
	        	if(csasSemPermissaoDesautorizacao == null || !csasSemPermissaoDesautorizacao.length) {
                    if(autorizaCsa && desautorizaCsa) {
                        modalAutorizacao.innerHTML = "<hl:message key='mensagem.autorizar.e.desautorizar.margem.consignataria.modal' arg0='" + diasValidadeAutorizacao + "' />";
                    } else if(desautorizaCsa) {
                        modalAutorizacao.innerHTML = "<hl:message key='mensagem.desautorizar.margem.consignataria.modal' />";
                    } else {
                        modalAutorizacao.innerHTML = "<hl:message key='mensagem.autorizar.margem.consignataria.modal' arg0='" + diasValidadeAutorizacao + "' />";
                    }
                    botaoAutorizar.style.display = "block";
	       		} else {
	       			let csasNomeSemPermissao = "";
	       			csasSemPermissaoDesautorizacao.forEach((csa, index) => {
	       				let inputCheck = document.getElementById("chkConsignataria" + csa.csaCodigo);
	       				inputCheck.checked = true;
	       				inputCheck.setAttribute("disabled", "");       				
	    				
	    				if(index === 0) {
	    					csasNomeSemPermissao += csa.csaNomeAbrev;
	    				} else {
	    					csasNomeSemPermissao += ", " + csa.csaNomeAbrev;
	    				}
	       			});	       			
	       			modalAutorizacao.innerHTML = "<hl:message key='mensagem.sem.permissao.desautorizar.consignataria.modal' arg0='" + csasNomeSemPermissao + "' />";
	       			botaoAutorizar.style.display = "none";
	       		}        	
	   			$('#dialogAutorizarConsignataria').modal('show');        
	        },
	        error: function (request, status, error) {
	        	console.log(error);	        	
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
