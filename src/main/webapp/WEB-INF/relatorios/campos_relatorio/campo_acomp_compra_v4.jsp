<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl" %>

<%
    AcessoSistema responsavelCompraPage = JspHelper.getAcessoSistema(request);
    String obrCompraPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
    boolean utilizaDiasUteis = ParamSist.getBoolParamSist(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, responsavelCompraPage);
    boolean temEtapaAprovacaoSaldo = ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavelCompraPage);

    String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
    boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true : false;
%>
<div class="col-sm-12">
    <fieldset>
        <div class="legend">
            <span><hl:message key="rotulo.relatorio.tipo.filtro"/></span>
        </div>
        <div class="row">
            <div class="col-sm-12 col-md-12">
                <div class="form-group mb-1" role="radiogroup" aria-labelledby="filtroConfiguravel">
                    <div class="form-check">
                        <input class="form-check-input ml-1" type="radio" name="filtroConfiguravel"
                               id="filtroConfiguravel0" value="0"
                               title='<hl:message key="rotulo.saldo.devedor.configuravel"/>'
                               onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                               disabled <%} %> <%=TextHelper.isNull(JspHelper.verificaVarQryStr(request, "filtroConfiguravel")) || JspHelper.verificaVarQryStr(request, "filtroConfiguravel").equals("0") ? "checked" : "" %>
                               onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                               onBlur="fout(this);ValidaMascaraV4(this);">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-3 col-sm-12 mb-2"
                               for="filtroConfiguravel0"><hl:message key="rotulo.saldo.devedor.configuravel"/></label>
                    </div>
                    <div class="form-check form-check">
                        <input class="form-check-input ml-1" type="radio" name="filtroConfiguravel"
                               id="filtroConfiguravel1" value="1"
                               title="<%=ApplicationResourcesHelper.getMessage("rotulo.apenas.contratos.pendencia.processo.compra", responsavelCompraPage)%>"
                               onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                               disabled <%} %> <%=JspHelper.verificaVarQryStr(request, "filtroConfiguravel").equals("1") ? "checked" : "" %>
                               onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                               onBlur="fout(this);ValidaMascaraV4(this);">
                        <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-3 col-sm-12"
                               for="filtroConfiguravel1"><hl:message
                                key="rotulo.apenas.contratos.pendencia.processo.compra"/></label>
                    </div>
                    <div class="form-check ml-0">
                        <input class="form-check-input ml-1 mt-3" type="radio" name="filtroConfiguravel"
                               id="filtroConfiguravel2" value="2"
                               title="<%=ApplicationResourcesHelper.getMessage("rotulo.apenas.contratos.bloqueados.processo.compra", responsavelCompraPage)%>"
                               onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                               disabled <%} %> <%=JspHelper.verificaVarQryStr(request, "filtroConfiguravel").equals("2") ? "checked" : "" %>
                               onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                               onBlur="fout(this);ValidaMascaraV4(this);">
                        <label class="form-check-label labelSemNegrito ml-1 mt-3 pr-2"
                               for="filtroConfiguravel2"><hl:message
                                key="rotulo.apenas.contratos.bloqueados.processo.compra"/></label>
                        <input class="form-control w-50 p-3" type="text" name="diasBloqueio" id="diasBloqueio"
                               value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasBloqueio") != null ? request.getParameter("diasBloqueio") : "0" )%>" <%if (desabilitado) { %>
                               disabled <%} %> size="2" maxlength="2"
                               onFocus="SetarEventoMascaraV4(this,'#D20',true);"
                               onBlur="fout(this);ValidaMascaraV4(this);"
                               placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelCompraPage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.dias", responsavelCompraPage))%>"/>
                    </div>
                </div>
            </div>
        </div>
</fieldset>

