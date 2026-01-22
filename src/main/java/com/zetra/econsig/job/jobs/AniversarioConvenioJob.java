package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.agendado.ProcessaAniversarioConvenio;

/**
 * <p>Title: AniversarioConvenioJob</p>
 * <p>Description: Criar mensagens automáticas para consignantes para data de aniversário do convênio</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AniversarioConvenioJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AniversarioConvenioJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia a criação de mensagem para data de aniversário do convênio.");
        ProcessaAniversarioConvenio processo = new ProcessaAniversarioConvenio(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
