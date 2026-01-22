package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: ProcessaHistoricoLog</p>
 * <p>Description: Processamento de Criação de Histórico de Log</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaHistoricoLog extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaHistoricoLog.class);

    public ProcessaHistoricoLog(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Gera histórico de log periódico
        LOG.debug("Gera Histórico de Log");
        LogDelegate logDelegate = new LogDelegate();
        logDelegate.geraHistoricoLog(getResponsavel());
    }

}
