<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    String obrTermoPrivaPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");

    String[] termo = request.getParameterValues("termo");
    List valueList = null;
    if (termo != null) {
        valueList = Arrays.asList(termo);
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
            <div class="col-sm-12 col-md-12">
                <div class="form-group mb-1" role="radiogroup">
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="termo" id="termo1"
                               title="<hl:message key="rotulo.termo.de.uso"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (termo != null && valueList.contains("1")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="1">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="termo1"><hl:message
                                key="rotulo.termo.de.uso"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="termo" id="termo2"
                               title="<hl:message key="rotulo.politica.privacidade.titulo"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (termo != null && valueList.contains("2")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="2">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="termo2"><hl:message
                                key="rotulo.politica.privacidade.titulo"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="termo" id="termo3"
                               title="<hl:message key="rotulo.termo.adesao.autorizado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (termo != null && valueList.contains("3")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="3">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="termo3"><hl:message
                                key="rotulo.termo.adesao.autorizado"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="checkbox" name="termo" id="termo4"
                               title="<hl:message key="rotulo.termo.adesao.nao.autorizado"/>"
                               onFocus="SetarEventoMascara(this,'#*200',true);"
                               onBlur="fout(this);ValidaMascara(this);" <%if (termo != null && valueList.contains("4")) {%>
                               checked disabled <%} else if (desabilitado) {%> disabled <%} else { %> checked <%} %>
                               value="4">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4" for="termo4"><hl:message
                                key="rotulo.termo.adesao.nao.autorizado"/></label>
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</div>


<script type="text/JavaScript">
    function valida_campo_termo_privacidade() {
        <% if (obrTermoPrivaPage.equals("true")) { %>
        var tam = document.forms[0].termo.length;
        var qtd = 0;
        for (var i = 0; i < tam; i++) {
            if (document.forms[0].termo[i].checked == true) {
                qtd++;
            }
        }
        if (qtd <= 0) {
            alert('<hl:message key="mensagem.informe.termo.politica"/>');
            return false;
        }
        <% } %>
        return true;
    }
</script>
                                            