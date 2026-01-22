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
AcessoSistema responsavelMensagensPage = JspHelper.getAcessoSistema(request);
String obrMensagensPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");

List<TransferObject> mensagens = (List<TransferObject>) request.getAttribute("listaMensagens");

boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
    <div class="form-group col-sm-12  col-md-6">
      <label id="menCodigo" for="iMenCodigo">${descricoes[recurso]}</label>            
      <%=JspHelper.geraCombo(mensagens, "iMenCodigo", Columns.MEN_CODIGO + ";" + Columns.MEN_TITULO, Columns.MEN_TITULO, ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavelMensagensPage), "" , true, 1, null, null, desabilitado, "form-control")%>
    </div>

    <% if (obrMensagensPage.equals("true")) { %>
          <script type="text/JavaScript">
          function funMensagensPage() {
              camposObrigatorios = camposObrigatorios + 'menCodigo,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.mensagem"/>,';
          }
          addLoadEvent(funMensagensPage);
          </script>
    <% } %>
    
    <script type="text/JavaScript">
    	function valida_campo_mensagens() {
        	return true;
		}
	</script>
