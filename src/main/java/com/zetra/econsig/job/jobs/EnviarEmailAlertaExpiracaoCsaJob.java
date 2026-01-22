package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaEnvioEmailDiasExpiracaoCsa;

/**
 * <p>Title: EnviarEmailAlertaExpiracaoCsaJob</p>
 * <p>Description: Tarefa de envio de email de alerta às consignatárias com data de expiração próxima.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailAlertaExpiracaoCsaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailAlertaExpiracaoCsaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia a verificação de consignatárias com datas de expiração próximas para envio de e-mail de alerta.");
        ProcessoAgendadoPeriodico processo = new ProcessaEnvioEmailDiasExpiracaoCsa(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
