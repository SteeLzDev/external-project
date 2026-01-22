<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    List<?> consignatarias = (List<?>) request.getAttribute("consignatarias");
	String linkRet = (String) request.getAttribute("linkRet");
	String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="title">
  <hl:message key="rotulo.folha.liberacao.arquivo.movimento.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
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
  <div class="row col-md-12">
    <div class="card col-md-12">
      <div class="card-header hasIcon pl-3">
        <h2 class="card-header-title">
          <hl:message key="rotulo.listar.consignataria.titulo" />
        </h2>
      </div>
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message
                  key="rotulo.consignataria.codigo" /></th>
              <th scope="col"><hl:message
                  key="rotulo.consignataria.nome" /></th>
              <th scope="col"><hl:message
                  key="rotulo.consignataria.nome.abreviado" /></th>
            </tr>
          </thead>
          <tbody>
            <%=JspHelper.msgRstVazio(consignatarias.size() == 0, "13", "lp")%>
            <%
                Iterator<?> it = consignatarias.iterator();
            				while (it.hasNext()) {
            					CustomTransferObject consignataria = (CustomTransferObject) it.next();
            					String csa_codigo = (String) consignataria.getAttribute(Columns.CSA_CODIGO);
            					String csa_nome = (String) consignataria.getAttribute(Columns.CSA_NOME);
            					String csa_identificador = (String) consignataria.getAttribute(Columns.CSA_IDENTIFICADOR);

            					String csa_nome_abrev = (String) consignataria.getAttribute(Columns.CSA_NOME_ABREV);
            					if (csa_nome_abrev == null || csa_nome_abrev.trim().length() == 0) {
            						csa_nome_abrev = csa_nome;
            					}
            %>
            <tr>
              <td><%=TextHelper.forHtmlContent(csa_identificador)%></td>
              <td><%=TextHelper.forHtmlContent(csa_nome.toUpperCase())%></td>
              <td><%=TextHelper.forHtmlContent(consignataria.getAttribute(Columns.CSA_NOME_ABREV) != null
							? (String) consignataria.getAttribute(Columns.CSA_NOME_ABREV).toString()
							: "")%></td>
              <%
                  }
              %>
            </tr>
          </tbody>
        </table>
        <div class="btn-action">
          <button class="btn btn-outline-danger" id="btnVoltar"
            onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkRet)%>'); return false;">
            <hl:message key="rotulo.botao.voltar" />
          </button> 
          <button class="btn btn-primary" id="btnImprimir" type="submit"
            onClick="imprimir()"> <hl:message
              key="rotulo.botao.imprimir" />
          </button>
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
				padding: 0;
				margin: 0;
				color: #000 !important;
			}
			body{color: #000 !important}
		    table th:last-child {display: none;}
		    table td:last-child {display: none;}
		  	#menuAcessibilidade {display: none;}
			#dataTables_length {display: none;}
			#dataTables_paginate {display: none;}
			#dataTables_filter {display: none;}	
			#dataTables_info {display: none;}
		    #active-buttons {display: none;}
		    #menuAcessibilidade {display: none;}
		    #footer-print {position: absolute; bottom: 0;}
		    #header-print img{width: 10%;}    
		    .opcoes-avancadas {display: none;}
			.table thead th {
				padding: 0 .75rem;
			}
		
		    .table thead tr th, .table tbody tr td {
		      font-size: 12px;
		      line-height: 1.25;
		      padding-top: 0;
		      padding-bottom: 0;
		      padding-left: .25rem;
		      padding-left: .25rem;
		      color: #000 !important;
		      border-left: 1px solid #000 !important;
		    }
		  }
		}
		@page{margin: 0.5cm;}
	</style>
	<script type="text/JavaScript">
		function imprimir() {
			injectDate();
			window.print();
		}
	</script>
	<script>
		function injectDate(){
			const dateTimePrint = document.querySelector('#date-time-print');
			const printDate = new Date();
			printDate.toLocaleString("pt-br");
			dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);	
		}
	</script>
	<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
	<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>