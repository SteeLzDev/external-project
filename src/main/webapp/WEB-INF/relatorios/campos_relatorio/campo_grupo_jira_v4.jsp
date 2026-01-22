<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String obrGrupoJira = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String descGrupoJira = JspHelper.verificaVarQryStr(request, "DESCRICAO");
String grupoJira = (String) JspHelper.verificaVarQryStr(request, "grupoJira");
   
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
      <div class="form-group col-sm-3 col-md-3">
        <label id="lblGrupoJira" for="grupoJira">${descricoes[recurso]}</label>
        <select name="grupoJira" id="grupoJira" class="form-control form-select" nf="btnEnvia" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <% if (!TextHelper.isNull(grupoJira) || desabilitado) { %>disabled <%} %>>
          <option value="" <% if (TextHelper.isNull(grupoJira)) { %>SELECTED  <%} %>><hl:message key="rotulo.campo.todos"/></option> 
          <option value="cse" <% if (!TextHelper.isNull(grupoJira) && grupoJira.equals("cse")) { %>SELECTED <%} %>><%=TextHelper.forHtml(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel))%></option>                
          <option value="csa" <% if (!TextHelper.isNull(grupoJira) && grupoJira.equals("csa")) { %>SELECTED <%} %>><%=TextHelper.forHtml(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel))%></option>                
          <option value="org" <% if (!TextHelper.isNull(grupoJira) && grupoJira.equals("org")) { %>SELECTED <%} %>><%=TextHelper.forHtml(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel))%></option>                
          <option value="ser" <% if (!TextHelper.isNull(grupoJira) && grupoJira.equals("ser")) { %>SELECTED <%} %>><%=TextHelper.forHtml(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel))%></option>                            
        </select>
      </div>
<% if (obrGrupoJira.equals("true")) { %>                    
  <script language="JavaScript" type="text/JavaScript">
  function funFmtPage() {
      camposObrigatorios = camposObrigatorios + 'grupoJira,';
      msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.grupo.jira"/>,';
  }
  addLoadEvent(funFmtPage);     
  </script>
<% } %>                       

    <script language="JavaScript" type="text/JavaScript">
     function valida_campo_grupo_jira() {
         return true;
     }
    </script>        
