package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaEnvioParaAssinaturaDigital;

/**
 * <p>Title: EnvioArquivoAssinaturaDigitalJob</p>
 * <p>Description: Quartz Job para o processo de envio de anexos de solicitação para o serviço de assinatura digital.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnvioArquivoAssinaturaDigitalJob extends AbstractJob {

    @Override
    public void executar() {
        if (!ControladorProcessos.getInstance().processoAtivo(getAgdCodigo())) {
            ProcessoAgendadoPeriodico processo = new ProcessaEnvioParaAssinaturaDigital(getAgdCodigo(), getResponsavel());
            processo.start();
            ControladorProcessos.getInstance().incluir(getAgdCodigo(), processo);
        }
    }

}
