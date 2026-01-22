package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaEnvioEmailContratosReativacaoPendente;

/**
 * <p>Title: EnviarEmailAlertaExpiracaoCsaJob</p>
 * <p>Description: Tarefa de envio de email de alerta às consignatárias com data de expiração próxima.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailServidorContratosSuspensosPendentesReativacaoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailServidorContratosSuspensosPendentesReativacaoJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia envio de email para os servidores que possuem contratos suspensos pendentes de reativação.");
        ProcessoAgendadoPeriodico processo = new ProcessaEnvioEmailContratosReativacaoPendente(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
