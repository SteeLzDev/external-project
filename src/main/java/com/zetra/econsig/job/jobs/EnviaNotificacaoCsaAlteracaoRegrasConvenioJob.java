package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.agendado.ProcessaEnviaNotificacaoCsaAlteracaoRegrasConvenio;

public class EnviaNotificacaoCsaAlteracaoRegrasConvenioJob extends AbstractJob {
	 private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviaNotificacaoCsaAlteracaoRegrasConvenioJob.class);

    @Override
    public void executar() {
    	LOG.info("Inicia Notificação para CSAs sobre alteração das regras de convênio Job");
        final ProcessaEnviaNotificacaoCsaAlteracaoRegrasConvenio processo = new ProcessaEnviaNotificacaoCsaAlteracaoRegrasConvenio(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
