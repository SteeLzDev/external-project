package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.seguranca.SegurancaController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRemocaoOperacoesLiberacaoMargemPosPrazo</p>
 * <p>Description: Processo para remover operações de liberação de margem que não geraram bloqueio
 * e que tem data passada, não passível de gerarem bloqueios.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRemocaoOperacoesLiberacaoMargemPosPrazo extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRemocaoOperacoesLiberacaoMargemPosPrazo.class);

    public ProcessaRemocaoOperacoesLiberacaoMargemPosPrazo(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.info("Inicia processo para remover operações de liberação de margem que não são mais passíveis de bloqueio.");
        SegurancaController segurancaController = ApplicationContextProvider.getApplicationContext().getBean(SegurancaController.class);
        segurancaController.removerOperacoesLiberacaoMargemPosPrazo(getResponsavel());
    }
}
