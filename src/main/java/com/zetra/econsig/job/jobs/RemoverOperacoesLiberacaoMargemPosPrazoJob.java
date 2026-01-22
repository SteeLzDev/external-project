package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaRemocaoOperacoesLiberacaoMargemPosPrazo;

/**
 * <p>Title: RemoverOperacoesLiberacaoMargemPosPrazoJob</p>
 * <p>Description: Trabalho para remover operações de liberação de margem que não geraram bloqueio
 * e que tem data passada, não passível de gerarem bloqueios.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RemoverOperacoesLiberacaoMargemPosPrazoJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendadoPeriodico processo = new ProcessaRemocaoOperacoesLiberacaoMargemPosPrazo(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getAgdCodigo(), processo);
    }
}
