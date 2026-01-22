package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaConfirmacaoOperacoesLiberacaoMargem;

/**
 * <p>Title: ConfirmarOperacaoLiberaMargemJob</p>
 * <p>Description: Quartz Job para confirmar os registros de operações que liberaram margem.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConfirmarOperacoesLiberacaoMargemJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendadoPeriodico processo = new ProcessaConfirmacaoOperacoesLiberacaoMargem(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getAgdCodigo(), processo);
    }

}