<fieldset>
    <div class="legend">
        <span><hl:message key="rotulo.relatorio.origem"/></span>
    </div>
    <div class="form-group" role="radiogroup" aria-labelledby="origem">
        <div class="form-check form-check-inline">
            <input class="form-check-input ml-1" type="radio" name="origem" id="origem0" value="0"
                   title="<%=ApplicationResourcesHelper.getMessage("rotulo.contratos.comprados.terceiros", responsavelCompraPage)%>"
                   onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                   disabled <%} %> <%=!JspHelper.verificaVarQryStr(request, "origem").equals("1") ? "checked" : "" %>
                   onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
            <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-3 col-sm-12" for="origem0"><hl:message
                    key="rotulo.contratos.comprados.terceiros"/></label>
        </div>
        <div class="form-check-inline form-check">
            <input class="form-check-input ml-1" type="radio" name="origem" id="origem1" value="1"
                   title="<%=ApplicationResourcesHelper.getMessage("rotulo.contratos.comprados", responsavelCompraPage) %>"
                   onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                   disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "origem").equals("1") ? "checked" : "" %>
                   onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
            <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-3 col-sm-12" for="origem1"><hl:message
                    key="rotulo.contratos.comprados"/></label>
        </div>
    </div>
</fieldset>

<fieldset>
    <div class="legend">
        <span><hl:message key="rotulo.saldo.devedor.valor.informado"/></span>
    </div>
    <div class="row">
        <div class="col-sm-12 col-md-12">
            <div class="form-group mb-1" role="radiogroup" aria-labelledby="temSaldoDevedor">
                <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1 mt-3" type="radio" name="temSaldoDevedor"
                           id="temSaldoDevedor1" value="SIM" title='<hl:message key="rotulo.sim"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "temSaldoDevedor").equals("SIM") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12 mt-3"
                           for="temSaldoDevedor1"><hl:message key="rotulo.sim"/></label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1 mb-3" type="radio" name="temSaldoDevedor"
                           id="temSaldoDevedor2" value="NAO" title='<hl:message key="rotulo.nao"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "temSaldoDevedor").equals("NAO") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito col-sm-12 ml-1 pr-4 mb-3"
                           for="temSaldoDevedor2"><hl:message
                            key="rotulo.nao"/></label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1" type="radio" name="temSaldoDevedor" id="temSaldoDevedor0"
                           value="TODOS" title='<hl:message key="rotulo.campo.todos.simples"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "temSaldoDevedor").equals("TODOS") || JspHelper.verificaVarQryStr(request, "temSaldoDevedor").equals("") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito ml-1 pr-4 col-sm-12"
                           for="temSaldoDevedor0"><hl:message key="rotulo.campo.todos.simples"/></label>
                </div>
            </div>
        </div>
        <div class="form-group col-sm-12 col-md-6">
            <% String saldoDevedorSemInformacao = (utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.uteis.sem.informacao", responsavelCompraPage) : ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.sem.informacao", responsavelCompraPage)); %>
            <label for="diasSemSaldoDevedor"><%= saldoDevedorSemInformacao %>
            </label>
            <input class="form-control mt-0 col-sm-3" type="text" name="diasSemSaldoDevedor"
                   id="diasSemSaldoDevedor" <%if (desabilitado) { %> disabled <%} %>
                   value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasSemSaldoDevedor") != null ? request.getParameter("diasSemSaldoDevedor") : "0" )%>"
                   size="2" maxlength="2" onFocus="SetarEventoMascaraV4(this,'#D20',true);"
                   onBlur="fout(this);ValidaMascaraV4(this);"
                   placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelCompraPage, saldoDevedorSemInformacao)%>"/>
        </div>
    </div>
</fieldset>

