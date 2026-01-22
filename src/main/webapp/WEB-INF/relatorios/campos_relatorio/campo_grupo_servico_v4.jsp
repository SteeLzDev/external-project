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
String obrGpsPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO"); 
AcessoSistema responsavelGpsPage = JspHelper.getAcessoSistema(request);
List<TransferObject> gruposServico = (List<TransferObject>) request.getAttribute("listaGrupossServico");

String grupoServico = (String) JspHelper.verificaVarQryStr(request, "grupoServico");
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
       <div class="form-group col-sm-12  col-md-6">
         <label id="lblGrupoServicoGpsPage" for="grupoServico">${descricoes[recurso]}</label>
         <%if (TextHelper.isNull(grupoServico) && !desabilitado) { %>
            <%=JspHelper.geraCombo(gruposServico, "grupoServico", Columns.TGS_CODIGO + ";" + Columns.TGS_GRUPO, Columns.TGS_GRUPO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelGpsPage), null, false, 1, null, null, false, "form-control")%>
         <%} else if (!TextHelper.isNull(grupoServico)) { %>
            <%=JspHelper.geraCombo(gruposServico, "grupoServico", Columns.TGS_CODIGO + ";" + Columns.TGS_GRUPO, Columns.TGS_GRUPO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelGpsPage), null, false, 1, grupoServico, null, true, "form-control")%>
         <%} else if (desabilitado) {%>
            <%=JspHelper.geraCombo(gruposServico, "grupoServico", Columns.TGS_CODIGO + ";" + Columns.TGS_GRUPO, Columns.TGS_GRUPO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelGpsPage), null, false, 1, null, null, true, "form-control")%>
         <%} %>
       </div>

    <% if (obrGpsPage.equals("true")) { %>                    
      <script type="text/JavaScript">
      function funGpsPage() {
          camposObrigatorios = camposObrigatorios + 'grupoServico,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.grupo.servico"/>,';
      }
      addLoadEvent(funGpsPage);     
      </script>
    <% } %>                       

        <script type="text/JavaScript">
         function valida_campo_grupo_servico() {
             return true;
         }
        </script>        
