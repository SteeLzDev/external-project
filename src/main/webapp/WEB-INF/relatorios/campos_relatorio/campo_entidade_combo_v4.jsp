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
   AcessoSistema responsavelEntidadeComboPage = JspHelper.getAcessoSistema(request);
   String obrEntidadeComboPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   
   List<TransferObject> csaListEntidadeComboPage = (List<TransferObject>) request.getAttribute("listaConsignatarias");
   
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
    <div class="form-group col-sm-12 col-md-6">
      <label id="lblMensagens" for="entidadeCombo">${descricoes[recurso]}</label>
      <SELECT NAME="entidadeCombo" id="entidadeCombo" CLASS="form-control" onChange="javascript:habilitaCampoCsa()" <%=desabilitado ? "disabled" : "" %>>
         <OPTION VALUE=""><hl:message key="rotulo.campo.selecione"/></OPTION>
         <OPTION VALUE="<%=AcessoSistema.ENTIDADE_CSE%>"><hl:message key="rotulo.consignante.singular"/></OPTION>
         <OPTION VALUE="<%=AcessoSistema.ENTIDADE_CSA%>"><hl:message key="rotulo.consignataria.singular"/></OPTION>
         <OPTION VALUE="<%=AcessoSistema.ENTIDADE_COR%>"><hl:message key="rotulo.correspondente.singular"/></OPTION>
         <OPTION VALUE="<%=AcessoSistema.ENTIDADE_ORG%>"><hl:message key="rotulo.orgao.singular"/></OPTION>
         <OPTION VALUE="<%=AcessoSistema.ENTIDADE_SER%>"><hl:message key="rotulo.servidor.singular"/></OPTION>
         <OPTION VALUE="<%=AcessoSistema.ENTIDADE_SUP%>"><hl:message key="rotulo.suporte.singular"/></OPTION>
      </SELECT>
    </div>
    
    <div class="form-group col-sm-12 col-md-6">
      <label id="lblConsignataria" for="csaCodigo"><hl:message key="rotulo.consignataria.singular"/></label>            
      <%=JspHelper.geraCombo(csaListEntidadeComboPage, "csaCodigo", Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavelEntidadeComboPage), null, false, 1, null, null, true, "form-control")%>
    </div>

    <% if (obrEntidadeComboPage.equals("true")) { %>
          <script type="text/JavaScript">
          function funEntidadeComboPage() {
              camposObrigatorios = camposObrigatorios + 'entidadeCombo,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.entidade"/>,';
          }
          addLoadEvent(funEntidadeComboPage);
          </script>
    <% } %>
    
    <script type="text/JavaScript">
    	function valida_campo_entidade_combo() {
        	return true;
		}
    	
        // habilita o campo csa 
        function habilitaCampoCsa() {
   		   	if(f0.entidadeCombo.value == "CSA"){
				f0.csaCodigo.disabled = false;
   		   	} else {
   				f0.csaCodigo.disabled = true;
   				f0.csaCodigo.value = '';
   		   	}
		}
        
	</script>
