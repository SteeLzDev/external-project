<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<%
    String obrCpfPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String descCpfPage = pageContext.getAttribute("descricao").toString();   
    AcessoSistema responsavelCpfPage = JspHelper.getAcessoSistema(request);
    
    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
    
    String others = "";
    if (desabilitado) {
        others = "disabled";
    }
%>

<div class="row">
	<div class="form-group col-sm-12  col-md-6">
		<label id="lblCPF" for="CPF">${descricoes[recurso]}</label>
          <hl:htmlinput name="CPF" 
                        di="CPF" 
                        type="text" 
                        classe="form-control"
                        mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>" 
                        size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfSize())%>"
                        maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMaxLenght())%>"
                        others="<%=TextHelper.forHtmlAttribute(others)%>"
                        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CPF"))%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.digite.cpf", responsavelCpfPage)%>" 
          />
	</div>
	<div class="form-group col-sm-12 col-md-1 mt-4">
		<a id="adicionaCpf" class="btn btn-primary w-50"
			href="javascript:void(0);"
			onClick="insereCpfLista('CPF', 'SER_CPF_MULTIPLO');"
			aria-label='<hl:message key="mensagem.inserir.ade.numero.clique.aqui"/>'>
			<svg width="15">
				<use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
		</a> <a id="removeCpf" class="btn btn-primary w-50 mt-1"
			href="javascript:void(0);" onClick="removeCpfDaLista('SER_CPF_MULTIPLO');"
			aria-label='<hl:message key="mensagem.remover.ade.numero.clique.aqui"/>'>
			<svg width="15">
				<use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
		</a>
	</div>
	<div class="form-group col-sm-12 col-md-5">
		<label for="SER_CPF_MULTIPLO"><hl:message key="rotulo.lista" /></label>
		<select class="form-control w-100" id="SER_CPF_MULTIPLO" name="SER_CPF_MULTIPLO" multiple="multiple" size="6"></select>
	</div>
</div>

<script>
	 function valida_campo_cpf_multiplo() {
		 <% if (obrCpfPage.equals("true")) { %>
		 	var lista = document.getElementById("SER_CPF_MULTIPLO");
			if(lista.options.length) {
				selecionarTodosCpfs('SER_CPF_MULTIPLO');
			    return true;
			} else {
				mostrarMensagem('mensagens','danger', '<hl:message key="mensagem.informe.cpf"/>');
				return false;
			}
		<% } %>
		 selecionarTodosCpfs('SER_CPF_MULTIPLO');
	     return true;
	 }
	 
	 function insereCpfLista(nomeCampoValor, nomeCampoLista) {
 	  var lista = document.getElementById(nomeCampoLista);
 	  var valor = document.getElementById(nomeCampoValor);

 	 if(CPF_OK(valor.value.replaceAll(".","").replaceAll("-",""))){
 	  if (valor.value != null && valor.value != '') {
 	    for (var i = 0; i < lista.length; i++) {
 	      if (lista.options[i].value == valor.value) {
 	        alert(mensagem('mensagem.erro.lista.valor.existe'));
 	        valor.focus();
 	        return;
 	      }
 	    }

 	    var opt = new Option(valor.value, valor.value);
 	    lista.options[lista.length] = opt;
 	    valor.value = '';
 	    valor.focus();
 	  }
 	 }
 	}
 
 function removeCpfDaLista(selectId) {
 	  var selectComp = document.getElementById(selectId);
 	  
 	  for (var i = selectComp.length - 1; i >= 0; i--) {
 	    if (selectComp.options[i].selected) {
 	      selectComp.options[i] = null;
 	    }
 	  }
 	}
 
 function selecionarTodosCpfs(selectId) {
	  var selectComp = document.getElementById(selectId);
	    if (selectComp != null) {
	    for (var i = selectComp.length - 1; i >= 0; i--) {
	      selectComp.options[i].selected = true;
	    }
	  }
	}
 </script>
