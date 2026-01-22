package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaExclusaoRelAntigos;

/**
 * <p>Title: ExclusaoRelatoriosAntigosJob</p>
 * <p>Description: Trabalho para exclusão de relatórios antigos</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExclusaoRelatoriosAntigosJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExclusaoRelatoriosAntigosJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Exclusão de Relatórios Antigos Job");
        ProcessoAgendado processo = new ProcessaExclusaoRelAntigos(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
