package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaDeferimentoAutomatico;

/**
 * <p>Title: DeferimentoAutomaticoJob</p>
 * <p>Description: Trabalho para deferimento automático de consignações</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DeferimentoAutomaticoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DeferimentoAutomaticoJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Deferimento Automático de Consignações Job");
        ProcessoAgendado processo = new ProcessaDeferimentoAutomatico(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
