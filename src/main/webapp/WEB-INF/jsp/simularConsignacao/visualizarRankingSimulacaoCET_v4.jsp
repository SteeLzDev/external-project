<%--
* <p>Title: simulacao</p>
* <p>Description: Pagina de resultado da simulaçio de emprestimos</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.zetra.econsig.helper.criptografia.RSA" %>
<%@ page import="com.zetra.econsig.helper.financeiro.CDCHelper" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.zetra.econsig.webservice.rest.request.CsaListInfoRequest" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

    List<CsaListInfoRequest> inforSimuEmail = new ArrayList<>();

    String titulo = (String) request.getAttribute("titulo");
    float nroColunas = (Float) request.getAttribute("floatQtdeColunasSimulacao");
    String przVlr = (String) request.getAttribute("PRZ_VLR");
    String vlrLiberado = (String) request.getAttribute("VLR_LIBERADO");
    String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");
    boolean temCET = (Boolean) request.getAttribute("temCET");
    boolean vlrOk = (Boolean) request.getAttribute("vlrOk");
    boolean simulacaoPorTaxaJuros = (Boolean) request.getAttribute("simulacaoPorTaxaJuros");
    boolean simulacaoMetodoMexicano = (Boolean) request.getAttribute("simulacaoMetodoMexicano");
    boolean simulacaoMetodoBrasileiro = (Boolean) request.getAttribute("simulacaoMetodoBrasileiro");
    boolean leilaoReverso = (request.getAttribute("leilaoReverso") != null && (Boolean) request.getAttribute("leilaoReverso")) && (request.getAttribute("posibilitaLeilaoReverso") != null && (Boolean) request.getAttribute("posibilitaLeilaoReverso"));
    boolean podeSolicitar = (Boolean) request.getAttribute("podeSolicitar");
    String adeVlr = (String) request.getAttribute("ADE_VLR");
    String rseCodigo = (String) request.getAttribute("RSE_CODIGO");
    String svcCodigo = (String) request.getAttribute("SVC_CODIGO");
    String orgCodigo = (String) request.getAttribute("ORG_CODIGO");
    List<TransferObject> simulacao = (List<TransferObject>) request.getAttribute("simulacao");
    Short numParcelas = (Short) request.getAttribute("numParcelas");
    Integer qtdeConsignatariasSimulacao = (Integer) request.getAttribute("qtdeConsignatariasSimulacao");
    boolean simulacaoAdeVlr = (Boolean) request.getAttribute("SIMULACAO_POR_ADE_VLR");
    boolean temAsterisco = false;
    int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    Date dataAtual = DateHelper.getSystemDatetime();
    HashMap<String, TransferObject> hashCsaPermiteContato = (HashMap<String, TransferObject>) request.getAttribute("hashCsaPermiteContato");

    boolean fun_532 = responsavel.temPermissao(CodedValues.FUN_SOLICITAR_PORTABILIDADE);
    boolean fun_303 = responsavel.temPermissao(CodedValues.FUN_SIMULAR_RENEGOCIACAO);

    boolean podeMostrarMargem = (Boolean) request.getAttribute("podeMostrarMargem");
    ExibeMargem exibeMargem = (ExibeMargem) request.getAttribute("exibeMargem");
    String margemConsignavel = (String) request.getAttribute("margemConsignavel");
    BigDecimal rseMargemRest = (BigDecimal) request.getAttribute("rseMargemRest");

    float colspan = 6;
    if (!simulacaoPorTaxaJuros) {
        colspan = (float) (4 * nroColunas);
    } else {
        colspan = (float) (9 * nroColunas);
    }

    boolean tpsExigenciaConfirmacaoLeituraServidor = (Boolean) request.getAttribute("tpsExigenciaConfirmacaoLeituraServidor");
    boolean origem = request.getAttribute("origem") != null ? (Boolean) request.getAttribute("origem") : false;
    boolean taxaJurosManCSA = (Boolean) request.getAttribute("taxaJurosManCSA");
	boolean exibeCETMinMax = (Boolean) request.getAttribute("exibeCETMinMax");
	String serNome = (String) request.getAttribute("serNome");
	boolean vlrLiberadoOk = (Boolean) request.getAttribute("vlrLiberadoOk");

    java.security.KeyPair keyPair = (java.security.KeyPair) request.getAttribute("keyPair");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
    <hl:message key="rotulo.simulacao.apenas.para.titulo"
                arg0="<%=TextHelper.forHtmlAttribute(titulo.toUpperCase())%>"/>
