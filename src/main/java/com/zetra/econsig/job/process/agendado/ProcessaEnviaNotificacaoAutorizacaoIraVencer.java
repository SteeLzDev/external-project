package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

public class ProcessaEnviaNotificacaoAutorizacaoIraVencer extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnviaNotificacaoAutorizacaoIraVencer.class);

    public ProcessaEnviaNotificacaoAutorizacaoIraVencer(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        final AcessoSistema responsavel = getResponsavel();
        final Integer qtdeDiasVencimentoAutorizacao = ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VENCIMENTO_AUTORIZACAO_SER_MARGEM, 0, responsavel);
        if(qtdeDiasVencimentoAutorizacao > 0) {
            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);

            LOG.debug("Envia notificação que a autorização irá vencer");
            servidorController.enviarNotificacaoVencimentoAutorizacao(qtdeDiasVencimentoAutorizacao, responsavel);
        }
    }
}
