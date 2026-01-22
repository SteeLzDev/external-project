<%--
* <p>Title: simulacao</p>
* <p>Description: Pagina de resultado da simulaçio de emprestimos</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.zetra.econsig.helper.criptografia.RSA"%>
<%@ page import="com.zetra.econsig.helper.financeiro.CDCHelper"%>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.log.Log" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String titulo = (String) request.getAttribute("titulo");
float nroColunas = (Float) request.getAttribute("floatQtdeColunasSimulacao");
String przVlr = (String) request.getAttribute("PRZ_VLR");
String vlrLiberado = (String) request.getAttribute("VLR_LIBERADO");
String mensagem = (String) request.getAttribute("mensagem");
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
boolean tpcSolicitarPortabilidadeRanking  = (Boolean) request.getAttribute("tpcSolicitarPortabilidadeRanking");
String adeCodigo  = (String) request.getAttribute("ADE_CODIGO");
boolean simulacaoAdeVlr = (Boolean) request.getAttribute("SIMULACAO_POR_ADE_VLR");
boolean temAsterisco = false;
boolean temX = false;
int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
Date dataAtual = DateHelper.getSystemDatetime();
boolean vlrTotal = (Boolean) request.getAttribute("vlrTotalPg");

boolean podeMostrarMargem = (Boolean) request.getAttribute("podeMostrarMargem");
ExibeMargem exibeMargem = (ExibeMargem) request.getAttribute("exibeMargem");
String margemConsignavel = (String) request.getAttribute("margemConsignavel");
BigDecimal rseMargemRest = (BigDecimal) request.getAttribute("rseMargemRest");

float colspan = 6;
if (!simulacaoPorTaxaJuros) {
  colspan = (float)(4*nroColunas);
} else {
  colspan = (float)(9*nroColunas);
}

boolean tpsExigenciaConfirmacaoLeituraServidor = (Boolean) request.getAttribute("tpsExigenciaConfirmacaoLeituraServidor");
boolean origem = request.getAttribute("origem") != null ? (Boolean) request.getAttribute("origem") : false;
boolean taxaJurosManCSA = (Boolean) request.getAttribute("taxaJurosManCSA");

List<String []> lstRowValues = new ArrayList<>();

