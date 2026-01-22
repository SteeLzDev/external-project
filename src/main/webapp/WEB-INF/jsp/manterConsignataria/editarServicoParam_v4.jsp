<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		List<TransferObject> parametros = (List<TransferObject>) request.getAttribute("parametros");
		List<String> tpsCodigos = (List<String>) request.getAttribute("tps_codigos");
		Map<String, List<TransferObject>> servicosParamCsa = (Map<String, List<TransferObject>>) request.getAttribute("servicosParamCsa");
		String voltar = (String) request.getAttribute("permanecerNaPaginaEditarServico");
		String svcCodigo = (String) request.getAttribute("svc_codigo");
    String csaCodigo = (String) request.getAttribute("csa_codigo");
    String csaNome = (String) request.getAttribute("csa_nome");
    String svcIdentificador = (String) request.getAttribute("svc_identificador");
    String svcDescricao = (String) request.getAttribute("svc_descricao");
%>
<c:set var="title">
  <hl:message key="rotulo.manutencao.servico.csa" />
</c:set>
<c:set var="imageHeader">
		<use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
	<form method="post" action="../v3/manterConsignataria?acao=salvarParametrosServicos&_skip_history_=true" name="form1" >
		<div class="card">
			<div class="card-header">
				<h2 class="card-header-title">
					<hl:message key="rotulo.servico.parametro.titulo"/>
				</h2>
			</div>
			<div class="card-body">
				<div class="alert alert-warning">
      		<hl:message key="rotulo.servico.parametro.descricao"/>
      	</div>
      	  <div class="row">
				    <div class="form-group ml-3" role="radiogroup">
				      <div><span><hl:message key="rotulo.servico.parametro.pergunta"/></span></div>
				      <div class="form-check form-check-inline pt-2">
				        <input class="form-check-input ml-1" type="radio" name="desejaReplicar" id="desejaReplicarS" value="true" onChange="habilitaCampos('true')" >
				        <label class="form-check-label labelSemNegrito pr-4" for="desejaReplicarS"><hl:message key="rotulo.sim"/></label>
					  </div>
						<div class="form-check form-check-inline pt-2">
						<input class="form-check-input ml-1" type="radio" name="desejaReplicar" id="desejaReplicarN" value="false" onChange="habilitaCampos('false')">
				        <label class="form-check-label labelSemNegrito pr-4" for="desejaReplicarN"><hl:message key="rotulo.nao"/></label>
				      </div>
				    </div>
				  </div>
				  <div id="camposSvc">
			    	<% for (TransferObject parametro : parametros) { 
			  			String tpsCodigo = (String) parametro.getAttribute(Columns.TPS_CODIGO);
			  			List<TransferObject> listServicos = servicosParamCsa.get(tpsCodigo);
			  			request.setAttribute("listServicos", listServicos);
			  			if (listServicos != null && !listServicos.isEmpty()) {
			  				String tpsDescricao = ((String) listServicos.get(0).getAttribute(Columns.TPS_DESCRICAO)).replace("</br>", "").replace("<br>", "").replace(".", "").replace(",", "");
			  		%>
			          <div class="form-group">
			        		<label for="nomeParametro"><hl:message key="rotulo.servico.parametro"/> <%=TextHelper.forHtmlAttribute(tpsDescricao) %></label>
			        			<div class="form-check" >
			          			<hl:htmlcombo listName="listServicos" 
			                  name="svcCodigos"
			                  di="listServicos" 
			                  classe="form-control"
			                  fieldValue="<%=TextHelper.forHtmlAttribute( (Columns.PSC_SVC_CODIGO) + ";" + "tpsCodigo" + ";" + "pscSvcVlrPadrao" + ";" + "pscSvcVlrRef") %>" 
			                  fieldLabel="<%=(String)(Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO)%>" 
			                  size="4"
			                />
			            </div>
			          </div>
			      	<% } %>
			  		<% } %>
			  	</div>
			</div>            
      </div>
		<div class="btn-action">
			<a href="#no-back" class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(voltar)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a> 
			<a class="btn btn-primary" id="btnSalvar" href="#no-back" onClick="f0.submit(); return false;" ><hl:message key="rotulo.botao.salvar"/></a>
			<a href="#no-back" class="btn btn-primary" id="btnConcluir" onClick="postData('<%=TextHelper.forJavaScriptAttribute(voltar)%>'); return false;"><hl:message key="rotulo.botao.concluir"/></a> 
		</div>
		<input type="hidden" name="svc_codigo" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>">
	  <input type="hidden" name="csa_codigo" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
	  <input type="hidden" name="csa_nome" value="<%=TextHelper.forHtmlAttribute(csaNome)%>">
	  <input type="hidden" name="svc_identificador" value="<%=TextHelper.forHtmlAttribute(svcIdentificador)%>">
	  <input type="hidden" name="svc_descricao" value="<%=TextHelper.forHtmlAttribute(svcDescricao)%>">
	</form>
</c:set>
<c:set var="javascript">
	<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
	<script type="text/JavaScript" src="../js/validacoes.js"></script>
	<script type="text/JavaScript" src="../js/validaform.js"></script>
	<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
	<script type="text/JavaScript" src="../js/listutils.js"></script>
	<script type="text/JavaScript">
			$(document).ready(function() {
		    $("#btnSalvar").hide();	
		    $("#btnConcluir").show();
		    $("#camposSvc").hide();
		  });
			  
	   var f0 = document.forms[0];
	   	window.onload = formLoad;


	   	function habilitaCampos(valor){
				if(valor==='true'){
					$("#btnSalvar").show();
					$("#btnConcluir").hide();
					$("#camposSvc").show();
				}	else {
			    $("#btnSalvar").hide();
			    $("#btnConcluir").show();
			    $("#camposSvc").hide();
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