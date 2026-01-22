package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaReativacaoAutomaticaAde;

/**
 * <p>Title: ReativacaoAutomaticaAdeJob</p>
 * <p>Description: Trabalho para reativação automático de consignações</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReativacaoAutomaticaAdeJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReativacaoAutomaticaAdeJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Reativação Automática de Consignações Job");
        ProcessoAgendado processo = new ProcessaReativacaoAutomaticaAde(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
