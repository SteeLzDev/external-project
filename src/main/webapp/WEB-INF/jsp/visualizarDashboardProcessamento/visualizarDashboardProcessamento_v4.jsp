<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");

List lstOrgaos = (List) request.getAttribute("lstOrgao");
String periodo = (String) request.getAttribute("periodo");
String estimativaTermino = (String) request.getAttribute("estimativaTermino");
Double percentualBlocosProcessados = (Double) request.getAttribute("percentualBlocosProcessados");
Double percentualBlocosProcessadosMargem = (Double) request.getAttribute("percentualBlocosProcessadosMargem");
Double percentualBlocosProcessadosRetorno = (Double) request.getAttribute("percentualBlocosProcessadosRetorno");
Double percentualBlocosProcessadosComErro = (Double) request.getAttribute("percentualBlocosProcessadosComErro");
Double percentualBlocosProcessadosRejeitados = (Double) request.getAttribute("percentualBlocosProcessadosRejeitados");
String orgaoIdentificadorProcessamento = (String) request.getAttribute("orgaoIdentificadorProcessamento");
String orgaoIdentificadorVariacaoMargem = (String) request.getAttribute("orgaoIdentificadorVariacaoMargem");
Map<String, String> dadosGraficoMargem = (Map<String, String>) request.getAttribute("dadosMediaMargem");
List<String> listKeys = null;
List<String> listValues = null;
if (dadosGraficoMargem != null) {
    listKeys = new ArrayList<String>(dadosGraficoMargem.keySet() );
    listValues = new ArrayList<String>(dadosGraficoMargem.values() );
}
boolean temBlocoProcessamento = (request.getAttribute("temBlocoProcessamento") != null && (boolean) request.getAttribute("temBlocoProcessamento"));
boolean temProcessoRodando = (request.getAttribute("temProcessoRodando") != null && (boolean) request.getAttribute("temProcessoRodando"));
boolean podeInterromperExecucao = (request.getAttribute("podeInterromperExecucao") != null && (boolean) request.getAttribute("podeInterromperExecucao"));
String linkRet = (String) request.getAttribute("linkRet");
if (TextHelper.isNull(linkRet)) {
    linkRet = "../v3/carregarPrincipal";
}
boolean exibeGraficos = false;
if (temBlocoProcessamento && !temProcessoRodando) {
    exibeGraficos = true;      
}
%>

