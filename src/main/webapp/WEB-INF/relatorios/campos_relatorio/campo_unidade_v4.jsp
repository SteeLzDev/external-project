<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<% 
AcessoSistema responsavelUniPage = JspHelper.getAcessoSistema(request);
String campoObrigatorioUniPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
List<TransferObject> listaUniPage = (List<TransferObject>) request.getAttribute("listaUnidade");
String uniCodigoUniPage = (String) JspHelper.verificaVarQryStr(request, "uniCodigo");
String paramDisabledUniPage = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitadoUniPage = (!TextHelper.isNull(paramDisabledUniPage) && paramDisabledUniPage.equals("true")) ? true:false;

String fieldValueUniPage = Columns.UNI_CODIGO + ";" + Columns.UNI_IDENTIFICADOR + ";" + Columns.UNI_DESCRICAO;
String fieldLabelUniPage = Columns.UNI_DESCRICAO;
String rotuloTodosUniPage = ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelUniPage);
%>
      <% if (listaUniPage != null && !listaUniPage.isEmpty()) { %>
        <div class="form-group col-sm-12 col-md-6">
          <label id="lblUniCodigoUniPage" for="uniCodigo">${descricoes[recurso]}</label>
                <%=JspHelper.geraCombo(listaUniPage, "uniCodigo", fieldValueUniPage, fieldLabelUniPage, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelUniPage), null, false, 1, uniCodigoUniPage, null, desabilitadoUniPage, "form-control")%>
        </div>
      <% } else { %>
        <hl:htmlinput type="hidden" name="uniCodigo" di="uniCodigo" value="" />
      <% } %>         
          <script type="text/JavaScript">
          function funUniPagePage() {
          <%if (campoObrigatorioUniPage.equals("true")) {%>
                camposObrigatorios = camposObrigatorios + 'uniCodigo,';
                msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.unidade"/>,';
          <% } %>
          }
          addLoadEvent(funUniPagePage);     
          </script>
              <script type="text/JavaScript">
              function valida_campo_unidade() {
                 return true;
              }
              </script>
