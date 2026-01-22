<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%
AcessoSistema responsavelCorPage = JspHelper.getAcessoSistema(request);
String obrCorPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String corCodigo = (String) JspHelper.verificaVarQryStr(request, "corCodigo");
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

List<TransferObject> correspondentes = (List<TransferObject>) request.getAttribute("listaCorrespondentes");
if (correspondentes != null && !correspondentes.isEmpty()) {
    String fieldValue = Columns.COR_CODIGO + ";" + Columns.COR_IDENTIFICADOR + ";" + Columns.COR_NOME;
    String fieldLabel = Columns.COR_NOME;
    String rotuloTodos = ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCorPage);
%>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblCorrespondente" for="corCodigo">${descricoes[recurso]}</label>
                <%=JspHelper.geraCombo(correspondentes, "corCodigo", fieldValue, fieldLabel, rotuloTodos, null, (TextHelper.isNull(corCodigo) && !desabilitado), 1, corCodigo, null, desabilitado, "form-control form-select")%>
              </div>
          </tr>
      <%if (obrCorPage.equals("true")) {%>
       <script type="text/JavaScript">
       function funCorPage() {
          camposObrigatorios = camposObrigatorios + 'corCodigo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.orgao"/>,';
       }
       addLoadEvent(funCorPage);     
       </script>
      <%}%>             
          
<% } else if (responsavelCorPage.isCor()) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblCorrespondente" for="corCodigo">${descricoes[recurso]}</label>
                <select NAME="corCodigo" id="corCodigo" class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%>>
                  <option VALUE=""><%=TextHelper.forHtmlContent(responsavelCorPage.getNomeEntidade())%></option>
                </select>
              </div>
<% } %>
              <script type="text/JavaScript">
              function valida_campo_cor() {
                return true;
              }
              </script>
