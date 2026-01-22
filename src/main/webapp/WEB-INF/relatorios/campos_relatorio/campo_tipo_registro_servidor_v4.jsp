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
AcessoSistema responsavelTipoRegServidorPage = JspHelper.getAcessoSistema(request);
String obrTipoRegServidorPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String trsCodigo = JspHelper.verificaVarQryStr(request, "TRS_CODIGO");

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

List<TransferObject> tipos = (List<TransferObject>) request.getAttribute("listaTipoRegistroServidor");  
%>
  <div class="form-group col-sm-12 col-md-6">
    <label id="lblTipoRegServidorPage" for="TRS_CODIGO">${descricoes[recurso]}</label>
    <%if (TextHelper.isNull(trsCodigo) && !desabilitado) { %>
        <%=JspHelper.geraCombo(tipos, "TRS_CODIGO", Columns.TRS_CODIGO + ';' + Columns.TRS_DESCRICAO,  Columns.TRS_CODIGO + ";" + Columns.TRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTipoRegServidorPage), null, false, 1, trsCodigo, null, false, "form-control")%>
    <%} else if (!TextHelper.isNull(trsCodigo)) { %>
        <%=JspHelper.geraCombo(tipos, "TRS_CODIGO", Columns.TRS_CODIGO + ';' + Columns.TRS_DESCRICAO,  Columns.TRS_CODIGO + ";" + Columns.TRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTipoRegServidorPage), null, false, 1, trsCodigo, null, true, "form-control")%>
    <%} else if (desabilitado) {%>
        <%=JspHelper.geraCombo(tipos, "TRS_CODIGO", Columns.TRS_CODIGO + ';' + Columns.TRS_DESCRICAO,  Columns.TRS_CODIGO + ";" + Columns.TRS_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTipoRegServidorPage), null, false, 1, null, null, true, "form-control")%>
    <%} %>
  </div>

  <% if (obrTipoRegServidorPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funTipoRegServidorPage() {
          camposObrigatorios = camposObrigatorios + 'TRS_CODIGO,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.registro.servidor"/>,';
      }
      addLoadEvent(funTipoRegServidorPage);
      </script>
  <% } %>


<script type="text/JavaScript">
  function valida_campo_tipo_registro_servidor() {
     return true;
  }
</script>