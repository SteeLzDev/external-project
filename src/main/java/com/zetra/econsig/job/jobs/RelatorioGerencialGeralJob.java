package com.zetra.econsig.job.jobs;

import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRelatorioGerencialGeral;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.ProcessoAgendadoEventual;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;

/**
 * <p>Title: RelatorioGerencialGeralJob</p>
 * <p>Description: Trabalho para execução do Relatório Gerencial.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialGeralJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendado processo = new ProcessoRelatorioGerencial(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

    /**
     * Processo que será chamada ao ser executada o trabalho agendado (RelatorioGerencialGeralJob).
     */
    class ProcessoRelatorioGerencial extends ProcessoAgendadoEventual {

        public ProcessoRelatorioGerencial(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected void executa() throws ZetraException {
            Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("gerencial");
            Map<String, String[]> parameterMap = getParametrosAgendamento(getAgdCodigo());
            Processo processo = new ProcessaRelatorioGerencialGeral(relatorio, parameterMap, getResponsavel());
            //Processo é executado ao invés de iniciado, pois o mesmo depende da Thread Pai.
            //O Processo Pai executa um fluxo antes e depois do término da execução do processo.
            //O Processo Pai que é gerenciado pelo ControladorProcessos.
            processo.run();
        }

    }

}
