package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioPerfilDataExpirada;

/**
 * <p>Title: BloqueioPerfilDataExpiradaJob</p>
 * <p>Description: Trabalho para bloquear perfils que tenham a data expirada</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioPerfilDataExpiradaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueioPerfilDataExpiradaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia o bloqueio de perfil com data de expiração passada");
        ProcessoAgendado processo = new ProcessaBloqueioPerfilDataExpirada(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
