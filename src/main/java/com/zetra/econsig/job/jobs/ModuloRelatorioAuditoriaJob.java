package com.zetra.econsig.job.jobs;

import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRelatorioAuditoria;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;

/**
 * <p>Title: ModuloRelatorioAuditoriaJob</p>
 * <p>Description: Trabalho para execução do Módulo de Relatório de Auditoria.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModuloRelatorioAuditoriaJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendado processo = new ProcessoModuloRelatorioAuditoria(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

    /**
     * Processo que será chamada ao ser executada o trabalho agendado (ModuloRelatorioAuditoriaJob).
     */
    class ProcessoModuloRelatorioAuditoria extends ProcessoAgendadoPeriodico {

        public ProcessoModuloRelatorioAuditoria(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected void executa() throws ZetraException {
            Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("auditoria");
            Map<String, String[]> parameterMap = getParametrosAgendamento(getAgdCodigo());
            HttpSession session = null;
            boolean moduloAuditoria = true;
            Processo processo = new ProcessaRelatorioAuditoria(relatorio, parameterMap, session, moduloAuditoria, getResponsavel());
            //Processo é executado ao invés de iniciado, pois o mesmo depende da Thread Pai.
            //O Processo Pai executa um fluxo antes e depois do término da execução do processo.
            //O Processo Pai que é gerenciado pelo ControladorProcessos.
            processo.run();
        }

    }

}
