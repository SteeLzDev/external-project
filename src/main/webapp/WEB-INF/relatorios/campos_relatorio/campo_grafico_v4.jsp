<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
  String obrGraficoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  
  String chkGrafico = request.getParameter("chkGrafico");
  
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
          <fieldset class="col-sm-12 col-md-12">
            <div class="legend"><span>${descricoes[recurso]}</span></div>
            <div class="form-check">
               <div class="row">
                 <div class="col-sm-12 col-md-4">
                   <span class="text-nowrap align-text-top">
                     <INPUT class="form-check-input ml-1" TYPE="CHECKBOX" NAME="chkGrafico" ID="chkGrafico" VALUE="1" TITLE='<hl:message key="rotulo.sim"/>' <%=TextHelper.forHtmlContent((chkGrafico != null ? " checked " : ""))%><%=TextHelper.forHtmlContent((desabilitado ? " disabled " : ""))%> onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                     <label class="form-check-label labelSemNegrito ml-1" id="lblGraficoGraficoPage" for="chkGrafico"><hl:message key="rotulo.sim"/></label>
                   </span>                  
                 </div>
               </div> 
            </div>
          </fieldset>

        <script type="text/JavaScript">
         function valida_campo_grafico() {
             return true;
         }
        </script>        
