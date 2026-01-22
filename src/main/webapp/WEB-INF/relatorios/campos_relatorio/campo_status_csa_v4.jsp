<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<% 
  String obrStatusCsaPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descStatusCsaPage = pageContext.getAttribute("descricao").toString();
  
  String csaAtivo = request.getParameter("CSA_ATIVO");
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
  
  String disabled = "false";
  if (csaAtivo != null || desabilitado) {
      disabled = "true";
  }
%>
      <hl:filtroStatusCsaTagv4 disabled="<%=TextHelper.forHtmlAttribute(disabled )%>" descricao="<%=TextHelper.forHtmlAttribute(descStatusCsaPage)%>" />
        
        <script type="text/JavaScript">
         function valida_campo_status_csa() {
            <% if (obrStatusCsaPage.equals("true")) { %>
             var descSrsCodigo = '<%=TextHelper.forJavaScriptBlock(descStatusCsaPage)%>';
             var tam = document.forms[0].CSA_ATIVO.length;
             var qtd = 0;
             for(var i = 0; i < tam; i++) {
               if (document.forms[0].CSA_ATIVO[i].checked == true) {
                 qtd++
               }
             }
             if (qtd <= 0) {
               alert('<hl:message key="mensagem.informe.csa.status"/>');
               return false;
             }             
            <% } %>    
             return true;
         }
        </script>        
