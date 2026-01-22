<%@page import="com.zetra.econsig.values.CodedValues"%>
<%@page import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<%@page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@page import="com.zetra.econsig.dto.entidade.MensagemTO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>

<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String email = (String) request.getAttribute("email");
String serTel = (String) request.getAttribute("telefone");
String serDdd = (String) request.getAttribute("ddd");
String serCel = (String) request.getAttribute("celular");
String celDdd = (String) request.getAttribute("dddCel");

String acao = (String) request.getAttribute("acao");
String cripto = (String) request.getAttribute("exibeCodigo");

if(!TextHelper.isNull(serDdd)) {
    serTel = serDdd+serTel;
}

if(!TextHelper.isNull(celDdd)) {
    serCel = celDdd+serCel;
}

boolean permiteAltEmail = (request.getAttribute("permiteAltEmail") != null) && ((boolean) request.getAttribute("permiteAltEmail"));
boolean exigeAtualizacaoDadosSerPrimeiroAcesso = (request.getAttribute("exigeAtualizacaoDadosSerPrimeiroAcesso") != null);
Boolean paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado = (request.getAttribute("paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado") != null) ? (Boolean) request.getAttribute("paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado") : false;

%>

<c:set var="imageHeader">
    <use xlink:href="#i-mensagem"></use>
</c:set>
<c:set var="title">
   <hl:message key="mensagem.atualizacao.email.telefone.titulo"/>