<% if (temEtapaAprovacaoSaldo) { %>
<fieldset>
    <div class="legend">
        <span><hl:message key="rotulo.saldo.devedor.valor.aprovado"/></span>
    </div>
    <div class="row">
        <div class="col-sm-12 col-md-12">
            <div class="form-group mb-1" role="radiogroup" aria-labelledby="saldoDevedorAprovado">
                <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1 mt-3" type="radio" name="saldoDevedorAprovado"
                           id="saldoDevedorAprovado1" value="SIM" title='<hl:message key="rotulo.sim"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "saldoDevedorAprovado").equals("SIM") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12 mt-3"
                           for="saldoDevedorAprovado1"><hl:message key="rotulo.sim"/></label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1 mb-3" type="radio" name="saldoDevedorAprovado"
                           id="saldoDevedorAprovado2" value="NAO" title='<hl:message key="rotulo.nao"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "saldoDevedorAprovado").equals("NAO") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito col-sm-12 ml-1 pr-4 mb-3"
                           for="saldoDevedorAprovado2"><hl:message key="rotulo.nao"/></label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1" type="radio" name="saldoDevedorAprovado"
                           id="saldoDevedorAprovado0" value="TODOS"
                           title='<hl:message key="rotulo.campo.todos.simples"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "saldoDevedorAprovado").equals("TODOS") || JspHelper.verificaVarQryStr(request, "saldoDevedorAprovado").equals("") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito ml-1 pr-4 col-sm-12"
                           for="saldoDevedorAprovado0"><hl:message
                            key="rotulo.campo.todos.simples"/></label>
                </div>
            </div>
        </div>
        <div class="form-group col-sm-12 col-md-6">
            <label for="diasSemAprovacaoSaldoDevedor"><%=(utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.uteis.sem.aprovacao", responsavelCompraPage) : ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.sem.aprovacao", responsavelCompraPage))%>
            </label>
            <input class="form-control mt-0 col-sm-3" type="text" name="diasSemAprovacaoSaldoDevedor"
                   id="diasSemAprovacaoSaldoDevedor" <%if (desabilitado) { %> disabled <%} %>
                   value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasSemAprovacaoSaldoDevedor") != null ? request.getParameter("diasSemAprovacaoSaldoDevedor") : "0" )%>"
                   size="2" maxlength="2" onFocus="SetarEventoMascaraV4(this,'#D20',true);"
                   onBlur="fout(this);ValidaMascaraV4(this);"/>
        </div>
    </div>
</fieldset>
<% } %>

<fieldset>
    <div class="legend">
        <span><hl:message key="rotulo.saldo.devedor.valor.informado.pago"/></span>
    </div>
    <div class="row">
        <div class="col-sm-12 col-md-12">
                <div class="form-group mb-1" role="radiogroup" aria-labelledby="saldoDevedorPago">
                    <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1 mt-3" type="radio" name="saldoDevedorPago"
                           id="saldoDevedorPago1" value="SIM" title='<hl:message key="rotulo.sim"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "saldoDevedorPago").equals("SIM") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12 mt-3"
                           for="saldoDevedorPago1"><hl:message key="rotulo.sim"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1 mb-3" type="radio" name="saldoDevedorPago"
                           id="saldoDevedorPago2" value="NAO" title='<hl:message key="rotulo.nao"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "saldoDevedorPago").equals("NAO") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito col-sm-12 ml-1 pr-4 mb-3"
                           for="saldoDevedorPago2"><hl:message key="rotulo.nao"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1" type="radio" name="saldoDevedorPago" id="saldoDevedorPago0"
                           value="TODOS" title='<hl:message key="rotulo.campo.todos.simples"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "saldoDevedorPago").equals("TODOS") || JspHelper.verificaVarQryStr(request, "saldoDevedorPago").equals("") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito ml-1 pr-4 col-sm-12"
                           for="saldoDevedorPago0"><hl:message key="rotulo.campo.todos.simples"/></label>
                </div>
            </div>
        </div>
        <div class="form-group col-sm-12 col-md-6">
            <label for="diasSemPagamentoSaldoDevedor"><%=(utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.uteis.sem.pagamento", responsavelCompraPage) : ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.sem.pagamento", responsavelCompraPage))%>
            </label>
            <input class="form-control mt-0 col-sm-3" type="text" name="diasSemPagamentoSaldoDevedor"
                   id="diasSemPagamentoSaldoDevedor" <%if (desabilitado) { %> disabled <%} %>
                   value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasSemPagamentoSaldoDevedor") != null ? request.getParameter("diasSemPagamentoSaldoDevedor") : "0" )%>"
                   size="2" maxlength="2" onFocus="SetarEventoMascaraV4(this,'#D20',true);"
                   onBlur="fout(this);ValidaMascaraV4(this);"/>
        </div>
    </div>
