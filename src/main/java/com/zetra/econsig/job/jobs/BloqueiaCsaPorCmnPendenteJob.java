package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioCsaCmnPendente;

/**
 * <p>Title: BloqueiaCsaPorCmnPendenteJob</p>
 * <p>Description: Tarefa de agendamento para verificação de csa com comunicação
 *                 pendente.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueiaCsaPorCmnPendenteJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueiaCsaPorCmnPendenteJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia a verificação de comunicações pendentes para bloqueio de consignatárias");
        ProcessoAgendadoPeriodico processo = new ProcessaBloqueioCsaCmnPendente(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
