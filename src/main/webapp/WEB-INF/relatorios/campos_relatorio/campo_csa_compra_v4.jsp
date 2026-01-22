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
   AcessoSistema responsavelCsaCompraPage = JspHelper.getAcessoSistema(request);
   String obrCsaCompraPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   List<TransferObject> csaListCsaCompraPage = (List<TransferObject>) request.getAttribute("listaConsignatarias");
   String csaCodigo = (String) JspHelper.verificaVarQryStr(request, "csaCodigo");
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   
   if (responsavelCsaCompraPage.isCseSupOrg() && csaListCsaCompraPage != null) {
%>
          <div class="form-group col-sm-12  col-md-6">
            <label id="lblConsignatariaCompra" for="csaCodigo">${descricoes[recurso]}</label>            
            <%if (TextHelper.isNull(csaCodigo) && !desabilitado) { %>
               <%=JspHelper.geraCombo(csaListCsaCompraPage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, 
                       ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCsaCompraPage), "onChange=\"habilitaCamposCompraContrato()\"", false, 1, null, null, desabilitado, "form-control")%>
            <%} else if (!TextHelper.isNull(csaCodigo)) { %>
               <%=JspHelper.geraCombo(csaListCsaCompraPage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, 
                       ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCsaCompraPage), "onChange=\"habilitaCamposCompraContrato()\"", false, 1, csaCodigo, null, true, "form-control")%>
            <%} else if (desabilitado) {%>
               <%=JspHelper.geraCombo(csaListCsaCompraPage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, 
                       ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCsaCompraPage), "onChange=\"habilitaCamposCompraContrato()\"", false, 1, null, null, true, "form-control")%>
            <%} %>                                                  
          </div>

    <% if (obrCsaCompraPage.equals("true")) { %>
          <script type="text/JavaScript">
          function funCsaCompraPage() {
              camposObrigatorios = camposObrigatorios + 'csaCodigo,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.consignataria"/>,';
          }
          addLoadEvent(funCsaCompraPage);     
          </script>
    <% } %>             
<% } %>

        <script type="text/JavaScript">
         function valida_campo_csa_compra() {
             return true;
         }
        </script>        
