package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioCsaNaoConfirmacaoLiquidacao;

/**
 * <p>Title: BloqueioCsaNaoConfirmarLiquidacaoJob</p>
 * <p>Description: Trabalho para bloqueio de consignatárias com solicitação de liquidação feita e não confirmada dentro do prazo.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: alexandrefernandes $
 * $Revision: 18703 $
 * $Date: 2015-01-28 15:41:56 -0200 (qua, 28 jan 2015) $
 */
public class BloqueioCsaNaoConfirmarLiquidacaoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueioCsaNaoConfirmarLiquidacaoJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Bloqueio de Consignatarias com solicitação de liquidação feita e não confirmada dentro do prazo Job");
        ProcessoAgendado processo = new ProcessaBloqueioCsaNaoConfirmacaoLiquidacao(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
