<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page
	import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<%
    String obrMatriculaPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String descMatriculaPage = pageContext.getAttribute("descricao").toString();   
    AcessoSistema responsavelMatriculaPage = JspHelper.getAcessoSistema(request);
    
 	//Máscara do campo de matrícula
    String maskMatricula = "#*20";
    Object matriculaNumerica = ParamSist.getInstance().getParam(CodedValues.TPC_MATRICULA_NUMERICA, responsavelMatriculaPage);
    if ((matriculaNumerica != null) && (matriculaNumerica.equals("S"))) {
       maskMatricula = "#D20";
    }
    int tamanhoMatriculaPage = 0;
    int tamMaxMatriculaPage = 0;
    try {
        Object param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavelMatriculaPage);
        tamanhoMatriculaPage = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
        param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, responsavelMatriculaPage);
        tamMaxMatriculaPage = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
    } 
    catch (Exception ex) {
    }
                      
    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
    
    String others = "";
    if (desabilitado) {
        others = "disabled";
    }
%>

<div class="row">
	<div class="form-group col-sm-12  col-md-6">
              <label id="lblMatriculaPage" for="RSE_MATRICULA"><%=TextHelper.forHtmlContent(descMatriculaPage)%></label>
              <hl:htmlinput name="RSE_MATRICULA" 
                            di="RSE_MATRICULA" 
                            type="text" 
                            classe="form-control"
                            mask="<%=TextHelper.forHtmlAttribute(maskMatricula)%>" 
                            size="10"
                            others="<%=TextHelper.forHtmlAttribute(others)%>"
                            maxlength="<%=(String)(tamMaxMatriculaPage > 0 ? String.valueOf(tamMaxMatriculaPage) : \"20\") %>"
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"))%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.digite.matricula", responsavelMatriculaPage)%>"
                />
            </div>
	<div class="form-group col-sm-12 col-md-1 mt-4">
		<a id="adicionaRseMatricula" class="btn btn-primary w-50"
			href="javascript:void(0);"
			onClick="insereMatriculaLista('RSE_MATRICULA', 'RSE_MATRICULA_MULTIPLO');"
			aria-label='<hl:message key="mensagem.inserir.ade.numero.clique.aqui"/>'>
			<svg width="15">
				<use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
		</a> <a id="removeRseMatricula" class="btn btn-primary w-50 mt-1"
			href="javascript:void(0);" onClick="removeMatriculaDaLista('RSE_MATRICULA_MULTIPLO');"
			aria-label='<hl:message key="mensagem.remover.ade.numero.clique.aqui"/>'>
			<svg width="15">
				<use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
		</a>
	</div>
	<div class="form-group col-sm-12 col-md-5">
		<label for="RSE_MATRICULA_MULTIPLO"><hl:message key="rotulo.lista" /></label>
		<select class="form-control w-100" id="RSE_MATRICULA_MULTIPLO" name="RSE_MATRICULA_MULTIPLO" multiple="multiple" size="6"></select>
	</div>
</div>

<script>
	 function valida_campo_matricula_multiplo() {
		 <% if (obrMatriculaPage.equals("true")) { %>
			 var lista = document.getElementById("RSE_MATRICULA_MULTIPLO");
				if(lista.options.length) {
					selecionarTodasMatriculas("RSE_MATRICULA_MULTIPLO");
				    return true;
				} else {
					mostrarMensagem('mensagens','danger', '<hl:message key="mensagem.informe.matricula"/>');
					return false;
				}
		 <% } %>
		 selecionarTodasMatriculas("RSE_MATRICULA_MULTIPLO");
		 return true;
	 }
	 
	 function vfRseMatricula() 
     {	
       var matriculaField = document.getElementById('RSE_MATRICULA');
       var matricula = matriculaField.value;
       var tamMinMatricula = <%=TextHelper.forJavaScriptBlock(tamanhoMatriculaPage)%>;    
       var tamMaxMatricula = <%=TextHelper.forJavaScriptBlock(tamMaxMatriculaPage)%>;
           
       if (matricula != ''){     
           if(matricula.length < tamMinMatricula){
             alert('<hl:message key="mensagem.erro.matricula.tamanho.min" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamanhoMatriculaPage))%>"/>');
             if (QualNavegador() == "NE") {
                   globalvar = matriculaField;
                   setTimeout("globalvar.focus()",0);
               } 
               else
                   matriculaField.focus();         
           }
           else if(tamMaxMatricula > 0 && matricula.length > tamMaxMatricula){
             alert('<hl:message key="mensagem.erro.matricula.tamanho.max" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamMaxMatriculaPage))%>"/>');
             if (QualNavegador() == "NE") {
                   globalvar = matriculaField;
                   setTimeout("globalvar.focus()",0);
               } 
               else
                   matriculaField.focus();
           }
           else{
             matriculaField.style.color = 'black';
               return true;
           }     
       }
       else{
         matriculaField.style.color = 'black';
         return true;
       }
     } 

     function insereMatriculaLista(nomeCampoValor, nomeCampoLista) {
 	  var lista = document.getElementById(nomeCampoLista);
 	  var valor = document.getElementById(nomeCampoValor);
 	  
 	 
 	 if(vfRseMatricula()){
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
 
 function removeMatriculaDaLista(selectId) {
 	  var selectComp = document.getElementById(selectId);
 	  
 	  for (var i = selectComp.length - 1; i >= 0; i--) {
 	    if (selectComp.options[i].selected) {
 	      selectComp.options[i] = null;
 	    }
 	  }
 	}
 
 function selecionarTodasMatriculas(selectId) {
	  var selectComp = document.getElementById(selectId);
	    if (selectComp != null) {
	    for (var i = selectComp.length - 1; i >= 0; i--) {
	      selectComp.options[i].selected = true;
	    }
	  }
	}
 </script>
