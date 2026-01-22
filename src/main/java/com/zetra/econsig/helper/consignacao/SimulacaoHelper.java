package com.zetra.econsig.helper.consignacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.financeiro.SimulacaoMetodoMexicano;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.RepasseHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: SimulacaoHelper</p>
 * <p>Description: Helper Class para Operação de Simulação de Consignação</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SimulacaoHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SimulacaoHelper.class);

    /**
     * Função para calcular a taxa de juros a partir do valor presente,
     * valor da parcela e o prazo do contrato.
     * Fonte: http://www.fw.uri.br/~arpasi/financeira/index.php
     * @param valorPresente
     * @param valorParcela
     * @param prazo
     * @param carencia
     * @return
     * @throws ViewHelperException
     */
    private static double calcularTaxaJurosAno360Dias(BigDecimal valorPresente, BigDecimal valorParcela, int prazo, double carencia, AcessoSistema responsavel) throws ViewHelperException {
        if ((valorPresente == null) || (valorParcela == null)) {
            throw new ViewHelperException("mensagem.informe.valor.total.liberado.valor.parcela", responsavel);
        }

        if (prazo == 0) {
            return 0;
        }
        if ((prazo * valorParcela.doubleValue()) > valorPresente.doubleValue()) {
            /*
             * Nomes das variáveis seguindo o padrão do algoritmo mostrado no arquivo
             * http://www.fw.uri.br/~arpasi/financeira/financeira.pdf
             */
            final double e = Math.pow(10, -10);
            double a = e;
            double b = 1 - e;
            double i = (a + b) / 2;
            double fk;
            while (Math.abs(a - b) > e) {
                fk = f(a, prazo, carencia, valorPresente, valorParcela) * f(i, prazo, carencia, valorPresente, valorParcela);
                if (fk > 0) {
                    a = i;
                    i = (a + b) / 2;
                } else if (fk < 0) {
                    b = i;
                    i = (a + b) / 2;
                } else {
                    break;
                }
            }
            return i * 100;
        }
        return 0;
    }

    /**
     * Função auxiliar para cálculo da taxa de juros (Modo Postecipado)
     * @param x
     * @param prazo
     * @param carencia
     * @param valorPresente
     * @param valorParcela
     * @return
     */
    private static double f(double x, int prazo, double carencia, BigDecimal valorPresente, BigDecimal valorParcela) {
        if (x == 0) {
            return 0;
        }
        return (((((valorPresente.doubleValue() / valorParcela.doubleValue()) * Math.pow(1 + x, carencia)) -
                  ((valorPresente.doubleValue() / valorParcela.doubleValue()) * Math.pow(1 + x, carencia - 1))) +
                 Math.pow(1 + x, -prazo)) -
                1);
    }

    /**
     * Calcula a taxa de juros de um financiamento
     * @param valorLiberado : Valor liberado no financiamento.
     * @param valorParcela : Valor da parcela.
     * @param prazoMeses : Prazo.
     * @param dataContrato : Data de realização do contrato.
     * @param mesAnoPrimeiraParcela : Primeiro mês de vencimento.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return
     */
    public static BigDecimal calcularTaxaJuros(BigDecimal valorLiberado, BigDecimal valorParcela, int prazoMeses, Date dataContrato, Date mesAnoPrimeiraParcela, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        BigDecimal taxaJuros = null;
        // Verifica se os cálculos devem ser baseados em anos de 365 dias.
        if (ParamSist.getBoolParamSist(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, responsavel)) {
            taxaJuros = calcularTaxaJurosAno365Dias(valorLiberado, valorParcela, prazoMeses, dataContrato, mesAnoPrimeiraParcela, orgCodigo, responsavel);
        } else {
            // Calcula a diferença entre a data de inclusão do contrato e a data do primeiro desconto
            final int dias = calculateDC(dataContrato, mesAnoPrimeiraParcela, orgCodigo, responsavel);
            //deve deixar negativo./dias = dias < 0 ? 0 : dias;
            final double periodoCarencia = dias / 30.0;
            LOG.debug("periodoCarencia: " + periodoCarencia);
            taxaJuros = new BigDecimal(calcularTaxaJurosAno360Dias(valorLiberado, valorParcela, prazoMeses, 1 + periodoCarencia, responsavel));
        }

        return taxaJuros;
    }

    /**
     * Calcula a taxa de juros de um financiamento considerando o ano com 365 dias.
     * @param valorLiberado : Valor liberado no financiamento.
     * @param valorParcela : Valor da parcela.
     * @param prazoMeses : Prazo.
     * @param dataContrato : Data de realização do contrato.
     * @param mesAnoPrimeiraParcela : Primeiro mês de vencimento.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return
     */
    private static BigDecimal calcularTaxaJurosAno365Dias(BigDecimal valorLiberado, BigDecimal valorParcela, int prazoMeses, Date dataContrato, Date mesAnoPrimeiraParcela, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        // Nenhuma taxa será menor ou igual a -100%.
        BigDecimal taxaMinima = new BigDecimal("-99.99");
        // Inicialmente a taxa de juros máxima que será experimentada é de 50%.
        BigDecimal taxaMaxima = new BigDecimal("50.0");
        // Tenta primeiro a taxa máxima, para verificar se ela vai ser suficiente no cálculo.
        BigDecimal taxaAproximada = taxaMaxima;

        // Determina se a taxa máxima estipulada é suficiente para o cálculo.
        boolean limiteSuperiorSuficiente = false;

        BigDecimal valorLiberadoCalculado = new BigDecimal("0.0");

        // Enquanto o valor liberado calculado não for equivalente ao valor liberado real, realiza a aproximação da taxa.
        while (valorLiberadoCalculado.subtract(valorLiberado).abs().compareTo(new BigDecimal("0.000001")) > 0) {
            // Calcula o valor liberado calculado de acordo com a taxa experimentada.
            valorLiberadoCalculado = calcularVlrLiberadoAno365Dias(dataContrato, mesAnoPrimeiraParcela, valorParcela, prazoMeses, taxaAproximada, orgCodigo, responsavel);
            // Se o valor liberado calculado é maior que o valor liberado real,
            // Então a taxa de juros experimentada é menor que a taxa real.
            if (valorLiberadoCalculado.compareTo(valorLiberado) > 0) {
                // A taxa é, no mínimo, a experimentada.
                taxaMinima = taxaAproximada;
                if (!limiteSuperiorSuficiente) {
                    // Se o limite superior foi considerado insuficiente na iteração anterior
                    // e a taxa aproximada é menor que a real, devemos aumentar o limite superior
                    // para evitar um loop infinito.
                    taxaMaxima = taxaMaxima.multiply(new BigDecimal("10.0"));
                }
            } else {
                // Se a taxa experimentada é maior que a do financiamento, então
                // o limite superior estipulado é suficiente.
                limiteSuperiorSuficiente = true;

                // A taxa é, no máximo, a experimentada.
                taxaMaxima = taxaAproximada;
            }
            // taxa a experimentar = taxa mínima + taxa máxima / 2.
            taxaAproximada = taxaMinima.add(taxaMaxima).divide(new BigDecimal("2.0"), 10, java.math.RoundingMode.HALF_UP);
        }

        return taxaAproximada;
    }

    /**
     * Calculo utilizando a formula financeira para descobrir o valor da parcela,
     * ou o valor presente.
     * @param valorPresente
     * @param valorParcela
     * @param prazo
     * @return
     * @throws ViewHelperException
     */
    private static double calcularVlrFinanceiroAno360Dias(BigDecimal valorPresente, BigDecimal valorParcela, BigDecimal taxa, int prazo, AcessoSistema responsavel) throws ViewHelperException {
        if (((valorPresente == null) && (valorParcela == null)) || (taxa == null)) {
            throw new ViewHelperException("mensagem.informe.valor.taxa.total.liberado.ou.valor.parcela", responsavel);
        }

        try {
            taxa = taxa.divide(new BigDecimal("100.00"), 6, java.math.RoundingMode.HALF_UP);

            final BigDecimal UM = new BigDecimal("1");

            // D = 1 - (1 / (1 + i))^n
            BigDecimal denominador = UM.divide(UM.add(taxa), 10, java.math.RoundingMode.HALF_UP);
            denominador = UM.subtract(new BigDecimal(Math.pow(denominador.doubleValue(), prazo)));

            BigDecimal vlr = null;

            if (valorPresente != null) {
                // PMT = (VF * i) / D
                vlr = valorPresente.multiply(taxa).divide(denominador, 2, java.math.RoundingMode.HALF_UP);
            } else {
                // VP = (PMT * D) / i
                vlr = valorParcela.multiply(denominador).divide(taxa, 10, java.math.RoundingMode.HALF_UP);
            }

            return vlr.doubleValue();
        } catch (final NumberFormatException | ArithmeticException ex) {
            throw new ViewHelperException("mensagem.erro.valor.taxa.cadastrada", responsavel);
        } catch (final Exception ex) {
            throw new ViewHelperException("mensagem.erro.interno.arg0", responsavel, ex.getMessage());
        }
    }

    /**
     * Calcula o novo valor baseado na carencia.
     * @param diaDesc
     * @param adeData
     * @param dataIni
     * @param valorLiberado
     * @param taxa
     * @return
     * @throws ViewHelperException
     */
    private static double calcularVlrCorrigidoAno360Dias(Date adeData, Date dataIni, BigDecimal valorLiberado, BigDecimal valorLibCorrigido, BigDecimal taxa, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        return calcularVlrCorrigidoAno360Dias(adeData, dataIni, valorLiberado, valorLibCorrigido, taxa, false, orgCodigo, responsavel);
    }

    /**
     * Calcula o novo valor baseado na carencia.
     * @param adeData
     * @param dataIni
     * @param valorLiberado
     * @param taxa
     * @param ignoraPeriodoCarencia - DESENV-9830: ignora a diferença entre o dia corrente e o dia de repasse para o cálculo
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return
     * @throws ViewHelperException
     */
    private static double calcularVlrCorrigidoAno360Dias(Date adeData, Date dataIni, BigDecimal valorLiberado, BigDecimal valorLibCorrigido, BigDecimal taxa, boolean ignoraPeriodoCarencia, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        if (((valorLiberado == null) && (valorLibCorrigido == null)) || (taxa == null)) {
            throw new ViewHelperException("mensagem.informe.valor.taxa.total.liberado.ou.total.liberado.corrigido", responsavel);
        }

        double periodoCarencia = 0.00;
        if (!ignoraPeriodoCarencia) {
            final int dias = calculateDC(adeData, dataIni, orgCodigo, responsavel);
            //deve deixar negativo./dias = dias < 0 ? 0 : dias;
            periodoCarencia = dias / 30.0;
        }
        taxa = taxa.divide(new BigDecimal("100.00"), 6, java.math.RoundingMode.HALF_UP);
        BigDecimal retorno = null;
        if (valorLiberado != null) {
            retorno = (valorLiberado.multiply(new BigDecimal(Math.pow((1 + taxa.doubleValue()), periodoCarencia)))).setScale(2, java.math.RoundingMode.HALF_UP);
        } else if (valorLibCorrigido != null) {
            retorno = (valorLibCorrigido.divide(new BigDecimal(Math.pow((1 + taxa.doubleValue()), periodoCarencia)), java.math.RoundingMode.HALF_UP)).setScale(2, java.math.RoundingMode.HALF_UP);
        }
        return retorno.doubleValue();
    }

    /**
     * Calcula o saldo devedor de um contrato.
     * @param valorPrestacao : Valor da prestação
     * @param prazoRestante : Prazo restante.
     * @param mesAnoProximoDesconto : Mês e ano do próximo desconto.
     * @param taxaJuros : Taxa de juros.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return
     * @throws ViewHelperException
     */
    public static BigDecimal calcularSaldoDevedor(BigDecimal valorPrestacao, int prazoRestante, Date mesAnoProximoDesconto, BigDecimal taxaJuros, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        final Date dataAtual = DateHelper.getSystemDatetime();

        BigDecimal saldoDevedor = new BigDecimal("0.0");
        // Verifica se os cálculos devem ser baseados em anos de 365 dias.
        if (ParamSist.getBoolParamSist(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, responsavel)) {
            // O saldo devedor corresponde ao valor financiado calculado à data atual.
            saldoDevedor = calcularVlrLiberadoAno365Dias(dataAtual, mesAnoProximoDesconto, valorPrestacao, prazoRestante, taxaJuros, orgCodigo, responsavel);
        } else {
            final Calendar cal = Calendar.getInstance();
            for (int i = 1; i <= prazoRestante; i++) {
                cal.setTime(mesAnoProximoDesconto);
                cal.add(Calendar.MONTH, i);
                final Date dtPeriodoParcela = cal.getTime();

                final double vlrParcelaDescorrigido = calcularVlrCorrigidoAno360Dias(dataAtual, dtPeriodoParcela, null, valorPrestacao, taxaJuros, orgCodigo, responsavel);
                saldoDevedor = saldoDevedor.add(new BigDecimal(vlrParcelaDescorrigido));
            }
        }

        return saldoDevedor;
    }

    /**
     * Calcula o valor da prestação de um financiamento.
     * @param valorLiberado : Valor liberado
     * @param prazoMeses : Prazo do financiamento
     * @param dataContrato : Data do contrato
     * @param mesAnoPrimeiraParcela : Mês de vencimento da primeira parcela
     * @param taxaCorrecao : Taxa para cálculo da carência
     * @param taxaJuros : Taxa de juros do financiamento
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return
     * @throws ViewHelperException
     */
    public static BigDecimal calcularValorPrestacao(BigDecimal valorLiberado, int prazoMeses, Date dataContrato, Date mesAnoPrimeiraParcela, BigDecimal taxaCorrecao, BigDecimal taxaJuros, String orgCodigo, String adePeriodicidade, AcessoSistema responsavel) throws ViewHelperException {
        final Object metodo = ParamSist.getInstance().getParam(CodedValues.TPC_METODO_CALCULO_SIMULACAO, responsavel);
        BigDecimal parcelaCalc = null;

        if (TextHelper.isNull(metodo) || CodedValues.MCS_BRASILEIRO.equals(metodo)) {
            // Verifica se os cálculos devem ser baseados em anos de 365 dias.
            if (ParamSist.getBoolParamSist(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, responsavel)) {
                parcelaCalc = calcularVlrPrestacaoAno365Dias(dataContrato, mesAnoPrimeiraParcela, valorLiberado, prazoMeses, taxaJuros, orgCodigo, responsavel);
            } else {
                final BigDecimal valorCorrigido = new BigDecimal(calcularVlrCorrigidoAno360Dias(dataContrato, mesAnoPrimeiraParcela, valorLiberado, null, taxaCorrecao, orgCodigo, responsavel)).setScale(2, java.math.RoundingMode.HALF_UP);
                parcelaCalc = new BigDecimal(calcularVlrFinanceiroAno360Dias(valorCorrigido, null, taxaJuros, prazoMeses, responsavel)).setScale(2, java.math.RoundingMode.HALF_UP);
            }
        } else if (CodedValues.MCS_MEXICANO.equals(metodo)) {
            try {
                parcelaCalc = SimulacaoMetodoMexicano.calcularValorParcela(valorLiberado, taxaJuros, prazoMeses, adePeriodicidade, responsavel)[0];
            } catch (final SimulacaoControllerException e) {
                throw new ViewHelperException(e.getMessageKey(), responsavel);
            }

        } else if (CodedValues.MCS_INDIANO.equals(metodo)) {
            // DESENV-9830: Método indiano faz o cálculo price básico.
            parcelaCalc = new BigDecimal(calcularVlrFinanceiroAno360Dias(valorLiberado, null, taxaJuros, prazoMeses, responsavel)).setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            throw new ViewHelperException("mensagem.erro.metodo.calculo.validacao.naoImplementado", responsavel);
        }

        return parcelaCalc;
    }

    /**
     * Calcula o valor liberado de um financiamento.
     * @param valorPrestacao : Valor da prestação
     * @param prazoMeses : Prazo em meses.
     * @param dataContrato : Data do contrato.
     * @param mesAnoPrimeiraParcela : Mês de vencimento da primeira parcela
     * @param taxaJuros : Taxa de juros.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return
     * @throws ViewHelperException
     */
    public static BigDecimal calcularValorLiberado(BigDecimal valorPrestacao, int prazoMeses, Date dataContrato, Date mesAnoPrimeiraParcela, BigDecimal taxaJuros, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        return calcularValorLiberado(valorPrestacao, prazoMeses, dataContrato, mesAnoPrimeiraParcela, taxaJuros, orgCodigo, false, responsavel);
    }

    /**
     * Calcula o valor liberado de um financiamento.
     * @param valorPrestacao : Valor da prestação
     * @param prazoMeses : Prazo em meses.
     * @param dataContrato : Data do contrato.
     * @param mesAnoPrimeiraParcela : Mês de vencimento da primeira parcela
     * @param taxaJuros : Taxa de juros.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param ignoraPeriodoCarencia : ignora a diferença em dias entre o dia corrente e o dia de repasse no cálculo do valor
     * @param responsavel : Responsável pela operação
     * @return
     * @throws ViewHelperException
     */
    public static BigDecimal calcularValorLiberado(BigDecimal valorPrestacao, int prazoMeses, Date dataContrato, Date mesAnoPrimeiraParcela, BigDecimal taxaJuros, String orgCodigo, boolean ignoraPeriodoCarencia, AcessoSistema responsavel) throws ViewHelperException {
        BigDecimal valorLiberado = null;

        // Verifica se os cálculos devem ser baseados em anos de 365 dias.
        if (ParamSist.getBoolParamSist(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, responsavel)) {
            valorLiberado = calcularVlrLiberadoAno365Dias(dataContrato, mesAnoPrimeiraParcela, valorPrestacao, prazoMeses, taxaJuros, orgCodigo, responsavel);
        } else {
            final BigDecimal valorLiberadoCorrigido = new BigDecimal(calcularVlrFinanceiroAno360Dias(null, valorPrestacao, taxaJuros, prazoMeses, responsavel));
            valorLiberado = new BigDecimal(calcularVlrCorrigidoAno360Dias(dataContrato, mesAnoPrimeiraParcela, valorLiberado, valorLiberadoCorrigido, taxaJuros, ignoraPeriodoCarencia, orgCodigo, responsavel)).setScale(2, java.math.RoundingMode.HALF_UP);
        }

        return valorLiberado;
    }

    /**
     * Calcula o valor das parcelas de um financiamento, considerando cada ano com 365 dias.
     * @param dataContrato : Data de início do contrato
     * @param mesAnoVencimentoPrimeiraParcela : Mês e ano de vencimento da primeira parcela.
     * @param valorLiberado : Valor liberado
     * @param prazo : Prazo em meses.
     * @param taxaMensal : Taxa de juros mensal.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return Valor da parcela.
     * @throws ViewHelperException
     */
    private static BigDecimal calcularVlrPrestacaoAno365Dias(Date dataContrato, Date mesAnoVencimentoPrimeiraParcela, BigDecimal valorLiberado, int prazo, BigDecimal taxaMensal, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        final double multiplicador = calcularMultiplicadorPrestacaoAno365Dias(dataContrato, mesAnoVencimentoPrimeiraParcela, prazo, taxaMensal, orgCodigo, responsavel);

        if (Double.isInfinite(multiplicador)) {
            throw new ViewHelperException("mensagem.erro.calcular.valor.parcela.taxa.negativa", (AcessoSistema) null);
        }

        return valorLiberado.divide(new BigDecimal(multiplicador).setScale(10, java.math.RoundingMode.HALF_UP), java.math.RoundingMode.HALF_UP).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calcula o valor das parcelas de um financiamento, considerando cada ano com 365 dias.
     * @param dataContrato : Data de início do contrato
     * @param mesAnoVencimentoPrimeiraParcela : Mês e ano de vencimento da primeira parcela.
     * @param valorPrestacao : Valor da prestação
     * @param prazo : Prazo em meses.
     * @param taxaMensal : Taxa de juros mensal.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return Valor liberado.
     * @throws ViewHelperException
     */
    private static BigDecimal calcularVlrLiberadoAno365Dias(Date dataContrato, Date mesAnoVencimentoPrimeiraParcela, BigDecimal valorPrestacao, int prazo, BigDecimal taxaMensal, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        final double multiplicador = calcularMultiplicadorPrestacaoAno365Dias(dataContrato, mesAnoVencimentoPrimeiraParcela, prazo, taxaMensal, orgCodigo, responsavel);

        if (Double.isInfinite(multiplicador)) {
            throw new ViewHelperException("mensagem.erro.calcular.valor.liberado.taxa.negativa", (AcessoSistema) null);
        }

        return valorPrestacao.multiply(new BigDecimal(multiplicador).setScale(10, java.math.RoundingMode.HALF_UP)).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calcula o multiplicador que torna verdade a expressão PV = PMT x multiplicador
     * sendo PV o valor liberado e PMT o valor das parcelas do contrato.
     * Considera ano com 365 dias.
     * @param dataContrato : Data de contratação do financiamento
     * @param mesAnoVencimentoPrimeiraParcela : Mês e ano do vencimento da primeira parcela.
     * @param prazo : Prazo em meses.
     * @param taxaMensal : Taxa de juros mensal.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return O multiplicador. Atenção: O seu valor pode ser infinito.
     */
    private static double calcularMultiplicadorPrestacaoAno365Dias(Date dataContrato, Date mesAnoVencimentoPrimeiraParcela, int prazo, BigDecimal taxaMensal, String orgCodigo, AcessoSistema responsavel) {
        // Calcula a taxa anual correspondente à taxa mensal.
        final double taxaAnual = Math.pow(taxaMensal.divide(new BigDecimal("100.0"), 10, java.math.RoundingMode.HALF_UP).add(new BigDecimal("1.0")).doubleValue(), 365.0 / 30.0) - 1.0;

        // O valor liberado (VP) é igual ao somatório das prestações trazidas à data de contratação.
        // Assim sendo, VP = PMT/A + PMT/B + PMT/C + ... sendo A, B e C igual à 1 mais a taxa de juros proporcional
        // ao período compreendido entre a data de contratação e a data de vencimento da parcela.
        // Portanto PMT = VP / (1/A + 1/B + 1/C + ...)

        // Somatório do inverso das taxas parciais.
        Date dataProximaParcela = recuperarDataVencimento(mesAnoVencimentoPrimeiraParcela, orgCodigo, null, true, responsavel);

        // Garante que a data de vencimento da primeira parcela será maior que a do contrato.
        Date mesAnoVencimentoAposDataContrato = mesAnoVencimentoPrimeiraParcela;
        while (dataProximaParcela.before(dataContrato)) {
            mesAnoVencimentoAposDataContrato = DateHelper.addMonths(mesAnoVencimentoAposDataContrato, 1);
            dataProximaParcela = recuperarDataVencimento(mesAnoVencimentoAposDataContrato, orgCodigo, null, true, responsavel);
        }

        int diasAteProximoDesconto = 0;
        double somatorioInversoTaxas = 0.0;
        for (int i = 0; i < prazo; i++) {
            diasAteProximoDesconto = DateHelper.dayDiff(dataProximaParcela, dataContrato);
            final double taxaProporcionalPrestacao = Math.pow(1.0 + taxaAnual, diasAteProximoDesconto / 365.0);
            somatorioInversoTaxas += 1.0 / taxaProporcionalPrestacao;

            // Quando a taxa anual se aproxima de -1.0 (taxa mensal = -100%), o somatório se torna infinito.
            if (Double.isInfinite(somatorioInversoTaxas)) {
                break;
            }

            dataProximaParcela = recuperarProximaPrestacao(dataProximaParcela, orgCodigo, responsavel);
        }

        return somatorioInversoTaxas;
    }

    /**
     * Recupera a data de vencimento de uma parcela.
     * @param anoMesVencimento : Ano e mês de vencimento da parcela.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param csaCodigo : código do órgão para recuperar dia de repasse específica da CSA
     * @param primeiraParcela : TRUE se é o cálculo da data base da primeira parcela
     * @param responsavel : Responsável pela operação
     * @return Data de vencimento da parcela.
     */
    private static Date recuperarDataVencimento(Date anoMesVencimento, String orgCodigo, String csaCodigo, boolean primeiraParcela, AcessoSistema responsavel) {
        int diaVencimento = 1;
        try {
            diaVencimento = RepasseHelper.getDiaRepasse(orgCodigo, anoMesVencimento, responsavel);
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            try {
                final ParametroDelegate parDelegate = new ParametroDelegate();
                final String csaDiaRepasse = parDelegate.getParamCsa(csaCodigo, CodedValues.TPA_DIA_REPASSE, responsavel);
                if (TextHelper.isNum(csaDiaRepasse)) {
                    diaVencimento = Integer.parseInt(csaDiaRepasse);
                }
            } catch (NumberFormatException | ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        Calendar dataVencimento = Calendar.getInstance();
        dataVencimento.setTime(anoMesVencimento);
        dataVencimento = DateHelper.clearHourTime(dataVencimento);

        if (primeiraParcela) {
            // Adiciona meses ao cálculo de pagamento da primeira parcela
            final Object paramMesesPgtPrimeiraParcela = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_MESES_PARA_PAGTO_PRIMEIRA_PARCELA, responsavel);
            final int mesesPgtPrimeiraParcela = (TextHelper.isNum(paramMesesPgtPrimeiraParcela) ? Integer.parseInt(paramMesesPgtPrimeiraParcela.toString()) : 0);
            dataVencimento.add(Calendar.MONTH, mesesPgtPrimeiraParcela);
        }

        int diaDataVencimento = diaVencimento;
        while (dataVencimento.getActualMaximum(Calendar.DAY_OF_MONTH) < diaDataVencimento) {
            diaDataVencimento--;
        }

        dataVencimento.set(Calendar.DAY_OF_MONTH, diaDataVencimento);

        return dataVencimento.getTime();
    }

    /**
     * Recupera a data de vencimento da próxima prestação a partir da data de vencimento da prestação atual.
     * @param vencimentoPrestacaoAtual : Data de vencimento da prestação atual.
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return
     */
    private static Date recuperarProximaPrestacao(Date vencimentoPrestacaoAtual, String orgCodigo, AcessoSistema responsavel) {
        final Calendar vencimentoAtual = Calendar.getInstance();
        vencimentoAtual.setTime(vencimentoPrestacaoAtual);

        int proximoAno = vencimentoAtual.get(Calendar.YEAR);
        int proximoMes = vencimentoAtual.get(Calendar.MONTH) + 1;

        if (proximoMes > Calendar.DECEMBER) {
            proximoMes -= 12;
            proximoAno += 1;
        }

        Calendar proximaPrestacao = Calendar.getInstance();
        proximaPrestacao.set(Calendar.DAY_OF_MONTH, 1);
        proximaPrestacao.set(Calendar.YEAR, proximoAno);
        proximaPrestacao.set(Calendar.MONTH, proximoMes);
        proximaPrestacao = DateHelper.clearHourTime(proximaPrestacao);

        return recuperarDataVencimento(proximaPrestacao.getTime(), orgCodigo, null, false, responsavel);
    }

    /**
     * Calcula a quantidade de dias de carência até a primeira parcela.
     * @param dataInicial : Data de início da contagem de tempo: adeData ou data corrente.
     * @param mesAnoPrimeiraParcela : Mês/Ano de vencimento da primeira parcela
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param responsavel : Responsável pela operação
     * @return
     */
    public static int calculateDC(Date dataInicial, Date mesAnoPrimeiraParcela, String orgCodigo, AcessoSistema responsavel) {
        return calculateDC(dataInicial, mesAnoPrimeiraParcela, orgCodigo, null, responsavel);
    }

    /**
     * Calcula a quantidade de dias de carência até a primeira parcela.
     * @param dataInicial : Data de início da contagem de tempo: adeData ou data corrente.
     * @param mesAnoPrimeiraParcela : Mês/Ano de vencimento da primeira parcela
     * @param orgCodigo : código do órgão para recuperar dia de repasse
     * @param csaCodigo : código do órgão para recuperar dia de repasse específico da CSA
     * @param responsavel : Responsável pela operação
     * @return
     */
    public static int calculateDC(Date dataInicial, Date mesAnoPrimeiraParcela, String orgCodigo, String csaCodigo, AcessoSistema responsavel) {
        final Date dataVencimento = recuperarDataVencimento(mesAnoPrimeiraParcela, orgCodigo, csaCodigo, true, responsavel);
        // DESENV-15465 : Adiciona 1 mês só ao imprimir a data pois no cálculo do juros, será
        // adicionado 1 mês por padrão, e esta é a data usada no site do Procon/SP para conferência
        LOG.trace("Data Pgt. 1a Parcela (Repasse): " + DateHelper.toDateString(DateHelper.addMonths(dataVencimento, 1)));
        return DateHelper.dayDiff(dataVencimento, dataInicial);
    }

    /**
     * Calcula a data de taxa de juros para o serviço informado
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static CustomTransferObject calcularDataTaxaJuros(String svcCodigo, AcessoSistema responsavel) throws ViewHelperException {
        CustomTransferObject result = null;
        try {
            result = new CustomTransferObject();
            final Calendar cal = Calendar.getInstance();
            final int diaDoMes = cal.get(Calendar.DAY_OF_MONTH);
            final ParametroDelegate paramDelegate = new ParametroDelegate();
            CustomTransferObject cto = paramDelegate.getParamSvcCse(svcCodigo, CodedValues.TPS_DATA_ABERTURA_TAXA, responsavel);
            final String dataAbertura = (cto != null) && (cto.getAttribute(Columns.PSE_VLR) != null) && !"".equals(cto.getAttribute(Columns.PSE_VLR)) ? cto.getAttribute(Columns.PSE_VLR).toString() : "";
            final String dataAberturaRef = (cto != null) && (cto.getAttribute(Columns.PSE_VLR_REF) != null) && !"".equals(cto.getAttribute(Columns.PSE_VLR_REF)) ? cto.getAttribute(Columns.PSE_VLR_REF).toString() : "U";

            if (!"".equals(dataAbertura)) {
                if ("D".equalsIgnoreCase(dataAberturaRef)) {
                    // Se for Dia do Mês seta para essa data onde for maior que hoje
                    // para retornar a data de início da vigência
                    final java.util.Date hoje = cal.getTime();
                    cal.set(Calendar.DATE, Integer.parseInt(dataAbertura));
                    if (hoje.compareTo(cal.getTime()) >= 0) {
                        cal.add(Calendar.MONTH, 1);
                    }

                } else if ("S".equalsIgnoreCase(dataAberturaRef)) {
                    // Se for Dia da Semana recupera a data de início da vigência
                    cal.setTime(DateHelper.getNextDayOfWeek(null, Integer.parseInt(dataAbertura)));

                } else if ("U".equalsIgnoreCase(dataAberturaRef)) {
                    // Se for Dias Úteis adiciona a quantidade de dias ao dia de hoje
                    // para retornar a data de início da vigência
                    cal.add(Calendar.DATE, Integer.parseInt(dataAbertura));
                }
            } else {
                // Se não houver cadastro do ínicio da vigência,
                // seta o dia de amanhã como a data de início da vigência
                cal.add(Calendar.DATE, 1);
            }

            // Obtém parâmetro com a data limite para digitação da taxa
            cto = paramDelegate.getParamSvcCse(svcCodigo, CodedValues.TPS_DATA_LIMITE_DIGIT_TAXA, responsavel);
            final String dataLimite = (cto != null) && (cto.getAttribute(Columns.PSE_VLR) != null) && !"".equals(cto.getAttribute(Columns.PSE_VLR)) ? cto.getAttribute(Columns.PSE_VLR).toString() : "";

            // Se a data limite já passou, então joga o inicio de vigencia para o próximo mês
            if (!"".equals(dataLimite) && (diaDoMes > Integer.parseInt(dataLimite))) {
                cal.add(Calendar.MONTH, 1);
            }

            // Seta Data de Início de Vigência
            result.setAttribute(Columns.CFT_DATA_INI_VIG, DateHelper.format(cal.getTime(), "yyyy-MM-dd"));
            // Seta Data de Final de Vigência
            cal.add(Calendar.DATE, -1);
            result.setAttribute(Columns.CFT_DATA_FIM_VIG, DateHelper.format(cal.getTime(), "yyyy-MM-dd"));

        } catch (final ParametroControllerException ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return result;
    }

    /**
     * Calcula a taxa de juros praticada no contrato.
     * @param ade Dados do contrato
     * @param simulacaoPorTaxaJuros Define se a simulação é por coeficientes ou taxa de juros
     * @param paramSvcCse Parâmetros de serviço
     * @param orgCodigo Órgão do servidor
     * @param responsavel
     * @return
     */
    public static BigDecimal calcularTaxaJuros(CustomTransferObject ade, boolean simulacaoPorTaxaJuros, ParamSvcTO paramSvcCse, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
        final Integer adePrazo = (Integer) ade.getAttribute(Columns.ADE_PRAZO);
        final String adePeriodicidade = (String) ade.getAttribute(Columns.ADE_PERIODICIDADE);
        final BigDecimal adeVlr = (BigDecimal) ade.getAttribute(Columns.ADE_VLR);
        BigDecimal adeVlrLiberado = (ade.getAttribute(Columns.CDE_VLR_LIBERADO) != null ? (BigDecimal) ade.getAttribute(Columns.CDE_VLR_LIBERADO) : new BigDecimal("0.00"));
        final BigDecimal adeVlrTac = (ade.getAttribute(Columns.ADE_VLR_TAC) != null ? (BigDecimal) ade.getAttribute(Columns.ADE_VLR_TAC) : new BigDecimal("0.00"));
        final BigDecimal adeVlrIof = (ade.getAttribute(Columns.ADE_VLR_IOF) != null ? (BigDecimal) ade.getAttribute(Columns.ADE_VLR_IOF) : new BigDecimal("0.00"));
        final Date adeAnoMesIni = (Date) ade.getAttribute(Columns.ADE_ANO_MES_INI);
        final Date adeData = (Date) ade.getAttribute(Columns.ADE_DATA);
        final Object metodoSimulacao = ParamSist.getInstance().getParam(CodedValues.TPC_METODO_CALCULO_SIMULACAO, responsavel);

        if (adeVlrLiberado.signum() == 0) {
            // Se o valor liberado na tabela coeficiente desconto não está cadastrado, verifica o valor presente
            // na tabela aut desconto, pois sistemas que possuem validação de taxa de juros, mas não possuem
            // simulador de empréstimos não cadastram os valores na tabela de coeficiente desconto
            adeVlrLiberado = (ade.getAttribute(Columns.ADE_VLR_LIQUIDO) != null ? (BigDecimal) ade.getAttribute(Columns.ADE_VLR_LIQUIDO) : new BigDecimal("0.00"));
        }

        // Se tem a informação de valor liberado, então calcula os valores financeiros
        if (adeVlrLiberado.signum() > 0) {
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

            BigDecimal taxaJuros = new BigDecimal("0.0");
            // Verifica se os cálculos devem ser baseados em anos de 365 dias.
            if (ParamSist.getBoolParamSist(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, responsavel)) {
                taxaJuros = calcularTaxaJurosAno365Dias(vlrLiberadoTotal, adeVlr, adePrazo, adeData, adeAnoMesIni, orgCodigo, responsavel);
                LOG.debug("calculou usando 365: " + taxaJuros);
            } else {
                // Calcula a diferença entre a data de inclusão do contrato e a data do primeiro desconto, para cálculo da taxa de juros
                final int dias = calculateDC(adeData, adeAnoMesIni, orgCodigo, responsavel);
                //deve deixar negativo./dias = dias < 0 ? 0 : dias;
                final double periodoCarencia = dias / 30.0;
                if (!TextHelper.isNull(metodoSimulacao) && CodedValues.MCS_MEXICANO.equals(metodoSimulacao)) {
                    taxaJuros = new BigDecimal(SimulacaoMetodoMexicano.calcularTaxaJuros(vlrLiberadoTotal, adeVlr, adePrazo, adePeriodicidade, responsavel)).setScale(2, java.math.RoundingMode.HALF_UP);
                } else {
                    final boolean sistemaIndiano = !TextHelper.isNull(metodoSimulacao) && CodedValues.MCS_INDIANO.equals(metodoSimulacao);

                    //DESENV-9830: Sistema indiano não considera período de carência. O mesmo é feito na simulação indiana.
                    taxaJuros = new BigDecimal(calcularTaxaJurosAno360Dias(vlrLiberadoTotal, adeVlr, adePrazo, (!sistemaIndiano) ? 1 + periodoCarencia : 1, responsavel)).setScale(2, java.math.RoundingMode.HALF_UP);
                    //DESENV-9830: na Índia a taxa é anual. O efetivo calculado é mensal. Logo, deve-se multiplicar por 12 para se ter
                    //             a taxa efetiva a ser gravada para a reserva.
                    if (sistemaIndiano) {
                        taxaJuros = taxaJuros.multiply(new BigDecimal(12)).setScale(2, RoundingMode.HALF_UP);
                    }
                }
                LOG.debug("calculou usando 360: " + taxaJuros);
            }

            return taxaJuros;
        }
        return null;
    }

    public static BigDecimal recuperaValorLiberado(CustomTransferObject autdes) {
        final BigDecimal adeVlrLiquido = autdes.getAttribute(Columns.ADE_VLR_LIQUIDO) != null ? new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_LIQUIDO).toString()) : null;
        final BigDecimal cdeVlrLiberado = autdes.getAttribute(Columns.CDE_VLR_LIBERADO) != null ? new BigDecimal(autdes.getAttribute(Columns.CDE_VLR_LIBERADO).toString()) : null;
        return (adeVlrLiquido != null ? adeVlrLiquido : cdeVlrLiberado);
    }

    public static BigDecimal recuperaCetMensal(CustomTransferObject autdes, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final Integer adePrazo = autdes.getAttribute(Columns.ADE_PRAZO) != null ? Integer.valueOf(autdes.getAttribute(Columns.ADE_PRAZO).toString()) : null;
            final BigDecimal vlrLibParaCalculo = recuperaValorLiberado(autdes);
            final BigDecimal adeVlrDecimal = autdes.getAttribute(Columns.ADE_VLR) != null ? new BigDecimal(autdes.getAttribute(Columns.ADE_VLR).toString()) : null;
            BigDecimal vlrCetMensal = null;

            final List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_VLR_LIQ_TAXA_JUROS);

            final ParametroDelegate parDelegate = new ParametroDelegate();
            final ParamSvcTO parSvcCse = parDelegate.selectParamSvcCse(autdes.getAttribute(Columns.SVC_CODIGO).toString(), tpsCodigos, responsavel);
            final boolean permiteVlrLiqTxJuros = parSvcCse.isTpsVlrLiqTaxaJuros();

            if ((vlrLibParaCalculo != null) && (adePrazo != null)) {
                vlrCetMensal = calcularTaxaJuros(vlrLibParaCalculo, adeVlrDecimal, adePrazo, (java.util.Date) autdes.getAttribute(Columns.ADE_DATA), (java.util.Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI), (String) autdes.getAttribute(Columns.ORG_CODIGO), responsavel);
                final BigDecimal vlrTaxaJuros = new BigDecimal((permiteVlrLiqTxJuros ? autdes.getAttribute(Columns.ADE_TAXA_JUROS) : autdes.getAttribute(Columns.CFT_VLR)).toString());
                // Se o CET mensal calculado é maior que zero significa que os valores
                // de prazo, prestação e vlr liberado estão de acordo.
                if ((vlrCetMensal != null) && (vlrCetMensal.signum() > 0)) {
                    // Verifica se o CET é maior que a taxa de juros (é esperado que seja), e caso não seja
                    // exibe o valor da própria Taxa de Juros como sendo CET mensal.
                    vlrCetMensal = ((vlrTaxaJuros != null) && (vlrTaxaJuros.compareTo(vlrCetMensal) > 0)) ? vlrTaxaJuros : vlrCetMensal;
                }
            }

            return vlrCetMensal;

        } catch (final ParametroControllerException ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
