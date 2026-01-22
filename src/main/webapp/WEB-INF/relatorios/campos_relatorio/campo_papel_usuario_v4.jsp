<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    String obrPapelUsuarioPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String[] papel = request.getParameterValues("papel");
    List valueList = null;
    if (papel != null) {
        valueList = Arrays.asList(papel);
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
                        <input class="form-check-input ml-1" type="checkbox" name="papel" id="papel1"
                               title="<hl:message key="rotulo.consignante.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papel != null && valueList.contains("cse")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="cse">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel1"><hl:message
                                key="rotulo.consignante.abreviado"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="papel" id="papel2"
                               title="<hl:message key="rotulo.orgao.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papel != null && valueList.contains("org")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="org">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel2"><hl:message
                                key="rotulo.orgao.abreviado"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="papel" id="papel3"
                               title="<hl:message key="rotulo.consignataria.sigla"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papel != null && valueList.contains("csa")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="csa">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel3"><hl:message
                                key="rotulo.consignataria.sigla"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="papel" id="papel4"
                               title="<hl:message key="rotulo.correspondente.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papel != null && valueList.contains("cor")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="cor">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel4"><hl:message
                                key="rotulo.correspondente.abreviado"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="papel" id="papel5"
                               title="<hl:message key="rotulo.servidor.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papel != null && valueList.contains("ser")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="ser">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="papel5"><hl:message
                                key="rotulo.servidor.abreviado"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="papel" id="papel6"
                               title="<hl:message key="rotulo.suporte.abreviado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (papel != null && valueList.contains("sup")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
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
    function valida_campo_papel_usuario() {
        <% if (obrPapelUsuarioPage.equals("true")) { %>
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
        <% } %>
        return true;
    }
</script>