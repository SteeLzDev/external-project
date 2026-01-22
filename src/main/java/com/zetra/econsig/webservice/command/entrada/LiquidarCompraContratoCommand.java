package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.OBS;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.CompraContratoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: LiquidarCompraContratoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de liquidar compra de contrato</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LiquidarCompraContratoCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LiquidarCompraContratoCommand.class);

    public LiquidarCompraContratoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        ArrayList<TransferObject> autList = (ArrayList<TransferObject>) parametros.get(CONSIGNACAO);
        Long adeNumero = (Long) parametros.get(ADE_NUMERO);

        TransferObject autdes = null;
        if (autList != null && autList.size() == 1) { // Otimização caso o contrato venha pela validação
            autdes = autList.get(0);
        } else {
            autdes = adeDelegate.findAutDescontoByAdeNumero(adeNumero, responsavel);
        }

        String adeCodigo = (String) autdes.getAttribute(Columns.ADE_CODIGO);

        List<TransferObject> relacionamentos = adeDelegate.pesquisarConsignacaoRelacionamento(adeCodigo, null, responsavel.getCsaCodigo(), null, CodedValues.TNT_CONTROLE_COMPRA, null, responsavel);
        if (relacionamentos == null || relacionamentos.isEmpty()) {
            throw new ZetraException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
        }

        ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
        String obs = (String) parametros.get(OBS);

        CustomTransferObject tmoTO = null;
        String tmoIdentificador = (String) parametros.get(TMO_IDENTIFICADOR);
        if (!TextHelper.isNull(tmoIdentificador)) {
            try {
                TipoMotivoOperacaoDelegate tmoDelegate = new TipoMotivoOperacaoDelegate();
                TipoMotivoOperacaoTransferObject tmo = tmoDelegate.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);

                tmoTO = new CustomTransferObject();
                tmoTO.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                tmoTO.setAttribute(Columns.TMO_CODIGO, tmo.getTmoCodigo());
                tmoTO.setAttribute(Columns.OCA_OBS, obs);
            } catch (TipoMotivoOperacaoControllerException tex) {
                LOG.error(tex.getMessage(), tex);
                throw new ZetraException("mensagem.erro.tipo.motivo.nao.encontrado", responsavel);
            }
        }
        consigDelegate.liquidarConsignacao(adeCodigo, tmoTO, null, responsavel);

        if (responsavel.isCsaCor()) {
            // Se é liquidação de contrato que está aguardando liquidação de compra, então executa rotina para desbloqueio
            String sadCodigo = autdes.getAttribute(Columns.SAD_CODIGO).toString();
            if (sadCodigo.equals(CodedValues.SAD_AGUARD_LIQUI_COMPRA)) {
                ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

                // Verifica se a consignatária pode ser desbloqueada automaticamente
                String csaCodigo = (responsavel.isCor() ? responsavel.getCodigoEntidadePai() : responsavel.getCodigoEntidade());
                if (csaDelegate.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                    ConsignatariaTransferObject csaTO = csaDelegate.findConsignataria(csaCodigo, responsavel);
                    LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.especifica.desbloqueada.automaticamente", responsavel, csaTO.getCsaNome()));
                }
                CompraContratoDelegate comDelegate = new CompraContratoDelegate();

                // Executa o desbloqueio automático também para os contratos resultantes da compra.
                // Evita que caso alguma consignatária tenha sido bloqueada por não informação
                // de pagamento de saldo devedor ela seja desbloqueada, se possível, em função da
                // liquidação do contrato originário.
                List<String> adesDestinoCompra = comDelegate.recuperarAdesCodigosDestinoCompra(adeCodigo);
                comDelegate.executarDesbloqueioAutomaticoConsignatarias(adesDestinoCompra, responsavel);
            }
        }
    }
}
