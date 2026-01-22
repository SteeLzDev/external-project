<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.web.ParamSession" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros" %>
<%@ page import="com.zetra.econsig.persistence.entity.Beneficiario" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");

    CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");

    Set<Integer> prazosPossiveis = (Set<Integer>) request.getAttribute("prazosPossiveis");
    int prazo = (Integer) request.getAttribute("prazo");
    int maxPrazo = (Integer) request.getAttribute("maxPrazo");
    boolean djuRevogada = (Boolean) request.getAttribute("djuRevogado");
    boolean permiteVlrNegativo = (Boolean) request.getAttribute("permiteVlrNegativo");
    boolean permiteCadVlrTac = (Boolean) request.getAttribute("permiteCadVlrTac");
    boolean permiteCadVlrIof = (Boolean) request.getAttribute("permiteCadVlrIof");
    boolean permiteCadVlrLiqLib = (Boolean) request.getAttribute("permiteCadVlrLiqLib");
    boolean permiteCadVlrMensVinc = (Boolean) request.getAttribute("permiteCadVlrMensVinc");
    boolean permiteAumentarVlr = (Boolean) request.getAttribute("permiteAumentarVlr");
    boolean permiteReduzirVlr = (request.getAttribute("permiteReduzirVlr") == null || (Boolean) request.getAttribute("permiteReduzirVlr"));
    boolean permiteAumentarPrz = (Boolean) request.getAttribute("permiteAumentarPrz");
    boolean exigeModalidadeOperacao = (Boolean) request.getAttribute("exigeModalidadeOperacao");
    boolean exigeMatriculaSerCsa = (Boolean) request.getAttribute("exigeMatriculaSerCsa");
    boolean exigeMotivoOperacao = (Boolean) request.getAttribute("exigeMotivoOperacao");
    boolean exibeMotivoOperacao = (Boolean) request.getAttribute("exibeMotivoOperacao");
    boolean usuPossuiAltAvancadaAde = (Boolean) request.getAttribute("usuPossuiAltAvancadaAde");
    boolean usuarioPodeAnexarAlteracao = (Boolean) request.getAttribute("usuarioPodeAnexarAlteracao");
    String exigeSenhaServidor = (String) request.getAttribute("exigeSenhaServidor");
    boolean mensagemMargemComprometida = (Boolean) request.getAttribute("mensagemMargemComprometida");
    String mensagem = (String) request.getAttribute("mensagem");
    boolean paramSvcTaxa = (Boolean) request.getAttribute("paramSvcTaxa");
    boolean permiteQualquerPrazo = (Boolean) request.getAttribute("permiteQualquerPrazo");
    boolean servicoCompulsorio = (Boolean) request.getAttribute("servicoCompulsorio");
    String svcCodigo = (String) request.getAttribute("svcCodigo");
    String adeCodigo = (String) request.getAttribute("adeCodigo");
    String rseCodigo = (String) request.getAttribute("rseCodigo");
    String fileName = (String) request.getAttribute("fileName");
    Set<Date> periodos = (Set<Date>) request.getAttribute("periodos");
    String rotuloPeriodicidadePrazo = (String) request.getAttribute("rotuloPeriodicidadePrazo");
    Integer valorAdeCarencia = (Integer) request.getAttribute("valorAdeCarencia");
    boolean podeAlterarCarencia = (Boolean) request.getAttribute("podeAlterarCarencia");
    boolean serInfBancariaObrigatoria = (Boolean) request.getAttribute("serInfBancariaObrigatoria");
    String adeVlrPadrao = (String) request.getAttribute("adeVlrPadrao");
    boolean retemMargemSvcPercentual = (Boolean) request.getAttribute("retemMargemSvcPercentual");
    String labelTipoVlr = (String) request.getAttribute("labelTipoVlr");
    String tipoVlr = (String) request.getAttribute("tipoVlr");
    BigDecimal adeVlrPercentual = (BigDecimal) request.getAttribute("adeVlrPercentual");
    BigDecimal adeVlr = (BigDecimal) request.getAttribute("adeVlr");
    boolean alteraAdeVlr = (Boolean) request.getAttribute("alteraAdeVlr");
    String svcPrioridade = (String) request.getAttribute("svcPrioridade");
    int prazoRest = (Integer) request.getAttribute("prazoRest");
    boolean permiteAlterarVlrLiberado = (Boolean) request.getAttribute("permiteAlterarVlrLiberado");
    boolean boolTpsSegPrestamista = (Boolean) request.getAttribute("boolTpsSegPrestamista");
    boolean permiteVlrLiqTxJuros = (Boolean) request.getAttribute("permiteVlrLiqTxJuros");
    boolean temCET = (Boolean) request.getAttribute("temCET");
    boolean identificadorAdeObrigatorio = (Boolean) request.getAttribute("identificadorAdeObrigatorio");
    String mascaraAdeIdentificador = (String) request.getAttribute("mascaraAdeIdentificador");
    String tdaModalidadeOp = (String) request.getAttribute("tdaModalidadeOp");
    List<TransferObject> tdaList = (List<TransferObject>) request.getAttribute("tdaList");
    Map<String, String> dadosAutorizacao = (Map<String, String>) request.getAttribute("dadosAutorizacao");
    String tdaMatriculaCsa = (String) request.getAttribute("tdaMatriculaCsa");
    List statusAutorizacao = (List) request.getAttribute("statusAutorizacao");
    Short intFolha = (Short) request.getAttribute("intFolha");
    List margens = (List) request.getAttribute("margens");
    List lstMtvOperacao = (List) request.getAttribute("lstMtvOperacao");
    List lstTipoJustica = (List) request.getAttribute("lstTipoJustica");
    Short incMargem = (Short) request.getAttribute("incMargem");
    String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");
    Short adeIntFolha = (Short) request.getAttribute("adeIntFolha");
    Short adeIncMargem = (Short) request.getAttribute("adeIncMargem");
    ParamSession paramSession = (ParamSession) request.getAttribute("paramSession");
    boolean permitePrazoMaiorContSer = (Boolean) request.getAttribute("permitePrazoMaiorContSer");
    String adeVlrAtual = (String) request.getAttribute("adeVlrAtual");

    // Exibição e obrigatoriedade campos
    Boolean exibirNovoValor = request.getAttribute("exibirNovoValor") != null ? (Boolean) request.getAttribute("exibirNovoValor") : true;
    Boolean exibirInfoBancaria = request.getAttribute("exibirInfoBancaria") != null ? (Boolean) request.getAttribute("exibirInfoBancaria") : true;
    Boolean exibirNumPrestacoesRest = request.getAttribute("exibirNumPrestacoesRest") != null? (Boolean) request.getAttribute("exibirNumPrestacoesRest") : true;
    Boolean exibirNovoValorLiq = request.getAttribute("exibirNovoValorLiq") != null ? (Boolean) request.getAttribute("exibirNovoValorLiq") : true;
    Boolean exibirCadVlrTac = request.getAttribute("exibirCadVlrTac") != null ? (Boolean) request.getAttribute("exibirCadVlrTac") : true;
    Boolean exibirCadVlrIof =  request.getAttribute("exibirCadVlrIof") != null ? (Boolean) request.getAttribute("exibirCadVlrIof") : true;
    Boolean exibirCadVlrMensVinc = request.getAttribute("exibirCadVlrMensVinc") != null ? (Boolean) request.getAttribute("exibirCadVlrMensVinc") : true;
    Boolean exibirSegPrestamista = request.getAttribute("exibirSegPrestamista") != null ? (Boolean) request.getAttribute("exibirSegPrestamista") : true;
    Boolean exibirVlrLqdTxJuros = request.getAttribute("exibirVlrLqdTxJuros") != null ? (Boolean) request.getAttribute("exibirVlrLqdTxJuros") : true;

    Boolean exibirMotivoOperacao = request.getAttribute("exibirMotivoOperacao") != null ? (Boolean) request.getAttribute("exibirMotivoOperacao") : true;
    Boolean motivoOperacaoObrigatorio = request.getAttribute("motivoOperacaoObrigatorio") != null ? (Boolean) request.getAttribute("motivoOperacaoObrigatorio") : false;

    Boolean exibirTipoJustica = (Boolean) request.getAttribute("exibirTipoJustica");
    Boolean tipoJusticaObrigatorio = (Boolean) request.getAttribute("tipoJusticaObrigatorio");

    Boolean exibirComarcaJustica = (Boolean) request.getAttribute("exibirComarcaJustica");
    Boolean comarcaJusticaObrigatorio = (Boolean) request.getAttribute("comarcaJusticaObrigatorio");

    Boolean exibirNumeroProcesso = (Boolean) request.getAttribute("exibirNumeroProcesso");
    Boolean numeroProcessoObrigatorio = (Boolean) request.getAttribute("numeroProcessoObrigatorio");

    Boolean exibirDataDecisao = (Boolean) request.getAttribute("exibirDataDecisao");
    Boolean dataDecisaoObrigatorio = (Boolean) request.getAttribute("dataDecisaoObrigatorio");

    Boolean exibirTextoDecisao = (Boolean) request.getAttribute("exibirTextoDecisao");
    Boolean textoDecisaoObrigatorio = (Boolean) request.getAttribute("textoDecisaoObrigatorio");

    Boolean exibirAnexo = (Boolean) request.getAttribute("exibirAnexo");
    Boolean anexoObrigatorio = (Boolean) request.getAttribute("anexoObrigatorio");

    Boolean permiteAlterarSemSenhaTemp = (Boolean) request.getAttribute("permiteAlterarSemSenhaTemp");
    Double percentualAlteracao = request.getAttribute("percentualAlteracao") != null ? (Double) request.getAttribute("percentualAlteracao") : 0.00;
    String formaPagamento = (String) request.getAttribute("formaPagamento");
    String servicoPermitePagamentoViaBoleto = (String) request.getAttribute("servicoPermitePagamentoViaBoleto");
    Beneficiario beneficiario = request.getAttribute("beneficiario") != null ? (Beneficiario) request.getAttribute("beneficiario") : null;
    
    String adeSuspensaLiquida = request.getAttribute("adeSuspensa") != null ? String.valueOf(request.getAttribute("adeSuspensa")) : "";
    String mensagemConfirmAdeSuspensa = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.decisao.judicial.liquidacao.ade.suspensa", responsavel, adeSuspensaLiquida);
