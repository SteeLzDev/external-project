package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.LOGIN_EXTERNO;
import static com.zetra.econsig.webservice.CamposAPI.OBS_MOTIVO_OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: CancelarRenegociacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de cancelar renegociação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CancelarRenegociacaoCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelarRenegociacaoCommand.class);

    public CancelarRenegociacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void preProcessa(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.preProcessa(parametros);

        TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        String svcCodigo = (String) autorizacao.getAttribute(Columns.SVC_CODIGO);
        String serSenha = (String) parametros.get(SER_SENHA);

        boolean exigeSenhaSerCancel = parametroController.senhaServidorObrigatoriaCancelarReneg(svcCodigo, responsavel);
        if (exigeSenhaSerCancel && TextHelper.isNull(serSenha)) {
            throw new ZetraException("mensagem.informe.ser.senha.ou.token", responsavel);
        }
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        if (autorizacao != null) {
            String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
            String tmoIdentificador = (String) parametros.get(TMO_IDENTIFICADOR);
            String obs = (String) parametros.get(OBS_MOTIVO_OPERACAO);
            String rseCodigo = (String) autorizacao.getAttribute(Columns.RSE_CODIGO);
            String serSenha = (String) parametros.get(SER_SENHA);
            String loginExterno = (String) parametros.get(LOGIN_EXTERNO);

            if (!TextHelper.isNull(serSenha)) {
                validarSenhaServidor(rseCodigo, serSenha, true, loginExterno, null, null, responsavel);
            }

            CustomTransferObject tmoTO = null;
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

            ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();
            consigDelegate.cancelarRenegociacao(adeCodigo, tmoTO, responsavel);
        }
    }
}
