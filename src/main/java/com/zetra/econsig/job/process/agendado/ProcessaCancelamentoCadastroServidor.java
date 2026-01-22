package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

public class ProcessaCancelamentoCadastroServidor extends ProcessoAgendadoPeriodico {

    public ProcessaCancelamentoCadastroServidor(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {

        ServidorDelegate serDelegate = new ServidorDelegate();
        serDelegate.cancelarCadastroServidor(getResponsavel());
    }

}