java.security.KeyPair keyPair = (java.security.KeyPair) request.getAttribute("keyPair");
HashMap<String, TransferObject> hashCsaPermiteContato = (HashMap<String, TransferObject>) request.getAttribute("hashCsaPermiteContato"); 
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.simulacao.apenas.para.titulo" arg0="<%=TextHelper.forHtmlAttribute(titulo.toUpperCase())%>"/>
</c:set>
<c:set var="bodyContent">
			<div class="card">
				<div class="card-header hasIcon">
					<span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular-config"></use></svg></span>
					<h2 class="card-header-title"><hl:message key="rotulo.simulacao.dados"/></h2>
                    <span class="ultima-edicao"><%=ApplicationResourcesHelper.getMessage("mensagem.informacao.simulacao.realizada.em", responsavel, DateHelper.toDateTimeString(dataAtual))%></span>
				</div>
				<div class="card-body">
					<dl class="row data-list">
						<dt class="col-6 col-sm-3"><hl:message key="rotulo.servico.singular" />:</dt> <dd class="col-6 col-sm-9"><%=TextHelper.forHtmlAttribute(titulo)%></dd>
                    <% if (!adeVlr.isEmpty()) { %>
                        <dt class="col-6 col-sm-3"><hl:message key="rotulo.consignacao.valor.parcela"/>:</dt> <dd class="col-6 col-sm-9"><hl:message key="rotulo.moeda"/>&nbsp;<%=NumberHelper.reformat(adeVlr, "en", NumberHelper.getLang(), true)%></dd>
                    <% } else { %>
                        <dt class="col-6 col-sm-3"><hl:message key="rotulo.consignacao.valor.solicitado"/>:</dt> <dd class="col-6 col-sm-9"><hl:message key="rotulo.moeda"/>&nbsp;<%=NumberHelper.reformat(vlrLiberado, "en", NumberHelper.getLang(), true)%></dd>
                    <% } %>
						<dt class="col-6 col-sm-3"><hl:message key="rotulo.consignacao.prazo.extenso"/>:</dt> <dd class="col-6 col-sm-9"><%=przVlr%></dd>
                    <% if (podeMostrarMargem) { %>
						<dt class="col-6 col-sm-3"><hl:message key="rotulo.simulacao.margem.consignavel" />:</dt> <dd class="col-6 col-sm-9"><%=TextHelper.forHtmlAttribute(request.getAttribute("tipoVlrMargemDisponivel"))%>&nbsp;<%=(String)(exibeMargem.isSemRestricao() || rseMargemRest.doubleValue() > 0 ? NumberHelper.reformat(margemConsignavel, "en", NumberHelper.getLang()) : "0,00")%></dd>
                    <% } %>
					</dl>
					<a class="btn btn-primary bt-alterar-simulacao" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><svg width="25"><use xlink:href="#i-simular"></use></svg><hl:message key="rotulo.botao.alterar"/> <%=ApplicationResourcesHelper.getMessage("rotulo.simulacao.titulo", responsavel).toLowerCase()%></a>
				</div>
			</div>
			<% if (tpsExigenciaConfirmacaoLeituraServidor) { %>
			<div class="alert alerta-checkbox" role="alert">
       			<input class="form-check-input " type="checkbox" id="exigenciaConfirmacaoLeitura" name="exigenciaConfirmacaoLeitura">
       			<label for="alerta" class="form-check-label font-weight-bold"><hl:message key="mensagem.informacao.simulacao.exigencia.confirmacao.leitura"/></label>
     		</div>
			<% } %>
			<div class="card">
				<div class="card-header hasIcon">
					<span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular-config"></use></svg></span>
					<h2 class="card-header-title"><hl:message key="rotulo.simulacao.resultado"/></h2>
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
                                <% if(vlrTotal) { %>
                                <th scope="col"><hl:message key="rotulo.consignacao.valor.total.pagamento"/></th>
                                <% } %>
                            <% if (simulacaoPorTaxaJuros) { %>
                              <% if (temCET) { %>
                                <th scope="col"><hl:message key="rotulo.consignacao.cet"/></th>
                                <th scope="col"><hl:message key="rotulo.consignacao.cet.anual"/></th>
                                  <%if(taxaJurosManCSA){ %>
                                      <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros"/></th>
                                      <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros.anual"/></th>
                                  <%} %>
                              <% } else { %>
                                <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros"/></th>
                                <% if (simulacaoMetodoMexicano) { %>
                                  <th scope="col"><hl:message key="rotulo.consignacao.valor.cat.abreviado"/> (<hl:message key="rotulo.porcentagem"/>)</th>
                                  <th scope="col"><hl:message key="rotulo.consignacao.valor.iva.abreviado"/> (<hl:message key="rotulo.moeda"/>)</th>
                                <% } else if (simulacaoMetodoBrasileiro) { %>
                                  <th scope="col"><hl:message key="rotulo.consignacao.valor.tac.abreviado"/> (<hl:message key="rotulo.moeda"/>)</th>
                                  <th scope="col"><hl:message key="rotulo.consignacao.valor.iof.abreviado"/> (<hl:message key="rotulo.moeda"/>)</th>
                                <% } %>
                              <% } %>
                            <% } %>
    						<th scope="col"><hl:message key="rotulo.acoes"/></th>
							</tr>
							<tr>
								<th class="th-info" scope="col" colspan="<%=colspan%>"><hl:message key="mensagem.simulacao.resultado" arg0="<%=DateHelper.toDateTimeString(dataAtual)%>" /></th>
							</tr>
						</thead>
						<tbody>
                    <%
                      String csa_codigo, csa_nome, cft_codigo, dtjCodigo_79, dtjCodigo_532, ranking, str_cft_vlr, str_cft_vlr_532, str_cft_vlr_ref, cetAnual, cetAnual532,jurosAnual, svcCodigoItem;
                      String tac = "", iof = "", tac_79 = "", iof_79 = "", tac_532 = "", iof_532 = "";
                      String totalPagar = "", cat = "", iva = ""; // simulacaoMetodoMexicano
                      BigDecimal cft_vlr, cft_vlr_532;
                      BigDecimal cft_vlr_ref;
                      Object objMotivoIndisponibilidade = null;
                      String motivoIndisponibilidade = null;
                      String textoMotivoIndisponibilidade = null;
                      CustomTransferObject coeficiente = null;
                      String vlr_ade, vlr_ade_79, vlr_ade_532, vlrLiberado_param, vlr_simulado = null, vlr_simulado_79 = null, vlr_simulado_532 = null;
                      String csa_whatsapp = "", csa_email_contato ="", csa_email = "", csa_email_usar = "", csa_contato_tel = "", tipo_contato = "";
                      Iterator<TransferObject> it = simulacao.iterator(); 
                      Integer relevancia = null;
                      while (it.hasNext()) {
                          vlr_simulado_79 = null;
                          vlr_simulado_532 = null;
                          coeficiente = (CustomTransferObject)it.next();
                          csa_codigo = (String)coeficiente.getAttribute(Columns.CSA_CODIGO);
                          svcCodigoItem = (String)coeficiente.getAttribute(Columns.SVC_CODIGO);
                          csa_nome = TextHelper.forHtmlAttribute((String)coeficiente.getAttribute("TITULO"));
                          objMotivoIndisponibilidade = coeficiente.getAttribute("MOTIVO_INDISPONIBILIDADE");
                          motivoIndisponibilidade = objMotivoIndisponibilidade != null ? String.valueOf((Integer)objMotivoIndisponibilidade) : "";
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
                          cft_codigo = (String)coeficiente.getAttribute(Columns.CFT_CODIGO);
                          dtjCodigo_79 = coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ?  (String) coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SIM_CONSIGNACAO) : null;
                          dtjCodigo_532 = coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ?  (String) coeficiente.getAttribute(Columns.DTJ_CODIGO + "_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) : null;
                          vlr_ade = coeficiente.getAttribute("VLR_PARCELA").toString();
                          vlr_ade_79 =  coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : null;
                          vlr_ade_532 = coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : null;
                          ranking = (String)coeficiente.getAttribute("RANKING");
                          vlrOk = ((Boolean) coeficiente.getAttribute("OK")).booleanValue();
                          cft_vlr = new BigDecimal(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : coeficiente.getAttribute(Columns.CFT_VLR).toString());
                          cft_vlr_532 = new BigDecimal(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : coeficiente.getAttribute(Columns.CFT_VLR).toString());
                          str_cft_vlr = NumberHelper.format(cft_vlr.doubleValue(), NumberHelper.getLang(), 2, 8);
                          str_cft_vlr_532 = NumberHelper.format(cft_vlr_532.doubleValue(), NumberHelper.getLang(), 2, 8);
                          cft_vlr_ref = !TextHelper.isNull(coeficiente.getAttribute(Columns.CFT_VLR_REF)) ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_REF).toString()) : null;
                          str_cft_vlr_ref = !TextHelper.isNull(cft_vlr_ref) ? NumberHelper.format(cft_vlr_ref.doubleValue(), NumberHelper.getLang(), 2, 8) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros.nao.registrado", responsavel);

                          if (!tpcSolicitarPortabilidadeRanking) {
                              vlrLiberado_param = coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : coeficiente.getAttribute("VLR_LIBERADO").toString();
                          } else {
                              vlrLiberado_param = coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : coeficiente.getAttribute("VLR_LIBERADO").toString();
                              vlr_ade = !TextHelper.isNull(vlr_ade_532) ? vlr_ade_532 : vlr_ade;
                          }
                          
                          cetAnual = CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr);
                          cetAnual532 = CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr_532);
                          jurosAnual = !TextHelper.isNull(cft_vlr_ref) ? CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr_ref) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros.nao.registrado", responsavel);
                          relevancia = (Integer)coeficiente.getAttribute("RELEVANCIA");

                          temAsterisco |= (csa_nome.indexOf('*') != -1);

                          if (simulacaoPorTaxaJuros) {
                            if (simulacaoMetodoMexicano) {
                                cat = NumberHelper.reformat((coeficiente.getAttribute("CAT") != null) ? coeficiente.getAttribute("CAT").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                iva = NumberHelper.reformat((coeficiente.getAttribute("IVA") != null) ? coeficiente.getAttribute("IVA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                totalPagar = NumberHelper.reformat((coeficiente.getAttribute("TOTAL_PAGAR") != null) ? coeficiente.getAttribute("TOTAL_PAGAR").toString() : "0.00", "en", NumberHelper.getLang(), true);
                            } else if (simulacaoMetodoBrasileiro) {
                                tac = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA") != null) ? coeficiente.getAttribute("TAC_FINANCIADA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                tac_79 = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SIM_CONSIGNACAO) != null) ? (String) coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : tac, "en", NumberHelper.getLang(), true);
                                tac_532 = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null) ? (String) coeficiente.getAttribute("TAC_FINANCIADA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : tac, "en", NumberHelper.getLang(), true);
                                iof = NumberHelper.reformat((coeficiente.getAttribute("IOF") != null) ? coeficiente.getAttribute("IOF").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                iof_79 = NumberHelper.reformat((coeficiente.getAttribute("IOF_" + CodedValues.FUN_SIM_CONSIGNACAO) != null) ? (String) coeficiente.getAttribute("IOF_" + CodedValues.FUN_SIM_CONSIGNACAO).toString() : tac, "en", NumberHelper.getLang(), true);
                                iof_532 = NumberHelper.reformat((coeficiente.getAttribute("IOF_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null) ? (String) coeficiente.getAttribute("IOF_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString() : tac, "en", NumberHelper.getLang(), true);

                            }
                          }

                          if (!adeVlr.equals("")) {
                            vlr_simulado = NumberHelper.format(((BigDecimal)coeficiente.getAttribute("VLR_LIBERADO")).doubleValue(), NumberHelper.getLang(), true);
                            vlr_simulado_79 = coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SIM_CONSIGNACAO)).doubleValue(), NumberHelper.getLang(), true) : null;
                            vlr_simulado_532 = coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE) != null ? NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_LIBERADO_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE)).doubleValue(), NumberHelper.getLang(), true) : vlr_simulado;
                          } else {
                            if (vlr_ade.equals(new BigDecimal(Double.MAX_VALUE).toString())) {
                              vlr_simulado = "-";
                            } else {
                              vlr_simulado = NumberHelper.format(((BigDecimal)coeficiente.getAttribute("VLR_PARCELA")).doubleValue(), NumberHelper.getLang(), true);
                            }
                            if (vlr_ade_79 != null) {
                                if (vlr_ade_79.equals(new BigDecimal(Double.MAX_VALUE).toString())) {
                                    vlr_simulado_79 = "-";
                                } else {
                                    vlr_simulado_79 = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO)).doubleValue(), NumberHelper.getLang(), true);
                                }
                            }
                            if (vlr_ade_532 != null) {
                                if (vlr_ade_532.equals(new BigDecimal(Double.MAX_VALUE).toString())) {
                                    vlr_simulado_532 = "-";
                                } else {
                                    vlr_simulado_532 = NumberHelper.format(((BigDecimal) coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE)).doubleValue(), NumberHelper.getLang(), true);
                                }
                            }
                          }

                          String chaveSeguranca = RSA.encrypt((vlr_ade_79 != null ? vlr_ade_79 : vlr_ade) + "|" + vlrLiberado_param + "|" + numParcelas, keyPair.getPublic());
                          String [] rowValue = {vlrOk ? "true" : "false", ranking, csa_nome, vlr_simulado, str_cft_vlr, cetAnual, cat, tac, iva, iof, cft_codigo, dtjCodigo_79, vlr_ade, vlrLiberado_param, svcCodigoItem, chaveSeguranca, csa_codigo, textoMotivoIndisponibilidade, motivoIndisponibilidade, relevancia.toString(), str_cft_vlr_ref, jurosAnual};
                          lstRowValues.add(rowValue);

                          BigDecimal vlrTotalPg = null;
                          String vlrTotalView = null;
                          if (vlrTotal && adeVlr.isEmpty()) {
                              BigDecimal aVlr = coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) coeficiente.getAttribute("VLR_PARCELA_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) coeficiente.getAttribute("VLR_PARCELA");
                              int aPrz = Integer.parseInt(przVlr);
                              vlrTotalPg = aVlr.multiply(BigDecimal.valueOf(aPrz));
                              vlrTotalView = NumberHelper.reformat(vlrTotalPg.toString(), "en", NumberHelper.getLang(), true);
                              vlrTotalView = vlrTotalView.replace("∞", "-");
                          } else if (vlrTotal && !adeVlr.isEmpty()){
                              BigDecimal adVlr = new BigDecimal(adeVlr);
                              int aPrz = Integer.parseInt(przVlr);
                              vlrTotalPg = adVlr.multiply(BigDecimal.valueOf(aPrz));
                              vlrTotalView = NumberHelper.reformat(vlrTotalPg.toString(), "en", NumberHelper.getLang(), true);
                              vlrTotalView = vlrTotalView.replace("∞", "-");
                          }
                          
                          if(hashCsaPermiteContato.get(csa_codigo) != null){
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
                          }
                    %>
							<tr <% if (!vlrOk) { %>class="indisponivel"<% } %>>
								<td><span class="p-2">
									<svg class="<%=(!vlrOk ? "i-indisponivel" : "i-disponivel")%>"><use xlink:href="#<%=(!vlrOk ? "i-status-x" : "i-status-v")%>"></use></svg>
                                        <%=TextHelper.forHtmlContent(ranking)%><span class="ordinal"></span></span>
                                </td>
								<td><div class="d-flex justify-content-between bd-highlight">
                                    <div class="p-2"><%=TextHelper.forHtmlContent(csa_nome)%><%if (!TextHelper.isNull(motivoIndisponibilidade)) {%> <sup><%=motivoIndisponibilidade%>
                                    </sup><%}%></div>
                                        <% if (relevancia.intValue() != Integer.valueOf(CodedValues.CSA_NAO_PROMOVIDA).intValue() && vlrOk) { %>
                                        <div class="d-flex flex-nowrap"><span class="p-2 alert alert-success btn-oferta mb-0" id="verdict"><hl:message key="rotulo.param.svc.relevancia.oferta.patrocinada"/></span></div>
                                   <%} %>
                                   </div>  
                                </td>
                                <% if(!tpcSolicitarPortabilidadeRanking){ %>
                                  <td><div class="p-2"><hl:message key="rotulo.moeda"/>&nbsp;<%=TextHelper.forHtmlContent(vlr_simulado_79 != null ? vlr_simulado_79 : vlr_simulado)%></div></td>
                                    <% if(vlrTotal){ %>
                                <td><div class="p-2"><hl:message key="rotulo.moeda"/>&nbsp;<%=vlrTotalView%></div></td>
                                <% } %>
                                <% if (simulacaoPorTaxaJuros) { %>
                                   <td><div class="p-2"><%=TextHelper.forHtmlContent(str_cft_vlr)%><span>%</span></div></td>
                                  <% if (temCET) { %>
                                   <td><div class="p-2"><%=TextHelper.forHtmlContent(cetAnual)%><span>%</span></div></td>
                                     <%if (taxaJurosManCSA){ %>
                                      <td><div class="p-2"><%=TextHelper.forHtmlContent(str_cft_vlr_ref)%>&nbsp;<%if(!TextHelper.isNull(cft_vlr_ref)){ %><span>%</span> <%} %></div></td>
                                      <td><div class="p-2"><%=TextHelper.forHtmlContent(jurosAnual)%>&nbsp;<%if(!TextHelper.isNull(cft_vlr_ref)){ %><span>%</span> <%} %></div></td>
                                     <%} %>
                                  <% } else if (simulacaoMetodoBrasileiro || simulacaoMetodoMexicano) { %>
                                    <td><div class="p-2">
                                      <%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : (tac_79 != null && !tac_79.isEmpty() ? tac_79 : tac))%>&nbsp;</div></td>
                                    <td><div class="p-2"><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : (iof_79 != null && !iof_79.isEmpty() ? iof_79 : iof))%>&nbsp;</div></td>
                                  <% } %>
                              <% } %>
                              <% } else { %>
                                  <td><div class="p-2"><hl:message key="rotulo.moeda"/>&nbsp;<%=TextHelper.forHtmlContent(vlr_simulado_532 != null ? vlr_simulado_532 : vlr_simulado)%></div></td>
                                <% if(vlrTotal){ %>
                                <td><div class="p-2"><hl:message key="rotulo.moeda"/>&nbsp;<%=vlrTotalView%></div></td>
                                <% } %>
                                <% if (simulacaoPorTaxaJuros) { %>
                                    <td><div class="p-2"><%=TextHelper.forHtmlContent(str_cft_vlr_532)%>&nbsp;<span>%</span></div></td>
                                    <% if (temCET) { %>
                                    <td><div class="p-2"><%=TextHelper.forHtmlContent(cetAnual532)%>&nbsp;<span>%</span></div></td>
                                       <%if (taxaJurosManCSA){ %>
                                    <td><div class="p-2"><%=TextHelper.forHtmlContent(str_cft_vlr_ref)%>&nbsp;<%if(!TextHelper.isNull(cft_vlr_ref)){ %><span>%</span> <%} %></div></td>
                                    <td><div class="p-2"><%=TextHelper.forHtmlContent(jurosAnual)%>&nbsp;<%if(!TextHelper.isNull(cft_vlr_ref)){ %><span>%</span> <%} %></div></td>
                                       <%} %>
                                    <% } else if (simulacaoMetodoBrasileiro || simulacaoMetodoMexicano) { %>
                                    <td><div class="p-2">
                                        <%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : (tac_532 != null && !tac_532.isEmpty() ? tac_532 : tac))%>&nbsp;</div></td>
                                    <td><div class="p-2"><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : (iof_532 != null && !iof_532.isEmpty() ? iof_532 : iof))%>&nbsp;</div></td>
                                    <% } %>
                                  <% } %>
                              <% } %>
								<td>
                                    <div class="d-flex bd-highlight align-td">
                                        <div class="p-2">
                                <% if (vlrOk) { %>
                                  <% if (podeSolicitar) { %>
                                    <% if(!tpcSolicitarPortabilidadeRanking){ %>
                                      <a class="solicitar" <%if (vlrOk && responsavel.isSer()) { %>href="#no-back" onClick="javascript:reservar('<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>', '<%=TextHelper.forJavaScriptAttribute(URLEncoder.encode(csa_nome, "ISO-8859-1"))%>', '<%=cft_codigo != null ? TextHelper.forJavaScriptAttribute(cft_codigo) : ""%>', '<%=dtjCodigo_79 != null ? TextHelper.forJavaScriptAttribute(dtjCodigo_79) : ""%>', '<%=TextHelper.forJavaScriptAttribute(vlr_ade_79 != null ? vlr_ade_79 : vlr_ade)%>', '<%=TextHelper.forJavaScriptAttribute(vlrLiberado_param)%>', '<%=TextHelper.forJavaScriptAttribute(ranking)%>', '<%=TextHelper.forJavaScriptAttribute(simulacaoMetodoMexicano ? cat : (tac_79 != null ? tac_79 : tac))%>', '<%=TextHelper.forJavaScriptAttribute(simulacaoMetodoMexicano ? iva : (iof_79 != null ? iof_79 : iof))%>', '<%=TextHelper.forJavaScriptAttribute(svcCodigoItem)%>', '<%=chaveSeguranca%>'); return false;"<% } %>>
  										<svg><use xlink:href="#i-malote-dinheiro"></use></svg>
  										<hl:message key="rotulo.botao.solicitar"/>
  									</a>
                                    <% } else {%>
                                      <a class="solicitar" <%if (vlrOk && responsavel.isSer()) { %>href="#no-back" onClick="javascript:simularPortabilidadeRanking('<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>', '<%=TextHelper.forJavaScriptAttribute(URLEncoder.encode(csa_nome, "ISO-8859-1"))%>', '<%=cft_codigo != null ? TextHelper.forJavaScriptAttribute(cft_codigo) : ""%>', '<%=dtjCodigo_532 != null ? TextHelper.forJavaScriptAttribute(dtjCodigo_532) : ""%>', '<%=TextHelper.forJavaScript(!TextHelper.isNull(adeVlr) ? adeVlr : (vlr_simulado_532 != null ? vlr_simulado_532 : vlr_simulado))%>','<%=TextHelper.forJavaScript(!TextHelper.isNull(vlrLiberado) ? vlrLiberado : (vlr_simulado_532 != null ? vlr_simulado_532 : vlr_simulado))%>', '<%=TextHelper.forJavaScriptAttribute(ranking)%>', '<%=TextHelper.forJavaScriptAttribute(simulacaoMetodoMexicano ? cat : (tac_532 != null ? tac_532 : tac))%>', '<%=TextHelper.forJavaScriptAttribute(simulacaoMetodoMexicano ? iva : (iof_532 != null ? iof_532 : iof))%>', '<%=TextHelper.forJavaScriptAttribute(svcCodigoItem)%>', '<%=chaveSeguranca%>'); return false;"<% } %>>
                                        <svg><use xlink:href="#i-malote-dinheiro"></use></svg>
                                        <hl:message key="rotulo.botao.solicitar"/>
                                      </a>
                                    <% } %>
                                  <% } else { %>
                                      <span class="nao-disponivel">
                                          <hl:message key="mensagem.simulacao.nao.disponivel"/>
                                          <a tabindex="0" class="legenda-indice" role="button" data-bs-toggle="popover" data-placement="left" data-trigger="focus" data-bs-content="<%=textoMotivoIndisponibilidade%>"><%=motivoIndisponibilidade%></a>
                                      </span>
                                  <%} %> 
                                <% } else { %>
                                    <span class="nao-disponivel">
                                        <hl:message key="mensagem.simulacao.nao.disponivel"/>
                                        <a tabindex="0" class="legenda-indice" role="button" data-bs-toggle="popover" data-placement="left" data-trigger="focus" data-bs-content="<%=textoMotivoIndisponibilidade%>"><%=motivoIndisponibilidade%></a>
                                    </span>
                                <% } %>
                                        </div>
                                  <%if (!TextHelper.isNull(tipo_contato) && hashCsaPermiteContato.get(csa_codigo) != null){ %>
                                    <%if (!TextHelper.isNull(csa_whatsapp) && CodedValues.TPA_CONTATOS_WHATSAPP.contains(tipo_contato)){ %>
                                        <i class="fa fa-whatsapp icon-contato-ranking" onclick="openModalQRCode(1,'<%=csa_whatsapp%>')"></i>
                                    <%} %>
                                    <%if (!TextHelper.isNull(csa_email_usar) && CodedValues.TPA_CONTATOS_EMAIL.contains(tipo_contato)){ %>
                                        <i class="fa fa-at icon-contato-ranking" onclick="openModalQRCode(2,'<%=csa_email_usar%>')"></i>
                                    <%} %>
                                    <%if (!TextHelper.isNull(csa_contato_tel) && CodedValues.TPA_CONTATOS_TELEFONE.contains(tipo_contato)){ %>
                                        <i class="fa fa-phone icon-contato-ranking" onclick="openModalQRCode(3,'<%=csa_contato_tel%>')"></i>                              
                                    <%} %>
                                  <%} %>
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
				<a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
			</div>
			<div class="legenda">
				<h2 class="legenda-head"><hl:message key="mensagem.simulacao.informacao.legenda.indisponibilidade"/></h2>
				<div class="legenda-body">
                    <% if (!responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO)) { %>
                    <p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.0"/></span><hl:message key="mensagem.simulacao.informacao.texto.0"/></p>
                    <% } %>
					<p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.1"/></span><hl:message key="mensagem.simulacao.informacao.texto.1"/></p>
					<p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.2"/></span><hl:message key="mensagem.simulacao.informacao.texto.2"/></p>
					<p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.3"/></span><hl:message key="mensagem.simulacao.informacao.texto.3"/></p>
					<p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.4"/></span><hl:message key="mensagem.simulacao.informacao.texto.4"/></p>
					<p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.5"/></span><hl:message key="mensagem.simulacao.informacao.texto.5"/></p>
                    <% if (qtdeConsignatariasSimulacao != Integer.MAX_VALUE) { %>
                    <p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.6"/></span><hl:message key="mensagem.simulacao.informacao.texto.6" arg0="<%= String.valueOf(qtdeConsignatariasSimulacao) %>"/></p>
                    <% } %>
                    <% if (temAsterisco) { %>
                    <p class="legenda-item"><span class="legenda-indice">*</span><hl:message key="mensagem.simulacao.consignatarias.trabalham.carencia.minima"/>
                    <% } %>
				</div>
			</div>
