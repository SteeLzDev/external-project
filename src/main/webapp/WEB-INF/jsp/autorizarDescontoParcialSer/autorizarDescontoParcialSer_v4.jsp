<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@page import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@page import="com.zetra.econsig.dto.entidade.MensagemTO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String serCodigo = (String) request.getAttribute("serCodigo");
String serEmail = (String) request.getAttribute("serEmail");
String serPrimeiroNome = (String) request.getAttribute("serPrimeiroNome");
String termoDescontoParcialServidor = (String) request.getAttribute("termoDescontoParcialServidor");
String cripto = (String) request.getAttribute("exibeCodigo");
String exibeCodigo = (String) request.getAttribute("exibeCodigo");
String acao = (String) request.getAttribute("acao");
String autorizarDesconto = (String) request.getAttribute("autorizarDesconto");
String permiteDesconto = request.getAttribute("permiteDesconto") !=null ? (String) request.getAttribute("permiteDesconto") : "";
boolean fluxoEditar = (request.getAttribute("fluxoEditar") != null && (Boolean) request.getAttribute("fluxoEditar"));
%>
<c:set var="imageHeader">
    <use xlink:href="#i-mensagem"></use>
</c:set>
<c:set var="title">
   <hl:message key="mensagem.autorizar.desconto.parcial.servidor.titulo"/>
</c:set>
<c:set var="bodyContent">
	<form method="post" action="../v3/autorizarDescontoParcialSer?acao=<%=acao%>">
	  <div class="card">
	    <div class="card-header hasIcon">
	      <span class="card-header-icon"><svg width="24"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-mensagem"></use></svg></span>
	      <h2 class="card-header-title"><hl:message key="mensagem.autorizar.desconto.parcial.servidor.informacao"/></h2>
	    </div>
	    <div class="card-body">
        <%if(TextHelper.isNull(cripto) && TextHelper.isNull(exibeCodigo)) { %>
              <div class="card-body">
                <%if (!TextHelper.isNull(serEmail) && TextHelper.isEmailValid(serEmail)) { %>
                  <p><%out.print(termoDescontoParcialServidor);%></p>
                <%} else { %>
                <div class="row">
                  <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("mensagem.info.servidor.autorizar.desconto.parcial.email.invalido", responsavel, serPrimeiroNome)%></h2>
                </div>
                <%} %>
              </div>
              <div class="row">
                <div class="form-group col-sm-12">
                  <label for="autorizarDesconto"><hl:message key="rotulo.autorizar.desconto.parcial" /></label>
                  <div class='form-check'>
                    <input type="radio" class="form-check-input ml-1" id="autorizarDescontoSim" name="autorizarDesconto" value="S" <%if (TextHelper.isNull(serEmail)) {%>disabled <%}%> <%if(permiteDesconto.equals("S")) {%> checked <% } %>/>
                    <label for="autorizarDescontoSim" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.sim"/></label>
                    <input type="radio" class="form-check-input ml-1" id="autorizarDescontoNao" name="autorizarDesconto" value="N" <%if (TextHelper.isNull(serEmail)) {%>disabled <%}%> <%if(permiteDesconto.equals("N")) {%> checked <% } %>/> 
                    <label for="autorizarDescontoNao" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.nao"/></label>
                  </div>  
                </div>
              </div>
        <%} else { %>
             <div class="row">
                <div class="card-body">
                  <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("mensagem.info.servidor.autorizar.desconto.parcial.token", responsavel, serPrimeiroNome, serEmail)%></h2>
                </div>
              </div>
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="tokenEmail"><hl:message key="rotulo.digite.codigo.autorizacao.enviado.email"/></label>
                  <hl:htmlinput name="tokenEmail"
                          di="tokenEmail"
                          type="text"
                        classe="form-control"
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.digite.codigo.autorizacao.enviado.email", responsavel) %>" />
                </div>
              </div>
       <%} %>
            </div>
          </div>
          <hl:htmlinput name="serCodigo" type="hidden" value="<%=serCodigo%>"/>
          <hl:htmlinput name="cripto" type="hidden" value="<%=cripto%>"/>
          <hl:htmlinput name="autorizarDesconto" type="hidden" value="<%=TextHelper.forHtmlAttribute(autorizarDesconto)%>"/>
	</form>
	<div class="btn-action">
      <%if (!TextHelper.isNull(serEmail)) { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/autorizarDescontoParcialSer?acao=gerarTokenEmail&DEPOIS=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><%if (!fluxoEditar) {%><hl:message key="rotulo.depois"/> <%} else { %><hl:message key="rotulo.botao.voltar"/> <%} %></a>
	    <a class="btn btn-primary" href="#no-back" onClick="validaForm(); return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></a>
    <% } else { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/autorizarDescontoParcialSer?acao=gerarTokenEmail&DEPOIS=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><%if (!fluxoEditar) {%><hl:message key="rotulo.depois"/> <%} else { %><hl:message key="rotulo.botao.voltar"/> <%} %></a>
    <%} %>  
	</div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
 f0 = document.forms[0];
 
 function validaForm() {
	 
	 <%if(TextHelper.isNull(cripto) && TextHelper.isNull(exibeCodigo)) { %>
	 	
	 	var autorizaSim = document.getElementById("autorizarDescontoSim").checked;
	 	var autorizaNao = document.getElementById("autorizarDescontoNao").checked;
	 	
	 	if (!autorizaNao && !autorizaSim) {
	 		alert('<hl:message key="mensagem.erro.servidor.autorizar.desconto.parcial.confirmar"/>');
	 		return false;
	 	}
	 <%}%>
	
	 if (<%=!TextHelper.isNull(cripto)%>) {
    	if (f0.tokenEmail.value == null || f0.tokenEmail.value.trim() == '') {
    		alert('<hl:message key="mensagem.informe.ser.codigo.conf.email"/>');
    		f0.tokenEmail.focus();	
    		return false;
    	}
	 }

	 f0.submit();
     
 }
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>