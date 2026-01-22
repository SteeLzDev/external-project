package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaLimpezaCoeficientesInativos;

/**
 * <p>Title: LimpezaCoeficientesInativosJob</p>
 * <p>Description: Tarefa de limpeza da tabela de coeficientes ativos, movendo os inativos para histórico..</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LimpezaCoeficientesInativosJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LimpezaCoeficientesInativosJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Limpeza de Coeficientes Inativos Job");
        ProcessoAgendado processo = new ProcessaLimpezaCoeficientesInativos(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