</fieldset>

<fieldset>
    <div class="legend">
        <span><hl:message key="rotulo.saldo.devedor.ade.liquidado"/></span>
    </div>
    <div class="row">
        <div class="col-sm-12 col-md-12">
                <div class="form-group mb-1" role="radiogroup" aria-labelledby="liquidado">
                    <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1 mt-3" type="radio" name="liquidado" id="liquidado1"
                           value="SIM" title='<hl:message key="rotulo.sim"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "liquidado").equals("SIM") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12 mt-3"
                           for="liquidado1"><hl:message key="rotulo.sim"/></label>
                </div>
                    <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1 mb-3" type="radio" name="liquidado" id="liquidado2"
                           value="NAO" title='<hl:message key="rotulo.nao"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %> <%= JspHelper.verificaVarQryStr(request, "liquidado").equals("NAO") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12"
                           for="liquidado2"><hl:message key="rotulo.nao"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                    <input class="form-check-input ml-1" type="radio" name="liquidado" id="liquidado0" value="TODOS"
                           title='<hl:message key="rotulo.campo.todos.simples"/>'
                           onClick="habilitaCamposCompraContrato()" <%if (desabilitado) { %>
                           disabled <%} %>  <%= JspHelper.verificaVarQryStr(request, "liquidado").equals("TODOS") || JspHelper.verificaVarQryStr(request, "liquidado").equals("") ? "checked" : "" %> />
                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12"
                           for="liquidado0"><hl:message key="rotulo.campo.todos.simples"/></label>
                </div>
            </div>
        </div>
        <div class="form-group col-sm-12 col-md-6">
            <% String saldoDevedorSemLiquidacao = (utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.uteis.sem.liquidacao", responsavelCompraPage) : ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.sem.liquidacao", responsavelCompraPage)); %>
            <label for="diasSemLiquidacao"><%= saldoDevedorSemLiquidacao %>
            </label>
            <input class="form-control mt-0 col-sm-3" type="text" name="diasSemLiquidacao"
                   id="diasSemLiquidacao" <%if (desabilitado) { %> disabled <%} %>
                   value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasSemLiquidacao") != null ? request.getParameter("diasSemLiquidacao") : "0" )%>"
                   size="2" maxlength="2" onFocus="SetarEventoMascaraV4(this,'#D20',true);"
                   onBlur="fout(this);ValidaMascaraV4(this);"
                   placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelCompraPage, saldoDevedorSemLiquidacao)%>"/>
        </div>
    </div>
</fieldset>
</div>

