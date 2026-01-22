<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavelCpfPage = JspHelper.getAcessoSistema(request);
if (!ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavelCpfPage)) {
    
    String obrCpfPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
     
    String others = "";
    if (desabilitado) {
        others = "disabled";
    }
%>
     <div class="form-group col-sm-12  col-md-6">
         <label id="lblCPF" for="CPF">${descricoes[recurso]}</label>
          <hl:htmlinput name="CPF" 
                        di="CPF" 
                        type="text" 
                        classe="form-control"
                        mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMask())%>" 
                        size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfSize())%>"
                        maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCpfMaxLenght())%>"
                        others="<%=TextHelper.forHtmlAttribute(others)%>"
                        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CPF"))%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.digite.cpf", responsavelCpfPage)%>" 
          />
     </div>

    <% if (obrCpfPage.equals("true")) { %>
       <script type="text/JavaScript">
       function funCpfPage() {
          camposObrigatorios = camposObrigatorios + 'CPF,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.cpf"/>,';
       }
       addLoadEvent(funCpfPage);     
       </script>
    <% } %>             
<% } %>
        <script type="text/JavaScript">
         function valida_campo_cpf() {
             return true;
         }
        </script>        
