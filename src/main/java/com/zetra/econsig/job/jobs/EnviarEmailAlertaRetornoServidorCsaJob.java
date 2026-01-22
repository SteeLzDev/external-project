package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaEnvioEmailAlertaRetornoServidorCsaJob;

/**
 * <p>Title: EnviarEmailAlertaRetornoServidorCsaJob</p>
 * <p>Description: Classe para Rotina de notificação de servidor/funcionário de licença com data de retorno próxima</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.kitagawa $
 * $Revision: 23454 $
 * $Date: 2018-01-05 11:35:34 -0200 (Sex, 05 Jan 2018) $
 */
public class EnviarEmailAlertaRetornoServidorCsaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailAlertaRetornoServidorCsaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia envio de email de alerta às consignatárias com servidor/funcionário de licença com data de retorno próxima.");
        ProcessoAgendadoPeriodico processo = new ProcessaEnvioEmailAlertaRetornoServidorCsaJob(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
