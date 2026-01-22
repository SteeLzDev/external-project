<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelCsaInadimplenciaPage = JspHelper.getAcessoSistema(request);

   String obrCsaPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

   List<TransferObject> csaListInadimplenciaPage = (List<TransferObject>) request.getAttribute("listaConsignatariasInadimplencia");

   if (responsavelCsaInadimplenciaPage.isCseSupOrg() && csaListInadimplenciaPage != null) {
%>
          <div class="form-group col-sm-12  col-md-6">
            <label id="lblConsignataria" for="csaCodigo">${descricoes[recurso]}</label>            
            <%if (TextHelper.isNull(csaCodigo) && !desabilitado) { %>
               <%=JspHelper.geraCombo(csaListInadimplenciaPage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCsaInadimplenciaPage), null, true, 1, csaCodigo, null, false, "form-control")%>
            <%} else if (!TextHelper.isNull(csaCodigo) && !desabilitado) { %>
               <%=JspHelper.geraCombo(csaListInadimplenciaPage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCsaInadimplenciaPage), null, false, 1, csaCodigo, null, false, "form-control")%>
            <%} else if (!TextHelper.isNull(csaCodigo)) { %>
               <%=JspHelper.geraCombo(csaListInadimplenciaPage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCsaInadimplenciaPage), null, false, 1, csaCodigo, null, true, "form-control")%>
            <%} else if (desabilitado) {%>
               <%=JspHelper.geraCombo(csaListInadimplenciaPage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCsaInadimplenciaPage), null, false, 1, csaCodigo, null, true, "form-control")%>
            <%} else {%>
               <%=JspHelper.geraCombo(csaListInadimplenciaPage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCsaInadimplenciaPage), null, true, 1, csaCodigo, null, true, "form-control")%>
            <%} %>
          </div>

    <% if (obrCsaPage.equals("true")) { %>
          <script type="text/JavaScript">
          function funCsaPage() {
              camposObrigatorios = camposObrigatorios + 'csaCodigo,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.consignataria"/>,';
          }
          addLoadEvent(funCsaPage);     
          </script>
    <% } %>             
                    
<% } else if (responsavelCsaInadimplenciaPage.isCsa()) { %>
          <div class="form-group col-sm-12  col-md-6">
            <label id="lblConsignataria" for="csaCodigo">${descricoes[recurso]}</label>
               <SELECT NAME="csaCodigo" id="csaCodigo" class="form-control" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%>>
                <OPTION VALUE=""><%=TextHelper.forHtmlContent(responsavelCsaInadimplenciaPage.getNomeEntidade())%></OPTION>
               </SELECT>
          </div>
<% } %>

<script type="text/JavaScript">
  function valida_campo_csa_inadimplencia() {
      return true;
  }
</script>
