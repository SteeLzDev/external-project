package com.zetra.econsig.helper.consignacao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AutorizacaoHelper</p>
 * <p>Description: Helper Class para operações com autorizações de desconto</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AutorizacaoHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizacaoHelper.class);

    /**
     * Valida o adeVlr de acordo com o valor mínimo e máximo cadastrados
     * nos parâmetros de serviço e de sistema
     * @param adeVlr
     * @param svcCodigo
     * @param csaCodigo
     * @param responsavel
     * @throws ViewHelperException
     */
    public static void validarValorAutorizacao(BigDecimal adeVlr, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (adeVlr == null || adeVlr.signum() != 1) {
                ParametroDelegate parDelegate = new ParametroDelegate();
                if (!parDelegate.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel)) {
                    throw new ViewHelperException("mensagem.valorParcelaMenorIgualZero", responsavel);
                }
            }

            // Recupera valores padrão minimo e maximo para os contratos.
            BigDecimal paramCseMin = null;
            BigDecimal paramCseMax = null;

            Object vlrParamSist = null;

            // Valor mínimo CSE
            vlrParamSist = ParamSist.getInstance().getParam(CodedValues.TPC_VLR_PADRAO_MINIMO_CONTRATO, responsavel);
            if (vlrParamSist != null && !vlrParamSist.equals("")) {
                paramCseMin = new BigDecimal(vlrParamSist.toString().replace(',','.'));
            }

            // Valor máximo CSE
            vlrParamSist = ParamSist.getInstance().getParam(CodedValues.TPC_VLR_PADRAO_MAXIMO_CONTRATO, responsavel);
            if (vlrParamSist != null && !vlrParamSist.equals("")) {
                paramCseMax = new BigDecimal(vlrParamSist.toString());
            }

            // Busca os parâmetros de serviço
            List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_TIPO_VLR);
            tpsCodigos.add(CodedValues.TPS_VLR_MINIMO_CONTRATO);
            tpsCodigos.add(CodedValues.TPS_VLR_MAXIMO_CONTRATO);

            ParametroDelegate parDelegate = new ParametroDelegate();
            ParamSvcTO paramSvc = parDelegate.selectParamSvcCse(svcCodigo, tpsCodigos, responsavel);

            BigDecimal vlrMin = null;
            BigDecimal vlrMax = null;

            final List<TransferObject> paramSvcCsa = parDelegate.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            for (final TransferObject vo : paramSvcCsa) {
                try {
                    if (CodedValues.TPS_VLR_MINIMO_CONTRATO.equals(vo.getAttribute(Columns.TPS_CODIGO)) && !TextHelper.isNull(vo.getAttribute(Columns.PSC_VLR))) {
                        vlrMin = new BigDecimal(NumberHelper.reformat(vo.getAttribute(Columns.PSC_VLR).toString(), NumberHelper.getLang(), "en"));
                    }

                    if (CodedValues.TPS_VLR_MAXIMO_CONTRATO.equals(vo.getAttribute(Columns.TPS_CODIGO)) && !TextHelper.isNull(vo.getAttribute(Columns.PSC_VLR))) {
                        vlrMax = new BigDecimal(NumberHelper.reformat(vo.getAttribute(Columns.PSC_VLR).toString(), NumberHelper.getLang(), "en"));
                    }
                } catch (ParseException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ViewHelperException(ex);
                }
            }

            if(TextHelper.isNull(vlrMin)) {
                // Se o parâmetro 118 não estiver configurado pela csa, define o vlr mínimo de acordo com o parâmetro de serviço e o parâmetro de sistema
                if (paramSvc.getTpsVlrMinimoContrato() != null && !paramSvc.getTpsVlrMinimoContrato().equals("")) {
                    vlrMin = new BigDecimal(paramSvc.getTpsVlrMinimoContrato());
                } else {
                    vlrMin = paramCseMin;
                }
            }

            if(TextHelper.isNull(vlrMax)) {
                // Se o parâmetro 119 não estiver configurado pela csa, define o vlr máximo de acordo com o parâmetro de serviço e o parâmetro de sistema
                if (paramSvc.getTpsVlrMaximoContrato() != null && !paramSvc.getTpsVlrMaximoContrato().equals("")) {
                    vlrMax = new BigDecimal(paramSvc.getTpsVlrMaximoContrato());
                } else {
                    vlrMax = paramCseMax;
                }
            }

            String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(paramSvc.getTpsTipoVlr());

            // Faz as comparações necessárias com o adeVlr e os valores mínimos e máximos por serviço
            if (vlrMin != null && adeVlr.compareTo(vlrMin) < 0) {
                throw new ViewHelperException("mensagem.erro.valor.parcela.minimo", responsavel, labelTipoVlr, NumberHelper.format(vlrMin.doubleValue(), NumberHelper.getLang()));
            }
            if (vlrMax != null && adeVlr.compareTo(vlrMax) > 0) {
                throw new ViewHelperException("mensagem.erro.valor.parcela.maximo", responsavel, labelTipoVlr, NumberHelper.format(vlrMax.doubleValue(), NumberHelper.getLang()));
            }
        } catch (ParametroControllerException ex) {
            throw new ViewHelperException(ex);
        }
    }

    /**
     * Verifica se o valor da ADE renegociada pode ser utilizada como valor de margem para uma renegociação/compra.
     * Os critérios são baseados nas incidências de margem dos serviços dos contratos renegociados e do contrato novo
     * além dos parâmetros de margem casadas. Quando há margem casada, contratos renegociados de margens menores (2 ou 3)
     * podem ser utilizados como valor disponível para margens maiores (1 por exemplo).
     * @param incMargemAdeNova
     * @param incMargemAdeRenegociada
     * @param responsavel
     * @return
     */
    public static boolean valorMargemDisponivelRenegociacao(Short incMargemAdeNova, Short incMargemAdeRenegociada, AcessoSistema responsavel) {
        boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
        boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
        boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);

        return (incMargemAdeNova.equals(incMargemAdeRenegociada) &&
                !incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_NAO))

                  ||

               ((incMargemAdeNova.equals(CodedValues.INCIDE_MARGEM_SIM)) &&
                (incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM) ||
                 incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM_2) ||
                 incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM_3)) &&
                  margem123Casadas)

                  ||

               ((incMargemAdeNova.equals(CodedValues.INCIDE_MARGEM_SIM_2)) &&
                (incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM_2) ||
                 incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM_3)) &&
                  margem123Casadas)

                  ||

               ((incMargemAdeNova.equals(CodedValues.INCIDE_MARGEM_SIM)) &&
                (incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM) ||
                 incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM_3)) &&
                  margem1CasadaMargem3)

                  ||

               ((incMargemAdeNova.equals(CodedValues.INCIDE_MARGEM_SIM) ||
                 incMargemAdeNova.equals(CodedValues.INCIDE_MARGEM_SIM_2) ||
                 incMargemAdeNova.equals(CodedValues.INCIDE_MARGEM_SIM_3)) &&
                (incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM) ||
                 incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM_2) ||
                 incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM_3)) &&
                  margem123CasadasEsq)

                  ||

               ((incMargemAdeNova.equals(CodedValues.INCIDE_MARGEM_SIM) ||
                 incMargemAdeNova.equals(CodedValues.INCIDE_MARGEM_SIM_3)) &&
                (incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM) ||
                 incMargemAdeRenegociada.equals(CodedValues.INCIDE_MARGEM_SIM_3)) &&
                  margem1CasadaMargem3Esq)

                  ||

               CasamentoMargem.getInstance().margemOrigemAfetaDestino(incMargemAdeRenegociada, incMargemAdeNova)

                  ;

    }

    /**
     * Restringe parte do valor a ser disponibilizado para renegociação/compra dos contratos
     * envolvidos no processo, respeitando a limitação do parâmetro de serviço
     * TPS_PERCENTUAL_MINIMO_MANTER_VALOR_RENEG. Atualmente só é utilizado no módulo de
     * financiamento de dívida de cartão, para o caso de renegociação, não afetando a compra.
     * @param adeVlr
     * @param svcCodigo
     * @param compra
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static BigDecimal restringirValorDisponivelRenegociacao(BigDecimal adeVlr, String svcCodigo, boolean compra, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)
                    && !compra) {
                List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_PERCENTUAL_MINIMO_MANTER_VALOR_RENEG);

                ParametroDelegate parDelegate = new ParametroDelegate();
                ParamSvcTO paramSvcCse = parDelegate.selectParamSvcCse(svcCodigo, tpsCodigos, responsavel);

                // Percentual mínimo a ser mantido do valor original dos contratos renegociados
                BigDecimal percentualPreservaValorReneg = null;
                try {
                    if (!TextHelper.isNull(paramSvcCse.getTpsPercentualMinimoManterValorReneg())) {
                        percentualPreservaValorReneg = new BigDecimal(Double.valueOf(paramSvcCse.getTpsPercentualMinimoManterValorReneg()) / 100.00).setScale(2, java.math.RoundingMode.HALF_UP);
                        if (percentualPreservaValorReneg.signum() == 0) {
                            // Percentual = 0 -> significa que não deve ser mantido valor.
                            percentualPreservaValorReneg = null;
                        }
                    }
                } catch (NumberFormatException ex) {
                    throw new ViewHelperException("mensagem.erro.parametro.percentual.valor.contratos", responsavel);
                }

                if (percentualPreservaValorReneg != null) {
                    BigDecimal valorMinimoResidual = adeVlr.multiply(percentualPreservaValorReneg);
                    return adeVlr.subtract(valorMinimoResidual).setScale(2, java.math.RoundingMode.HALF_UP);
                }
            }
            return adeVlr;
        } catch (ParametroControllerException ex) {
            throw new ViewHelperException(ex);
        }
    }

    /**
     * Recuperar o período em que a ocorrência de liquidação de renegociação deve ser criada.
     * @param orgCodigo
     * @param csaCosigo
     * @param svcCodigo
     * @param dataInclusaoNovaAde
     * @param anoMesIniNovaAde
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static Date recuperarPeriodoOcorrenciaLiquidacao(String orgCodigo, String csaCodigo, String svcCodigo, Date dataInclusaoNovaAde, Date anoMesIniNovaAde, AcessoSistema responsavel) throws ViewHelperException {
        try {
            // período atual
            Date ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
            String periodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);

            // Verifica se o parâmetro de controle de prazo de renegociação no período está habilitado
            int maxPrazoRenegociacaoPeriodo = 0;
            List<String> tpsCodigo = new ArrayList<>();
            tpsCodigo.add(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO);
            ParametroDelegate parDelegate = new ParametroDelegate();
            List<TransferObject> params = parDelegate.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigo, false, responsavel);
            for (TransferObject param : params) {
                if (param != null && param.getAttribute(Columns.PSC_VLR) != null) {
                    if (param.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO)){
                        maxPrazoRenegociacaoPeriodo = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty())? Integer.valueOf((String)param.getAttribute(Columns.PSC_VLR)) : 0;
                    }
                }
            }

            if (maxPrazoRenegociacaoPeriodo > 0) {
                // diferença entre a data de inclusão e a data do primeiro recebimento da csa
                int diferenca = SimulacaoHelper.calculateDC(dataInclusaoNovaAde, anoMesIniNovaAde, orgCodigo, csaCodigo, responsavel);
                if (diferenca <= maxPrazoRenegociacaoPeriodo) {
                    // Calcula o período em que deve ser criada a ocorrência de liquidação do contrato
                    ocaPeriodo = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(ocaPeriodo), 1, periodicidade, responsavel);
                }
            }
            return ocaPeriodo;
        } catch (ParametroControllerException | PeriodoException ex) {
            throw new ViewHelperException(ex);
        }
    }

    /**
     * Calcular a carência de contratos destino de renegociação quando a liquidação dos contratos de origem acontecer em períodos futuros
     * ou quando o parâmetro de controle de prazo mínimo para início de contrato renegociado estiver configurado.
     * @param orgCodigo
     * @param csaCosigo
     * @param svcCodigo
     * @param adeCarencia
     * @param ocaPeriodoRenegociacao
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static Integer calcularCarenciaContratoDestinoRenegociacao(String orgCodigo, String csaCodigo, String svcCodigo, Integer adeCarencia, Date ocaPeriodoRenegociacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            // período atual
            Date ocaPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
            String periodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);

            Integer adeCarenciaNova = 0;
            if (ocaPeriodo.compareTo(ocaPeriodoRenegociacao) < 0) {
                // calcula a carência inicial usando o período de liquidação do contrato renegociado (origem)
                adeCarenciaNova = PeriodoHelper.getInstance().calcularCarencia(orgCodigo, ocaPeriodoRenegociacao, periodicidade, responsavel);
                // assume a carência informada pelo usuário, caso seja maior que a carência calculada
                if (adeCarencia.intValue() > adeCarenciaNova.intValue()) {
                    adeCarenciaNova = adeCarencia;
                }
            } else {
                // se o período atual for maior ou igual ao período renegociado, não calcula nova carência, apenas assume a carência informada
                adeCarenciaNova = adeCarencia;
            }

            // Verifica se o parâmetro de controle de carência mínima para contrato de renegociação está habilitado
            int minPrazoRenegociacaoPeriodo = 0;
            List<String> tpsCodigo = new ArrayList<>();
            tpsCodigo.add(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO);
            ParametroDelegate parDelegate = new ParametroDelegate();
            List<TransferObject> params = parDelegate.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigo, false, responsavel);
            for (TransferObject param : params) {
                if (param != null && param.getAttribute(Columns.PSC_VLR) != null) {
                    if (param.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO)){
                        minPrazoRenegociacaoPeriodo = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty())? Integer.valueOf((String)param.getAttribute(Columns.PSC_VLR)) : 0;
                    }
                }
            }
            if (minPrazoRenegociacaoPeriodo > 0) {
                boolean carenciaValida = false;
                // Calcula período de início do contrato com a carência aplicada
                Date prazoIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(ocaPeriodo), adeCarenciaNova, periodicidade, responsavel);
                while (!carenciaValida) {
                    // Calcula a quantidade de dias para desconto da primeira parcela do contrato e compara com o parâmetro que define o prazo mínimo
                    Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());
                    int diasPrimeiraParcela = SimulacaoHelper.calculateDC(agora, prazoIni, orgCodigo, csaCodigo, responsavel);
                    if (diasPrimeiraParcela < minPrazoRenegociacaoPeriodo) {
                        // aumenta a carência e calcula novo prazo
                        adeCarenciaNova++;
                        prazoIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(ocaPeriodo), adeCarenciaNova, periodicidade, responsavel);
                    } else {
                        carenciaValida = true;
                    }
                }
            }
            return adeCarenciaNova;
        } catch (ParametroControllerException | PeriodoException ex) {
            throw new ViewHelperException(ex);
        }
    }
}