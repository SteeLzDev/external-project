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
String obrSvcSelecPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
AcessoSistema responsavelSvcSelecPage = JspHelper.getAcessoSistema(request);
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("listaServicos");

String svcCodigo = (String) JspHelper.verificaVarQryStr(request, "svcCodigo");

String disabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(disabled) && disabled.equals("true")) ? true:false;
%>
          <div class="form-group col-sm-12  col-md-6">
            <label id="lblServicoSvcPage" for="svcCodigo">${descricoes[recurso]}</label>
            <%if (TextHelper.isNull(svcCodigo) && !desabilitado) { %>
               <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelSvcSelecPage), null, true, 3, svcCodigo, null, false, "form-control")%>
            <%} else if (!TextHelper.isNull(svcCodigo)) { %>
               <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelSvcSelecPage), null, true, 3, svcCodigo, null, true, "form-control")%>
            <%} else if (desabilitado) {%>
               <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelSvcSelecPage), null, true, 3, null, null, true, "form-control")%> 
            <%} %>
          </div>

  <% if (obrSvcSelecPage.equals("true")) { %>          
      <script type="text/JavaScript">
      function funSvcSelecPage() {
          camposObrigatorios = camposObrigatorios + 'svcCodigo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.servico"/>,';
      }
      addLoadEvent(funSvcSelecPage);     
      </script>          
  <% } %>

        <script type="text/JavaScript">
         function valida_campo_svc_selec() {
             return true;
         }
        </script>        
