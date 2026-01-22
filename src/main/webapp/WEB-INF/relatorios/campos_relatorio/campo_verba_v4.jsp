<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
AcessoSistema responsavelVerbaPage = JspHelper.getAcessoSistema(request);
String obrVerbaPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("listaCodVerbaReajuste");
%>
   
  <div class="form-group col-sm-12 col-md-6">
    <label id="lblVerbaVerPage" for="CNV_COD_VERBA">${descricoes[recurso]}</label>
    <select name="CNV_COD_VERBA" id="CNV_COD_VERBA"
      class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);"
      onBlur="fout(this);ValidaMascaraV4(this);">
        <option value=""><hl:message key="rotulo.campo.todas"/></option>   
<%
if (servicos != null && !servicos.isEmpty()) {
  Iterator<TransferObject> it = servicos.iterator();
  while (it.hasNext()) {
    TransferObject servico = it.next();
    String cnvCodVerba = (String)servico.getAttribute(Columns.CNV_COD_VERBA);
    if (cnvCodVerba != null && !cnvCodVerba.equals("")) {
%>
        <option value="<%=TextHelper.forHtmlAttribute(cnvCodVerba)%>" <%=(JspHelper.verificaVarQryStr(request, "verba").equals(cnvCodVerba)) ? "selected" : ""%>><%=TextHelper.forHtmlContent(cnvCodVerba)%></option>
<%
    }
  }
}
%>
    </select>
  </div>

  <% if (obrVerbaPage.equals("true")) { %>
      <script type="text/JavaScript">
      function funVerbaPage() {
          camposObrigatorios = camposObrigatorios + 'CNV_COD_VERBA,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.codigo.verba"/>,';
      }
      addLoadEvent(funVerbaPage);
      </script>
  <% } %>

<script type="text/JavaScript">
  function valida_campo_verba() {
     return true;
  }
</script>
