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
    <legend class="legend pt-2"><span><hl:message key="rotulo.decisao.judicial.titulo"/></span></legend>
    <div class="row form-group">
        <div><span id="descricao"><hl:message key="rotulo.decisao.listar.judicial"/></span></div>
        <div class=" mb-1" role="radiogroup">
            <div class="form-check form-check-inline">
                <input class="form-check-input ml-1" type="radio" name="tmoDecisaoJudicial" id="judicialSim"
                       title='<hl:message key="rotulo.sim"/>'
                       value="true" <%= JspHelper.verificaVarQryStr(request, "judicial").equals("true") ? "checked" : "" %>
                       onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);"
                       onChange="habilitaDesabilitaAgendamento();" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="judicialSim"><hl:message
                        key="rotulo.sim"/></label>
            </div>
            <div class="form-check form-check-inline">
                <input class="form-check-input ml-1" type="radio" name="tmoDecisaoJudicial" id="judicialNao"
                       title='<hl:message key="rotulo.nao"/>'
                       value="false" <%=!JspHelper.verificaVarQryStr(request, "judicial").equals("true") ? "checked" : "" %>
                       onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);"
                       onChange="habilitaDesabilitaAgendamento();" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="judicialNao"><hl:message
                        key="rotulo.nao"/></label>
            </div>
        </div>
    </div>
</fieldset>
<script type="text/JavaScript">
    <%if (obrAgdPage.equals("true")) {%>

    function funAgdPage() {
        camposObrigatorios = camposObrigatorios + 'tmoDecisaoJudicial,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.decisao.judicial"/>,';
    }

    addLoadEvent(funAgdPage);
    <%}%>

    function valida_campo_judicial() {
        return true;
    }
</script>