%>
<c:set var="title">
    <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>?acao=salvar" method="post"
          name="form1">
        <div class="row">
            <div class="col-sm-7 pl-0">
                <% pageContext.setAttribute("autdes", autdes); %>
                <hl:detalharADEv4 name="autdes" table="false" type="alterar"/>
            </div>

            <div class="col-sm-5">
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-header-title"><hl:message key="rotulo.alterar.consignacao.novos.dados"/></h2>
                    </div>
                    <div class="card-body">
                        <%
                            out.print(SynchronizerToken.generateHtmlToken(request));
                        %>
                        <hl:htmlinput name="ADE_CODIGO" type="hidden"
                                      value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>"/>
                        <hl:htmlinput name="adeCodigo" type="hidden"
                                      value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>"/>
                        <hl:htmlinput name="svcCodigo" type="hidden"
                                      value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>"/>
                        <hl:htmlinput name="tipo" type="hidden" value="alterar"/>
                        <hl:htmlinput name="adePeriodicidade" type="hidden" value="<%=TextHelper.forHtmlAttribute(adePeriodicidade)%>"/>
                        <hl:htmlinput name="MM_update" type="hidden" value="form1"/>
                        <hl:htmlinput name="adeIntFolha" type="hidden" di="adeIntFolha"
                                      value="<%=TextHelper.forHtmlAttribute((adeIntFolha))%>"/>
                        <hl:htmlinput name="adeIncMargem" type="hidden" di="adeIncMargem"
                                      value="<%=TextHelper.forHtmlAttribute((adeIncMargem))%>"/>
                        <hl:htmlinput name="adeTipoVlr" type="hidden" di="adeTipoVlr"
                                      value="<%=TextHelper.forHtmlAttribute(tipoVlr)%>"/>
                        <hl:htmlinput name="tipoVlr" type="hidden" di="tipoVlr"
                                      value="<%=TextHelper.forHtmlAttribute(tipoVlr)%>"/>
                        <hl:htmlinput name="rseCodigo" type="hidden" di="rseCodigo"
                                      value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"/>
                        <hl:htmlinput name="rsePrazo" type="hidden" di="rsePrazo"
                                      value="<%=TextHelper.forHtmlAttribute(autdes.getAttribute(Columns.RSE_PRAZO))%>"/>
                        <hl:htmlinput name="adePrazo" type="hidden" di="adePrazo"
                                      value="<%=TextHelper.forHtmlAttribute((prazo != -1 ? String.valueOf(prazoRest) : ""))%>"/>
                        <hl:htmlinput name="exigeSenhaServidor" type="hidden" di="exigeSenhaServidor"
                                      value="<%=TextHelper.forHtmlAttribute(exigeSenhaServidor)%>"/>
                        <hl:htmlinput name="usuPossuiAltAvancadaAde" type="hidden"
                                      value="<%=String.valueOf(usuPossuiAltAvancadaAde)%>"/>
                        <hl:htmlinput name="permiteAumentarVlr" type="hidden"
                                      value="<%=String.valueOf(permiteAumentarVlr)%>"/>
                        <hl:htmlinput name="paramSvcTaxa" type="hidden" value="<%=String.valueOf(paramSvcTaxa)%>"/>

                        <% if (periodos != null && !periodos.isEmpty()) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="iNumPrestaRestante"><hl:message key="rotulo.folha.periodo"/></label>
                                <select class="form-control form-select" id="ocaPeriodo" name="ocaPeriodo"
                                        onFocus="SetarEventoMascara(this,'#*200',true);"
                                        onBlur="fout(this);ValidaMascara(this);">
                                    <% for (Date periodo : periodos) { %>
                                    <option value="<%=TextHelper.forHtmlAttribute(periodo)%>"><%=TextHelper.forHtmlContent(DateHelper.toPeriodString(periodo))%>
                                    </option>
                                    <% } %>
                                </select>
                            </div>
                        </div>
                        <% } %>
                        <% if (!responsavel.isSer() && podeAlterarCarencia && ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_CARENCIA, responsavel)) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="iNovaCarencia"><hl:message
                                        key="rotulo.consignacao.carencia.nova"/><%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%>
                                </label>
                                <input type="text" class="form-control" id="adeCarencia" name="adeCarencia"
                                       placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.carencia", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute(valorAdeCarencia)%>">
                            </div>
                        </div>
                        <% } else { %>
                        <hl:htmlinput name="adeCarencia" di="adeCarencia" type="hidden"
                                      value="<%=valorAdeCarencia != null ? TextHelper.forHtmlAttribute(valorAdeCarencia) : "0" %>"/>
                        <% } %>
                        <% if (serInfBancariaObrigatoria && exibirInfoBancaria) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="iNumPrestaRestante"><hl:message
                                        key="rotulo.servidor.informacoesbancarias"/></label>
                                &nbsp;<hl:message
                                    key="rotulo.servidor.informacoesbancarias.banco"/>&nbsp;<%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_BANCO) != null) ? autdes.getAttribute(Columns.ADE_BANCO) : "")%>
                                &nbsp;<hl:message
                                    key="rotulo.servidor.informacoesbancarias.agencia"/>&nbsp;<%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_AGENCIA) != null) ? autdes.getAttribute(Columns.ADE_AGENCIA) : "")%>
                                &nbsp;<hl:message
                                    key="rotulo.servidor.informacoesbancarias.conta"/>&nbsp;<%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_CONTA) != null) ? autdes.getAttribute(Columns.ADE_CONTA) : "")%>
                            </div>
                        </div>
                        <% } %>
                        <% if (exibirNovoValor) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="iNovValorPrestacao"><hl:message
                                        key="rotulo.consignacao.valor.parcela.novo"/>&nbsp;(<%=TextHelper.forHtmlContent(labelTipoVlr)%>
                                    )</label>
                                <input type="text" class="form-control" id="adeVlr" name="adeVlr" size="8" mask="#F11"
                                       placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.valor.parcela", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute(adeVlrAtual)%>"
                                       others="<%=TextHelper.forHtmlAttribute((!alteraAdeVlr ? "disabled" : ""))%>"
                                       onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }">
                                <% if (servicoCompulsorio && permiteAumentarVlr && (svcPrioridade != null && !svcPrioridade.trim().equals(""))) { %>
                                &nbsp;<a href="#no-back" onClick="abrirComposicaoCompulsorio()"><img
                                    src="../img/icones/reservar_margem.gif"
                                    alt="<hl:message key="mensagem.composicao.margem.compulsorios.clique.aqui"/>"
                                    title="<hl:message key="mensagem.composicao.margem.compulsorios.clique.aqui"/>"
                                    border="0"/></a>
                                <% } %>
                            </div>
                        </div>
                        <% } else { %>
                        <input type="hidden" class="form-control" id="adeVlr" name="adeVlr"
                               value="<%=TextHelper.forHtmlAttribute(adeVlrAtual)%>">
                        <% } %>
                        <% if (exibirNumPrestacoesRest) { %>
                        <div class="row" id="prazoEditavel">
                            <div class="form-group col-sm">
                                <label for="adePrazoEdt"><hl:message
                                        key="rotulo.consignacao.prazo.restante"/><%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%>
                                </label>
                                <input type="text" class="form-control" NAME="adePrazoEdt" ID="adePrazoEdt"
                                       VALUE="<%=TextHelper.forHtmlAttribute((prazo != -1 ? String.valueOf(prazoRest) : ""))%>"
                                       SIZE="8"
                                       placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.prazo", responsavel)%>"
                                       onFocus="SetarEventoMascara(this,'#D4',true);"
                                       onBlur="fout(this);ValidaMascara(this);verificaPrazo(this);"
                                       onChange="setaPrazo(this);" <%=(String)((maxPrazo == 0) ? "disabled " : "")%>>
                            </div>
                        </div>

                        <div class="row" id="prazoDeterminado">
                            <div class="form-group col-sm">
                                <label for="adePrazoDet"><hl:message
                                        key="rotulo.consignacao.prazo.restante"/><%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%>
                                </label>
                                <select class="form-control form-select" name="adePrazoDet" ID="adePrazoDet"
                                        onFocus="SetarEventoMascara(this,'#*200',true);"
                                        onBlur="fout(this);ValidaMascara(this);" onChange="setaPrazo(this);">
                                    <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                                    <%
                                        boolean existePrz = false;
                                        for (Integer prazoVlr : prazosPossiveis) {
                                            if (!existePrz) {
                                                existePrz = prazoVlr.equals(prazoRest);
                                            }
                                    %>
                                    <option value="<%=prazoVlr%>" <%=(String) (prazoVlr.equals(prazoRest) ? "selected" : "")%>><%=prazoVlr%>
                                    </option>
                                    <% } %>
                                    <% if (!existePrz && prazoRest > 0) { %>
                                    <option value="<%=(int)prazoRest%>"
                                            selected><%=TextHelper.forHtmlContent(prazoRest)%>
                                    </option>
                                    <% } %>
                                </select>
                            </div>
                        </div>
                        <% if (maxPrazo <= 0) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <hl:htmlinput classe="form-check-input ml-1" type="checkbox" name="adeSemPrazo"
                                              di="adeSemPrazo" onClick="habilitaPrazo();" mask="#*200"
                                              others="<%=(String)((maxPrazo==0 || prazo==-1)? "CHECKED": "")%>"/>
                                <label class="form-check-label" for="adeSemPrazo"><hl:message
                                        key="rotulo.consignacao.prazo.indeterminado"/></label>
                            </div>
                        </div>
                        <% } %>
                        <% } else { %>
                        <% if (prazosPossiveis == null || prazosPossiveis.isEmpty()) { %>
                        <input type="hidden" name="adePrazoEdt" value="<%=prazoRest%>"/>
                        <% } else { %>
                        <input type="hidden" name="adePrazoDet" value="<%=prazoRest%>"/>
                        <% } %>
                        <hl:htmlinput classe="form-check-input ml-1" type="hidden" name="adeSemPrazo"
                                      di="adeSemPrazo" mask="#*200"
                                      others="<%=(String)((maxPrazo==0 || prazo==-1)? "CHECKED": "")%>"/>
                        <% } %>
                        <% if (permiteCadVlrTac && exibirCadVlrTac) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeVlrTac"><hl:message
                                        key="rotulo.consignacao.valor.tac.novo"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</label>
                                <input type="text" class="form-control" name="adeVlrTac" id="adeVlrTac"
                                       placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.tac", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_TAC) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_TAC).toString(), "en", NumberHelper.getLang()) : "")%>"
                                       size="8" mask="#F11"
                                       onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
                            </div>
                        </div>
                        <% } %>
                        <% if (permiteCadVlrIof && exibirCadVlrIof) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeVlrIof"><hl:message
                                        key="rotulo.consignacao.valor.iof.novo"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</label>
                                <input type="text" class="form-control" name="adeVlrIof" id="adeVlrIof"
                                       placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.iof", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_IOF) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_IOF).toString(), "en", NumberHelper.getLang()) : "")%>"
                                       size="8" mask="#F11"
                                       onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
                            </div>
                        </div>
                        <% } %>
                        <% if (permiteCadVlrLiqLib && permiteAlterarVlrLiberado && exibirNovoValorLiq) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeVlrLiquido"><hl:message
                                        key="rotulo.consignacao.valor.liquido.liberado.novo"/>&nbsp;(<hl:message
                                        key="rotulo.moeda"/>)</label>
                                <input type="text" class="form-control" name="adeVlrLiquido" id="adeVlrLiquido"
                                       placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.valor.liquido", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_LIQUIDO) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_LIQUIDO).toString(), "en", NumberHelper.getLang()) : "")%>"
                                       size="8" mask="#F11"
                                       onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
                            </div>
                        </div>
                        <% } %>
                        <% if (permiteCadVlrMensVinc && exibirCadVlrMensVinc) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeVlrMensVinc"><hl:message
                                        key="rotulo.consignacao.valor.mensalidade.vinc.novo"/>&nbsp;(<hl:message
                                        key="rotulo.moeda"/>)</label>
                                <input type="text" class="form-control" name="adeVlrMensVinc" id="adeVlrMensVinc"
                                       placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.valor.mens.vinc", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_MENS_VINC) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_MENS_VINC).toString(), "en", NumberHelper.getLang()) : "")%>"
                                       size="8" mask="#F11"
                                       onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
                            </div>
                        </div>
                        <% } %>
                        <% if (boolTpsSegPrestamista && exibirSegPrestamista) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeVlrSegPrestamista"><hl:message
                                        key="rotulo.consignacao.seguro.prestamista.novo"/>&nbsp;(<hl:message
                                        key="rotulo.moeda"/>)</label>
                                <input type="text" class="form-control" name="adeVlrSegPrestamista"
                                       id="adeVlrSegPrestamista"
                                       placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.seguro.prestamista", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA).toString(), "en", NumberHelper.getLang()) : "")%>"
                                       size="8" mask="#F11"
                                       onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
                            </div>
                        </div>
                        <% } %>
                        <% if (permiteVlrLiqTxJuros && exibirVlrLqdTxJuros) {
                            String placeHolderTaxa = (String) (temCET ? ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.cet", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.taxa.juros", responsavel));
                        %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeTaxaJuros"><hl:message
                                        key="<%=(String)(temCET ? "rotulo.consignacao.cet.novo" : "rotulo.consignacao.taxa.juros.nova")%>"/></label>
                                <input type="text" class="form-control" name="adeTaxaJuros" id="adeTaxaJuros"
                                       placeholder="<%=placeHolderTaxa%>"
                                       value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_TAXA_JUROS) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_TAXA_JUROS).toString(), "en", NumberHelper.getLang()) : "")%>"
                                       size="8" mask="#F11"
                                       onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
                            </div>
                        </div>
                        <% } %>
                        <% if (request.getAttribute("exibirCampoIndice") != null) { %>
                        <%
                            boolean vlrIndiceDisabled = (request.getAttribute("vlrIndiceDisabled") != null && (Boolean) request.getAttribute("vlrIndiceDisabled"));
                            String vlrIndice = (String) request.getAttribute("vlrIndice");
                            String mascaraIndice = (String) request.getAttribute("mascaraIndice");
                            List<TransferObject> indices = (List<TransferObject>) request.getAttribute("lstIndices");
                        %>
                        <% if (indices != null && !indices.isEmpty()) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeIndice"><hl:message key="rotulo.consignacao.indice"/>&nbsp;<hl:message
                                        key="rotulo.campo.opcional"/></label>
                                <%=JspHelper.geraCombo(indices, "adeIndice", Columns.IND_CODIGO, Columns.IND_CODIGO + ";" + Columns.IND_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control form-select\"", false, 1, vlrIndice)%>
                            </div>
                        </div>
                        <% } else { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeIndice"><hl:message key="rotulo.consignacao.indice"/>&nbsp;<hl:message
                                        key="rotulo.campo.opcional"/></label>
                                <hl:htmlinput name="adeIndice" type="text"
                                              others="<%=TextHelper.forHtmlAttribute(vlrIndiceDisabled ? "disabled" : "")%>"
                                              classe="form-control" di="adeIndice" size="10" mask="<%=mascaraIndice%>"
                                              value="<%=TextHelper.forHtmlAttribute(request.getParameter("adeIndice") != null && !request.getParameter("adeIndice").equals("null")? request.getParameter("adeIndice").toString() : vlrIndice )%>"
                                              placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.indice", responsavel)%>'/>
                            </div>
                        </div>
                        <% } %>
                        <% } %>
                        <% if (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.ALTERAR_CONSIGNACAO_ADE_IDENTIFICADOR, responsavel)) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeIdentificador"><hl:message
                                        key="rotulo.consignacao.identificador"/><% if (!identificadorAdeObrigatorio) { %>&nbsp;<hl:message
                                        key="rotulo.campo.opcional"/><% } %></label>
                                <input type="text" class="form-control" id="adeIdentificador" name="adeIdentificador"
                                       placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.ade.identificador", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.ADE_IDENTIFICADOR)))%>"
                                       size="15"
                                       mask="<%=TextHelper.forHtmlAttribute(TextHelper.isNull(mascaraAdeIdentificador) ? "#*40":mascaraAdeIdentificador )%>"
                                       nf="submit" <%= !ShowFieldHelper.isDisabled(FieldKeysConstants.ALTERAR_CONSIGNACAO_ADE_IDENTIFICADOR, responsavel) ? "" : "disabled"%> />
                            </div>
                        </div>
                        <% } %>
                        <% if (!TextHelper.isNull(formaPagamento) && !TextHelper.isNull(servicoPermitePagamentoViaBoleto) && servicoPermitePagamentoViaBoleto.equals(CodedValues.PAGAMENTO_VIA_BOLETO_OPICIONAL)) {%>
                        <div class="row">
                            <div class="form-group col-sm-4 col-md-6">
                    <span class="text-nowrap align-text-top">
                      <input class="form-check-input ml-1" type="checkbox" name="permiteDescontoViaBoleto"
                             id="permiteDescontoViaBoleto"
                             value="S" <%= formaPagamento.equals(CodedValues.FORMA_PAGAMENTO_BOLETO) ? "checked" : "" %>>
                      <label class="form-check-label labelSemNegirto ml-1"
                             aria-label='<hl:message key="rotulo.reservar.margem.pagar.via.boleto"/>'
                             for="permiteDescontoViaBoleto"><hl:message
                              key="rotulo.reservar.margem.pagar.via.boleto"/></label>
                    </span>
                            </div>
                        </div>
                        <% } else if (!TextHelper.isNull(servicoPermitePagamentoViaBoleto) && servicoPermitePagamentoViaBoleto.equals(CodedValues.PAGAMENTO_VIA_BOLETO_OBRIGATORIO)) { %>
                        <div class="row">
                            <div class="form-group col-sm-4 col-md-6">
                        <span class="text-nowrap align-text-top">
                          <input class="form-check-input ml-1" type="checkbox" name="permiteDescontoViaBoleto"
                                 id="permiteDescontoViaBoleto" value="S" checked disabled>
                          <label class="form-check-label labelSemNegirto ml-1"
                                 aria-label='<hl:message key="rotulo.reservar.margem.pagar.via.boleto"/>'
                                 for="permiteDescontoViaBoleto"><hl:message
                                  key="rotulo.reservar.margem.pagar.via.boleto"/></label>
                        </span>
                            </div>
                        </div>
                        <%} %>
                        <% if (responsavel.isCsaCor() && exigeModalidadeOperacao) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="tdaModalidadeOp"><hl:message
                                        key="rotulo.consignacao.nova.modalidade.operacao"/></label>
                                <input type="text" class="form-control" name="tdaModalidadeOp" id="tdaModalidadeOp"
                                       placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.modalidade.operacao", responsavel)%>%>"
                                       size="6" mask="#*6" value="<%=TextHelper.forHtmlAttribute(tdaModalidadeOp) %>"/>
                            </div>
                        </div>
                        <% } %>
                        <% if (responsavel.isCsaCor() && exigeMatriculaSerCsa) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="tdaMatriculaCsa"><hl:message
                                        key="rotulo.consignacao.nova.matricula.ser.csa"/></label>
                                <input type="text" class="form-control" name="tdaMatriculaCsa" id="tdaMatriculaCsa"
                                       placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.matricula.ser.csa", responsavel)%>%>"
                                       size="20" mask="#*20"
                                       value="<%=TextHelper.forHtmlAttribute(tdaMatriculaCsa) %>"/>
                            </div>
                        </div>
                        <% } %>
                        <% for (TransferObject tda : tdaList) { %>
                        <hl:paramv4
                                prefixo="TDA_"
                                descricao="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"
                                codigo="<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>"
                                dominio="<%=(String) tda.getAttribute(Columns.TDA_DOMINIO)%>"
                                valor="<%=dadosAutorizacao.get((String) tda.getAttribute(Columns.TDA_CODIGO))%>"
                                desabilitado="<%= CodedValues.CAS_BLOQUEADO.equals(tda.getAttribute(Columns.SPT_EXIBE)) %>"
                        />
                        <% } %>
                        <% if (!exigeSenhaServidor.equals(CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS)) { %>
                        <%
                            String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                            if (!TextHelper.isNull(mascaraLogin)) {
                        %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="serLogin"><hl:message
                                        key="rotulo.usuario.autorizacao.servidor.singular"/><%=exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS) ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)%>
                                </label>
                                <input type="text" class="form-control" name="serLogin" id="serLogin"
                                       placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.matricula.ser.csa", responsavel)%>%>"
                                       size="15" mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>"/>
                            </div>
                        </div>
                        <% } %>

                        <div class="row">
                            <div class="form-group col-sm">
                                <hl:senhaServidorv4
                                        senhaObrigatoria="<%=(String)(exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS) ? "true" : "false")%>"
                                        senhaParaAutorizacaoReserva="false"
                                        nomeCampoSenhaCriptografada="serAutorizacao"
                                        rseCodigo="<%=rseCodigo%>"
                                        classe="form-control"
                                        comTagDD="false"
                                        nf="submit"/>
                            </div>
                        </div>
                        <% } %>

                            <%-- Anexo de arquivo --%>
                        <% if (usuarioPodeAnexarAlteracao || exibirAnexo) {%>
                        <hl:fileUploadV4 tipoArquivo="<%= (String) request.getAttribute("tipoArquivoAnexo") %>"/>
                        <% } %>

                            <%-- Motivo e observação da operação --%>
                        <% if (exibeMotivoOperacao && exibirMotivoOperacao) { %>
                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="tmoCodigo"><hl:message key="rotulo.avancada.tmoCodigo"/></label>
                                <%=JspHelper.geraCombo(lstMtvOperacao, "tmoCodigo", Columns.TMO_CODIGO, Columns.TMO_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control form-select\"", false, 1)%>
                            </div>
                        </div>

                        <div class="row">
                            <div class="form-group col-sm">
                                <label for="adeObs"><hl:message key="rotulo.avancada.adeObs"/></label>
                                <textarea class="form-control"
                                          placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs",responsavel)%>'
                                          id="adeObs" name="adeObs" rows="6"
                                          onFocus="SetarEventoMascara(this,'#*10000',true);"
                                          onBlur="fout(this);ValidaMascara(this);"></textarea>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>
                <% if (usuPossuiAltAvancadaAde || ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel))) { %>
                <div class="opcoes-avancadas">
                    <a class="opcoes-avancadas-head collapsed" href="#opcoesAvancadas" data-bs-toggle="collapse"
                       aria-expanded="false" aria-controls="opcoesAvancadas" id="opcoesAvancadasLink"><hl:message
                            key="rotulo.avancada.opcoes"/></a>
                    <div class="collapse" id="opcoesAvancadas">
                        <div class="opcoes-avancadas-body pl-4">
                        <div class="row">
                      <div class="form-group ml-3" aria-labelledby="visibilidade">
                        <div class="form-check pt-2">
                          <span id="visibilidade">
                            <hl:message key="rotulo.avancada.anexos.visibilidade"/>
                          </span>
                          <fieldset class="col-sm-12 col-md-12">
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeSup" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_SUPORTE%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeSup">
                                      <hl:message key="rotulo.suporte.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeCse" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_CONSIGNANTE%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeCse">
                                      <hl:message key="rotulo.consignante.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeOrg" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_ORGAO%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeOrg">
                                      <hl:message key="rotulo.orgao.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeCsa" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_CONSIGNATARIA%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeCsa">
                                      <hl:message key="rotulo.consignataria.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeCor" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_CORRESPONDENTE%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeCor">
                                      <hl:message key="rotulo.correspondente.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeSer" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_SERVIDOR%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeSer">
                                      <hl:message key="rotulo.servidor.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="checkTodos"  type="checkbox" onclick="!this.checked ? uncheckAll(f0, 'aadExibe') : checkAll(f0, 'aadExibe')" value="S" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="checkTodos">
                                      <hl:message key="rotulo.campo.todos.simples"/>
                                  </label>
                              </div>
                          </fieldset>
                        </div>
                      </div>
                    </div>
                            <div class="row">

                                <% if (request.getAttribute("omitirOpcoesAvancadas") == null) { %>

                                <% if (!permiteQualquerPrazo && (prazosPossiveis != null && !prazosPossiveis.isEmpty())) { %>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iPermiteUtilizarPrazoNaoCadastradoServico">
                                    <div class="form-group my-0">
                                        <span id="iPermiteUtilizarPrazoNaoCadastradoServico"><hl:message
                                                key="rotulo.avancada.permitePrzNaoCadastrado"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="permitePrzNaoCadastrado"
                                               id="iPrazoCadastradSim" value="true"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onclick="exibePrzEditavel();" onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="permitePrzNaoCadastradoSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="permitePrzNaoCadastrado"
                                               id="iPrazoCadastradNao" value="false" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onclick="exibePrzDeterminado();"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="permitePrzNaoCadastradoNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <% } %>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iPermiteAlterarConveniServicoServidorConsignatariaConsignante">
                                    <div class="form-group my-0">
                                        <span id="iPermiteAlterarConveniServicoServidorConsignatariaConsignante"><hl:message
                                                key="rotulo.avancada.permiteAltEntidadesBloqueadas"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio"
                                               name="permiteAltEntidadesBloqueadas" id="PermiteAlterarSim" value="true"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="permiteAltEntidadesBloqueadasSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio"
                                               name="permiteAltEntidadesBloqueadas" id="PermiteAlterarNao" value="false"
                                               CHECKED onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="permiteAltEntidadesBloqueadasNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iAfetarMargemServidor">
                                    <div class="form-group my-0">
                                        <span id="iAfetarMargemServidor"><hl:message
                                                key="rotulo.avancada.afetaMargem"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="afetaMargem"
                                               id="iAfetarMargemSim" value="true" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="iAfetarMargemSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="afetaMargem"
                                               id="iAfetarMargemNao" value="false"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="iAfetarMargemNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="validaMargem">
                                    <div class="form-group my-0">
                                        <span id="validaMargem"><hl:message key="rotulo.avancada.validaMargem"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="validaMargem"
                                               id="validaMargemSim" value="true" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="validaMargemSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="validaMargem"
                                               id="validaMargemNao" value="false"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="validaMargemNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <% if (paramSvcTaxa) { %>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iValidarTaxaJuros">
                                    <div class="form-group my-0">
                                        <span id="iValidarTaxaJuros"><hl:message
                                                key="rotulo.avancada.validaTaxa"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="validaTaxa"
                                               id="iValidaTaxaJurosSim" value="true" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="iValidaTaxaJurosSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="validaTaxa"
                                               id="iValidaTaxaJurosNao" value="false"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="iValidaTaxaJurosNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <% } %>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iAlterarValorPrazoLimite">
                                    <div class="form-group my-0">
                                        <span id="iAlterarValorPrazoLimite"><hl:message
                                                key="rotulo.avancada.valorPrazoSemLimite"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="valorPrazoSemLimite"
                                               id="iAlterarValorPrazoSim" value="true"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="iAlterarValorPrazoSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="valorPrazoSemLimite"
                                               id="iAlterarValorPrazoNao" value="false" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="iAlterarValorPrazoNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <% if (!exigeSenhaServidor.equals(CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS)) { %>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iAlterarValorPrazoLimite">
                                    <div class="form-group my-0">
                                        <span id="iExigeSenhaAltAvancada"><hl:message
                                                key="rotulo.avancada.exigeSenha"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="exigeSenhaAltAvancada"
                                               id="exigeSenhaAltAvancadaSim" value="true" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="exigeSenhaAltAvancadaSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="exigeSenhaAltAvancada"
                                               id="exigeSenhaAltAvancadaNao" value="false"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="exigeSenhaAltAvancadaNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <% } %>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iCriarNovoContratoDiferencaValorParcela">
                                    <div class="form-group my-0">
                                        <span id="iCriarNovoContratoDiferencaValorParcela"><hl:message
                                                key="rotulo.avancada.novoContratoDif"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="novoContratoDif"
                                               id="iCriarNovoContratoDiferencaValorParcelaSim" value="true"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);"
                                               onChange="atualizaOpcoesAvancadas()">
                                        <label class="form-check-label pr-3"
                                               for="iCriarNovoContratoDiferencaValorParcelaSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="novoContratoDif"
                                               id="iCriarNovoContratoDiferencaValorParcelaNao" value="false" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);"
                                               onChange="atualizaOpcoesAvancadas()">
                                        <label class="form-check-label"
                                               for="iCriarNovoContratoDiferencaValorParcelaNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iCalcularPrazoDiferencaValorParcela">
                                    <div class="form-group my-0">
                                        <span id="iCalcularPrazoDiferencaValorParcela"><hl:message
                                                key="rotulo.avancada.calcularPrazoDifValor"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="calcularPrazoDifValor"
                                               id="iCalcularPrazoDiferencaValorParcelaSim" value="true"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);"
                                               onChange="atualizaOpcoesAvancadas()">
                                        <label class="form-check-label pr-3"
                                               for="iCalcularPrazoDiferencaValorParcelaSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="calcularPrazoDifValor"
                                               id="iCalcularPrazoDiferencaValorParcelaNao" value="false" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);"
                                               onChange="atualizaOpcoesAvancadas()">
                                        <label class="form-check-label" for="iCalcularPrazoDiferencaValorParcelaNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iManterDiferencaValorParcelaMargem">
                                    <div class="form-group my-0">
                                        <span id="iManterDiferencaValorParcelaMargem"><hl:message
                                                key="rotulo.avancada.manterDifValorMargem"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="manterDifValorMargem"
                                               id="iManterDiferencaValorParcelaMargemSim" value="true"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);"
                                               onChange="atualizaOpcoesAvancadas()">
                                        <label class="form-check-label pr-3"
                                               for="iManterDiferencaValorParcelaMargemSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="manterDifValorMargem"
                                               id="iManterDiferencaValorParcelaMargemNao" value="false" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);"
                                               onChange="atualizaOpcoesAvancadas()">
                                        <label class="form-check-label" for="iManterDiferencaValorParcelaMargemNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>

                                <div class="form-check form-group col-md-12 mt-2">
                                    <label for="novoSadCodigo"><hl:message key="rotulo.avancada.novoSadCodigo"/></label>
                                    <%=JspHelper.geraCombo(statusAutorizacao, "novoSadCodigo", Columns.SAD_CODIGO, Columns.SAD_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control form-select\"", false, 1)%>
                                </div>

                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iIncluirOcorrencia">
                                    <div class="form-group my-0">
                                        <span id="iIncluirOcorrencia"><hl:message
                                                key="rotulo.avancada.insereOcorrencia"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="insereOcorrencia"
                                               id="iIncluirOcorrenciaSim" value="true" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="iIncluirOcorrenciaSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="insereOcorrencia"
                                               id="iIncluirOcorrenciaNao" value="false"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="iIncluirOcorrenciaNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iIntegraFolha">
                                    <div class="form-group my-0">
                                        <span id="iIntegraFolha"><hl:message key="rotulo.avancada.integraFolha"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="integraFolha"
                                               id="iIntegraFolhaSim"
                                               VALUE="<%=(Short)CodedValues.INTEGRA_FOLHA_SIM%>" <%=(intFolha.equals(CodedValues.INTEGRA_FOLHA_SIM) ? "CHECKED" : "")%>
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="iIntegraFolhaSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="integraFolha"
                                               id="iIntegraFolhaNao"
                                               VALUE="<%=(Short)CodedValues.INTEGRA_FOLHA_NAO%>" <%=(intFolha.equals(CodedValues.INTEGRA_FOLHA_NAO) ? "CHECKED" : "")%>
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="iIntegraFolhaNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>
                                <div class=" col-md-12 form-check mt-2" role="radiogroup"
                                     aria-labelledby="iValidaLimiteContrato">
                                    <div class="form-group my-0">
                                        <span id="iValidaLimiteContrato"><hl:message
                                                key="rotulo.avancada.validaLimiteAde"/></span>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="validaLimiteAde"
                                               id="iValidaLimiteContratoSim" VALUE="true" CHECKED
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label pr-3" for="iValidaLimiteContratoSim">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.sim"/></span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input ml-1" type="radio" name="validaLimiteAde"
                                               id="iValidaLimiteContratoNao" VALUE="false"
                                               onFocus="SetarEventoMascara(this,'#*100',true);"
                                               onBlur="fout(this);ValidaMascara(this);">
                                        <label class="form-check-label" for="iValidaLimiteContratoNao">
                                            <span class="text-nowrap align-text-top"><hl:message
                                                    key="rotulo.nao"/></span>
                                        </label>
                                    </div>
                                </div>

                                <div class="form-check form-group col-md-12 mt-2">
                                    <label for="incideMargem"><hl:message key="rotulo.avancada.incideMargem"/></label>
                                    <%=JspHelper.geraCombo(margens, "incideMargem", Columns.MAR_CODIGO, Columns.MAR_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control form-select\"", false, 1, incMargem.toString())%>
                                </div>

                                <% } else { %>

                                <input type="hidden" name="validaMargem"
                                       value="<%=request.getAttribute("opcaoValidaMargemFixo")                  != null ? (Boolean) request.getAttribute("opcaoValidaMargemFixo")                  : AlterarConsignacaoParametros.PADRAO_VALIDA_MARGEM %>"/>
                                <input type="hidden" name="validaTaxa"
                                       value="<%=request.getAttribute("opcaoValidaTaxaFixo")                    != null ? (Boolean) request.getAttribute("opcaoValidaTaxaFixo")                    : AlterarConsignacaoParametros.PADRAO_VALIDA_TAXA_JUROS %>"/>
                                <input type="hidden" name="validaLimiteAde"
                                       value="<%=request.getAttribute("opcaoValidaLimiteAdeFixo")               != null ? (Boolean) request.getAttribute("opcaoValidaLimiteAdeFixo")               : AlterarConsignacaoParametros.PADRAO_VALIDA_LIMITE_ADE %>"/>
                                <input type="hidden" name="valorPrazoSemLimite"
                                       value="<%=request.getAttribute("opcaoValorPrazoSemLimiteFixo")           != null ? (Boolean) request.getAttribute("opcaoValorPrazoSemLimiteFixo")           : AlterarConsignacaoParametros.PADRAO_ALTERA_VALOR_PRAZO_SEM_LIMITE %>"/>
                                <input type="hidden" name="permiteAltEntidadesBloqueadas"
                                       value="<%=request.getAttribute("opcaoPermiteAltEntidadesBloqueadasFixo") != null ? (Boolean) request.getAttribute("opcaoPermiteAltEntidadesBloqueadasFixo") : AlterarConsignacaoParametros.PADRAO_PERMITE_ALT_ENTIDADES_BLOQUEADAS %>"/>
                                <input type="hidden" name="exigeSenhaAltAvancada"
                                       value="<%=request.getAttribute("opcaoExigeSenhaAltAvancadaFixo")         != null ? (Boolean) request.getAttribute("opcaoExigeSenhaAltAvancadaFixo")         : AlterarConsignacaoParametros.PADRAO_EXIGE_SENHA %>"/>
                                <input type="hidden" name="afetaMargem"
                                       value="<%=request.getAttribute("opcaoAfetaMargemFixo")                   != null ? (Boolean) request.getAttribute("opcaoAfetaMargemFixo")                   : AlterarConsignacaoParametros.PADRAO_ALTERA_MARGEM %>"/>
                                <input type="hidden" name="novoContratoDif"
                                       value="<%=request.getAttribute("opcaoNovoContratoDifFixo")               != null ? (Boolean) request.getAttribute("opcaoNovoContratoDifFixo")               : AlterarConsignacaoParametros.PADRAO_CRIAR_NOVO_CONTRATO_DIF %>"/>
                                <input type="hidden" name="insereOcorrencia"
                                       value="<%=request.getAttribute("opcaoInsereOcorrenciaFixo")              != null ? (Boolean) request.getAttribute("opcaoInsereOcorrenciaFixo")              : AlterarConsignacaoParametros.PADRAO_INCLUI_OCORRENCIA %>"/>
                                <input type="hidden" name="liquidaRelacionamentoJudicial" id="liquidaRelacionamentoJudicial"
                                       value="<%=request.getAttribute("opcaoLiquidaRelacionamentoJudicialFixo") != null ? (Boolean) request.getAttribute("opcaoLiquidaRelacionamentoJudicialFixo") : AlterarConsignacaoParametros.PADRAO_LIQUIDA_RELACIONAMENTO_JUDICIAL %>"/>
                                <input type="hidden" name="incideMargem"
                                       value="<%=request.getAttribute("opcaoIncideMargemFixo")                  != null ? (Short) request.getAttribute("opcaoIncideMargemFixo")                    : incMargem %>"/>
                                <input type="hidden" name="integraFolha"
                                       value="<%=request.getAttribute("opcaoIntegraFolhaFixo")                  != null ? (Short) request.getAttribute("opcaoIntegraFolhaFixo")                    : intFolha %>"/>
                                <input type="hidden" name="novoSadCodigo"
                                       value="<%=request.getAttribute("opcaoNovoSadCodigoFixo")                 != null ? (String) request.getAttribute("opcaoNovoSadCodigoFixo")                  : "" %>"/>

                                <% } %>

                                <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) { %>
                                <div class="card-header col-md-12 mt-2">
                                    <h2 class="card-header-title"><hl:message
                                            key="rotulo.avancada.decisao.judicial.titulo"/></h2>
                                </div>

                                <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, CodedValues.TPC_SIM, responsavel) && request.getAttribute("djuCodigo") != null && request.getAttribute("tipoDecisaoJudicial").equals(CodedValues.DECISAO_JUDICIAL_OPCAO_ALTERAR_CONSIGNACAO) && !djuRevogada)   { %>
                                <div class="form-check djuRevogar pt-3  form-group col-md-12 mt-7">
                                    <label class="form-check-label" for="djuRevogacao">Revogar Decisão Judicial</label>
                                    <input class="form-check-input" id="djuRevogacao" name="djuRevogacao" value="S" type="checkbox"/>
                                    <input type="hidden" id="djuCodigo" name="djuCodigo" value="<%=request.getAttribute("djuCodigo") != null ? (String) request.getAttribute("djuCodigo") : "" %>"/>
                                </div>
                                <% } %>
                                <% if (exibirTipoJustica) { %>
                                <div class="form-check form-group col-md-12 mt-2">
                                    <label for="tjuCodigo"><hl:message
                                            key="rotulo.avancada.decisao.judicial.tipo.justica"/></label>
                                    <%=JspHelper.geraCombo(lstTipoJustica, "tjuCodigo", Columns.TJU_CODIGO, Columns.TJU_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control form-select\"")%>
                                </div>
                                <% } %>

                                <% if (exibirComarcaJustica) { %>
                                <div class="form-check form-group col-md-12 mt-2">
                                    <label for="djuEstado"><hl:message
                                            key="rotulo.avancada.decisao.judicial.estado"/></label>
                                    <%= JspHelper.geraComboUF("djuEstado", "djuEstado", "", false, "form-control", responsavel) %>
                                </div>

                                <div class="form-check form-group col-md-12 mt-2">
                                    <label for="djuComarca"><hl:message
                                            key="rotulo.avancada.decisao.judicial.comarca"/></label>
                                    <select name="djuComarca" id="djuComarca" class="form-control form-select"></select>
                                    <hl:htmlinput name="cidCodigo" di="cidCodigo" type="hidden"/>
                                </div>
                                <% } %>

                                <% if (exibirNumeroProcesso) { %>
                                <div class="form-check form-group col-md-12 mt-2">
                                    <label for="djuNumProcesso"><hl:message
                                            key="rotulo.avancada.decisao.judicial.numero.processo"/></label>
                                    <hl:htmlinput name="djuNumProcesso" di="djuNumProcesso" type="text"
                                                  classe="form-control" size="40"/>
                                </div>
                                <% } %>

                                <% if (exibirDataDecisao) { %>
                                <div class="form-check form-group col-md-12 mt-2">
                                    <label for="djuData"><hl:message
                                            key="rotulo.avancada.decisao.judicial.data"/></label>
                                    <hl:htmlinput name="djuData" di="djuData" type="text" classe="form-control"
                                                  size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>"/>
                                </div>
                                <% } %>

                                <% if (exibirTextoDecisao) { %>
                                <div class="form-check form-group col-md-12 mt-2">
                                    <label for="djuTexto"><hl:message
                                            key="rotulo.avancada.decisao.judicial.texto"/></label>
                                    <textarea name="djuTexto" id="djuTexto" class="form-control" cols="32" rows="5"
                                              onFocus="SetarEventoMascara(this,'#*10000',true);"
                                              onBlur="fout(this);ValidaMascara(this);"></textarea>
                                </div>
                                <% } %>
                                <% } %>

                                <hl:htmlinput name="exibeAltAvancada" type="hidden" value="true"/>

                            </div>

                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
        <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
            <%if (request.getAttribute("omitirOpcoesAvancadas") != null && (usuPossuiAltAvancadaAde || ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)))) {%>
	            <a class="btn btn-primary" href="#no-back" onClick="verificaLiquidaAdeSuspensa()"><hl:message key="rotulo.botao.salvar"/></a>        			
        	<%} else {%>
	            <a class="btn btn-primary" href="#no-back" onClick="alterarContrato(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
        	<%} %>
        </div>
        
        <!-- Modal LiquidaAdeSuspensa-->
        <%if (request.getAttribute("omitirOpcoesAvancadas") != null && (usuPossuiAltAvancadaAde || ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)))) {%>
	        <div class="modal fade" id="modalConfirmaLiquidacao" tabindex="-1" role="dialog" aria-labelledby="modalConfirmaLiquidacaoLabel" aria-hidden="true">
			  <div class="modal-dialog" role="document">
			    <div class="modal-content">
			      <div class="modal-header">
			        <h5 class="modal-title about-title mb-0"><hl:message key="rotulo.suspensao.decisao.judicial.confirma.titulo"/></h5>
			        <button type="button" class="logout mr-3" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
			          <span aria-hidden="true"></span>
			        </button>
			      </div>
			      <div class="modal-body">
			        <%=mensagemConfirmAdeSuspensa%>
			      </div>
			      <div class="modal-footer pt-0">
					  <div class="btn-action mt-2 mb-0">
						<a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.nao"/>' href="#" onclick="alteraHiddenOpcaoLiquidaRelacionamentoJudicialFixo('false')"><hl:message key="rotulo.nao"/></a>
						<a class="btn btn-primary" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.sim"/>' onclick="alteraHiddenOpcaoLiquidaRelacionamentoJudicialFixo('true');" href="#"><hl:message key="rotulo.sim"/></a>
					  </div>
				</div>
			    </div>
			  </div>
			</div>
        <%} %>
    </form>
