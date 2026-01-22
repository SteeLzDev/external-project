package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaValidacaoIntegracaoCsa;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: ValidaRelatorioIntegracaoCsaJob</p>
 * <p>Description: Implementação de classe tarefa de processo agendado para validação de relatórios de integração de consignatária.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidaRelatorioIntegracaoCsaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidaRelatorioIntegracaoCsaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia a validação de relatórios de integração de consignatária");
        ProcessoAgendadoPeriodico processo = new ProcessaValidacaoIntegracaoCsa(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
