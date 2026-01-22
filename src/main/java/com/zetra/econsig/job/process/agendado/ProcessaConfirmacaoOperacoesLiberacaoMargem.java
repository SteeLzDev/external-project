package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.seguranca.SegurancaController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaConfirmacaoOperacoesLiberacaoMargem</p>
 * <p>Description: Processo para confirmar operações de liberação de margem que estão pendentes de confirmação.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ProcessaConfirmacaoOperacoesLiberacaoMargem extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaConfirmacaoOperacoesLiberacaoMargem.class);

    public ProcessaConfirmacaoOperacoesLiberacaoMargem(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.info("Inicia processo para confirmar operações de liberação de margem que estão pendentes de confirmação.");
        SegurancaController segurancaController = ApplicationContextProvider.getApplicationContext().getBean(SegurancaController.class);
        segurancaController.confirmarOperacoesLiberacaoMargem(getResponsavel());
    }
}
