package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaEnvioEmailComunicacaoNaoLida;

/**
 * <p>Title: EnviarEmailComunicacaoNaoLidaJob</p>
 * <p>Description: Classe para Rotina de notificação de comunicação não lida</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailComunicacaoNaoLidaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailComunicacaoNaoLidaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia envio de email de comunicação não lida.");
        ProcessoAgendadoPeriodico processo = new ProcessaEnvioEmailComunicacaoNaoLida(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
