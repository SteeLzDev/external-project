package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaCancelamentoSenhasUsuarios;

/**
 * <p>Title: CancelamentoSenhasUsuariosJob</p>
 * <p>Description: Processo para cancelamento de senhas de usuários</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CancelamentoSenhasUsuariosJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelamentoSenhasUsuariosJob.class);

    @Override
    public void executar() {
        LOG.info("Início do Job de Cancelamento de Senhas de Usuários");
        ProcessoAgendado processo = new ProcessaCancelamentoSenhasUsuarios(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
