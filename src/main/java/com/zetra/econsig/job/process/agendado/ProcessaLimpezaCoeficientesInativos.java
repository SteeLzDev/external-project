package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.coeficiente.CoeficienteAtivoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaLimpezaCoeficientesInativos</p>
 * <p>Description: Processamento de Limpeza de Coeficientes Inativos</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaLimpezaCoeficientesInativos extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaLimpezaCoeficientesInativos.class);

    public ProcessaLimpezaCoeficientesInativos(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Executa a rotina de limpeza da tabela de coeficientes ativos, passando para a tabela de histórico as taxas
        // não mais ativas.
        LOG.info("Executa Limpeza de Coeficientes Inativos");
        CoeficienteAtivoController coeficienteAtivoController = ApplicationContextProvider.getApplicationContext().getBean(CoeficienteAtivoController.class);
        coeficienteAtivoController.limparCoeficientesInativos(getResponsavel());
    }
}
