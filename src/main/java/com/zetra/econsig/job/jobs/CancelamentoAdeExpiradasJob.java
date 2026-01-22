package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaCancelamentoAdeExpiradas;

/**
 * <p>Title: CancelamentoAdeExpiradasJob</p>
 * <p>Description: Trabalho para cancelamento automático de consignações</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CancelamentoAdeExpiradasJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelamentoAdeExpiradasJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Cancelamento Consignações Expiradas Job");
        ProcessoAgendado processo = new ProcessaCancelamentoAdeExpiradas(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
