<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csaCodigo = (request.getParameter("csaCodigo") != null ? TextHelper.join(request.getParameterValues("csaCodigo"), ";") : null);
String svcCodigoOrigem = (request.getParameter("svcCodigoOrigem") != null ? TextHelper.join(request.getParameterValues("svcCodigoOrigem"), ";") : null);
String svcCodigoDestino = (request.getParameter("svcCodigoDestino") != null ? TextHelper.join(request.getParameterValues("svcCodigoDestino"), ";") : null);

String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");

String msgDownload = (String) request.getAttribute("msgDownload");
%>
<c:set var="title">
  <%= ApplicationResourcesHelper.getMessage("rotulo.distribuir.consignacoes.titulo", responsavel) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <c:if test="${msgDownload != null}">
    <div class="alert alert-warning" role="alert"><c:out escapeXml="false" value="${msgDownload}"/></div>
  </c:if>
  <form id="form1" method="post" action="../v3/distribuirConsignacoesPorServicos">
    <%= SynchronizerToken.generateHtmlToken(request) %>
    <input type="hidden" name="acao" value="validar">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.distribuir.consignacoes.titulo"/></h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="csaCodigo"><hl:message key="rotulo.consignataria.singular"/></label>
            <hl:htmlcombo listName="lstConsignataria" name="csaCodigo" fieldValue="<%=Columns.CSA_CODIGO%>" fieldLabel="<%=Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR%>" notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel)%>" classe="form-control" size="5" selectedValue="<%=csaCodigo%>"/>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="svcCodigoOrigem"><hl:message key="rotulo.servico.singular"/> / <hl:message key="rotulo.distribuir.consignacoes.origem"/></label>
            <hl:htmlcombo listName="lstServico" name="svcCodigoOrigem" fieldValue="<%=Columns.SVC_CODIGO%>" fieldLabel="<%=Columns.SVC_DESCRICAO%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' classe="form-control" selectedValue="<%=svcCodigoOrigem%>"/>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="svcCodigoDestino"><hl:message key="rotulo.servico.singular"/> / <hl:message key="rotulo.distribuir.consignacoes.destino"/></label>
            <hl:htmlcombo listName="lstServico" name="svcCodigoDestino" fieldValue="<%=Columns.SVC_CODIGO%>" fieldLabel="<%=Columns.SVC_DESCRICAO%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' classe="form-control" size="5" selectedValue="<%=svcCodigoDestino%>"/>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <hl:campoMatriculav4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>' />
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <hl:campoCPFv4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' classe="form-control"/>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <hl:efetivaAcaoMotivoOperacaov4 inputSizeCSS="col-sm-12" msgConfirmacao="" />
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-third" href="#no-back" onClick="enviar('validar'); return false;">
        <svg width="20">
          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use>
        </svg><hl:message key="rotulo.botao.listarTodos"/>
      </a>
      <a class="btn btn-primary" href="#no-back" onClick="enviar('executar'); return false;"><hl:message key="rotulo.botao.concluir"/></a>
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
                  <div class="modal-body"><span><hl:message key="mensagem.distribuir.consignacoes.aguarde.execucao"/></span></div>            
                </div>
              </div>
            </div>
          </div>
        </div>
    </div>
  </form>
</c:set>
<c:set var="javascript">
   <script type="text/JavaScript">
     function formLoad() {
  	  focusFirstField();
  	}

  	function enviar(acao) {
  	  var controles = new Array("svcCodigoOrigem", "svcCodigoDestino");
  	  var msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.distribuir.consignacoes.selecione.servico.origem", responsavel)%>',
  	                        '<%=ApplicationResourcesHelper.getMessage("mensagem.distribuir.consignacoes.selecione.servico.destino", responsavel)%>');

  	  if (!ValidaCampos(controles, msgs)) {
  	    return false;
  	  }

  	  var svcDestinosField = document.getElementById("svcCodigoDestino");
  	  var svcDestinos = getFieldValue(svcDestinosField);
  	  if (svcDestinos == undefined || svcDestinos == null || svcDestinos.length < 2) {
  	    alert('<%=ApplicationResourcesHelper.getMessage("mensagem.distribuir.consignacoes.selecione.servico.destino", responsavel)%>');
  	    svcDestinosField.focus();
  	    return false;
  	  }

  	  if (typeof vfRseMatricula === 'function') {
  	    if (vfRseMatricula(true)) {
  	      enviarFormulario(acao);
  	    }
  	  } else {
  	    enviarFormulario(acao);
  	  } 
  	}

  	function enviarFormulario(acao) {
  	  if (acao == "validar" || confirmaAcaoConsignacao ()) {
  	    f0.acao.value = acao;
  	    f0.submit();
  	  }
  	}

  	f0 = document.forms["form1"];
  	window.onload = formLoad;

    f0.myFormOnSubmit = function() {
      $('#modalAguarde').modal({
        backdrop: 'static',
        keyboard: false
      });
    }

   </script>
   <hl:campoMatriculav4 scriptOnly="true"/>
   <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" scriptOnly="true" />
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
