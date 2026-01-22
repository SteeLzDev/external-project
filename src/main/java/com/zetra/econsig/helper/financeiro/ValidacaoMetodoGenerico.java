package com.zetra.econsig.helper.financeiro;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.persistence.query.coeficiente.ListaTaxasJurosQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCseQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ValidacaoMetodoGenerico</p>
 * <p>Description: Classe base para os cálculos financeiros para validação de taxa.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ValidacaoMetodoGenerico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidacaoMetodoGenerico.class);

    public abstract BigDecimal[] validarTaxaJuros(BigDecimal adeVlr, BigDecimal adeVlrLiquido, BigDecimal adeVlrTac,
            BigDecimal adeVlrIof, BigDecimal adeVlrMensVinc, Integer adePrazo,
            Date adeData, Date adeAnoMesIni, String svcCodigo, String csaCodigo,
            String orgCodigo, boolean alteracao, Map<String, Object> parametros, String adePeriodicidade,
            String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    /**
     * Metodo para validar a taxa de juros anunciada no sistema para o serviço, de
     * acordo com o valor da parcela e o valor liberado informado pelo usuário
     * @param adeData
     * @param adeAnoMesIni
     * @param taxaCsa
     * @param valor
     * @param parcela
     * @param vlrVinc
     * @param prazo
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param alteracao
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    protected void validarParcelaPelaTaxaJuros(Date adeData, Date adeAnoMesIni, BigDecimal taxaCsa, BigDecimal valor, BigDecimal parcela, BigDecimal vlrVinc,
            Integer prazo, String svcCodigo, String csaCodigo, String orgCodigo, boolean alteracao, String adePeridiocidade, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // validacao de valor minimo para CET/Taxa de juros
            BigDecimal taxaMinimaPermitida = null;
            CustomTransferObject paramVlrMinPermitidoTaxaJuros = getParametroSvc(CodedValues.TPS_VALOR_MINIMO_PERMITIDO_TAXA_JUROS, svcCodigo, new BigDecimal("0"), false, null);
            if (paramVlrMinPermitidoTaxaJuros != null && paramVlrMinPermitidoTaxaJuros.getAttribute(Columns.PSE_VLR) != null) {
                try {
                    taxaMinimaPermitida = new BigDecimal(paramVlrMinPermitidoTaxaJuros.getAttribute(Columns.PSE_VLR).toString());
                } catch (NumberFormatException nfex) {
                    LOG.debug("Valor Inválido para o parâmetro de serviço (" + CodedValues.TPS_VALOR_MINIMO_PERMITIDO_TAXA_JUROS + ").");
                }
            }

            // Valor padrão para o parametro é usar a taxa da CSA.
            boolean usaTaxaCsa = !ParamSist.paramEquals(CodedValues.TPC_USA_TAXA_CSA_CORRECAO_VLR_PRESENTE, CodedValues.TPC_NAO, responsavel);
            BigDecimal taxaCorrecao = taxaCsa;
            BigDecimal taxaCse = null;
            Integer prazoCse = null;
            if (!usaTaxaCsa) {
                // Obtém as faixas de limites de taxas de juros do serviço
                SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
                List<TransferObject> limiteTaxa = simulacaoController.getLimiteTaxas(svcCodigo, responsavel);
                if (limiteTaxa.size() > 0) {
                    for (TransferObject ctoLte : limiteTaxa) {
                        prazoCse = Integer.valueOf(ctoLte.getAttribute(Columns.LTJ_PRAZO_REF).toString());
                        if (prazoCse.compareTo(prazo) >= 0) {
                            taxaCse = new BigDecimal(ctoLte.getAttribute(Columns.LTJ_JUROS_MAX).toString()).setScale(2, java.math.RoundingMode.HALF_UP);
                            break;
                        }
                    }
                }
                LOG.debug("taxaCse: " + taxaCse);
                if (taxaCse == null) {
                    throw new AutorizacaoControllerException("mensagem.erro.configuracao.sistema.configurar.valor.maximo.para.taxa.nos.parametros.servico", responsavel);
                } else if (taxaCse.compareTo(taxaCsa) < 0) {
                    throw new AutorizacaoControllerException("mensagem.erro.valor.informado.taxa.invalido.maximo.permitido.para.prazo.valor", responsavel, String.valueOf(prazo), NumberHelper.reformat(taxaCse.toString(), "en", NumberHelper.getLang()));
                }
                taxaCorrecao = taxaCse;
            }

            LOG.debug("adeData: " + adeData);
            LOG.debug("adeAnoMesIni: " + adeAnoMesIni);

            BigDecimal parcelaCalc = SimulacaoHelper.calcularValorPrestacao(valor, prazo, adeData, adeAnoMesIni, taxaCorrecao, taxaCsa, orgCodigo, adePeridiocidade, responsavel);
            LOG.debug("parcelaCalc: " + parcelaCalc + (usaTaxaCsa ? " usou CSA" : " usou CSE"));

            BigDecimal parcelaCalcMin = null;
            if (taxaMinimaPermitida != null) {
                parcelaCalcMin = SimulacaoHelper.calcularValorPrestacao(valor, prazo, adeData, adeAnoMesIni, taxaMinimaPermitida, taxaMinimaPermitida, orgCodigo, adePeridiocidade, responsavel);
                LOG.debug("parcelaCalcMin: " + parcelaCalcMin);
            }

            // Se valida mensalidade vinculada, adiciona o valor da mensalidade na parcela informada
            CustomTransferObject valMensVinc = null;
            ListaParamSvcCseQuery query = new ListaParamSvcCseQuery();
            query.svcCodigo = svcCodigo;
            query.tpsCodigo = CodedValues.TPS_VALIDA_MENS_VINC_VAL_TAXA_JUROS;
            List<TransferObject> lstParamValidaMensVinc = query.executarDTO();
            if (lstParamValidaMensVinc != null && lstParamValidaMensVinc.size() > 0) {
                valMensVinc = (CustomTransferObject) lstParamValidaMensVinc.get(0);
            }

            if (valMensVinc != null && valMensVinc.getAttribute(Columns.PSE_VLR) != null &&
                    valMensVinc.getAttribute(Columns.PSE_VLR).toString().equals("1") && vlrVinc != null) {
                parcela = parcela.add(vlrVinc);
            }

            // Valor de desvio da parcela para validação da taxa de juros
            CustomTransferObject excMonetTxJuros = null;
            query = new ListaParamSvcCseQuery();
            query.svcCodigo = svcCodigo;
            query.tpsCodigo = CodedValues.TPS_EXCEDENTE_MONETARIO_TX_JUROS;
            List<TransferObject> lstParamExcMonetTxJuros = query.executarDTO();
            if (lstParamExcMonetTxJuros != null && lstParamExcMonetTxJuros.size() > 0) {
                excMonetTxJuros = (CustomTransferObject) lstParamExcMonetTxJuros.get(0);
            }

            if (excMonetTxJuros != null && excMonetTxJuros.getAttribute(Columns.PSE_VLR) != null &&
                    !excMonetTxJuros.getAttribute(Columns.PSE_VLR).toString().equals("")) {
                BigDecimal excMon = new BigDecimal(excMonetTxJuros.getAttribute(Columns.PSE_VLR).toString());
                excMon = excMon.divide(new BigDecimal("100.00"), 4, java.math.RoundingMode.HALF_UP).add(new BigDecimal("1.00")).setScale(4, java.math.RoundingMode.HALF_UP);
                parcelaCalc = parcelaCalc.multiply(excMon).setScale(2, java.math.RoundingMode.HALF_UP);
                if (taxaMinimaPermitida != null) {
                    parcelaCalcMin = parcelaCalcMin.multiply(excMon).setScale(2, java.math.RoundingMode.HALF_UP);
                    LOG.debug("parcelaCalcMin: " + parcelaCalcMin);
                }
            }
            LOG.debug("parcelaCalc: " + parcelaCalc);
            LOG.debug("parcela: " + parcela);

            // Verifica se o sistema está configurado para trabalhar com o CET.
            boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            if (taxaCsa == null || (!usaTaxaCsa && taxaCse == null)) {
                throw new AutorizacaoControllerException((temCET ? "mensagem.aviso.sem.cet.prazo.csa" : "mensagem.aviso.sem.taxa.prazo.csa") + (alteracao ? ".alter" : ""), responsavel);
            } else if (parcela.compareTo(parcelaCalc) > 0) {
                if(temCET) {
                    throw new AutorizacaoControllerException("mensagem.erro.cet.calculado.maior.anunciado", responsavel, String.valueOf(taxaCorrecao), String.valueOf(NumberHelper.format(parcelaCalc.doubleValue(), NumberHelper.getLang())));
                } else {
                    throw new AutorizacaoControllerException("mensagem.erro.taxa.calculada.maior.anunciado" + (alteracao ? ".alter" : ""), responsavel);
                }

            }

            // Verifica se o valor da parcela obedece ao mínimo estabelecido pela taxa de juros mínima permitida
            if (taxaMinimaPermitida != null && parcela.compareTo(parcelaCalcMin) < 0) {
                throw new AutorizacaoControllerException((temCET ? "mensagem.erro.cet.calculado.menor.minimo.permitido" : "mensagem.erro.taxa.calculada.menor.minimo.permitido") + (alteracao ? ".alter" : ""), responsavel);
            }
        } catch (SimulacaoControllerException | ViewHelperException | ParseException | NumberFormatException | HQueryException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    protected BigDecimal obterTaxaJurosValor(String svcCodigo, String csaCodigo, Integer prazo,
            String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short przVlr,
            boolean taxaJurosCompartilhada, AcessoSistema responsavel)
                    throws HQueryException, AutorizacaoControllerException {

        BigDecimal cftVlr = null;

        BigDecimal valorTotal = new BigDecimal(0);
        if (vlrParcela != null) {
            valorTotal = vlrParcela.multiply(new BigDecimal(przVlr));
        }

        if (ParamSist.paramEquals(CodedValues.TPC_USA_DEFINICAO_TAXA_JUROS, CodedValues.TPC_SIM, responsavel)) {
            try {
                SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
                List<TransferObject> defTaxaJurosList = simulacaoController.buscarDefinicaoTaxaJuros(csaCodigo, orgCodigo, svcCodigo, rseCodigo,
                        valorTotal, vlrLiberado, Integer.valueOf(przVlr), responsavel);
                if (defTaxaJurosList != null && !defTaxaJurosList.isEmpty()) {
                    cftVlr = (BigDecimal) defTaxaJurosList.get(0).getAttribute(Columns.CFT_VLR);
                }
            } catch (SimulacaoControllerException e) {
                throw new AutorizacaoControllerException(e);
            }
        }

        if (cftVlr == null) {
            ListaTaxasJurosQuery query = new ListaTaxasJurosQuery();
            CustomTransferObject parametrosQuery = new CustomTransferObject();
            parametrosQuery.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            parametrosQuery.setAttribute(Columns.SVC_CODIGO, svcCodigo);
            parametrosQuery.setAttribute("PRAZO", prazo);
            parametrosQuery.setAttribute("ATIVO", true);
            parametrosQuery.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);
            query.setCriterios(parametrosQuery);

            if (taxaJurosCompartilhada) {
                query.setTaxaJuroCompartilhada(taxaJurosCompartilhada);
            }

            List<TransferObject> listPrazo = query.executarDTO();
            if (listPrazo.size() > 0) {
                TransferObject cto = listPrazo.get(0);
                if (cto.getAttribute(Columns.CFT_VLR) != null) {
                    cftVlr = new BigDecimal(cto.getAttribute(Columns.CFT_VLR).toString()).setScale(2, java.math.RoundingMode.HALF_UP);
                }
            }

        }

        return cftVlr;
    }

    protected CustomTransferObject getParametroSvc(String tpsCodigo, String svcCodigo, Object tipoRetorno, boolean nuloVerdadeiro, Map<String, Object> parametros) throws AutorizacaoControllerException {
        AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
        return autorizacaoController.getParametroSvc(tpsCodigo, svcCodigo, tipoRetorno, nuloVerdadeiro, parametros);
    }
}
