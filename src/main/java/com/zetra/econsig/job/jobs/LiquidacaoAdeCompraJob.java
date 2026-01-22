package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaLiquidacaoAdeCompra;

/**
 * <p>Title: LiquidacaoAdeCompraJob</p>
 * <p>Description: Trabalho para liquidação de contrato em compra ainda não liquidado.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LiquidacaoAdeCompraJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LiquidacaoAdeCompraJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Liquidação Contrato Compra Job");
        ProcessoAgendado processo = new ProcessaLiquidacaoAdeCompra(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
