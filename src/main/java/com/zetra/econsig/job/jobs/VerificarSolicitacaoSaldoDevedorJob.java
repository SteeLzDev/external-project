package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaControleSolicitacaoSaldoDevedor;

/**
 * <p>Title: VerificarSolicitacaoSaldoDevedorJob</p>
 * <p>Description: Tarefa para verificar se as consignatárias estão atendendo a solicitação de saldo devedor feita pelo servidor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerificarSolicitacaoSaldoDevedorJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VerificarSolicitacaoSaldoDevedorJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia a verificação de solicitações de saldo devedor feitas pelo servidor");
        ProcessoAgendadoPeriodico processo = new ProcessaControleSolicitacaoSaldoDevedor(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
