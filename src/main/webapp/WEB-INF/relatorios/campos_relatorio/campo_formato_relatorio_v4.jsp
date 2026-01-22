<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%
   AcessoSistema responsavelFmtPage = JspHelper.getAcessoSistema(request);
   String obrFmtPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String formato = (String) JspHelper.verificaVarQryStr(request, "formato");

   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
      <div class="form-group col-sm-12 col-md-6">
        <label id="lblFormatoFmtPage" for="formato">${descricoes[recurso]}</label>
        <select name="formato" id="formato" class="Select form-select form-control" nf="btnEnvia" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <% if (!TextHelper.isNull(formato) || desabilitado) { %>disabled <%} %>>
          <option value="" <% if (TextHelper.isNull(formato)) { %>SELECTED <%} %>><hl:message key="rotulo.campo.selecione"/></option>
          <option value="PDF" <% if (!TextHelper.isNull(formato) && formato.equals("PDF")) { %>SELECTED  <%} %>>PDF</option>
          <option value="TEXT" <% if (!TextHelper.isNull(formato) && formato.equals("TEXT")) { %>SELECTED <%} %>>TXT</option>
          <option value="CSV" <% if (!TextHelper.isNull(formato) && formato.equals("CSV")) { %>SELECTED <%} %>>CSV</option>
          <option value="DOC" <% if (!TextHelper.isNull(formato) && formato.equals("DOC")) { %>SELECTED <%} %>>DOC</option>
          <option value="XLS" <% if (!TextHelper.isNull(formato) && formato.equals("XLS")) { %>SELECTED <%} %>>XLS</option>
          <option value="XLSX" <% if (!TextHelper.isNull(formato) && formato.equals("XLSX")) { %>SELECTED <%} %>>XLSX</option>
          <option value="XML" <% if (!TextHelper.isNull(formato) && formato.equals("XML")) { %>SELECTED <%} %>>XML</option>
          <option value="ODT" <% if (!TextHelper.isNull(formato) && formato.equals("ODT")) { %>SELECTED <%} %>>ODT</option>
          <option value="ODS" <% if (!TextHelper.isNull(formato) && formato.equals("ODS")) { %>SELECTED <%} %>>ODS</option>
          <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRE_VISUALIZAR_RELATORIOS, CodedValues.TPC_SIM, responsavelFmtPage)) { %>
          	<option value="HTML" <% if (!TextHelper.isNull(formato) && formato.equals("HTML")) { %>SELECTED <%} %>><hl:message key="rotulo.campo.formato.relatorio.pre.visualizar"/></option>
          <% } %>
        </select>
      </div>

      <script type="text/JavaScript">
      <%if (obrFmtPage.equals("true")) {%>
      function funFmtPage() {
        camposObrigatorios = camposObrigatorios + 'formato,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.formato"/>,';
      }
      addLoadEvent(funFmtPage);
      <%}%>
      function valida_campo_formato_relatorio() {
        return true;
      }
      </script>
