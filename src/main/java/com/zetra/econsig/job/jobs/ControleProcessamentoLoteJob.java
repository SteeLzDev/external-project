package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaControleProcessamentoLote;

/**
 * <p>Title: ControleProcessamentoLoteJob</p>
 * <p>Description: Tarefa para recuperar de uma falha enquanto processamento de lote esta rodando.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ControleProcessamentoLoteJob extends AbstractJob {

    @Override
    public void executar() {
        final ProcessoAgendadoPeriodico processo = new ProcessaControleProcessamentoLote(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
