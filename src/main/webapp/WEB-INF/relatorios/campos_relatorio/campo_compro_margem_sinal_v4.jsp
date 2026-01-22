<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<% 
String obrSinalMargemPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String descSinalMargemPage = pageContext.getAttribute("descricao").toString();   
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
String desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? "true" : "false";
AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
%>
<fieldset class="col-sm-12 col-md-12">
	<div class="legend">
		<span><%=descSinalMargemPage%></span>
	</div>
	<div class="row">
		<div class="col-sm-2 col-md-2">
			<input type="checkbox" name="SINAL" id="SINAL1" value="1"
				title="Positiva" onfocus="SetarEventoMascara(this,'#*200',true);"
				onblur="fout(this);ValidaMascara(this);"
				class="form-check-input ml-1">
				<label class="form-check-label labelSemNegrito ml-1 pr-4" for="SINAL1"><%=ApplicationResourcesHelper.getMessage("rotulo.positiva.singular", responsavel)%></label>
		</div>
		<div class="col-sm-2 col-md-2">
			<input type="checkbox" name="SINAL" id="SINAL2" value="0"
				title="Zerada" onfocus="SetarEventoMascara(this,'#*200',true);"
				onblur="fout(this);ValidaMascara(this);"
				class="form-check-input ml-1">
			<label class="form-check-label labelSemNegrito ml-1 pr-4" for="SINAL2"><%=ApplicationResourcesHelper.getMessage("rotulo.zerada.singular", responsavel)%></label>
		</div>
		<div class="col-sm-2 col-md-2">
			<input type="checkbox" name="SINAL" id="SINAL3" value="-1"
				title="Negativa" onfocus="SetarEventoMascara(this,'#*200',true);"
				onblur="fout(this);ValidaMascara(this);"
				class="form-check-input ml-1">
				<label class="form-check-label labelSemNegrito ml-1 pr-4" for="SINAL3"><%=ApplicationResourcesHelper.getMessage("rotulo.negativa.singular", responsavel)%></label>
		</div>
	</div>
</fieldset>
<script type="text/JavaScript">
	function valida_campo_compro_margem_sinal() {
		return true;
	}
</script>