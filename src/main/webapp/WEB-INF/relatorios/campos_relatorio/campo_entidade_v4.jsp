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
   AcessoSistema responsavelEntidadePage = JspHelper.getAcessoSistema(request);
   String obrEntidadePage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
   String cseCodigo = JspHelper.verificaVarQryStr(request, "cseCodigo");
   String corCodigo = JspHelper.verificaVarQryStr(request, "corCodigo");
   String orgCodigo = JspHelper.verificaVarQryStr(request, "orgCodigo");
   String includeSuporte = JspHelper.verificaVarQryStr(request, "includeSuporte");
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

   List<TransferObject> cseListEntidadePage = (List<TransferObject>) request.getAttribute("listaConsignantes");
   List<TransferObject> orgListEntidadePage = (List<TransferObject>) request.getAttribute("listaOrgaos");
   List<TransferObject> csaListEntidadePage = (List<TransferObject>) request.getAttribute("listaConsignatarias");
   List<TransferObject> corListEntidadePage = (List<TransferObject>) request.getAttribute("listaCorrespondentes");

   if (responsavelEntidadePage.isCseSup() && csaListEntidadePage != null) {
%>
          <div class="col-sm-12">
            <div class="row">
              <div class="form-group col-sm-6">
                <label id="lblConsignanteEntidadePage" for="cseCodigo"><hl:message key="rotulo.consignante.singular"/></label>
                <SELECT NAME="cseCodigo" id="cseCodigo" class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (!TextHelper.isNull(cseCodigo) || desabilitado) { %> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
                 <OPTION VALUE="NENHUM" <%if (TextHelper.isNull(cseCodigo)) {%> SELECTED <%} %>><hl:message key="rotulo.campo.nenhum"/></OPTION>
                 <%
                   Iterator iteCse = cseListEntidadePage.iterator();
                   while (iteCse.hasNext()) {
                       CustomTransferObject ctoCse = (CustomTransferObject)iteCse.next();
                       String fieldValueCse = ctoCse.getAttribute(Columns.CSE_CODIGO) + ";" + ctoCse.getAttribute(Columns.CSE_IDENTIFICADOR) + ";" + ctoCse.getAttribute(Columns.CSE_NOME);
                       String fieldLabelCse = ctoCse.getAttribute(Columns.CSE_NOME) + " - " + ctoCse.getAttribute(Columns.CSE_IDENTIFICADOR);
                 %>
                 <OPTION VALUE="<%=TextHelper.forHtmlAttribute(fieldValueCse)%>" <%if ((!TextHelper.isNull(cseCodigo) && cseCodigo.equals(fieldValueCse))) { %> disabled selected <%} else if (desabilitado) {%> disabled <%} %>><%=TextHelper.forHtmlContent(fieldLabelCse)%></OPTION>
                 <%    
                   }
                 %>
                </SELECT>
              </div>
              <div class="col-sm-6">
                <div class="d-flex align-items-center" style="height: 100px">
                  <span class="text-nowrap align-text-top">
                    <div class="form-check">
                       <INPUT class="form-check-input ml-1" TYPE="checkbox" VALUE="true" TITLE='<hl:message key="mensagem.incluir.usuario.suporte"/>' <%=(String)(includeSuporte.equals("true") ? "checked" : "")%> NAME="includeSuporte" ID="includeSuporte" <%=(String)(desabilitado ? "disabled" : "")%>/>
                       <label class="form-check-label labelSemNegrito ml-1" for="includeSuporte"><hl:message key="mensagem.incluir.usuario.suporte"/></label>
                    </div>
                  </span>
                </div>
              </div>
            </div>
          </div>
<% } %>

<% if (csaListEntidadePage != null && responsavelEntidadePage.isCseSup()) { %>

          <div class="form-group col-sm-12 col-md-6">
            <label id="lblConsignatariaEntidadePage" for="csaCodigo"><hl:message key="rotulo.consignataria.singular"/></label>
            <%if (TextHelper.isNull(csaCodigo) && !desabilitado) { %>
               <%=JspHelper.geraCombo(csaListEntidadePage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidadePage),"", false, 1, csaCodigo, null, false, "form-control")%>
            <%} else if (!TextHelper.isNull(csaCodigo)) { %>
               <%=JspHelper.geraCombo(csaListEntidadePage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidadePage),"", false, 1, csaCodigo, null, true, "form-control")%>
            <%} else if (desabilitado) {%>
               <%=JspHelper.geraCombo(csaListEntidadePage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidadePage),"", false, 1, null, null, true, "form-control")%>
            <%} %>
          </div>
          
