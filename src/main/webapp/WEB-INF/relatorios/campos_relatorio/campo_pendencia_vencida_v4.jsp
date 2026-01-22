<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    AcessoSistema responsavelPendenciaVencPage = JspHelper.getAcessoSistema(request);
    String obrPendenciaVencPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true : false;
%>
<div class="col-sm-12">
    <fieldset>
        <div class="legend">
            <span>${descricoes[recurso]}</span>
        </div>
        <div class="form-group mb-1" role="radiogroup" aria-labelledby="PvDescricao">
            <div class="form-check form-check-inline">
                <input class="form-check-input ml-1" type="radio" name="pendenciaVencida" id="pendenciaVencida2"
                       title="<hl:message key="rotulo.sim"/>"
                       value="true" <%= JspHelper.verificaVarQryStr(request, "pendenciaVencida").equals("true") ? "checked" : "" %>
                       onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                       onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="pendenciaVencida2"><hl:message
                        key="rotulo.sim"/></label>
            </div>
            <div class="form-check form-check-inline">
                <input class="form-check-input ml-1" type="radio" name="pendenciaVencida" id="pendenciaVencida1"
                       title="<hl:message key="rotulo.nao"/>"
                       value="false" <%=!JspHelper.verificaVarQryStr(request, "pendenciaVencida").equals("true") ? "checked" : "" %>
                       onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                       onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="pendenciaVencida1"><hl:message
                        key="rotulo.nao"/></label>
            </div>
        </div>
    </fieldset>
</div>

<% if (obrPendenciaVencPage.equals("true")) { %>
<script type="text/JavaScript">
    function funPendenciaVencidaPage() {
        camposObrigatorios = camposObrigatorios + 'pendenciaVencida,';

        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.pendencia.somente.vencida"/>,';
    }

    addLoadEvent(funPendenciaVencidaPage);
</script>
<% } %>

<script type="text/JavaScript">
    function valida_campo_pendencia_vencida() {
        return true;
    }
</script>
