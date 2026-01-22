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
AcessoSistema responsavelPostoPage = JspHelper.getAcessoSistema(request);
String obrPostoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String posCodigo = JspHelper.verificaVarQryStr(request, "POS_CODIGO");

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

List<TransferObject> postos = (List<TransferObject>) request.getAttribute("listaPostos");  
%>
  <div class="form-group col-sm-12 col-md-6">
    <label id="lblPostoPage" for="TRS_CODIGO">${descricoes[recurso]}</label>    
    <%if (TextHelper.isNull(posCodigo) && !desabilitado) { %>
        <%=JspHelper.geraCombo(postos, "POS_CODIGO", Columns.POS_CODIGO + ';' + Columns.POS_DESCRICAO,  Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelPostoPage), null, false, 1, posCodigo, null, false, "form-control")%>
    <%} else if (!TextHelper.isNull(posCodigo)) { %>
        <%=JspHelper.geraCombo(postos, "POS_CODIGO", Columns.POS_CODIGO + ';' + Columns.POS_DESCRICAO,  Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelPostoPage), null, false, 1, posCodigo, null, true, "form-control")%>
    <%} else if (desabilitado) {%>
        <%=JspHelper.geraCombo(postos, "POS_CODIGO", Columns.POS_CODIGO + ';' + Columns.POS_DESCRICAO,  Columns.POS_IDENTIFICADOR + ";" + Columns.POS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelPostoPage), null, false, 1, null, null, true, "form-control")%>
    <%} %>
  </div>

  <% if (obrPostoPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funPostoPage() {
          camposObrigatorios = camposObrigatorios + 'POS_CODIGO,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.posto"/>,';
      }
      addLoadEvent(funPostoPage);
      </script>
  <% } %>


<script type="text/JavaScript">
  function valida_campo_posto() {
     return true;
  }
</script>