package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaValidacaoAmbiente;

/**
 * <p>Title: ValidarAmbienteJob</p>
 * <p>Description: Tarefa para validar o ambiente.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarAmbienteJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarAmbienteJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia a Validação do Ambiente do eConsig");
        ProcessoAgendadoPeriodico processo = new ProcessaValidacaoAmbiente(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
