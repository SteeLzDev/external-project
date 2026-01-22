package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaExclusaoHistoricoOcorrenciaAgendamento;

/**
 * <p>Title: ExclusaoHistoricoOcorrenciaAgendamentoExternoJob</p>
 * <p>Description: Exclui histórico ocorrência agendamento quando o parâmetro define que a ocorrência já expirou</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExclusaoHistoricoOcorrenciaAgendamentoExternoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExclusaoHistoricoOcorrenciaAgendamentoExternoJob.class);

    @Override
    public void executar() {
        LOG.info("Verifica se existêm ocorrências no histórico de agendamento para serem excluídas de acordo com a configuração do parâmetro de sistema Job");
        final ProcessoAgendado processo = new ProcessaExclusaoHistoricoOcorrenciaAgendamento(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
