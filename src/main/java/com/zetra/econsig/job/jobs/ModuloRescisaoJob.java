package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaExportarArquivoRescisao;

/**
 * <p>Title: ModuloRescisaoJob</p>
 * <p>Description: Trabalho para geração do arquivo de movimento diário para o módulo de rescisão.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModuloRescisaoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ModuloRescisaoJob.class);

    @Override
    public void executar() {
        LOG.info("Gera Arquivo de Movimento de Rescisão Job");
        final ProcessoAgendado processo = new ProcessaExportarArquivoRescisao(getAgdCodigo(), false, getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
