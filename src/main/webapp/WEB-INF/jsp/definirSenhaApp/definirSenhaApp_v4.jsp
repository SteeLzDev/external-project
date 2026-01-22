<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

 String serEmail = (String) request.getAttribute("serEmail");
 Integer tamMinSenha = (Integer) request.getAttribute("tamMinSenha");
 Integer tamMaxSenha = (Integer) request.getAttribute("tamMaxSenha");
 Integer pwdStrengthLevel = (Integer) request.getAttribute("pwdStrengthLevel");
 String strMensagemSenha = (String) request.getAttribute("strMensagemSenha");
 String strMensagemErroSenha = (String) request.getAttribute("strMensagemErroSenha");
 boolean ignoraSeveridade = (request.getAttribute("ignoraSeveridade") != null);
 boolean senhaServidorNumerica = (request.getAttribute("senhaServidorNumerica") != null);
 boolean emailUsurioSer = (request.getAttribute("emailUsurioSer") != null);
%>

<c:set var="title">
	<hl:message key="rotulo.definir.senha.app.titulo" />
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-home"></use>
</c:set>

<c:set var="bodyContent">
	<div class="row justify-content-md-center">
		<div class="col-sm-12 form-check mt-2 form-group">
			<div class="card">
				<div class="card-header">
					<h2 class="card-header-title">
						<hl:message key="rotulo.editar.grid" />
					</h2>
				</div>
				<div class="card-body">
          			<div class="alert alert-warning" role="alert">
          				<%=strMensagemSenha%>
       				</div>
					<form name="form1" id="form1" method="post" action="../v3/definirSenhaApp" autocomplete="off">
						<input type="hidden" name="acao" value="alterar" />
						<%=SynchronizerToken.generateHtmlToken(request)%>
						
						<% if (!ignoraSeveridade) { %>
						<div class="row">
							<div class="form-group col-sm-12 col-md-6">
								<div class="alert alert-success" role="alert">
							    	<hl:message key="rotulo.usuario.nivel.seguranca"/>: <span id="verdict"><hl:message key="rotulo.nivel.senha.muito.baixo"/></span>
							    </div>
							</div>
						</div>
						 <% } %>
						<div class="row">
					    	<div class="form-group col-sm-12 col-md-6">
					           	<label for="senhaNova"><hl:message key="rotulo.usuario.nova.senha.app"/></label>
					           	<hl:htmlpassword name="senhaNova" di="senhaNova" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.senha", responsavel)%>" cryptedfield="senhaNovaRSA" onFocus="setMascaraSenha(this,true); setanewOnKeyUp(this);" onBlur="fout(this);ValidaMascaraV4(this);newOnKeyUp(this);" classe="form-control" isSenhaServidor="true" />
					       	</div>
					    </div>

						<input name="score" type="hidden" id="score">
						<input name="matchlog" type="hidden" id="matchlog">

						<div class="row">
					    	<div class="form-group col-sm-12 col-md-6">
					           	<label for="senhaNovaConfirmacao"><hl:message key="rotulo.usuario.confirma.nova.senha.app"/></label>
					           	<hl:htmlpassword name="senhaNovaConfirmacao" di="senhaNovaConfirmacao" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.confirme.nova.senha", responsavel)%>" cryptedfield="senhaNovaConfirmacaoRSA" onFocus="setMascaraSenha(this,true);" onBlur="fout(this);ValidaMascaraV4(this);" classe="form-control" isSenhaServidor="true" />
					       	</div>
					    </div>

						<% if (emailUsurioSer) { %>
						<div class="row">
						  	<div class="form-group col-sm-12 col-md-12">
						       	<div class="alert alert-warning" role="alert">
						           	<hl:message key="rotulo.ajuda.alteracaoEmail.servidor.obrigatorio"/>
						        </div>
						    </div>
						</div>
						<div class="row">
						   	<div class="form-group col-sm-12 col-md-12">
								<label for="email"><hl:message key="rotulo.servidor.email"/></label>
						       	<input type="text" class="form-control" id="email" name="email" value="<%=TextHelper.forHtmlAttribute( TextHelper.isNull(serEmail) ? "" : serEmail )%>" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email", responsavel)%>">
						    </div>
						</div>
						
						<% } %>
					</form>
					<div class="btn-action">
						<a class="btn btn-outline-danger" id="btnCancelar" href="#no-back" onClick="postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true'); return false;"><hl:message key="rotulo.botao.cancelar" /></a>
						<a class="btn btn-primary" id="btnSalvarNovaSenha" href="#no-back" onClick="if(verificaForm()){cleanFields();} return false;"><hl:message key="rotulo.botao.salvar" /></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" src="../js/passwordmeter_3010.js?<hl:message key="release.tag"/>"></script>

	<script type="text/JavaScript">
		f0 = document.forms[0];
	
		var primOnFocus = true;

		function formLoad() {
			if( f0.senha != null) {
		    	f0.senha.focus();
			}
		}
		
		window.onload = formLoad; 

		function newOnKeyUp(Controle) {
		  <% if (!ignoraSeveridade) { %>
			testPassword(Controle.value);
		  <% } %>
		}
		
		function setanewOnKeyUp(Controle) {
		  <% if (!ignoraSeveridade) { %>
			if(!primOnFocus) {
		    	return false;
		  	}
		  
			primOnFocus = false;
		  	var oldonkeyup = Controle.onkeyup;
		  
		  	if (typeof Controle.onkeyup != 'function') {
		    	Controle.onkeyup = newOnKeyUp(Controle);
		  	} else {
		    	Controle.onkeyup = function() {
		      		if (oldonkeyup) {
		        		oldonkeyup(Controle);
		      		}
		      		testPassword(Controle.value);
		    	}
		  	}
		  <% } %>
		}
		
		function setMascaraSenha(ctrl, AutoSkip) {
		  	SetarEventoMascaraV4(ctrl,'<%=(String)("#" + ((responsavel.isSer() && senhaServidorNumerica) ? "D" : "*") + tamMaxSenha)%>', AutoSkip);
		}
		
		function limpaSenhas() {
		  	f0.senhaNova.value = '';
		  	f0.senhaNovaConfirmacao.value = '';
		  	f0.senhaNova.focus();
		  <% if (!ignoraSeveridade) { %>
		  	f0.verdict.value = '';
		  <% } %>  
		}
		
		function verificaForm () {
		  	if ((f0.senhaNova.value != null) && (f0.senhaNovaConfirmacao.value != null) &&
		       (f0.senhaNova.value != "") && (f0.senhaNovaConfirmacao.value != "")) {
		    	newOnKeyUp(f0.senhaNova);
		    	if (f0.senhaNova.value.length < <%=tamMinSenha%>) {
		        	alert('<hl:message key="mensagem.erro.nova.senha.deve.ter.pelo.menos.arg0.caracteres" arg0="<%=String.valueOf(tamMinSenha)%>"/>');
		        	limpaSenhas();
		        	return false;
		    	} else if (f0.senhaNova.value.length > <%=tamMaxSenha%>) {
		        	alert('<hl:message key="mensagem.erro.nova.senha.deve.ter.no.maximo.arg0.caracteres" arg0="<%=String.valueOf(tamMaxSenha)%>"/>');
		        	limpaSenhas();
		        	return false;
		    	} else if (f0.senhaNova.value == f0.senhaNovaConfirmacao.value) {
		    <% if (!ignoraSeveridade) { %>    
		       	if (f0.score.value < <%=(int)pwdStrengthLevel%>) {
			    	    alert ('<%=strMensagemErroSenha%>');
			     	limpaSenhas();
			     	return false;
		       	}
		    <% } %>      
		    	} else {
		        	alert('<hl:message key="mensagem.erro.campo.nova.senha.diverge.confirma"/>');
		        	limpaSenhas();
		        	return false;
		    	}
		  	} else if(f0.senhaNova.value == null || f0.senhaNova.value == "") {
				alert('<hl:message key="mensagem.informe.servidor.usuario.senha"/>');
				limpaSenhas();
			  	return false;
			  
		  	} else if(f0.senhaNovaConfirmacao.value == null || f0.senhaNovaConfirmacao.value == "") {
				alert('<hl:message key="mensagem.informe.servidor.usuario.senha.confirmacao"/>');
				limpaSenhas();
			  	return false;
			}
		
		  	if (f0.email != null && (f0.email.value == null || f0.email.value.trim() == '')) {
		    	alert('<hl:message key="mensagem.informe.ser.email"/>');
		    	f0.email.focus();
		    	return false;
		  	}
		
		  	if (f0.email != null && f0.email.value != null && f0.email.value != '' && !isEmailValid(f0.email.value)) {
		    	alert('<hl:message key="mensagem.erro.email.invalido"/>');
		    	f0.email.focus();
		    	return false;
		  	}
		
		  	// A validação passou.
		  	f0.senhaNovaRSA.value = criptografaRSA(f0.senhaNova.value);;
		  	f0.senhaNova.value = '';
		  	f0.senhaNovaConfirmacao.value = '';
		  	return true;
		}
		
		jQuery(function () {
			// Pensar em forma de desabilitar o copy/paste usando outro valor ao invés de class
		    var controls = jQuery("input[name$='email']") // Seleciona os campos input com name terminado em 'email'
		    controls.bind("paste", function () {
		        return false;
		    });
		    controls.bind("cut", function () {
		        return false;
		    });
		    controls.bind("copy", function () {
		        return false;
		    });
		});
		
		function cleanFields() {
			if (f0.senhaNova && f0.senhaNova.type == "password") {
				f0.senhaNova.type = "hidden";
		   	}
		   	if (f0.senhaNovaConfirmacao && f0.senhaNovaConfirmacao.type == "password") {
				f0.senhaNovaConfirmacao.type = "hidden";
		   	}
		   	f0.submit();
		}
	</script>
</c:set>

<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>