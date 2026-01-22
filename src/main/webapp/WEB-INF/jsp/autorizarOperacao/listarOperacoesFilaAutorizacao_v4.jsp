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
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> lstFilaOperacoes = (List<TransferObject>) request.getAttribute("lstFilaOperacoes");
%>
<c:set var="title">
  <hl:message key="rotulo.fila.op.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <form action="../v3/autorizarOperacoesFila?acao=resolverOperacoes&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="formLista" id="formLista">
    <div class="card">
      <div class="card-header">
         <h2 class="card-header-title">${title}</h2>
      </div>
      <div class="card-body">
        <div class="pt-3 table-responsive">
           <table id="dataTables" class="table table-striped table-hover w-100">
              <thead>
                <tr>
                  <th scope="col" width="10%" style="display: none;">
                      <div class="form-check">
                        <input type="checkbox" class="form-check-input ml-0" id="chkConfirmarAll">
                      </div> <hl:message key="rotulo.fila.op.confirmar.abrev"/>
                  </th>
                  <th scope="col" width="10%" style="display: none;">
                      <div class="form-check">
                        <input type="checkbox" class="form-check-input ml-0" id="chkDescartarAll">
                      </div> <hl:message key="rotulo.fila.op.descartar.abrev"/>
                  </th>
                  <th scope="col"><hl:message key="rotulo.fila.op.sensiveis.usuario.nome"/></th>
                  <th scope="col"><hl:message key="rotulo.fila.op.sensiveis.usuario.login"/></th>
                  <th scope="col"><hl:message key="rotulo.fila.op.sensiveis.ip.acesso"/></th>
                  <th scope="col"><hl:message key="rotulo.fila.op.sensiveis.data"/></th>
                  <th scope="col"><hl:message key="rotulo.fila.op.sensiveis.funcao"/></th>
              	  <th scope="col"><hl:message key="rotulo.fila.op.sensiveis.servidor"/></th>
                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
               </tr>
             </thead>
             <tbody>
               <%=JspHelper.msgRstVazio(lstFilaOperacoes.size()==0, 6, responsavel)%>
               <%
                  for (TransferObject operacao: lstFilaOperacoes) {
               %>
               <tr class="selecionarLinha">
                  <td class="ocultarColunaDupla" aria-label="<hl:message key="rotulo.fila.op.confirmar"/>" title="<hl:message key="rotulo.fila.op.confirmar"/>" style="display: none;">
                      <div class="form-check">
                         <input type="checkbox" class="form-check-input ml-0" name="chkConfirmar" value="<%=TextHelper.forHtmlAttribute(operacao.getAttribute(Columns.ONC_CODIGO))%>">
                      </div>
                  </td>
                  <td class="ocultarColunaDupla" aria-label="<hl:message key="rotulo.fila.op.confirmar"/>" title="<hl:message key="rotulo.fila.op.confirmar"/>" style="display: none;">
                      <div class="form-check">
                         <input type="checkbox" class="form-check-input ml-0" name="chkDescartar" value="<%=TextHelper.forHtmlAttribute(operacao.getAttribute(Columns.ONC_CODIGO))%>">
                      </div>
                  </td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(operacao.getAttribute(Columns.USU_NOME))%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(operacao.getAttribute(Columns.USU_LOGIN))%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(operacao.getAttribute(Columns.ONC_IP_ACESSO))%></td>
                  <td class="selecionarColuna"><span class="d-none"><%=TextHelper.forHtmlContent(DateHelper.toISOString((Date) operacao.getAttribute(Columns.ONC_DATA)))%></span><%=TextHelper.forHtmlContent(DateHelper.format((Date) operacao.getAttribute(Columns.ONC_DATA), LocaleHelper.getDateTimePattern()))%></td>                                
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(operacao.getAttribute(Columns.FUN_DESCRICAO))%></td>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(operacao.getAttribute("SERVIDOR"))%></td>
                  <td class="acoes">
                     <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.fila.op.sensiveis.ver.detalhes", responsavel)%>"> <svg>
                                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                            </span> <hl:message key="rotulo.botao.opcoes"/>
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                           <a class="dropdown-item" href="#" name="selecionaConfirmar"><hl:message key="rotulo.fila.op.confirmar"/></a>
                           <a class="dropdown-item" href="#" name="selecionaDescartar"><hl:message key="rotulo.fila.op.descartar"/></a>                         
                           <a class="dropdown-item" href="#" onclick ="visualizarOperacao('<%=TextHelper.forHtmlContent(operacao.getAttribute(Columns.ONC_CODIGO))%>')"><hl:message key="rotulo.fila.op.exibir.detalhe"/></a>
                        </div>
                      </div>
                     </div>   
                  </td>                
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
     <a class="btn btn-primary" href="#confirmarSenha" data-bs-toggle="modal" onClick="enviar();">
        <svg width="17">
          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar" onClick="enviar();"></use>
        </svg><hl:message key="rotulo.botao.confirmar"/>
     </a>        
  </div> 
  
  <%-- Modal de apresentação do detalhe da operação --%>
  <div id="dialogDetalhe" class="autorizacao-dialog" title='<hl:message key="rotulo.fila.op.modal.detalhe.titulo"/>' style="display: none;">
  </div>
  
  <%-- Modal de observação de descarte de operações --%>
  <div id="dialogObsDescarte" title='<hl:message key="rotulo.fila.op.modal.descarte.titulo"/>' style="display: none;">
      <form id="formObsDescarte">
        <div id="dialogDescarteErro" class="alert alert-danger" role="alert" style="display: none;"></div>
        <div class="form-group mb-0">
          <div class="form-check">
            <label for="obsDescarte">
              <hl:message key="rotulo.fila.op.obs.descarte" />
            </label><br><br>            
            <textarea name="obsDescarte" id="obsDescarte" class="form-control" cols="32" rows="5" placeholder='<hl:message key="mensagem.info.fila.op.obs.descarte.placeholder"/>' onFocus="SetarEventoMascara(this,'#*10000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
          </div>          
        </div>        
      </form>
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

    var clicklinha = false;

    $(".selecionarColuna").click(function() {
   	     // 1- Seleciona a linha e mostrar as colunas dos checks   	
   	     var checked = $("table tbody tr input[type=checkbox]:checked").length;

   	     if (checked == 0) {

   		    if (clicklinha) {
   			   $("table th:nth-child(-n+2)").hide();
   			   $(".ocultarColunaDupla").hide();
   		    } else {
   			   $("table th:nth-child(-n+2)").show();
   			   $(".ocultarColunaDupla").show();
   		    }

   		    clicklinha = !clicklinha;
   	     }
    });

    $("[name='selecionaConfirmar']").click(function() {
    	// 1- Exibe as colunas dos checksboxes
    	$("table th:nth-child(-n+2)").show();
    	$(".ocultarColunaDupla").show();
    	
    	// 2- Colore a linha e marca o checkbox do confirmar, e caso o check do descartar esteja marcado, será desmarcado
    	$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
    	$(this).parentsUntil("tbody").find('input[name="chkDescartar"]').prop("checked",false);
    	$(this).parentsUntil("tbody").find('input[name="chkConfirmar"]').prop("checked", true);
    	$("#chkDescartarAll").prop('checked', false);
    	
    	// 3- Verifica se todos os checkboxes do Confrimar estão marcados, marca o checkAll do Confirmar, e desmarca o do Descartar
    	var qtdCheckboxCheked = $("[name='chkConfirmar']").not($("#chkConfirmarAll")).filter(':checked').length;
    	var qtdCheckbox = $("[name='chkConfirmar']").not($("#chkConfirmarAll")).length;
    	if (qtdCheckbox == qtdCheckboxCheked) {
    		$("#chkConfirmarAll").prop('checked', true);
    	} else if (qtdCheckbox != qtdCheckboxCheked) {
    		$("#chkConfirmarAll").prop('checked', false);
    	}
     });

     $("[name='selecionaDescartar']").click(function() {
    	// 1- Exibe as colunas dos checksboxes
    	$("table th:nth-child(-n+2)").show();
    	$(".ocultarColunaDupla").show();
    	
    	// 2- Colore a linha e marca o checkbox do confirmar, e caso o check do descartar esteja marcado, será desmarcado
    	$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
    	$(this).parentsUntil("tbody").find('input[name="chkConfirmar"]').prop("checked",false);
    	$(this).parentsUntil("tbody").find('input[name="chkDescartar"]').prop("checked", true);
    	$("#chkConfirmarAll").prop('checked', false);
    	
    	// 3- Verifica se todos os checkboxes do Confrimar estão marcados, marca o checkAll do Confirmar, e desmarca o do Descartar
    	var qtdCheckboxCheked = $("[name='chkDescartar']").not($("#chkDescartarAll")).filter(':checked').length;
    	var qtdCheckbox = $("[name='chkDescartar']").not($("#chkDescartarAll")).length;
    	if (qtdCheckbox == qtdCheckboxCheked) {
    		$("#chkDescartarAll").prop('checked', true);
    	} else if (qtdCheckbox != qtdCheckboxCheked) {
    		$("#chkDescartarAll").prop('checked', false);
    	}
     });

     /* **Click do check Confirmar
      * 1- Ao ser selecionado, colorir a linha e ou ser desselecionado tirar a cor da linha
      * 2- Ao ser selecionado, caso complete a seleção de todos na coluna, marcar a opção checkall, caso quebre essa regra desmarcar o checkall
      * 3- Ao ser desselecionado, caso seja o único checkbox da página, esconder as colunas extras
      * 4- Ao ser selecionado, caso o checkall contrário esteja marcado, ele será desmarcado
      * 
      */
     $("[name='chkConfirmar']").click(function() {
     	//1- colore a linha
     	if ($(this).is(":checked")) {
     		$(this).parentsUntil("tbody", ".selecionarLinha").addClass("table-checked");
     		$(this).parentsUntil("tbody").find('input[name="chkDescartar"]').prop("checked",false);
     		
     		//4- desmarca o checkall contrário
     		$("#chkDescartarAll").prop('checked', false);
     	} else {
     		$(this).parentsUntil("tbody", ".selecionarLinha").removeClass("table-checked");
     	}
     	
     	//2- marca ou desmarca o checkall correspondente
     	var qtdCheckboxCheked = $("[name='chkConfirmar']").not($("#chkConfirmarAll")).filter(':checked').length;
     	var qtdCheckbox = $("[name='chkConfirmar']").not($("#chkConfirmarAll")).length;
     	if (qtdCheckbox == qtdCheckboxCheked) {
     		$("#chkConfirmarAll").prop('checked', true);
     	} else if (qtdCheckbox != qtdCheckboxCheked) {
     		$("#chkConfirmarAll").prop('checked', false);
     	}
     	
     	//3- esconde as colunas
     	if ($('input[type="checkbox"]').filter(':checked').length == 0) {
     		$("table th:nth-child(-n+2)").hide();
     		$(".ocultarColunaDupla").hide();
     		clicklinha = false;
     	}
     });

     /* **Click do check Descartar
      * 1- Ao ser selecionado, colorir a linha e ou ser desselecionado tirar a cor da linha
      * 2- Ao ser selecionado, caso complete a seleção de todos na coluna, marcar a opção checkall, caso quebre essa regra desmarcar o checkall
      * 3- Ao ser desselecionado, caso seja o único checkbox da página, esconder as colunas extras
      * 4- Ao ser selecionado, caso o checkall contrário esteja marcado, ele será desmarcado
      * 
      */
     $("[name='chkDescartar']").click(function() {
     	//1- colore a linha
     	if ($(this).is(":checked")) {
     		$(this).parentsUntil("tbody", ".selecionarLinha").addClass("table-checked");
     		$(this).parentsUntil("tbody").find('input[name="chkConfirmar"]').prop("checked",false);
     		
     		//4- marca o checkAll contrário
     		$("#chkConfirmarAll").prop('checked', false);
     	} else {
     		$(this).parentsUntil("tbody", ".selecionarLinha").removeClass("table-checked");
     	}
     	
     	//2- marca ou desmarca o checkall correpondente
     	var qtdCheckboxCheked = $("[name='chkDescartar']").not($("#chkDescartarAll")).filter(':checked').length;
     	var qtdCheckbox = $("[name='chkDescartar']").not($("#chkDescartarAll")).length;
     	if (qtdCheckbox == qtdCheckboxCheked) {
     		$("#chkDescartarAll").prop('checked', true);
     	} else if (qtdCheckbox != qtdCheckboxCheked) {
     		$("#chkDescartarAll").prop('checked', false);
     	}
     	
     	// 3- esconde as colunas
     	if ($('input[type="checkbox"]').filter(':checked').length == 0) {
     		$("table th:nth-child(-n+2)").hide();
     		$(".ocultarColunaDupla").hide();
     		clicklinha = false;
     	}
     });

     /* **CheckAll Confirmar
      * 1- Colorir todas as linhas, quando o checkAll Confirmar for marcado, ou quando for desmarcado descolorir
      * 2- Ocultar as colunas dos checks quando não houver nenhum deles selecionados
      * 3- Desmarcar o checkAll contrário, quando o correspondente é marcado
     */
     $("#chkConfirmarAll").click(function() {
     	$('input[name="chkConfirmar"]').prop("checked",function(i, val) {
     		if (!(i < 0)) {
     			// 1- Colore as linhas, quando o checkAll está marcado ou quando for desmacado
     			if ($("#chkConfirmarAll").is(":checked")) {
     				$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
     			} else {
     				$(this).parentsUntil("tbody",".selecionarLinha").removeClass("table-checked");
     			}
     			return $("#chkConfirmarAll").is(":checked");
     		}
     	});
     	
     	// 2- Oculta as colunas dos checks, quando não contém nenhum checkbox marcado
     	if (!$("#chkConfirmarAll").is(":checked")) {
     		if($('input[type="checkbox"]').filter(':checked').length == 0) {
     			$("table th:nth-child(-n+2)").hide();
     			$(".ocultarColunaDupla").hide();
     		}
     	} else {
     		//3- Desmarca o checkAll contrário
     		$('input[name="chkDescartar"]').prop("checked",function(i, val) {
     			return false;
     		});
     		$("#chkDescartarAll").prop("checked", false);
     	}
     });

     /* **CheckAll Indeferir
      * 1- Colorir todas as linhas, quando o checkAll Indeferir for marcado, ou quando for desmarcado descolorir
      * 2- Ocultar as colunas dos checks quando não houver nenhum deles selecionados
      * 3- Desmarcar o checkAll contrário, quando o correspondente é marcado
     */
     $("#chkDescartarAll").click(function() {
     	$('input[name="chkDescartar"]').prop("checked",function(i, val) {
     		if (!(i < 0)) {
     			// 1- Colore as linhas, quando o checkAll está marcado
     			if ($("#chkDescartarAll").is(":checked")) {
     				$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
     			} else {
     				$(this).parentsUntil("tbody",".selecionarLinha").removeClass("table-checked");
     			}
     			return $("#chkDescartarAll").is(":checked");
     		}
     	});
     	
     	// 2- Oculta as colunas dos checks, quando não contém nenhum checkbox marcado
     	if (!$("#chkDescartarAll").is(":checked")) {
     		if($('input[type="checkbox"]').filter(':checked').length == 0) {
     			$("table th:nth-child(-n+2)").hide();
     			$(".ocultarColunaDupla").hide();
     		}
     	} else {
     		//3- Desmarca o checkAll contrário
     		$('input[name="chkConfirmar"]').prop("checked",function(i, val) {
     			return false;
     		});
     		$("#chkConfirmarAll").prop("checked", false);
     	}
     });
  });  
     
     function visualizarOperacao(oncCodigo) {
    	 return $.ajax({
	          type: 'POST',
	          url: '../v3/verDetalheOperacao?&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_=true',
	          data: {
	              'oncCodigo': oncCodigo
	          },
	          success: function (detalhe) {
	        	  var trimData = $.trim(JSON.stringify(detalhe));
                  var resultDetalhe = JSON.parse(trimData); 

                  var zip = new JSZip();
                  zip.loadAsync(resultDetalhe.oncDetalhe, { base64: true })
                    .then(function(zip) {
                      zip.file("screen-capture.html").async("string").then(function (html) {
                        $("#dialogDetalhe").html(html);
                        var dialogWidth = $(window).width();
                        dialogWidth *= (dialogWidth <= 1318) ? 0.55 : (dialogWidth > 1318 && dialogWidth <= 1373) ? 0.8 : 0.9;
                        var dialogHeight = $(window).height() * 0.9;                 
                      
                        $("#dialogDetalhe").dialog({
                            modal: true,
                            autoOpen: true,
                            position: { my: "center", at: "center", of: window },
                            minWidth: dialogWidth,
                            maxHeight: dialogHeight,                       
                            classes: {
                                "ui-dialog": "no-close",
                                "ui-dialog-titlebar": "",                          
                             },
                             buttons: [
                             {
                                text: '<hl:message key="rotulo.botao.fechar"/>',
                                class: "btn btn-outline-danger",
                                click: function() {
                                  $( "form :input" ).prop("disabled", false);                        	                                
                                    $( this ).dialog("close");                              
                                }
                             }]
                         });
                      });
                    });

	          },
	          error: function (request, status, error) {
	          }
	      }).done(function () {
	      });
     }

     function enviar() {
    	 if($("[type=checkbox]").filter(':checked').length == 0 ) {
 			alert('<hl:message key="mensagem.erro.fila.op.deve.selecionar"/>');
             return false;
         }
         
    	 if( $("[name='chkDescartar']").not($("#chkDescartarAll")).filter(':checked').length > 0) {
    		 $("#dialogObsDescarte").dialog({
                 modal: true,
                 autoOpen: true,
                 position: { my: "center", at: "center", of: window },                                        
                 classes: {
                     "ui-dialog": "no-close",
                     "ui-dialog-titlebar": "",                          
                  },
                  buttons: [
                  {
                     text: '<hl:message key="rotulo.botao.cancelar"/>',
                     class: "btn btn-outline-danger",
                     click: function() {
                    	 $('#dialogDescarteErro').empty();
                         $('#dialogDescarteErro').hide();
                         continuaSubmit = false;                        	                                
                         $( this ).dialog("close");                              
                     }
                  },{
                     text: '<hl:message key="rotulo.botao.confirmar"/>',
                     class: "btn btn-primary",
                     click: function() {
                    	 $('#dialogDescarteErro').empty();
                         $('#dialogDescarteErro').hide(); 
                                                    
                         var obsDescarte = $("#obsDescarte").val();
                         if (obsDescarte == null || obsDescarte == "") {
                        	$('#dialogDescarteErro').append('<p class=\"mb-0\">' + '<hl:message key="mensagem.erro.fila.op.obs.descarte.vazio"/>' + '</p>');
                        	$('#dialogDescarteErro').show();                   	        
                         } else {
                        	var input = document.createElement('input');
                        	input.type = 'hidden';
                        	input.name = 'obsUsuarioDescarte';
                        	input.id = 'obsUsuarioDescarte';
                        	input.value = obsDescarte;

                            var formPrincipal = document.getElementById("formLista");
                            formPrincipal.appendChild(input);

                            formPrincipal.submit();
                         }                  
                     }
                  }]
              }); 
         } else {
        	 document.getElementById("formLista").submit();
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