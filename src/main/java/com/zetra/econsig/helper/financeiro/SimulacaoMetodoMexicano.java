package com.zetra.econsig.helper.financeiro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: SimulacaoMetodoMexicano</p>
 * <p>Description: Classe auxiliar com metodologia mexicana dos cálculos financeiros para simulação.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: geovani.morais $
 * $Revision: 19169 $
 * $Date: 2015-05-07 12:44:12 -0300 (Qui, 07 Mai 2015) $
 */
@Component
public class SimulacaoMetodoMexicano implements SimuladorCustomizado {
    private static final BigDecimal UM = BigDecimal.ONE;
    private static final BigDecimal CEM = BigDecimal.valueOf(100.00);

    @Autowired
    private SimulacaoController simulacaoController;

    /**
     * Realiza a simulação de uma consignação, de acordo com as taxas cadastradas no sistema,
     * seja pelo valor da parcela ou pelo valor liberado.
     * @param csaCodigo    : Código da consignatária, caso seja para simular apenas para prazos cadastrados para esta
     * @param svcCodigo    : Código do serviço da simulação
     * @param orgCodigo    : Código do órgão do servidor que está simulando
     * @param rseCodigo    : Código do registro servidor que está simulando
     * @param vlrParcela   : Valor da parcela, caso a simulação seja pelo valor da parcela
     * @param vlrLiberado  : Valor liberado, caso a simulação seja pelo valor liberado
     * @param przVlr       : Número de parcelas
     * @param adeAnoMesIni : Data inicial do contrato
     * @param validaBloqSerCnvCsa : TRUE se o bloqueio de consignatária impede que seja exibida no resultado
     * @param utilizaLimiteTaxa   : TRUE se utiliza a taxa limite do serviço para a simulação, ao invés da cadastrada pela CSA
     * @param adePeriodicidade    : Periodicidade da ade
     * @param responsavel  : Responsável pela operação
     * @return
     * @throws SimulacaoControllerException
     */
    @Override
    public List<TransferObject> simularConsignacao(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short numParcelas, Date adeAnoMesIni, boolean validaBloqSerCnvCsa, boolean utilizaLimiteTaxa, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException {
        // Verifica se o sistema está configurado para trabalhar com o CET pois no México o sistema irá trabalhar com taxa de juros
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        if (temCET) {
            throw new SimulacaoControllerException("mensagem.erro.simulacao.parametrizacao.taxa.iva", responsavel);
        }

        final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
        if (!simulacaoPorTaxaJuros) {
            throw new SimulacaoControllerException("mensagem.erro.simulacao.parametrizacao.taxa.iva", responsavel);
        }

        // México usa ano de 360 dias
        final boolean usaAno365 = ParamSist.getBoolParamSist(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, responsavel);
        if (usaAno365) {
            throw new SimulacaoControllerException("mensagem.erro.simulacao.parametrizacao.dias.taxa.juros", responsavel);
        }

        svcCodigo = simulacaoController.getSvcTaxaCompartilhada(svcCodigo, false, responsavel);
        // Periodicidade padrão se não informado é quinzenal
        final boolean mensal = PeriodoHelper.isMensal(adePeriodicidade, responsavel);
        final Short prazo = mensal ? numParcelas : PeriodoHelper.reverterPrazoPeriodicidadeParaMensal(numParcelas, responsavel);

        // Pega os coeficientes/taxas/CETs para a simulação
        final List<TransferObject> coeficientes = simulacaoController.getCoeficienteSimulacao(csaCodigo, svcCodigo, orgCodigo, rseCodigo, vlrParcela, vlrLiberado, prazo, validaBloqSerCnvCsa, utilizaLimiteTaxa, responsavel);

        String csaNome = null, titulo = null;
        BigDecimal cftVlr;
        TransferObject coeficiente = null;
        final boolean simulaPelaParcela = (vlrParcela != null);

        // Se o simulador é agrupado pela natureza de serviço EMPRESTIMO então
        // monta um mapa para contar quantas vezes uma mesma consignatária aparece
        // no ranking para determinar se será exibida a descrição do serviço
        final boolean simuladorAgrupadoPorNaturezaServico = ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())
                && (rseCodigo != null)
                && (csaCodigo == null);

        final Map<String, Integer> qtdServicoConsignataria = new HashMap<>();
        if (simuladorAgrupadoPorNaturezaServico) {
            final Iterator<TransferObject> it = coeficientes.iterator();
            while (it.hasNext()) {
                coeficiente = it.next();
                final String csaCodigoCft = (!TextHelper.isNull(csaCodigo)) ? csaCodigo : coeficiente.getAttribute(Columns.CSA_CODIGO).toString();
                Integer total = qtdServicoConsignataria.get(csaCodigoCft);
                if (total == null) {
                    total = 1;
                } else {
                    total = total + 1;
                }
                qtdServicoConsignataria.put(csaCodigoCft, total);
            }
        }

        // Realiza a simulação
        final Iterator<TransferObject> it = coeficientes.iterator();
        while (it.hasNext()) {
            coeficiente = it.next();
            final String csaCodigoCft = (!TextHelper.isNull(csaCodigo)) ? csaCodigo : coeficiente.getAttribute(Columns.CSA_CODIGO).toString();
            cftVlr = new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR).toString());
            csaNome = (String) coeficiente.getAttribute(Columns.CSA_NOME_ABREV);
            if ((csaNome == null) || csaNome.isBlank()) {
                csaNome = (String) coeficiente.getAttribute(Columns.CSA_NOME);
            }

