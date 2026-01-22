package com.zetra.econsig.helper.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ValidacaoMetodoBrasileiro</p>
 * <p>Description: Classe auxiliar com metodologia brasileira dos cálculos financeiros para validação.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: geovani.morais $
 * $Revision: 19169 $
 * $Date: 2015-05-07 12:44:12 -0300 (Qui, 07 Mai 2015) $
 */
public class ValidacaoMetodoBrasileiro extends ValidacaoMetodoGenerico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidacaoMetodoBrasileiro.class);

    /**
     * Executa a validação da taxa de juros de acordo com os parâmetros de serviço, que dizem
     * se deve calcular IOF/TAC, se deve adicioná-los ao valor financiado, se a CSA é isenta, etc.
     * Retorna um array com os valores finais do calculo.
     * @param adeVlr
     * @param adeVlrLiquido
     * @param adeVlrTac
     * @param adeVlrIof
     * @param adeVlrMensVinc
     * @param adePrazo
     * @param adeData
     * @param adeAnoMesIni
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param alteracao
     * @param parametros
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public BigDecimal[] validarTaxaJuros(BigDecimal adeVlr, BigDecimal adeVlrLiquido, BigDecimal adeVlrTac,
            BigDecimal adeVlrIof, BigDecimal adeVlrMensVinc, Integer adePrazo,
            Date adeData, Date adeAnoMesIni, String svcCodigo, String csaCodigo,
            String orgCodigo, boolean alteracao, Map<String, Object> parametros, String adePeriodicidade,
            String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            LOG.debug("ValidacoesControllerBean.validarTaxaJuros");
            LOG.debug("adeVlrLiquido: " + adeVlrLiquido);
            LOG.debug("adePrazo: " + adePrazo);

            // Verifica se o serviço adiciona o valor da TAC e/ou IOF ao valor liberado na validação da taxa de juros
            CustomTransferObject addTAC = getParametroSvc(CodedValues.TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS, svcCodigo, Boolean.FALSE, false, parametros);
            CustomTransferObject addIOF = getParametroSvc(CodedValues.TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS, svcCodigo, Boolean.FALSE, false, parametros);

            // Verifica se o sistema está configurado para trabalhar com o CET.
            boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            // Verifica se deve ser calculada a TAC e o IOF para este serviço
            CustomTransferObject paramCalcTacIof = getParametroSvc(CodedValues.TPS_CALC_TAC_IOF_VALIDA_TAXA_JUROS, svcCodigo, "", false, parametros);
            boolean calcTacIofCse = !temCET && (paramCalcTacIof != null && paramCalcTacIof.getAttribute(Columns.PSE_VLR) != null && paramCalcTacIof.getAttribute(Columns.PSE_VLR).equals("1"));
            boolean calcIof = calcTacIofCse;

            if (TextHelper.isNull(adePrazo)) {
                throw new AutorizacaoControllerException("mensagem.erro.validacao.exige.que.prazo.seja.informado", responsavel, ApplicationResourcesHelper.getMessage(temCET ? "rotulo.cet.abreviado" : "rotulo.taxa.juros.singular", responsavel));
            }

            if (!temCET) {
                // Usa o parametro de SVC por CSA se ele estiver configurado (Diferente de Nulo)
                ParametroDelegate parDelegate = new ParametroDelegate();
                List<String> tpsCodigoIOF = new ArrayList<>();
                tpsCodigoIOF.add(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA);
                List<TransferObject> params = parDelegate.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigoIOF, false, responsavel);
                if (params != null && params.size() == 1) {
                    TransferObject param = params.get(0);
                    if (param != null && param.getAttribute(Columns.PSC_VLR) != null) {
                        calcIof = param.getAttribute(Columns.PSC_VLR).toString().equalsIgnoreCase("S");
                    }
                }
            }

            // Obtém a taxa de juros atual da consignatária
            BigDecimal cftVlr = obterTaxaJurosValor(svcCodigo, csaCodigo, adePrazo, orgCodigo,
					rseCodigo, adeVlr, adeVlrLiquido, Short.valueOf(adePrazo.toString()), false, responsavel);
            LOG.debug("taxaCsa: " + cftVlr);

            if (cftVlr == null || cftVlr.signum() != 1) {
                //verifica antes se o serviço é destino de um relacionamento de compartilhamento de taxas.
                //se for, procura se o serviço origem tem taxas ativas
                boolean temCompartilhamentoTaxas = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, responsavel);

                if (temCompartilhamentoTaxas) {
                    //seta query para buscar taxas compartilhadas
                	cftVlr = obterTaxaJurosValor(svcCodigo, csaCodigo, adePrazo, orgCodigo,
        					rseCodigo, adeVlr, adeVlrLiquido, Short.valueOf(adePrazo.toString()), true, responsavel);
                    if (cftVlr == null || cftVlr.signum() != 1) {
                        throw new AutorizacaoControllerException((temCET ? "mensagem.aviso.sem.cet.prazo.csa" : "mensagem.aviso.sem.taxa.prazo.csa") + (alteracao ? ".alter" : ""), responsavel);
                    }
                } else {
                    throw new AutorizacaoControllerException((temCET ? "mensagem.aviso.sem.cet.prazo.csa" : "mensagem.aviso.sem.taxa.prazo.csa") + (alteracao ? ".alter" : ""), responsavel);
                }
            }

            BigDecimal vlrTotal = adeVlrLiquido;
            if (vlrTotal != null && vlrTotal.signum() > 0) {
                if (calcIof) {
                    // Se calcula IOF também calcula TAC
                    SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
                    adeVlrTac = simulacaoController.calculaTAC(svcCodigo, csaCodigo, orgCodigo, vlrTotal, cftVlr, responsavel);

                    // Calcula os dias de carência
                    int dc = SimulacaoHelper.calculateDC(adeData, adeAnoMesIni, orgCodigo, responsavel);
                    LOG.debug("vlrTotal = " + vlrTotal.doubleValue() + " adePrazo = " + Integer.parseInt(adePrazo.toString()) + " cftVlr = " +  cftVlr.doubleValue() / 100.00 + " dc = " +  dc);
                    CDCHelper cdcHelper = new CDCHelper(vlrTotal.doubleValue(), Integer.parseInt(adePrazo.toString()), cftVlr.doubleValue() / 100.00, dc);
                    cdcHelper.calculate();

                    adeVlrIof = new BigDecimal(cdcHelper.getIOFE()).setScale(2, java.math.RoundingMode.HALF_UP);
                    /*
                     *
                     *  TRECHO NÃO ALCANSÁVEL pois na linha 1446 está
                     *  boolean calcIof = calcTacIofCse;
                     *
                     *
                } else if (calcTacIofCse) {
                    // Se a consignatária é isenta de IOF, porém o serviço possui calculo de IOF,
                    // verifica se a aliquota adicional de IOF existe e se deve ser utilizada
                    adeVlrIof = new BigDecimal(CDCHelper.calcularIofAdicional(vlrTotal.doubleValue())).setScale(2, java.math.RoundingMode.HALF_UP);
                    */
                }

                LOG.debug("adeVlrTac: " + adeVlrTac);
                LOG.debug("adeVlrIof: " + adeVlrIof);

                if (addTAC != null && addTAC.getAttribute(Columns.PSE_VLR) != null &&
                        ((Boolean) addTAC.getAttribute(Columns.PSE_VLR)).booleanValue() &&
                        adeVlrTac != null) {
                    vlrTotal = vlrTotal.add(adeVlrTac);
                }

                if (addIOF != null && addIOF.getAttribute(Columns.PSE_VLR) != null &&
                        ((Boolean) addIOF.getAttribute(Columns.PSE_VLR)).booleanValue() &&
                        adeVlrIof != null) {
                    vlrTotal = vlrTotal.add(adeVlrIof);
                }
            } else {
                throw new AutorizacaoControllerException("mensagem.erro.deve.ser.informado.valor.liquido.liberado.para.validacao.taxa.juros", responsavel);
            }

            // Executa rotina que verifica o valor da parcela, de acordo com a taxa anunciada
            validarParcelaPelaTaxaJuros(adeData, adeAnoMesIni, cftVlr, vlrTotal, adeVlr, adeVlrMensVinc, adePrazo, svcCodigo, csaCodigo, orgCodigo, alteracao, adePeriodicidade, responsavel);

            // Retorna os valores finais da operação de validação da reserva
            return new BigDecimal[]{adeVlr, vlrTotal, adeVlrTac, adeVlrIof, adeVlrMensVinc, cftVlr};

        } catch (HQueryException | ParametroControllerException | SimulacaoControllerException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }
}
