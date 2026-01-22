<%--
* <p>Title: acompanharPortabilidade</p>
* <p>Description: Página de acompanhamento de contratos comprados no leiaute v4</p>
* <p>Copyright: Copyright (c) 2006</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

    String rotuloBotaoCancelar = ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar", responsavel);
    String rotuloBotaoPesquisar = ApplicationResourcesHelper.getMessage("rotulo.botao.pesquisar", responsavel);

    CustomTransferObject criteriosPesquisa = (CustomTransferObject) request.getAttribute("criteriosPesquisa");

    boolean utilizaDiasUteis = (boolean) request.getAttribute("utilizaDiasUteis");
    boolean temEtapaAprovacaoSaldo = (boolean) request.getAttribute("temEtapaAprovacaoSaldo");
    boolean filtroDataObrigatorio = (boolean) request.getAttribute("filtroDataObrigatorio");

    String csaCodigo = (String) request.getAttribute("csaCodigo");
    String corCodigo = (String) request.getAttribute("corCodigo");
    String orgCodigo = (String) request.getAttribute("orgCodigo");
    String pesquisar = (String) request.getAttribute("pesquisar");
    String filtroConfiguravel = (String) request.getAttribute("filtroConfiguravel");
    String tipoPeriodo = (String) request.getAttribute("tipoPeriodo");
    String filtroDataIni = (String) request.getAttribute("filtroDataIni");
    String filtroDataFim = (String) request.getAttribute("filtroDataFim");

//Se é usuário de CSE/ORG/SUP, monta uma lista de consignatárias
    List<TransferObject> consignatarias = null;
    if (responsavel.isCseSupOrg() && request.getAttribute("consignatarias") != null) {
        consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
    }

// Se é usuário de CSA ou de COR que pode acessar as consignações da consignatária, monta uma lista de correspondentes
    List<TransferObject> correspondentes = null;
    if (responsavel.isCsa() || (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) && request.getAttribute("correspondentes") != null) {
        correspondentes = (List<TransferObject>) request.getAttribute("correspondentes");
    }

