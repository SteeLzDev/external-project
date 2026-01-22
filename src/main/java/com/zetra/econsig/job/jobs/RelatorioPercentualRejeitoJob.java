package com.zetra.econsig.job.jobs;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRelatorioPercentualRejeito;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: RelatorioPercentualRejeitoJob</p>
 * <p>Description: Trabalho para execução do Relatório de Percentual de Rejeito.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioPercentualRejeitoJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendado processo = new ProcessoRelatorioPercentualRejeito(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

    /**
     * Processo que será chamada ao ser executada o trabalho agendado (RelatorioPercentualRejeitoJob).
     */
    class ProcessoRelatorioPercentualRejeito extends ProcessoAgendadoPeriodico {

        public ProcessoRelatorioPercentualRejeito(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected void executa() throws ZetraException {
            Processo processo = new ProcessaRelatorioPercentualRejeito(getAgdCodigo(), getResponsavel());
            //Processo é executado ao invés de iniciado, pois o mesmo depende da Thread Pai.
            //O Processo Pai executa um fluxo antes e depois do término da execução do processo.
            //O Processo Pai que é gerenciado pelo ControladorProcessos.
            processo.run();
        }

    }

}
