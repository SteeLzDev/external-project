package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioConsignatarias;

/**
 * <p>Title: BloqueioConsignatariasJob</p>
 * <p>Description: Trabalho para bloqueio de consignatárias</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioConsignatariasJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueioConsignatariasJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Bloqueio de Consignatarias Job");
        ProcessoAgendado processo = new ProcessaBloqueioConsignatarias(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
