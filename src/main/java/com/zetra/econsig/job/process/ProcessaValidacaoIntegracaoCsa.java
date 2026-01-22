package com.zetra.econsig.job.process;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.importacao.ValidaIntegracaoConsignataria;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ProcessaValidacaoIntegracaoCsa</p>
 * <p>Description: Classe a ser executada pela thread de processo agendado que executa validação de relatório de integração de consignatária.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaValidacaoIntegracaoCsa extends ProcessoAgendadoPeriodico {

    public ProcessaValidacaoIntegracaoCsa(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        ValidaIntegracaoConsignataria.validar();
    }
}
