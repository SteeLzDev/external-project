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
    String[] ncaCodigos = JspHelper.obterParametrosRequisicao(request, null, new String[] { "ncaCodigo" });
    String[] ncaDescricoes =  JspHelper.obterParametrosRequisicao(request, null, new String[] { "ncaDescricao" }) ;
    String ncaCodigo = "";
    String selectedValueSeparator = "&";
    if (ncaCodigos != null && ncaCodigos.length != 0 && ncaDescricoes != null && ncaDescricoes.length != 0 && ncaCodigos.length == ncaDescricoes.length) {
        for (int i = 0; i < ncaCodigos.length; i++) {
            ncaCodigo += ncaCodigos[i] + ";" + ncaDescricoes[i];
            if (i < ncaCodigos.length - 1) {
                ncaCodigo += selectedValueSeparator;
            }
        }
    }

    String obrNcaSelecPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    AcessoSistema responsavelNcaSelecPage = JspHelper.getAcessoSistema(request);

    List<TransferObject> naturezas = (List<TransferObject>) request.getAttribute("listaNaturezasConsignataria");

    String disabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(disabled) && disabled.equals("true"));
%>

<fieldset class="col-sm-12 col-md-12">
  <legend class="legend pt-2"><span>${descricoes[recurso]}</span></legend>
  <div class="row">
    <div class="col-sm-12 col-md-6">
      <%if (TextHelper.isNull(ncaCodigo) && !desabilitado) { %>
         <%=JspHelper.geraCombo(naturezas, "ncaCodigo", Columns.NCA_CODIGO + ";" + Columns.NCA_DESCRICAO, Columns.NCA_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelNcaSelecPage), "onChange=\"carregaDadosFiltro('ncaCodigo');\"", true, 3, ncaCodigo, null, false, "form-control", selectedValueSeparator)%>
      <%} else if (!TextHelper.isNull(ncaCodigo)) { %>
         <%=JspHelper.geraCombo(naturezas, "ncaCodigo", Columns.NCA_CODIGO + ";" + Columns.NCA_DESCRICAO, Columns.NCA_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelNcaSelecPage), "onChange=\"carregaDadosFiltro('ncaCodigo');\"", true, 3, ncaCodigo, null, true, "form-control", selectedValueSeparator)%>
      <%} else if (desabilitado) {%>
         <%=JspHelper.geraCombo(naturezas, "ncaCodigo", Columns.NCA_CODIGO + ";" + Columns.NCA_DESCRICAO, Columns.NCA_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelNcaSelecPage), "onChange=\"carregaDadosFiltro('ncaCodigo');\"", true, 3, null, null, true, "form-control", selectedValueSeparator)%>
      <%} %>
    </div>
  </div>
</fieldset>

<% if (obrNcaSelecPage.equals("true")) { %>          
    <script type="text/JavaScript">
    function funNcaSelecPage() {
        camposObrigatorios = camposObrigatorios + 'ncaCodigo,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.csa.natureza"/>,';
    }
    addLoadEvent(funNcaSelecPage);     
    </script>          
<% } %>

<script type="text/JavaScript">
 function valida_campo_natureza_csa() {
     return true;
 }
</script>        