</c:set>
<c:set var="pageModals">
    <%-- Modal iniciar leilão --%>     
    <div class="modal fade" id="dialogIniciarLeilao" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" >          
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <span class="modal-title about-title mb-0" id="exampleModalLabel"><%=TextHelper.forHtmlAttribute(request.getAttribute("tituloPagina"))%></span>
            <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
         <div class="modal-body">
           <hl:message key="mensagem.leilao.reverso.iniciar.melhor.taxa" />
         </div>
         <div class="modal-footer pt-0">
           <div class="btn-action mt-2 mb-0">
             <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.nao"/>' href="#no-back" onclick="cancelarLeilao()" alt="<hl:message key="rotulo.nao"/>" title="<hl:message key="rotulo.nao"/>">
               <hl:message key="rotulo.nao" />
             </a>
             <a class="btn btn-primary" id="SimLinkAutoDesbloqueio" href="#no-back" onclick="iniciarLeilao()" alt="<hl:message key="rotulo.sim"/>" title="<hl:message key="rotulo.sim"/>">
               <hl:message key="rotulo.sim" />
             </a>
           </div>
         </div>
       </div>
     </div>
   </div> 
    <%-- Modal taxa melhor --%>
    <div class="modal fade" id="dialogProsseguirLeilao" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" >          
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header pb-0">
            <span class="modal-title about-title mb-0" id="exampleModalLabel"><%=TextHelper.forHtmlAttribute(request.getAttribute("tituloPagina"))%></span>
            <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
         <div class="modal-body">
           <hl:message key="mensagem.leilao.reverso.confirmar.taxa.maior" />
         </div>
         <div class="modal-footer pt-0">
           <div class="btn-action mt-2 mb-0">
             <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.nao"/>' href="#" alt="<hl:message key="rotulo.nao"/>" title="<hl:message key="rotulo.nao"/>">
               <hl:message key="rotulo.nao" />
             </a>
             <a class="btn btn-primary" id="SimLinkAutoDesbloqueio" href="#no-back" onclick="acionarURL()" alt="<hl:message key="rotulo.sim"/>" title="<hl:message key="rotulo.sim"/>">
               <hl:message key="rotulo.sim" />
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
    <script  src="../node_modules/moment/min/moment.min.js"></script>
  	<script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
  <script type="text/JavaScript">
  var f0 = document.forms[0];
  <% if(!tpcSolicitarPortabilidadeRanking){ %>
  	var urlBase = '../v3/<%=leilaoReverso ? "solicitarLeilao" : "simularConsignacao"%>';  
  <%} else { %>
  	var urlBase = '../v3/solicitarPortabilidade';
  <% } %>
  var url = '';

  function formLoad() {
    <% if (leilaoReverso) { %>
    $("#dialogIniciarLeilao").modal("show");
    <% } %>
    
    $('#table').dataTable({
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

  function acionarURL() {
    postData(url);
  }
    
  function reservar(codigo, nome, cft, dtj_codigo, ade_vlr, vlr_liberado, ranking, val1, val2, servico, chaveSeguranca) {
	  url = urlBase + '?CSA_CODIGO=' + codigo
        + '&CSA_NOME=' + nome
        + '&CFT_CODIGO=' + cft
        + '&DTJ_CODIGO=' + dtj_codigo
        + '&ADE_VLR=' + ade_vlr
        + '&VLR_LIBERADO=' + vlr_liberado
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
              alert ('<hl:message key="mensagem.informacao.simulacao.informar.confirmacao.leitura"/>');
              return false;
          } 
        <% } %>
      
		acionarURL();
	}

  function simularPortabilidadeRanking(codigo, nome, cft, dtj_codigo, ade_vlr, vlr_liberado, ranking, val1, val2, servico, chaveSeguranca) {
	  url = urlBase + '?CSA_CODIGO=' + codigo
      + '&CSA_NOME=' + nome
      + '&CFT_CODIGO=' + cft
      + '&DTJ_CODIGO=' + dtj_codigo
      + '&ADE_VLR=' + ade_vlr
      + '&VLR_LIBERADO=' + vlr_liberado
      + '&RANKING=' + ranking
      + '&SVC_CODIGO=' + servico
      + '&SVC_CODIGO_ORIGEM=<%=TextHelper.forHtmlContent(svcCodigo)%>'
      + '&ADE_CODIGO=<%=TextHelper.forHtmlContent(adeCodigo)%>'
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
      + '&acao=confirmarSimulacaoPortabilidade'
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
            alert ('<hl:message key="mensagem.informacao.simulacao.informar.confirmacao.leitura"/>');
            return false;
        } 
      <% } %>
      
  	acionarURL();
  }

  function iniciarLeilao() {
    url = '../v3/solicitarLeilao?acao=iniciarLeilao'
        + '&titulo=<%=TextHelper.forHtmlContent(java.net.URLEncoder.encode(titulo, "ISO-8859-1"))%>'
        + '&ADE_VLR=<%=TextHelper.forHtmlContent(adeVlr)%>'
        + '&VLR_LIBERADO=<%=TextHelper.forHtmlContent(vlrLiberado)%>'
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
        
  <%
  StringBuilder listJson = new StringBuilder("{\"linhasTabela\": {");
  for (int i = 0; i < lstRowValues.size(); i++) {
    String [] tRow = lstRowValues.get(i);

    
    listJson.append("\"").append(i).append("\":[");
    for (int j = 0; j < tRow.length; j++) {    
        
        if (!TextHelper.isNull(tRow[j])) {
            String linha = tRow[j].replace("\"","");
            listJson.append("\"").append(linha).append("\"");
        } else {
            listJson.append("\"").append("null").append("\"");
        }
        if (j < (tRow.length - 1)) { listJson.append(","); }
        
    }
    listJson.append("]");
    
    if(i < (lstRowValues.size() - 1)) { listJson.append(","); }
  }
  listJson.append("}}");
  %>

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