<% } else if (responsavelEntidadePage.isCsa()) { %>

          <div class="form-group col-sm-12 col-md-6">
            <label id="lblConsignatariaEntidadePage" for="csaCodigo"><hl:message key="rotulo.consignataria.singular"/></label>
            <SELECT NAME="csaCodigo" id="csaCodigo" class="form-control" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (!TextHelper.isNull(csaCodigo) || desabilitado) { %> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
                 <OPTION VALUE="<%=TextHelper.forHtmlAttribute(responsavelEntidadePage.getCsaCodigo())%>" SELECTED ><%=TextHelper.forHtmlContent(responsavelEntidadePage.getNomeEntidade())%></OPTION>
            </SELECT>
          </div>
<% } %> 

<% if (corListEntidadePage != null && !corListEntidadePage.isEmpty()) { %>   
           <div class="form-group col-sm-12 col-md-6">
               <label id="lblCorrespondenteEntidadePage" for="corCodigo"><hl:message key="rotulo.correspondente.singular"/></label>             
             <%if (TextHelper.isNull(corCodigo) && !desabilitado) { %>
               <%=JspHelper.geraCombo(corListEntidadePage, "corCodigo", Columns.COR_CODIGO + ";" + Columns.COR_IDENTIFICADOR + ";" + Columns.COR_NOME, Columns.COR_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidadePage),null, false, 1, null, null, false, "form-control")%>
             <%} else if (!TextHelper.isNull(corCodigo)) { %>
               <%=JspHelper.geraCombo(corListEntidadePage, "corCodigo", Columns.COR_CODIGO + ";" + Columns.COR_IDENTIFICADOR + ";" + Columns.COR_NOME, Columns.COR_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidadePage),null, false, 1, corCodigo, null, true, "form-control")%>
             <%} else if (desabilitado) {%>
               <%=JspHelper.geraCombo(corListEntidadePage, "corCodigo", Columns.COR_CODIGO + ";" + Columns.COR_IDENTIFICADOR + ";" + Columns.COR_NOME, Columns.COR_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidadePage),null, false, 1, null, null, true, "form-control")%>
             <%} %>
           </div>
<% } else if (responsavelEntidadePage.isCor()) { %>

           <div class="form-group col-sm-12 col-md-6">
             <label id="lblCorrespondenteEntidadePage" for="corCodigo"><hl:message key="rotulo.correspondente.singular"/></label>
             <SELECT NAME="corCodigo" id="corCodigo" class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (!TextHelper.isNull(corCodigo) || desabilitado) { %> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
               <OPTION VALUE="<%=TextHelper.forHtmlAttribute(responsavelEntidadePage.getCorCodigo())%>" SELECTED><%=TextHelper.forHtmlContent(responsavelEntidadePage.getNomeEntidade())%></OPTION>
             </SELECT>
           </div>
<% } %>
   
<% if (responsavelEntidadePage.isCseSupOrg() && orgListEntidadePage != null && !orgListEntidadePage.isEmpty()) { %>

          <div class="form-group col-sm-12 col-md-6">
               <label id="lblOrgaoEntidadePage" for="orgCodigo"><hl:message key="rotulo.orgao.singular"/></label>            
             <%if (TextHelper.isNull(orgCodigo) && !desabilitado) { %>
               <%=JspHelper.geraCombo(orgListEntidadePage, "orgCodigo", Columns.ORG_CODIGO + ";" + Columns.ORG_IDENTIFICADOR + ";" + Columns.ORG_NOME, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidadePage), null, false, 1, null, null, false, "form-control")%>
             <%} else if (!TextHelper.isNull(orgCodigo)) { %>
               <%=JspHelper.geraCombo(orgListEntidadePage, "orgCodigo", Columns.ORG_CODIGO + ";" + Columns.ORG_IDENTIFICADOR + ";" + Columns.ORG_NOME, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidadePage), null, false, 1, orgCodigo, null, true, "form-control")%>
             <%} else if (desabilitado) {%>
               <%=JspHelper.geraCombo(orgListEntidadePage, "orgCodigo", Columns.ORG_CODIGO + ";" + Columns.ORG_IDENTIFICADOR + ";" + Columns.ORG_NOME, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidadePage), null, false, 1, null, null, true, "form-control")%>
             <%} %>
          </div>
          