<c:set var="title">
  <hl:message key="rotulo.processamento.folha.dashboard.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
      <% if (temProcessoRodando) { %>
      <div class="alert alert-info" role="alert"><hl:message key="mensagem.processamento.folha.dashboard.aguarde.reload"/> <b><span id="timeoutTempoRestante"></span></b></div>
      <% } %>
      <% if (exibeGraficos) { %>
      <div class="btn-action">
        <a class="btn btn-primary" href="javascript:void(0);" onClick="atualizar()">
          <svg width="42" height="42"> 
            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-atualiza"></use>
          </svg><hl:message key="rotulo.botao.atualizar.processamento.folha.dashboard"/>
        </a>
      </div>
      <div class="card">
        <div class="card-header">
          <div class="card-header-title">
            <i class="header-icon icon-gradient"></i>
              <hl:message key="rotulo.processamento.folha.dashboard.titulo.barra.progresso"/>
          </div>
          <span class="ultima-edicao">
          <hl:message key="rotulo.processamento.folha.dashboard.estimativa.termino" arg0="<%=TextHelper.forHtmlAttribute(estimativaTermino)%>"/>
          </span>
        </div>
        <div class="card-body">
          <div class="progress md-progress" style="height: 20px">
            <div class="progress-bar" role="progressbar" style="width: <%=TextHelper.forHtmlAttribute(percentualBlocosProcessados)%>%; height: 20px" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100">
              <%=TextHelper.forHtmlAttribute(percentualBlocosProcessados)%>%
            </div>
          </div>
        </div>
        <div class="modal fade" id="modalInterromperExecucao" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
          <div class="modal-dialog modal-wide-content" role="document">
            <div class="modal-content p-3">
              <div class="modal-header">
                <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.processamento.folha.dashboard.interromper.execucao"/></h5>
              </div>
              <div class="form-group modal-body m-0">
                <span class="modal-title mb-0" id="subTitulo"> <hl:message key="mensagem.processamento.folha.dashboard.informar.motivo.interrupcao"/></span>
                <br>
                <label for="editfield"><hl:message key="rotulo.processamento.folha.dashboard.interromper.observacao"/></label>
                <textarea class="form-control" id="editfield" name="editfield" rows="3" cols="28"></textarea>
              </div>
              <div class="ui-dialog-buttonset mb-3">
              <button type="button" class="btn btn-primary ml-4 mr-3 float-end" id="btnInterromperExecucao" onClick="confirmarInterromperExecucao()"><hl:message key="rotulo.botao.confirmar"/></button>
              <button type="button" class="btn btn-outline-danger ml-4 float-end" id="voltarModalInterromperExecucao"><hl:message key="rotulo.botao.cancelar"/></button>
              </div>          
            </div>
          </div>
        </div> 
      </div>
      <div class="mb-5">
        <div class="row">
          <div class='col-sm-6 col-md-6'>
            <div class="mb-3 card">
              <div class="card-header">
                <div class="card-header-title">
                  <i class="header-icon icon-gradient"></i>
                  <hl:message key="rotulo.processamento.folha.dashboard.titulo.processamento.blocos"/>
                </div>
                <span class="ultima-edicao">
                  <hl:message key="rotulo.processamento.folha.dashboard.periodo" arg0="<%=TextHelper.forHtmlAttribute(periodo)%>"/>
                </span>
              </div>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="form-group col-sm-12 col-md-12" id="">
                  <label for="selecaoOrgao"><hl:message key="rotulo.orgao.singular"/></label>
                  <%=JspHelper.geraCombo(lstOrgaos, "selecaoOrgao", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, orgaoIdentificadorProcessamento, "atualizar()", false, "form-control")%>
                </div>
              </div>
              <div class="container" id="DivChartContainer">
                <div class="row d-flex justify-content-center">
                  <div class="col-md-10">
                    <canvas id="barChart"></canvas>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class='col-sm-6 col-md-6'>
            <div class="mb-3 card">
              <div class="card-header">
                <div class="card-header-title">
                  <i class="header-icon icon-gradient"></i>
                  <hl:message key="rotulo.processamento.folha.dashboard.titulo.media.margem"/>                  
                </div>
              </div>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="form-group col-sm-12 col-md-12" id="">
                  <label for="selecaoOrgao1"><hl:message key="rotulo.orgao.singular"/></label>
                  <%=JspHelper.geraCombo(lstOrgaos, "selecaoOrgao1", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, orgaoIdentificadorVariacaoMargem, "atualizar()", false, "form-control")%>
                </div>
              </div>
              <div class="container" id="DivChartContainerLine">
                <div class="row d-flex justify-content-center">
                  <div class="col-md-12">
                    <canvas id="lineChart"></canvas>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      <% } %>
        <div class='row mt-4'>
          <div class='col-md-12 col-sm-12'>
            <div class="btn-action">
              <% if (exibeGraficos) { %>
              <a class="btn btn-outline-danger" href="javascript:void(0);" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
                 <% if (podeInterromperExecucao) { %>
                 <a class="btn btn-outline-danger" href="javascript:void(0);" onClick="abrirModalInterromperExecucao()"><hl:message key="rotulo.botao.interromper.processamento.folha.dashboard"/></a>
                 <% } %>
              <% } else { %>
              <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=linkRet%>');"><hl:message key="rotulo.botao.voltar"/></a>
              <% } %>
            </div>
          </div>
        </div>
      </div>
</c:set>

