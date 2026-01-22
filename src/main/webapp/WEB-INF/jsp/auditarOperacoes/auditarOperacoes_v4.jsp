<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String codigoEntidade = responsavel.getCodigoEntidade();
String tipoEntidade = responsavel.getTipoEntidade();

String diffDias = (String) request.getAttribute("diffDias");
List<TransferObject> logsAuditoria = (List<TransferObject>) request.getAttribute("logsAuditoria");
List<TransferObject> tiposEntidade = (List<TransferObject>) request.getAttribute("tiposEntidade");
List<TransferObject> funcoesAuditaveis = (List<TransferObject>) request.getAttribute("funcoesAuditaveis");

String periodo_ini = JspHelper.verificaVarQryStr(request, "periodoIni");
String periodo_fim = JspHelper.verificaVarQryStr(request, "periodoFim");
String fun_codigo = JspHelper.verificaVarQryStr(request, "FUN_CODIGO");
String ten_codigo = JspHelper.verificaVarQryStr(request, "TEN_CODIGO");
String usu_login = JspHelper.verificaVarQryStr(request, "USU_LOGIN");
String log_obs = JspHelper.verificaVarQryStr(request, "LOG_OBS");
String exibeAuditado = JspHelper.verificaVarQryStr(request, "EXIBE_AUDITADO");
boolean naoAuditado = !exibeAuditado.equals(CodedValues.TPC_SIM);

