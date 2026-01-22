package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaEnvioEmailContratosReativacaoPendente</p>
 * <p>Description: Envia email para o servidor quando existem contratos suspensos por rejeito de parcela e com margem disponível.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmailContratosReativacaoPendente extends ProcessoAgendadoPeriodico {

    public ProcessaEnvioEmailContratosReativacaoPendente(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        if(ParamSist.getBoolParamSist(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, responsavel)) {
            ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            servidorController.notificaServidorContratosPendentesReativacao(responsavel);
        }
    }
}
