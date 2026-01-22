<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
   String obrSerPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");   
   AcessoSistema responsavelSerPage = JspHelper.getAcessoSistema(request);
   
   String nome = (String) JspHelper.verificaVarQryStr(request, "nome");
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   
   String parametroRelatorioEditavel = JspHelper.verificaVarQryStr(request, "PARAMETRO");
   
   String others = "";
   String valueNome = "";
   
   if(!TextHelper.isNull(parametroRelatorioEditavel)){
       String[] splitParametro = parametroRelatorioEditavel.split(";");
       if(splitParametro.length > 1){
           nome = splitParametro[1];
           desabilitado = true;
       }
   }
   
   if (!TextHelper.isNull(nome)) {
       valueNome = nome;
       others = "disabled";
   }
   
   if (desabilitado) {
       others = "disabled";
   }      
%>
    <div class="form-group col-sm-12  col-md-6">
      <label id="lblNome" for="nome"><hl:message key="rotulo.servidor.nome"/></label>
      <hl:htmlinput name="nome" di="nome" value="<%=TextHelper.forHtmlAttribute(valueNome)%>" others="<%=TextHelper.forHtmlAttribute(others)%>" type="text" classe="form-control" size="40" mask="#*40" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelSerPage, ApplicationResourcesHelper.getMessage("rotulo.servidor.nome", responsavelSerPage))%>"/>
    </div>
    
    <script type="text/JavaScript">
    function valida_campo_nome() {
       return true;
    }
    </script>
