<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<% 
   AcessoSistema responsavelCsaPage = JspHelper.getAcessoSistema(request);
   String obrCsaPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   List<TransferObject> csaListCsaPage = (List<TransferObject>) request.getAttribute("listaConsignatarias");
   String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   String strTipoCsaPage = JspHelper.verificaVarQryStr(request, "STRTIPO");

   String fieldValue = Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME;
   String fieldLabel = Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR;
   String rotuloTodos = ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelCsaPage);
   if ((responsavelCsaPage.isCseSupOrg() || responsavelCsaPage.isSer()) && csaListCsaPage != null) {
%>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblConsignataria" for="csaCodigo">${descricoes[recurso]}</label>
                <%=JspHelper.geraCombo(csaListCsaPage, "csaCodigo", fieldValue, fieldLabel, rotuloTodos, "onChange=\"carregaDadosFiltro();\"", (TextHelper.isNull(csaCodigo) && !desabilitado), 1, csaCodigo, null, desabilitado, "form-control")%>
              </div>
              <%if (obrCsaPage.equals("true")) {%>
              <script type="text/JavaScript">
              function funCsaPage() {
                camposObrigatorios = camposObrigatorios + 'csaCodigo,';
                msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.consignataria"/>,';
              }
              addLoadEvent(funCsaPage);     
              </script>
              <%}%> 

<% } else if (responsavelCsaPage.isCsa()) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblConsignataria" for="csaCodigo">${descricoes[recurso]}</label>
                <select name="csaCodigo" id="csaCodigo" class="Select form-control" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%>>
                  <option value=""><%=TextHelper.forHtmlContent(responsavelCsaPage.getNomeEntidade())%></option>
               </select>
              </div>
<% } %>
              <script type="text/JavaScript">
              function valida_campo_csa() {
                 return true;
              }
              </script>
