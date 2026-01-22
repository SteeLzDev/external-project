package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioCsaPorCetExpirado;

/**
 * <p>Title: BloqueiaCsaPorCetExpiradoJob</p>
 * <p>Description: Trabalho para bloqueio de consignatárias por CET / Taxa de Juros com data de vigência expirada.</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueiaCsaPorCetExpiradoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueiaCsaPorCetExpiradoJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Bloqueio de Consignatarias por CET / Taxa de Juros com data de vigência expirada Job");
        ProcessoAgendado processo = new ProcessaBloqueioCsaPorCetExpirado(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
