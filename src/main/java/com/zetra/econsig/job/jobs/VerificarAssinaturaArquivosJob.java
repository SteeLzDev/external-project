package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaVerificacaoDocumentacaoAssinada;

/**
 * <p>Title: EnvioArquivoAssinaturaDigitalJob</p>
 * <p>Description: Quartz Job para o processo de conferência de assinatura digital de anexos de solicitação pelo serviço externo.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerificarAssinaturaArquivosJob extends AbstractJob {

    @Override
    public void executar() {
        if (!ControladorProcessos.getInstance().processoAtivo(getAgdCodigo())) {
            ProcessoAgendadoPeriodico processo = new ProcessaVerificacaoDocumentacaoAssinada(getAgdCodigo(), getResponsavel());
            processo.start();
            ControladorProcessos.getInstance().incluir(getAgdCodigo(), processo);
        }
    }

}
