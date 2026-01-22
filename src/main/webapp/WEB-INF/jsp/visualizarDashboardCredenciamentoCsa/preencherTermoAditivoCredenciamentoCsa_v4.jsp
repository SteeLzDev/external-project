<%@page import="com.zetra.econsig.values.TipoArquivoEnum"%>
<%@page import="com.zetra.econsig.values.StatusCredenciamentoEnum"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl"   uri="/html-lib" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.*" %>
<%@page import="com.zetra.econsig.helper.upload.UploadHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.dto.web.AcaoConsignacao" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String creCodigo = (String) request.getAttribute("creCodigo");
String csaCodigo = (String) request.getAttribute("csaCodigo");
List<ModeloTermoTag> modeloTermoTags = (List<ModeloTermoTag>) request.getAttribute("modeloTermoTags");
TransferObject modeloTermoAditivo = (TransferObject) request.getAttribute("modeloTermoAditivo");
String mtaCodigo = (String) modeloTermoAditivo.getAttribute(Columns.MTA_CODIGO);
%>
<c:set var="title">
   <hl:message key="rotulo.dashboard.credenciamento.csa.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
	<form name="form1" id="form1" method="POST" action="../v3/visualizarDashboardCredenciamento?acao=preVisualizarTermoCredenciamentoCsa&<%=SynchronizerToken.generateToken4URL(request)%>">
		<div class="row">
			<div class="col-sm-12">
			    <div class="card">
			        <div class="card-header">
			        	<h2 class="card-header-title"><%=(String) modeloTermoAditivo.getAttribute(Columns.MTA_DESCRICAO)%></h2>
			        </div>
			        <div class="card-body">
			        	<span><hl:message key="rotulo.dashboard.titulo.termo.aditivo.info"/></span><p>
			        	<div class="row">
					        	<% for (ModeloTermoTag modeloTermoTag : modeloTermoTags) { %>
						        	<div class="col-md-3">
								    	<div class="form-group">
									      <label for="tag_<%=modeloTermoTag.getMttCodigo()%>"><%=TextHelper.forHtmlContent(modeloTermoTag.getMttValor())%></label>
									      <input type="text" class="form-control" id="tag_<%=modeloTermoTag.getMttCodigo()%>" name="tag_<%=modeloTermoTag.getMttCodigo()%>">
									    </div>
								    </div>
							    <%} %>
					  </div>
			        </div>
			    </div>
			</div>
		</div>	
	      <input hidden="true" id="creCodigo" name="creCodigo" value="<%=TextHelper.forHtmlContent(creCodigo)%>">
	      <input hidden="true" id="csaCodigo" name="csaCodigo" value="<%=TextHelper.forHtmlContent(csaCodigo)%>">
	      <input hidden="true" id="mtaCodigo" name="mtaCodigo" value="<%=TextHelper.forHtmlContent(mtaCodigo)%>">
	</form>
	<div class="float-end">
	  <div class="btn-action">
	      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <a class="btn btn-primary" href="#" onclick="validarPreenchimentos()"><hl:message key="rotulo.acoes.visualizar" /></a>
	  </div>
	</div>
</c:set>
<c:set var="javascript">
	<script type="text/JavaScript">
	function validarPreenchimentos(){
		<% for (ModeloTermoTag modeloTermoTag : modeloTermoTags) { %>
			const valor_<%=modeloTermoTag.getMttCodigo()%> = document.getElementById("tag_<%=modeloTermoTag.getMttCodigo()%>");
			if(valor_<%=modeloTermoTag.getMttCodigo()%> == null || valor_<%=modeloTermoTag.getMttCodigo()%>.value =='' || valor_<%=modeloTermoTag.getMttCodigo()%>.value == 'undefined'){
				valor_<%=modeloTermoTag.getMttCodigo()%>.focus();
	    		alert('<hl:message key="rotulo.dashboard.titulo.termo.aditivo.tag.campo.obrigatorio" arg0="<%=modeloTermoTag.getMttValor()%>"/>');
	    		return false;
	    	}
		<%} %>
    	
		document.form1.submit();
    }
	</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>