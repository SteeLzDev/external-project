package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioCsaAdeSemMinAnexos;

/**
 * <p>Title: BloqueiaCsaAdeSemMinAnexosJob</p>
 * <p>Description: Trabalho para bloqueio de consignatárias com contratos realizados por usuário CSA/COR sem um número mínimo
 *                 de anexos definidos pelo parâmetro de serviço 284</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: alexandrefernandes $
 * $Revision: 17192 $
 * $Date: 2014-06-26 19:50:03 -0300 (qui, 26 jun 2014) $
 */
public class BloqueiaCsaAdeSemMinAnexosJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueiaCsaAdeSemMinAnexosJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia o cancelamento dos cadastros do servidores pendentes de validação com prazo excedido.");
        ProcessaBloqueioCsaAdeSemMinAnexos processo = new ProcessaBloqueioCsaAdeSemMinAnexos(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);

    }

}
