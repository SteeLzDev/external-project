package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConfirmarReservaCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de confirmar reserva</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConfirmarReservaCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarReservaCommand.class);

    public ConfirmarReservaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
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
            ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
            AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();

            boolean temPermissaoConfReserva = responsavel.temPermissao(CodedValues.FUN_CONF_RESERVA);
            boolean temPermissaoConfRenegociacao = responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_RENEGOCIACAO);
            boolean isRenegociacaoCompra = adeDelegate.isDestinoRelacionamento(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO)
                    || adeDelegate.isDestinoRelacionamento(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);

            if ((!temPermissaoConfRenegociacao && isRenegociacaoCompra) || (!temPermissaoConfReserva && !isRenegociacaoCompra)) {
                throw new ZetraException("mensagem.erro.usuario.nao.tem.permissao.para.confirmar.esta.autorizacao", responsavel);
            }

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

            try {
                consigDelegate.confirmarConsignacao(adeCodigo, tmoTO, responsavel);
            } catch (AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);

                if (!TextHelper.isNull(ex.getMessageKey()) && ex.getMessageKey().equals("mensagem.erro.usuario.atual.nao.possui.permissao.para.confirmar.renegociacao")) {
                    throw new ZetraException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
                } else {
                    throw ex;
                }
            }
        }
    }
}
