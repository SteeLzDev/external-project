<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.margem.variacao.VariacaoMargemGraficoDataSet" %>
<%@ page import="com.zetra.econsig.helper.margem.MargemHelper" %>
<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ taglib prefix="t"      tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"     uri="/html-lib" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

Map<Short, ExibeMargem> exibeMargens = (Map<Short, ExibeMargem>) request.getAttribute("exibeMargens");
Map<Date, Map<Short, Double>> variacaoMargem = (Map<Date, Map<Short, Double>>) request.getAttribute("variacaoMargem");
VariacaoMargemGraficoDataSet variacaoMargemDS = (VariacaoMargemGraficoDataSet) request.getAttribute("variacaoMargemDS");
List<Short> marCodigos = (List<Short>) request.getAttribute("marCodigos");

String voltar = (String) request.getAttribute("destinoBotaoVoltar");

List<String> chartLegends = (List<String>) request.getAttribute("chartLegends");
List<String> chartLabels  = (List<String>) request.getAttribute("chartLabels");
List<String> chartValues  = (List<String>) request.getAttribute("chartValues");

List<String> chartLegendsBruta = (List<String>) request.getAttribute("chartLegendsBruta");
List<String> chartLabelsBruta  = (List<String>) request.getAttribute("chartLabelsBruta");
List<String> chartValuesBruta  = (List<String>) request.getAttribute("chartValuesBruta");
boolean exibeMargemBrutaCsa = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_MARGEM_BRUTA_VARIACAO_MARGEM_CSA, responsavel);
%>
<c:set var="title">
  <hl:message key="rotulo.variacao.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">

  <div class="row">
    <div class="col-sm-12 col-md-12">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title">
            <hl:message key="rotulo.consultar.margem.resultado" />
          </h2>
        </div>
        <%
            if (variacaoMargemDS != null && variacaoMargem != null && variacaoMargem.size() > 0) {
              // Recupera as datas iniciais e finais do período.
              String dataInicial = (variacaoMargemDS.getInicioPeriodo() != null ? DateHelper.toDateString(variacaoMargemDS.getInicioPeriodo()) : "");
              String dataFinal = (variacaoMargemDS.getFimPeriodo() != null ? DateHelper.toDateString(variacaoMargemDS.getFimPeriodo()) : "");
        %>
        <div class="card-body">
          <dl class="row data-list mt-2">
            <% for (Short marCodigo : marCodigos) {
                if (exibeMargens.get(marCodigo) != null && exibeMargens.get(marCodigo).isExibeValor()) {
                  String mediaMargem = variacaoMargemDS.getMediaMargem(marCodigo) != null ? NumberHelper.reformat(String.valueOf(variacaoMargemDS.getMediaMargem(marCodigo)), "en", NumberHelper.getLang()) : "";
            %>
            <dt class="col-6">
              <hl:message key="rotulo.variacao.margem.media"
                arg0="<%=TextHelper.forHtmlAttribute(MargemHelper.getInstance().getMarDescricao(marCodigo, responsavel))%>"
                arg1="<%=TextHelper.forHtmlAttribute(dataInicial)%>"
                arg2="<%=TextHelper.forHtmlAttribute(dataFinal)%>" />
              :
            </dt>
            <dd class="col-6">
              <b><%=TextHelper.forHtmlAttribute(mediaMargem)%></b>
            </dd>
            <%
                }
              }
            %>
          </dl>

          <!-- TABELA -->
          <div class="table-responsive pt-2">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.variacao.margem.data" /></th>
                  <% for (Short marCodigo : marCodigos) { %>
                    <% if (exibeMargens.get(marCodigo) != null && exibeMargens.get(marCodigo).isExibeValor()) { %>
                      <th scope="col"><%=TextHelper.forHtmlContent(MargemHelper.getInstance().getMarDescricao(marCodigo, responsavel))%></th>
                    <% } %>
                  <% } %>
                </tr>
              </thead>
              <tbody>
                <%
                    List<Date> datasVariacaoMargem = variacaoMargemDS.recuperarDatasVariacaoMargem();
                    for (Date dataEvento : datasVariacaoMargem) {
                        String data = DateHelper.toDateString(dataEvento);
                        Map<Short, Double> dadosHistoricoMargem = variacaoMargem.get(dataEvento);
                %>
                   <tr>
                     <td><%=TextHelper.forHtmlContent(data)%></td>
                     <%
                     for (Short marCodigo : marCodigos) {
                        if (exibeMargens.get(marCodigo) != null && exibeMargens.get(marCodigo).isExibeValor()) {
                            double margem = dadosHistoricoMargem.get(marCodigo) != null ? ((Double) dadosHistoricoMargem.get(marCodigo)).doubleValue() : 0.0;
                         %>
                         <td><%=NumberHelper.reformat(String.valueOf(margem), "en", NumberHelper.getLang())%></td>
                         <%
                        }
                     }
                     %>
                   </tr>
               <%
                  }
               %>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="4"><hl:message key="rotulo.listagem.variacao.margem.acao" /></td>
                </tr>
              </tfoot>
            </table>
          </div>
          <ul class="nav nav-tabs responsive-tabs" id="charVariacaoMargem" role="tablist">
            <li class="nav-item">
              <a class="nav-link active" id="margemLiquida-tab" data-bs-toggle="tab" href="#margemLiquida" role="tab" onClick="escondeMargemBruta()" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.variacao.margem.grafico.margem.liquida"/></a>
            </li>
            <%if(!TextHelper.isNull(chartValuesBruta) && (responsavel.isCseSupOrg() || (exibeMargemBrutaCsa && responsavel.isCsa()))) {%>
                <li class="nav-item">
                  <a class="nav-link" id="margemBruta-tab" data-bs-toggle="tab" href="#margemBruta" role="tab" onClick="exibeMargemBruta()" aria-controls="profile" aria-selected="false"><hl:message key="rotulo.variacao.margem.grafico.margem.bruta"/></a>
                </li>
            <%} %>
          </ul>      
          <div class="tab-content" id="charVariacaoMargem">
            <div class="tab-pane fade show active" id="margemLiquida" role="tabpanel" aria-labelledby="margemLiquida-tab">
            </div>
            <%if(!TextHelper.isNull(chartValuesBruta) && (responsavel.isCseSupOrg() || (exibeMargemBrutaCsa && responsavel.isCsa()))) {%>
                <div class="tab-pane fade" id="margemBruta" role="tabpanel" aria-labelledby="margemBruta-tab">
                </div>
            <%} %>
          </div>
        </div>
      <% } %>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=voltar%>')"><hl:message key="rotulo.botao.voltar" /></a>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <script src="../node_modules/responsive-bootstrap-tabs/jquery.responsivetabs.js"></script>
  <script type="text/JavaScript">
  $(function() {
      $('.nav-tabs').responsiveTabs();
  });
  $(document).ready(function () {
  
      var myChart = echarts.init(document.getElementById('margemLiquida'), null, {
    height: 250
  });
  
  window.addEventListener('resize', function() {
    myChart.resize();
  }); 

  const legends = [<%= TextHelper.sqlJoin(chartLegends) %>];
  const labels = [<%= TextHelper.sqlJoin(chartLabels) %>];
  const values = [<%= TextHelper.sqlJoin(chartValues).replace("'", "") %>];

  var seriesData = [];

  if (Array.isArray(legends) && Array.isArray(values)) {
      for (var i = 0; i < legends.length; i++) {
          seriesData.push({
              name: legends[i],
              type: 'line',
              data: values[i]
          });
      }
  }
  var option = {
	        title: {
	            text: ''
	        },
	        tooltip: {},
	        legend: {
	            data: legends
	        },
	        xAxis: {
	            data: labels
	        },
	        yAxis: {},
	        series: seriesData
	    };

      myChart.setOption(option);
      
      
    });
  function exibeMargemBruta(){
      <%if(!TextHelper.isNull(chartValuesBruta) && !chartValuesBruta.isEmpty() && responsavel.isCseSupOrg()) {%>            
            $( "#margemBruta" ).addClass( "show active" );
            
            var myChartBruta = echarts.init(document.getElementById('margemBruta'), null, {
			    height: 250
			  });
			  
			  window.addEventListener('resize', function() {
			    myChartBruta.resize();
			  }); //responsividade

			  const legendsBruta = [<%= TextHelper.sqlJoin(chartLegendsBruta) %>];
			  const labelsBruta = [<%= TextHelper.sqlJoin(chartLabelsBruta) %>];
			  const valuesBruta = [<%= TextHelper.sqlJoin(chartValuesBruta).replace("'", "") %>];

			  var seriesDataBruta = [];

			  if (Array.isArray(legendsBruta) && Array.isArray(valuesBruta)) {
			      for (var i = 0; i < legendsBruta.length; i++) {
			          seriesDataBruta.push({
			              name: legendsBruta[i],
			              type: 'line',
			              data: valuesBruta[i]
			          });
			      }
			  }
			  var option = {
				        title: {
				            text: ''
				        },
				        tooltip: {},
				        legend: {
				            data: legendsBruta
				        },
				        xAxis: {
				            data: labelsBruta
				        },
				        yAxis: {},
				        series: seriesDataBruta
				    };
			    
			      myChartBruta.setOption(option);
  <%}%>
  }
  function escondeMargemBruta(){
    $( "#margemBruta" ).removeClass( "show active" );
  }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
