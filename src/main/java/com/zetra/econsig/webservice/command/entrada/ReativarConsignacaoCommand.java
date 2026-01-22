package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignacao.SuspenderConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ReativarConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de reativar consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReativarConsignacaoCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReativarConsignacaoCommand.class);

    public ReativarConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        exigeMotivoOperacao(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        if (autorizacao != null) {
            final String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
            final ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();

            if (ParamSist.getBoolParamSist(CodedValues.TPC_REATIVAR_CONTRATO_SUSP_PRD_REJEITADA_EXIGE_CONF_GESTOR, responsavel) && ParamSist.paramEquals(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, CodedValues.TPC_SIM, responsavel)) {
                final String rseCodigo = (String) autorizacao.getAttribute(Columns.RSE_CODIGO);
                final String csaCodigo = (String) parametros.get(CSA_CODIGO);
                final String serSenha = (String) parametros.get(SER_SENHA);
                final String token = (String) parametros.get(TOKEN);
                final String loginExterno = (String) parametros.get(SER_LOGIN);
                final String svcCodigo = (String) autorizacao.getAttribute(Columns.SVC_CODIGO);

                final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                final SuspenderConsignacaoController suspenderConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(SuspenderConsignacaoController.class);

                if (paramSvc.isTpsExigeSenhaSerReativarConsignacao() && suspenderConsignacaoController.contratoSuspensoPrdRejeitadaNaoReativado(adeCodigo, responsavel)) {
                    if (!TextHelper.isNull(serSenha) || !TextHelper.isNull(token)) {
                        validarSenhaServidor(rseCodigo, serSenha, false, loginExterno, csaCodigo, token, responsavel);
                    } else {
                        throw new ZetraException("mensagem.informe.ser.senha.ou.token", responsavel);
                    }
                }
            }

            final String tmoObs = (String) parametros.get(TMO_OBS);
            final String tmoIdentificador = (String) parametros.get(TMO_IDENTIFICADOR);

            CustomTransferObject tmoTO = null;
            if (!TextHelper.isNull(tmoIdentificador)) {
                try {
                    final TipoMotivoOperacaoDelegate tmoDelegate = new TipoMotivoOperacaoDelegate();
                    final TipoMotivoOperacaoTransferObject tmo = tmoDelegate.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);

                    tmoTO = new CustomTransferObject();
                    tmoTO.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tmoTO.setAttribute(Columns.TMO_CODIGO, tmo.getTmoCodigo());
                    tmoTO.setAttribute(Columns.OCA_OBS, tmoObs);
                } catch (final TipoMotivoOperacaoControllerException tex) {
                    LOG.error(tex.getMessage(), tex);
                    throw new ZetraException("mensagem.erro.tipo.motivo.nao.encontrado", responsavel);
                }
            }

            consigDelegate.reativarConsignacao(adeCodigo, tmoTO, null, responsavel);
        }
    }
}