%>
<c:set var="title">
    <hl:message key="rotulo.acompanhar.compra.contrato.titulo"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
    <form action="../v3/acompanharPortabilidade?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>"
          method="post" name="form1">
        <input type="hidden" name="pesquisar" value="true"/>
        <div class="card">
            <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="26">
            <use xlink:href="../img/sprite.svg#i-consultar"></use></svg>
        </span>
                <h2 class="card-header-title"><hl:message key="mensagem.informe.opcoes.pesquisa"/></h2>
            </div>
            <div class="card-body">
                <div class="row">
                    <c:if test="${consignatarias != null && !consignatarias.isEmpty()}">
                        <div class="form-group col-sm-12  col-md-6">
                            <label for="CSA_CODIGO"><hl:message key="rotulo.consignataria.singular"/></label>
                            <%=JspHelper.geraCombo(consignatarias, "CSA_CODIGO", Columns.CSA_CODIGO, Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control form-select\"", false, 1, JspHelper.verificaVarQryStr(request, "CSA_CODIGO"), "changeForm()")%>
                        </div>
                    </c:if>

                    <c:if test="${correspondentes != null && !correspondentes.isEmpty()}">
                        <div class="form-group col-sm-12  col-md-6">
                            <label for="COR_CODIGO"><hl:message key="rotulo.correspondente.singular"/></label>
                            <%=JspHelper.geraCombo(correspondentes, "COR_CODIGO", Columns.COR_CODIGO, Columns.COR_IDENTIFICADOR + ";" + Columns.COR_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control form-select\"", false, 1, JspHelper.verificaVarQryStr(request, "COR_CODIGO"), "changeForm()")%>
                        </div>
                    </c:if>
                </div>
                <fieldset>
                    <div class="legend">
                        <span><hl:message key="rotulo.acompanhamento.tipo.filtro"/></span>
                    </div>
                    <div class="row">
                        <div class="col-sm-12 col-md-12">
                            <div class="form-group mb-1" role="radiogroup" aria-labelledby="filtroConfiguravel">
                                <div class="form-check">
                                    <input type="radio" class="form-check-input ml-1" id="cbTDFConfiguravel"
                                           name="filtroConfiguravel" value="0"
                                           onclick="changeForm()" <%=TextHelper.isNull(JspHelper.verificaVarQryStr(request, "filtroConfiguravel")) || JspHelper.verificaVarQryStr(request, "filtroConfiguravel").equals("0") ? "checked" : "" %>
                                           onFocus="SetarEventoMascara(this,'#*100',true);"
                                           onBlur="fout(this);ValidaMascara(this);">
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-3 col-sm-12 mb-2"
                                           for="cbTDFConfiguravel"><hl:message
                                            key="rotulo.saldo.devedor.configuravel"/></label>
                                </div>
                                <div class="form-check">
                                    <input type="radio" class="form-check-input ml-1" id="cbTDFContratoPendencia"
                                           name="filtroConfiguravel" value="1"
                                           onclick="changeForm()" <%=JspHelper.verificaVarQryStr(request, "filtroConfiguravel").equals("1") ? "checked" : "" %>
                                           onFocus="SetarEventoMascara(this,'#*100',true);"
                                           onBlur="fout(this);ValidaMascara(this);">
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12"
                                           for="cbTDFContratoPendencia"><hl:message
                                            key="rotulo.apenas.contratos.pendencia.processo.compra"/></label>
                                </div>
                                    <div class="form-check ml-0">
                                        <input class="form-check-input ml-1 mt-3" type="radio"
                                               id="cbTDFContratoBloqueado" name="filtroConfiguravel" value="2"
                                               onclick="changeForm()" <%=JspHelper.verificaVarQryStr(request, "filtroConfiguravel").equals("2") ? "checked" : "" %>
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label labelSemNegrito ml-1 mt-3 pr-2"
                                               for="cbTDFContratoBloqueado"><hl:message
                                                key="rotulo.apenas.contratos.bloqueados.processo.compra"/></label>
                                        <input class="form-control w-50 p-3" type="text" name="diasBloqueio"
                                               value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasBloqueio") != null ? request.getParameter("diasBloqueio") : "0" )%>"
                                               size="2" maxlength="2" id="diasBloqueio"
                                               placeholder="<%=(String) ApplicationResourcesHelper.getMessage("rotulo.relatorio.dias", responsavel)%>"
                                               aria-label="<%=(String) ApplicationResourcesHelper.getMessage("rotulo.apenas.contratos.bloqueados.processo.compra", responsavel)%>">
                                    </div>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <fieldset>
                    <div class="legend">
                        <span><hl:message key="rotulo.acompanhamento.origem"/></span>
                    </div>
                    <div class="row">
                        <div class="col-sm-12 col-md-12">
                            <div class="form-group mb-1" role="radiogroup" aria-labelledby="origem">
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input ml-1" type="radio" name="origem"
                                           id="cbOContratoDestaEnt" value="0"
                                           onClick="changeForm()" <%=!JspHelper.verificaVarQryStr(request, "origem").equals("1") ? "checked" : "" %>
                                           onFocus="SetarEventoMascara(this,'#*100',true);"
                                           onBlur="fout(this);ValidaMascara(this);">
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-3 col-sm-12"
                                           for="cbOContratoDestaEnt"><hl:message
                                            key="rotulo.contratos.comprados.terceiros"/></label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input ml-1" type="radio" name="origem"
                                           id="cbOContratoOutraEnt" value="1"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "origem").equals("1") ? "checked" : "" %>
                                           onFocus="SetarEventoMascara(this,'#*100',true);"
                                           onBlur="fout(this);ValidaMascara(this);">
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-3 col-sm-12"
                                           for="cbOContratoOutraEnt"><hl:message
                                            key="rotulo.contratos.comprados"/></label>
                                </div>
                            </div>
                        </div>
                    </div>
                </fieldset>

                <fieldset>
                    <div class="legend">
                        <span><hl:message key="rotulo.acompanhamento.saldo.devedor.informado"/></span>
                    </div>
                    <div class="row">
                        <div class="col-sm-12 col-md-12">
                            <div class="form-group mb-1" role="radiogroup" aria-labelledby="temSaldoDevedor">
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input ml-1 mt-3" type="radio" name="temSaldoDevedor"
                                           id="cbSDISim" value="SIM"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "temSaldoDevedor").equals("SIM") ? "checked" : "" %> />
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12 mt-3"
                                           for="cbSDISim"><hl:message key="rotulo.sim"/></label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input  ml-1 mb-3" type="radio" name="temSaldoDevedor"
                                           id="cbSDINao" value="NAO"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "temSaldoDevedor").equals("NAO") ? "checked" : "" %> />
                                    <label class="form-check-label labelSemNegrito col-sm-12 ml-1 pr-4 mb-3"
                                           for="cbSDINao"><hl:message key="rotulo.nao"/></label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input ml-1" type="radio" name="temSaldoDevedor"
                                           id="cbSDITodos" value="TODOS"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "temSaldoDevedor").equals("TODOS") || JspHelper.verificaVarQryStr(request, "temSaldoDevedor").equals("") ? "checked" : "" %> />
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4 col-sm-12"
                                           for="cbSDITodos"><hl:message key="rotulo.campo.todos.simples"/></label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group col-sm-12  col-md-6">
                            <label for="diasSemSaldoDevedor"><%=utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.uteis.sem.informacao", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.sem.informacao", responsavel)%>
                            </label>
                            <input type="text" class="form-control" id="diasSemSaldoDevedor" name="diasSemSaldoDevedor"
                                   placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.dias.sem.informarcao.saldo.devedor", responsavel)%>"
                                   value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasSemSaldoDevedor") != null ? request.getParameter("diasSemSaldoDevedor") : "0" )%>"
                                   class="EditMinusculo" size="2" maxlength="2"
                                   onFocus="SetarEventoMascara(this,'#D20',true);"
                                   onBlur="fout(this);ValidaMascara(this);"/>
                        </div>
                    </div>
                </fieldset>

                <c:if test="${temEtapaAprovacaoSaldo}">
                    <fieldset>
                        <div class="legend">
                            <span><hl:message key="rotulo.acompanhamento.saldo.devedor.aprovado"/></span>
                        </div>
                        <div class="row">
                            <div class="col-sm-12 col-md-12">
                                <div class="form-group mb-1" role="radiogroup" aria-labelledby="saldoDevedorAprovado">
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1 mt-3" type="radio"
                                               name="saldoDevedorAprovado" id="cbSDAPSim" value="SIM"
                                               onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "saldoDevedorAprovado").equals("SIM") ? "checked" : "" %> />
                                        <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12 mt-3"
                                               for="cbSDAPSim"><hl:message key="rotulo.sim"/></label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input  ml-1" type="radio" name="saldoDevedorAprovado"
                                               id="cbSDAPNao" value="NAO"
                                               onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "saldoDevedorAprovado").equals("NAO") ? "checked" : "" %> />
                                        <label class="form-check-label labelSemNegrito col-sm-12 ml-1 pr-4 mb-3"
                                               for="cbSDAPNao"><hl:message key="rotulo.nao"/></label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="saldoDevedorAprovado"
                                               id="cbSDAPTodos" value="TODOS"
                                               onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "saldoDevedorAprovado").equals("TODOS") || JspHelper.verificaVarQryStr(request, "saldoDevedorAprovado").equals("") ? "checked" : "" %> />
                                        <label class="form-check-label labelSemNegrito ml-1 pr-4  col-sm-12"
                                               for="cbSDAPTodos"><hl:message key="rotulo.campo.todos.simples"/></label>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group col-sm-12  col-md-6">
                                <label for="diasSemAprovacaoSaldoDevedor"><%=utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.uteis.sem.aprovacao", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.sem.aprovacao", responsavel)%>
                                </label>
                                <input type="text" class="form-control" id="diasSemAprovacaoSaldoDevedor"
                                       name="diasSemAprovacaoSaldoDevedor"
                                       placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.dias.sem.informarcao.saldo.devedor", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasSemAprovacaoSaldoDevedor") != null ? request.getParameter("diasSemAprovacaoSaldoDevedor") : "0" )%>"
                                       class="EditMinusculo" size="2" maxlength="2"
                                       onFocus="SetarEventoMascara(this,'#D20',true);"
                                       onBlur="fout(this);ValidaMascara(this);"/>
                            </div>
                        </div>
                    </fieldset>
                </c:if>

                <fieldset>
                    <div class="legend">
                        <span><hl:message key="rotulo.acompanhamento.saldo.devedor.informado.pago"/></span>
                    </div>
                    <div class="row">
                        <div class="col-sm-12 col-md-12">
                            <div class="form-group mb-1" role="radiogroup" aria-labelledby="saldoDevedorPago">
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input ml-1 mt-3" type="radio" name="saldoDevedorPago"
                                           id="cbSDICPSim" value="SIM"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "saldoDevedorPago").equals("SIM") ? "checked" : "" %> />
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12 mt-3"
                                           for="cbSDICPSim"><hl:message key="rotulo.sim"/></label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input  ml-1" type="radio" name="saldoDevedorPago"
                                           id="cbSDICPNao" value="NAO"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "saldoDevedorPago").equals("NAO") ? "checked" : "" %> />
                                    <label class="form-check-label labelSemNegrito col-sm-12 ml-1 pr-4 mb-3"
                                           for="cbSDICPNao"><hl:message key="rotulo.nao"/></label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input ml-1" type="radio" name="saldoDevedorPago"
                                           id="cbSDICPTodos" value="TODOS"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "saldoDevedorPago").equals("TODOS") || JspHelper.verificaVarQryStr(request, "saldoDevedorPago").equals("") ? "checked" : "" %> />
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4  col-sm-12"
                                           for="cbSDICPTodos"><hl:message key="rotulo.campo.todos.simples"/></label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group col-sm-12  col-md-6">
                            <label for="diasSemPagamentoSaldoDevedor"><%=utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.uteis.sem.pagamento", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.sem.pagamento", responsavel)%>
                            </label>
                            <input type="text" class="form-control" id="diasSemPagamentoSaldoDevedor"
                                   name="diasSemPagamentoSaldoDevedor"
                                   placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.dias.sem.informarcao.saldo.devedor", responsavel)%>"
                                   value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasSemPagamentoSaldoDevedor") != null ? request.getParameter("diasSemPagamentoSaldoDevedor") : "0" )%>"
                                   class="EditMinusculo" size="2" maxlength="2"
                                   onFocus="SetarEventoMascara(this,'#D20',true);"
                                   onBlur="fout(this);ValidaMascara(this);"/>
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
                                    <input class="form-check-input ml-1 mt-3" type="radio" name="liquidado" id="cbCLSim"
                                           value="SIM"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "liquidado").equals("SIM") ? "checked" : "" %> />
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4 pb-2 col-sm-12 mt-3"
                                           for="cbCLSim"><hl:message key="rotulo.sim"/></label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input  ml-1" type="radio" name="liquidado" id="cbCLNao"
                                           value="NAO"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "liquidado").equals("NAO") ? "checked" : "" %> />
                                    <label class="form-check-label labelSemNegrito col-sm-12 ml-1 pr-4 mb-3"
                                           for="cbCLNao"><hl:message key="rotulo.nao"/></label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input ml-1" type="radio" name="liquidado" id="cbCLTodos"
                                           value="TODOS"
                                           onClick="changeForm()" <%= JspHelper.verificaVarQryStr(request, "liquidado").equals("TODOS") || JspHelper.verificaVarQryStr(request, "liquidado").equals("") ? "checked" : "" %> />
                                    <label class="form-check-label labelSemNegrito ml-1 pr-4 col-sm-12" for="cbCLTodos"><hl:message
                                            key="rotulo.campo.todos.simples"/></label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group col-sm-12  col-md-6">
                            <label for="diasSemLiquidacao"><%=utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.uteis.sem.liquidacao", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.dias.sem.liquidacao", responsavel)%>
                            </label>
                            <input type="text" class="form-control" id="NumDiasContratoLiquidado"
                                   name="diasSemLiquidacao"
                                   placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.dias.sem.informarcao.saldo.devedor", responsavel)%>"
                                   value="<%=TextHelper.forHtmlAttribute( request.getParameter("diasSemLiquidacao") != null ? request.getParameter("diasSemLiquidacao") : "0" )%>"
                                   class="EditMinusculo" size="2" maxlength="2"
                                   onFocus="SetarEventoMascara(this,'#D20',true);"
                                   onBlur="fout(this);ValidaMascara(this);"/>
                        </div>
                    </div>
                </fieldset>

                <fieldset>
                    <div class="legend">
                        <span id="iData"><hl:message key="rotulo.consignacao.data"/></span>
                    </div>
                    <div class="row">
                        <div class="form-group col-sm-12 col-md-12">
                            <div class="row">
                                <!--                 <div class="form-group col-sm-12  col-md-12"> -->
                                <!--                   <label for="iTipo">Tipo</label> -->
                                <div class="form-group col-sm-6">
                                    <SELECT name="tipoPeriodo" class="form-control form-select" id="tipoPeriodo">
                                        <OPTION VALUE="T" <%=(String) (tipoPeriodo != null && tipoPeriodo.equals("T") ? "SELECTED" : "")%>>
                                            <hl:message key="rotulo.campo.todos"/></OPTION>
                                        <OPTION VALUE="IC"<%=(String) (tipoPeriodo == null || (tipoPeriodo != null && tipoPeriodo.equals("IC")) ? "SELECTED" : "")%>>
                                            <hl:message key="rotulo.inclusao.compra"/></OPTION>
                                        <OPTION VALUE="I" <%=(String) (tipoPeriodo != null && tipoPeriodo.equals("I") ? "SELECTED" : "")%>>
                                            <hl:message key="rotulo.acompanhamento.informacao.saldo.devedor"/></OPTION>
                                        <OPTION VALUE="A" <%=(String) (tipoPeriodo != null && tipoPeriodo.equals("A") ? "SELECTED" : "")%>>
                                            <hl:message key="rotulo.acompanhamento.aprovacao.saldo.devedor"/></OPTION>
                                        <OPTION VALUE="P" <%=(String) (tipoPeriodo != null && tipoPeriodo.equals("P") ? "SELECTED" : "")%>>
                                            <hl:message key="rotulo.acompanhamento.pagamento.saldo.devedor"/></OPTION>
                                        <OPTION VALUE="L" <%=(String) (tipoPeriodo != null && tipoPeriodo.equals("L") ? "SELECTED" : "")%>>
                                            <hl:message key="rotulo.acompanhamento.liquidacao"/></OPTION>
                                    </SELECT>
                                </div>
                                <div class="col-sm-1">
                                    <div class="text-center align-middle mt-4 form-control-label">
                                        <label for="periodoIni"><hl:message key="rotulo.data.de"/></label>
                                    </div>
                                </div>
                                <div class="form-group col-sm-2">
                                    <hl:htmlinput name="periodoIni" di="periodoIni" type="text"
                                                  classe="form-control w-100"
                                                  placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
                                                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                                  value="<%=TextHelper.forHtmlAttribute(filtroDataIni)%>"/>
                                </div>

                                <div class="col-sm-1">
                                    <div class="text-center align-middle mt-4 form-control-label">
                                        <label for="periodoFim"><hl:message key="rotulo.data.ate"/></label>
                                    </div>
                                </div>
                                <div class="form-group col-sm-2">
                                    <hl:htmlinput name="periodoFim" di="periodoFim" type="text"
                                                  classe="form-control w-100"
                                                  placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"
                                                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                                  value="<%=TextHelper.forHtmlAttribute(filtroDataFim)%>"/>
                                </div>
                            </div>

                        </div>
                    </div>
                </fieldset>

                <fieldset>
                    <div class="legend">
                        <span id="iAde"><hl:message key="rotulo.dados.consignacao.titulo"/></span>
                    </div>
                    <div class="row">
                        <div class="form-group col-sm-12  col-md-6">
                            <label for="iNade"><hl:message key="rotulo.consignacao.numero"/></label>
                            <hl:htmlinput name="ADE_NUMERO" type="text"
                                          classe="form-control w-100" di="ADE_NUMERO"
                                          mask="#*20" size="8"
                                          value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"ADE_NUMERO\"))%>"
                                          placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero", responsavel)%>'
                            />
                        </div>
                    </div>
                </fieldset>

                <fieldset>
                    <div class="legend">
                        <span id="iServidor"><hl:message key="rotulo.servidor.dados"/></span>
                    </div>
                    <div class="row">
                        <div class="form-group col-sm-12  col-md-6">
                            <hl:campoMatriculav4
                                    placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>'/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="form-group col-sm-12  col-md-6">
                            <hl:campoCPFv4
                                    placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>'
                                    classe="form-control"/>
                        </div>
                    </div>
                </fieldset>
            </div>
        </div>
    </form>

    <div class="btn-action">
        <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back"
           onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
        <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="validaSubmit()">
            <svg width="20">
                <use xlink:href="../img/sprite.svg#i-consultar"></use>
            </svg>
            <%=TextHelper.forHtmlAttribute(rotuloBotaoPesquisar)%>
        </a>
    </div>

    <hl:listaAcompanhamentoComprav4
            csaCodigo="<%=TextHelper.forHtmlAttribute(csaCodigo)%>"
            orgCodigo="<%=TextHelper.forHtmlAttribute(orgCodigo)%>"
            corCodigo="<%=TextHelper.forHtmlAttribute(corCodigo)%>"
            pesquisar="<%=TextHelper.forHtmlAttribute(pesquisar)%>"
            filtroConfiguravel="<%=TextHelper.forHtmlAttribute(filtroConfiguravel)%>"
            criteriosPesquisa="<%=(CustomTransferObject)(criteriosPesquisa)%>"
            linkPaginacao="${linkPaginacao}"
            offset="${offset2}"
            reusePageToken="true"
    />

