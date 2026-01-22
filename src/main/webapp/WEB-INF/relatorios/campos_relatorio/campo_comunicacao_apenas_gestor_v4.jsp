<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    AcessoSistema responsavelComGestorPage = JspHelper.getAcessoSistema(request);
    String obrComGestorPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String comunicacaoApenasGestor = (String) JspHelper.verificaVarQryStr(request, "comunicacaoApenasGestor");
    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true : false;

    String others = "";
    if (desabilitado) {
        others = "disabled";
    }

%>
<div class="col-sm-12">
    <fieldset>
        <legend class="legend">
            <span>${descricoes[recurso]}</span>
        </legend>
        <div class="row mb-2">
            <div class="col-sm-12 col-md-6">
                <div class="form-group mb-1">
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="radio" name="comunicacaoApenasGestor"
                               id="comunicacaoApenasGestor1" title='<hl:message key="rotulo.sim"/>'
                               value="true" <% if (!TextHelper.isNull(comunicacaoApenasGestor) && comunicacaoApenasGestor.equals("true")) { %>
                               checked <% } %> <%if (desabilitado) {%> disabled <%} %>
                               onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                               onBlur="fout(this);ValidaMascaraV4(this);">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4"
                               for="comunicacaoApenasGestor1"><hl:message key="rotulo.sim"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input ml-1" type="radio" name="comunicacaoApenasGestor"
                               id="comunicacaoApenasGestor2" title='<hl:message key="rotulo.nao"/>'
                               value="false" <% if (!TextHelper.isNull(comunicacaoApenasGestor) && comunicacaoApenasGestor.equals("false")) { %>
                               checked <% } %> <%if (desabilitado) {%> disabled <%} %>
                               onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                               onBlur="fout(this);ValidaMascaraV4(this);">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4"
                               for="comunicacaoApenasGestor2"><hl:message key="rotulo.nao"/></label>
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</div>

<% if (obrComGestorPage.equals("true")) { %>
<script type="text/JavaScript">
    function funComGestorPage() {
        camposObrigatorios = camposObrigatorios + 'comunicacaoApenasGestor,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.comunicacao.apenas.gestor"/>,';
    }

    addLoadEvent(funComGestorPage);
</script>
<% } %>

<script type="text/JavaScript">
    function valida_campo_comunicacao_apenas_gestor() {
        return true;
    }
</script>
