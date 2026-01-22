package com.zetra.econsig.job.jobs;

import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRelatorioServicoOperacaoMes;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.ProcessoAgendadoEventual;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;

/**
 * <p>Title: RelatorioServicoOperacaoMesJob</p>
 * <p>Description: Trabalho para execução do Relatório Serviço Operação por Mês.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioServicoOperacaoMesJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendado processo = new ProcessoRelatorioServicoOperacaoMes(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

    /**
     * Processo que será chamada ao ser executada o trabalho agendado (RelatorioServicoOperacaoMesJob).
     */
    class ProcessoRelatorioServicoOperacaoMes extends ProcessoAgendadoEventual {

        public ProcessoRelatorioServicoOperacaoMes(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected void executa() throws ZetraException {
            Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("servico_operacao");
            Map<String, String[]> parameterMap = getParametrosAgendamento(getAgdCodigo());
            Processo processo = new ProcessaRelatorioServicoOperacaoMes(relatorio, parameterMap, null, true, getResponsavel());
            //Processo é executado ao invés de iniciado, pois o mesmo depende da Thread Pai.
            //O Processo Pai executa um fluxo antes e depois do término da execução do processo.
            //O Processo Pai que é gerenciado pelo ControladorProcessos.
            processo.run();
        }

    }

}
