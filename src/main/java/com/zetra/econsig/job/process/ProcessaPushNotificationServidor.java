package com.zetra.econsig.job.process;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.notificacao.NotificacaoDispositivoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaPushNotificationServidor</p>
 * <p>Description: Classe que dispara processo para envio de push notification para servidor</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $
 * $
 * $
 */
public final class ProcessaPushNotificationServidor extends Processo {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaPushNotificationServidor.class);

    private final String tituloPush;
    private final String mensagemPush;
    private final AcessoSistema responsavel;

    public ProcessaPushNotificationServidor(String titulo, String mensagem, AcessoSistema responsavel) {
        tituloPush = titulo;
        mensagemPush = mensagem;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        try {
            final LogDelegate logDelegate = new LogDelegate (responsavel, Log.NOTIFICACAO_DISPOSITIVO, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.write();
            final String strJsonBody = "{ \"tipo:\": \"MENSAGEM_PORTAL\" }";
            final NotificacaoDispositivoController notificacaoDispositivoController = ApplicationContextProvider.getApplicationContext().getBean(NotificacaoDispositivoController.class);
            notificacaoDispositivoController.enviarNotificacaoMultipla(tituloPush, mensagemPush, strJsonBody, responsavel);
            final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
            final String msg = ApplicationResourcesHelper.getMessage("mensagem.sucesso.envio.push.notification.servidor", responsavel);
            consignanteController.createOcorrenciaCse(CodedValues.TOC_ENVIO_MENSAGEM_SERVIDOR_PUSH_NOTIFICATION, msg, AcessoSistema.getAcessoUsuarioSistema());
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            codigoRetorno = ERRO;
        }
    }
}