<script type="text/JavaScript">
    // Ajusta as op��es do formul�rio de acordo as op��es j� marcadas
    function habilitaCamposCompraContrato() {
        with (document.forms[0]) {
            if (getCheckedRadio('form1', 'filtroConfiguravel') != '0') {
                origem[0].disabled = true;
                origem[1].disabled = true;
                temSaldoDevedor[0].disabled = true;
                temSaldoDevedor[1].disabled = true;
                temSaldoDevedor[2].disabled = true;
                diasSemSaldoDevedor.disabled = true;
                <% if (temEtapaAprovacaoSaldo) { %>
                saldoDevedorAprovado[0].disabled = true;
                saldoDevedorAprovado[1].disabled = true;
                saldoDevedorAprovado[2].disabled = true;
                diasSemAprovacaoSaldoDevedor.disabled = true;
                <% } %>
                saldoDevedorPago[0].disabled = true;
                saldoDevedorPago[1].disabled = true;
                saldoDevedorPago[2].disabled = true;
                diasSemPagamentoSaldoDevedor.disabled = true;
                liquidado[0].disabled = true;
                liquidado[1].disabled = true;
                liquidado[2].disabled = true;
                diasSemLiquidacao.disabled = true;
                if (getCheckedRadio('form1', 'filtroConfiguravel') == '1') {
                    diasBloqueio.disabled = true;
                } else {
                    diasBloqueio.disabled = false;
                }
            } else {
                if (document.forms[0].csaCodigo != null && document.forms[0].csaCodigo.selectedIndex == 0) {
                    origem[0].disabled = true;
                    origem[1].disabled = true;
                } else {
                    origem[0].disabled = false;
                    origem[1].disabled = false;
                }
                diasBloqueio.disabled = true;
                temSaldoDevedor[0].disabled = false;
                temSaldoDevedor[1].disabled = false;
                temSaldoDevedor[2].disabled = false;
                <% if (temEtapaAprovacaoSaldo) { %>
                saldoDevedorAprovado[0].disabled = false;
                saldoDevedorAprovado[1].disabled = false;
                saldoDevedorAprovado[2].disabled = false;
                <% } %>
                saldoDevedorPago[0].disabled = false;
                saldoDevedorPago[1].disabled = false;
                saldoDevedorPago[2].disabled = false;
                liquidado[0].disabled = false;
                liquidado[1].disabled = false;
                liquidado[2].disabled = false;

                if (getCheckedRadio('form1', 'temSaldoDevedor') == 'NAO') {
                    diasSemSaldoDevedor.disabled = false;
                } else {
                    diasSemSaldoDevedor.disabled = true;
                }

                <% if (temEtapaAprovacaoSaldo) { %>
                if (getCheckedRadio('form1', 'saldoDevedorAprovado') == 'NAO') {
                    diasSemAprovacaoSaldoDevedor.disabled = false;
                } else {
                    diasSemAprovacaoSaldoDevedor.disabled = true;
                }
                <% } %>

                if (getCheckedRadio('form1', 'saldoDevedorPago') == 'NAO') {
                    diasSemPagamentoSaldoDevedor.disabled = false;
                } else {
                    diasSemPagamentoSaldoDevedor.disabled = true;
                }

                if (getCheckedRadio('form1', 'liquidado') == 'NAO') {
                    diasSemLiquidacao.disabled = false;
                } else {
                    diasSemLiquidacao.disabled = true;
                }
            }

            if (document.forms[0].csaCodigo != null) {
                if (document.forms[0].csaCodigo.selectedIndex == 0) {
                    origem[0].disabled = true;
                    origem[1].disabled = true;
                }
            }
        }
    }
     window.onload = habilitaCamposCompraContrato;
</script>

<% if (obrCompraPage.equals("true")) { %>
<script type="text/JavaScript">
    function funCompraPage() {
        camposObrigatorios = camposObrigatorios + 'filtroConfiguravel,';
        camposObrigatorios = camposObrigatorios + 'origem,';
        camposObrigatorios = camposObrigatorios + 'temSaldoDevedor,';
        camposObrigatorios = camposObrigatorios + 'diasSemSaldoDevedor,';
        <% if (temEtapaAprovacaoSaldo) { %>
        camposObrigatorios = camposObrigatorios + 'saldoDevedorAprovado,';
        camposObrigatorios = camposObrigatorios + 'diasSemAprovacaoSaldoDevedor,';
        <% } %>
        camposObrigatorios = camposObrigatorios + 'saldoDevedorPago,';
        camposObrigatorios = camposObrigatorios + 'diasSemPagamentoSaldoDevedor,';
        camposObrigatorios = camposObrigatorios + 'liquidado,';
        camposObrigatorios = camposObrigatorios + 'diasSemLiquidacao,';

        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.filtro"/>,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.origem"/>,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.condicional.saldo.devedor"/>,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.dias.sem.info.saldo.devedor"/>,';
        <% if (temEtapaAprovacaoSaldo) { %>
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.condicional.saldo.devedor.aprovado"/>,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.dias.sem.aprovacao.saldo.devedor"/>,';
        <% } %>
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.condicional.saldo.devedor.pago"/>,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.dias.sem.info.pagamento.saldo.devedor"/>,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.condicional.ade.liquidado"/>,';
        msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.dias.sem.liquidacao.ade"/>,';
    }

    window.onload = funCompraPage;
</script>
<% } %>

<script type="text/JavaScript">
    function valida_campo_acomp_compra() {
        return true;
    }
</script>
