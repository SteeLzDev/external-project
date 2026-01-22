package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.notificacao.NotificacaoEmailController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaEnvioEmail</p>
 * <p>Description: Verifica e envia email agendados.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmail extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmail.class);

    public ProcessaEnvioEmail(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.debug("Executa envio de emails agendados.");
        NotificacaoEmailController notificacaoEmailController = ApplicationContextProvider.getApplicationContext().getBean(NotificacaoEmailController.class);
        notificacaoEmailController.enviarNotificacao(getResponsavel());
    }
}
