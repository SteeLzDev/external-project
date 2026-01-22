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
  AcessoSistema responsavelEnderecoPage = JspHelper.getAcessoSistema(request);
  String obrEnderecoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String echCodigo = JspHelper.verificaVarQryStr(request, "ECH_CODIGO");
  
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

  List<TransferObject> enderecos = (List<TransferObject>) request.getAttribute("listaEnderecos");
%>
  <div class="form-group col-sm-12  col-md-6">
    <label id="lblEnderecoEndPage" for="ECH_CODIGO">${descricoes[recurso]}</label>
    <%=JspHelper.geraCombo(enderecos, "ECH_CODIGO", Columns.ECH_CODIGO + ";" + Columns.ECH_DESCRICAO, Columns.ECH_IDENTIFICADOR + ";" + Columns.ECH_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEnderecoPage), null, false, 1, echCodigo, null, desabilitado, "form-control")%>
  </div>

  <% if (obrEnderecoPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funEnderecoPage() {
          camposObrigatorios = camposObrigatorios + 'ECH_CODIGO,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.endereco"/>,';
      }
      addLoadEvent(funEnderecoPage);
      </script>
  <% } %>


<script type="text/JavaScript">
  function valida_campo_endereco() {
     return true;
  }
</script>