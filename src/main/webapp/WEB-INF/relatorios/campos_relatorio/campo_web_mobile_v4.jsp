<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    String obrWebMobilePage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");

    String[] origem = request.getParameterValues("origem");
    List valueList = null;
    if (origem != null) {
        valueList = Arrays.asList(origem);
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
                <div class="form-group mb-1" role="radiogroup">
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="origem" id="origem1"
                               title="<hl:message key="rotulo.solicitacao.via.web"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (origem != null && valueList.contains("1")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="1">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="origem1"><hl:message
                                key="rotulo.solicitacao.via.web"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="origem" id="origem2"
                               title="<hl:message key="rotulo.solicitacao.via.mobile"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (origem != null && valueList.contains("2")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="2">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="origem2"><hl:message
                                key="rotulo.solicitacao.via.mobile"/></label>
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</div>

<script language="JavaScript" type="text/JavaScript">
    function valida_campo_web_mobile() {
        <% if (obrWebMobilePage.equals("true")) { %>
        var tam = document.forms[0].origem.length;
        var qtd = 0;
        for (var i = 0; i < tam; i++) {
            if (document.forms[0].origem[i].checked == true) {
                qtd++;
            }
        }
        if (qtd <= 0) {
            alert('<hl:message key="mensagem.informe.web.mobile"/>');
            return false;
        }
        <% } %>
        return true;
    }
</script>
                                            