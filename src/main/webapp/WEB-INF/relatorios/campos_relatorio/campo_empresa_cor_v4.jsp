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
  AcessoSistema responsavelEcoPage = JspHelper.getAcessoSistema(request);
  String obrEcoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String ecoCodigo = (String) JspHelper.verificaVarQryStr(request, "ecoCodigo");
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

  List<TransferObject> empresas = (List<TransferObject>) request.getAttribute("listaEmpresasCorrespondente");
  if (empresas != null && !empresas.isEmpty()) {
%>
          <div class="form-group col-sm-12  col-md-6">
            <label id="lblEcoPage" for="ecoCodigo">${descricoes[recurso]}</label>
            <%=JspHelper.geraCombo(empresas, "ecoCodigo", Columns.ECO_CODIGO + ";" + Columns.ECO_IDENTIFICADOR + ";" + Columns.ECO_NOME, Columns.ECO_NOME + ";" + Columns.ECO_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEcoPage), null, false, 1, ecoCodigo, null, desabilitado, "form-control")%>
          </div>

    <% if (obrEcoPage.equals("true")) { %>
       <script type="text/JavaScript">
       function funEcoPage() {
          camposObrigatorios = camposObrigatorios + 'ecoCodigo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.empresa.correspondente"/>,';
       }
       addLoadEvent(funEcoPage);     
       </script>
    <% } %>                       
<% } %>

        <script type="text/JavaScript">
         function valida_campo_empresa_cor() {
             return true;
         }
        </script>        
