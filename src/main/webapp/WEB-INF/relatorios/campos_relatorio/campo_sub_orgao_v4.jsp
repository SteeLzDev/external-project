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
AcessoSistema responsavelSubOrgao = JspHelper.getAcessoSistema(request);
String campoObrigatorioSubOrgao = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
List<TransferObject> listaSubOrgao = (List<TransferObject>) request.getAttribute("listaSubOrgao");
String sboCodigoSubOrgao = (String) JspHelper.verificaVarQryStr(request, "sboCodigo");
String paramDisabledSubOrgao = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitadoSubOrgao = (!TextHelper.isNull(paramDisabledSubOrgao) && paramDisabledSubOrgao.equals("true")) ? true:false;

String fieldValueSubOrgao = Columns.SBO_CODIGO + ";" + Columns.SBO_IDENTIFICADOR + ";" + Columns.SBO_DESCRICAO;
String fieldLabelSubOrgao = Columns.SBO_DESCRICAO;
String rotuloTodosSubOrgao = ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelSubOrgao);
%>
      <% if (listaSubOrgao != null && !listaSubOrgao.isEmpty()) { %>
        <div class="form-group col-sm-12 col-md-6">
          <label id="lblSubOrgao" for="sboCodigo">${descricoes[recurso]}</label>
                <%=JspHelper.geraCombo(listaSubOrgao, "sboCodigo", fieldValueSubOrgao, fieldLabelSubOrgao, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelSubOrgao), "onChange=\"carregaDadosFiltro();\"", false, 1, sboCodigoSubOrgao, null, desabilitadoSubOrgao, "form-control")%>
        </div>
      <% } else { %>
        <hl:htmlinput type="hidden" name="sboCodigo" di="sboCodigo" value="" />
      <% } %>
          
          <script type="text/JavaScript">
          function funSubOrgaoPage() {
          <%if (campoObrigatorioSubOrgao.equals("true")) {%>
                camposObrigatorios = camposObrigatorios + 'sboCodigo,';
                msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.suborgao"/>,';
          <% } %>
          }
          addLoadEvent(funSubOrgaoPage);     
          </script>
              <script type="text/JavaScript">
              function valida_campo_sub_orgao() {
                 return true;
              }
              </script>
