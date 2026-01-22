package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaArquivamentoServidores;
import com.zetra.econsig.job.process.ProcessoAgendado;

/**
 * <p>Title: ArquivamentoServidoresJob</p>
 * <p>Description: Trabalho para arquivamento de servidores.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivamentoServidoresJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivamentoServidoresJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia arquivamento de servidores.");
        ProcessoAgendado processo = new ProcessaArquivamentoServidores(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
