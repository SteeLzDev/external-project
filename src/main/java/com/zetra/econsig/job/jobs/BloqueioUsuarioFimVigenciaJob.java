package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioUsuarioFimVigencia;

/**
 * <p>Title: BloqueioUsuarioFimVigenciaJob</p>
 * <p>Description: Trabalho para bloqueio de usuários por fim de periodo vigente.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioUsuarioFimVigenciaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueioUsuarioFimVigenciaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Bloqueio de Usuários por Fim de Vigencia Job");
        ProcessoAgendado processo = new ProcessaBloqueioUsuarioFimVigencia(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
