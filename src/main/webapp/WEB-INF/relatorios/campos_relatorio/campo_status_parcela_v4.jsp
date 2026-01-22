<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<% 
    String obrStatusPrdPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");  
    String descStatusPrdPage = pageContext.getAttribute("descricao").toString();       
    AcessoSistema responsavelStatusPrdPage = JspHelper.getAcessoSistema(request);
    
    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    String desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? "true" : "false";
%>
        <hl:filtroStatusPrdTagv4 disabled="<%=TextHelper.forHtmlAttribute(desabilitado)%>" descricao="<%=TextHelper.forHtmlAttribute(descStatusPrdPage)%>" />
        <script type="text/JavaScript">
        function valida_campo_status_parcela() {
          <%if (obrStatusPrdPage.equals("true")) {%>
          var tam = document.forms[0].SPD_CODIGO.length;
          var qtd = 0;
          for (var i = 0; i < tam; i++) {
            if (document.forms[0].SPD_CODIGO[i].checked == true) {
              qtd++;
            }
          }
          if (qtd <= 0) {
            alert('<hl:message key="mensagem.informe.prd.status"/>');
            return false;
          }
          <%}%>
          return true;
        }
        </script>
