package com.zetra.econsig.service.consignacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoEConsigEnum;

/**
 * <p>Title: AutorizarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Autorização de Consignação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class AutorizarConsignacaoControllerBean extends DeferirConsignacaoControllerBean implements AutorizarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizarConsignacaoControllerBean.class);

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Override
    public void autorizar(String adeCodigo, String corCodigo, String senhaUtilizada, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
            String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();
            if (!sadCodigo.equals(CodedValues.SAD_AGUARD_CONF) && !sadCodigo.equals(CodedValues.SAD_AGUARD_DEFER) && !sadCodigo.equals(CodedValues.SAD_SOLICITADO)) {
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.autorizada.situacao.atual.dela.nao.permite.esta.operacao", responsavel, autdes.getStatusAutorizacaoDesconto().getSadDescricao());
            }

            VerbaConvenio verbaConvenio = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo());
            String cnvCodigo = verbaConvenio.getConvenio().getCnvCodigo();

            Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
            String csaCodigo = convenio.getConsignataria().getCsaCodigo();
            String svcCodigo = convenio.getServico().getSvcCodigo();
            String rseCodigo = autdes.getRegistroServidor().getRseCodigo();

            // Verifica se as entidades estão ativas para fazer novas reservas
            validarEntidades(cnvCodigo, corCodigo, responsavel);

            if (usuarioPodeConfirmarReserva(responsavel) && usuarioPodeDeferir(svcCodigo, csaCodigo, rseCodigo, true, responsavel)) {
                deferir(autdes, senhaUtilizada, responsavel);
            }
            String ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR, ApplicationResourcesHelper.getMessage("mensagem.informacao.autorizacao.pela.senha.do.servidor", responsavel), responsavel);

            // grava motivo da operacao
            if (tipoMotivoOperacao != null) {
                if (ocaCodigo != null) {
                    tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                }
                tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
            }

            // Gera o Log de auditoria
            LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.AUTORIZAR_RESERVA, Log.LOG_INFORMACAO);
            logDelegate.setAutorizacaoDesconto(adeCodigo);
            logDelegate.write();

            // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
            // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
            ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.AUTORIZAR_RESERVA, adeCodigo, null, tipoMotivoOperacao, responsavel);
            processoEmail.start();

        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }
}
