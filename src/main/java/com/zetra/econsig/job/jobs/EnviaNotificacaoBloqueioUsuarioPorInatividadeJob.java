package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaNotificacaoBloqueioUsuarioInatividade;

/**
 * <p>Title: EnviaNotificacaoBloqueioUsuarioPorInatividadeJob</p>
 * <p>Description: Processamento de Bloqueio de Usuários por Inatividade</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: $
 */
public class EnviaNotificacaoBloqueioUsuarioPorInatividadeJob extends AbstractJob {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviaNotificacaoBloqueioUsuarioPorInatividadeJob.class);

	@Override
	public void executar() {
		LOG.info("Inicia Notificação de Bloqueio de Usuários Inativos Job");
		ProcessoAgendado processo = new ProcessaNotificacaoBloqueioUsuarioInatividade(getAgdCodigo(), getResponsavel());
		processo.start();
		ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
	}

}
