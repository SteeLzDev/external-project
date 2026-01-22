package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaEnvioEmailDownloadNaoRealizadoMovFin;

/**
 * <p>Title: CancelamentoAdeExpiradasJob</p>
 * <p>Description: Trabalho para envio de email de alerta de download não realizado de Movimento Financeiro</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailDownloadNaoRealizadoMovFinJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailDownloadNaoRealizadoMovFinJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia envio de email de alerta de download não realizado de Movimento Financeiro");
        ProcessoAgendado processo = new ProcessaEnvioEmailDownloadNaoRealizadoMovFin(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
