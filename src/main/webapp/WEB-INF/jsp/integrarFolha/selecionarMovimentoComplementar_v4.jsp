<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> ades = (List<TransferObject>) request.getAttribute("lstConsignacao");
List<String> adeNumeroList = (List<String>) request.getAttribute("adeNumeroList");
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="title">
   <hl:message key="rotulo.folha.exportar.movimento.financeiro.complementar"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
	<div id="header-print">
		<% if ("v4".equals(versaoLeiaute)) { %>
			<img src="../img/econsig-logo.svg">
		<% } else { %>
			<img src="../img/logo_sistema_v5.png">
		<%} %>
		<p id="date-time-print"></p>
	</div>
    <div class="page-title">
      <div class="row d-print-none">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <button id="acoes" class="btn btn-primary" type="submit" onClick="imprimir()"><hl:message key="rotulo.botao.imprimir"/></button>
          </div>
        </div>
      </div>
    </div>

    <form action="../v3/exportarMovimentoComplementar?acao=exportar" method="post" name="form1">
      <%= SynchronizerToken.generateHtmlToken(request) %>
      <% for (String adeNumero : adeNumeroList) { %>
      <input type="hidden" name="ADE_NUMERO" value="<%=TextHelper.forHtmlAttribute(adeNumero)%>">
      <% } %>
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
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col" width="3%" class="colunaUnica" style="display: none;">
                  <div class="form-check"><hl:message key="rotulo.acoes.selecionar.todos"/><br/>
                    <input type="checkbox" class="form-check-input ml-0" name="checkAll_ADE" id="checkAll_ADE" data-bs-toggle="tooltip" data-original-title="<hl:message key="rotulo.acoes.selecionar.todos"/>" alt="<hl:message key="rotulo.acoes.selecionar.todos"/>" title="<hl:message key="rotulo.acoes.selecionar.todos"/>">
                  </div>                  
                </th>
                <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.responsavel"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
                <th scope="col"><hl:message key="rotulo.servico.singular"/></th>
                <th scope="col"><hl:message key="rotulo.servidor.singular"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.data.inclusao"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.valor.folha.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.pagas"/></th>
                <th scope="col"><hl:message key="rotulo.consignacao.status"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <%=JspHelper.msgRstVazio(ades.size() == 0, 13, responsavel)%>
              
              <%
              for (TransferObject ade : ades) {
                  String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
              %>
              
               <tr class="selecionarLinha">
                 <td class="colunaUnica" aria-label="<hl:message key="rotulo.acoes.selecionar"/>" title="<hl:message key="rotulo.acoes.selecionar"/>" data-bs-toggle="tooltip" data-original-title="<hl:message key="rotulo.acoes.selecionar"/>" style="display: none;">
                    <div class="form-check">
                      <input type="checkbox" class="form-check-input ml-0" name="ADE_NUMERO_SELECIONADO" id="ADE_NUMERO_<%=TextHelper.forHtmlAttribute(adeNumero)%>" value="<%=TextHelper.forHtmlAttribute(adeNumero)%>">
                    </div>
                 </td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_FOLHA))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS))%></td>
                 <td class="selecionarColuna"><%=TextHelper.forHtmlContent(ade.getAttribute(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS))%></td>
                 <td class="acoes">
                    <a class="ico-action" href="#no-back"><hl:message key="rotulo.acoes.selecionar"/></a>
                 </td>
               </tr>

              <% } %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="13">
                  <hl:message key="mensagem.info.exportar.movimento.financeiro.complementar.lista.consignacao" /> 
                  <span class="font-italic"> - <%=request.getAttribute("_paginacaoSubTitulo")%></span>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
      </div>
      <div id="exportar-todas-movimentacoes" class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.exportar.movimento.financeiro.complementar.exportar.todos"/></h2>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-sm-12 col-md-6">
              <div class="form-group mb-1" role="radiogroup" aria-labelledby="agDescricao">
                <div><span id="agDescricao"><hl:message key="mensagem.ajuda.exportar.movimento.financeiro.complementar.exportar.todos"/></span></div>
                <div class="form-check form-check-inline pt-3">
                  <input class="form-check-input ml-1" type="radio" name="exportarTodos" id="exportarTodosSim" value="S" title="<hl:message key="rotulo.sim"/>">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="exportarTodosSim"><hl:message key="rotulo.sim"/></label>
                </div>
                <div class="form-check form-check-inline pt-3">
                  <input class="form-check-input ml-1" type="radio" name="exportarTodos" id="exportarTodosNao" value="N" title="<hl:message key="rotulo.nao"/>" checked>
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="exportarTodosNao"><hl:message key="rotulo.nao"/></label>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </form>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/exportarMovimentoComplementar?acao=iniciar", request))%>');"><hl:message key="rotulo.botao.voltar"/></a>
      <a class="btn btn-primary" id="btnExportar" href="#no-back" onClick="exportar(); return false;"><svg width="20"><use xlink:href="../img/sprite.svg#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
    </div>
    
    <!-- Modal aguarde -->
    <div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog-upload modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-body">
              <div class="row">
                <div class="col-md-12 d-flex justify-content-center">
                  <img src="../img/loading.gif" class="loading">
                </div>
                <div class="col-md-12">
                  <div class="modal-body"><span><hl:message key="mensagem.info.exportar.movimento.financeiro.complementar.aguarde.execucao"/></span></div>            
                </div>
              </div>
            </div>
          </div>
        </div>
    </div>
	<% if ("v4".equals(versaoLeiaute)) { %>
	  <div id="footer-print">
		<img src="../img/footer-logo.png">
	  </div>
	<% } else { %>
		<div id="footer-print">
			<img src="../img/footer-logo-v5.png">
		</div>
	<%} %>
