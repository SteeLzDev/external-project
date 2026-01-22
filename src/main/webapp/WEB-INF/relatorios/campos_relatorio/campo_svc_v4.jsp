<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<% 
   String obrSvcPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String svcCodigo = (String) JspHelper.verificaVarQryStr(request, "svcCodigo");
   AcessoSistema responsavelSvcPage = JspHelper.getAcessoSistema(request);
   List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("listaServicos");
   
   String disabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(disabled) && disabled.equals("true")) ? true:false;
%>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblServicoSvcPage" for="svcCodigo">${descricoes[recurso]}</label>
                <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelSvcPage), null, false, 3, svcCodigo, null, desabilitado, "form-control")%>
              </div>
              <script type="text/JavaScript">
              <%if (obrSvcPage.equals("true")) {%>          
              function funSvcPage() {
                camposObrigatorios = camposObrigatorios + 'svcCodigo,';
                msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.servico"/>,';
              }
              addLoadEvent(funSvcPage);     
              <%}%>
              function valida_campo_svc() {
                return true;
              }
              </script>
