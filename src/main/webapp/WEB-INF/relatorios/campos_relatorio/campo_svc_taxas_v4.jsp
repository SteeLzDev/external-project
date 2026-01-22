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
String obrSvcTaxasPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String svcCodigo = (String) JspHelper.verificaVarQryStr(request, "svcCodigo");
AcessoSistema responsavelSvcTaxasPage = JspHelper.getAcessoSistema(request);
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("listaServicos");

String disabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(disabled) && disabled.equals("true")) ? true:false;
%>
          <div class="form-group col-sm-12  col-md-6">
            <label id="lblServicoSvcTaxasPage" for="svcCodigo">${descricoes[recurso]}</label>
            <% if (responsavelSvcTaxasPage.isCseSupOrg()) { %>
                 <%if (TextHelper.isNull(svcCodigo) && !desabilitado) { %>
                    <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelSvcTaxasPage), null, true, 1, svcCodigo, null, false, "form-control")%>
                 <%} else if (!TextHelper.isNull(svcCodigo)) { %>
                    <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelSvcTaxasPage), null, true, 1, svcCodigo, null, true, "form-control")%>
                 <%} else if (desabilitado) {%>
                    <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelSvcTaxasPage), null, true, 1, null, null, true, "form-control")%>
                 <%} %>
            <% } else { %>
              <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelSvcTaxasPage), null, false, 1, svcCodigo, null, desabilitado, "form-control")%>
            <% } %>
          </div>

  <% if (obrSvcTaxasPage.equals("true")) { %>          
      <script type="text/JavaScript">
      function funSvcTaxasPage() {
          camposObrigatorios = camposObrigatorios + 'svcCodigo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.servico"/>,';
      }
      addLoadEvent(funSvcTaxasPage);     
      </script>          
  <% } %>

        <script type="text/JavaScript">
         function valida_campo_svc_taxas() {
             return true;
         }
        </script>        
