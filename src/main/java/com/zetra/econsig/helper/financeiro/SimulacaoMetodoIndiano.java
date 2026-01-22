package com.zetra.econsig.helper.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;


/**
 * <p>Title: SimulacaoMetodoIndiano</p>
 * <p>Description: Classe auxiliar com metodologia indiana dos cálculos financeiros para simulação.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class SimulacaoMetodoIndiano implements SimuladorCustomizado {

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private ParametroController parametroController;

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
            throw new SimulacaoControllerException("mensagem.erro.simulacao.parametrizacao.taxa", responsavel);
        }

        final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
        if (!simulacaoPorTaxaJuros) {
            throw new SimulacaoControllerException("mensagem.erro.simulacao.parametrizacao.taxa", responsavel);
        }

        // México usa ano de 360 dias
        final boolean usaAno365 = ParamSist.getBoolParamSist(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, responsavel);
        if (usaAno365) {
            throw new SimulacaoControllerException("mensagem.erro.simulacao.parametrizacao.dias.taxa.juros", responsavel);
        }

        svcCodigo = simulacaoController.getSvcTaxaCompartilhada(svcCodigo, false, responsavel);

        // Pega os coeficientes/taxas/CETs para a simulação
        final List<TransferObject> coeficientes = simulacaoController.getCoeficienteSimulacao(csaCodigo, svcCodigo, orgCodigo, rseCodigo, vlrParcela, vlrLiberado, numParcelas, validaBloqSerCnvCsa, utilizaLimiteTaxa, responsavel);

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

        // Determina a data inicial do contrato utilizada para cálculo de correção de valor presente
        final Map<String, Object> prazoIniDC = new HashMap<>();

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

            final String svcCodigoCsa = (!TextHelper.isNull(coeficiente.getAttribute(Columns.SVC_CODIGO))) ? coeficiente.getAttribute(Columns.SVC_CODIGO).toString() : svcCodigo;

            final String keyCseDC = "cse_" + svcCodigoCsa + "_dc";
            final String keyCsePrazoIni = "cse_" + svcCodigoCsa + "_prazoIni";
            final String keyCseCarencia = "cse_" + svcCodigoCsa + "_carencia";
            final String keyCseCarenciaMaxima = "cse_" + svcCodigoCsa + "_carenciaMaxima";

            if (prazoIniDC.get(keyCseDC) == null) {
                // Se tem carência no serviço, ajusta o prazoIni
                try {
                    CustomTransferObject cto = parametroController.getParamSvcCse(svcCodigoCsa, CodedValues.TPS_CARENCIA_MINIMA, responsavel);
                    if ((cto != null) && !TextHelper.isNull(cto.getAttribute(Columns.PSE_VLR))) {
                        prazoIniDC.put(keyCseCarencia, Integer.parseInt(cto.getAttribute(Columns.PSE_VLR).toString()));
                    } else {
                        prazoIniDC.put(keyCseCarencia, Integer.valueOf(0));
                    }
                    cto = parametroController.getParamSvcCse(svcCodigoCsa, CodedValues.TPS_CARENCIA_MAXIMA, responsavel);
                    if ((cto != null) && !TextHelper.isNull(cto.getAttribute(Columns.PSE_VLR))) {
                        prazoIniDC.put(keyCseCarenciaMaxima, Integer.parseInt(cto.getAttribute(Columns.PSE_VLR).toString()));
                    } else {
                        prazoIniDC.put(keyCseCarenciaMaxima, Integer.MAX_VALUE);
                    }

                    Date prazoIniGeral = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, (Integer) prazoIniDC.get(keyCseCarencia), null, responsavel);

                    // Se a reserva possui carência maior do que o mínima do CSE, ajusta o prazoIni.
                    if ((adeAnoMesIni != null) && (adeAnoMesIni.compareTo(prazoIniGeral) > 0)) {
                        prazoIniGeral = adeAnoMesIni;
                    }

                    prazoIniDC.put(keyCseDC, SimulacaoHelper.calculateDC(DateHelper.getSystemDatetime(), prazoIniGeral, orgCodigo, responsavel));
                    prazoIniDC.put(keyCsePrazoIni, prazoIniGeral);
                } catch (final NumberFormatException ex) {
                    throw new SimulacaoControllerException("mensagem.erro.carencia.configurada.formato.incorreto", responsavel);
                } catch (final ParametroControllerException | PeriodoException ex) {
                    throw new SimulacaoControllerException(ex);
                }
            }

            final int carencia = (Integer) prazoIniDC.get(keyCseCarencia);
            final int carenciaMaxima = (Integer) prazoIniDC.get(keyCseCarenciaMaxima);
            int dc = (Integer) prazoIniDC.get(keyCseDC);
            Date prazoIni = (Date) prazoIniDC.get(keyCsePrazoIni);
            try {
                final String keyCsaDC = "csa_" + csaCodigoCft + "_" + svcCodigoCsa + "_dc";
                final String keyCsaPrazoIni = "csa_" + csaCodigoCft + "_" + svcCodigoCsa + "_prazoIni";
                if (prazoIniDC.get(keyCsaDC) != null) {
                    dc = (Integer) prazoIniDC.get(keyCsaDC);
                    prazoIni = (Date) prazoIniDC.get(keyCsaPrazoIni);
                } else {
                    // Pega a carência mínima da CSA e do serviço em questão
                    final List<String> tpsCodigo = new ArrayList<>();
                    tpsCodigo.add(CodedValues.TPS_CARENCIA_MINIMA);
                    final List<TransferObject> params = parametroController.selectParamSvcCsa(svcCodigoCsa, csaCodigoCft, tpsCodigo, false, responsavel);
                    if ((params != null) && (params.size() == 1)) {
                        final TransferObject param = params.get(0);
                        if ((param != null) && !TextHelper.isNull(param.getAttribute(Columns.PSC_VLR))) {
                            final int carenciaCsa = Integer.parseInt(param.getAttribute(Columns.PSC_VLR).toString());
                            if ((carenciaCsa > carencia) && (carenciaCsa <= carenciaMaxima)) {
                                prazoIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, carenciaCsa, null, responsavel);
                                dc = SimulacaoHelper.calculateDC(DateHelper.getSystemDatetime(), prazoIni, orgCodigo, responsavel);
                                titulo += "*";
                            }
                        }
                        prazoIniDC.put(keyCsaDC, dc);
                        prazoIniDC.put(keyCsaPrazoIni, prazoIni);
                    }
                 }
            } catch (final NumberFormatException ex) {
                throw new SimulacaoControllerException("mensagem.erro.carencia.configurada.formato.incorreto", responsavel);
            } catch (final ParametroControllerException | PeriodoException ex) {
                throw new SimulacaoControllerException(ex);
            }


            /***********************************
             * Simulação pelo valor da parcela *
             ***********************************/
            if (simulaPelaParcela) {
                // Faz o cálculo somente se tem coeficiente corretamente cadastrado
                if (cftVlr.signum() <= 0) {
                    vlrLiberado = new BigDecimal("0");
                } else {
                    //taxas indianas são cadastros são anuais. Então, faz-se a divisão por 12 para se ter a taxa mensal
                    final BigDecimal cftVlrMensal = cftVlr.divide(new BigDecimal("12"), 6, java.math.RoundingMode.HALF_UP);

                    try {
                        final BigDecimal[] retorno = calcularValorLiberado(vlrParcela, cftVlrMensal, (numParcelas > 0) ? numParcelas : (Short) coeficiente.getAttribute(Columns.PRZ_VLR), prazoIni, adePeriodicidade, responsavel);
                        vlrLiberado = retorno[0];
                        /*BigDecimal totalPagar = retorno[1]; // valor toral a pagar (liberado mais juros)

                        // Seta os valores de retorno
                        coeficiente.setAttribute("TOTAL_PAGAR", totalPagar);*/
                    } catch (final ViewHelperException ex) {
                        vlrLiberado = new BigDecimal("0");
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
                //taxas indianas são cadastros anuais. Então, faz-se a divisão por 12 para se ter a taxa mensal
                final BigDecimal cftVlrMensal = cftVlr.divide(new BigDecimal("12"), 6, java.math.RoundingMode.HALF_UP);

                try {
                    final BigDecimal[] retorno = calcularValorParcela(vlrLiberado, cftVlrMensal, (numParcelas > 0) ? numParcelas : (Short) coeficiente.getAttribute(Columns.PRZ_VLR), prazoIni, orgCodigo, adePeriodicidade, responsavel);
                    vlrParcela = retorno[0];
                } catch (final ViewHelperException ex) {
                    throw new SimulacaoControllerException("mensagem.erro.interno.argumento", responsavel, ex.getMessage());
                }
            }

            // Limita a 2 casas decimais tanto o valor liberado quanto o valor da parcela
            if (vlrParcela.doubleValue() != Double.MAX_VALUE) {
                vlrParcela = vlrParcela.setScale(2, java.math.RoundingMode.HALF_UP);
            }
            if (vlrLiberado.doubleValue() != 0) {
                vlrLiberado = vlrLiberado.setScale(2, java.math.RoundingMode.HALF_UP);
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

        if (numParcelas > 0) {
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

    public BigDecimal[] calcularValorLiberado(BigDecimal vlrParcela, BigDecimal cftVlr, int numParcelas, Date prazoIni, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException, ViewHelperException {
        final BigDecimal vlrLiberado = SimulacaoHelper.calcularValorLiberado(vlrParcela, numParcelas, DateHelper.getSystemDatetime(), prazoIni, cftVlr, null, true, responsavel);
        //adotando o rounding para transações do banco central indiano
        return new BigDecimal[]{vlrLiberado.setScale(0, java.math.RoundingMode.HALF_UP)};
    }

    public BigDecimal[] calcularValorParcela(BigDecimal vlrLiberado, BigDecimal cftVlr, int numParcelas, Date prazoIni, String orgCodigo, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException, ViewHelperException {
        final BigDecimal vlrParcela = SimulacaoHelper.calcularValorPrestacao(vlrLiberado, numParcelas, DateHelper.getSystemDatetime(), prazoIni, cftVlr, cftVlr, orgCodigo, adePeriodicidade, responsavel);
        //adotando o rounding para transações do banco central indiano
        return new BigDecimal[]{vlrParcela.setScale(0, java.math.RoundingMode.HALF_UP)};
    }
}
