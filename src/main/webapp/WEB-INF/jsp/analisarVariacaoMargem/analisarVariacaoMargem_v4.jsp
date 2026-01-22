<%@page import="com.zetra.econsig.helper.texto.NumberHelper"%>
<%@page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@page import="com.zetra.econsig.persistence.entity.HistoricoMargemFolha"%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.ParamSession"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.values.OperacaoHistoricoMargemEnum"%>
<%@ page import="com.zetra.econsig.dto.web.VisualizarHistoricoDTO"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
ParamSession paramSession = (ParamSession) request.getAttribute("paramSession");
CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
List margensServidor = (List) request.getAttribute("margensServidor");
List<MargemTO> margensTab = (List<MargemTO>) request.getAttribute("margensTab");
String rseCodigo = (String) request.getAttribute("rseCodigo");
Map<Short, List<CustomTransferObject>> lstHistoricoVariacaoMap =(Map<Short, List<CustomTransferObject>>) request.getAttribute("lstHistoricoVariacaoMap");
String voltar = (String) request.getAttribute("destinoBotaoVoltar");
Short primeiroMarcodigo = null;
%>
<c:set var="title">
<hl:message key="rotulo.analisar.variacao.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-7">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.consultar.margem.resultado"/></h2>
        </div>
        <div class="card-body">
          <dl class= "row data-list firefox-print-fix">
            <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
            <% pageContext.setAttribute("servidor", servidor); %>
            <hl:detalharServidorv4 name="servidor"/>
            <%-- Fim dos dados da ADE --%>
          </dl>
        </div>
      </div>
    </div>
    <div class="col-sm-5">
      <form action="../v3/analisarVariacaoMargem?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="formPesqAvancada">
        <div class="card">
          <div class="card-header hasIcon pl-3">
          	<h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
          </div>
            <div class="opcoes-avancadas-body">
               <div class="row form-group">
                  <label for="dataEvento"><hl:message key="rotulo.analisar.variacao.margem.periodo"/></label>
                   <div class="row mt-2 align-items-center" role="group" aria-labelledby="dataEvento">
                      <div class="col-auto">
                          <label for="periodoIni" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                      </div>
                     <div class="col">
                      <hl:htmlinput 
                            name="periodoIni" 
                            di="periodoIni" 
                            type="text" 
                            classe="form-control w-100 pr-0" 
                            placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" 
                            mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoIni"))%>" 
                            ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inclusao.ini", responsavel)%>'/>
                     </div>
                     <div class="col-auto">
                         <label for="periodoFim" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                     </div>
                     <div class="col">
                     <hl:htmlinput 
                            name="periodoFim" 
                            di="periodoFim"  
                            type="text" 
                            classe="form-control w-100 pr-0" 
                            placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" 
                            mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoFim"))%>" 
                            ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inclusao.fim", responsavel)%>'/>
                      </div>
                    </div>
              </div>
              <div class="row form-group col-sm-12 p-0">
                  <label for="marCodigo"><hl:message key="rotulo.historico.margem.tipo.margem"/></label>
                  <%= JspHelper.geraCombo(margensServidor, "marCodigo", Columns.MAR_CODIGO, Columns.MAR_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "marCodigo"), null, false, "form-control")%>                
              </div>
              <div class="row form-group">
			    <label for="variacaoEvento"><hl:message key="rotulo.analisar.variacao.margem.variacao.filter"/></label>
			    <div class="row mt-2 align-items-center" role="group" aria-labelledby="variacaoEvento">
			        <div class="col-auto">
			            <label for="variacaoIni" class="labelSemNegrito"><hl:message key="rotulo.analisar.variacao.margem.variacao.de"/></label>
			        </div>
			        <div class="col">
			            <hl:htmlinput name="variacaoIni"
			                          type="text"
			                          classe="form-control"
			                          di="variacaoIni"
			                          size="15"
			                          mask="#F30"
			                          value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "variacaoIni"))%>" 
			                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
			            />
			        </div>
			        <div class="col-auto">
			            <label for="variacaoFim" class="labelSemNegrito"><hl:message key="rotulo.analisar.variacao.margem.variacao.ate"/></label>
			        </div>
			        <div class="col">
			            <hl:htmlinput name="variacaoFim"
			                          type="text"
			                          classe="form-control"
			                          di="variacaoFim"
			                          size="15"
			                          mask="#F30"
			                          value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "variacaoFim"))%>" 
			                          onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
			            />
			        </div>
			    </div>
			</div>
            <input name="RSE_CODIGO" di="RSE_CODIGO" type="hidden" class="form-control" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"/>
            </div>
          </div>
        </div>
          <div id="actions" class="btn-action">
            <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="pesquisar(); return false;"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
          </div>
      </form>  
    </div>
	<ul class="nav nav-tabs responsive-tabs" id="margensInfo" role="tablist">
		<%
		boolean abaActive = true;
		for (MargemTO margem : margensTab ) {
			Short marCodigo = margem.getMarCodigo();
			String marDescricao = margem.getMarDescricao();
			
			%>
			
			<li class="nav-item">
              <a class="nav-link <%= abaActive ? " active" : "" %>"  onclick="renderizarGrafico('<%= marCodigo %>')" id="<%=marCodigo%>-tab" data-bs-toggle="tab" href="#margem<%=marCodigo%>" role="tab" aria-controls="profile" aria-selected="<%= abaActive ? " true" : "false" %>"><%=marDescricao%></a>
            </li>
            
            <%if (abaActive) {
                primeiroMarcodigo = marCodigo;
			    abaActive = false;
			}
		} %>
	</ul>
	<div class="tab-content" id="margensInfo">
		<% boolean tabActive = true; 
	    for (Map.Entry<Short, List<CustomTransferObject>> mapHistorico : lstHistoricoVariacaoMap.entrySet()) { 
	        Short marCodigo = mapHistorico.getKey();
		    %>
	        <div class="tab-pane fade <%= tabActive ? "show active" : "" %>" id="margem<%=marCodigo%>" role="tabpanel">
	            <div id="graficoMargem-<%= marCodigo %>" style="width: 100%; height: 400px;"></div>
	        </div>
    		<% tabActive = false; 
    	} %>
	</div>
	<div class="row mt-2">
		<div id="actions" class="btn-action">
			  <% if (!responsavel.isSer()){%>
			    <a class="btn btn-outline-danger" href="#no-back" onClick="return postData('<%=TextHelper.forJavaScriptAttribute(voltar)%>');" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
			  <%}else{ %>
			    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
			  <%}%>
	  	</div>
	</div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
   function pesquisar() {
    	    var form = document.formPesqAvancada;

    	    var periodoIni = form.periodoIni.value.trim();
    	    var periodoFim = form.periodoFim.value.trim();
    	    var variacaoIni = form.variacaoIni.value.trim();
    	    var variacaoFim = form.variacaoFim.value.trim();

    	    function parseDate(dateStr) {
    	        var parts = dateStr.split('/');
    	        return new Date(parts[2], parts[1] - 1, parts[0]);
    	    }

    	    if (periodoIni !== "" && periodoFim !== "") {
    	        var dataInicio = parseDate(periodoIni);
    	        var dataFim = parseDate(periodoFim);

    	        var diffTime = Math.abs(dataFim - dataInicio);
    	        var diffMonths = diffTime / (1000 * 60 * 60 * 24 * 30.44);

    	        if (diffMonths > 12) {
        	        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.analisar.variacao.margem.periodo.maximo", responsavel)%>');
    	            return false;
    	        }
    	    }

    	    if (periodoIni !== "" && periodoFim === "") {
    	        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.analisar.variacao.margem.periodo.fim", responsavel)%>');
    	        return false;
    	    }
    	    if (periodoFim !== "" && periodoIni === "") {
    	        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.analisar.variacao.margem.periodo.ini", responsavel)%>');
    	        return false;
    	    }

    	    if (variacaoIni !== "" && variacaoFim === "") {
    	        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.analisar.variacao.margem.variacao.fim", responsavel)%>');
    	        return false;
    	    }
    	    if (variacaoFim !== "" && variacaoIni === "") {
    	        alert('<%=ApplicationResourcesHelper.getMessage("mensagem.analisar.variacao.margem.variacao.ini", responsavel)%>');
    	        return false;
    	    }

    	    form.submit();
    	}
    var charts = {};
    function renderizarGrafico(marCodigo) {
        var chartDom = document.getElementById('graficoMargem-' + marCodigo);

        if (charts[marCodigo]) {
            let chartDomMarCodigo = document.getElementById('graficoMargem-' + marCodigo);
            echarts.dispose(chartDomMarCodigo);
        }
        
        var periodos = [];
        var margensBrutas = [];
        var variacoes = [];

        <% for (Map.Entry<Short, List<CustomTransferObject>> mapHistorico : lstHistoricoVariacaoMap.entrySet()) { 
            Short marCodigoLoop = mapHistorico.getKey();
        %>
            if (marCodigo === '<%= marCodigoLoop %>') {
                <% for (CustomTransferObject historico : mapHistorico.getValue()) { 
                    HistoricoMargemFolha historicoMargemFolha = (HistoricoMargemFolha) historico.getAttribute("historicoMargemFolha");
                    BigDecimal variacao = (BigDecimal) historico.getAttribute("variacao");
                %>
                    periodos.push('<%= DateHelper.toPeriodMesExtensoString(historicoMargemFolha.getHmaPeriodo()) %>');
                    margensBrutas.push(<%= historicoMargemFolha.getHmaMargemFolha() %>);
                    variacoes.push(<%= variacao %>);
                <% } %>
            }
        <% } %>

        if (periodos.length === 0) {
        	alert('<%=ApplicationResourcesHelper.getMessage("mensagem.analisar.variacao.margem.data.nao.existe", responsavel)%>');
            return;
        }

        var myChart = echarts.init(chartDom);
        charts[marCodigo] = myChart;
        
        var option = {
            title: {},
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: [
                    <% if (responsavel.isCsa()) { %>
                        '<%= ApplicationResourcesHelper.getMessage("rotulo.analisar.variacao.margem.percentual", responsavel) %>'
                    <% } else { %>
                        '<%= ApplicationResourcesHelper.getMessage("rotulo.analisar.variacao.margem.bruta", responsavel) %>',
                        '<%= ApplicationResourcesHelper.getMessage("rotulo.analisar.variacao.margem.percentual", responsavel) %>'
                    <% } %>
                ]
            },
            xAxis: {
                type: 'category',
                data: periodos
            },
            yAxis: [
                <% if (responsavel.isCsa()) { %>
                    {
                        type: 'value',
                        name: '<%= ApplicationResourcesHelper.getMessage("rotulo.analisar.variacao.margem.variacao", responsavel) %>',
                        axisLabel: {
                            formatter: '{value} %'
                        }
                    }
                <% } else { %>
                    {
                        type: 'value',
                        name: '<%= ApplicationResourcesHelper.getMessage("rotulo.analisar.variacao.margem.bruta", responsavel) %>'
                    },
                    {
                        type: 'value',
                        name: '<%= ApplicationResourcesHelper.getMessage("rotulo.analisar.variacao.margem.variacao", responsavel) %>',
                        axisLabel: {
                            formatter: '{value} %'
                        }
                    }
                <% } %>
            ],
            series: [
                <% if (responsavel.isCsa()) { %>
                    {
                        name: '<%= ApplicationResourcesHelper.getMessage("rotulo.analisar.variacao.margem.percentual", responsavel) %>',
                        type: 'line',
                        yAxisIndex: 0,
                        data: variacoes,
                        label: {
                            show: true,
                            formatter: '{c} %'
                        }
                    }
                <% } else { %>
                    {
                        name: '<%= ApplicationResourcesHelper.getMessage("rotulo.analisar.variacao.margem.bruta", responsavel) %>',
                        type: 'bar',
                        data: margensBrutas
                    },
                    {
                        name: '<%= ApplicationResourcesHelper.getMessage("rotulo.analisar.variacao.margem.percentual", responsavel) %>',
                        type: 'line',
                        yAxisIndex: 1,
                        data: variacoes,
                        label: {
                            show: true,
                            formatter: '{c} %'
                        }
                    }
                <% } %>
            ]
        };
        
        myChart.setOption(option);
    }

    document.addEventListener("DOMContentLoaded", function() {
        renderizarGrafico('<%= primeiroMarcodigo %>');
    });
</script>
    
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
  