package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.agendado.ProcessaUpdateCalendarioOffset;

/**
 * <p>Title: AtualizaCalendarioJob</p>
 * <p>Description: Tarefa de agendamento para atualização de tb_calendario.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtualizaCalendarioJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtualizaCalendarioJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia a atualização da tabela calendário.");
        ProcessaUpdateCalendarioOffset processo = new ProcessaUpdateCalendarioOffset(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