%>
<c:set var="title">
  <hl:message key="rotulo.auditoria"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
      <div class="card">
        <div class="card-body p-0">
          <form action="../v3/auditarOperacoes?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="formPesqAvancada">
            <div class="opcoes-avancadas">
              <a class="opcoes-avancadas-head collapsed" href="#faq1" data-bs-toggle="collapse" aria-expanded="false" aria-controls="faq1"><hl:message key="rotulo.pesquisa.avancada"/></a>
              <div class="ml-4 collapse" id="faq1" style="">
                <div class="row form-group col-sm-12 col-md-6 mt-4">
                  <span id="periodo"><hl:message key="rotulo.pesquisa.data.periodo"/></span>
                  <div class="row" role="group" aria-labelledby="periodo">
                    <div class="form-check pt-2 col-sm-12 col-md-1">
                      <div class="float-left align-middle mt-4 form-control-label">
                        <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.pesquisa.data.prefixo.inicio"/></label>
                      </div>
                    </div>
                    <div class="form-check pt-2 col-sm-12 col-md-5">
                       <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="form-control w-100" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoIni"))%>"/>
                    </div>
                    <div class="form-check pt-2 col-sm-12 col-md-1">
                      <div class="float-left align-middle mt-4 form-control-label">
                        <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.pesquisa.data.prefixo.fim"/></label>
                      </div>
                    </div>
                    <div class="form-check pt-2 col-sm-12 col-md-5">
                       <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="form-control w-100" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoFim"))%>"/>
                    </div>
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm-12 col-md-6">
                    <label for="tipoDeEntidade"><hl:message key="rotulo.auditoria.tipo.entidade"/></label>
                    <hl:htmlcombo listName="tiposEntidade" name="TEN_CODIGO" fieldValue="<%=Columns.TEN_CODIGO%>" fieldLabel="<%=Columns.TEN_DESCRICAO%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm-12  col-md-6">
                    <label for="funcao"><hl:message key="rotulo.auditoria.funcao"/></label>
                    <hl:htmlcombo listName="funcoesAuditaveis" name="FUN_CODIGO" di="FUN_CODIGO" fieldValue="<%=Columns.FUN_CODIGO%>" fieldLabel="<%=Columns.FUN_DESCRICAO%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm-12 col-md-6">
                    <label for="observacao"><hl:message key="rotulo.auditoria.observacao"/></label>
                    <input type="text" class="form-control" id="LOG_OBS" name="LOG_OBS" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs", responsavel)%>">
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm-12 col-md-6">
                    <label for="responsavel"><hl:message key="rotulo.auditoria.responsavel"/></label>
                    <input type="text" class="form-control" id="USU_LOGIN" name="USU_LOGIN" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.responsavel", responsavel)%>">
                  </div>
                </div>
                <div class="row">
                  <div class="col-sm-12 col-md-6">
                    <div class="form-group" role="radiogroup" aria-labelledby="Auditado">
                      <div ><span id="Auditado"><hl:message key="rotulo.auditoria.auditado"/></span></div>
                      <div class="form-check form-check-inline pt-2">
                        <input class="form-check-input ml-1" type="radio" name="EXIBE_AUDITADO" id="cbAuditadosim" value="<%=(String)CodedValues.TPC_SIM%>" <%=(String)(exibeAuditado.toString().equals(CodedValues.TPC_SIM) ? "checked" :  "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="cbAuditadoSim"><hl:message key="rotulo.sim"/></label>
                      </div>
                        <div class="form-check form-check-inline pt-2">
                        <input class="form-check-input ml-1" type="radio" name="EXIBE_AUDITADO" id="cbAuditadonao" value="<%=(String)CodedValues.TPC_NAO%>" <%=(String)(!exibeAuditado.toString().equals(CodedValues.TPC_SIM) ? "checked" :  "")%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="cbAuditadoNao"><hl:message key="rotulo.nao"/></label>
                      </div>
                    </div>
                  </div>
                </div>
              <div class="btn-action mr-4">
                 <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/auditarOperacoes?acao=iniciar&<%=(String)(SynchronizerToken.generateToken4URL(request))%>')" ><hl:message key="rotulo.botao.voltar"/></a>
                 <a class="btn btn-primary" id="btnEnvia" href="#no-back" onclick="pesquisar();">
                    <hl:message key="rotulo.botao.pesquisar"/>
                 </a>
              </div>
              </div>
            </div>
          </form>
        </div>
      </div>
      <%-- ******** FIM FILTRO DE PESQUISA ******** --%>

<form name="formAuditoria" method="post" action="../v3/auditarOperacoes?acao=auditar&<%=SynchronizerToken.generateToken4URL(request)%>">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="mensagem.auditoria.tabela.registros"/></h2>
        </div>
        <div class="card-body table-responsive ">
          <table class="table table-striped table-hover" id="tabelaRegistrosAuditoria">
            <thead>
              <tr>
                <th class="colunaUnica" scope="col" title='<hl:message key="rotulo.auditoria.selecionar.multiplos.registros"/>' style="display: none;" width="10%">
                  <div class="form-check">
                    <input type="checkbox" class="form-check-input ml-0" name="checkAll" id="checkAll">
                  </div>
                </th>
                <th scope="col"><hl:message key="rotulo.auditoria.tipo.log"/></th>
                <th scope="col"><hl:message key="rotulo.auditoria.tipo.entidade"/></th>
                <th scope="col"><hl:message key="rotulo.auditoria.funcao"/></th>
                <th scope="col"><hl:message key="rotulo.auditoria.data"/></th>
                <th scope="col"><hl:message key="rotulo.auditoria.observacao"/></th>
                <th scope="col"><hl:message key="rotulo.auditoria.auditado"/></th>
                <th scope="col"><hl:message key="rotulo.ip"/></th>
                <th scope="col"><hl:message key="rotulo.auditoria.usuario"/></th>
                <% if (naoAuditado) { %>
                <th scope="col"><hl:message key="rotulo.auditoria.acoes"/></th>
                <% } else { %>
                <th scope="col"><hl:message key="rotulo.auditoria.data.auditoria"/></th>
                <th scope="col"><hl:message key="rotulo.auditoria.auditor"/></th>
                <% } %>
              </tr>
            </thead>
            <tbody>

        <%
            if (logsAuditoria != null && !logsAuditoria.isEmpty()) {
                CustomTransferObject log = null;
                String audCodigo, auditado, logData, logIp, logObs, tloDescricao, usuLogin, funDescricao, tenDescricao, dataAuditoria, usuLoginAuditor;
                Iterator it = logsAuditoria.iterator();
                while (it.hasNext()) {
                  log = (CustomTransferObject)it.next();
                  audCodigo = String.valueOf((Integer)log.getAttribute("AUDITORIA_CODIGO"));
                  tloDescricao = (String)log.getAttribute("TLO_DESCRICAO");
                  tenDescricao = (String)log.getAttribute("TEN_DESCRICAO");
                  funDescricao = (String)log.getAttribute("FUN_DESCRICAO");
                  logData = DateHelper.toDateTimeString((Date)log.getAttribute("DATA"));
                  logObs = (String)log.getAttribute("OBSERVACAO");
                  auditado = log.getAttribute("AUDITADO").toString();
                  logIp = (String)log.getAttribute("IP");
                  usuLogin = (String)log.getAttribute("USU_LOGIN");
                  dataAuditoria = !TextHelper.isNull(log.getAttribute("DATA_AUDITORIA")) ? DateHelper.toDateTimeString((Date)log.getAttribute("DATA_AUDITORIA")) : "";
                  usuLoginAuditor = !TextHelper.isNull(log.getAttribute("USU_LOGIN_AUDITOR")) ? (String)log.getAttribute("USU_LOGIN_AUDITOR") : "";
        %>
               <tr class="selecionarLinha">
		        	<td class="colunaUnica" aria-label="" title="" data-bs-toggle="tooltip" data-original-title="" style="display: none;">
                    <div class="form-check">
                      	<input type="checkbox" class="form-check-input ml-0" name="AUD_CODIGO" id="AUD_CODIGO_<%=(String)audCodigo%>" value="<%=TextHelper.forHtmlAttribute(audCodigo)%>">
                  	</div>
                  </td>
                <td class="selecionarColuna"><%=(String)tloDescricao%></td>
                <td class="selecionarColuna"><%=(String)tenDescricao%></td>
                <td class="selecionarColuna"><%=(String)funDescricao%></td>
                <td class="selecionarColuna"><%=(String)logData%></td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContentComTags(logObs)%></td>
                <td class="selecionarColuna"><%=(String)auditado%></td>
                <td class="selecionarColuna"><%=(String)logIp%></td>
                <td class="selecionarColuna"><%=(String)usuLogin%></td>
                  <% if (naoAuditado) { %>
                    <td class="selecioneCheckBox"><a href="#" id="selecioneCheckBox"  onclick ="escolhechk('Selecionar',this)"><hl:message key="rotulo.auditoria.selecionar"/></a></td>
                  <% } else { %>
                    <td class="selecionarColuna"><%=(String)dataAuditoria%></td>
                    <td class="selecionarColuna"><%=(String)usuLoginAuditor%></td>
                  <% } %>
              </tr>
                <% } %>
        <% } else { %>
                <tr>
                  <td colspan="9"><hl:message key="mensagem.auditoria.nenhum.encontrado"/></td>
                </tr>
        <% } %>
            </tbody>
           <tfoot>
              <tr>
                <td colspan="8">
                  <hl:message key="rotulo.auditoria.listagem.registro"/> -
                  <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
                </td>
              </tr>
           </tfoot>
         </table>
    <hl:htmlinput name="periodoIni" di="periodoIni" type="hidden" value="<%=TextHelper.forHtmlAttribute(periodo_ini)%>" />
    <hl:htmlinput name="periodoFim" di="periodoFim" type="hidden" value="<%=TextHelper.forHtmlAttribute(periodo_fim)%>" />
    <hl:htmlinput name="FUN_CODIGO" di="FUN_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(fun_codigo)%>" />
    <hl:htmlinput name="TEN_CODIGO" di="TEN_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(ten_codigo)%>" />
    <hl:htmlinput name="LOG_OBS" di="LOG_OBS" type="hidden" value="<%=TextHelper.forHtmlAttribute(log_obs)%>" />
    <hl:htmlinput name="USU_LOGIN" di="USU_LOGIN" type="hidden" value="<%=TextHelper.forHtmlAttribute(usu_login)%>" />
    <hl:htmlinput name="EXIBE_AUDITADO" di="EXIBE_AUDITADO" type="hidden" value="<%=TextHelper.forHtmlAttribute(exibeAuditado)%>" />
      </div>
      <div class="card-footer">
        <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
   </div>
   <% if (naoAuditado) { %>
   <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.auditoria.deseja.auditar.todos.titulos"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="col-sm-12 col-md-6">
            <div><span id="descricao"><hl:message key="mensagem.auditoria.deseja.auditar.todos"/></span></div>
          <div class="form-group mb-1" role="radiogroup" aria-labelledby="agDescricao">
              <div class="form-check form-check-inline">
              <input class="form-check-input ml-1" type="radio" name="APLICAR_TODOS" id="aplicarTodosSim" value="S" title='<hl:message key="rotulo.sim"/>'>
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="aplicarTodosSim"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline">
              <input class="form-check-input ml-1" type="radio" name="APLICAR_TODOS" id="aplicarTodosNao" value="N" title='<hl:message key="rotulo.nao"/>' checked>
              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="aplicarTodosNao"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>
        </div>
      </div>
     </div>
   </div>
   <% } %>
</form>
  <div class="btn-action">
     <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
     <a class="btn btn-primary" href="#confirmarSenha" data-bs-toggle="modal" onClick="if (auditar()) { document.formAuditoria.submit(); } return false;"><hl:message key="rotulo.botao.confirmar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<script>
function auditar() {
  with (document.formAuditoria) {
	var aplicarTodos = document.getElementById('aplicarTodosSim').checked;
    if (!aplicarTodos) {
	  var tam = AUD_CODIGO.length;
      if (tam == undefined) {
        if (AUD_CODIGO.checked == false) {
          alert('<hl:message key="mensagem.erro.auditoria.selecionar.registro"/>');
          return false;
        }
      } else {
        var qtd = 0;
        for(var i = 0; i < tam; i++) {
          if (AUD_CODIGO[i].checked == true) {
            qtd++
          }
        }
        if (qtd <= 0) {
          alert('<hl:message key="mensagem.erro.auditoria.selecionar.registro"/>');
          return false;
        }
      }
    }

    var msg = '';
    if (aplicarTodos) {
    	msg = '<hl:message key="mensagem.auditoria.confirmacao.todos"/>';
    } else {
    	msg = '<hl:message key="mensagem.auditoria.confirmacao.selecionados"/>';
    }

    if (confirm(msg)) {
      return true;
    }
    return false;
  }
}

function showHidePesquisar() {
  var pesquisar = document.getElementById('pesquisarBar');
  var image = document.getElementById('showPesquisarImage');

  if(pesquisar.style.display == 'none') {
    pesquisar.style.display = 'block';
    image.src = '../img/icones/minus.gif';
  } else {
    pesquisar.style.display = 'none';
    image.src = '../img/icones/plus_pesquisar.gif';
  }
}

function refresh() {
  postData('../v3/auditarOperacoes?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>');
}

function validaFormPesqAvancada() {
  with (document.formPesqAvancada) {
  // Valida período informado
    if (periodoIni != null && periodoIni.value != '' && (!verificaData(periodoIni.value))) {
      periodoIni.focus();
      return false;
    }
    if (periodoFim != null && periodoFim.value != '' && (!verificaData(periodoFim.value))) {
      periodoFim.focus();
      return false;
    }
    if (periodoIni != null && periodoFim != null) {
        var diasDif = '';
        <% if (!diffDias.equals("")) { %>
             diasDif = <%=TextHelper.forJavaScriptBlock(diffDias)%>;
        <% } %>
        var PartesData = new Array();
        PartesData = obtemPartesData(periodoIni.value);
        var Dia = PartesData[0];
        var Mes = PartesData[1];
        var Ano = PartesData[2];
        PartesData = obtemPartesData(periodoFim.value);
        var DiaFim = PartesData[0];
        var MesFim = PartesData[1];
        var AnoFim = PartesData[2];
        if (!VerificaPeriodoExt(Dia, Mes, Ano, DiaFim, MesFim, AnoFim, diasDif)) {
            periodoIni.focus();
            return false;
        }
    }

    if (trim(USU_LOGIN.value) != '' && trim(USU_LOGIN.value).length < 3) {
      // Se está pesquisando pelo USU_LOGIN, a quantidade mínima de caracteres são 3
      alert('<hl:message key="mensagem.informe.auditoria.caracteres.responsavel"/>');
      return false;
    }

    if (trim(LOG_OBS.value) != '' && trim(LOG_OBS.value).length < 3) {
        // Se está pesquisando pelo LOG_OBS, a quantidade mínima de caracteres são 3
        alert('<hl:message key="mensagem.informe.auditoria.caracteres.observacao"/>');
        return false;
    }

  return true;
  }
}

function pesquisar() {
  if (validaFormPesqAvancada()) {
    with (document.formPesqAvancada) {
      submit();
    }
  }
}

/* **Click na linha
 * 1- Mostrar a coluna de checkbox, quando se clica na linha.
*/
var clicklinha = false;

$(".selecionarColuna").click(function() {
  // 1- Seleciona a linha e mostra a coluna dos checks

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



</script>
</c:set>
 <t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>