package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaExportacaoMovimentoOrgaoAutomaticamente;

/**
 * <p>Title: ExportarMovimentoOrgaoAutomaticamenteJob</p>
 * <p>Description: Tarefa para exportar o movimento por órgão automaticamente após a data de corte quando o sistema está com o parâmetro habilitado</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExportarMovimentoOrgaoAutomaticamenteJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportarMovimentoOrgaoAutomaticamenteJob.class);

    @Override
    public void executar() {
        LOG.info("Se o sistema exporta por órgão automaticamente verifica se passou a data de corte.");
        ProcessoAgendadoPeriodico processo = new ProcessaExportacaoMovimentoOrgaoAutomaticamente(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
