package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.agendado.ProcessaEnviaNotificacaoAutorizacaoIraVencer;

public class EnviaNotificacaoAutorizacaoIraVencerJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviaNotificacaoAutorizacaoIraVencerJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Notificação que Informa o Vencimento da Autorização Job");
        final ProcessaEnviaNotificacaoAutorizacaoIraVencer processo = new ProcessaEnviaNotificacaoAutorizacaoIraVencer(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
