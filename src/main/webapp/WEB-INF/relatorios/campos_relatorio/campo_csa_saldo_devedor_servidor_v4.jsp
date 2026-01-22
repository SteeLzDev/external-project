<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<% 
AcessoSistema responsavelCsaPage = JspHelper.getAcessoSistema(request);
String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");

String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
String obrCsaSaldoDevedorServidorPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");  

String fieldValue = Columns.CSA_CODIGO + ";" + Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME;
String fieldLabel = Columns.CSA_NOME + ";" + Columns.CSA_IDENTIFICADOR;

List<TransferObject> lstCsaSaldoDevedorServidor = (List<TransferObject>) request.getAttribute("lstCsaSaldoDevedorServidor");

String rotuloNenhum = (lstCsaSaldoDevedorServidor != null && !lstCsaSaldoDevedorServidor.isEmpty()) ? null : ApplicationResourcesHelper.getMessage("rotulo.campo.nenhuma", responsavelCsaPage);
String campoObrigatorioMsg = ApplicationResourcesHelper.getMessage("mensagem.informe.consignataria", responsavelCsaPage);

%>
	<div class="form-group col-sm-12 col-md-6">
	  <label id="lblCsaSaldoDevedorCsa" for="csaCodigoSaldoDevedor">${descricoes[recurso]}</label>
	  <%=JspHelper.geraCombo(lstCsaSaldoDevedorServidor, "csaCodigoSaldoDevedor", fieldValue, fieldLabel, rotuloNenhum, null, false, 1, csaCodigo, null, desabilitado, "form-control")%>
	</div>

	<% if (obrCsaSaldoDevedorServidorPage.equals("true")) { %>
     <script type="text/JavaScript">
     function funCsaSaldoDevedorServidorPage() {
         camposObrigatorios = camposObrigatorios + 'csaCodigoSaldoDevedor,';
         msgCamposObrigatorios = msgCamposObrigatorios + '<%=campoObrigatorioMsg%>,';
     }
     addLoadEvent(funCsaSaldoDevedorServidorPage);     
     </script>
	<% } %>   

	<script type="text/JavaScript">
	function valida_campo_csa_saldo_devedor_servidor() {
	 return true;
	}
	</script>
