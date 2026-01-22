<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.TextoSistemaTO"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%
String btnCancelar = (String) request.getAttribute("btnCancelar");
TextoSistemaTO textoSistemaTO = (TextoSistemaTO) request.getAttribute("textoSistemaTO");
String dataAlteracao = (String) request.getAttribute("dataAlteracao");
%>
<c:set var="title">
   <hl:message key="rotulo.visualizar.texto.sistema"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
    <div class="row">
		<div class="form-group col-sm-12 col-md-12">
      		<div class="card">
            	<div class="card-header hasIcon pl-3">
              		<h2 class="card-header-title"><%=TextHelper.forHtmlContent(textoSistemaTO.getTexChave())%></h2>
            	</div>
            	<div class="card-body">
            		<form method="post" action="../v3/manterTextoSistema?acao=listar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
            			<hl:htmlinput name="texChave" type="hidden" value="<%=TextHelper.forHtmlAttribute(textoSistemaTO.getTexChave())%>"/>
                		<dl class="row data-list">
                			<dt class="col-2"><hl:message key="rotulo.texto.sistema.data.alteracao"/>:</dt>
                			<dd class="col-10"><%=TextHelper.forHtmlContent(dataAlteracao)%></dd>
                			<dt class="col-2"><hl:message key="rotulo.texto.sistema.texto"/>:</dt>
                			<dd class="col-10"><%=TextHelper.forHtmlContent(textoSistemaTO.getTexTexto())%></dd>
                			<dt class="col-2"><hl:message key="rotulo.texto.sistema.texto.interpolado"/>:</dt>
                			<dd class="col-10"><hl:message key="<%=textoSistemaTO.getTexChave()%>"/></dd>
		                </dl>
                	</form>
            	</div>
            </div>
        </div>
    </div>
    <div class="btn-action">
		<a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(btnCancelar)%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
	</div>
</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" id="MainScript">
		var f0 = document.forms[0];
	</script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>