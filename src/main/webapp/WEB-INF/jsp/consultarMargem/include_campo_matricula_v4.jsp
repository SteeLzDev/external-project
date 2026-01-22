<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ taglib uri="/html-lib" prefix="hl"%>

<%
  AcessoSistema _responsavel = JspHelper.getAcessoSistema(request);

  String configKey = (String) request.getAttribute("configKey");

  // Parâmetro de sistema que indica que utiliza a matricula
  Object servidorPossuiMatricula = ParamSist.getInstance().getParam(CodedValues.TPC_SERVIDOR_POSSUI_MATRICULA, _responsavel);
  boolean utilizaMatricula = (servidorPossuiMatricula == null || servidorPossuiMatricula.equals(CodedValues.TPC_SIM));

  if (utilizaMatricula) {

    //Quantidade mínima de dígitos da matrícula a ser informado
    int tamanhoMatricula = 0;
    // Quantidade máxima de dígitos da matrícula a ser informado
    int tamMaxMatricula = 0;
    try {
      Object param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, _responsavel);
      tamanhoMatricula = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
      param = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, _responsavel);
      tamMaxMatricula = (param != null && !param.equals("")) ? Integer.parseInt(param.toString()) : 0;
    } 
    catch (Exception ex) {
      
    }
    
    // Máscara do campo de matrícula
    String maskMatricula = "#*20";
    Object matriculaNumerica = ParamSist.getInstance().getParam(CodedValues.TPC_MATRICULA_NUMERICA, _responsavel);
    
    if ((matriculaNumerica != null) && (matriculaNumerica.equals("S"))) {
      maskMatricula = "#D20";
    }
%>

<script language="JavaScript" type="text/JavaScript">
  
function vfRseMatricula(validaForm) 
{  
  if(validaForm === undefined) {
       validaForm = false;
  }
  
  var matriculaField = document.getElementById('RSE_MATRICULA');
  var matricula = matriculaField.value;
  var tamMinMatricula = <%=TextHelper.forJavaScriptBlock(tamanhoMatricula)%>;    
  var tamMaxMatricula = <%=TextHelper.forJavaScriptBlock(tamMaxMatricula)%>;
      
  if (matricula != ''){    	
    if(validaForm){
      if(matricula.length < tamMinMatricula){
      	alert('<hl:message key="mensagem.erro.matricula.tamanho.min" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamanhoMatricula))%>"/>');
      	if (QualNavegador() == "NE") {
              globalvar = matriculaField;
              setTimeout("globalvar.focus()",0);
          } 
          else
              matriculaField.focus();        	
    	}
      else if(tamMaxMatricula > 0 && matricula.length > tamMaxMatricula){
      	alert('<hl:message key="mensagem.erro.matricula.tamanho.max" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamMaxMatricula))%>"/>');
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
  	  if(matricula.length < tamMinMatricula){
			matriculaField.style.color = 'red';
			return false;
		  }    	  
  	  else if(tamMaxMatricula > 0 && matricula.length > tamMaxMatricula){
			matriculaField.style.color = 'red';
			return false;
		  }
  	  else{
  		  matriculaField.style.color = 'black';
  		  return true;      	
		  }    		  
    }
  }
  else{
		matriculaField.style.color = 'black';
		return true;
	}      
}
</script>

  <div class="form-group col-sm pl-0 pr-0" id="include-campo-matricula">      
      <label for="RSE_MATRICULA"><hl:message key="rotulo.servidor.matricula"/></label>        
      <hl:htmlinput 
        name="RSE_MATRICULA" 
        di="RSE_MATRICULA" 
        type="text"       
        size="10"
        mask="<%=TextHelper.forHtmlAttribute(maskMatricula)%>" 
        maxlength="<%=(String)(tamMaxMatricula > 0 ? String.valueOf(tamMaxMatricula) : \"20\") %>"
        onBlur="javascript:vfRseMatricula();" 
        classe="form-control"
        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"))%>"        
        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", _responsavel)%>"
      />
  </div>

<% } %>
