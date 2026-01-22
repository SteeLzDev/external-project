package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: ProcessaEnvioEmailAlertaRetornoServidorCsaJob</p>
 * <p>Description: Classe para processamento da notificação de servidor/funcionário de licença com data de retorno próxima</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.kitagawa $
 * $Revision: 23454 $
 * $Date: 2018-01-05 11:35:34 -0200 (Sex, 05 Jan 2018) $
 */
public class ProcessaEnvioEmailAlertaRetornoServidorCsaJob extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmailAlertaRetornoServidorCsaJob.class);

    public ProcessaEnvioEmailAlertaRetornoServidorCsaJob(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.debug("Executa envio de email de alerta às consignatárias com servidor/funcionário de licença com data de retorno próxima.");
        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
        csaDelegate.enviarEmailAlertaRetornoServidor(getResponsavel());
    }
}
