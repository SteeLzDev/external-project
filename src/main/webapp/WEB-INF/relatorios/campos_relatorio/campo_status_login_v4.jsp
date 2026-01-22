<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<% 
  String obrStatusLoginPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descStatusLoginPage = pageContext.getAttribute("descricao").toString();   
  AcessoSistema responsavelStatusLoginPage = JspHelper.getAcessoSistema(request);

  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  String desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? "true" : "false";
%>
        <hl:filtroStatusLoginTagv4 disabled="<%=TextHelper.forHtmlAttribute(desabilitado)%>" descricao="<%=TextHelper.forHtmlAttribute(descStatusLoginPage)%>" />

        <script type="text/JavaScript">
         function valida_campo_status_login() {
            <% if (obrStatusLoginPage.equals("true")) { %>
             var tam = document.forms[0].STU_CODIGO.length;
             var qtd = 0;
             for (var i = 0; i < tam; i++) {
               if (document.forms[0].STU_CODIGO[i].checked == true) {
                 qtd++;
               }
             }
             if (qtd <= 0) {
               alert('<hl:message key="mensagem.informe.login.status"/>');
               return false;
             }                                                  
            <% } %>                                              
             return true;
         }
        </script>        
