package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ExportaMovimentoDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: ProcessaEnvioEmailDownloadNaoRealizadoMovFin</p>
 * <p>Description: Processamento de envio de email de alerta de download não realizado de Movimento Financeiro</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmailDownloadNaoRealizadoMovFin extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmailDownloadNaoRealizadoMovFin.class);

    public ProcessaEnvioEmailDownloadNaoRealizadoMovFin(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Executa envio de email de alerta de download não realizado de Movimento Financeiro
        LOG.debug("Executa envio de email de alerta de download não realizado de Movimento Financeiro.");
        ExportaMovimentoDelegate expDelegate = new ExportaMovimentoDelegate();
        expDelegate.enviarEmailDownloadNaoRealizadoMovFin(getResponsavel());
    }
}
