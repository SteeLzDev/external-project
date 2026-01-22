<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavelCorPage = JspHelper.getAcessoSistema(request);
String obrCorPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String corCodigo = (String) JspHelper.verificaVarQryStr(request, "corCodigo");

List<TransferObject> correspondentes = (List<TransferObject>) request.getAttribute("listaCorrespondentes");

String fieldValue = Columns.COR_CODIGO + ";" + Columns.COR_IDENTIFICADOR + ";" + Columns.COR_NOME;
String fieldLabel = Columns.COR_NOME;
String rotuloNenhum = ApplicationResourcesHelper.getMessage("rotulo.campo.nenhum", responsavelCorPage);
%>
<div class="form-group col-sm-12 col-md-6">
	<label id="lblCorrespondente" for="corCodigo">${descricoes[recurso]}</label>
	<%=JspHelper.geraCombo(correspondentes, "corCodigo", fieldValue, fieldLabel, rotuloNenhum, null, (TextHelper.isNull(corCodigo)), 5, corCodigo, null, false, "form-control")%>
</div>
<%
    if (obrCorPage.equals("true")) {
%>
<script type="text/JavaScript">
	function funCorPage() {
		camposObrigatorios = camposObrigatorios + 'corCodigo,';
		msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.orgao"/>,';
	}
	addLoadEvent(funCorPage);
</script>
<%
    }
%>

<script type="text/JavaScript">
	function valida_campo_cor_multiplo() {
		return true;
	}
</script>
