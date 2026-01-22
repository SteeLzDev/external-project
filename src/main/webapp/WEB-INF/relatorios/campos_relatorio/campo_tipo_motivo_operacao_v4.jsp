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
AcessoSistema responsavelTmoPage = JspHelper.getAcessoSistema(request);
String obrTmoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
List<TransferObject> tipoMotivoOperacao = (List<TransferObject>) request.getAttribute("listaTiposMotivoOperacao");

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
String tmoCodigo = JspHelper.verificaVarQryStr(request, "tmoCodigo");
%>
        <div class="form-group col-sm-12 col-md-6">
          <label id="lblTipoMotivoTmoPage" for="tmoCodigo">${descricoes[recurso]}</label>
          <%=JspHelper.geraCombo(tipoMotivoOperacao, "tmoCodigo", Columns.TMO_CODIGO + ";" + Columns.TMO_DESCRICAO, Columns.TMO_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTmoPage), null, false, 3, tmoCodigo, null, desabilitado, "form-control")%>
        </div>

    <% if (obrTmoPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funTmoPage() {
          camposObrigatorios = camposObrigatorios + 'tmoCodigo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.motivo.operacao"/>,';
      }
      addLoadEvent(funTmoPage);     
      </script>
    <% } %>                 
    
        <script type="text/JavaScript">
         function valida_campo_tipo_motivo_operacao() {
             return true;
         }
        </script>        
