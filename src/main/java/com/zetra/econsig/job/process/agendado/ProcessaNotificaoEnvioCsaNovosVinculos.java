package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaNotificaoEnvioCsaNovosVinculos</p>
 * <p>Description: Classe de processamento para enviar notificação para as consignatárias caso exista um vinculo criado</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaNotificaoEnvioCsaNovosVinculos extends ProcessoAgendadoPeriodico {

    public ProcessaNotificaoEnvioCsaNovosVinculos(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
        consignatariaController.notificaCsaNovosVinculos(getResponsavel());
    }
}