<c:set var="javascript">
<script type="text/javascript" src="../js/chart.umd.js"></script>
<script type="text/javascript">

  var procData = []; 
  var procLabels = [];
  var margemLabels = [];
  var margemData = [];
  
  function atualizar() {
	var idOrgaoProc = $("#selecaoOrgao").children("option:selected").val();
	var idOrgaoMargem = $("#selecaoOrgao1").children("option:selected").val();
	postData('../v3/dashboardProcessamento?acao=iniciar&orgaoIdentificadorProcessamento=' + idOrgaoProc + '&orgaoIdentificadorVariacaoMargem=' + idOrgaoMargem + '&<%=SynchronizerToken.generateToken4URL(request)%>');
  }
  
  function geraBarChart() {
	$('#DivChartContainer').empty();
	$('#DivChartContainer').append('<canvas id="barChart"></canvas>');
	    
	var ctx = document.getElementById("barChart").getContext('2d');
	
	var barChart = new Chart(ctx, {
	  type: 'bar',
	  data: {
	    labels: procLabels,
	    datasets: [
	        {
	        label: '<hl:message key="rotulo.processamento.folha.dashboard.grafico.processados.titulo"/>',
	        data: procData,
	        backgroundColor: [
	          'rgba(82, 176, 113, 0.2)',
	          'rgba(82, 176, 113, 0.2)',
	          'rgba(255, 159, 64, 0.2)',
	          'rgba(255, 99, 132, 0.2)',
	        ],
	        borderColor: [
	          'rgba(75, 192, 192, 1)',
	          'rgba(75, 192, 192, 1)',
	          'rgba(255, 159, 64, 1)',
	          'rgba(255,99,132,1)',
	        ],
	        borderWidth: 1
	        }
	    ]
	  },
	  options: {
	    scales: {
	      y: {
	        ticks: {
	          beginAtZero: true
	        }
	      }
	    }
	  }
	});
  }

  function geraLineChart() {
    $('#DivChartContainerLine').empty();
    $('#DivChartContainerLine').append('<canvas id="lineChart"></canvas>');
    
    var ctxL = document.getElementById("lineChart").getContext('2d');
    
    var myLineChart = new Chart(ctxL, {
      type: 'line',
      data: {
        labels: margemLabels,
        datasets: [
          {
            label: "<hl:message key="rotulo.processamento.folha.dashboard.grafico.variacao.margem.titulo"/>",
            data: margemData,
            backgroundColor: [
              'rgba(0, 137, 132, .2)',
            ],
            borderColor: [
              'rgba(0, 10, 130, .7)',
            ],
            borderWidth: 2
          }
        ]
      },
      options: {
        responsive: true
      }
    });
  }
  
  $(document).ready(function() {
	  if (<%=(boolean) (temProcessoRodando)%>) {
	      var timeout = 30;
	      function reload() {
	          if (timeout <= 0) {
	              atualizar();
	          } else {
	            jQuery('#timeoutTempoRestante').text(timeout);
	            timeout--;
	            setTimeout(reload, 1000);
	          }
	      }
	      setTimeout(reload, 1000);
	  }
	  if (<%=(boolean) (exibeGraficos)%>) {
		  procLabels = ["<hl:message key="rotulo.processamento.folha.dashboard.grafico.processados.margem"/>"
      	              , "<hl:message key="rotulo.processamento.folha.dashboard.grafico.processados.retorno"/>"
	                  , "<hl:message key="rotulo.processamento.folha.dashboard.grafico.processados.rejeitados"/>"
	                  , "<hl:message key="rotulo.processamento.folha.dashboard.grafico.processados.com.erro"/>"
	      ];
	  
	      procData = [<%=TextHelper.forJavaScriptBlock(percentualBlocosProcessadosMargem)%>
		            , <%=TextHelper.forJavaScriptBlock(percentualBlocosProcessadosRetorno)%>
		            , <%=TextHelper.forJavaScriptBlock(percentualBlocosProcessadosRejeitados)%>
		            , <%=TextHelper.forJavaScriptBlock(percentualBlocosProcessadosComErro)%>
          ];

	      margemLabels = [
          <% if (listKeys != null) {
                for (String dado : listKeys) { %>
      	           "<%=TextHelper.forJavaScriptBlock(dado)%>",
          <%    }
             }%>
          ];
		  
          margemData = [
          <% if (listValues != null) {
               for (String dado : listValues) { %>
      	          <%=TextHelper.forJavaScriptBlock(dado)%>,
    	  <%   }
             }%>
          ];

	      geraBarChart();
	      geraLineChart();
	  
	      setTimeout("atualizar()", 5*60*1000); // atualiza a cada 5 minutos
	  }
  });
  
  $('#voltarModalInterromperExecucao').on('click',function() {
     $('#modalInterromperExecucao').modal('hide');
  });

  function abrirModalInterromperExecucao() {
    $('#modalInterromperExecucao').modal('show');
  }

  function confirmarInterromperExecucao() {
    var obs = $('#editfield').val();
    if (obs == null || obs.trim() == '') {
    	alert('<hl:message key="mensagem.informe.observacao.processamento.folha.interromper"/>');
    } else {
    	postData('../v3/dashboardProcessamento?acao=interromperExecucao&periodo=<%=TextHelper.forJavaScriptAttribute(periodo)%>&observacao=' + obs + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
    	$('#modalInterromperExecucao').modal('hide');
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