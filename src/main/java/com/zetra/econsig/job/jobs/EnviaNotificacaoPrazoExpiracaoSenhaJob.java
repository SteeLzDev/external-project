package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.agendado.ProcessaEnviaNotificacaoPrazoExpiracaoSenha;

public class EnviaNotificacaoPrazoExpiracaoSenhaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviaNotificacaoPrazoExpiracaoSenhaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Notificação de Prazo de Expiração de Senha Job");
        final ProcessaEnviaNotificacaoPrazoExpiracaoSenha processo = new ProcessaEnviaNotificacaoPrazoExpiracaoSenha(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}