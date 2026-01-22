package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaEnvioEmailAlertaProximidadeCorteCsa;

/**
 * <p>Title: EnviarEmailAlertaProximidadeCorteCsaJob</p>
 * <p>Description: Tarefa de envio de email de alerta às consignatárias com data de corte próxima.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailAlertaProximidadeCorteCsaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailAlertaProximidadeCorteCsaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia envio de email de alerta às consignatárias com data de corte próxima.");
        ProcessoAgendadoPeriodico processo = new ProcessaEnvioEmailAlertaProximidadeCorteCsa(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
