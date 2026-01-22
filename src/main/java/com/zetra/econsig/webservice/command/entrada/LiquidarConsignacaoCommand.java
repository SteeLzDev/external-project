package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ALERTA;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.CompraContratoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: LiquidarConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de liquidar consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LiquidarConsignacaoCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LiquidarConsignacaoCommand.class);

    public LiquidarConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        exigeMotivoOperacao(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        if (autorizacao != null) {
            String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
            String csaCodigo = (String) parametros.get(CSA_CODIGO);

            ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

            String tmoObs = (String) parametros.get(TMO_OBS);
            String tmoIdentificador = (String) parametros.get(TMO_IDENTIFICADOR);

            CustomTransferObject tmoTO = null;
            if (!TextHelper.isNull(tmoIdentificador)) {
                try {
                    TipoMotivoOperacaoDelegate tmoDelegate = new TipoMotivoOperacaoDelegate();
                    TipoMotivoOperacaoTransferObject tmo = tmoDelegate.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);

                    tmoTO = new CustomTransferObject();
                    tmoTO.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tmoTO.setAttribute(Columns.TMO_CODIGO, tmo.getTmoCodigo());
                    tmoTO.setAttribute(Columns.OCA_OBS, tmoObs);
                } catch (TipoMotivoOperacaoControllerException tex) {
                    LOG.error(tex.getMessage(), tex);
                    throw new ZetraException("mensagem.erro.tipo.motivo.nao.encontrado", responsavel);
                }
            }

            Date ocaPeriodo = null;
            String strOcaPeriodo = (String) parametros.get(PERIODO);
            if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
                    ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {

                if (!TextHelper.isNull(strOcaPeriodo)) {
                    try {
                        if (strOcaPeriodo.matches("([0-9]{2})/([0-9]{4})")) {
                            ocaPeriodo = DateHelper.parsePeriodString(strOcaPeriodo);
                        } else {
                            throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
                        }
                    } catch (ParseException e) {
                        throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
                    }
                }
            }

            LiquidarConsignacaoParametros parametrosLiquidacao = new LiquidarConsignacaoParametros();
            parametrosLiquidacao.setOcaPeriodo(ocaPeriodo);

            consigDelegate.liquidarConsignacao(adeCodigo, tmoTO, parametrosLiquidacao, responsavel);

            try {
                if (responsavel.isCsaCor()) {
                    // Verifica se a consignatária pode ser desbloqueada automaticamente
                    if (csaDelegate.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                        parametros.put(ALERTA, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
                    }

                    if (autorizacao.getAttribute(Columns.SAD_CODIGO).toString().equals(CodedValues.SAD_AGUARD_LIQUI_COMPRA)) {
                        // Executa o desbloqueio automático também para os contratos resultantes da compra.
                        // Evita que caso alguma consignatária tenha sido bloqueada por não informação
                        // de pagamento de saldo devedor ela seja desbloqueada, se possível, em função da
                        // liquidação do contrato originário.
                        CompraContratoDelegate comDelegate = new CompraContratoDelegate();
                        List<String> adesDestinoCompra = comDelegate.recuperarAdesCodigosDestinoCompra(adeCodigo);
                        comDelegate.executarDesbloqueioAutomaticoConsignatarias(adesDestinoCompra, responsavel);
                    }
                }
            } catch (ConsignatariaControllerException ex) {
                throw ex;
            }
        }
    }
}
