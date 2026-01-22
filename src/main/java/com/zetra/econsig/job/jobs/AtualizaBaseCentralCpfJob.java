package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaAtualizacaoBaseCentralCpf;
import com.zetra.econsig.job.process.ProcessoAgendado;

/**
 * <p>Title: AtualizaBaseCentralCpfJob</p>
 * <p>Description: Trabalho para envio ao Centralizador do cadastro atualizado de CPFs.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtualizaBaseCentralCpfJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtualizaBaseCentralCpfJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia atualização de base central de CPFs.");
        ProcessoAgendado processo = new ProcessaAtualizacaoBaseCentralCpf(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