</c:set>
<c:set var="bodyContent">
    <form NAME="form1" METHOD="post" ACTION="../v3/simularRenegociacao">
        <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
        <hl:htmlinput name="CSA_CODIGO" type="hidden" di="CSA_CODIGO" value=""/>
        <hl:htmlinput name="CSA_NOME" type="hidden" di="CSA_NOME" value=""/>
        <hl:htmlinput name="VLR_LIBERADO" type="hidden" di="VLR_LIBERADO" value=""/>
        <hl:htmlinput name="ADE_VLR" type="hidden" di="ADE_VLR" value=""/>
        <hl:htmlinput name="CSA_IDENTIFICADOR" type="hidden" di="CSA_IDENTIFICADOR" value=""/>
        <hl:htmlinput name="SVC_CODIGO" type="hidden" di="SVC_CODIGO" value=""/>
        <hl:htmlinput name="SVC_DESCRICAO" type="hidden" di="SVC_DESCRICAO" value=""/>
        <hl:htmlinput name="SVC_IDENTIFICADOR" type="hidden" di="SVC_IDENTIFICADOR" value=""/>
		<hl:htmlinput name="VLR_LIBERADO_OK" type="hidden" di="VLR_LIBERADO_OK" value=""/>
        <hl:htmlinput name="acao" type="hidden" di="acao" value="pesquisarConsignacao"/>
        <hl:htmlinput name="funcao" type="hidden" di="funcao"
                      value="<%=(String)CodedValues.FUN_SIMULAR_RENEGOCIACAO%>"/>
        <hl:htmlinput name="RSE_MATRICULA" type="hidden" di="RSE_MATRICULA"
                      value="<%=TextHelper.forHtmlAttribute(responsavel.getRseMatricula() )%>"/>
        <% if (responsavel.isSer()) { %>
        <hl:htmlinput name="RSE_CODIGO" type="hidden" di="RSE_CODIGO"
                      value="<%=TextHelper.forHtmlAttribute(responsavel.getRseCodigo() )%>"/>
        <% } %>
        <hl:htmlinput name="TIPO_LISTA" type="hidden" di="TIPO_LISTA" value="pesquisa"/>
    </form>
	<% if (exibeCETMinMax) { %>
	<div id="alertaServidorCETJuros" class="alert alert-warning mb-1" role="alert">
	  	<p class="mb-0"><%=ApplicationResourcesHelper.getMessage("mensagem.alerta.servidor.simulacao.taxa.minima.maxima", responsavel, serNome)%></p>
	</div>	
	<% } %>
    <div class="card">
        <div class="card-header hasIcon">
            <span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular-config"></use></svg></span>
            <h2 class="card-header-title"><hl:message key="rotulo.simulacao.dados"/></h2>
            <span class="ultima-edicao"><%=ApplicationResourcesHelper.getMessage("mensagem.informacao.simulacao.realizada.em", responsavel, DateHelper.toDateTimeString(dataAtual))%></span>
        </div>
        <div class="card-body">
            <dl class="row data-list">
                <dt class="col-6 col-sm-3"><hl:message key="rotulo.servico.singular"/>:</dt>
                <dd class="col-6 col-sm-9"><%=TextHelper.forHtmlAttribute(titulo.toUpperCase())%>
                </dd>
                <% if (!adeVlr.isEmpty()) { %>
                <dt class="col-6 col-sm-3"><hl:message key="rotulo.consignacao.valor.parcela"/>:</dt>
                <dd class="col-6 col-sm-9"><hl:message
                        key="rotulo.moeda"/>&nbsp;<%=NumberHelper.reformat(adeVlr, "en", NumberHelper.getLang(), true)%>
                </dd>
                <% } else { %>
                <dt class="col-6 col-sm-3"><hl:message key="rotulo.consignacao.valor.solicitado"/>:</dt>
                <dd class="col-6 col-sm-9"><hl:message
                        key="rotulo.moeda"/>&nbsp;<%=NumberHelper.reformat(vlrLiberado, "en", NumberHelper.getLang(), true)%>
                </dd>
                <% } %>
                <dt class="col-6 col-sm-3"><hl:message key="rotulo.consignacao.prazo.extenso"/>:</dt>
                <dd class="col-6 col-sm-9"><%=przVlr%>
                </dd>
                <% if (podeMostrarMargem) { %>
                <dt class="col-6 col-sm-3"><hl:message key="rotulo.simulacao.margem.consignavel"/>:</dt>
                <dd class="col-6 col-sm-9"><%=TextHelper.forHtmlAttribute(request.getAttribute("tipoVlrMargemDisponivel"))%>&nbsp;<%=(String) (exibeMargem.isSemRestricao() || rseMargemRest.doubleValue() > 0 ? NumberHelper.reformat(margemConsignavel, "en", NumberHelper.getLang()) : "0,00")%>
                </dd>
                <% } %>
            </dl>
            <div style="float: right">
                <% if(responsavel.temPermissao(CodedValues.FUN_ENVIAR_EMAIL_SIMULACAO_SOLICITACAO_ADE)){ %>
                <a id="enviaEmailPDF" class="btn btn-outline-danger" onclick="modalEnviarEmail()" href="#no-back">
                    <hl:message key="rotulo.botao.enviar.email"/>
                </a>
                <%}%>
            <a class="btn btn-primary" href="#no-back"
               onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')">
                <svg width="25">
                    <use xlink:href="#i-simular"></use>
                </svg>
                <hl:message
                        key="rotulo.botao.alterar"/> <%=ApplicationResourcesHelper.getMessage("rotulo.simulacao.titulo", responsavel).toLowerCase()%>
            </a>
         </div>
       </div>
    </div>
    <% if (tpsExigenciaConfirmacaoLeituraServidor) { %>
    <div class="alert alerta-checkbox" role="alert">
        <input class="form-check-input " type="checkbox" id="exigenciaConfirmacaoLeitura"
               name="exigenciaConfirmacaoLeitura">
        <label for="exigenciaConfirmacaoLeitura" class="form-check-label font-weight-bold"><hl:message
                key="mensagem.informacao.simulacao.exigencia.confirmacao.leitura"/></label>
    </div>
    <% } %>
    <div class="card">
        <div class="card-header hasIcon">
            <span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular-config"></use></svg></span>
            <h2 class="card-header-title"><hl:message key="rotulo.simulacao.resultado"/></h2>
        </div>
        <div class="row">
            <div class="col-md-6 gap-2 col-lg-6 divButton">
                <button onclick="filtroPorOperacao(1)" type="button" id="nv" class="btn text-bg-blue"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel)%>
                </button>
                <% if (fun_303) { %>
                <button onclick="filtroPorOperacao(2)" type="button" id="rn" class="btn text-bg-red"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", responsavel)%>
                </button>
                <% } %>
                <% if (fun_532) { %>
                <button onclick="filtroPorOperacao(3)" type="button" id="pr" class="btn text-bg-orange"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.compra", responsavel)%>
                </button>
                <% } %>
            </div>
            <div class="col-md-3 col-lg-2"></div>
            <div class="col-sm-12 col-md-3 col-md-12 col-lg-3">
            </div>
        </div>
        <div class="card-body table-responsive">
            <table id="table" class="table table-striped table-hover table-ranking">
                <thead>
                <tr>
                    <th scope="col"><hl:message key="rotulo.consignacao.ranking"/></th>
                    <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
                    <% if (!adeVlr.equals("")) { %>
                    <th scope="col"><hl:message key="rotulo.consignacao.valor.solicitado"/></th>
                    <% } else { %>
                    <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela"/></th>
                    <% } %>
                    <% if (simulacaoPorTaxaJuros) { %>
                    <% if (temCET) { %>
                    <th scope="col"><hl:message key="rotulo.consignacao.cet"/></th>
                    <th scope="col"><hl:message key="rotulo.consignacao.cet.anual"/></th>
                    <%if (taxaJurosManCSA) { %>
                    <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros"/></th>
                    <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros.anual"/></th>
                    <%} %>
                    <% } else { %>
                    <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros"/></th>
                    <% if (simulacaoMetodoMexicano) { %>
                    <th scope="col"><hl:message key="rotulo.consignacao.valor.cat.abreviado"/> (<hl:message
                            key="rotulo.porcentagem"/>)
                    </th>
                    <th scope="col"><hl:message key="rotulo.consignacao.valor.iva.abreviado"/> (<hl:message
                            key="rotulo.moeda"/>)
                    </th>
                    <% } else if (simulacaoMetodoBrasileiro) { %>
                    <th scope="col"><hl:message key="rotulo.consignacao.valor.tac.abreviado"/> (<hl:message
                            key="rotulo.moeda"/>)
                    </th>
                    <th scope="col"><hl:message key="rotulo.consignacao.valor.iof.abreviado"/> (<hl:message
                            key="rotulo.moeda"/>)
                    </th>
                    <% } %>
                    <% } %>
                    <% } %>
                    <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
                <tr>
                    <th class="th-info" scope="col" colspan="<%=colspan%>"><hl:message
                            key="mensagem.simulacao.resultado" arg0="<%=DateHelper.toDateTimeString(dataAtual)%>"/></th>
                </tr>
                </thead>
                <tbody>
					<%!
						public String obterVlrFormatado(String minimo, String maximo, String moeda, boolean isTooltip, boolean exibeFaixaMinima) {
							String vlrFormatado = isTooltip ? "Valor: " : "";
							if (exibeFaixaMinima && minimo != null) {
						        vlrFormatado += moeda + minimo + " à ";
							}
							vlrFormatado += moeda + maximo;
							
							return vlrFormatado;
						}
					%>
                        <%
                    String csa_codigo, csa_nome, cft_codigo, dtjCodigo_79, ranking, dtjCodigo_303, dtjCodigo_532;
                    String totalPagar = "", cat = "", iva = ""; // simulacaoMetodoMexicano
                    String csa_whatsapp = "", csa_email_contato ="", csa_email = "", csa_email_usar = "", csa_contato_tel = "", tipo_contato = "", csa_txt_contato = "";;
                    BigDecimal cft_vlr, cft_vlr_303, cft_vlr_532;
					BigDecimal cft_vlr_minimo, cft_vlr_minimo_303, cft_vlr_minimo_532;
                    BigDecimal cft_vlr_ref;
                    Object objMotivoIndisponibilidade = null;
                    String motivoIndisponibilidade = null;
                    String textoMotivoIndisponibilidade = null;
                    CustomTransferObject coeficiente = null;
                    Iterator<TransferObject> it = simulacao.iterator();
                    Integer relevancia = null;
                    boolean renegociacao = false;
                    boolean portabilidade = false;

                    while (it.hasNext()) {
                        String svc_identificador, svc_descricao, csa_identificador, vlr_ade, vlr_ade_79, vlr_ade_303, vlr_ade_532, vlrLiberado_param, vlr_simulado, vlr_simulado_79 = null, vlr_simulado_303 = null, vlr_simulado_532 = null;
                        String vlr_ade_minimo = null, vlr_ade_minimo_79 = null, vlr_ade_minimo_303 = null, vlr_ade_minimo_532 = null, vlr_simulado_minimo = null, vlr_simulado_minimo_79 = null, vlr_simulado_minimo_303 = null, vlr_simulado_minimo_532 = null;
						String tac = "", tac_79 = "", tac_303 = "", tac_532 = "", iof = "", iof_79 = "", iof_303 = "", iof_532 = "";
                        String str_cft_vlr, str_cft_vlr_303, str_cft_vlr_532, str_cft_vlr_minimo = null, str_cft_vlr_minimo_303 = null, str_cft_vlr_minimo_532 = null, str_cft_vlr_ref, cetAnual, cetAnual303, cetAnual532, jurosAnual, svcCodigoItem;
                        coeficiente = (CustomTransferObject) it.next();
                        csa_codigo = (String) coeficiente.getAttribute(Columns.CSA_CODIGO);
                        svcCodigoItem = (String) coeficiente.getAttribute(Columns.SVC_CODIGO);
                        csa_nome = TextHelper.forHtmlAttribute((String) coeficiente.getAttribute("TITULO"));
                        renegociacao = coeficiente.getAttribute("RENEGOCIACAO") != null && coeficiente.getAttribute("RENEGOCIACAO").equals(true);
                        portabilidade = coeficiente.getAttribute("PERMITE_PORTABILIDADE") != null && coeficiente.getAttribute("PERMITE_PORTABILIDADE").equals(true);
                        svc_descricao = coeficiente.getAttribute(Columns.SVC_DESCRICAO) != null ? coeficiente.getAttribute(Columns.SVC_DESCRICAO).toString() : "";
                        svc_identificador = coeficiente.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? coeficiente.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "";
                        csa_identificador = coeficiente.getAttribute(Columns.CSA_IDENTIFICADOR) != null ? coeficiente.getAttribute(Columns.CSA_IDENTIFICADOR).toString() : "";
                        objMotivoIndisponibilidade = coeficiente.getAttribute("MOTIVO_INDISPONIBILIDADE");
                        motivoIndisponibilidade = objMotivoIndisponibilidade != null ? String.valueOf((Integer) objMotivoIndisponibilidade) : "";
                        if (!TextHelper.isNull(motivoIndisponibilidade)) {
                            try {
                                if ("6".equals(motivoIndisponibilidade)) {
                                    textoMotivoIndisponibilidade = ApplicationResourcesHelper.getMessage("mensagem.simulacao.informacao.texto." + motivoIndisponibilidade, responsavel, String.valueOf(qtdeConsignatariasSimulacao));
                                } else {
                                    textoMotivoIndisponibilidade = ApplicationResourcesHelper.getMessage("mensagem.simulacao.informacao.texto." + motivoIndisponibilidade, responsavel);
                                }
                            } catch (Exception ex) {
                                textoMotivoIndisponibilidade = ApplicationResourcesHelper.getMessage("mensagem.simulacao.nao.disponivel", responsavel);
                            }
                        }
                        cft_codigo = (String) coeficiente.getAttribute(Columns.CFT_CODIGO);
                        dtjCodigo_79 = coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ?  (String) coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SIM_CONSIGNACAO) : null;
                        dtjCodigo_303 = coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) != null ?  (String) coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) : null;
                        dtjCodigo_532 = coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ?  (String) coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) : null;
                        vlr_ade = coeficiente.getAttribute("VLR_PARCELA").toString();
                        vlr_ade_79 =  coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : null;
                        vlr_ade_303 = coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) != null ? coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO).toString() : null;
                        vlr_ade_532 = coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : null;
						vlrLiberado_param = coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : coeficiente.getAttribute("VLR_LIBERADO").toString();
                        ranking = (String) coeficiente.getAttribute("RANKING");
                        vlrOk = ((Boolean) coeficiente.getAttribute("OK")).booleanValue();
                        cft_vlr = new BigDecimal(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : coeficiente.getAttribute(Columns.CFT_VLR).toString());
                        cft_vlr_303 = new BigDecimal(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) != null ? coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO).toString() : coeficiente.getAttribute(Columns.CFT_VLR).toString());
                        cft_vlr_532 = new BigDecimal(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : coeficiente.getAttribute(Columns.CFT_VLR).toString());
                        str_cft_vlr = NumberHelper.format(cft_vlr.doubleValue(), NumberHelper.getLang(), 2, 8);
                        str_cft_vlr_303 = NumberHelper.format(cft_vlr_303.doubleValue(), NumberHelper.getLang(), 2, 8);
                        str_cft_vlr_532 = NumberHelper.format(cft_vlr_532.doubleValue(), NumberHelper.getLang(), 2, 8);
                        cft_vlr_ref = !TextHelper.isNull(coeficiente.getAttribute(Columns.CFT_VLR_REF)) ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_REF).toString()) : null;
                        str_cft_vlr_ref = !TextHelper.isNull(cft_vlr_ref) ? NumberHelper.format(cft_vlr_ref.doubleValue(), NumberHelper.getLang(), 2, 8) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros.nao.registrado", responsavel);

						if(exibeCETMinMax) {
							vlr_ade_minimo = coeficiente.getAttribute("VLR_PARCELA_MINIMA") != null ? coeficiente.getAttribute("VLR_PARCELA_MINIMA").toString() : null;
		                    vlr_ade_minimo_79 =  coeficiente.getAttribute("VLR_PARCELA_MINIMA_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? coeficiente.getAttribute("VLR_PARCELA_MINIMA_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : null;
		                    vlr_ade_minimo_303 = coeficiente.getAttribute("VLR_PARCELA_MINIMA_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) != null ? coeficiente.getAttribute("VLR_PARCELA_MINIMA_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO).toString() : null;
		                    vlr_ade_minimo_532 = coeficiente.getAttribute("VLR_PARCELA_MINIMA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? coeficiente.getAttribute("VLR_PARCELA_MINIMA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : null;
							Object valor = coeficiente.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? coeficiente.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) : coeficiente.getAttribute(Columns.CFT_VLR_MINIMO);
							cft_vlr_minimo = valor != null ? new BigDecimal(valor.toString()) : null;
							
							Object valor303 = coeficiente.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) != null ? coeficiente.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) : coeficiente.getAttribute(Columns.CFT_VLR_MINIMO);
							cft_vlr_minimo_303 = valor303 != null ? new BigDecimal(valor303.toString()) : null;
							
							Object valor532 = coeficiente.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? coeficiente.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) : coeficiente.getAttribute(Columns.CFT_VLR_MINIMO);
							cft_vlr_minimo_532 = valor532 != null ? new BigDecimal(valor532.toString()) : null;
	
							str_cft_vlr_minimo = !TextHelper.isNull(cft_vlr_minimo) ? NumberHelper.format(cft_vlr_minimo.doubleValue(), NumberHelper.getLang(), 2, 8) : null;
							str_cft_vlr_minimo_303 = !TextHelper.isNull(cft_vlr_minimo_303) ? NumberHelper.format(cft_vlr_minimo_303.doubleValue(), NumberHelper.getLang(), 2, 8) : null;
							str_cft_vlr_minimo_532 = !TextHelper.isNull(cft_vlr_minimo_532) ? NumberHelper.format(cft_vlr_minimo_532.doubleValue(), NumberHelper.getLang(), 2, 8) : null;
						}
                        cetAnual = CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr);
                        cetAnual303 = CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr_303);
                        cetAnual532 = CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr_532);
                        jurosAnual = !TextHelper.isNull(cft_vlr_ref) ? CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr_ref) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros.nao.registrado", responsavel);
                        relevancia = (Integer) coeficiente.getAttribute("RELEVANCIA");

                        temAsterisco |= (csa_nome.indexOf('*') != -1);

                        if (simulacaoPorTaxaJuros) {
                            if (simulacaoMetodoMexicano) {
                                cat = NumberHelper.reformat((coeficiente.getAttribute("CAT") != null) ? coeficiente.getAttribute("CAT").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                iva = NumberHelper.reformat((coeficiente.getAttribute("IVA") != null) ? coeficiente.getAttribute("IVA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                totalPagar = NumberHelper.reformat((coeficiente.getAttribute("TOTAL_PAGAR") != null) ? coeficiente.getAttribute("TOTAL_PAGAR").toString() : "0.00", "en", NumberHelper.getLang(), true);
                            } else if (simulacaoMetodoBrasileiro) {
                                tac = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA") != null) ? coeficiente.getAttribute("TAC_FINANCIADA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                tac_79 = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SIM_CONSIGNACAO) != null) ? (String) coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : tac, "en", NumberHelper.getLang(), true);
                                tac_303 = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) != null) ? (String) coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO).toString() : tac, "en", NumberHelper.getLang(), true);
                                tac_532 = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null) ? (String) coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : tac, "en", NumberHelper.getLang(), true);
                                iof = NumberHelper.reformat((coeficiente.getAttribute("IOF") != null) ? coeficiente.getAttribute("IOF").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                iof_79 = NumberHelper.reformat((coeficiente.getAttribute("IOF_" + CodedValues.FUN_SIM_CONSIGNACAO) != null) ? (String) coeficiente.getAttribute("IOF_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : tac, "en", NumberHelper.getLang(), true);
                                iof_303 = NumberHelper.reformat((coeficiente.getAttribute("IOF_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) != null) ? (String) coeficiente.getAttribute("IOF_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO).toString() : tac, "en", NumberHelper.getLang(), true);
                                iof_532 = NumberHelper.reformat((coeficiente.getAttribute("IOF_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null) ? (String) coeficiente.getAttribute("IOF_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : tac, "en", NumberHelper.getLang(), true);
                            }
                        }

                        if (!adeVlr.equals("")) {
                            vlr_simulado = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO")).doubleValue(), NumberHelper.getLang(), true);
                            vlr_simulado_79 = coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO)).doubleValue(), NumberHelper.getLang(), true) : null;
                            vlr_simulado_303 = coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) != null ? NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO)).doubleValue(), NumberHelper.getLang(), true) : vlr_simulado;
                            vlr_simulado_532 = coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE)).doubleValue(), NumberHelper.getLang(), true) : vlr_simulado;
							if(exibeCETMinMax) {
								vlr_simulado_minimo = coeficiente.getAttribute("VLR_LIBERADO_MINIMO") != null ? NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_MINIMO")).doubleValue(), NumberHelper.getLang(), true) : null;
	                            vlr_simulado_minimo_79 = coeficiente.getAttribute("VLR_LIBERADO_MINIMO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_MINIMO_" + CodedValues.FUN_SIM_CONSIGNACAO)).doubleValue(), NumberHelper.getLang(), true) : null;
	                            vlr_simulado_minimo_303 = coeficiente.getAttribute("VLR_LIBERADO_MINIMO_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO) != null ? NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_MINIMO_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO)).doubleValue(), NumberHelper.getLang(), true) : vlr_simulado_minimo;
	                            vlr_simulado_minimo_532 = coeficiente.getAttribute("VLR_LIBERADO_MINIMO_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_MINIMO_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE)).doubleValue(), NumberHelper.getLang(), true) : vlr_simulado_minimo;
							}
                        } else {
                            if (vlr_ade.equals(new BigDecimal(Double.MAX_VALUE).toString())) {
                                vlr_simulado = "-";
                            } else {
                                vlr_simulado = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA")).doubleValue(), NumberHelper.getLang(), true);
                            }
							if (vlr_ade_minimo != null && !vlr_ade_minimo.equals(new BigDecimal(Double.MAX_VALUE).toString())) {
								vlr_simulado_minimo = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA_MINIMA")).doubleValue(), NumberHelper.getLang(), true);
							}
                            if (vlr_ade_79 != null) {
                                if (vlr_ade_79.equals(new BigDecimal(Double.MAX_VALUE).toString())) {
                                    vlr_simulado_79 = "-";
                                } else {
                                    vlr_simulado_79 = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO)).doubleValue(), NumberHelper.getLang(), true);
                                }
								if (vlr_ade_minimo_79 != null && !vlr_ade_minimo_79.equals(new BigDecimal(Double.MIN_VALUE).toString())) {
									vlr_simulado_minimo_79 = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA_MINIMA_" + CodedValues.FUN_SIM_CONSIGNACAO)).doubleValue(), NumberHelper.getLang(), true);
								}
                            }
                            if (vlr_ade_303 != null) {
                                if (vlr_ade_303.equals(new BigDecimal(Double.MAX_VALUE).toString())) {
                                    vlr_simulado_303 = "-";
                                } else {
                                    vlr_simulado_303 = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO)).doubleValue(), NumberHelper.getLang(), true);
                                }
								if (vlr_ade_minimo_303 != null && !vlr_ade_minimo_303.equals(new BigDecimal(Double.MIN_VALUE).toString())) {
									vlr_simulado_minimo_303 = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA_MINIMA_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO)).doubleValue(), NumberHelper.getLang(), true);
								}
                            }
                            if (vlr_ade_532 != null) {
                                if (vlr_ade_532.equals(new BigDecimal(Double.MAX_VALUE).toString())) {
                                    vlr_simulado_532 = "-";
                                } else {
                                    vlr_simulado_532 = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE)).doubleValue(), NumberHelper.getLang(), true);
                                }
								if (vlr_ade_minimo_532 != null && !vlr_ade_minimo_532.equals(new BigDecimal(Double.MIN_VALUE).toString())) {
									vlr_simulado_minimo_532 = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA_MINIMA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE)).doubleValue(), NumberHelper.getLang(), true);
								}
                            }
                        }

                        String chaveSeguranca = RSA.encrypt((vlr_ade_79 != null ? vlr_ade_79 : vlr_ade) + "|" + vlrLiberado_param + "|" + numParcelas, keyPair.getPublic());

                        if (hashCsaPermiteContato.get(csa_codigo) != null){
                            TransferObject consignatariaContato = hashCsaPermiteContato.get(csa_codigo);
                            csa_whatsapp = (String) consignatariaContato.getAttribute(Columns.CSA_WHATSAPP);
                            csa_email_contato = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL_CONTATO);
                            csa_email = (String) consignatariaContato.getAttribute(Columns.CSA_EMAIL);
                            tipo_contato = (String) consignatariaContato.getAttribute(Columns.PCS_VLR);
                            csa_email_usar = !TextHelper.isNull(csa_email_contato) ? csa_email_contato : csa_email;
                            if(!TextHelper.isNull(csa_whatsapp)){
                                csa_whatsapp = LocaleHelper.formataCelular(csa_whatsapp);
                            }
                            csa_contato_tel = (String) consignatariaContato.getAttribute(Columns.CSA_TEL);
                            csa_txt_contato = (String) consignatariaContato.getAttribute(Columns.CSA_TXT_CONTATO);
                            }

                            if (responsavel.temPermissao(CodedValues.FUN_ENVIAR_EMAIL_SIMULACAO_SOLICITACAO_ADE) && vlrOk) {
                                CsaListInfoRequest infor = new CsaListInfoRequest();
                                infor.setCsaNome(csa_nome);
                                infor.setRanking(Integer.parseInt(ranking));
                                if (hashCsaPermiteContato.get(csa_codigo) != null) {
                                    infor.setCsaEmail(csa_email_contato);
                                    infor.setCsaWhatsapp(csa_whatsapp);
                                    infor.setCsaTxt(csa_txt_contato);
                                }
                                inforSimuEmail.add(infor);
                            }
                %>
                <tr <% if (!vlrOk) { %>class="indisponivel"<% } %>>
                    <td><span class="p-2">
									<svg class="<%=(!vlrOk ? "i-indisponivel" : "i-disponivel")%>"><use xlink:href="#<%=(!vlrOk ? "i-status-x" : "i-status-v")%>"></use></svg>
                                        <%=TextHelper.forHtmlContent(ranking)%><span class="ordinal"></span></span>
                    </td>
                    <td>
                        <div class="d-flex justify-content-between bd-highlight">
                            <div class="p-2"><%=TextHelper.forHtmlContent(csa_nome)%><%if (!TextHelper.isNull(motivoIndisponibilidade)) {%> <sup><%=motivoIndisponibilidade%>
                            </sup><%}%>
                            </div>
                            <% if (relevancia.intValue() != Integer.valueOf(CodedValues.CSA_NAO_PROMOVIDA).intValue() && vlrOk) { %>
                            <div class="d-flex flex-nowrap"><span class="p-2 alert alert-success btn-oferta mb-0"
                                                                  id="verdict"><hl:message
                                    key="rotulo.param.svc.relevancia.oferta.patrocinada"/></span></div>
                            <%} %>
                        </div>
                    </td>
					<%			
					String moeda = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel);								
					String vlrMin = vlr_simulado_minimo_79 != null ? vlr_simulado_minimo_79 : vlr_simulado_minimo;
				    String vlrMax = vlr_simulado_79 != null ? vlr_simulado_79 : vlr_simulado;
				    String tooltipVlr = obterVlrFormatado(vlrMin, vlrMax, moeda, true, exibeCETMinMax);
					String vlrFormatado = obterVlrFormatado(vlrMin, vlrMax, moeda, false, exibeCETMinMax);
					%>
                    <td><div class="p-2"><span class="badge nv text-bg-blue"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-blue"
							  data-bs-title="<%= tooltipVlr %>">
							  <%= TextHelper.forHtmlContent(vlrFormatado) %>
						</span>
                        <% if (fun_303 && renegociacao) { 
							String vlrMin303 = vlr_simulado_minimo_303 != null ? vlr_simulado_minimo_303 : vlr_simulado_minimo;
						    String vlrMax303 = vlr_simulado_303 != null ? vlr_simulado_303 : vlr_simulado;
							String tooltipVlr303 = obterVlrFormatado(vlrMin303, vlrMax303, moeda, true, exibeCETMinMax);
						    String vlrFormatado303 = obterVlrFormatado(vlrMin303, vlrMax303, moeda, false, exibeCETMinMax);
						%>
                        <span class="badge rn text-bg-red"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-red"
                              data-bs-title="<%= tooltipVlr303 %>">
							  <%= TextHelper.forHtmlContent(vlrFormatado303) %>
						</span>
                        <% } %>
                        <% if (fun_532 && portabilidade) { 
							String vlrMin532 = vlr_simulado_minimo_532 != null ? vlr_simulado_minimo_532 : vlr_simulado_minimo;
						    String vlrMax532 = vlr_simulado_532 != null ? vlr_simulado_532 : vlr_simulado;
							String tooltipVlr532 = obterVlrFormatado(vlrMin532, vlrMax532, moeda, true, exibeCETMinMax);
						    String vlrFormatado532 = obterVlrFormatado(vlrMin532, vlrMax532, moeda, false, exibeCETMinMax);						
						%>
                        <span class="badge pr text-bg-orange"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-orange"
                              data-bs-title="<%= tooltipVlr532 %>">
							  <%= TextHelper.forHtmlContent(vlrFormatado532) %>
						  </span>
                        <% } %>
                    </div></td>
                            <% if (simulacaoPorTaxaJuros) { %>
                    <td><div class="p-2">
						<%
						    String tooltipCft = temCET ? "CET: " : "Taxa de juros: ";
						    if (exibeCETMinMax && str_cft_vlr_minimo != null) {
						        tooltipCft += TextHelper.forHtmlContent(str_cft_vlr_minimo) + "% à ";
						    }
						    tooltipCft += TextHelper.forHtmlContent(str_cft_vlr) + "%";
						%>
						<span
						    class="badge nv text-bg-blue"
						    data-bs-toggle="tooltip"
						    data-bs-placement="top"
						    data-bs-custom-class="custom-tooltip-blue"
						    data-bs-title="<%= tooltipCft %>">
						    <% if (exibeCETMinMax && str_cft_vlr_minimo != null) { %>
						        <%= TextHelper.forHtmlContent(str_cft_vlr_minimo) %><span>%</span> à
						    <% } %>
						    <%= TextHelper.forHtmlContent(str_cft_vlr) %><span>%</span>
						</span>
						<% if (fun_303 && renegociacao) {
						    String tooltipCft303 = temCET ? "CET: " : "Taxa de juros: ";
						    if (exibeCETMinMax && str_cft_vlr_minimo_303 != null) {
						        tooltipCft303 += TextHelper.forHtmlContent(str_cft_vlr_minimo_303) + "% à ";
						    }
						    tooltipCft303 += TextHelper.forHtmlContent(str_cft_vlr_303) + "%";
						%>
						    <span class="badge rn text-bg-red"
						          data-bs-toggle="tooltip"
						          data-bs-placement="top"
						          data-bs-custom-class="custom-tooltip-red"
						          data-bs-title="<%= tooltipCft303 %>">
						        <% if (exibeCETMinMax && str_cft_vlr_minimo_303 != null) { %>
						            <%= TextHelper.forHtmlContent(str_cft_vlr_minimo_303) %><span>%</span> à
						        <% } %>
						        <%= TextHelper.forHtmlContent(str_cft_vlr_303) %><span>%</span>
						    </span>
						<% } %>

						<% if (fun_532 && portabilidade) {
						    String tooltipCft532 = temCET ? "CET: " : "Taxa de juros: ";
						    if (exibeCETMinMax && str_cft_vlr_minimo_532 != null) {
						        tooltipCft532 += TextHelper.forHtmlContent(str_cft_vlr_minimo_532) + "% à ";
						    }
						    tooltipCft532 += TextHelper.forHtmlContent(str_cft_vlr_532) + "%";
						%>
						    <span class="badge pr text-bg-orange"
						          data-bs-toggle="tooltip"
						          data-bs-placement="top"
						          data-bs-custom-class="custom-tooltip-orange"
						          data-bs-title="<%= tooltipCft532 %>">
						        <% if (exibeCETMinMax && str_cft_vlr_minimo_532 != null) { %>
						            <%= TextHelper.forHtmlContent(str_cft_vlr_minimo_532) %><span>%</span> à
						        <% } %>
						        <%= TextHelper.forHtmlContent(str_cft_vlr_532) %><span>%</span>
						    </span>
						<% } %>
                    </div></td>
                            <% if (temCET) { %>
                    <td><div class="p-2"><span
                            class="badge nv text-bg-blue"
                            data-bs-toggle="tooltip"
                            data-bs-placement="top"
                            data-bs-custom-class="custom-tooltip-blue"
                            data-bs-title="CET anual: <%=TextHelper.forHtmlContent(cetAnual)%>%"><%=TextHelper.forHtmlContent(cetAnual)%><span>%</span></span>
                        <% if (fun_303 && renegociacao) { %>
                        <span class="badge rn text-bg-red"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-red"
                              data-bs-title="CET anual: <%=TextHelper.forHtmlContent(cetAnual303)%>%"><%=TextHelper.forHtmlContent(cetAnual303)%><span>%</span></span>
                        <% } %>
                        <% if (fun_532 && portabilidade) { %>
                        <span class="badge pr text-bg-orange"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-orange"
                              data-bs-title="CET anual: <%=TextHelper.forHtmlContent(cetAnual532)%>%"><%=TextHelper.forHtmlContent(cetAnual532)%><span>%</span></span>
                        <% } %>
                    </div></td>
                            <%if (taxaJurosManCSA) { %>
                    <td><div class="p-2">
                        <span class="badge nv text-bg-blue"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-blue"
                              data-bs-title="Valor: "><%=TextHelper.forHtmlContent(str_cft_vlr_ref)%>&nbsp;<%if (!TextHelper.isNull(cft_vlr_ref)) { %><span>%</span> <%} %></span>
                    </div></td>
                    <td> <div class="p-2">
                        <span class="badge nv text-bg-blue"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-blue"
                              data-bs-title="Valor: "><%=TextHelper.forHtmlContent(jurosAnual)%>&nbsp;<%if (!TextHelper.isNull(cft_vlr_ref)) { %><span>%</span> <%} %></span>
                    </div></td>
                            <%} %>
                            <% } else if (simulacaoMetodoBrasileiro || simulacaoMetodoMexicano) { %>
                    <td><div class="p-2"><span
                            class="badge nv text-bg-blue"
                            data-bs-toggle="tooltip"
                            data-bs-placement="top"
                            data-bs-custom-class="custom-tooltip-blue"
                            data-bs-title="<%=simulacaoMetodoMexicano ? "CAT" : "TAC"%>: <%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : tac_79 != null ? tac_79 : tac)%>"><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : tac_79 != null ? tac_79 : tac)%></span>&nbsp;
                        <% if (fun_303 && renegociacao) { %>
                        <span class="badge rn text-bg-red"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-red"
                              data-bs-title="<%=simulacaoMetodoMexicano ? "CAT" : "TAC"%>: <%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : tac_303)%>"><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : tac_303)%></span>
                        <% } %>
                        <% if (fun_532 && portabilidade) { %>
                        <span class="badge pr text-bg-orange"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-orange"
                              data-bs-title="<%=simulacaoMetodoMexicano ? "CAT" : "TAC"%>: <%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : tac_532)%>"><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : tac_532)%></span>
                        <% } %>
                    </div></td>
                    <td> <div class="p-2"><span
                            class="badge nv text-bg-blue"
                            data-bs-toggle="tooltip"
                            data-bs-placement="top"
                            data-bs-custom-class="custom-tooltip-blue"
                            data-bs-title="<%=simulacaoMetodoMexicano ? "IOF" : "IVA"%>: <%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : iof_79 != null ? iof_79 : iof)%> "><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : iof_79 != null ? iof_79 : iof)%></span>
                        <% if (fun_303 && renegociacao) { %>
                        <span class="badge rn text-bg-red"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-red"
                              data-bs-title="<%=simulacaoMetodoMexicano ? "IOF" : "IVA"%>: <%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : iof_303)%> "><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : iof_303)%></span>
                        <% } %>
                        <% if (fun_532 && portabilidade) { %>
                        <span class="badge pr text-bg-orange"
                              data-bs-toggle="tooltip"
                              data-bs-placement="top"
                              data-bs-custom-class="custom-tooltip-orange"
                              data-bs-title="<%=simulacaoMetodoMexicano ? "IOF" : "IVA"%>: <%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : iof_532)%>"><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : iof_532)%></span>
                        <% } %>
                    </div></td>
                            <% } %>
                            <% } %>
                    <td>
                        <div class="d-flex bd-highlight align-td">
                            <% if ((exibeCETMinMax && adeVlr.equals("")) || vlrOk) { %>
                            <% if (podeSolicitar) { %>
                            <div class="p-2">
                                <a href="#no-back"
                                   data-bs-toggle="dropdown"
                                   aria-haspopup="true"
                                   id="acoes"
                                   aria-expanded="false"
                                   class="solicitar "
                                   type="submit"><hl:message key="rotulo.mais.acoes"/>
                                    <svg>
                                        <use xlink:href="#i-engrenagem"></use>
                                    </svg>
                                </a>
                                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes"
                                     x-placement="bottom-center">
                                    <a class="nv dropdown-item 3"
                                       <%if (vlrOk && responsavel.isSer()) { %>href="#no-back"
                                       onClick="javascript:reservar('<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>', '<%=TextHelper.forJavaScriptAttribute(URLEncoder.encode(csa_nome, "ISO-8859-1"))%>', '<%=cft_codigo != null ? TextHelper.forJavaScriptAttribute(cft_codigo) : ""%>', '<%=dtjCodigo_79 != null ? TextHelper.forJavaScriptAttribute(dtjCodigo_79) : ""%>', '<%=TextHelper.forJavaScriptAttribute(vlr_ade_79 != null ? vlr_ade_79 : vlr_ade)%>', '<%=TextHelper.forJavaScriptAttribute(vlrLiberado_param)%>', '<%=TextHelper.forJavaScript(vlrLiberadoOk)%>', '<%=TextHelper.forJavaScriptAttribute(ranking)%>', '<%=TextHelper.forJavaScriptAttribute(simulacaoMetodoMexicano ? cat : (tac_79 != null ? tac_79 : tac))%>', '<%=TextHelper.forJavaScriptAttribute(simulacaoMetodoMexicano ? iva : (iof_79 != null ? iof_79 : iof))%>', '<%=TextHelper.forJavaScriptAttribute(svcCodigoItem)%>', '<%=chaveSeguranca%>'); return false;"<% } %>><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel)%>                                    </a>
                                    <% if (fun_303 && renegociacao && (vlr_simulado_303 != null && !"-".equals(vlr_simulado_303) || vlr_simulado != null && !"-".equals(vlr_simulado))) { %>
                                    <a onclick="renegociar('<%=TextHelper.forJavaScript(csa_codigo)%>', '<%=TextHelper.forJavaScript(svcCodigo)%>', '<%=TextHelper.forJavaScript(csa_nome)%>', '<%=TextHelper.forJavaScript(svc_descricao)%>', '<%=TextHelper.forJavaScript(csa_identificador)%>', '<%=TextHelper.forJavaScript(svc_identificador)%>', '<%=TextHelper.forJavaScript(!TextHelper.isNull(adeVlr) ? adeVlr : (vlr_simulado_303 != null ? vlr_simulado_303 : vlr_simulado))%>','<%=TextHelper.forJavaScript(!TextHelper.isNull(vlrLiberado) ? vlrLiberado : (vlr_simulado_303 != null ? vlr_simulado_303 : vlr_simulado))%>'); return false;"
                                       href="#no-back"
                                       class="rn dropdown-item 2"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", responsavel)%>
                                    </a>
                                    <% } %>
                                    <% if (fun_532 && portabilidade && (vlr_simulado_532 != null && !"-".equals(vlr_simulado_532) || vlr_simulado != null && !"-".equals(vlr_simulado))) { %>
                                    <a href="#no-back"
                                       onclick="portabilidadeSemLeilao('<%=TextHelper.forJavaScript(csa_codigo)%>', '<%=TextHelper.forJavaScript(!TextHelper.isNull(adeVlr) ? adeVlr : (vlr_simulado_532 != null ? vlr_simulado_532 : vlr_simulado))%>','<%=TextHelper.forJavaScript(!TextHelper.isNull(vlrLiberado) ? vlrLiberado : (vlr_simulado_532 != null ? vlr_simulado_532 : vlr_simulado))%>' ,'<%=TextHelper.forJavaScript(svcCodigo)%>', '<%=TextHelper.forJavaScript(przVlr)%>', '<%=cft_codigo != null ? TextHelper.forJavaScriptAttribute(cft_codigo) : ""%>', '<%=dtjCodigo_532 != null ? TextHelper.forJavaScriptAttribute(dtjCodigo_532) : ""%>', '<%=ranking != null ? TextHelper.forJavaScriptAttribute(ranking) : ""%>', '<%=!adeVlr.isEmpty() ? "S" : "N"%>', '<%=TextHelper.forJavaScript(vlrLiberadoOk)%>')"
                                       class="pr dropdown-item 1"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.compra", responsavel)%>
                                    </a>
                                    <% } %>
                                </div>
                            </div>
                            <%if (!TextHelper.isNull(tipo_contato) && hashCsaPermiteContato.get(csa_codigo) != null) { %>
                            <%if (!TextHelper.isNull(csa_whatsapp) && CodedValues.TPA_CONTATOS_WHATSAPP.contains(tipo_contato)) { %>
                            <i class="fa fa-whatsapp icon-contato-ranking"
                               onclick="openModalQRCode(1,'<%=csa_whatsapp%>')"></i>
                            <%} %>
                            <%if (!TextHelper.isNull(csa_email_usar) && CodedValues.TPA_CONTATOS_EMAIL.contains(tipo_contato)) { %>
                            <i class="fa fa-at icon-contato-ranking"
                               onclick="openModalQRCode(2,'<%=csa_email_usar%>')"></i>
                            <%} %>
                            <%if (!TextHelper.isNull(csa_contato_tel) && CodedValues.TPA_CONTATOS_TELEFONE.contains(tipo_contato)) { %>
                            <i class="fa fa-phone icon-contato-ranking"
                               onclick="openModalQRCode(3,'<%=csa_contato_tel%>')"></i>
                            <%} %>
                            <%} %>
                        </div>
                                <% } else { %>
                        <div class="p-2">
                        <span class="nao-disponivel">
                                          <hl:message key="mensagem.simulacao.nao.disponivel"/>
                                          <a tabindex="0" class="legenda-indice" role="button" data-bs-toggle="popover"
                                             data-placement="left" data-trigger="focus"
                                             data-bs-content="<%=textoMotivoIndisponibilidade%>"><%=motivoIndisponibilidade%></a>
                                      </span>
                        </div>
                                <%if (!TextHelper.isNull(tipo_contato) && hashCsaPermiteContato.get(csa_codigo) != null){ %>
                                <%if (!TextHelper.isNull(csa_whatsapp) && CodedValues.TPA_CONTATOS_WHATSAPP.contains(tipo_contato)){ %>
                        <i class="fa fa-whatsapp icon-contato-ranking"
                           onclick="openModalQRCode(1,'<%=csa_whatsapp%>')"></i>
                                <%} %>
                                <%if (!TextHelper.isNull(csa_email_usar) && CodedValues.TPA_CONTATOS_EMAIL.contains(tipo_contato)){ %>
                        <i class="fa fa-at icon-contato-ranking" onclick="openModalQRCode(2,'<%=csa_email_usar%>')"></i>
                                <%} %>
                                <%if (!TextHelper.isNull(csa_contato_tel) && CodedValues.TPA_CONTATOS_TELEFONE.contains(tipo_contato)){ %>
                        <i class="fa fa-phone icon-contato-ranking"
                           onclick="openModalQRCode(3,'<%=csa_contato_tel%>')"></i>
                                <%} %>
                                <%} %>
                                <% } %>
                                <% } else { %>
                        <div class="p-2">
                        <span class="nao-disponivel">
                                        <hl:message key="mensagem.simulacao.nao.disponivel"/>
                                        <a tabindex="0" class="legenda-indice" role="button" data-bs-toggle="popover"
                                           data-placement="left" data-trigger="focus"
                                           data-bs-content="<%=textoMotivoIndisponibilidade%>"><%=motivoIndisponibilidade%></a>
                        </span></div>
                                <%if (!TextHelper.isNull(tipo_contato) && hashCsaPermiteContato.get(csa_codigo) != null){ %>
                                <%if (!TextHelper.isNull(csa_whatsapp) && CodedValues.TPA_CONTATOS_WHATSAPP.contains(tipo_contato)){ %>
                        <i class="fa fa-whatsapp icon-contato-ranking"
                           onclick="openModalQRCode(1,'<%=csa_whatsapp%>')"></i>
                                <%} %>
                                <%if (!TextHelper.isNull(csa_email_usar) && CodedValues.TPA_CONTATOS_EMAIL.contains(tipo_contato)){ %>
                        <i class="fa fa-at icon-contato-ranking" onclick="openModalQRCode(2,'<%=csa_email_usar%>')"></i>
                                <%} %>
                                <%if (!TextHelper.isNull(csa_contato_tel) && CodedValues.TPA_CONTATOS_TELEFONE.contains(tipo_contato)){ %>
                        <i class="fa fa-phone icon-contato-ranking"
                           onclick="openModalQRCode(3,'<%=csa_contato_tel%>')"></i>
                                <%} %>
                                <%} %>
                                <% } %>
        </div>
        </td>
        </tr>

        <%
            }
        %>
        </tbody>
        <tfoot>
        <tr>
            <td colspan="<%=colspan%>"><hl:message key="mensagem.simulacao.resultado"
                                                   arg0="<%=DateHelper.toDateTimeString(dataAtual)%>"/></td>
        </tr>
        </tfoot>
        </table>
    </div>
    </div>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back"
           onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message
                key="rotulo.botao.voltar"/></a>
    </div>
    <div class="legendas">
        <h2 class="legenda-head"><hl:message key="mensagem.simulacao.informacao.legenda.indisponibilidade"/></h2>
        <div class="legenda-body">
            <% if (!responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO)) { %>
            <p class="legenda-item"><span class="legenda-indice"><hl:message
                    key="mensagem.simulacao.informacao.numero.0"/></span><hl:message
                    key="mensagem.simulacao.informacao.texto.0"/></p>
            <% } %>
            <p class="legenda-item"><span class="legenda-indice"><hl:message
                    key="mensagem.simulacao.informacao.numero.1"/></span><hl:message
                    key="mensagem.simulacao.informacao.texto.1"/></p>
            <p class="legenda-item"><span class="legenda-indice"><hl:message
                    key="mensagem.simulacao.informacao.numero.2"/></span><hl:message
                    key="mensagem.simulacao.informacao.texto.2"/></p>
            <p class="legenda-item"><span class="legenda-indice"><hl:message
                    key="mensagem.simulacao.informacao.numero.3"/></span><hl:message
                    key="mensagem.simulacao.informacao.texto.3"/></p>
            <p class="legenda-item"><span class="legenda-indice"><hl:message
                    key="mensagem.simulacao.informacao.numero.4"/></span><hl:message
                    key="mensagem.simulacao.informacao.texto.4"/></p>
            <p class="legenda-item"><span class="legenda-indice"><hl:message
                    key="mensagem.simulacao.informacao.numero.5"/></span><hl:message
                    key="mensagem.simulacao.informacao.texto.5"/></p>
            <% if (qtdeConsignatariasSimulacao != Integer.MAX_VALUE) { %>
            <p class="legenda-item"><span class="legenda-indice"><hl:message
                    key="mensagem.simulacao.informacao.numero.6"/></span><hl:message
                    key="mensagem.simulacao.informacao.texto.6"
                    arg0="<%= String.valueOf(qtdeConsignatariasSimulacao) %>"/></p>
            <% } %>
            <% if (temAsterisco) { %>
            <p class="legenda-item"><span class="legenda-indice">*</span><hl:message
                    key="mensagem.simulacao.consignatarias.trabalham.carencia.minima"/>
                        <% } %>
        </div>
    </div>
    <div class="legendas">
        <h2 class="legenda-head"><hl:message key="mensagem.simulacao.legenda.titulo"/></h2>
        <div class="legenda-body">
            <p class="legenda-item"><span class="badge text-bg-blue"><hl:message
                    key="mensagem.simulacao.legenda.titulo.novo"/></span><span class="m-1"><hl:message
                    key="mensagem.simulacao.legenda.texto.novo"/></span></p>
            <% if (fun_303) { %>
            <p class="legenda-item"><span class="badge text-bg-red"><hl:message
                    key="mensagem.simulacao.legenda.titulo.renegociacao"/></span><span class="m-1"><hl:message
                    key="mensagem.simulacao.legenda.texto.renegociacao"/></span></p>
            <% } %>
            <% if (fun_532) { %>
            <p class="legenda-item"><span class="badge text-bg-orange"><hl:message
                    key="mensagem.simulacao.legenda.titulo.portabilidade"/></span><span class="m-1"><hl:message
                    key="mensagem.simulacao.legenda.texto.portabilidade"/></span></p>
            <% } %>
        </div>
    </div>