</c:set>
<c:set var="bodyContent">
	<form method="post" action="../v3/atualizarEmailTelefone?acao=<%=acao%>">
	  <div class="card">
	    <div class="card-header hasIcon">
	      <span class="card-header-icon"><svg width="24"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-mensagem"></use></svg></span>
	      <h2 class="card-header-title"><hl:message key="mensagem.atualizacao.email.telefone.informacao"/></h2>
	    </div>
	    <div class="card-body">
	    <% if (ShowFieldHelper.showField(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel)) { %>
			<div class="row">
				<div class="form-group col-sm-12 col-md-6">
					<label for="email"><hl:message key="rotulo.servidor.email"/></label>
					<hl:htmlinput name="email"
						            di="email"
						          type="text"
						         classe="form-control"
					              value="<%=TextHelper.forHtmlAttribute( TextHelper.isNull(email) ? "" : email )%>"
					            onFocus="SetarEventoMascara(this,'#*100',true);"
					            onBlur="fout(this);ValidaMascara(this);"
					            readonly="<%=(!TextHelper.isNull(cripto) || !permiteAltEmail)?"true":"false"%>"
							placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.alterar.senha.digite.email", responsavel) %>"  />
				</div>
			</div>
             <% if (!permiteAltEmail && !TextHelper.isNull(email) && paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado) { %>
                 <div class="form-group">
                  <input class="mr-1" type="checkbox" name="email_Incorreto" id="email_Incorreto" value="S"/>
                   <label for="email_Incorreto">
  	                <hl:message key="mensagem.informacao.alterar.senha.digite.email.incorreto"/>
  	               </label>
                 </div>
            <% } %>
            <% if (!permiteAltEmail && TextHelper.isNull(cripto)) { %>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <hl:message key="mensagem.info.sem.permissao.alteracao.email"/>
                </div>
              </div>
            <% } %>
		<% } %>
    
    
		<% if (ShowFieldHelper.showField(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel)) { 
		 // Quebra o telefone em DDD + número.
			String serTelDdd = "";
			if (!TextHelper.isNull(serTel)) {
				serTel = TextHelper.dropSeparator(serTel);
				serTelDdd = serTel.substring(0, 2);
				serTel = serTel.substring(2, serTel.length()).trim();
			}
		%> 
			<div class="row">
				<show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_TELEFONE)%>">
					<div class="form-group col-sm-2">
						<label for="ddd"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
						<hl:htmlinput name="ddd"
						                di="ddd"
						              type="text"
						            classe="form-control"
					                 value="<%=TextHelper.forHtmlAttribute(serTelDdd)%>"
			        				  mask="<%=LocaleHelper.getDDDMask()%>"
			        			  readonly="<%=!TextHelper.isNull(cripto)?"true":"false"%>"
							   placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel) %>"  />
					</div>
				</show:showfield>
				<div class="form-group col-sm-6">
					<label for="telefone"><hl:message key="rotulo.servidor.telefone"/></label>
					<hl:htmlinput name="telefone"
								    di="telefone"
								  type="text"
								classe="form-control"
								 value="<%=TextHelper.forHtmlAttribute(serTel)%>"
								  mask="<%=LocaleHelper.getTelefoneMask()%>"
							  readonly="<%=!TextHelper.isNull(cripto)?"true":"false"%>"
						   placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.telefone", responsavel)%>" />
				</div>
			</div> 
		<%
 		    }
 		%>
    
        <%
                if (ShowFieldHelper.showField(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel)) { 
                     // Quebra o telefone em DDD + número.
                      String serCelDdd = "";
                      if (!TextHelper.isNull(serCel)) {
                  serCel = TextHelper.dropSeparator(serCel);
                serCelDdd = serCel.substring(0, 2);
                serCel = serCel.substring(2, serCel.length()).trim();
                      }
            %> 
          <div class="row">
            <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_SERVIDOR_DDD_CELULAR)%>">
              <div class="form-group col-sm-2">
                <label for="dddCel"><hl:message key="rotulo.servidor.codigo.localidade"/></label>
                <hl:htmlinput name="dddCel"
                                di="dddCel"
                              type="text"
                            classe="form-control"
                               value="<%=TextHelper.forHtmlAttribute(serCelDdd)%>"
                            mask="<%=LocaleHelper.getDDDCelularMask()%>"
                          readonly="<%=!TextHelper.isNull(cripto)?"true":"false"%>"
                     placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.ddd", responsavel)%>"  />
              </div>
            </show:showfield>
            <div class="form-group col-sm-6">
              <label for="celular"><hl:message key="rotulo.servidor.celular"/></label>
              <hl:htmlinput name="celular"
                        di="celular"
                      type="text"
                    classe="form-control"
                     value="<%=TextHelper.forHtmlAttribute(serCel)%>"
                      mask="<%=LocaleHelper.getCelularMask()%>"
                    readonly="<%=!TextHelper.isNull(cripto)?"true":"false"%>"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.celular", responsavel) %>" />
            </div>
          </div> 
        <% } %>

		<%if(!TextHelper.isNull(cripto)) { %>
            <div class="form-group col-sm-6">
              <label for="tokenEmail"><hl:message key="rotulo.digite.codigo.autorizacao.enviado.email"/></label>
              <hl:htmlinput name="tokenEmail"
                      di="tokenEmail"
                      type="text"
                    classe="form-control"
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.digite.codigo.autorizacao.enviado.email", responsavel) %>" />
            </div>
            
           <hl:htmlinput name="cripto" type="hidden" value="<%=cripto%>"/>
          <%} %>	
          
	    </div>
	  </div>
	</form>
	<div class="btn-action">
	  <a class="btn btn-primary" href="#no-back" onClick="validaForm(); return false;"><svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></a>
	</div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
 f0 = document.forms[0];
 
 function validaForm() {

	if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel)%> && f0.email != null && (f0.email.value == null || f0.email.value.trim() == '')) {
	    alert('<hl:message key="mensagem.informe.ser.email"/>');
	    f0.email.focus();
	    return false;
	}
	
	if (<%=ShowFieldHelper.showField(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel)%> && f0.email != null && f0.email.value != null && f0.email.value != '' && !isEmailValid(f0.email.value, "1")) {
	    alert('<hl:message key="mensagem.erro.email.invalido"/>');
	    f0.email.focus();
	    return false;
	}
			  
	<% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel)) { %>

		if (f0.ddd != null && (f0.ddd.value == null || f0.ddd.value.trim() == '')) {
			alert('<hl:message key="mensagem.informe.ser.ddd"/>');
			f0.ddd.focus();	
			return false;
		}
		
		if (f0.telefone != null && (f0.telefone.value == null || f0.telefone.value.trim() == '')) {
			alert('<hl:message key="mensagem.informe.ser.telefone"/>');
			f0.telefone.focus();
			return false;
		}

	<% } %>
	
	
	<% if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel)) { %>

    	if (f0.dddCel != null && (f0.dddCel.value == null || f0.dddCel.value.trim() == '')) {
    		alert('<hl:message key="mensagem.informe.ser.ddd"/>');
    		f0.dddCel.focus();	
    		return false;
    	}
    	
    	if (f0.celular != null && (f0.celular.value == null || f0.celular.value.trim() == '')) {
    		alert('<hl:message key="mensagem.informe.ser.celular"/>');
    		f0.celular.focus();
    		return false;
    	}

  <% } %>
	
	<% if (cripto != null && cripto != "") { %>
    
	    var emailIncorretoFoiSelecionado = false;
	    
	    <% if (!permiteAltEmail && !TextHelper.isNull(email) && paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado) { %>
	    	emailIncorretoFoiSelecionado = document.getElementById("email_Incorreto").checked == true	    
	    <%}%>
    	if (f0.cripto != null && (f0.tokenEmail.value == null || f0.tokenEmail.value.trim() == '') && !emailIncorretoFoiSelecionado) {
    		alert('<hl:message key="mensagem.informe.ser.codigo.conf.email"/>');
    		f0.tokenEmail.focus();	
    		return false;
    	}

  <% } %>
	 
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