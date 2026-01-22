<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<% 
  String obrVincSerPage =  JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descVincSerPage = pageContext.getAttribute("descricao").toString();   
  String rseTipo = JspHelper.verificaVarQryStr(request, "RSE_TIPO");
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");

  AcessoSistema responsavelVincSerPage = JspHelper.getAcessoSistema(request);
  
%>
        <div class="form-group col-sm-12 col-md-6">
          <label id="lblVinculo" for="RSE_TIPO"><hl:message key="rotulo.servidor.categoria"/></label>            
          <hl:htmlinput name="RSE_TIPO" 
                        di="RSE_TIPO" 
                        type="text" 
                        classe="form-control"
                        mask="#*20" 
                        size="10"
                        maxlength="20"
                        value="<%=TextHelper.forHtmlAttribute(rseTipo)%>"
                        readonly="<%=TextHelper.forHtmlAttribute(paramDisabled)%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelVincSerPage, descVincSerPage)%>"
            />
        </div>

        <script type="text/JavaScript">
         function valida_campo_vinculo_servidor() {
             return true;
         }
        </script>        
