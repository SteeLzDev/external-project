package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.job.process.agendado.ProcessaCarregamentoCalendarioFolhaAutomaticamente;

/**
 * <p>Title: CarregarCalendarioFolhaAutomaticamenteJob</p>
 * <p>Description: Carregar calendario folha automaticamente quando a quantidade de periodos é menor que anual</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CarregarCalendarioFolhaAutomaticamenteJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CarregarCalendarioFolhaAutomaticamenteJob.class);

    @Override
    public void executar() {
        LOG.info("Verifica a necessidade de preencher a calendario folha");
        ProcessoAgendadoPeriodico processo = new ProcessaCarregamentoCalendarioFolhaAutomaticamente(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
