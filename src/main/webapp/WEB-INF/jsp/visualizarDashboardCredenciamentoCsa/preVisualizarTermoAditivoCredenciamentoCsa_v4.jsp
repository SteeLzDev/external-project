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
ModeloTermoAditivo modeloTermoAditivo = (ModeloTermoAditivo) request.getAttribute("modeloTermoAditivo");
boolean visualizar = !TextHelper.isNull(request.getAttribute("visualizar")) && "true".equals((String) request.getAttribute("visualizar"));
%>
<c:set var="title">
   <hl:message key="rotulo.dashboard.credenciamento.csa.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
	<div class="row">
		<div class="col-sm-12">
		    <div class="card">
		        <div class="card-header">
		        	<h2 class="card-header-title"><%=modeloTermoAditivo.getMtaDescricao()%></h2>
		        </div>
		        <div class="card-body">
		        	<div id="termoPreenchido"></div>
		        </div>
		    </div>
		</div>
	</div>	
	<div class="float-end">
	  <div class="btn-action">
	      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <%if (!visualizar) {%>
          	<a class="btn btn-primary" href="#" onclick="enviarTermo();"><hl:message key="rotulo.acoes.confirmar" /></a>
          <%} %>
	  </div>
	</div>
</c:set>
<c:set var="javascript">
	<link rel="stylesheet" type="text/css" href="../node_modules/trumbowyg/dist/ui/trumbowyg.min.css">
	<script src="../node_modules/trumbowyg/dist/trumbowyg.min.js"></script>
	<script src="../node_modules/trumbowyg/dist/langs/pt_br.min.js"></script>
	<script type="text/JavaScript">
	$('#termoPreenchido').trumbowyg({
		autogrow: true,
		lang: 'pt_br',
		removeformatPasted: true
	});
	
	$('#termoPreenchido').trumbowyg('html', '<%=modeloTermoAditivo.getMtaTexto()%>');
	
	function enviarTermo(){
		var dataToSend = JSON.stringify({
			'creCodigo' : '<%=TextHelper.forJavaScript(creCodigo)%>',
	        'csaCodigo': '<%=TextHelper.forJavaScript(csaCodigo)%>',
	        'mtaDescricao': '<%=TextHelper.forJavaScript(modeloTermoAditivo.getMtaDescricao())%>',
	        'termoPreenchido': document.getElementById('termoPreenchido').innerHTML
			});
		
		$.ajax({ 
			url: "../v3/visualizarDashboardCredenciamento?acao=confirmarTermoCredenciamentoCsa&<%=SynchronizerToken.generateToken4URL(request)%>",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: dataToSend,
            success: function (status) {
            	postData('../v3/visualizarDashboardCredenciamento?acao=iniciar');
            },
            error: function (error) {
            	postData('../v3/visualizarDashboardCredenciamento?acao=iniciar');
         	 }
        });
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