package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.agendado.ProcessaCancelamentoCadastroServidor;

/**
 * <p>Title: CancelamentoCadastroServidorJob</p>
 * <p>Description: Trabalho para cancelamento automático de cadastro de servidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CancelamentoCadastroServidorJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelamentoCadastroServidorJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia o cancelamento dos cadastros do servidores pendentes de validação com prazo excedido.");
        ProcessaCancelamentoCadastroServidor processo = new ProcessaCancelamentoCadastroServidor(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