</c:set>

<c:set var="javascript">
    <hl:fileUploadV4 scriptOnly="true" tipoArquivo="<%= (String) request.getAttribute("tipoArquivoAnexo") %>"/>
    <hl:senhaServidorv4
            senhaObrigatoria="<%=(String)(exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS) ? "true" : "false")%>"
            senhaParaAutorizacaoReserva="false"
            nomeCampoSenhaCriptografada="serAutorizacao"
            rseCodigo="<%=rseCodigo%>"
            classe="form-control"
            comTagDD="false"
            nf="submit"
            scriptOnly="true"/>

    <script type="text/JavaScript">
        function verificaAnexo() {
            <% if (usuarioPodeAnexarAlteracao) { %>
            if (document.getElementById('FILE1').value == '') {
                alert('<hl:message key="mensagem.informe.arquivo.upload"/>');
                return false;
            }
            <% } %>
            return true;
        }

        function exibePrzDeterminado() {
            $('#prazoEditavel').hide();
            $('#prazoDeterminado').show();
            f0.adePrazoDet.onblur = function onblur(event) {
                fout(this);
                ValidaMascara(this);
            };
            f0.adePrazoEdt.onblur = function onblur(event) {
                return true;
            };
            setaPrazo(f0.adePrazoDet);
        }

        function exibePrzEditavel() {
            $('#prazoDeterminado').hide();
            $('#prazoEditavel').show();
            f0.adePrazoDet.onblur = function onblur(event) {
                return true;
            };
            f0.adePrazoEdt.onblur = function onblur(event) {
                fout(this);
                ValidaMascara(this);
                verificaPrazo(f0.adePrazoEdt);
            };
            setaPrazo(f0.adePrazoEdt);
        }

        function formLoad() {
            <% if (exibirNumPrestacoesRest) { %>
            <%if (prazosPossiveis == null || prazosPossiveis.isEmpty()) {%>
            exibePrzEditavel();
            <%} else {%>
            exibePrzDeterminado();
            <%}%>
            habilitaPrazo();
            <% } %>
            focusFirstField();
        }

        function setaPrazo(e) {
            f0.adePrazo.value = getFieldValue(e);
        }

        function habilitaPrazo() {
            if (f0.adeSemPrazo != null) {
                if (maxPrazo == 0) {       // somente prazo indeterminado
                    f0.adeSemPrazo.checked = true;
                    f0.adeSemPrazo.disabled = true;
                } else if (maxPrazo > 0) { // somente prazo determinado menor que maxPrazo
                    f0.adeSemPrazo.checked = false;
                    f0.adeSemPrazo.disabled = true;
                } else {                   // qualquer prazo
                    f0.adeSemPrazo.disabled = false;
                }
                f0.adePrazoEdt.disabled = f0.adeSemPrazo.checked;
                f0.adePrazoDet.disabled = f0.adeSemPrazo.checked;
            }
            if (f0.adePrazoEdt.disabled) {
                f0.adePrazoEdt.value = '';
            } else if (f0.adePrazoDet.disabled) {
                f0.adePrazoDet.value = '';
            } else {
                try {
                    f0.adePrazoEdt.focus();
                } catch (err) {
                }
                try {
                    f0.adePrazoDet.focus();
                } catch (err) {
                }
            }
            setaPrazo(f0.adePrazoEdt);
        }
		
        function alterarContrato() {
            <% if (exibirNumPrestacoesRest) {%>
            f0.adePrazoEdt.onblur = function onblur(event) {
                return true;
            };
            f0.adePrazoDet.onblur = function onblur(event) {
                return true;
            };
            <% } %>
            if (!vf_reservar_margem('true', <%= (boolean) permiteVlrNegativo %>)) {
                return false;
            }

            <%if (permiteCadVlrTac || permiteCadVlrIof || permiteCadVlrLiqLib || permiteCadVlrMensVinc &&
            exibirCadVlrMensVinc || exibirCadVlrIof || exibirCadVlrTac || exibirNovoValorLiq ) {%>
            var Controles = new Array("adeVlrTac", "adeVlrIof", "adeVlrLiquido", "adeVlrMensVinc");
            var Msgs = new Array('<hl:message key="mensagem.informe.ade.valor.tac"/>',
                '<hl:message key="mensagem.informe.ade.valor.iof"/>',
                '<hl:message key="mensagem.informe.ade.valor.liberado"/>',
                '<hl:message key="mensagem.informe.ade.valor.mensalidade"/>');

            if (!ValidaCampos(Controles, Msgs)) {
                return false;
            }
            <%}%>

            var adeVlr = parseFloat(parse_num(f0.adeVlr.value));
            var adePrz = parseFloat(parse_num(f0.adePrazo.value));
            var validaValorPrazo = usuPossuiAltAvancadaAde ? !getCheckedRadio('form1', 'valorPrazoSemLimite') : true;

            if (rsePrazo != '' && adePrz > parseInt(rsePrazo) && !permitePrazoMaiorContSer && validaValorPrazo) {
                alert('<hl:message key="mensagem.erro.prazo.maior.ser"/>'.replace("{0}", rsePrazo));
                return false;
            }

            <%if (!permiteAumentarVlr) {%>
            if ((adeVlr > parseFloat(adeVlrOld)) && validaValorPrazo) {
                alert('<hl:message key="mensagem.erro.valor.parcela.maior.atual"/>');
                focusFirstField();
                return false;
            }
            <%}%>
            <%if (!permiteReduzirVlr) {%>
            if (adeVlr < parseFloat(adeVlrOld)) {
                alert('<hl:message key="mensagem.erro.valor.parcela.menor.atual"/>');
                focusFirstField();
                return false;
            }
            <%}%>
            <%if (!permiteAumentarPrz) {%>
            if (!(f0.adeSemPrazo != null && f0.adeSemPrazo.checked == true) && (adePrz > adePrzOld) && validaValorPrazo) {
                alert('<hl:message key="mensagem.erro.prazo.maior.atual"/>');
                focusFirstField();
                return false;
            }
            <%}%>

            <% if (responsavel.isCsaCor() && exigeModalidadeOperacao) { %>
            if (f0.tdaModalidadeOp.value == null || f0.tdaModalidadeOp.value == '') {
                alert('<hl:message key="mensagem.erro.modalidade.operacao.obrigatorio"/>');
                return false;
            }
            <% }%>

            <% if (responsavel.isCsaCor() && exigeMatriculaSerCsa) { %>
            if (f0.tdaMatriculaCsa.value == null || f0.tdaMatriculaCsa.value == '') {
                alert('<hl:message key="mensagem.erro.matricula.csa.obrigatoria"/>');
                return false;
            }
            <%}%>

            var exigeSenhaAltAvancada = "true";
            <%if (usuPossuiAltAvancadaAde) {%>
            exigeSenhaAltAvancada = getCheckedRadio('form1', 'exigeSenhaAltAvancada');
            <%}%>

            if (exigeSenhaAltAvancada == "true") {
                if ((<%=(boolean)exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS)%>) ||
                    (<%=(boolean)exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_ALTERACAO_CONTRATOS_PARA_MAIOR)%> && ((adeVlr > parseFloat(adeVlrOld)) || (adePrz > 0 && (adePrz > adePrzOld)))) ||
                (<%=(boolean)exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_ALTERACAO_CAPITAL_DEVIDO_MAIOR)%> && ((adeVlr * adePrz) > (parseFloat(adeVlrOld) * adePrzOld))
            ))
                {

                    var permiteAlterarSemSenha = false;

                    var valorContrato = (adeVlr * adePrz);
                    var valorContratoOld = (parseFloat(adeVlrOld) * adePrzOld);

                    var valorParcelaDentroLimite = ((adeVlr / (adeVlrOld)) - 1) * 100 <= <%=percentualAlteracao%>;
                    var valorContratoDentroLimite = ((valorContrato / (valorContratoOld)) - 1) * 100 <= <%=percentualAlteracao%>;

                    permiteAlterarSemSenha =
                    <%=permiteAlterarSemSenhaTemp%> &&
                    valorParcelaDentroLimite && valorContratoDentroLimite;

                    if (f0.serLogin != null && trim(f0.serLogin.value) == '' && !permiteAlterarSemSenha) {
                        <%if (exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS)) {%>
                        alert('<hl:message key="mensagem.informe.ser.usuario"/>');
                        <%} else if (exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_ALTERACAO_CONTRATOS_PARA_MAIOR)) {%>
                        alert('<hl:message key="mensagem.alterar.consignacao.informe.ser.usuario.prazo.valor.maior"/>');
                        <%} else {%>
                        alert('<hl:message key="mensagem.alterar.consignacao.informe.ser.usuario.capital.devido.maior"/>');
                        <%}%>
                        f0.serLogin.focus();
                        return false;
                    }
                    if (f0.senha != null && trim(f0.senha.value) == '' && !permiteAlterarSemSenha) {
                        <%if (exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS)) {%>
                        alert('<hl:message key="mensagem.informe.ser.senha"/>');
                        <%} else if (exigeSenhaServidor.equals(CodedValues.EXIGE_SENHA_ALTERACAO_CONTRATOS_PARA_MAIOR)) {%>
                        if ((adeVlr > parseFloat(adeVlrOld)) && (adePrz > adePrzOld)) {
                            alert('<hl:message key="mensagem.alterar.consignacao.informe.ser.senha.prazo.valor.maior"/>');
                        } else if (adeVlr > parseFloat(adeVlrOld)) {
                            alert('<hl:message key="mensagem.alterar.consignacao.informe.ser.senha.valor.maior"/>');
                        } else if (adePrz > adePrzOld) {
                            alert('<hl:message key="mensagem.alterar.consignacao.informe.ser.senha.prazo.maior"/>');
                        }
                        <%} else {%>
                        alert('<hl:message key="mensagem.alterar.consignacao.informe.ser.senha.capital.devido.maior"/>');
                        <%}%>
                        f0.senha.focus();
                        return false;
                    }
                }
            }

            <%if (usuPossuiAltAvancadaAde || ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel))) {%>
            if (verificarOpcoesAvancadasAlteradas()) {
                var ControlesAvancados = new Array("incideMargem", "tmoCodigo", "adeObs");
                var MsgsAvancadas = new Array('<hl:message key="mensagem.informe.ade.incide.margem"/>',
                    '<hl:message key="mensagem.motivo.operacao.obrigatorio"/>',
                    '<hl:message key="mensagem.informe.observacao"/>');

                if (!ValidaCampos(ControlesAvancados, MsgsAvancadas)) {
                    return false;
                }
            }
            <%} else if (exigeMotivoOperacao || motivoOperacaoObrigatorio){%>
            var ControleMotivoOperacao = new Array("tmoCodigo", "adeObs");
            var Msgs = new Array('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>',
                '<hl:message key="mensagem.informe.observacao"/>');

            if (!ValidaCampos(ControleMotivoOperacao, Msgs)) {
                return false;
            }
            <%}%>

            if (!verificarCamposAdicionais()) {
                return false;
            }

            <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) { %>
            if (!verificaCamposTju()) {
                return false;
            }
            <% } %>

            if (f0.senha != null && trim(f0.senha.value) != '') {
                CriptografaSenha(f0.senha, f0.serAutorizacao, false);
            }

            if (<%=(boolean)mensagemMargemComprometida%>) {
                alert('<hl:message key="mensagem.alerta.margem.comprometida"/>');
            }
            <%if (!TextHelper.isNull(formaPagamento)) {%>
            var pagamentoViaBoletoCheck = document.getElementById("permiteDescontoViaBoleto");
            if (pagamentoViaBoletoCheck.checked) {
                if (<%=beneficiario !=null && !TextHelper.isNull(beneficiario.getBfcClassificacao()) && beneficiario.getBfcClassificacao().equals(CodedValues.BFC_CLASSIFICACAO_ESPECIAL)%>) {
                    alert('<hl:message key="mensagem.reservar.margem.dependente.via.boleto.nao.permitido"/>');
                    return false;
                }
            }
            <%}%>
            <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL, CodedValues.TPC_SIM, responsavel) && request.getAttribute("djuCodigo") != null && request.getAttribute("tipoDecisaoJudicial").equals(CodedValues.DECISAO_JUDICIAL_OPCAO_ALTERAR_CONSIGNACAO) && !djuRevogada) { %>
            var checkRevogacao = document.getElementById('djuRevogacao');
            console.log(checkRevogacao.checked);
            if (checkRevogacao.checked ? confirm('<%=TextHelper.forJavaScriptBlock(mensagem)%><hl:message key="mensagem.confirmacao.alteracao"/>') : confirm('<%=TextHelper.forJavaScriptBlock(mensagem)%><hl:message key="mensagem.confirmacao.alteracao.revogacao"/>')) {
                enableAll();
                f0.submit();
                return true;
            }
            <% } else { %>
            if (confirm('<%=TextHelper.forJavaScriptBlock(mensagem)%><hl:message key="mensagem.confirmacao.alteracao"/>')) {
                enableAll();
                f0.submit();
                return true;
            }
            <% } %>
            return false;
        }

        function verificarCamposAdicionais() {

            <% if (tdaList != null) { %>
            <% for (TransferObject tda : tdaList) { %>
            var sptExibe = '<%=(String) tda.getAttribute(Columns.SPT_EXIBE)%>';
            var cptExibe = '<%=(String) tda.getAttribute(Columns.CPT_EXIBE)%>';
            if ('O' == sptExibe || 'O' == cptExibe) {
                var elements = document.getElementsByName('TDA_<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>')

                if (elements[0].type == 'text') {
                    var value = elements[0].value;
                    if (!value || !value.trim()) {
                        alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
                        return false;
                    }
                } else if (elements[0].type == 'radio') {
                    var preenchido = false;
                    for (el of elements) {
                        if (el.checked) {
                            preenchido = true;
                            break;
                        }
                    }
                    if (!preenchido) {
                        alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
                        return false;
                    }
                }
            }
            <% } %>
            <% } %>

            return true;
        }

        function exibeAlteracaoAvancada() {
            var linhas = document.getElementsByTagName("tr");

            for (var i = 0; i < linhas.length; i++) {
                if (linhas[i].getAttribute('alteracaoAvancada') == 'true') {
                    linhas[i].style.display = '';
                }
            }
        }

        function escondeAlteracaoAvancada() {
            var linhas = document.getElementsByTagName("tr");

            for (var i = 0; i < linhas.length; i++) {
                if (linhas[i].getAttribute('alteracaoAvancada') == 'true') {
                    linhas[i].style.display = 'none';
                }
            }
        }

        function exibeEscondeAltAvancada() {
            if (f0.exibeAltAvancada == null || f0.exibeAltAvancada == undefined) {
                return;
            }

            if (f0.exibeAltAvancada.value == 'true') {
                f0.imgAvancada.src = '../img/menu/HM_More_black_top.gif';
                exibeAlteracaoAvancada();
                f0.exibeAltAvancada.value = 'false';
            } else if (f0.exibeAltAvancada.value == 'false') {
                f0.imgAvancada.src = '../img/menu/HM_More_black_bot.gif';
                escondeAlteracaoAvancada();
                f0.exibeAltAvancada.value = 'true';
            }
        }

        function verificarOpcoesAvancadasAlteradas() {
            var exigeMotivoOperacao = <%= TextHelper.forJavaScriptBlock(exigeMotivoOperacao) %>;
            var adeIncideMargem = f0.incideMargem.value;
            var validaMargem = getCheckedRadio('form1', 'validaMargem');
            var adeIntFolha = getCheckedRadio('form1', 'integraFolha');
            var alteraMargem = getCheckedRadio('form1', 'afetaMargem');
            var exigeSenhaAltAvancada = getCheckedRadio('form1', 'exigeSenhaAltAvancada');
            var permitePrzNaoCadastrado = getCheckedRadio('form1', 'permitePrzNaoCadastrado');
            var permiteAltEntidadesBloqueadas = getCheckedRadio('form1', 'permiteAltEntidadesBloqueadas');
            var alterarValorPrazoSemLimite = getCheckedRadio('form1', 'valorPrazoSemLimite');
            var criarNovoContratoDif = getCheckedRadio('form1', 'novoContratoDif');
            var calcularPrazoDifValor = getCheckedRadio('form1', 'calcularPrazoDifValor');
            var manterDifValorMargem = getCheckedRadio('form1', 'manterDifValorMargem');
            var incluiOcorrencia = getCheckedRadio('form1', 'insereOcorrencia');
            var validaLimiteAde = getCheckedRadio('form1', 'validaLimiteAde');
            var novaSituacaoContrato = f0.novoSadCodigo.value;
            var validaTaxaJuros = <%= paramSvcTaxa ? "getCheckedRadio('form1', 'validaTaxa')" : "padraoValidaTaxaJuros" %>;

            return exigeMotivoOperacao ||
                adeIncideMargem != padraoAdeIncideMargem ||
                validaMargem != padraoValidaMargem ||
                adeIntFolha != padraoAdeIntFolha ||
                alteraMargem != padraoAlteraMargem ||
                <%if (!exigeSenhaServidor.equals(CodedValues.NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS)) {%>
                exigeSenhaAltAvancada != padraoexigeSenhaAltAvancada ||
                <%}%>
                <%if (!permiteQualquerPrazo && (prazosPossiveis != null && !prazosPossiveis.isEmpty())) {%>
                permitePrzNaoCadastrado != padraoPermitePrzNaoCadastrado ||
                <%}%>
                permiteAltEntidadesBloqueadas != padraoPermiteAltEntidadesBloqueadas ||
                alterarValorPrazoSemLimite != padraoAlterarValorPrazoSemLimite ||
                criarNovoContratoDif != padraoCriarNovoContratoDif ||
                calcularPrazoDifValor != padraoCalcularPrazoDifValor ||
                manterDifValorMargem != padraoManterDifValorMargem ||
                incluiOcorrencia != padraoIncluiOcorrencia ||
                novaSituacaoContrato != padraoNovaSituacaoContrato ||
                validaTaxaJuros != padraoValidaTaxaJuros ||
                validaLimiteAde != padraoValidaLimiteAde;
        }

        function atualizaOpcoesAvancadas() {
            if (getCheckedRadio('form1', 'novoContratoDif') == 'true') {
                setCheckedRadio('form1', 'calcularPrazoDifValor', 'false');
                setCheckedRadio('form1', 'manterDifValorMargem', 'false');
                disableRadioButton('form1', 'calcularPrazoDifValor', true);
                disableRadioButton('form1', 'manterDifValorMargem', true);
            } else {
                disableRadioButton('form1', 'calcularPrazoDifValor', false);
                disableRadioButton('form1', 'manterDifValorMargem', false);
            }

            if (getCheckedRadio('form1', 'calcularPrazoDifValor') == 'true' ||
                getCheckedRadio('form1', 'manterDifValorMargem') == 'true') {
                setCheckedRadio('form1', 'novoContratoDif', 'false');
                disableRadioButton('form1', 'novoContratoDif', true);
            } else {
                disableRadioButton('form1', 'novoContratoDif', false);
            }
        }

        function verificaCamposTju() {
            var validaCamposTju = false;
            var Controles = new Array();
            var Msgs = new Array();

            // Verifica se algum campo foi informado, sendo assim, tipo de justiça é obrigatório
            Controles = new Array("tjuCodigo", "djuEstado", "djuComarca", "djuNumProcesso", "djuData", "djuTexto");
            for (var j = 0; j < Controles.length; j++) {
                var elementTju = document.getElementById(Controles[j]);

                if (elementTju != null && (elementTju.value != null && elementTju.value != '')) {
                    validaCamposTju = true;
                    break;
                }
            }

            // Verifica campos obrigatórios na campo sistema
            Controles = new Array();
            if (<%=tipoJusticaObrigatorio%> || validaCamposTju)
            {
                Controles.push("tjuCodigo");
                Msgs.push('<hl:message key="mensagem.informe.tju.codigo"/>');
            }

            if (<%=comarcaJusticaObrigatorio%>) {
                Controles.push("djuEstado");
                Msgs.push('<hl:message key="mensagem.informe.tju.estado"/>');
            }

            if (<%=comarcaJusticaObrigatorio%>) {
                Controles.push("djuComarca");
                Msgs.push('<hl:message key="mensagem.informe.tju.comarca"/>');
            }

            if (<%=numeroProcessoObrigatorio%>) {
                Controles.push("djuNumProcesso");
                Msgs.push('<hl:message key="mensagem.informe.num.processo"/>');
            }

            if (<%=dataDecisaoObrigatorio%>) {
                Controles.push("djuData");
                Msgs.push('<hl:message key="mensagem.informe.tju.data"/>');
            }

            if (<%=textoDecisaoObrigatorio%>) {
                Controles.push("djuTexto");
                Msgs.push('<hl:message key="mensagem.informe.tju.texto"/>');
            }

            if (<%=anexoObrigatorio%>) {
                Controles.push("FILE1");
                Msgs.push('<hl:message key="mensagem.informe.tju.anexo"/>');
            }

            if (!ValidaCampos(Controles, Msgs)) {
                return false;
            }

            return true;
        }

        <%if (servicoCompulsorio) {%>

        function abrirComposicaoCompulsorio() {
            postData('../v3/listarCompulsorio?acao=alterar&SVC_CODIGO=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>&RSE_CODIGO=<%=TextHelper.forJavaScriptBlock(rseCodigo)%>&ADE_CODIGO=<%=TextHelper.forJavaScriptBlock(adeCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>');
        }
        
        <%}%>
        
        <%if (request.getAttribute("omitirOpcoesAvancadas") != null && (usuPossuiAltAvancadaAde || ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)))) {%>
			function verificaLiquidaAdeSuspensa(){
				<% if (request.getAttribute("opcaoLiquidaRelacionamentoJudicialFixo") != null) { %>
						$('#modalConfirmaLiquidacao').modal('show');
				<% } else {%>
						alterarContrato();
				<% } %>
			}
			
			function alteraHiddenOpcaoLiquidaRelacionamentoJudicialFixo(valor){
				var hiddenliquidaRelacionamentoJudicial = document.getElementById('liquidaRelacionamentoJudicial');
				hiddenliquidaRelacionamentoJudicial.value = valor;
				
				alterarContrato();
			}
    	<%}%>
    </script>
    <script type="text/JavaScript">
        adeVlrOld = '<%=TextHelper.forJavaScriptBlock(retemMargemSvcPercentual && tipoVlr.equals(CodedValues.TIPO_VLR_PERCENTUAL) ? autdes.getAttribute(Columns.ADE_VLR_PERCENTUAL).toString() : autdes.getAttribute(Columns.ADE_VLR).toString())%>';
        adePrzOld = <%=TextHelper.forJavaScriptBlock(prazoRest)%>;
        maxPrazo = '<%=TextHelper.forJavaScriptBlock(maxPrazo)%>';
        rsePrazo = '<%=TextHelper.forJavaScriptBlock(autdes.getAttribute(Columns.RSE_PRAZO) != null ? autdes.getAttribute(Columns.RSE_PRAZO).toString() : "")%>';
        permitePrazoMaiorContSer = <%=TextHelper.forJavaScriptBlock(permitePrazoMaiorContSer)%>;
        usuPossuiAltAvancadaAde = <%=TextHelper.forJavaScriptBlock(usuPossuiAltAvancadaAde)%>;

        padraoAdeIncideMargem = '<%=TextHelper.forJavaScriptBlock(incMargem.toString())%>';
        padraoValidaMargem = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_VALIDA_MARGEM%>';
        padraoAdeIntFolha = '<%=TextHelper.forJavaScriptBlock(intFolha.toString())%>';
        padraoAlteraMargem = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_ALTERA_MARGEM%>';
        padraoexigeSenhaAltAvancada = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_EXIGE_SENHA%>';
        padraoPermitePrzNaoCadastrado = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_PERMITE_PRZ_NAO_CADASTRADO%>';
        padraoPermiteAltEntidadesBloqueadas = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_PERMITE_ALT_ENTIDADES_BLOQUEADAS%>';
        padraoAlterarValorPrazoSemLimite = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_ALTERA_VALOR_PRAZO_SEM_LIMITE%>';
        padraoCriarNovoContratoDif = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_CRIAR_NOVO_CONTRATO_DIF%>';
        padraoCalcularPrazoDifValor = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_CALCULAR_PRAZO_DIF_VALOR%>';
        padraoManterDifValorMargem = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_MANTER_DIF_VALOR_MARGEM%>';
        padraoIncluiOcorrencia = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_INCLUI_OCORRENCIA%>';
        padraoNovaSituacaoContrato = '';
        padraoValidaTaxaJuros = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_VALIDA_TAXA_JUROS%>';
        padraoValidaLimiteAde = '<%=(boolean)AlterarConsignacaoParametros.PADRAO_VALIDA_LIMITE_ADE%>';
        identificadorObrigatorio = <%=TextHelper.forJavaScriptBlock(identificadorAdeObrigatorio)%>;

        f0 = document.forms[0];
        window.onload = formLoad;
    </script>
    <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) { %>
    <script type="text/JavaScript">
        $(document).ready(function () {
            <% if (exibirComarcaJustica) { %>
            document.getElementById('djuEstado').setAttribute("onchange", "listarCidades(this.value)");
            document.getElementById('djuComarca').setAttribute("onchange", "setCidCodigo(this.value)");
            <% } %>
            <% if (request.getAttribute("omitirOpcoesAvancadas") != null) { %>
            $("#opcoesAvancadasLink").click();
            <% } %>
        });

        function listarCidades(codEstado) {

            if (!codEstado) {
                document.getElementById('djuComarca').innerText = "";
                $("[name='cidCodigo']").val("");
                return;
            } else {
                $.ajax({
                    type: 'post',
                    url: "../v3/listarCidades?acao=<%=request.getAttribute("acaoListarCidades")%>&codEstado=" + codEstado + "&_skip_history_=true",
                    async: true,
                    contentType: 'application/json',
                    success: function (data) {

                        var options = "<option value>" + "<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" + "</option> ";
                        var cidades = null;
                        var nomeCidade = null;
                        var codigoCidade = null;

                        data.forEach(function (objeto) {
                            codigoCidade = objeto.atributos['<%=Columns.CID_CODIGO_IBGE%>'];
                            nomeCidade = objeto.atributos['<%=Columns.CID_NOME%>'];
                            options = options.concat('<option value="').concat(objeto.atributos['<%=Columns.CID_CODIGO%>']).concat('">').concat(nomeCidade).concat('</option>');
                        });

                        document.getElementById('djuComarca').innerHTML = options;
                    },
                    error: function (response) {
                        console.log(response.statusText);
                    }
                });
            }
        }

        function setCidCodigo(cidCodigo) {
            $("[name='cidCodigo']").val(cidCodigo);
        }
    </script>
    <% } %>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>