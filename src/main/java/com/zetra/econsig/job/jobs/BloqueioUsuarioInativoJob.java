package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioUsuarioInativo;

/**
 * <p>Title: BloqueioUsuarioInativoJob</p>
 * <p>Description: Trabalho para bloqueio de usuários inativos.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioUsuarioInativoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueioUsuarioInativoJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Bloqueio de Usuários Inativos Job");
        ProcessoAgendado processo = new ProcessaBloqueioUsuarioInativo(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
