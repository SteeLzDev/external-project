package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaBloqueioCsaPendenteLiquidacaoAdePagaAnexo;

/**
 * <p>Title: BloqueioCsaLiquidacaoPendenteAdePagaAnexoJob</p>
 * <p>Description: Trabalho para bloqueio de consignatárias com liquidação pendente de contrato pago e com comprovante anexo</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioCsaLiquidacaoPendenteAdePagaAnexoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BloqueioCsaLiquidacaoPendenteAdePagaAnexoJob.class);

    @Override
    public void executar() {
        LOG.info("Inicia Bloqueio de Consignatarias com liquidação pendente de contrato pago e com comprovante anexo Job");
        ProcessoAgendado processo = new ProcessaBloqueioCsaPendenteLiquidacaoAdePagaAnexo(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }

}
