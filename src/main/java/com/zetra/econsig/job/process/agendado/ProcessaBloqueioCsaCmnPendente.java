package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaBloqueioCsaCmnPendente</p>
 * <p>Description: processo assíncrono que dispara verificação de consignatárias a
 *                 bloquear por comunicação pendente.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBloqueioCsaCmnPendente extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioCsaCmnPendente.class);

    public ProcessaBloqueioCsaCmnPendente(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Executa bloqueio automática de consignatárias cujo prazo para tratamento de comunicações pendentes expirou.
        LOG.debug("Executa Bloqueio de Consignatárias");
        ComunicacaoController comunicacaoController = ApplicationContextProvider.getApplicationContext().getBean(ComunicacaoController.class);
        comunicacaoController.bloqueiaCsaPorCmnPendente(getResponsavel());
    }

}
