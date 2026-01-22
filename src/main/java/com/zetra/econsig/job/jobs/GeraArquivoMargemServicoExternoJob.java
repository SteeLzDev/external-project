package com.zetra.econsig.job.jobs;

import com.zetra.econsig.job.AbstractJob;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaGeraArquivoMargemServicoExterno;

/**
 * <p>Title: GeraArquivoMargemServicoExternoJob</p>
 * <p>Description: Gera arquivo de margem (serviço externo) de acordo com data prevista de retorno</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GeraArquivoMargemServicoExternoJob extends AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GeraArquivoMargemServicoExternoJob.class);

    @Override
    public void executar() {
        LOG.info("Gera arquivo de margem (serviço externo) de acordo com data prevista de retorno Job");
        ProcessoAgendado processo = new ProcessaGeraArquivoMargemServicoExterno(getAgdCodigo(), getResponsavel());
        processo.start();
        ControladorProcessos.getInstance().incluir(getResponsavel().getUsuCodigo(), processo);
    }
}