            titulo = csaNome;
            if (!TextHelper.isNull(titulo) && (qtdServicoConsignataria.get(csaCodigoCft) != null) && (qtdServicoConsignataria.get(csaCodigoCft).intValue() > 1)) {
                titulo += " - " + coeficiente.getAttribute(Columns.SVC_DESCRICAO);
            }

            if ((numParcelas == 0) && !mensal) {
                // Se é simulação de renegociação (numParcelas == 0) em periodicidade quinzenal, então
                // os prazos cadastrados nos coeficientes serão multiplicados por 2 visto que são taxas mensais
                final short przVlr = ((Short) coeficiente.getAttribute(Columns.PRZ_VLR));
                coeficiente.setAttribute(Columns.PRZ_VLR, PeriodoHelper.converterPrazoMensalEmPeriodicidade(przVlr, responsavel));
            }

            /***********************************
             * Simulação pelo valor da parcela *
             ***********************************/
            if (simulaPelaParcela) {
                // Faz o cálculo somente se tem coeficiente corretamente cadastrado
                if (cftVlr.signum() <= 0) {
                    vlrLiberado = BigDecimal.ZERO;
                } else {
                    try {
                        final BigDecimal[] retorno = calcularValorLiberado(vlrParcela, cftVlr, (numParcelas > 0) ? numParcelas : (Short) coeficiente.getAttribute(Columns.PRZ_VLR), adePeriodicidade, responsavel);
                        vlrLiberado = retorno[0];
                        final BigDecimal cat = retorno[1]; // cat
                        final BigDecimal iva = retorno[2]; // iva
                        final BigDecimal totalPagar = retorno[3]; // valor toral a pagar (liberado mais juros + iva)

                        // Seta os valores de retorno
                        coeficiente.setAttribute("CAT", cat);
                        coeficiente.setAttribute("IVA", iva);
                        coeficiente.setAttribute("TOTAL_PAGAR", totalPagar);
                    } catch (final SimulacaoControllerException ex) {
                        vlrLiberado = BigDecimal.ZERO;
                        throw new SimulacaoControllerException("mensagem.erro.interno.argumento", responsavel, ex.getMessage());
                    }
                }

                /*********************************
                 * Simulação pelo valor liberado *
                 *********************************/
            } else // Faz o cálculo somente se tem coeficiente corretamente cadastrado
            if (cftVlr.signum() <= 0) {
                vlrParcela = new BigDecimal(Double.MAX_VALUE);

            } else {
                try {
                    final BigDecimal[] retorno = calcularValorParcela(vlrLiberado, cftVlr, (numParcelas > 0) ? numParcelas : (Short) coeficiente.getAttribute(Columns.PRZ_VLR), adePeriodicidade, responsavel);
                    vlrParcela = retorno[0];
                    final BigDecimal cat = retorno[1]; // cat
                    final BigDecimal iva = retorno[2]; // iva
                    final BigDecimal totalPagar = retorno[3]; // valor toral a pagar (liberado mais juros + iva)

                    coeficiente.setAttribute("CAT", cat);
                    coeficiente.setAttribute("IVA", iva);
                    coeficiente.setAttribute("TOTAL_PAGAR", totalPagar);
                } catch (final SimulacaoControllerException ex) {
                    throw new SimulacaoControllerException("mensagem.erro.interno.argumento", responsavel, ex.getMessage());
                }
            }

            // Limita a 2 casas decimais tanto o valor liberado quanto o valor da parcela
            if (vlrParcela.doubleValue() != Double.MAX_VALUE) {
                vlrParcela = vlrParcela.setScale(2, RoundingMode.HALF_UP);
            }
            if (vlrLiberado.doubleValue() != 0) {
                vlrLiberado = vlrLiberado.setScale(2, RoundingMode.HALF_UP);
            }

