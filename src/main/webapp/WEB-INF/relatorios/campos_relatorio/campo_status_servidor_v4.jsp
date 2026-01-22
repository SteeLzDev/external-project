<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<% 
  String obrStatusSerPage =  JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descStatusSerPage = pageContext.getAttribute("descricao").toString();   
  
  String [] srsCodigo = request.getParameterValues("SRS_CODIGO");
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
  
  String disabled = "false";
  if (srsCodigo != null || desabilitado) {
      disabled = "true";
  }
%>
      <hl:filtroStatusRegSerTagv4 disabled="<%=TextHelper.forHtmlAttribute(disabled)%>" descricao="<%=TextHelper.forHtmlAttribute(descStatusSerPage)%>" />
      <script type="text/JavaScript">
      function valida_campo_status_servidor() {
        <%if (obrStatusSerPage.equals("true")) {%>
        var tam = document.forms[0].SRS_CODIGO.length;
        var qtd = 0;
        for (var i = 0; i < tam; i++) {
          if (document.forms[0].SRS_CODIGO[i].checked == true) {
            qtd++;
          }
        }
        if (qtd <= 0) {
          alert('<hl:message key="mensagem.informe.rse.status"/>');
          return false;
        }
        <%}%>
        return true;
      }
      </script>