</c:set>
<c:set var="javascript">
	<style>
		@media print {
			*{
				margin: 0;
				padding: 0;
				color: #000 !important;
			}
			tfoot{display: none;}
			#exportar-todas-movimentacoes {display: none;}
	        table th:last-child {display: none;}
		    table td:last-child {display: none;}
		    table thead tr th, table tbody tr td{ border: 1px solid #000 !important;} 
			#menuAcessibilidade {display: none;}
			#header-print img {width: 10%;}
			#footer-print {position: absolute; bottom: 0;}
		}
		@page{margin: 0.5cm;}
	</style>
<script>
	function injectDate(){
		const dateTimePrint = document.querySelector('#date-time-print');
		const printDate = new Date();
		printDate.toLocaleString("pt-br");
		dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);
	}
</script>
<script type="text/JavaScript">
	var f0 = document.getElementsByName("form1")[0];
	
	function imprimir() {
		injectDate();
	    window.print();
	}
	
	var verificarCheckbox = function () {
		var checked = $("table tbody tr input[type=checkbox]:checked").length;
		var total = $("table tbody tr input[type=checkbox]").length;
		$("input[id*=checkAll_]").prop('checked', checked == total);
		if (checked == 0) {
			$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").hide();
		} else {
			$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").show();
		}
	};
	
	$("table tbody tr td").not("td.colunaUnica, td.acoes").click(function (e) {
		$(e.target).parents('tr').find('input[type=checkbox]').click();
	});
	
	$("a.ico-action").click(function (e) {
	    $(e.target).parents('tr').find('input[type=checkbox]').click();
	});
	
	$("table tbody tr input[type=checkbox]").click(function (e) {
		verificarCheckbox();
		var checked = e.target.checked;
		if (checked) {
			$(e.target).parents('tr').addClass("table-checked");
		} else {
			$(e.target).parents('tr').removeClass("table-checked");
		}
	});
	
	$("input[id*=checkAll_").click(function (e){
		var checked = e.target.checked;
		$('table tbody tr input[type=checkbox]').prop('checked', checked);
		if (checked) {
			$("table tbody tr").addClass("table-checked");
		} else {
			$("table tbody tr").removeClass("table-checked");
		}
		verificarCheckbox();
	}); 
	
	function validaFormExportacao() {
	  with (document.form1) {
	    var exportarTodosChecked = document.getElementById('exportarTodosSim').checked;
	    if (!exportarTodosChecked) {
	      var tam = ADE_NUMERO_SELECIONADO.length;
	      if (tam == undefined) {
	        if (ADE_NUMERO_SELECIONADO.checked == false) {
	          alert('<hl:message key="mensagem.erro.exportar.movimento.financeiro.complementar.nenhum.registro.selecionado"/>');
	          return false;
	        }
	      } else {
	        var qtd = 0;
	        for(var i = 0; i < tam; i++) {
	          if (ADE_NUMERO_SELECIONADO[i].checked == true) {
	            qtd++;
	          }
	        }
	        if (qtd <= 0) {
	          alert('<hl:message key="mensagem.erro.exportar.movimento.financeiro.complementar.nenhum.registro.selecionado"/>');
	          return false;
	        }
	      }
	    }
	    
	    var msg = '';
	    if (exportarTodosChecked) {
	        msg = '<hl:message key="mensagem.aviso.exportar.movimento.financeiro.complementar.confirmacao.todos"/>';
	    } else {
	        msg = '<hl:message key="mensagem.aviso.exportar.movimento.financeiro.complementar.confirmacao.selecionados"/>';
	    }
	    
	    if (confirm(msg)) {
	      return true;
	    }
	  }
	
	  return false;
	}
	
	function exportar() {
	  if (validaFormExportacao()) {
	    $('#modalAguarde').modal({
	      backdrop: 'static',
	      keyboard: false
	    });
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
