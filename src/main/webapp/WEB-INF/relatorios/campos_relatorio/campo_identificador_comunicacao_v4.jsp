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
  AcessoSistema responsavelCmnPage = JspHelper.getAcessoSistema(request);
  String obrCmnPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descCmnPage = pageContext.getAttribute("descricao").toString();     
  String cmnNumero = (String) JspHelper.verificaVarQryStr(request, "cmnNumero");
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

%>
    <div class="form-group col-sm-12 col-md-6">
      <label for="CMN_NUMERO"><%=TextHelper.forHtmlContent(descCmnPage)%></label>
      <hl:htmlinput name="CMN_NUMERO"
                    di="CMN_NUMERO" 
                    type="text" 
                    classe="form-control"
                    mask="#D20" 
                    size="20"
                    value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CMN_NUMERO"))%>" 
                    nf="btnEnvia" 
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelCmnPage, descCmnPage)%>"
      />
    </div>   
          
    <% if (obrCmnPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funCmnPage() {
          camposObrigatorios = camposObrigatorios + 'cmnNumero,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.cmn.identificador"/>,';
      }
      addLoadEvent(funCmnPage);     
      </script>
    <% } %>   
    
        <script type="text/JavaScript">
         function valida_campo_identificador_comunicacao() {
             return true;
         }
        </script>        
                
