<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
            <div class="form-group col-sm-12  col-md-6">
              <label id="lblMatriculaPage" for="RSE_MATRICULA"><%=TextHelper.forHtmlContent(descMatriculaPage)%></label>
              <hl:htmlinput name="RSE_MATRICULA" 
                            di="RSE_MATRICULA" 
                            type="text" 
                            classe="form-control"
                            mask="<%=TextHelper.forHtmlAttribute(maskMatricula)%>" 
                            size="10"
                            others="<%=TextHelper.forHtmlAttribute(others )%>"
                            maxlength="<%=(String)(tamMaxMatriculaPage > 0 ? String.valueOf(tamMaxMatriculaPage) : \"20\") %>"
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"))%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.digite.matricula", responsavelMatriculaPage)%>"
                            onBlur="vfRseMatricula();"
                />
            </div>
            
    <% if (obrMatriculaPage.equals("true")) { %>
        <script type="text/JavaScript">
        function funMatriculaPage() {
            camposObrigatorios = camposObrigatorios + 'RSE_MATRICULA,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.matricula"/>,';
        }
        addLoadEvent(funMatriculaPage);     
        </script>
    <% } %>    
    
       <script>
      
       function vfRseMatricula(validaForm) 
       {   
         if(validaForm === undefined) {
       	  validaForm = false;
         }
         
         var matriculaField = document.getElementById('RSE_MATRICULA');
         var matricula = matriculaField.value;
         var tamMinMatricula = <%=TextHelper.forJavaScriptBlock(tamanhoMatriculaPage)%>;    
         var tamMaxMatricula = <%=TextHelper.forJavaScriptBlock(tamMaxMatriculaPage)%>;
             
         if (matricula != ''){     
                 
           if(validaForm){
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
       
       function valida_campo_matricula() {
           return vfRseMatricula(true);
       }
        </script>        
       