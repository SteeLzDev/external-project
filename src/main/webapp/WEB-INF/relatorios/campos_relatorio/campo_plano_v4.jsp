<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
AcessoSistema responsavelPlanoPage = JspHelper.getAcessoSistema(request);
String obrPlanoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String plaCodigo = JspHelper.verificaVarQryStr(request, "PLA_CODIGO");

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

List<TransferObject> planos = (List<TransferObject>) request.getAttribute("listaPlanosDesconto");
%>
   <div class="form-group col-sm-12 col-md-6">
     <label id="lblPlanoPlaPage" for="PLA_CODIGO">${descricoes[recurso]}</label>
     <%=JspHelper.geraCombo(planos, "PLA_CODIGO", Columns.PLA_CODIGO + ";" + Columns.PLA_DESCRICAO, Columns.PLA_IDENTIFICADOR + ";" + Columns.PLA_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelPlanoPage), null, false, 1, plaCodigo, null, desabilitado, "form-control")%>
   </div>

  <% if (obrPlanoPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funPlanoPage() {
          camposObrigatorios = camposObrigatorios + 'PLA_CODIGO,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.plano"/>,';
      }
      addLoadEvent(funPlanoPage);
      </script>
  <% } %>


<script type="text/JavaScript">
  function valida_campo_plano() {
     return true;
  }
</script>