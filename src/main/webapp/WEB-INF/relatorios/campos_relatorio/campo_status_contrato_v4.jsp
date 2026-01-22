<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<% 
   String obrStatusPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String descStatusPage = pageContext.getAttribute("descricao").toString();
   String [] sadCodigo = request.getParameterValues("SAD_CODIGO");
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   
   String disabled = "false";
   if (sadCodigo != null || desabilitado) {
       disabled = "true";
   }
%>
        <hl:filtroStatusAdeTagv4 disabled="<%=TextHelper.forHtmlAttribute(disabled )%>" descricao="<%=TextHelper.forHtmlAttribute(descStatusPage)%>" />
        <script type="text/JavaScript">
        function valida_campo_status_contrato() {
          <%if (obrStatusPage.equals("true")) {%>
          var descSadCodigo = '<%=TextHelper.forJavaScriptBlock(descStatusPage)%>';
          var tam = document.forms[0].SAD_CODIGO.length;
          var qtd = 0;
          for(var i = 0; i < tam; i++) {
           if (document.forms[0].SAD_CODIGO[i].checked == true) {
             qtd++
           }
          }
          if (qtd <= 0) {
           alert('<hl:message key="mensagem.informe.ade.status"/>');
           return false;
          }
          <%}%>
          return true;
        }
        </script>
