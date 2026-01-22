package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.ambiente.ValidacaoAmbienteController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaValidacaoAmbiente</p>
 * <p>Description: Verifica se o ambiente está preparado para a execução do eConsig.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaValidacaoAmbiente extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaValidacaoAmbiente.class);

    public ProcessaValidacaoAmbiente(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.debug("Verifica se o ambiente está preparado para a execução do eConsig");
        ValidacaoAmbienteController validacaoAmbienteController = ApplicationContextProvider.getApplicationContext().getBean(ValidacaoAmbienteController.class);
        validacaoAmbienteController.verificarRegraValidacaoAmbiente(getResponsavel());
    }

}
