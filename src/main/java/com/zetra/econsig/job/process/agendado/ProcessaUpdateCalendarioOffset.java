package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaUpdateCalendarioOffset</p>
 * <p>Description: processo assíncrono que dispara atualização diário de tb_calendario.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaUpdateCalendarioOffset extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaUpdateCalendarioOffset.class);

    public ProcessaUpdateCalendarioOffset(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Executa bloqueio automática de consignatárias cujo prazo para tratamento de comunicações pendentes expirou.
        LOG.debug("Atualização da Tabela Calendário.");
        CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
        calendarioController.atualizaCalendarioOffset(DateHelper.getSystemDate(), getResponsavel());
    }

}