</c:set>
<c:set var="pageModals">
    <%--- modal enviar email --%>
    <div class="modal fade" id="modalEmailEnviar" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
         aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header pb-0">
                    <span class="modal-title about-title mb-0"
                          id="exampleModalLabe"><hl:message key="rotulo.botao.enviar.email"/></span>
                    <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal"
                            aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="" role="alert">
                        <label for="email"><hl:message key="rotulo.servidor.email"/></label>
                        <input type="text" name="email" id="email"  placeholder="<hl:message key="mensagem.informacao.alterar.senha.digite.email"/>" value="<%=TextHelper.forHtmlAttribute(responsavel.getSerEmail() != null ? responsavel.getSerEmail() : "")%>" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" class="form-control"/>
                    </div>
                </div>
                <div class="modal-footer pt-0">
                    <div class="btn-action mt-2 mb-0">
                        <a class="btn btn-primary" id="enviaEmail" href="#no-back" onclick="enviaEmail()"
                           alt="<hl:message key="rotulo.listagem.mensagem.enviar"/>" title="<hl:message key="rotulo.listagem.mensagem.enviar"/>">
                            <hl:message key="rotulo.listagem.mensagem.enviar"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%-- Modal iniciar leilão --%>
    <div class="modal fade" id="dialogIniciarLeilao" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
         aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header pb-0">
                    <span class="modal-title about-title mb-0"
                          id="exampleModalLabel"><%=
                    TextHelper.forHtmlAttribute(request.getAttribute("tituloPagina"))%></span>
                    <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal"
                            aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <hl:message key="mensagem.leilao.reverso.iniciar.melhor.taxa"/>
                </div>
                <div class="modal-footer pt-0">
                    <div class="btn-action mt-2 mb-0">
                        <a class="btn btn-outline-danger" data-bs-dismiss="modal"
                           aria-label='<hl:message key="rotulo.nao"/>' href="#no-back" onclick="cancelarLeilao()"
                           alt="<hl:message key="rotulo.nao"/>" title="<hl:message key="rotulo.nao"/>">
                            <hl:message key="rotulo.nao"/>
                        </a>
                        <a class="btn btn-primary" id="SimLinkAutoDesbloqueio" href="#no-back" onclick="iniciarLeilao()"
                           alt="<hl:message key="rotulo.sim"/>" title="<hl:message key="rotulo.sim"/>">
                            <hl:message key="rotulo.sim"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%-- Modal taxa melhor --%>
    <div class="modal fade" id="dialogProsseguirLeilao" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
         aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header pb-0">
                    <span class="modal-title about-title mb-0"
                          id="exampleModalLabel"><%=
                    TextHelper.forHtmlAttribute(request.getAttribute("tituloPagina"))%></span>
                    <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal"
                            aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <hl:message key="mensagem.leilao.reverso.confirmar.taxa.maior"/>
                </div>
                <div class="modal-footer pt-0">
                    <div class="btn-action mt-2 mb-0">
                        <a class="btn btn-outline-danger" data-bs-dismiss="modal"
                           aria-label='<hl:message key="rotulo.nao"/>' href="#" alt="<hl:message key="rotulo.nao"/>"
                           title="<hl:message key="rotulo.nao"/>">
                            <hl:message key="rotulo.nao"/>
                        </a>
                        <a class="btn btn-primary" id="SimLinkAutoDesbloqueio" href="#no-back" onclick="acionarURL()"
                           alt="<hl:message key="rotulo.sim"/>" title="<hl:message key="rotulo.sim"/>">
                            <hl:message key="rotulo.sim"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:set>
