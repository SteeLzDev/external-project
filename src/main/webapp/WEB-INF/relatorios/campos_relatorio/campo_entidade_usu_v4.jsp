<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelEntidUsuPage = JspHelper.getAcessoSistema(request);
   String obrEntidUsuPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   
   String csaValorCampoEntUsuPage = JspHelper.verificaVarQryStr(request, "csaCodigo");
   String[] arrCsaValorCampoEntUsuPage = csaValorCampoEntUsuPage.split(";");
   String csaCodigoCampoEntUsuPage = arrCsaValorCampoEntUsuPage[0];
   String csaIdentificadorCampoEntUsuPage = "";
   String csaNomeCampoEntUsuPage = "";
   if (arrCsaValorCampoEntUsuPage.length > 1) {
     csaIdentificadorCampoEntUsuPage = arrCsaValorCampoEntUsuPage[1];
     csaNomeCampoEntUsuPage = arrCsaValorCampoEntUsuPage[2];
   }

   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

   String cseCodigo = JspHelper.verificaVarQryStr(request, "cseCodigo");
   String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
   String corCodigo = JspHelper.verificaVarQryStr(request, "corCodigo");
   String orgCodigo = JspHelper.verificaVarQryStr(request, "orgCodigo");
   String includeSuporte = JspHelper.verificaVarQryStr(request, "includeSuporte");

   List<TransferObject> cseListEntidadePage = (List<TransferObject>) request.getAttribute("listaConsignantes");
   List<TransferObject> orgListEntidadePage = (List<TransferObject>) request.getAttribute("listaOrgaos");
   List<TransferObject> csaListEntidadePage = (List<TransferObject>) request.getAttribute("listaConsignatarias");
   List<TransferObject> corListEntidadePage = (List<TransferObject>) request.getAttribute("listaCorrespondentes");

   if (responsavelEntidUsuPage.isCseSup() && csaListEntidadePage != null) {
%>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblConsignanteUsuPage" for="cseCodigo"><hl:message key="rotulo.consignante.singular"/></label>
            <SELECT NAME="cseCodigo" id="cseCodigo" class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%>>
                <OPTION VALUE="NENHUM" SELECTED><hl:message key="rotulo.campo.nenhum"/></OPTION>
                <%
                  Iterator iteCse = cseListEntidadePage.iterator();
                  while (iteCse.hasNext()) {
                      CustomTransferObject ctoCse = (CustomTransferObject)iteCse.next();
                      String fieldValueCse = ctoCse.getAttribute(Columns.CSE_CODIGO) + ";" + ctoCse.getAttribute(Columns.CSE_IDENTIFICADOR) + ";" + ctoCse.getAttribute(Columns.CSE_NOME);
                      String fieldLabelCse = ctoCse.getAttribute(Columns.CSE_NOME) + " - " + ctoCse.getAttribute(Columns.CSE_IDENTIFICADOR);
                %>
                <OPTION VALUE="<%=TextHelper.forHtmlAttribute(fieldValueCse)%>" <%= fieldValueCse.equals(cseCodigo) ? "selected" : "" %>><%=TextHelper.forHtmlContent(fieldLabelCse)%></OPTION>
                <%    
                  }
                %>
            </SELECT>
          </div>
          <div class="form-group col-sm-12 col-md-6">
            <div class="d-flex align-items-center" style="height: 100px">
              <div class="form-check pt-1">
                 <INPUT TYPE="checkbox" VALUE="true" TITLE='<hl:message key="mensagem.incluir.usuario.suporte"/>' <%=(String)(includeSuporte.equals("true") ? "checked" : "")%> NAME="includeSuporte" ID="includeSuporte" <%=(String)(desabilitado ? "disabled" : "")%>/>
                 <label class="form-check-label labelSemNegrito pl-1" for="includeSuporte"><hl:message key="mensagem.incluir.usuario.suporte"/></label>
              </div>
            </div>
          </div>
<% } %>

<% if (responsavelEntidUsuPage.isCseSup() && csaListEntidadePage != null) { %>

          <div class="form-group col-sm-12 col-md-6">
            <label id="lblConsignatariaUsuPage" for="csaCodigo"><hl:message key="rotulo.consignataria.singular"/></label>
            <%=JspHelper.geraCombo(csaListEntidadePage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidUsuPage), null, false, 1, csaCodigo, null, desabilitado, "form-control")%>
          </div>
          
