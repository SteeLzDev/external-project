package com.zetra.econsig.helper.financeiro;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ValidacaoMetodoIndiano</p>
 * <p>Description: Classe auxiliar com metodologia indiana dos cálculos financeiros para validação.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidacaoMetodoIndiano extends ValidacaoMetodoGenerico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidacaoMetodoIndiano.class);

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
            String rseCodigo, AcessoSistema responsavel)
            		throws AutorizacaoControllerException {

        try {
            LOG.debug("ValidacoesControllerBean.validarTaxaJuros");
            LOG.debug("adeVlrLiquido: " + adeVlrLiquido);
            LOG.debug("adePrazo: " + adePrazo);

            // Verifica se o sistema está configurado para trabalhar com o CET pois no México o sistema irá trabalhar com taxa de juros
            boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
            if (temCET) {
                throw new AutorizacaoControllerException("mensagem.erro.validacao.parametrizacao.taxa.iva", responsavel);
            }

            // México usa ano de 360 dias
            boolean usaAno365 = ParamSist.getBoolParamSist(CodedValues.TPC_USA_ANO_365_DIAS_CALCULO_JUROS, responsavel);
            if (usaAno365) {
                throw new AutorizacaoControllerException("mensagem.erro.validacao.parametrizacao.dias.taxa.juros", responsavel);
            }

            if (TextHelper.isNull(adePrazo)) {
                throw new AutorizacaoControllerException("mensagem.erro.validacao.exige.que.prazo.seja.informado", responsavel, ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.singular", responsavel));
            }

         // Obtém a taxa de juros atual da consignatária
         // Obtém a taxa de juros atual da consignatária
            BigDecimal cftVlr = obterTaxaJurosValor(svcCodigo, csaCodigo, adePrazo, orgCodigo,
					rseCodigo, adeVlr, adeVlrLiquido, Short.valueOf(adePrazo.toString()), false, responsavel);
            LOG.debug("taxaCsa (anual): " + cftVlr);

            if (cftVlr == null || cftVlr.signum() != 1) {
                //verifica antes se o serviço é destino de um relacionamento de compartilhamento de taxas.
                //se for, procura se o serviço origem tem taxas ativas
                boolean temCompartilhamentoTaxas = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, responsavel);

                if (temCompartilhamentoTaxas) {
                    //seta query para buscar taxas compartilhadas
                	cftVlr = obterTaxaJurosValor(svcCodigo, csaCodigo, adePrazo, orgCodigo,
        					rseCodigo, adeVlr, adeVlrLiquido, Short.valueOf(adePrazo.toString()), true, responsavel);
                    if (cftVlr == null || cftVlr.signum() != 1) {
                        throw new AutorizacaoControllerException("mensagem.aviso.sem.taxa.prazo.csa" + (alteracao ? ".alter" : ""), responsavel);
                    }
                } else {
                    throw new AutorizacaoControllerException("mensagem.aviso.sem.taxa.prazo.csa" + (alteracao ? ".alter" : ""), responsavel);
                }
            }

            if (adeVlrLiquido == null || adeVlrLiquido.signum() <= 0) {
                throw new AutorizacaoControllerException("mensagem.erro.deve.ser.informado.valor.liquido.liberado.para.validacao.taxa.juros", responsavel);
            }

            //taxas indianas são cadastros anuais. Então, faz-se a divisão por 12 para se ter a taxa mensal
            BigDecimal cftVlrMensal = cftVlr.divide(new BigDecimal("12"), 6, java.math.RoundingMode.HALF_UP);

            // Executa rotina que verifica o valor da parcela, de acordo com a taxa anunciada
            validarParcelaPelaTaxaJuros(adeData, adeAnoMesIni, cftVlrMensal, adeVlrLiquido, adeVlr, adeVlrMensVinc, adePrazo, svcCodigo, csaCodigo, orgCodigo, alteracao, adePeriodicidade, responsavel);

            // Retorna os valores finais da operação de validação da reserva
            return new BigDecimal[]{adeVlr, adeVlrLiquido, adeVlrTac, adeVlrIof, adeVlrMensVinc, cftVlr};
        } catch (HQueryException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

}
