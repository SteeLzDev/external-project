package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaNotificacaoNovaPropostaLeilao;

/**
 * <p>Title: EnviaNotificacaoNovaPropostaLeilaoJob</p>
 * <p>Description: Quartz Job para o processo de envio de notificação para nova proposta de leilão.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviaNotificacaoNovaPropostaLeilaoJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendadoPeriodico processo = new ProcessaNotificacaoNovaPropostaLeilao(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getAgdCodigo(), processo);

    }

}
