package com.zetra.econsig.helper.financeiro;

import java.math.BigDecimal;
import java.text.ParseException;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: CDCHelper</p>
 * <p>Description: Classe auxiliar para cálculo de valores de empréstimo.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CDCHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CDCHelper.class);

    private int prazo = 0;
    private double taxaMensal = 0.0;
    private double valorSolicitado = 0.0;

    private double valorFinanciado = 0.0;
    private double iofEmbutido = 0.0;

    private final double aliquotaCpmf = 0.0;
    private double aliquotaMensalIof = 0.0;
    private double aliquotaAdicionalIofSobreOperacao = 0.0;

    // Número de dias corridos entre a data da contratação da operação, inclusive,
    // e a data-base do período anterior ao do vencimento da primeira prestação, exclusive.
    private int dc = 0;

    // Tarifa de abertura de crédito.
    private double tac = 0;
    // Alíquota da TAC expressa na forma unitária.
    private final double ptac = 0;
    // TAC mínima por operação.
    private final double tacMin = 0;
    // TAC máxima por operação.
    private final double tacMax = 0;

    // Prazo, em meses, contado da data de contratação, inclusive, até a data de vencimento
    // da prestação "k", exclusive, para o cálculo do IOF.
    private final double[] tk;
    // Valor, em moeda corrente, da quota de amortização de capital referente à prestação "k"
    // da operação, para cálculo do IOF.
    private final double[] qsk;
    // Valor, em moeda corrente, dos juros referentes ao período de carência da operação, para
    // cálculo do IOF.
    private final double[] jsk;
    // Valor, em moeda corrente, do saldo devedor da operação na data de vencimento da
    // prestação "k", após o pagamento da mesma, para o cálculo do IOF.
    private final double[] ssk;
    // Valor, em moeda corrente, do saldo devedor da operação na data de vencimento da
    // prestação "k", após o pagamento da mesma.
    private final double[] sk;
    // Valor, em moeda corrente, da prestação da operação.
    private double pmt = 0;

    // Informa se a TAC é financiada.
    private final boolean isTacFinanciada = false;

    /**
     * Construtor
     * @param valorSolicitado
     * @param numeroDePrestacoes
     * @param taxa
     * @param dc Dias entre a data de contratação e a data-base do período anterior ao do vencimento da primeira parcela.
     */
    public CDCHelper(double valorSolicitado, int numeroDePrestacoes, double taxa, int dc,
                     Double aliquotaAnualIOF, Double aliquotaAdicionalIOF) {
        // ------------------------------------------------------------------------------------------------
        // Obtém a alíquota do IOF do parâmetro de sistema TPC_ALIQUOTA_IOF.
        // Se o parâmetro for nulo, o IOF será 1.5
        if (aliquotaAnualIOF == null) {
            try {
                aliquotaAnualIOF = (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ANUAL_IOF, AcessoSistema.getAcessoUsuarioSistema())) ?
                        Double.parseDouble(ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ANUAL_IOF, AcessoSistema.getAcessoUsuarioSistema()).toString()) : null);
            } catch (Exception ex) {
                LOG.debug("Valor Inválido para o parâmetro de sistema (" + CodedValues.TPC_ALIQUOTA_ANUAL_IOF + ") Alíquota IOF");
                aliquotaAnualIOF = null;
            }
            aliquotaAnualIOF = (aliquotaAnualIOF != null ? aliquotaAnualIOF : 1.5) / 100.0;
        }

        // Calcula o valor mensal do IOF, baseado na aliquota anual
        aliquotaMensalIof =  aliquotaAnualIOF / 12.0;
        // ------------------------------------------------------------------------------------------------
        // Obtém a alíquota adicional do IOF do parâmetro de sistema TPC_ALIQUOTA_ADICIONAL_IOF.
        // Se o parâmetro for nulo, a aliquota adicional será 0
        if (aliquotaAdicionalIOF == null) {
            try {
                aliquotaAdicionalIOF = (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ADICIONAL_IOF, AcessoSistema.getAcessoUsuarioSistema())) ?
                        Double.parseDouble(ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ADICIONAL_IOF, AcessoSistema.getAcessoUsuarioSistema()).toString()) : null);
            } catch (Exception ex) {
                LOG.debug("Valor Inválido para o parâmetro de sistema (" + CodedValues.TPC_ALIQUOTA_ADICIONAL_IOF + ") Alíquota Adicional IOF");
                aliquotaAdicionalIOF = null;
            }
            aliquotaAdicionalIOF = aliquotaAdicionalIOF != null ? aliquotaAdicionalIOF / 100.0 : 0.0;
        }
        aliquotaAdicionalIofSobreOperacao = aliquotaAdicionalIOF;

        this.valorSolicitado = valorSolicitado;
        prazo = numeroDePrestacoes;
        taxaMensal = taxa;
        this.dc = dc;

        tk  = new double[numeroDePrestacoes + 1];
        qsk = new double[numeroDePrestacoes + 1];
        jsk = new double[numeroDePrestacoes + 1];
        ssk = new double[numeroDePrestacoes + 1];
        sk  = new double[numeroDePrestacoes + 1];
    }

    public CDCHelper(double valorSolicitado, int numeroDePrestacoes, double taxa, int dc) {
        this (valorSolicitado, numeroDePrestacoes, taxa, dc, null, null);

    }

    public void calculate() {
        //TABELA 1 - Apuração do valor financiado, IOF, TAC e CPMF.

        // 1 - Multiplicador para o cálculo da prestação.
        double mj = (Math.pow(1.0 + taxaMensal, prazo) * taxaMensal) / (Math.pow(1.0 + taxaMensal, prazo) - 1);

        // 2 - Juros no período de carência.
        double jcs = valorSolicitado * (Math.pow(1.0 + taxaMensal, dc / 30.0) - 1.0);

        // 3 - Saldo da operação na data final da carência.
        ssk[0] = valorSolicitado + jcs;

        // 4 - Prestação para cálculo do IOF.
        double pmts = ssk[0] * mj;

        // 5 - Juros no período "k".
        for (int k = 1; k < prazo; k++) {
            jsk[k] = ssk[k - 1] * taxaMensal;
            ssk[k] = ssk[k - 1] + jsk[k] - pmts;
        }
        jsk[prazo] = pmts - ssk[prazo - 1];

        // 6 - Saldo devedor da operação no vencimento da prestação "k".
        ssk[prazo] = ssk[prazo - 1] + jsk[prazo] - pmts;

        double somaTQSk = 0;
        for (int k = 1; k <= prazo; k++) {
            // 7 - Quota de amortização do capital no período "k".
            qsk[k] = getQSk(k, pmts, jcs);
            // 8 - Prazo, em meses, até o período "k"
            tk[k] = getTk(k);

            // Aproveita o loop para realizar o somatório necessário no item 9.
            somaTQSk += tk[k] * qsk[k];
        }

        // 9 - Cálculo do IOF não embutido.
        double iofne = ((valorSolicitado * aliquotaAdicionalIofSobreOperacao) + aliquotaMensalIof * somaTQSk) / (1.0 - aliquotaCpmf - ptac);

        // 10 - Cálculo da TAC sobre valor solicitado.
        double tacs = valorSolicitado * ptac;
        if (tacs < tacMin) {
            tacs = tacMin;
        } else if (tacs > tacMax) {
            tacs = tacMax;
        }

        // 11 - Cálculo do IOF embutido.
        iofEmbutido = (valorSolicitado * iofne) / ((valorSolicitado - iofne) - (1.0 - aliquotaCpmf - (tacs / valorSolicitado)));

        // 12 - Cálculo do valor a ser financiado no fluxo normal de pagamento
        if (!isTacFinanciada) {
            tacs = 0;
            valorFinanciado = (valorSolicitado + (iofEmbutido * (1.0 - aliquotaCpmf))) / (1.0 - aliquotaCpmf - (tacs / valorSolicitado));
        } else {
            double tac = this.tac;
            if (valorFinanciado * ptac < tacMin) {
                tac = tacMin;
            } else if (valorFinanciado * ptac > tacMax) {
                tac = tacMax;
            }
            valorFinanciado = (valorSolicitado + tac + (iofEmbutido * (1.0 - aliquotaCpmf))) / (1.0 - aliquotaCpmf);
        }

        //TABELA 2 - Contratação

        // 1 - Juros no período de carência.
        double jc = valorFinanciado * (Math.pow(1.0 + taxaMensal, dc / 30.0) - 1.0);

        // 2 - Saldo devedor na data final de carência (k = 0).
        sk[0] = valorFinanciado + jc;

        // 3 - Prestação no período "k".
        pmt = sk[0] * mj;
    }

    /**
     * Calcula a quota de amortização do capital no período "k".
     * @param k Período
     * @param pmts Valor da prestação da operação prefixada
     * @param jcs Valor dos juros referentes ao período de carência da operação
     * @return
     */
    private double getQSk(int k, double pmts, double jcs) {
        double somaJSp = 0;
        for (int p = 1; p <= k; p++) {
            somaJSp += jsk[p];
        }
        double somaQSp = 0;
        for (int p = 1; p < k; p++) {
            somaQSp += qsk[p];
        }
        double qsk = (k * pmts) - jcs - somaJSp - somaQSp;
        return (qsk < 0) ? 0 : qsk;
    }


    /**
     * Prazo, em meses, contados da data da contratação, inclusive, até a data de vencimento da prestação "k".
     * @param k Prestação.
     * @return
     */
    private double getTk(int k) {
        double tk = k + (dc / 30.0);
        return (tk > 12.0) ? 12.0 : tk;
    }

    /**
     * Obtém IOF embutido.
     * @return
     */
    public double getIOFE() {
        return iofEmbutido;
    }

    /**
     * Obtém valor financiado.
     * @return
     */
    public double getVF() {
        return valorFinanciado;
    }

    /**
     * Obtém valor da prestação.
     * @return
     */
    public double getPMT() {
        return pmt;
    }

    /**
     * Obtém o valor da tarifa de abertura de crédito.
     * @return
     */
    public double getTAC() {
        tac = valorFinanciado * ptac;
        if (tac < tacMin) {
            tac = tacMin;
        } else if (tac > tacMax) {
            tac = tacMax;
        }

        return tac;
    }

    /**
     * Obtém o valor da CPMF sobre o valor financiado.
     * @return
     */
    public double getCPMF() {
        return (valorFinanciado - iofEmbutido) * aliquotaCpmf;
    }

    /**
     * Obtém o valor do IOF adicional de acordo com a configuração dos sistema.
     * @param valorSolicitado
     * @return
     */
    public static double calcularIofAdicional(double valorSolicitado) {
        // ------------------------------------------------------------------------------------------------
        // Obtém a alíquota adicional do IOF do parâmetro de sistema TPC_ALIQUOTA_ADICIONAL_IOF.
        // Se o parâmetro for nulo, a aliquota adicional será 0
        double aliquotaAdicionalIOF = -1;
        try {
            aliquotaAdicionalIOF = (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ADICIONAL_IOF, AcessoSistema.getAcessoUsuarioSistema())) ?
                    Double.parseDouble(ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ADICIONAL_IOF, AcessoSistema.getAcessoUsuarioSistema()).toString()) : -1);
        } catch (Exception ex) {
            LOG.debug("Valor Inválido para o parâmetro de sistema (" + CodedValues.TPC_ALIQUOTA_ADICIONAL_IOF + ") Alíquota Adicional IOF");
            aliquotaAdicionalIOF = -1;
        }
        double aliquotaAdicionalIofSobreOperacao = (aliquotaAdicionalIOF != -1) ? (aliquotaAdicionalIOF / 100.00) : 0;
        // ------------------------------------------------------------------------------------------------

        return (valorSolicitado * aliquotaAdicionalIofSobreOperacao);
    }

    /**
     * Calcula a taxa anual equivalente a taxa mensal informada.
     * @param taxaMensal String com taxa mensal no formato NumberHelper.getLang() a ser convertida.
     * @return Retorna o string no formato NumberHelper.getLang() com a taxa anual calculada.
     *         Retorna null se não conseguir fazer a conversão.
     */
    public static String getStrTaxaEquivalenteAnual(String taxaMensal) {
        String taxaAnual = null;
        if (!TextHelper.isNull(taxaMensal)) {
            try {
                BigDecimal um = new BigDecimal(1), cem = new BigDecimal(100);
                BigDecimal mensal = new BigDecimal(NumberHelper.parse(taxaMensal, NumberHelper.getLang())).divide(cem);
                BigDecimal anual = mensal.add(um).pow(12).subtract(new BigDecimal(1)).multiply(cem);
                taxaAnual = NumberHelper.format(anual.doubleValue(), NumberHelper.getLang());
            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return taxaAnual;
    }
}