<% } else if (responsavelEntidadePage.isOrg()) { %>
             <div class="form-group col-sm-12 col-md-6">
               <label id="lblOrgaoEntidadePage" for="orgCodigo"><hl:message key="rotulo.orgao.singular"/></label>
               <SELECT NAME="orgCodigo" id="orgCodigo" class="form-control" onFocus="SetarEventoMascaraV4(this,'#*200',true);" <%if (!TextHelper.isNull(corCodigo) || desabilitado) { %> disabled <%} %> onBlur="fout(this);ValidaMascaraV4(this);">
                 <OPTION VALUE="<%=TextHelper.forHtmlAttribute(responsavelEntidadePage.getOrgCodigo())%>" SELECTED><%=TextHelper.forHtmlContent(responsavelEntidadePage.getNomeEntidade())%></OPTION>
               </SELECT>
             </div>
<% } %>

     <%if (!desabilitado) {%> 
      <script type="text/JavaScript">
        function addOptionsEntidade() {
          var f0 = document.forms[0];         
          if (f0.csaCodigo != null) {
            addOption(f0.csaCodigo, 0, '<hl:message key="rotulo.campo.nenhuma"/>', 'NENHUM');
            f0.csaCodigo.selectedIndex = 0;
          }
          if (f0.corCodigo != null) {
          <% if (!responsavelEntidadePage.isCsaCor()) { %>
             addOption(f0.corCodigo, 0, '<hl:message key="rotulo.campo.csa.todos"/>', 'TODOS_DA_CSA');
             f0.corCodigo.selectedIndex = 0;
          <% } %>
          <% if (!responsavelEntidadePage.isCor()) { %>
             addOption(f0.corCodigo, 0, '<hl:message key="rotulo.campo.nenhum"/>', 'NENHUM');
             f0.corCodigo.selectedIndex = 0;
          <% } %>    
          }
          if (f0.orgCodigo != null) {
            addOption(f0.orgCodigo, 0, '<hl:message key="rotulo.campo.nenhum"/>', 'NENHUM');
            f0.orgCodigo.selectedIndex = 0;
          }
        }
        addLoadEvent(addOptionsEntidade);
      </script> 
     <%} %> 
   
   <script type="text/JavaScript">
   var f0 = document.forms[0];   
   function validaInformacaoSelecionada() {
     var nenhum = 'NENHUM';
     var check = false;
     var cseNotExists = (f0.cseCodigo == null || f0.cseCodigo == undefined);
     var csaNotExists = (f0.csaCodigo == null || f0.csaCodigo == undefined);
     var corNotExists = (f0.corCodigo == null || f0.corCodigo == undefined);
     var orgNotExists = (f0.orgCodigo == null || f0.orgCodigo == undefined);
     var supNotExists = (f0.includeSuporte == null || f0.includeSuporte == undefined);

     var cseSelected = (f0.cseCodigo != null && f0.cseCodigo != undefined && f0.cseCodigo.options[f0.cseCodigo.selectedIndex].value != nenhum);
     var csaSelected = (f0.csaCodigo != null && f0.csaCodigo != undefined && f0.csaCodigo.options[f0.csaCodigo.selectedIndex].value != nenhum);
     var corSelected = (f0.corCodigo != null && f0.corCodigo != undefined && f0.corCodigo.options[f0.corCodigo.selectedIndex].value != nenhum);
     var orgSelected = (f0.orgCodigo != null && f0.orgCodigo != undefined && f0.orgCodigo.options[f0.orgCodigo.selectedIndex].value != nenhum);
     var supSelected = (f0.includeSuporte != null && f0.includeSuporte != undefined && f0.includeSuporte.checked);

     if ((cseNotExists && csaNotExists && corNotExists && orgNotExists && supNotExists) || 
         (cseSelected || csaSelected || corSelected || orgSelected || supSelected)) {
       check = true;
     }
     return check;
   }	    
   </script>   
   
        <script type="text/JavaScript">
         function valida_campo_entidade() {
             <% if (obrEntidadePage.equals("true")) { %>
             if (!validaInformacaoSelecionada()) {
                 alert('<hl:message key="mensagem.informe.uma.entidade"/>');
                 return false;
             }
             <% } %>
             return true;
         }
        </script>        