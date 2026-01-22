package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioCsaMensagemPendenteLeitura;

/**
 * <p>Title: BloqueioCsaMensagemNaoLidaJob</p>
 * <p>Description: Trabalho para bloqueio de consignatárias com mensagem pendente de confirmação de leitura.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioCsaMensagemNaoLidaJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueioCsaMensagemNaoLidaJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Bloqueio de Consignatarias com mensagem pendente de confirmacao de leitura Job");
        ProcessoAgendado processo = new ProcessaBloqueioCsaMensagemPendenteLeitura(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
