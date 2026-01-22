package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaDesbloqueioCsaPenalidadeExpirada;

/**
 * <p>Title: DesbloqueioCsaPenalidadeExpiradaJob</p>
 * <p>Description: Trabalho para desloqueio de consignatária com pendência expirada.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DesbloqueioCsaPenalidadeExpiradaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DesbloqueioCsaPenalidadeExpiradaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia desloqueio de consignatária com pendência expirada Job");
        ProcessoAgendado processo = new ProcessaDesbloqueioCsaPenalidadeExpirada(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
