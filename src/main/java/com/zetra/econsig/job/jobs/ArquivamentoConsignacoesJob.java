package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.agendado.ProcessaArquivamentoConsignacoes;

/**
 * <p>Title: ArquivamentoConsignacoesJob</p>
 * <p>Description: Tarefa de agendamento para arquivamento de consignações.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivamentoConsignacoesJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivamentoConsignacoesJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia arquivamento de consignações finalizadas.");
        ProcessaArquivamentoConsignacoes processo = new ProcessaArquivamentoConsignacoes(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
