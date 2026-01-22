<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<%
  AcessoSistema responsavelAdeNumPage = JspHelper.getAcessoSistema(request);
  String obrAdeNumPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descAdeNumPage = pageContext.getAttribute("descricao").toString();     
  String adeNumero = (String) JspHelper.verificaVarQryStr(request, "adeNumero");
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

%>
    <div class="form-group col-sm-12 col-md-6">
      <label for="ADE_NUMERO"><%=TextHelper.forHtmlContent(descAdeNumPage)%></label>
      <hl:htmlinput name="ADE_NUMERO"
                    di="ADE_NUMERO" 
                    type="text" 
                    classe="form-control"
                    mask="#D20" 
                    size="20"
                    value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_NUMERO"))%>" 
                    nf="btnEnvia" 
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelAdeNumPage, descAdeNumPage)%>"
      />
    </div>   
          
    <% if (obrAdeNumPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funAdeNumPage() {
          camposObrigatorios = camposObrigatorios + 'adeNumero,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ade.numero"/>,';
      }
      addLoadEvent(funAdeNumPage);     
      </script>
    <% } %>   
    
        <script type="text/JavaScript">
         function valida_campo_ade_numero() {
             return true;
         }
        </script>        
                
