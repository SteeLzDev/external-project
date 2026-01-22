package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.relatorio.RelatorioHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: ProcessaExclusaoRelAntigos</p>
 * <p>Description: Processamento de Exclusão de Relatórios Antigos</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaExclusaoRelAntigos extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaExclusaoRelAntigos.class);

    public ProcessaExclusaoRelAntigos(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Executa cancelamento automático de consignações
        LOG.debug("Executa Exclusão de Relatórios Antigos");
        RelatorioHelper.executarLimpeza(getResponsavel());
    }

}
