package com.zetra.econsig.job.jobs;

import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRelatorioGerencialGeralInternacional;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.ProcessoAgendadoEventual;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;

/**
 * <p>Title: RelatorioGerencialGeralInternacionalJob</p>
 * <p>Description: Trabalho para execução do Relatório Gerencial Internacional.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialGeralInternacionalJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendado processo = new ProcessoRelatorioGerencialInternacional(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

    /**
     * Processo que será chamada ao ser executada o trabalho agendado (RelatorioGerencialGeralInternacionalJob).
     */
    class ProcessoRelatorioGerencialInternacional extends ProcessoAgendadoEventual {

        public ProcessoRelatorioGerencialInternacional(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected void executa() throws ZetraException {
            Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("gerencial_internacional");
            Map<String, String[]> parameterMap = getParametrosAgendamento(getAgdCodigo());
            Processo processo = new ProcessaRelatorioGerencialGeralInternacional(relatorio, parameterMap, getResponsavel());
            //Processo é executado ao invés de iniciado, pois o mesmo depende da Thread Pai.
            //O Processo Pai executa um fluxo antes e depois do término da execução do processo.
            //O Processo Pai que é gerenciado pelo ControladorProcessos.
            processo.run();
        }

    }

}