<c:set var="javascript">
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
    <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
    <script src="../node_modules/jszip/dist/jszip.min.js"></script>
    <script src="../node_modules/pdfmake/build/pdfmake.min.js"></script>
    <script src="../node_modules/pdfmake/build/vfs_fonts.js"></script>
    <script src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
    <script src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
    <script src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
    <script src="../node_modules/moment/min/moment.min.js"></script>
  	<script src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
    <script src="../node_modules/html2canvas/dist/html2canvas.min.js"></script>
    <script src="../node_modules/jspdf/dist/jspdf.umd.min.js"></script>
    <script type="text/JavaScript">
        var f0 = document.forms[0];
        var urlBase = '../v3/<%=leilaoReverso ? "solicitarLeilao" : "simularConsignacao"%>';
        var url = '';

        function formLoad() {
            <% if (leilaoReverso) { %>
            $("#dialogIniciarLeilao").modal("show");
            <% } %>

            $('#table').dataTable({
                columnDefs: {type: 'html-num-fmt', target: 4, orderable: true},
                "order": [],
                "searching": false,
                paging: false,
                language: {
                    info:              '<hl:message key="mensagem.datatables.info"/>',
                    infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
                    zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
                    emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
                }
            });
        }

        function filtroPorOperacao(op) {
            switch (op) {
                case 1:
                    if ($('#nv').is('.text-bg-blue')) {
                        $('#nv').removeClass('text-bg-blue').addClass('text-out-blue');
                        $('.nv').hide();
                    } else {
                        $('.nv').show();
                        $('#nv').removeClass('text-out-blue').addClass('text-bg-blue');
                    }
                    break;
                case 2:
                    if ($('#rn').is('.text-bg-red')) {
                        $('#rn').removeClass('text-bg-red').addClass('text-out-red');
                        $('.rn').hide();
                    } else {
                        $('#rn').removeClass('text-out-red').addClass('text-bg-red');
                        $('.rn').show();
                    }
                    break;
                case 3:
                    if ($('#pr').is('.text-bg-orange')) {
                        $('#pr').removeClass('text-bg-orange').addClass('text-out-orange');
                        $('.pr').hide();
                    } else {
                        $('#pr').removeClass('text-out-orange').addClass('text-bg-orange');
                        $('.pr').show();
                    }
                    break
            }
        }

        function acionarURL() {
            postData(url);
        }

        function reservar(codigo, nome, cft, dtj_codigo, ade_vlr, vlr_liberado, vlrLiberadoOk, ranking, val1, val2, servico, chaveSeguranca) {
            url = urlBase + '?CSA_CODIGO=' + codigo
                + '&CSA_NOME=' + nome
                + '&CFT_CODIGO=' + cft
                + '&DTJ_CODIGO=' + dtj_codigo
                + '&ADE_VLR=' + ade_vlr
                + '&VLR_LIBERADO=' + vlr_liberado
				+ '&vlrLiberadoOk=' + vlrLiberadoOk
                + '&RANKING=' + ranking
                + '&SVC_CODIGO=' + servico
                + '&SVC_CODIGO_ORIGEM=<%=TextHelper.forHtmlContent(svcCodigo)%>'
                + '&titulo=<%=TextHelper.forHtmlContent(java.net.URLEncoder.encode(titulo, "ISO-8859-1"))%>'
                + '&RSE_CODIGO=<%=TextHelper.forHtmlContent(rseCodigo)%>'
                + '&PRZ_VLR=<%=TextHelper.forHtmlContent(numParcelas)%>'
                + '&CFT_DIA=<%=TextHelper.forHtmlContent(dia)%>'
                <% if (simulacaoMetodoMexicano) { %>
                + '&ADE_VLR_CAT=' + val1
                + '&ADE_VLR_IVA=' + val2
                + '&ADE_PERIODICIDADE=' + '<%=TextHelper.forHtmlContent(adePeriodicidade)%>'
                <% } else if (simulacaoMetodoBrasileiro) { %>
                + '&ADE_VLR_TAC=' + val1
                + '&ADE_VLR_IOF=' + val2
                <% } %>
                + '&SIMULACAO_POR_ADE_VLR=<%=TextHelper.forHtmlAttribute(simulacaoAdeVlr)%>'
                <% if (!leilaoReverso && tpsExigenciaConfirmacaoLeituraServidor) { %>
                + '&exigenciaConfirmacaoLeitura=true'
                <% } %>
                + '&acao=confirmar'
                + '&chaveSeguranca=' + chaveSeguranca
                + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';

            <% if (leilaoReverso) { %>
            if (ranking > 1) {
                $("#dialogProsseguirLeilao").modal("show");
                return false;
            }
            <% } %>

            <% if (!leilaoReverso && tpsExigenciaConfirmacaoLeituraServidor) { %>
            var checked = document.getElementById('exigenciaConfirmacaoLeitura').checked;
            if (!checked) {
                alert('<hl:message key="mensagem.informacao.simulacao.informar.confirmacao.leitura"/>');
                return false;
            }
            <% } %>

            acionarURL();
        }

        function renegociar(csaCodigo, svcCodigo, csaNome, svcDescricao, csaIdentificador, svcIdentificador, vlrAde, vlrLiberado) {
            f0.CSA_CODIGO.value = csaCodigo;
            f0.CSA_NOME.value = csaNome;
            f0.SVC_CODIGO.value = svcCodigo;
            f0.SVC_DESCRICAO.value = svcDescricao;
            f0.CSA_IDENTIFICADOR.value = csaIdentificador;
            f0.SVC_IDENTIFICADOR.value = svcIdentificador;
            f0.submit();
        }

        function portabilidadeSemLeilao(csaCodigo, adevlr, adeVlrParcela, svcCodigo, prazo, cftCodigo, dtjCodigo, rank, isParcela, vlrLiberadoOk) {
            url = '../v3/solicitarPortabilidade?acao=portabilidadeSemLeilao'
                + '&CSA_CODIGO=' + csaCodigo
                + '&ADE_VLR=' + adevlr
                + '&VLR_LIBERADO=' + adeVlrParcela
				+ '&vlrLiberadoOk=' + vlrLiberadoOk
                + '&SVC_CODIGO=' + svcCodigo
                + '&PRZ_VLR=' + prazo
                + '&CFT_CODIGO=' + cftCodigo
                + '&DTJ_CODIGO=' + dtjCodigo
                + '&RANK=' + rank
                + '&IS_PARCELA=' + isParcela
				+ '&ADE_PERIODICIDADE=' + '<%=TextHelper.forHtmlContent(adePeriodicidade)%>'
                + '&chaveSeguranca=<%=RSA.encrypt(adeVlr + "|" + vlrLiberado + "|" + numParcelas, keyPair.getPublic())%>'
                + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';
            acionarURL();
        }

        function iniciarLeilao() {
            url = '../v3/solicitarLeilao?acao=iniciarLeilao'
                + '&titulo=<%=TextHelper.forHtmlContent(java.net.URLEncoder.encode(titulo, "ISO-8859-1"))%>'
                + '&ADE_VLR=<%=TextHelper.forHtmlContent(adeVlr)%>'
                + '&VLR_LIBERADO=<%=TextHelper.forHtmlContent(vlrLiberado)%>'
				+ '&VLR_LIBERADO_OK=<%=TextHelper.forHtmlContent(vlrLiberadoOk)%>'
                + '&RSE_CODIGO=<%=TextHelper.forHtmlContent(rseCodigo)%>'
                + '&ORG_CODIGO=<%=TextHelper.forHtmlContent(orgCodigo)%>'
                + '&SVC_CODIGO=<%=TextHelper.forHtmlContent(svcCodigo)%>'
                + '&SVC_CODIGO_ORIGEM=<%=TextHelper.forHtmlContent(svcCodigo)%>'
                + '&PRZ_VLR=<%=TextHelper.forHtmlContent(numParcelas)%>'
                + '&CFT_DIA=<%=TextHelper.forHtmlContent(dia)%>'
                <% if (simulacaoMetodoMexicano) { %>
                + '&ADE_PERIODICIDADE=' + '<%=TextHelper.forHtmlContent(adePeriodicidade)%>'
                <% } %>
                + '&chaveSeguranca=<%=RSA.encrypt(adeVlr + "|" + vlrLiberado + "|" + numParcelas, keyPair.getPublic())%>'
                + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';

            acionarURL();
        }

        function cancelarLeilao() {
            <% if (origem){ %>
            //se o servidor tiver solicitado o leilao do menu leilao e clicado em 'não' - volta para a pagina inicial
            url = '../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true&<%=SynchronizerToken.generateToken4URL(request)%>';
            acionarURL();
            <% } else { %>
            //se o servidor tiver solicitado emprestimo, mesmo com o leilao reverso habilitado
            $("#dialogIniciarLeilao").modal('hide');
            urlBase = '../v3/simularConsignacao';
            <%}%>
        }

        function modalEnviarEmail() {
            $("#modalEmailEnviar").modal('show');
        }

         async function enviaEmail() {
             $("#modalEmailEnviar").modal('hide');
            setTimeout(() => {
                html2canvas(document.body).then(function (canvas) {
                    const {jsPDF} = window.jspdf;
                    const doc = new jsPDF();

                    const imgWidth = canvas.width;
                    const imgHeight = canvas.height;

                    const pdfWidth = 210;

                    const ratio = imgHeight / imgWidth;
                    const adjustedHeight = pdfWidth * ratio;

                    const imgData = canvas.toDataURL('image/png');

                    doc.addImage(imgData, 'PNG', 0, 0, pdfWidth, adjustedHeight);

                    const pdfBase64 = doc.output('datauristring');

                    const chunkSize = 1024 * 1024;
                    const chunks = [];

                    for (let i = 0; i < pdfBase64.length; i += chunkSize) {
                        chunks.push(pdfBase64.slice(i, i + chunkSize));
                    }

                    <%
                    Gson gson = new Gson();
                    String jsonInforSimuEmail = gson.toJson(inforSimuEmail);
                    %>
                    const inforSimuEmail = <%= jsonInforSimuEmail %>;
                    const serEmail = document.getElementById('email').value;
                    if (isEmailValid(serEmail)) {
                        if (serEmail == null || serEmail == '') {
                            alert('<hl:message key="mensagem.informe.servidor.email"/>');
                            return false;
                        }

                        // Modal de Aguarde enquanto enviamos o email
                        let modal = document.createElement('div');
                        modal.id = 'loadingModal';
                        modal.style.position = 'fixed';
                        modal.style.top = '0';
                        modal.style.left = '0';
                        modal.style.width = '100%';
                        modal.style.height = '100%';
                        modal.style.background = 'rgba(0, 0, 0, 0.5)';
                        modal.style.zIndex = '1000';
                        modal.style.display = 'flex';
                        modal.style.justifyContent = 'center';
                        modal.style.alignItems = 'center';

                        let modalContent = document.createElement('div');
                        modalContent.style.background = '#fff';
                        modalContent.style.padding = '20px';
                        modalContent.style.borderRadius = '10px';
                        modalContent.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';
                        modalContent.style.textAlign = 'center';
                        modalContent.style.width = '400px';

                        let heading = document.createElement('h2');
                        heading.innerText = '<hl:message key="rotulo.email.simulacao.ranking.modal.aguarde"/>';
                        heading.style.marginBottom = '10px';

                        let paragraph = document.createElement('p');
                        paragraph.innerText = '<hl:message key="rotulo.email.simulacao.ranking.modal.aguarde.conteudo"/>';

                        let spinner = document.createElement('div');
                        spinner.style.margin = '20px auto';
                        spinner.style.border = '4px solid #f3f3f3';
                        spinner.style.borderRadius = '50%';
                        spinner.style.borderTop = '4px solid #3498db';
                        spinner.style.width = '40px';
                        spinner.style.height = '40px';
                        spinner.style.animation = 'spin 1s linear infinite';

                        modalContent.appendChild(heading);
                        modalContent.appendChild(paragraph);
                        modalContent.appendChild(spinner);
                        modal.appendChild(modalContent);
                        document.body.appendChild(modal);

                        let style = document.createElement('style');
                        style.type = 'text/css';
                        style.innerHTML = `
                            @keyframes spin {
                                0% { transform: rotate(0deg); }
                                100% { transform: rotate(360deg); }
                            }
                        `;
                        document.head.appendChild(style);

                        modal.style.display = 'flex';

                        $.ajax({
                            url: '../v3/simularConsignacao?acao=enviaEmailConsignacao&_skip_history_=true',
                            type: 'POST',
                            contentType: 'application/json',
                            dataType: 'json',
                            data: JSON.stringify({
                                chunks: chunks,
                                inforCsas: inforSimuEmail,
                                serEmail: serEmail
                            }),
                            success: function () {
                                modal.style.display = 'none';
                                document.body.removeChild(modal);

                                alert('<hl:message key="mensagem.servidor.email.enviado.sucesso"/>');
                            },
                            error: function (error) {
                                modal.style.display = 'none';
                                document.body.removeChild(modal);

                                alert('<hl:message key="mensagem.servidor.email.enviado.erro"/>');
                                console.log(error);
                            }
                        });
                    } else {
                        alert('<hl:message key="mensagem.servidor.email.enviado.invalido"/>');
                    }
                });
            }, 1000);
        }

        window.onload = formLoad;

    </script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
