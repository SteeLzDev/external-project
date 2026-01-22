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
String obrAuditPage =  JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String tipoOperador = (String) JspHelper.verificaVarQryStr(request, "tipoOperador");
String operador = (String) JspHelper.verificaVarQryStr(request, "operador");
String tipoEntidadeParam = (String) JspHelper.verificaVarQryStr(request, "tipoEntidade");
String entidade = (String) JspHelper.verificaVarQryStr(request, "entidade");
String tipoLog = (String) JspHelper.verificaVarQryStr(request, "tipoLog");
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
AcessoSistema responsavelAuditPage = JspHelper.getAcessoSistema(request);
List<TransferObject> tipoLogAuditList = (List<TransferObject>) request.getAttribute("tipoLogAuditList");
List<TransferObject> tipoEntAuditList = (List<TransferObject>) request.getAttribute("tipoEntAuditList");
%>
 
    <script type="text/JavaScript">
     <%
      out.println("var titulosTipoEntidade = new Array (" + (tipoEntAuditList.size() + 1) + ");");
      Iterator itTipoEntidade = tipoEntAuditList.iterator();
      out.println("titulosTipoEntidade[0] = '';");
      int i = 1;
      while (itTipoEntidade.hasNext()) {
        TransferObject tipoEntidade = (TransferObject) itTipoEntidade.next();
        out.println("titulosTipoEntidade[" + i + "] = '" + tipoEntidade.getAttribute(Columns.TEN_TITULO) + "';");
        i++;
      }    
     %>
      function trocaTituloTipoEntidade() {
        tituloTipoEntidade.innerHTML = titulosTipoEntidade[document.forms[0].tipoEntidade.selectedIndex];
      }
    </script>

            <div class="form-group col-sm-3">
              <label id="lblOperador" for="tipoOperador"><hl:message key="rotulo.relatorio.operador"/></label>
              <select name="tipoOperador" id="tipoOperador" class="form-control" onFocus="SetarEventoMascaraV4(this,'#*200',true);"  onBlur="fout(this);ValidaMascaraV4(this);" nf="Filtrar" <% if (!TextHelper.isNull(tipoOperador) || desabilitado) { %>disabled <%} %>>
                <option value="" <% if (TextHelper.isNull(tipoOperador)) { %>SELECTED  <%} %>><hl:message key="rotulo.campo.todos"/></option>
                <%if (responsavelAuditPage.isCseSup()) { %>
                <option value="CSE" <% if (!TextHelper.isNull(tipoOperador) && tipoOperador.equals("CSE")) { %>SELECTED  <%} %>><hl:message key="rotulo.consignante.singular"/></option>
                <%} %>
                <option value="CSA" <% if (!TextHelper.isNull(tipoOperador) && tipoOperador.equals("CSA")) { %>SELECTED  <%} %>><hl:message key="rotulo.consignataria.singular"/></option>
                <option value="COR" <% if (!TextHelper.isNull(tipoOperador) && tipoOperador.equals("COR")) { %>SELECTED  <%} %>><hl:message key="rotulo.correspondente.singular"/></option>
                <%if (responsavelAuditPage.isCseSup()) { %>
                <option value="SER" <% if (!TextHelper.isNull(tipoOperador) && tipoOperador.equals("SER")) { %>SELECTED  <%} %>><hl:message key="rotulo.servidor.singular"/></option>
                <option value="USU" <% if (!TextHelper.isNull(tipoOperador) && tipoOperador.equals("USU")) { %>SELECTED  <%} %>><hl:message key="rotulo.usuario.singular"/></option>
                <%} %>
              </select>
            </div>
            <div class="form-group col-sm-3">
              <label for="operador"><hl:message key="rotulo.relatorio.operador.nome"/></label>
              <input type="text" name="operador" id="operador" class="form-control" size="20" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <% if (!TextHelper.isNull(operador)) { %> disabled value="<%=TextHelper.forHtmlAttribute(operador )%>"<%} else if (desabilitado) {%> disabled <%} %> placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelAuditPage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.operador.nome", responsavelAuditPage))%>">
            </div>

            <div class="form-group col-sm-3">
              <label id="lblEntidade" for="tipoEntidade"><hl:message key="rotulo.relatorio.entidade.operada"/></label>
              <%if (TextHelper.isNull(tipoEntidadeParam) && !desabilitado) { %>
                 <%=JspHelper.geraCombo(tipoEntAuditList, "tipoEntidade", Columns.TEN_CODIGO, Columns.TEN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavelAuditPage), "onChange=\"trocaTituloTipoEntidade();\"", false, 1, tipoEntidadeParam, null, false, "form-control")%>
              <%} else if (!TextHelper.isNull(tipoEntidadeParam)) { %>   
                 <%=JspHelper.geraCombo(tipoEntAuditList, "tipoEntidade", Columns.TEN_CODIGO, Columns.TEN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavelAuditPage), "onChange=\"trocaTituloTipoEntidade();\"", false, 1, tipoEntidadeParam, null, true, "form-control")%>
              <%} else if (desabilitado) {%>
                 <%=JspHelper.geraCombo(tipoEntAuditList, "tipoEntidade", Columns.TEN_CODIGO, Columns.TEN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavelAuditPage), "onChange=\"trocaTituloTipoEntidade();\"", false, 1, null, null, true, "form-control")%>
              <%} %>
            </div>
            <div class="form-group col-sm-3">   
              <label id="tituloTipoEntidade" for="entidade"><hl:message key="rotulo.campo.entidade" /></label>
              <input class="form-control" type="text" name="entidade" id="entidade" size="20" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <% if (!TextHelper.isNull(entidade)) { %> disabled value="<%=TextHelper.forHtmlAttribute(entidade )%>"<%} else if (desabilitado) {%> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelAuditPage, ApplicationResourcesHelper.getMessage("rotulo.campo.entidade", responsavelAuditPage))%>">
            </div>

            <div class="form-group col-sm-12 col-md-6">
              <label id="lblTipoLog" for="tipoLog"><hl:message key="rotulo.relatorio.tipo.log"/></label>
              <%if (TextHelper.isNull(tipoLog) && !desabilitado) { %>
                <%=JspHelper.geraCombo(tipoLogAuditList, "tipoLog", Columns.TLO_CODIGO, Columns.TLO_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelAuditPage), null, false, 1, tipoLog, null, false, "form-control")%>
              <%} else if (!TextHelper.isNull(tipoLog)) { %>   
                 <%=JspHelper.geraCombo(tipoLogAuditList, "tipoLog", Columns.TLO_CODIGO, Columns.TLO_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelAuditPage), null, false, 1, tipoLog, null, true, "form-control")%>
              <%} else if (desabilitado) {%>
                 <%=JspHelper.geraCombo(tipoLogAuditList, "tipoLog", Columns.TLO_CODIGO, Columns.TLO_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelAuditPage), null, false, 1, null, null, true, "form-control")%>
              <%} %>     
            </div>
          
  <% if (obrAuditPage.equals("true")) { %>
     <script type="text/JavaScript">
     function funAuditPage() {
         camposObrigatorios = camposObrigatorios + 'tipoOperador,';
         camposObrigatorios = camposObrigatorios + 'operador,';
         camposObrigatorios = camposObrigatorios + 'tipoEntidade,';
         camposObrigatorios = camposObrigatorios + 'entidade,';
         camposObrigatorios = camposObrigatorios + 'tipoLog,';
         msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.operador"/>,';
         msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.operador"/>,';
         msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.entidade.operada"/>,';
         msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.entidade.operada"/>,';
         msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.log"/>,';
     }
     addLoadEvent(funAuditPage);     
     </script>
  <% } %>     
  
        <script type="text/JavaScript">
         function valida_campo_auditoria() {
             return true;
         }
        </script>        
       
