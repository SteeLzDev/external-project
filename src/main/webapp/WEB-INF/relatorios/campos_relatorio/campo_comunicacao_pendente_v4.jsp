<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    AcessoSistema responsavelComunicacaoPendentePage = JspHelper.getAcessoSistema(request);
    String obrComunicacaoPendentePage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String comunicacaoPendente = (String) JspHelper.verificaVarQryStr(request, "comunicacaoPendente");
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
        <div class="row">
            <div class="col-sm-12 col-md-6">
                    <div class="form-group">
                        <div class="form-check form-check-inline">
                            <input class="form-check-input mt-1" type="radio" name="comunicacaoPendente"
                                   id="comunicacaoPendente1" title='<hl:message key="rotulo.sim"/>'
                                   value="1" <% if (!TextHelper.isNull(comunicacaoPendente) && comunicacaoPendente.equals("1")) { %>
                                   checked <% } %> <%if (desabilitado) {%> disabled <%} %>
                                   onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                                   onBlur="fout(this);ValidaMascaraV4(this);">
                            <label class="form-check-label labelSemNegrito ml-1 pr-4"
                                   for="comunicacaoPendente1"><hl:message key="rotulo.sim"/></label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input mt-1" type="radio" name="comunicacaoPendente"
                                   id="comunicacaoPendente2" title='<hl:message key="rotulo.nao"/>'
                                   value="0" <% if (!TextHelper.isNull(comunicacaoPendente) && comunicacaoPendente.equals("0")) { %>
                                   checked <% } %> <%if (desabilitado) {%> disabled <%} %>
                                   onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                                   onBlur="fout(this);ValidaMascaraV4(this);">
                            <label class="form-check-label labelSemNegrito ml-1 pr-4"
                                   for="comunicacaoPendente2"><hl:message key="rotulo.nao"/></label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input mt-1" type="radio" name="comunicacaoPendente"
                                   id="comunicacaoPendente3" title='<hl:message key="rotulo.campo.todos.simples"/>'
                                   value="2" <% if (TextHelper.isNull(comunicacaoPendente) || comunicacaoPendente.equals("2")) { %>
                                   checked <% } %> <%if (desabilitado) {%> disabled <%} %>
                                   onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                                   onBlur="fout(this);ValidaMascaraV4(this);">
                            <label class="form-check-label labelSemNegrito ml-1 pr-4"
                                   for="comunicacaoPendente3"><hl:message key="rotulo.campo.todos.simples"/></label>
                        </div>
                    </div>
                </div>
            </div>
    </fieldset>
</div>

<% if (obrComunicacaoPendentePage.equals("true")) { %>
<script type="text/JavaScript">
    function funComunicacaoPendentePage() {
        camposObrigatorios = camposObrigatorios + 'comunicacaoPendente,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.comunicacao.pendentes"/>,';
    }

    addLoadEvent(funComunicacaoPendentePage);
</script>
<% } %>

<script type="text/JavaScript">
    function valida_campo_comunicacao_pendente() {
        return true;
    }
</script>
       