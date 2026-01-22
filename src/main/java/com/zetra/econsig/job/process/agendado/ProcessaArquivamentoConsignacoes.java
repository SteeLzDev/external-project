package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaArquivamentoConsignacoes</p>
 * <p>Description: processo assíncrono que dispara arquivamento de consignações finalizadas.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaArquivamentoConsignacoes extends ProcessoAgendadoPeriodico {

    public ProcessaArquivamentoConsignacoes(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
        sistemaController.arquivarConsignacoesFinalizadas(getResponsavel());
    }
}
