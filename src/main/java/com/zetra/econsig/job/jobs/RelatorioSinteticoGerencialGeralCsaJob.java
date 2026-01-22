package com.zetra.econsig.job.jobs;

import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRelatorioSinteticoGerencialGeralCsa;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.ProcessoAgendadoEventual;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;

/**
 * <p>Title: RelatorioSinteticoGerencialGeralCsaJob</p>
 * <p>Description: Trabalho para execução do Relatório Sintético Gerencial de Consignatária.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialGeralCsaJob extends AbstractJob {

    @Override
    public void executar() {
        final ProcessoAgendado processo = new ProcessoRelatorioSinteticoGerencialGeralCsa(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

    /**
     * Processo que será chamada ao ser executada o trabalho agendado (RelatorioSinteticoGerencialGeralCsaJob).
     */
    class ProcessoRelatorioSinteticoGerencialGeralCsa extends ProcessoAgendadoEventual {

        public ProcessoRelatorioSinteticoGerencialGeralCsa(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected void executa() throws ZetraException {
            final Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("sintetico_gerencial_csa");
            final Map<String, String[]> parameterMap = getParametrosAgendamento(getAgdCodigo());
            final Processo processo = new ProcessaRelatorioSinteticoGerencialGeralCsa(relatorio, parameterMap, getResponsavel());
            //Processo é executado ao invés de iniciado, pois o mesmo depende da Thread Pai.
            //O Processo Pai executa um fluxo antes e depois do término da execução do processo.
            //O Processo Pai que é gerenciado pelo ControladorProcessos.
            processo.run();
        }

    }

}
