package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaHistoricoLog;

/**
 * <p>Title: GeraHistoricoLogJob</p>
 * <p>Description: Trabalho para criar histórico de log</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GeraHistoricoLogJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GeraHistoricoLogJob.class);

    @Override
    public void executar() {
        LOG.info("Gera Histórico de Log Job");
        ProcessoAgendado processo = new ProcessaHistoricoLog(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
