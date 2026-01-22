<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    String obrScvPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String[] scvCodigo = request.getParameterValues("scvCodigo");
    List valueList = null;
    if (scvCodigo != null) {
        valueList = Arrays.asList(scvCodigo);
    }

    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true : false;
%>

<div class="col-sm-12">
    <fieldset>
        <div class="legend">
            <span>${descricoes[recurso]}</span>
        </div>
        <div class="row">
            <div class="col-sm-12 col-md-6">
                <div class="form-group mb-1" role="radiogroup" aria-labelledby="scvCodigo">
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="scvCodigo" id="scvCodigo1"
                               title="<hl:message key="rotulo.convenio.status.ativo"/>" <%if (scvCodigo != null && valueList.contains(CodedValues.SCV_ATIVO)) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="<%=(String)CodedValues.SCV_ATIVO%>"
                               onFocus="SetarEventoMascaraV4(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascaraV4(this);">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="scvCodigo1"><hl:message
                                key="rotulo.convenio.status.ativo"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="scvCodigo" id="scvCodigo2"
                               title="<hl:message key="rotulo.convenio.status.inativo"/>" <%if (scvCodigo != null && valueList.contains(CodedValues.SCV_INATIVO)) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} %>
                               value="<%=(String)CodedValues.SCV_INATIVO%>"
                               onFocus="SetarEventoMascaraV4(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascaraV4(this);">
                        <label class="form-check-label labelSemNegrito ml-1" for="scvCodigo2"><hl:message
                                key="rotulo.convenio.status.inativo"/></label>
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</div>

<script type="text/JavaScript">
    function valida_campo_status_convenio() {
        <% if (obrScvPage.equals("true")) { %>
        var tam = document.forms[0].scvCodigo.length;
        var qtd = 0;
        for (var i = 0; i < tam; i++) {
            if (document.forms[0].scvCodigo[i].checked == true) {
                qtd++;
            }
        }
        if (qtd <= 0) {
            alert('<hl:message key="mensagem.informe.cnv.status"/>');
            return false;
        }
        <% } %>
        return true;
    }
</script>
                                            