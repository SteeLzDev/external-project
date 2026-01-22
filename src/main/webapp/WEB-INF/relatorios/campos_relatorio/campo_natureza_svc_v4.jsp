<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="org.apache.commons.lang3.ArrayUtils" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
    String[] nseCodigos = JspHelper.obterParametrosRequisicao(request, null, new String[] { "nseCodigo" });
    String[] nseDescricoes = JspHelper.obterParametrosRequisicao(request, null, new String[] { "nseDescricao" });
    String nseCodigo = "";
    String selectedValueSeparator = "&";
    if (nseCodigos != null && nseCodigos.length != 0 && nseDescricoes != null && nseDescricoes.length != 0 && nseCodigos.length == nseDescricoes.length) {
        for (int i = 0; i < nseCodigos.length; i++) {
            nseCodigo += nseCodigos[i] + ";" + nseDescricoes[i];
            if (i < nseCodigos.length - 1) {
                nseCodigo += selectedValueSeparator;
            }
        }
    }

    String obrNseSelecPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    AcessoSistema responsavelNseSelecPage = JspHelper.getAcessoSistema(request);

    List<TransferObject> naturezas = (List<TransferObject>) request.getAttribute("listaNaturezasServico");

    String disabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(disabled) && disabled.equals("true"));
%>

<fieldset class="col-sm-12 col-md-12">
  <legend class="legend pt-2"><span>${descricoes[recurso]}</span></legend>
  <div class="row">
    <div class="col-sm-12 col-md-6">
      <%if (TextHelper.isNull(nseCodigo) && !desabilitado) { %>
         <%=JspHelper.geraCombo(naturezas, "nseCodigo", Columns.NSE_CODIGO + ";" + Columns.NSE_DESCRICAO, Columns.NSE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavelNseSelecPage), "onChange=\"carregaDadosFiltro('nseCodigo');\"", true, 3, nseCodigo, null, false, "form-control", selectedValueSeparator)%>
      <%} else if (!TextHelper.isNull(nseCodigo)) { %>
         <%=JspHelper.geraCombo(naturezas, "nseCodigo", Columns.NSE_CODIGO + ";" + Columns.NSE_DESCRICAO, Columns.NSE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavelNseSelecPage), "onChange=\"carregaDadosFiltro('nseCodigo');\"", true, 3, nseCodigo, null, true, "form-control", selectedValueSeparator)%>
      <%} else if (desabilitado) {%>
         <%=JspHelper.geraCombo(naturezas, "nseCodigo", Columns.NSE_CODIGO + ";" + Columns.NSE_DESCRICAO, Columns.NSE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavelNseSelecPage), "onChange=\"carregaDadosFiltro('nseCodigo');\"", true, 3, null, null, true, "form-control", selectedValueSeparator)%>
      <%} %>
    </div>
  </div>
</fieldset>

<% if (obrNseSelecPage.equals("true")) { %>
    <script type="text/JavaScript">
    function funNseSelecPage() {
        camposObrigatorios = camposObrigatorios + 'nseCodigo,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.svc.natureza"/>,';
    }
    addLoadEvent(funNseSelecPage);     
    </script>          
<% } %>

<script type="text/JavaScript">
 function valida_campo_natureza_svc() {
     return true;
 }
</script>
