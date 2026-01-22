<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, java.text.*"%>
<%@ page import="com.zetra.econsig.folha.exportacao.ParametrosExportacao" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.parser.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean isEst = (Boolean) request.getAttribute("isEst");
String nomeArqSaida = (String) request.getAttribute("nomeArqSaida");
boolean reexportar = (Boolean) request.getAttribute(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo());
String periodAnterior = (String) request.getAttribute("periodAnterior");
String rotuloOrgao = (String) request.getAttribute("rotuloOrgao");
String rotuloEstabelecimento = (String) request.getAttribute("rotuloEstabelecimento");
List<?> verbas = (List<?>) request.getAttribute("verbas");
boolean expPorEstab = (Boolean) request.getAttribute("expPorEstab");
List<?> orgCodigos = (List<?>) request.getAttribute("orgCodigos");
List<?> estCodigos = (List<?>) request.getAttribute("estCodigos");
%>

<c:set var="title">
  <hl:message key="rotulo.folha.exportacao.movimento.financeiro.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form method="POST" action="../v3/processarMovimento?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <div class="btn-action">
    <% if (!TextHelper.isNull(nomeArqSaida)) { %>
      <a class="btn btn-primary" href="#no-back" onclick="fazDownload('<%=TextHelper.forJavaScriptAttribute(nomeArqSaida)%>');"><hl:message key="mensagem.folha.download.arquivo.nome.clique.aqui" arg0="<%=nomeArqSaida%>"/></a>
    <% } %>
    <% if (responsavel.isOrg() && !isEst && !reexportar) { %>
      <a class="btn btn-primary" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute("../v3/processarMovimento?acao=iniciar&org_codigo=" + responsavel.getCodigoEntidade() + "&reexportar=true&" + SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.reexportar"/></a>
    <% } %>
  </div>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.folha.informacoes.periodo.atual"/></h2>
      </div>
      <div class="card-body">
        <div class="mt-2">
          <dl class="row data-list firefox-print-fix">
            <dt class="col-5"><hl:message key="rotulo.folha.periodo.atual"/>:</dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(periodAnterior)%></dd>
            <dt class="col-5"><hl:message key="rotulo.folha.data.hora"/>:</dt>
            <dd class="col-7"><%=DateHelper.toDateTimeString(DateHelper.getSystemDatetime())%>s</dd>
          </dl>
        </div>
        <div class="table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th class="colunaUnica" id="chkColumn" scope="col" width="10%" title="Selecione todos os arquivos para exportação">
                  <div class="form-check">
                    <input type="checkbox" class="form-check-input ml-0" name="checkAll" id="checkAll" checked>
                  </div>
                </th>
                <th scope="col"><hl:message key="rotulo.servico.singular"/>/<hl:message key="rotulo.verba.singular"/></th>
                <th scope="col"><hl:message key="rotulo.folha.numero.parcela.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.folha.valor.parcela.abreviado"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>              
  <%
    double vlrTotalParc = 0;
    int qtdeTotalCtr = 0;
    
    String svc_descricao, cnv_cod_verba, qtde, vlr;
    
    Iterator<?> it = verbas.iterator();
    CustomTransferObject servico = null;
    while (it.hasNext()) {
      servico = (CustomTransferObject)it.next();
      svc_descricao = servico.getAttribute("SVC_DESCRICAO").toString();
      cnv_cod_verba = (String)servico.getAttribute("CNV_COD_VERBA");
      if (cnv_cod_verba == null || cnv_cod_verba.equals("")) {
        cnv_cod_verba = servico.getAttribute("SVC_IDENTIFICADOR").toString();
      }
      qtde = servico.getAttribute("QTDE").toString();
      vlr = servico.getAttribute("VLR").toString();
  %>
              <tr>
                <td class="colunaUnica">
                  <div class="form-check">
                    <input value="<%=TextHelper.forHtmlAttribute(cnv_cod_verba)%>" type="checkbox" name="verbas" checked>
                  </div>
                </td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(svc_descricao)%> - <%=TextHelper.forHtmlContent(cnv_cod_verba)%></td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(qtde)%></td>
                <td class="selecionarColuna"><%=NumberHelper.reformat(vlr, "en", NumberHelper.getLang(), true)%></td>
                <td class="acoes selecionarColuna">
                  <a href="#" name="selecionaAcaoSelecionar"><hl:message key="rotulo.acoes.selecionar"/></a>
                </td>
              </tr>
  <%
      qtdeTotalCtr += Integer.parseInt(qtde);
      vlrTotalParc += Double.valueOf(vlr).doubleValue();
    }
  %>
  
              <tr class="font-weight-bold">
                <td class="selecionarColuna"><hl:message key="rotulo.folha.total"/></td>
                <td class="selecionarColuna"><%=TextHelper.forHtmlContent(qtdeTotalCtr)%></td>
                <td class="selecionarColuna" colspan="3"><%=NumberHelper.format(vlrTotalParc, NumberHelper.getLang(), true)%></td>
              </tr>
            </tbody>
          </table>
        </div>           
      </div>
    </div>
  <% if (responsavel.isCseSup() || isEst) { %>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.folha.opcoes.integracao.folha"/></h2>
      </div>
      <div class="card-body">
        <div class="col-sm-12 col-md-6 pt-3">
          <div class="form-group mb-1" role="radiogroup" aria-labelledby="escolhaOpcaoArquivo">
            <span id="escolhaOpcaoArquivo"><hl:message key="rotulo.folha.opcoes.arquivo"/></span>
            <div class="form-check pt-3">
              <input class="form-check-input ml-1" type="radio" name="opcao" value="1" id="arquivoUnico">
              <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="arquivoUnico"><hl:message key="rotulo.folha.arquivo.unico"/></label>
            </div>
            <div class="form-check">
              <input class="form-check-input ml-1" type="radio" name="opcao" value="2" id="arquivoSeparadosOrgao">
              <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="arquivoSeparadosOrgao"><%=ApplicationResourcesHelper.getMessage("rotulo.folha.arquivos.separados.arg", responsavel, expPorEstab ? rotuloEstabelecimento : rotuloOrgao)%></label>
            </div>
            <div class="form-check">
              <input class="form-check-input ml-1" type="radio" name="opcao" value="3" id="arquivoSeparadosVerba">
              <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="arquivoSeparadosVerba"><hl:message key="rotulo.folha.arquivos.separados.verba"/></label>
            </div>
            <div class="form-check">
              <input class="form-check-input ml-1" type="radio" name="opcao" value="4" id="arquivosSeparadosOrgaoVerba">
              <label class="form-check-label labelSemNegrito col-md-12 col-sm-12 ml-1 pr-4" for="arquivosSeparadosOrgaoVerba"><%=ApplicationResourcesHelper.getMessage("rotulo.folha.arquivos.separados.arg.verba", responsavel, expPorEstab ? rotuloEstabelecimento : rotuloOrgao)%></label>
            </div>
          </div>
        </div>               
      </div>
    </div>
  <% } else { %>
    <input type="hidden" name="opcao" value="1">
  <% } %>
  
    <div class="btn-action">
      <% if (responsavel.isOrg() && !isEst) { %>
      <a href="#no-back" class="btn btn-outline-danger" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar" /></a>
      <% } else { %>
      <a href="#no-back" class="btn btn-outline-danger" onClick="postData('../v3/exportarMovimento?acao=<%=TextHelper.forJavaScriptAttribute(reexportar ? ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo() : "iniciar")%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.botao.cancelar" /></a>
      <% } %>
      <a href="#no-back" class="btn btn-primary" onClick="if (vf_exporta_movimento()) {f0.submit();} return false;"><hl:message key="rotulo.botao.concluir" /></a>     
      <input type="hidden" name="acao" value="processar">
      <input type="hidden" name="<%=ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo()%>" value="<%=reexportar ? "true" : ""%>">
    </div>

  <%
    if (orgCodigos != null) {
      Iterator<?> it2 = orgCodigos.iterator();
      while (it2.hasNext()) {
        out.print("<input type=\"hidden\" name=\"org_codigo\" value=\"" + it2.next() + "\">");
      }
    }
    if (estCodigos != null) {
      Iterator<?> it2 = estCodigos.iterator();
      while (it2.hasNext()) {
        out.print("<input type=\"hidden\" name=\"est_codigo\" value=\"" + it2.next() + "\">");
      }
    }
  %>    
  </form>
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
				 <div class="modal-body"><span><hl:message key="mensagem.folha.aguarde.exportacao"/></span></div>            
			   </div>
			 </div>
		   </div>
		 </div>
	   </div>
	  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
  function formLoad(){}
  
  function vf_exporta_movimento() {
    var checked1 = false, checked2 = false;
    for (i=0; i < f0.elements.length; i++) {
      var e = f0.elements[i];
      if (((e.type == 'check') || (e.type == 'checkbox') || (e.type == 'radio')) && (e.checked == true)) {
        if ((e.type == 'check') || (e.type == 'checkbox'))
          checked1 = true;
        else
          checked2 = true;
      }
    }
    if (!checked1) {
      alert('<hl:message key="mensagem.folha.escolher.codigo.verba"/>');
      return false;
    }
  
  <% if (responsavel.isCseSup() || isEst) { %>
    if (!checked2) {
      alert('<hl:message key="mensagem.folha.escolher.opcao.geracao.arquivo"/>');
      return false;
    }
  <% } %>
  
    var ok = (confirm('<hl:message key="mensagem.folha.confirmacao.exportar.movimento"/>'));
  
    if (ok) {    
    	$('#modalAguarde').modal({
			backdrop: 'static',
			keyboard: false
		});
    }
  
    return ok;
  }
  
  function fazDownload(nomeArqSaida){
    postData('../v3/downloadArquivo?arquivo_nome='+ nomeArqSaida + '&tipo=movimento&skip_history=true&<%=SynchronizerToken.generateToken4URL(request)%>','download');
  }
  </script>
  <script type="text/javascript">
    /* **Click na linha
     * 1- Mostrar a coluna de checkbox, quando se clica na linha.
    */
    var clicklinha = false;
  
    $(".selecionarColuna").click(function() {
      // 1- Seleciona a linha e mostra a coluna dos checks
      
      var checked = $("table tbody tr input[type=checkbox]:checked").length;
  
      if (checked == 0) {
  
        if (clicklinha) {
          $("#chkColumn").hide();
          $(".colunaUnica").hide();
        } else {
          $("#chkColumn").show();
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
        $(e.target).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
      } else {
        $(e.target).parentsUntil("tbody",".selecionarLinha").removeClass("table-checked");
      }
    });
  
    $("input[id*=checkAll").click(function (e){
      var checked = e.target.checked;
      $('table tbody tr input[type=checkbox]').prop('checked', checked);
      if (checked) {
        $(".selecionarLinha").addClass("table-checked");
      } else {
        $(".selecionarLinha").removeClass("table-checked");
      }
      verificarCheckbox();
    });  
  </script>
  <script type="text/JavaScript">
    var f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>