<% } else if (responsavelEntidUsuPage.isCsa()) { %>

          <div class="form-group col-sm-12 col-md-6">
            <label id="lblConsignatariaUsuPage" for="csaCodigo"><hl:message key="rotulo.consignataria.singular"/></label>
            <SELECT NAME="csaCodigo" id="csaCodigo" class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%>>
                <OPTION VALUE="" SELECTED><%=TextHelper.forHtmlContent(responsavelEntidUsuPage.getNomeEntidade())%></OPTION>
            </SELECT>
          </div>
<% } %>
   
<% if ((responsavelEntidUsuPage.isCseSup() || responsavelEntidUsuPage.isCsa()) && corListEntidadePage != null && !corListEntidadePage.isEmpty()) {

       String outros = "";
       if (responsavelEntidUsuPage.isCseSup()) {
         outros = "onChange=\"resetCorrespondentes();\"";
       }
%>
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblCorrespondenteUsuPage" for="corCodigo"><hl:message key="rotulo.correspondente.singular"/></label>
            <%=JspHelper.geraCombo(corListEntidadePage, "corCodigo", Columns.COR_CODIGO + ";" + Columns.COR_IDENTIFICADOR + ";" + Columns.COR_NOME, Columns.COR_NOME, corCodigo, outros, true, 1, corCodigo, null, desabilitado, "form-control")%>
          </div>

      <script type="text/JavaScript">
          <% if (!responsavelEntidUsuPage.isCsaCor()) { %>
          function adicionarOpcaoCsaTodosEspecifico() {
             addOption(f0.corCodigo, 0, '<%=ApplicationResourcesHelper.getMessage("rotulo.campo.csa.todos.especifico", responsavelEntidUsuPage, csaNomeCampoEntUsuPage + " - " + csaIdentificadorCampoEntUsuPage)%>', 'TODOS_DA_CSA;<%=TextHelper.forJavaScriptBlock(csaCodigoCampoEntUsuPage + ";" + csaIdentificadorCampoEntUsuPage + ";" + csaNomeCampoEntUsuPage)%>');
             f0.corCodigo.selectedIndex = 0;
          }
          addLoadEvent(adicionarOpcaoCsaTodosEspecifico);
          <% } %>
      </script> 

<% } else if (responsavelEntidUsuPage.isCseSup()) { %>

           <div class="form-group col-sm-12 col-md-6">
             <label id="lblCorrespondenteUsuPage" for="corCodigo"><hl:message key="rotulo.correspondente.singular"/></label>
             <SELECT NAME="corCodigo" id="corCodigo" class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" onChange="carregaCorrespondentes();" <%=(String)(desabilitado ? "disabled" : "")%>>
             <%
                Iterator ite = csaListEntidadePage.iterator();
                while (ite.hasNext()) {
                  CustomTransferObject row = (CustomTransferObject) ite.next();
                  String rowValue = "TODOS_DA_CSA;" + row.getAttribute(Columns.CSA_CODIGO).toString() + ";" + row.getAttribute(Columns.CSA_IDENTIFICADOR).toString() + ";" + row.getAttribute(Columns.CSA_NOME).toString();
             %>
             <OPTION VALUE="<%=TextHelper.forHtmlAttribute(rowValue)%>" <%= rowValue.equals(corCodigo) ? "selected" : "" %>><%=ApplicationResourcesHelper.getMessage("rotulo.campo.csa.todos.especifico", responsavelEntidUsuPage, row.getAttribute(Columns.CSA_NOME).toString() + " - " + row.getAttribute(Columns.CSA_IDENTIFICADOR).toString())%></OPTION>
             <%
                }
             %>
             </SELECT>
           </div>
           
<% } else if (responsavelEntidUsuPage.isCor()) { %>
 
          <div class="form-group col-sm-12 col-md-6">
            <label id="lblCorrespondenteUsuPage" for="corCodigo"><hl:message key="rotulo.correspondente.singular"/></label>
            <SELECT NAME="corCodigo" id="corCodigo" class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%>>
                <OPTION VALUE="" SELECTED><%=TextHelper.forHtmlContent(responsavelEntidUsuPage.getNomeEntidade())%></OPTION>
            </SELECT>
          </div>
<% } %>
 
