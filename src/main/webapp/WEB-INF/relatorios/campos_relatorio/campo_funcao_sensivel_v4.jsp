<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    String obrFuncaoSensivelPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String[] papelFuncaoSensivelPage = request.getParameterValues("papel");
    List valueListFuncaoSensivelPage = null;
    if (papelFuncaoSensivelPage != null) {
        valueListFuncaoSensivelPage = Arrays.asList(papelFuncaoSensivelPage);
    }

    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true : false;
%>
<div class="col-sm-6">
    <fieldset>
        <div class="legend">
            <span>${descricoes[recurso]}</span>
        </div>
                 <div class="form-check form-check-inline">
                     <input class="form-check-input ml-1" type="checkbox" name="somenteFuncoesSensiveis" id="somenteFuncoesSensiveis"
                            title="<hl:message key="rotulo.relatorio.somente.funcoes.sensiveis"/>"
                            onFocus="SetarEventoMascara(this,'#*200',true);"
                            onBlur="fout(this);ValidaMascara(this);" 
                            <%if (papelFuncaoSensivelPage != null && valueListFuncaoSensivelPage.contains("somenteFuncoesSensiveis")) {%>checked disabled <%} else if (desabilitado) {%> disabled <%} %>
                            value="true">
                     <label class="form-check-label labelSemNegrito ml-1 pr-4" for="somenteFuncoesSensiveis"><hl:message
                             key="rotulo.relatorio.somente.funcoes.sensiveis"/></label>
                 </div>
        <div class="legend">
            <span><hl:message key="rotulo.papel.singular"/></span>
        </div>
        <div class="row">
            <div class="col-sm-12 col-md-6">
                <div class="form-group mb-1" role="radiogroup">
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="radio" name="papel" id="papel1"
                               title="<hl:message key="rotulo.consignante.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papelFuncaoSensivelPage != null && valueListFuncaoSensivelPage.contains("cse")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} %>
                               value="cse">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel1"><hl:message
                                key="rotulo.consignante.abreviado"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="radio" name="papel" id="papel2"
                               title="<hl:message key="rotulo.orgao.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papelFuncaoSensivelPage != null && valueListFuncaoSensivelPage.contains("org")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} %>
                               value="org">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel2"><hl:message
                                key="rotulo.orgao.abreviado"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="radio" name="papel" id="papel3"
                               title="<hl:message key="rotulo.consignataria.sigla"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papelFuncaoSensivelPage != null && valueListFuncaoSensivelPage.contains("csa")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} %>
                               value="csa">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel3"><hl:message
                                key="rotulo.consignataria.sigla"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="radio" name="papel" id="papel4"
                               title="<hl:message key="rotulo.correspondente.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papelFuncaoSensivelPage != null && valueListFuncaoSensivelPage.contains("cor")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} %>
                               value="cor">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel4"><hl:message
                                key="rotulo.correspondente.abreviado"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="radio" name="papel" id="papel5"
                               title="<hl:message key="rotulo.servidor.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papelFuncaoSensivelPage != null && valueListFuncaoSensivelPage.contains("ser")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} %>
                               value="ser">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel5"><hl:message
                                key="rotulo.servidor.abreviado"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="radio" name="papel" id="papel6"
                               title="<hl:message key="rotulo.suporte.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papelFuncaoSensivelPage != null && valueListFuncaoSensivelPage.contains("sup")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} %>
                               value="sup">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel6"><hl:message
                                key="rotulo.suporte.abreviado"/></label>
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</div>

<script language="JavaScript" type="text/JavaScript">
    function valida_campo_funcao_sensivel() {
    	with (document.forms[0]) {
	        if (<%=obrFuncaoSensivelPage.equals("true")%> || getCheckedRadio('form1', 'somenteFuncoesSensiveis') == 'true') {
				// Limpa função selecionada porque será filtrado somente pelas funções sensíveis
	        	document.forms[0].funCodigo.value = '';
	        	
		        var tam = document.forms[0].papel.length;
		        var qtd = 0;
		        for (var i = 0; i < tam; i++) {
		            if (document.forms[0].papel[i].checked == true) {
		                qtd++;
		            }
		        }
		        if (qtd <= 0) {
		            alert('<hl:message key="mensagem.informe.papel.usuario"/>');
		            return false;
		        }
	        }
    	}
        return true;
    }
</script>