</c:set>
<c:set var="pageModals">
    <t:modalSubAcesso>
        <jsp:attribute name="titulo"><hl:message key="rotulo.historico.liq.antecipada.titulo"/></jsp:attribute>
    </t:modalSubAcesso>
</c:set>

<c:set var="javascript">
    <script type="text/JavaScript">
        var f0 = document.forms[0];

        function formLoad() {
            changeForm();
        }

        // ajusta as opções do formulário de acordo as opções já marcadas
        function changeForm() {
            with (f0) {
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
                    if (f0.CSA_CODIGO != null && f0.CSA_CODIGO.selectedIndex == 0) {
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

                if (document.forms[0].CSA_CODIGO != null) {
                    if (CSA_CODIGO.selectedIndex == 0) {
                        origem[0].disabled = true;
                        origem[1].disabled = true;
                    }
                }
            }
        }

        // valida o formulário antes do envio do submit
        function validForm() {
            var msg = '';
            with (document.form1) {
                if (!diasSemSaldoDevedor.disabled && diasSemSaldoDevedor.value == '') {
                    msg += '<%=utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("mensagem.informe.dias.uteis.sem.info.saldo.devedor", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informe.dias.sem.info.saldo.devedor", responsavel)%>\n';
                }
                <% if (temEtapaAprovacaoSaldo) { %>
                if (!diasSemAprovacaoSaldoDevedor.disabled && diasSemAprovacaoSaldoDevedor.value == '') {
                    msg += '<%=utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("mensagem.informe.dias.uteis.sem.aprovacao.saldo.devedor", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informe.dias.sem.aprovacao.saldo.devedor", responsavel)%>\n';
                }
                <% } %>
                if (!diasSemPagamentoSaldoDevedor.disabled && diasSemPagamentoSaldoDevedor.value == '') {
                    msg += '<%=utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("mensagem.informe.dias.uteis.sem.info.pagamento.saldo.devedor", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informe.dias.sem.info.pagamento.saldo.devedor", responsavel)%>\n';
                }
                if (!diasSemLiquidacao.disabled && diasSemLiquidacao.value == '') {
                    msg += '<%=utilizaDiasUteis ? ApplicationResourcesHelper.getMessage("mensagem.informe.dias.uteis.sem.liquidacao.ade", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informe.dias.sem.liquidacao.ade", responsavel)%>\n';
                }
                <% if (filtroDataObrigatorio) { %>
                if (!periodoIni.disabled && !periodoFim.disabled) {
                    if (periodoIni.value == '' || periodoFim.value == '') {
                        alert('<hl:message key="mensagem.informe.data"/>\n');
                        if (periodoIni.value == '') {
                            periodoIni.focus();
                        } else {
                            periodoFim.focus();
                        }
                        return false;
                    } else {
                        if (!verificaData(periodoIni.value)) {
                            periodoIni.focus();
                            return false;
                        }
                        if (!verificaData(periodoFim.value)) {
                            periodoFim.focus();
                            return false;
                        }

                        // valida se as datas estão preenchidas corretamente
                        var PartesData = new Array();
                        PartesData = obtemPartesData(f0.periodoIni.value);
                        var DiaIni = PartesData[0];
                        var MesIni = PartesData[1];
                        var AnoIni = PartesData[2];
                        PartesData = obtemPartesData(f0.periodoFim.value);
                        var DiaFim = PartesData[0];
                        var MesFim = PartesData[1];
                        var AnoFim = PartesData[2];
                        if (!VerificaPeriodoExt(DiaIni, MesIni, AnoIni, DiaFim, MesFim, AnoFim, 30)) {
                            periodoIni.focus();
                            return false;
                        }
                    }
                }
                <% } %>
            }
            if (msg != '') {
                alert(msg);
                return false;
            } else {
                return true;
            }
        }

        function validaSubmit() {
            if (validForm()) {
                if (typeof vfRseMatricula === 'function') {
                    if (vfRseMatricula(true)) {
                        f0.submit();
                    }
                } else {
                    f0.submit();
                }
            }
        }

        window.onload = formLoad;
    </script>
</c:set>

<t:page_v4>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>