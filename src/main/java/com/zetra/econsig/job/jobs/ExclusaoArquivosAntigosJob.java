package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaExclusaoArquivosAntigos;

/**
 * <p>Title: ExclusaoArquivosAntigosJob</p>
 * <p>Description: Tarefa de exclusao de arquivos antigos.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExclusaoArquivosAntigosJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExclusaoArquivosAntigosJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Exclusão de Arquivos Antigos Job");
        ProcessoAgendado processo = new ProcessaExclusaoArquivosAntigos(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
