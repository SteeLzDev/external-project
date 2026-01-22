package com.zetra.econsig.job.jobs;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaCopiaSegurancaExportar;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.ProcessoAgendadoEventual;

/**
 * <p>Title: BackupJob</p>
 * <p>Description: Tarefa para backup.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BackupJob extends AbstractJob {

    @Override
    public void executar() {
        ProcessoAgendado processo = new ProcessoBackup(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

    /**
     * Processo que será chamada ao ser executada o trabalho agendado (BackupJob).
     */
    class ProcessoBackup extends ProcessoAgendadoEventual {

        public ProcessoBackup(String agdCodigo, AcessoSistema responsavel) {
            super(agdCodigo, responsavel);
        }

        @Override
        protected void executa() throws ZetraException {
            Processo processo = new ProcessaCopiaSegurancaExportar(getResponsavel());
            //Processo é executado ao invés de iniciado, pois o mesmo depende da Thread Pai.
            //O Processo Pai executa um fluxo antes e depois do término da execução do processo.
            //O Processo Pai que é gerenciado pelo ControladorProcessos.
            processo.run();
        }
    }
}
