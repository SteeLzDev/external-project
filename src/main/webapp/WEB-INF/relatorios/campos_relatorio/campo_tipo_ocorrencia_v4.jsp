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
AcessoSistema responsavelTocPage = JspHelper.getAcessoSistema(request);
String obrTocPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
List<TransferObject> tipoOcorrencia = (List<TransferObject>) request.getAttribute("listaTiposOcorrencia");

String tocCodigo = (String) JspHelper.verificaVarQryStr(request, "tocCodigo");
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
        <div class="form-group col-sm-12 col-md-6">
          <label id="lblTipoOcorrenciaTocPage" for="tocCodigo">${descricoes[recurso]}</label>
          <%if (TextHelper.isNull(tocCodigo) && !desabilitado) { %>
             <%=JspHelper.geraCombo(tipoOcorrencia, "tocCodigo", Columns.TOC_CODIGO + ";" + Columns.TOC_DESCRICAO, Columns.TOC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTocPage), null, false, 3, tocCodigo, null, false, "form-control")%>
          <%} else if (!TextHelper.isNull(tocCodigo)) { %>
             <%=JspHelper.geraCombo(tipoOcorrencia, "tocCodigo", Columns.TOC_CODIGO + ";" + Columns.TOC_DESCRICAO, Columns.TOC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTocPage), null, false, 3, tocCodigo, null, true, "form-control")%>
          <%} else if (desabilitado) {%>
             <%=JspHelper.geraCombo(tipoOcorrencia, "tocCodigo", Columns.TOC_CODIGO + ";" + Columns.TOC_DESCRICAO, Columns.TOC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTocPage), null, false, 3, null, null, true, "form-control")%>
          <%} %>
        </div>

    <% if (obrTocPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funTocPage() {
          camposObrigatorios = camposObrigatorios + 'tocCodigo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.ocorrencia"/>,';
      }
      addLoadEvent(funTocPage);     
      </script>
    <% } %>                 
    
        <script type="text/JavaScript">
         function valida_campo_tipo_ocorrencia() {
             return true;
         }
        </script>        
        