<% if (responsavelEntidUsuPage.isCseSupOrg() && orgListEntidadePage != null && !orgListEntidadePage.isEmpty()) { %>

          <div class="form-group col-sm-12 col-md-6">
            <label id="lblOrgaoUsuPage" for="orgCodigo"><hl:message key="rotulo.orgao.singular"/></label>
            <%=JspHelper.geraCombo(orgListEntidadePage, "orgCodigo", Columns.ORG_CODIGO + ";" + Columns.ORG_IDENTIFICADOR + ";" + Columns.ORG_NOME, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEntidUsuPage), null, false, 1, orgCodigo, null, desabilitado, "form-control")%>
          </div>
          
<% } else if (responsavelEntidUsuPage.isOrg()) { %>

          <div class="form-group col-sm-12 col-md-6">
            <label id="lblOrgaoUsuPage" for="orgCodigo"><hl:message key="rotulo.orgao.singular"/></label>
            <SELECT NAME="orgCodigo" id="orgCodigo" class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled" : "")%>>
                <OPTION VALUE="" SELECTED><%=TextHelper.forHtmlContent(responsavelEntidUsuPage.getNomeEntidade())%></OPTION>
            </SELECT>
          </div>
<% } %>

      <script type="text/JavaScript">
          function adicionarOpcoesNenhum() {
            if (f0.csaCodigo != null) {
              addOption(f0.csaCodigo, 0, '<hl:message key="rotulo.campo.nenhuma"/>', 'NENHUM');
              if (f0.csaCodigo.value == '') {
                f0.csaCodigo.selectedIndex = 0;
              }
            }
            if (f0.corCodigo != null) {
            <% if ((responsavelEntidUsuPage.isCsa() || 
                   (responsavelEntidUsuPage.isCor() && responsavelEntidUsuPage.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) || 
                   (responsavelEntidUsuPage.isCseSupOrg() && TextHelper.isNull(csaValorCampoEntUsuPage))) { %>
               addOption(f0.corCodigo, 0, '<hl:message key="rotulo.campo.todos"/>', '');
            <% } %>
            <%  if (!responsavelEntidUsuPage.isCor()) { %>
                var i;
                var jaTemNenhumValue = 'false';
                for (i=0;i<f0.corCodigo.length;i++)
                {
                  if(f0.corCodigo.options[i].text.indexOf("NENHUM")> -1) {
                     jaTemNenhumValue = 'true'
                  }
                }
                if (jaTemNenhumValue == 'false') {
                  addOption(f0.corCodigo, 0, '<hl:message key="rotulo.campo.nenhum"/>', 'NENHUM');
                }
               <%if (TextHelper.isNull(csaValorCampoEntUsuPage) ) { %>
                f0.corCodigo.selectedIndex = 0;
               <%} %>    
            <% } %>    
            }
            if (f0.orgCodigo != null) {
              addOption(f0.orgCodigo, 0, '<hl:message key="rotulo.campo.nenhum"/>', 'NENHUM');
              f0.orgCodigo.selectedIndex = 0;
            }
          }
          addLoadEvent(adicionarOpcoesNenhum);

          function carregaCorrespondentes() {
            if (f0.corCodigo && f0.corCodigo.selectedIndex >= 0) {
              var valores = f0.corCodigo.options[f0.corCodigo.selectedIndex].value.split(';');
              if (valores[0] == 'TODOS_DA_CSA') {
                var csaCodigo = valores[1];
                var csaIdentificador = valores[2];
                var csaNome = valores[3];
                refresh('csaCodigo=' + csaCodigo);
              }
            }
          }
          
          function resetCorrespondentes() {
            if (f0.corCodigo && f0.corCodigo.selectedIndex == 0) {
              refresh();
            }
          }
      </script> 
   
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
         function valida_campo_entidade_usu() {
             <% if (obrEntidUsuPage.equals("true")) { %>
             if (!validaInformacaoSelecionada()) {
                 alert('<hl:message key="mensagem.informe.uma.entidade"/>');
                 return false;
             }
             <% } %>
             return true;
         }
        </script>        