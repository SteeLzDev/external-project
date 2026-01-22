package com.zetra.econsig.helper.consignacao;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.financeiro.CDCHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: BoletoHelper</p>
 * <p>Description: Helper Class para geração de boletos.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BoletoHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BoletoHelper.class);

    /**
     * Gera um texto de boleto, baseado em um template e um objeto com
     * os dados do contrato.
     * @param template
     * @param cto
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static String gerarTextoBoleto(String template, CustomTransferObject cto, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (cto != null) {
                final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
                final String adeCodigo = (String) cto.getAttribute(Columns.ADE_CODIGO);
                final List<TransferObject> dadoAdicional = autorizacaoController.lstDadoAutDesconto(adeCodigo, null, VisibilidadeTipoDadoAdicionalEnum.WEB, responsavel);

                if ((dadoAdicional != null) && !dadoAdicional.isEmpty()) {
                    for (final TransferObject dado : dadoAdicional) {
                        cto.setAttribute(CodedValues.DAD_VALOR_ + dado.getAttribute(Columns.TDA_CODIGO), dado.getAttribute(Columns.DAD_VALOR));
                    }
                }

                calcularValores(cto, responsavel);
                return substituirPadroes(template, cto);
            }
            return null;
        } catch (ViewHelperException | AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Gera um texto de boleto, baseado em um template e um objeto com
     * os dados do contrato.
     * @param adeCodigo
     * @param includeCSS - se mantém ou remove CSS do template final.
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static String gerarTextoBoleto(String adeCodigo, boolean includeCSS, AcessoSistema responsavel) throws ViewHelperException {
        final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        CustomTransferObject cto;
        try {
            cto = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

            cto = TransferObjectHelper.mascararUsuarioHistorico(cto, null, responsavel);
            String msgBoleto = null;

            final List<String> parametros = new ArrayList<>();
            parametros.add(CodedValues.TPS_BUSCA_BOLETO_EXTERNO);

            final String svcCodigo = cto.getAttribute(Columns.SVC_CODIGO).toString();
            final ParametroDelegate parDelegate = new ParametroDelegate();
            final ParamSvcTO paramSvcCse = parDelegate.selectParamSvcCse(svcCodigo, parametros, responsavel);
            final boolean boletoExterno = paramSvcCse.isTpsBuscaBoletoExterno();

            if (boletoExterno) {
                File arqBoleto = null;
                final String sadCodigo = (String) cto.getAttribute(Columns.ADE_SAD_CODIGO);
                String absolutePath = null;
                if (!CodedValues.SAD_SOLICITADO.equals(sadCodigo)) {
                    final String boleto = CodedNames.TEMPLATE_EXTRATO_AUT_DESCONTO;
                    absolutePath = ParamSist.getDiretorioRaizArquivos();
                    absolutePath += File.separatorChar +  "boleto" + File.separatorChar + svcCodigo + File.separatorChar + boleto;
                    arqBoleto = new File(absolutePath);
                    if (!arqBoleto.exists()) {
                        absolutePath = ParamSist.getDiretorioRaizArquivos();
                        absolutePath += File.separatorChar + "boleto" + File.separatorChar + boleto;
                        arqBoleto = new File(absolutePath);
                        if (!arqBoleto.exists()) {
                            arqBoleto = null;
                        }
                    }
                }
                if (arqBoleto == null) {
                    final String boleto = CodedNames.TEMPLATE_BOLETO_AUT_DESCONTO;

                    absolutePath = ParamSist.getDiretorioRaizArquivos();
                    absolutePath += File.separatorChar +  "boleto" + File.separatorChar + svcCodigo + File.separatorChar + boleto;

                    arqBoleto = new File(absolutePath);
                    if (!arqBoleto.exists()) {
                        absolutePath = ParamSist.getDiretorioRaizArquivos();
                        absolutePath += File.separatorChar + "boleto" + File.separatorChar + boleto;
                        arqBoleto = new File(absolutePath);
                        if (!arqBoleto.exists()) {
                            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.boleto.nao.encontrado", responsavel, absolutePath));
                            throw new ViewHelperException("mensagem.erro.interno.boleto.nao.encontrado", responsavel);
                        }
                    }
                }

                // Busca os demais dados necessários para o boleto, que não estão na query principal.
                final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                final String orgCodigo = cto.getAttribute(Columns.ORG_CODIGO).toString();
                final OrgaoTransferObject orgao = cseDelegate.findOrgao(orgCodigo, responsavel);
                cto.setAtributos(orgao.getAtributos());

                // Definição da origem do contrato
                final List<String> adeList = new ArrayList<>();
                adeList.add(adeCodigo);
                final ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
                final List<TransferObject> relList = consigDelegate.pesquisarConsignacaoRelacionamento(adeList, responsavel);

                if (!relList.isEmpty()) {
                    for (final TransferObject relaciomento : relList) {
                        final String adeDestino = (String) relaciomento.getAttribute(Columns.RAD_ADE_CODIGO_DESTINO);
                        if (!TextHelper.isNull(adeDestino) && adeDestino.equals(adeCodigo)) {
                            final String tntCodigo = (String) relaciomento.getAttribute(Columns.RAD_TNT_CODIGO);

                            if (CodedValues.TNT_CONTROLE_RENEGOCIACAO.equals(tntCodigo)) {
                                cto.setAttribute("origem_ade", ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", responsavel));
                            } else if (CodedValues.TNT_CONTROLE_COMPRA.equals(tntCodigo)) {
                                cto.setAttribute("origem_ade", ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.compra", responsavel));
                            }

                            break;
                        }
                    }

                    if (cto.getAttribute("origem_ade") == null) {
                        cto.setAttribute("origem_ade", ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel));
                    }
                } else {
                    cto.setAttribute("origem_ade", ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel));
                }

                String msgTemplate = FileHelper.readAll(absolutePath);
                if (!includeCSS) {
                    msgTemplate = msgTemplate.replace("\r", "");
                    msgTemplate = msgTemplate.replace("\n", "");
                    msgTemplate = msgTemplate.replaceAll("(?i)<style>(.*)</style>", "");
                }
                msgBoleto = gerarTextoBoleto(msgTemplate, cto, responsavel);

            } else {
                msgBoleto = EnviaEmailHelper.geraHtmlBoleto(cto, includeCSS, responsavel);
            }

            return msgBoleto;

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private static void calcularValores(CustomTransferObject cto, AcessoSistema responsavel) throws ViewHelperException {
        // Parametros de sistema necessários
        final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        // Recupera dia de pagamento da primeira parcela utilizado no cálculo da simulação por taxa
        final String orgCodigo = cto.getAttribute(Columns.ORG_CODIGO) != null ? (String) cto.getAttribute(Columns.ORG_CODIGO) : null;

        // Dados necessários do contrato para os cálculos
        final String adeCodigo = cto.getAttribute(Columns.ADE_CODIGO).toString();
        final Integer adePrazo = (Integer) cto.getAttribute(Columns.ADE_PRAZO);
        final BigDecimal adeVlr = (BigDecimal) cto.getAttribute(Columns.ADE_VLR);
        BigDecimal adeVlrLiberado = (cto.getAttribute(Columns.CDE_VLR_LIBERADO) != null ? (BigDecimal) cto.getAttribute(Columns.CDE_VLR_LIBERADO) : new BigDecimal("0.00"));
        final BigDecimal adeVlrTac = (cto.getAttribute(Columns.ADE_VLR_TAC) != null ? (BigDecimal) cto.getAttribute(Columns.ADE_VLR_TAC) : new BigDecimal("0.00"));
        final BigDecimal adeVlrIof = (cto.getAttribute(Columns.ADE_VLR_IOF) != null ? (BigDecimal) cto.getAttribute(Columns.ADE_VLR_IOF) : new BigDecimal("0.00"));
        final java.util.Date adeAnoMesIni = (java.util.Date) cto.getAttribute(Columns.ADE_ANO_MES_INI);
        final java.util.Date adeData = (java.util.Date) cto.getAttribute(Columns.ADE_DATA);
        final String adeTipoTaxa = (String) cto.getAttribute(Columns.ADE_TIPO_TAXA);

        if (adeVlrLiberado.signum() == 0) {
            // Se o valor liberado na tabela coeficiente desconto não está cadastrado, verifica o valor presente
            // na tabela aut desconto, pois sistemas que possuem validação de taxa de juros, mas não possuem
            // simulador de empréstimos não cadastram os valores na tabela de coeficiente desconto
            adeVlrLiberado = (cto.getAttribute(Columns.ADE_VLR_LIQUIDO) != null ? (BigDecimal) cto.getAttribute(Columns.ADE_VLR_LIQUIDO) : new BigDecimal("0.00"));
        }

        // Parâmetros de serviço necessários
        final String svcCodigo = cto.getAttribute(Columns.SVC_CODIGO).toString();

        final List<String> parametros = new ArrayList<>();
        parametros.add(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF);
        parametros.add(CodedValues.TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS);
        parametros.add(CodedValues.TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS);
        parametros.add(CodedValues.TPS_VLR_LIQ_TAXA_JUROS);

        ParamSvcTO paramSvcCse = null;
        try {
            final ParametroDelegate parDelegate = new ParametroDelegate();
            paramSvcCse = parDelegate.selectParamSvcCse(svcCodigo, parametros, responsavel);
        } catch (final ParametroControllerException ex) {
            throw new ViewHelperException(ex);
        }

        // Data Atual
        cto.setAttribute("agora", DateHelper.toDateTimeString(DateHelper.getSystemDatetime()));
        cto.setAttribute("ade_data_hora", DateHelper.toDateTimeString(adeData));

        // Data de Validade da Solicitação
        final int diasDesblSolicitacoes = !TextHelper.isNull(paramSvcCse.getTpsDiasDesblSolicitacaoNaoConf()) ? Integer.parseInt(paramSvcCse.getTpsDiasDesblSolicitacaoNaoConf()) : -1;
        if (diasDesblSolicitacoes != -1) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(adeData);
            cal.add(Calendar.DATE, diasDesblSolicitacoes - 1);
            cto.setAttribute("ade_data_validade", DateHelper.format(cal.getTime(), "yyyy-MM-dd"));
        }

        if ((adePrazo != null) && (adePrazo.intValue() > 0)) {
            // Valor Total Pago
            final BigDecimal adeVlrTotalPago = adeVlr.multiply(new BigDecimal(adePrazo.toString()));
            cto.setAttribute("ade_vlr_total_pago", adeVlrTotalPago);

            // CET Anual
            try {
                final String cet = cto.getAttribute(Columns.CFT_VLR) != null ?  NumberHelper.reformat(cto.getAttribute(Columns.CFT_VLR).toString(), "en", NumberHelper.getLang()) : null;
                cto.setAttribute("cet_anual", !TextHelper.isNull(cet) ? CDCHelper.getStrTaxaEquivalenteAnual(cet) : "");
            } catch (final ParseException ex) {
                LOG.debug(ex.getMessage());
            }

            // Se tem a informação de valor liberado, então calcula os valores financeiros
            if (adeVlrLiberado.signum() > 0) {

                // Valor Total Encargos
                BigDecimal adeVlrTotalEncargos = adeVlrTotalPago.subtract(adeVlrLiberado);
                if (adeVlrTotalEncargos.signum() == -1) {
                    adeVlrTotalEncargos = new BigDecimal("0.00");
                }
                cto.setAttribute("ade_vlr_total_encargos", adeVlrTotalEncargos);

                // Valor Total Juros
                final BigDecimal adeVlrJuros = adeVlrTotalEncargos.subtract(adeVlrTac).subtract(adeVlrIof);
                cto.setAttribute("ade_vlr_juros", adeVlrJuros);

                try {
                    BigDecimal vlrLiberadoTotal = adeVlrLiberado;
                    if (simulacaoPorTaxaJuros) {
                        // Verifica parâmetros de serviço para adição de TAC / IOF ao valor liberado
                        if (paramSvcCse.isTpsAddValorTacValTaxaJuros()) {
                            vlrLiberadoTotal = vlrLiberadoTotal.add(adeVlrTac);
                        }
                        if (paramSvcCse.isTpsAddValorIofValTaxaJuros()) {
                            vlrLiberadoTotal = vlrLiberadoTotal.add(adeVlrIof);
                        }
                    } else {
                        // Se simulação por coeficientes, adiciona TAC / OP ao valor liberado
                        BigDecimal adeTac = null, adeOp = null;
                        try {
                            final List<String> tpsCodigosTaxas = new ArrayList<>();
                            tpsCodigosTaxas.add(CodedValues.TPS_TAC_FINANCIADA);
                            tpsCodigosTaxas.add(CodedValues.TPS_OP_FINANCIADA);
                            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                            final Map<String, String> taxas = adeDelegate.getParamSvcADE(adeCodigo, tpsCodigosTaxas, responsavel);
                            adeTac = taxas.get(CodedValues.TPS_TAC_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_TAC_FINANCIADA).toString());
                            adeOp = taxas.get(CodedValues.TPS_OP_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_OP_FINANCIADA).toString());

                            vlrLiberadoTotal = vlrLiberadoTotal.add(adeTac).add(adeOp);
                        } catch (final AutorizacaoControllerException ex) {
                            throw new ViewHelperException(ex);
                        }
                    }

                    // Taxa de Juros Real: Cálculo com valor liberado somando IOF e TAC
                    final BigDecimal adeTaxaJurosReal = SimulacaoHelper.calcularTaxaJuros(vlrLiberadoTotal, adeVlr, adePrazo, adeData, adeAnoMesIni, orgCodigo, responsavel).setScale(2, java.math.RoundingMode.HALF_UP);
                    cto.setAttribute("cft_vlr_real", adeTaxaJurosReal);

                    // CET Real: Cálculo com valor liberado sem adicionar IOF e TAC
                    final BigDecimal vlrCetReal = SimulacaoHelper.calcularTaxaJuros(adeVlrLiberado, adeVlr, adePrazo, adeData, adeAnoMesIni, orgCodigo, responsavel).setScale(2, java.math.RoundingMode.HALF_UP);
                    cto.setAttribute("cet_vlr_real", vlrCetReal);

                    // CET Anual Real: Cálculo à partir do CET real
                    final String cetReal = NumberHelper.format(vlrCetReal.doubleValue(), NumberHelper.getLang());
                    cto.setAttribute("cet_anual_real", !TextHelper.isNull(cetReal) ? CDCHelper.getStrTaxaEquivalenteAnual(cetReal) : "");

                    // Define rótulo para o campo de Taxa de Juros/CET/Coeficiente
                    String rotuloTaxa = "";
                    if (adeTipoTaxa != null) {
                        if (CodedValues.TIPO_TAXA_CET.equals(adeTipoTaxa)) {
                            rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel);
                        } else if (CodedValues.TIPO_TAXA_JUROS.equals(adeTipoTaxa)) {
                            rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel);
                        } else if (CodedValues.TIPO_TAXA_COEFICIENTE.equals(adeTipoTaxa)) {
                            rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.coeficiente", responsavel);
                        }
                    } else if (temCET) {
                        rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel);
                    } else if (paramSvcCse.isTpsVlrLiqTaxaJuros() || simulacaoPorTaxaJuros) {
                        rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel);
                    } else {
                        rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.coeficiente", responsavel);
                    }
                    cto.setAttribute("rotulo_taxa", rotuloTaxa);
                } catch (final ViewHelperException ex) {
                    LOG.debug(ex.getMessage());
                }
            }
        }
    }

    /**
     * Substitui padrões no estilo "<@chave>" pelas variáveis correspondentes presentes
     * no custom transfer object passado por parâmetro.
     * @param template
     * @param cto
     * @return
     * @throws ViewHelperException
     */
    public static String substituirPadroes(String template, CustomTransferObject cto) throws ViewHelperException {
        String chave = "";
        String padrao = "";
        String valor = "";

        final Iterator<String> it = cto.getAtributos().keySet().iterator();
        while (it.hasNext()) {
            valor = "";
            chave = it.next();

            if (chave.indexOf(".") > 0) {
                padrao = "<@" + chave.substring(chave.indexOf(".") + 1, chave.length()) + ">";
            } else {
                padrao = "<@" + chave + ">";
            }

            if (cto.getAttribute(chave) != null) {
                if ("<@ade_prazo>".equalsIgnoreCase(padrao)) {
                    valor = TextHelper.forHtmlContent(cto.getAttribute(chave).toString().trim());

                } else if ("<@ade_tipo_vlr>".equalsIgnoreCase(padrao)) {
                    if ("p".equalsIgnoreCase(cto.getAttribute(chave).toString().trim())) {
                        valor = ApplicationResourcesHelper.getMessage("rotulo.porcentagem", (AcessoSistema) null);
                    } else {
                        valor = "" ;
                    }

                } else if ("<@usu_login>".equalsIgnoreCase(padrao)) {
                    final String loginResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                    final String adeResponsavel = (loginResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) &&
                            (cto.getAttribute(Columns.USU_TIPO_BLOQ) != null)) ? (cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") :
                                loginResponsavel;
                    valor = TextHelper.forHtmlContent(adeResponsavel);

                } else if ("<@ade_data_hora>".equalsIgnoreCase(padrao)) {
                    valor = TextHelper.forHtmlContent(cto.getAttribute(chave).toString());

                } else if ((chave.toLowerCase().indexOf("data") != -1) || (chave.toLowerCase().indexOf("ano_mes") != -1)) {
                    try {
                        final Object objctVlr = cto.getAttribute(chave);
                        if (!TextHelper.isNull(objctVlr)) {
                            final boolean formatarPeriodo =
                                    Columns.ADE_ANO_MES_INI.equals(chave) ||
                                    Columns.ADE_ANO_MES_FIM.equals(chave) ||
                                    Columns.ADE_ANO_MES_INI_REF.equals(chave) ||
                                    Columns.ADE_ANO_MES_FIM_REF.equals(chave) ||
                                    Columns.ADE_ANO_MES_INI_FOLHA.equals(chave) ||
                                    Columns.ADE_ANO_MES_FIM_FOLHA.equals(chave);

                            if (objctVlr instanceof final java.util.Date data) {
                                valor = formatarPeriodo ? DateHelper.toPeriodString(data) : DateHelper.toDateString(data);
                            } else if (chave.toLowerCase().endsWith("_noescape")) {
                                valor = objctVlr.toString();
                            } else {
                                valor = DateHelper.reformat(objctVlr.toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
                            }
                        }
                    } catch (final ParseException ex) {
                        LOG.error(chave + " - " + cto.getAttribute(chave) + ": " + ex.getMessage());
                        valor = "";
                    }

                } else if ((chave.toLowerCase().indexOf("_vlr") != -1) || ((chave.toLowerCase().indexOf("_margem") != -1) && !Columns.CSA_CONSULTA_MARGEM_SEM_SENHA.equals(chave.toLowerCase()))) {
                    try {
                        final Object objctVlr = cto.getAttribute(chave);
                        if (!TextHelper.isNull(objctVlr)) {
                            if (objctVlr instanceof BigDecimal) {
                                valor = NumberHelper.format(((BigDecimal) objctVlr).doubleValue(), NumberHelper.getLang(), true);
                            } else if (objctVlr instanceof Double) {
                                valor = NumberHelper.format(((Double) objctVlr), NumberHelper.getLang(), true);
                            } else if (chave.toLowerCase().endsWith("_noescape")) {
                                valor = cto.getAttribute(chave).toString().trim();
                            } else {
                                valor = NumberHelper.reformat(TextHelper.forHtmlContent(objctVlr).toString(), "en", NumberHelper.getLang(), true);
                            }
                        }
                    } catch (final ParseException ex) {
                        LOG.error(chave + " - " + cto.getAttribute(chave) + ": " + ex.getMessage());
                        valor = "";
                    }

                } else {
                    final Object objctVlr = cto.getAttribute(chave);

                    if ((objctVlr != null) && (objctVlr instanceof List)) {
                        template = geraLista(padrao, (List<CustomTransferObject>) objctVlr, template);
                        valor = "";
                        final String padraoFim = padrao.substring(0,2) + "/" + padrao.substring(2);
                        template = template.replaceAll(padraoFim, "");
                    } else if (chave.toLowerCase().endsWith("_noescape")) {
                        valor = cto.getAttribute(chave).toString().trim();
                    } else {
                        valor = TextHelper.forHtmlContent(cto.getAttribute(chave).toString().trim());
                    }
                }
            }
            template = template.replaceAll(padrao, Matcher.quoteReplacement(valor));
        }

        String limpa = "";
        while (template.indexOf("<@") != -1) {
            limpa = template.substring(template.indexOf("<@"), template.indexOf(">", template.indexOf("<@")) + 1);
            template = template.replaceAll(limpa, "");
        }
        return template;
    }

    private static String geraLista(String padrao, List<CustomTransferObject> lista, String template) throws ViewHelperException {
        final int posPadrao = template.indexOf(padrao);
        if (posPadrao < 0) {
            return template;
        }
        final int inicioRepeticao = posPadrao + padrao.length();
        final String padraoFim = padrao.substring(0,2) + "/" + padrao.substring(2);
        final int fimRepeticao = template.indexOf(padraoFim);

        if (fimRepeticao < 0) {
            throw new ViewHelperException("mensagem.erro.sintaxe.boleto", (AcessoSistema) null, padraoFim);
        } else if (posPadrao < 0) {
            throw new ViewHelperException("mensagem.erro.sintaxe.boleto", (AcessoSistema) null, padrao);
        }

        final String templateRepeticao = template.substring(inicioRepeticao, fimRepeticao);

        final StringBuilder listagem = new StringBuilder("");

        for (final CustomTransferObject cto: lista) {
            listagem.append(substituirPadroes(templateRepeticao, cto));
        }

        return template.substring(0,inicioRepeticao) + listagem.toString() + template.substring((fimRepeticao + padraoFim.length()), template.length());
    }
}
