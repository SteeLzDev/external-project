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
   AcessoSistema responsavelCsaSelecPage = JspHelper.getAcessoSistema(request);
   String obrCsaSelecPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   List<TransferObject> csaListCsaSelecPage = (List<TransferObject>) request.getAttribute("listaConsignatarias");
   
   String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
          <div class="form-group col-sm-12  col-md-6">
            <label id="lblConsignataria" for="csaCodigo">${descricoes[recurso]}</label>            
            <%=JspHelper.geraCombo(csaListCsaSelecPage, "csaCodigo", Columns.CSA_CODIGO, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelCsaSelecPage), null, false, 1, csaCodigo, null, desabilitado, "form-control")%>
          </div>

    <% if (obrCsaSelecPage.equals("true")) { %>
          <script type="text/JavaScript">
          function funCsaSelecPage() {
              camposObrigatorios = camposObrigatorios + 'csaCodigo,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.consignataria"/>,';
          }
          addLoadEvent(funCsaSelecPage);     
          </script>
    <% } %>                       

        <script type="text/JavaScript">
         function valida_campo_csa_selec() {
             return true;
         }
        </script>        