            coeficiente.setAttribute("VLR_PARCELA", vlrParcela);
            coeficiente.setAttribute("VLR_LIBERADO", vlrLiberado);

            // Define parâmetros para a ordenação
            if (!TextHelper.isNull(csaNome)) {
                coeficiente.setAttribute("TITULO", titulo);
                coeficiente.setAttribute("CONSIGNATARIA", csaNome.toUpperCase());
            }
            coeficiente.setAttribute("VLR_ORDEM", (simulaPelaParcela ? vlrLiberado : vlrParcela));
        }

        if (prazo > 0) {
            // Ordena as consignatárias
            if (simulaPelaParcela) {
                // Se a simulação é pela parcela, então a ordenação é feita
                // de forma decrescente pelo valor liberado
                Collections.sort(coeficientes, (o1, o2) -> {
                    int result = ((BigDecimal) o2.getAttribute("VLR_ORDEM")).compareTo((BigDecimal) o1.getAttribute("VLR_ORDEM"));
                    if (result == 0) {
                        result = o1.getAttribute("TITULO").toString().compareTo(o2.getAttribute("TITULO").toString());
                    }
                    return result;
                });

            } else {
                // Se a simulação é pelo valor liberado, então a ordenação é feita
                // de forma crescente pela parcela
                Collections.sort(coeficientes, (o1, o2) -> {
                    int result = ((BigDecimal) o1.getAttribute("VLR_ORDEM")).compareTo((BigDecimal) o2.getAttribute("VLR_ORDEM"));
                    if (result == 0) {
                        result = o1.getAttribute("TITULO").toString().compareTo(o2.getAttribute("TITULO").toString());
                    }
                    return result;
                });
            }
        }

        simulacaoController.setaRankingSimulacao(coeficientes, CodedValues.FUN_SIM_CONSIGNACAO, responsavel);

        return coeficientes;
    }

    /**
     * Executa calculo do valor da parcela e CAT de acordo com os parâmetros de entrada.
     * @param vlrLiberado
     * @param cftVlr
     * @param przVlr
     * @param diaPgto
     * @param adePeriodicidade
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static BigDecimal[] calcularValorParcela(BigDecimal vlrLiberado, BigDecimal cftVlr, int numParcelas, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException {
        final boolean mensal = PeriodoHelper.isMensal(adePeriodicidade, responsavel);
        final BigDecimal aliquotaIva = new BigDecimal((String)ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ANUAL_IOF, responsavel));
        BigDecimal calculoMensal, juros, iva, totalPagar, vlrParcela;

        final boolean usaTaxaSaldoInsoluto = ParamSist.getBoolParamSist(CodedValues.TPC_USA_TAXA_JUROS_SALDO_INSOLUTO, responsavel);
        if (!usaTaxaSaldoInsoluto) {
            final BigDecimal prazoEmMeses = mensal ? new BigDecimal(numParcelas) : new BigDecimal(PeriodoHelper.reverterPrazoPeriodicidadeParaMensal(numParcelas, responsavel));
            calculoMensal = vlrLiberado.multiply(cftVlr.divide(CEM)).setScale(2, RoundingMode.HALF_UP);
            juros = calculoMensal.multiply(prazoEmMeses).setScale(2, RoundingMode.HALF_UP);
            iva = juros.multiply(aliquotaIva.divide(CEM)).setScale(2, RoundingMode.HALF_UP);
            totalPagar = vlrLiberado.add(iva).add(juros).setScale(2, RoundingMode.HALF_UP);
            vlrParcela = totalPagar.divide(new BigDecimal(numParcelas), 2, RoundingMode.HALF_UP);
        } else {
            try {
                BigDecimal tasaConIva = cftVlr.multiply(UM.add(aliquotaIva.divide(CEM))).setScale(4, RoundingMode.HALF_UP);
                if (!mensal) {
                    final int qtdPeriodos = PeriodoHelper.getQuantidadePeriodosFolha(responsavel);
                    tasaConIva = tasaConIva.divide(new BigDecimal(qtdPeriodos / 12.0f), 10, RoundingMode.HALF_UP);
                }
                tasaConIva = tasaConIva.divide(CEM);

                // D = 1 - (1 / (1 + i))^n
                BigDecimal denominador = UM.divide(UM.add(tasaConIva), 10, RoundingMode.HALF_UP);
                denominador = UM.subtract(new BigDecimal(Math.pow(denominador.doubleValue(), numParcelas)));

                // PMT = (VF * i) / D
                vlrParcela = vlrLiberado.multiply(tasaConIva).divide(denominador, 2, RoundingMode.HALF_UP);

                totalPagar = vlrParcela.multiply(new BigDecimal(numParcelas)).setScale(2, RoundingMode.HALF_UP);
                juros = totalPagar.subtract(vlrLiberado).divide(UM.add(aliquotaIva.divide(CEM)), 2, RoundingMode.HALF_UP);
                iva = totalPagar.subtract(vlrLiberado).subtract(juros);

            } catch (NumberFormatException | ArithmeticException ex) {
                throw new SimulacaoControllerException("mensagem.erro.valor.taxa.cadastrada", responsavel);
            }
        }
        final BigDecimal cat = calculaCAT(vlrLiberado, vlrParcela, numParcelas, mensal, responsavel).setScale(2, RoundingMode.HALF_UP);
        return new BigDecimal[]{vlrParcela, cat, iva, totalPagar};
     }

    /**
     * Executa calculo do valor liberado e CAT de acordo com os parâmetros de entrada.
     * @param vlrParcela
     * @param cftVlr
     * @param przVlr
     * @param adePeriodicidade
     * @param responsavel
     * @return
     * @throws SimulacaoControllerException
     */
    public BigDecimal[] calcularValorLiberado(BigDecimal vlrParcela, BigDecimal cftVlr, int numParcelas, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException {
        final boolean mensal = PeriodoHelper.isMensal(adePeriodicidade, responsavel);
        final BigDecimal aliquotaIva = new BigDecimal((String)ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ANUAL_IOF, responsavel));
        BigDecimal iva, totalPagar, vlrLiberado;

        final int prazoEmMeses = mensal ? numParcelas : PeriodoHelper.reverterPrazoPeriodicidadeParaMensal(numParcelas, responsavel);
        final boolean usaTaxaSaldoInsoluto = ParamSist.getBoolParamSist(CodedValues.TPC_USA_TAXA_JUROS_SALDO_INSOLUTO, responsavel);
        if (!usaTaxaSaldoInsoluto) {
            totalPagar = vlrParcela.multiply(new BigDecimal(numParcelas)).setScale(2, RoundingMode.HALF_UP);
            vlrLiberado = totalPagar.divide(UM.add(cftVlr.divide(CEM)
                                    .multiply(new BigDecimal(prazoEmMeses))
                                    .multiply(UM.add(aliquotaIva.divide(CEM)))), 2, RoundingMode.HALF_UP);
            iva = aliquotaIva.divide(CEM)
                             .multiply(vlrLiberado)
                             .multiply(cftVlr.divide(CEM).multiply(new BigDecimal(prazoEmMeses)))
                             .setScale(2, RoundingMode.HALF_UP);
        } else {
            try {
                BigDecimal tasaConIva = cftVlr.multiply(UM.add(aliquotaIva.divide(CEM))).setScale(2, RoundingMode.HALF_UP);
                tasaConIva = tasaConIva.divide(CEM);

                // D = 1 - (1 / (1 + i))^n
                BigDecimal denominador = UM.divide(UM.add(tasaConIva), 10, RoundingMode.HALF_UP);
                denominador = UM.subtract(new BigDecimal(Math.pow(denominador.doubleValue(), prazoEmMeses)));
                if (!mensal) {
                    final int qtdPeriodos = PeriodoHelper.getQuantidadePeriodosFolha(responsavel);
                    denominador = denominador.multiply(new BigDecimal(qtdPeriodos / 12.0f));
                }

                // VP = (PMT * D) / i
                vlrLiberado = vlrParcela.multiply(denominador).divide(tasaConIva, 2, RoundingMode.HALF_UP);

                totalPagar = vlrParcela.multiply(new BigDecimal(numParcelas)).setScale(2, RoundingMode.HALF_UP);
                final BigDecimal juros = totalPagar.subtract(vlrLiberado).divide(UM.add(aliquotaIva.divide(CEM)), 2, RoundingMode.HALF_UP);
                iva = totalPagar.subtract(vlrLiberado).subtract(juros);

            } catch (NumberFormatException | ArithmeticException ex) {
                throw new SimulacaoControllerException("mensagem.erro.valor.taxa.cadastrada", responsavel);
            }
        }
        final BigDecimal cat = calculaCAT(vlrLiberado, vlrParcela, numParcelas, mensal, responsavel).setScale(2, RoundingMode.HALF_UP);
        return new BigDecimal[]{vlrLiberado, cat, iva, totalPagar};
    }

    private static BigDecimal calculaCAT(BigDecimal vlrLiberado, BigDecimal vlrParcela, int numParcelas, boolean mensal, AcessoSistema responsavel) throws SimulacaoControllerException {
        final Double tir = IRR(vlrLiberado, vlrParcela, numParcelas);
        BigDecimal cat = BigDecimal.ZERO;
        if ((tir != null) && !tir.equals(Double.NaN)) {
            final int qtdPeriodos = mensal ? 12 : PeriodoHelper.converterPrazoMensalEmPeriodicidade(12, responsavel);
            cat = new BigDecimal((Math.pow((1+(tir/100)), qtdPeriodos)-1) * 100).setScale(2, RoundingMode.HALF_UP);
        }
        return cat;
    }

    /**
     * Calculo do TIR usando array de fluxos
     * @param vlrLiberado
     * @param vlrParcela
     * @param vlrPrz
     * @return
     */
    public static Double IRR(BigDecimal vlrLiberado, BigDecimal vlrParcela, Integer vlrPrz) {
       // Método numérico
       final double MINDIF=.0000001;
       double guess = 1; // Em %

       if (vlrLiberado.compareTo(BigDecimal.ZERO) < 0) {
           return Double.NaN;
       }

       double irr = (vlrParcela.doubleValue() * vlrPrz) - vlrLiberado.doubleValue();

       if (irr < 0) {
           irr = -guess;
       } else {
           irr = guess;
       }

       boolean was_hi=false;
       double a3;

       // Iteração para aproximar o valor
       // IRR é definido como f0/(1+tir)^0 + f1/(1+tir)^1 + ... + fn/(1+tir)^n = zero
       // f0 é o valor liberado (normalmente sinal negativo para o ponto de vista do banco)
       // f1...fn são as parcelas (normalmente sinal positivo ponto de vista do banco)
       for (int iter=0;iter<=50;iter++) {
           a3 = -vlrLiberado.doubleValue(); // f0
           int n=1;
           // Somatório das parcelas aplicando a taxa a ser verificada
           for (int i=1;i<=vlrPrz;i++) {
               a3 += vlrParcela.doubleValue()/Math.pow(1.0+(irr/100),n); // fn
               n++;
           }

           if (Math.abs(a3)<.01) {
               return irr;
           }

           if (a3>0) {
               if (was_hi) {
                   guess/=2;
               }
               irr+=guess;
               if (was_hi) {
                   guess-=MINDIF;
                   was_hi=false;
               }
          } else {
              guess/=2;
              irr-=guess;
              was_hi=true;
          }

          if (guess<=MINDIF) {
              return irr;
          }
       }
       return Double.NaN;
    }


    /**
     * Função para calcular a taxa de juros a partir do valor presente,
     * valor da parcela e o prazo do contrato.
     * ((vlrLiberado*tx)*adePrz/2) + ((vlrLiberado*tx)*adePrz/2*IVA) + (vlrLiberado) = adePrz * adeVlr
     * Fonte: http://www.fw.uri.br/~arpasi/financeira/index.php
     * @param valorPresente
     * @param valorParcela
     * @param prazo
     * @param carencia
     * @return
     * @throws ViewHelperException
     */
    public static double calcularTaxaJuros(BigDecimal vlrLiberado, BigDecimal adeVlr, int prazo, String adePeriodicidade, AcessoSistema responsavel) throws ViewHelperException {
        if ((vlrLiberado == null) || (adeVlr == null)) {
            throw new ViewHelperException("mensagem.informe.valor.total.liberado.valor.parcela", responsavel);
        }

        if (prazo == 0) {
            return 0;
        }

        final boolean mensal = PeriodoHelper.isMensal(adePeriodicidade, responsavel);
        final BigDecimal prazoEmMeses = mensal ? new BigDecimal(prazo) : new BigDecimal(PeriodoHelper.reverterPrazoPeriodicidadeParaMensal(prazo, responsavel));
        final BigDecimal aliquotaIva = new BigDecimal((String)ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ANUAL_IOF, responsavel));
        final BigDecimal total = adeVlr.multiply(new BigDecimal(prazo)).setScale(4, RoundingMode.HALF_UP); // vlrLiberado + juros + IVA*juros
        final BigDecimal dividendoTaxa = total.divide(vlrLiberado,4, RoundingMode.HALF_UP).subtract(UM).setScale(4, RoundingMode.HALF_UP);
        final BigDecimal divisorTaxa = prazoEmMeses.multiply(UM.add(aliquotaIva.divide(CEM))).setScale(4, RoundingMode.HALF_UP);
        final BigDecimal taxa = dividendoTaxa.multiply(CEM).divide(divisorTaxa,4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);

        return taxa.doubleValue();
    }
}
