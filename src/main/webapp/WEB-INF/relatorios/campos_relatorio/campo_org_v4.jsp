<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<% 
AcessoSistema responsavelOrgPage = JspHelper.getAcessoSistema(request);
String obrOrgPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String orgCodigo = (String) JspHelper.verificaVarQryStr(request, "orgCodigo");
String strTipoOrgPage = JspHelper.verificaVarQryStr(request, "STRTIPO");

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

List<TransferObject> orgaosTO = (List<TransferObject>) request.getAttribute("listaOrgaos");
if (orgaosTO != null && !orgaosTO.isEmpty()) {
%>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblOrgaoOrgPage" for="orgCodigo">${descricoes[recurso]}</label>
                <%=JspHelper.geraCombo(orgaosTO, "orgCodigo", Columns.ORG_CODIGO + ";" + Columns.ORG_IDENTIFICADOR + ";" + Columns.ORG_NOME, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelOrgPage), null, false, 5, orgCodigo, null, desabilitado, "form-control")%>
              </div>
              <%if (obrOrgPage.equals("true")) {%>
              <script type="text/JavaScript">
              function funOrgPage() {
                  camposObrigatorios = camposObrigatorios + 'orgCodigo,';
                  msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.orgao"/>,';
              }
              addLoadEvent(funOrgPage);     
              </script>
              <%}%>                        
          
<% } else if (responsavelOrgPage.isOrg()) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblOrgaoOrgPage" for="orgCodigo">${descricoes[recurso]}</label>
                <select name="orgCodigo" id="orgCodigo" class="form-control" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%>>
                  <option value=""><%=TextHelper.forHtmlContent(responsavelOrgPage.getNomeEntidade())%></option>
                </select>
              </div>
<% } %>
              <script type="text/JavaScript">
              function valida_campo_org() {
                return true;
              }
              </script>        
