<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<%
     AcessoSistema responsavelOpLoginPage = JspHelper.getAcessoSistema(request);
     String mensagemOpLogin = ApplicationResourcesHelper.getMessage("mensagem.filtro.op.login", responsavelOpLoginPage);
     String obrOpLoginPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
     String paramOpPage = JspHelper.verificaVarQryStr(request, "PARAMETRO");
     String descOpLoginPage = pageContext.getAttribute("descricao").toString();
     String opLogin = JspHelper.verificaVarQryStr(request, "OP_LOGIN");
     
     String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
     boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
     
     String parametroRelatorioEditavel = JspHelper.verificaVarQryStr(request, "PARAMETRO");
     if(!TextHelper.isNull(parametroRelatorioEditavel)){
         String[] splitParametro = parametroRelatorioEditavel.split(";");
         if(splitParametro.length > 1){
             opLogin = splitParametro[1];
             desabilitado = true;
         }
     }

%>
    <div class="form-group col-sm-12  col-md-6">
        <label id="lblOpLoginPage" for="OP_LOGIN"><%=TextHelper.forHtmlContent(descOpLoginPage)%></label>
        <input name="OP_LOGIN" id="OP_LOGIN" type="text" class="form-control" <% if (!TextHelper.isNull(opLogin) || desabilitado) { %> value="<%=TextHelper.forHtmlAttribute(opLogin )%>" disabled <%} %> size="20" onFocus="" onBlur="" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelOpLoginPage, descOpLoginPage)%>"/>
        <% if (paramOpPage.equalsIgnoreCase("MULTIPLO")) { %>
            <div class="slider col-sm-12 col-md-12 mt-2 pl-0 pr-0">
              <div class="tooltip-inner"><%=TextHelper.forHtmlContent(mensagemOpLogin)%></div>
            </div>
        <% } %>
    </div>

    <% if (obrOpLoginPage.equals("true")) { %>
        <script type="text/JavaScript">
        function funOpLoginPage() {
            camposObrigatorios = camposObrigatorios + 'OP_LOGIN,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.arg0" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(descOpLoginPage))%>"/>,';
        }
        addLoadEvent(funOpLoginPage);     
        </script>
    <% } %>                        

        <script type="text/JavaScript">
         function valida_campo_op_login() {
             return true;
         }
        </script>        
