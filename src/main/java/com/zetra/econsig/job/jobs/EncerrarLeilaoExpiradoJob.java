package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaEncerramentoLeilaoExpirado;

/**
 * <p>Title: EncerrarLeilaoExpiradoJob</p>
 * <p>Description: Job para execução do processo de encerramento de
 * leilão de solicitação expirado, ou seja, aquele que o servidor
 * não efetou a escolha da proposta.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EncerrarLeilaoExpiradoJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendadoPeriodico processo = new ProcessaEncerramentoLeilaoExpirado(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getAgdCodigo(), processo);
    }
}
