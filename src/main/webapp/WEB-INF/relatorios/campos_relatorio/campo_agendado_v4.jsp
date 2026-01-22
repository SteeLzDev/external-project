<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%
    AcessoSistema responsavelAgdPage = JspHelper.getAcessoSistema(request);
    String obrAgdPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true : false;
%>
<fieldset class="col-sm-12 col-md-12">
    <legend class="legend pt-2"><span><hl:message key="rotulo.relatorio.titulo.configuracao.agendamento"/></span>
    </legend>
    <div class="row">
        <div class="form-group col-sm-12 col-md-6">
            <div>
                <span id="agDescricao">${descricoes[recurso]}</span>
            </div>
            <div class="form-group mb-1">
                <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1" type="radio" name="agendado" id="agendadoSim"
                           title='<hl:message key="rotulo.sim"/>'
                           value="true" <%= JspHelper.verificaVarQryStr(request, "agendado").equals("true") ? "checked" : "" %>
                           onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);"
                           onChange="habilitaDesabilitaAgendamento();" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                    <label class="form-check-label labelSemNegrito ml-1 pr-4" for="agendadoSim"><hl:message
                            key="rotulo.sim"/></label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1" type="radio" name="agendado" id="agendadoNao"
                           title='<hl:message key="rotulo.nao"/>'
                           value="false" <%=!JspHelper.verificaVarQryStr(request, "agendado").equals("true") ? "checked" : "" %>
                           onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);"
                           onChange="habilitaDesabilitaAgendamento();" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                    <label class="form-check-label labelSemNegrito ml-1 pr-4" for="agendadoNao"><hl:message
                            key="rotulo.nao"/></label>
                </div>
            </div>
        </div>
    </div>
</fieldset>
<script type="text/JavaScript">
    <%if (obrAgdPage.equals("true")) {%>

    function funAgdPage() {
        camposObrigatorios = camposObrigatorios + 'agendado,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.agendado"/>,';
    }

    addLoadEvent(funAgdPage);
    <%}%>

    function valida_campo_agendado() {
        return true;
    }
</script>
