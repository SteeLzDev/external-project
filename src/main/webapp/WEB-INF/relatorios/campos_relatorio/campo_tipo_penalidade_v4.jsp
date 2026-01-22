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
   String obrTpePage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   AcessoSistema responsavelTpePage = JspHelper.getAcessoSistema(request);
   List<TransferObject> tiposPenalidade = (List<TransferObject>) request.getAttribute("listaTiposPenalidade");
   
   String tpeCodigo = (String) JspHelper.verificaVarQryStr(request, "TPE_CODIGO");
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblTipoPenalidadeTpePage" for="TPE_CODIGO">${descricoes[recurso]}</label>
            <%if (TextHelper.isNull(tpeCodigo) && !desabilitado) { %>
               <%=JspHelper.geraCombo(tiposPenalidade, "TPE_CODIGO", Columns.TPE_CODIGO + ";" + Columns.TPE_DESCRICAO, Columns.TPE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTpePage), null, false, 3, tpeCodigo, null, false, "form-control")%>
            <%} else if (!TextHelper.isNull(tpeCodigo)) { %>
               <%=JspHelper.geraCombo(tiposPenalidade, "TPE_CODIGO", Columns.TPE_CODIGO + ";" + Columns.TPE_DESCRICAO, Columns.TPE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTpePage), null, false, 3, tpeCodigo, null, true, "form-control")%>
            <%} else if (desabilitado) {%>
               <%=JspHelper.geraCombo(tiposPenalidade, "TPE_CODIGO", Columns.TPE_CODIGO + ";" + Columns.TPE_DESCRICAO, Columns.TPE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTpePage), null, false, 1, null, null, true, "form-control")%>
            <%} %>
          </div>

    <% if (obrTpePage.equals("true")) { %>
      <script type="text/JavaScript">
      function funTpePage() {
          camposObrigatorios = camposObrigatorios + 'TPE_CODIGO,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.penalidade"/>,';
      }
      addLoadEvent(funTpePage);     
      </script>
    <% } %>                                   

        <script type="text/JavaScript">
         function valida_campo_tipo_penalidade() {
             return true;
         }
        </script>        
