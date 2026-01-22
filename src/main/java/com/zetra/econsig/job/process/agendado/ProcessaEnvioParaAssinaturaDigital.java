package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaEnvioParaAssinaturaDigital</p>
 * <p>Description: Processo que verifica existência de anexos de solicitação a assinar digitalmente e os envia para o
 *                 serviço de assinatura digital.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioParaAssinaturaDigital extends ProcessoAgendadoPeriodico {

    private final AcessoSistema responsavel;

    public ProcessaEnvioParaAssinaturaDigital(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
        this.responsavel = responsavel;
    }

    @Override
    protected void executa() throws ZetraException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, getResponsavel())) {
            SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
            simulacaoController.assinarAnexosSolicitacaoAutorizacao(null, responsavel);
        }
    }

}
