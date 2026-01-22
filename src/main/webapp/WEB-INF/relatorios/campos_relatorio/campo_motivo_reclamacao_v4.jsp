<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<% 
  String obrMotivoRecPage =  JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descMotivoRecPage = pageContext.getAttribute("descricao").toString();   
  
  String [] tmrCodigo = request.getParameterValues("TMR_CODIGO");
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
  
  String disabled = "false";
  if (tmrCodigo != null || desabilitado) {
      disabled = "true";
  }
%>
      <hl:filtroMotivoRecTagv4 disabled="<%=TextHelper.forHtmlAttribute(disabled )%>" descricao="<%=TextHelper.forHtmlAttribute(descMotivoRecPage)%>" />
        
        <script type="text/JavaScript">
         function valida_campo_motivo_reclamacao() {
            <% if (obrMotivoRecPage.equals("true")) { %>
             var tam = document.forms[0].SRS_CODIGO.length;
             var qtd = 0;
             for(var i = 0; i < tam; i++) {
               if (document.forms[0].SRS_CODIGO[i].checked == true) {
                 qtd++
               }
             }
             if (qtd <= 0) {
               alert('<hl:message key="mensagem.informe.motivo.reclamacao"/>');
               return false;
             }             
            <% } %>    
             return true;
         }
        </script>        
