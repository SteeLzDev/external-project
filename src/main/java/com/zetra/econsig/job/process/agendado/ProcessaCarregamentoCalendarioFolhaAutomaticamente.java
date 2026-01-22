package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaCarregamentoCalendarioFolhaAutomaticamente</p>
 * <p>Description:Carrega automaticamente a tabela de calendario folha quando o sistema tem periodos menor que um ano preenchido</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaCarregamentoCalendarioFolhaAutomaticamente extends ProcessoAgendadoPeriodico {

    public ProcessaCarregamentoCalendarioFolhaAutomaticamente(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
        calendarioController.carregaCalendarioFolhaAutomatico(getResponsavel());
    }
}
