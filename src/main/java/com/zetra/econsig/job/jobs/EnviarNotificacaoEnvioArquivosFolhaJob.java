package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaNotificacaoEnvioArquivosFolha;

/**
 * <p>Title: EnviarNotificacaoEnvioArquivosFolhaJob</p>
 * <p>Description: Inicia envio de email de notificação de envio de arquivos da folha.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarNotificacaoEnvioArquivosFolhaJob extends AbstractJob {
    @Override
    public void executar() {
        if (!ControladorProcessos.getInstance().processoAtivo(getAgdCodigo())) {
            ProcessoAgendadoPeriodico processo = new ProcessaNotificacaoEnvioArquivosFolha(getAgdCodigo(), getResponsavel());
            processo.start();
            ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
        }
    }
}
