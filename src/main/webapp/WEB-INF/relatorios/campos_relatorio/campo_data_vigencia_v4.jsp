<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
  String obrDataVigenciaPage =  JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String dataTaxa = JspHelper.verificaVarQryStr(request, "dataTaxa");
  
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
  
  String value = "";
  String others = "";
  if (!TextHelper.isNull(dataTaxa)) {
      value = dataTaxa;
      others = "disabled";
  }
  
  if (desabilitado) {
      others = "disabled";
  }
%>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblDataVigenciaPage" for="dataTaxa">${descricoes[recurso]}</label>
            <hl:htmlinput name="dataTaxa" di="dataTaxa" type="text" value="<%=TextHelper.forHtmlAttribute(value )%>" others="<%=TextHelper.forHtmlAttribute(others )%>" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" />
          </div>

  <% if (obrDataVigenciaPage.equals("true")) { %>
     <script type="text/JavaScript">
     function funDataVigenciaPage() {
        camposObrigatorios = camposObrigatorios + 'dataTaxa,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.taxas.data.vigencia"/>,';
     }
     addLoadEvent(funDataVigenciaPage);     
     </script>
  <% } %>
  
        <script type="text/JavaScript">
         function valida_campo_data_vigencia() {
             with(document.forms[0]) {
                if (dataTaxa != null && dataTaxa.value != '') {
                    if (!verificaData(dataTaxa.value)) {
                      dataTaxa.focus();
                      return false;
                    }
                }                 
             }              
             return true;
         }
        </script>        
  
