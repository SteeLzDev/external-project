package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaNotificacaoEnvioArquivosFolha</p>
 * <p>Description: Executa envio de email de notificação de envio de arquivos da folha.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaNotificacaoEnvioArquivosFolha extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaNotificacaoEnvioArquivosFolha.class);

    public ProcessaNotificacaoEnvioArquivosFolha(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_DATA_PREVISTA_RETORNO, CodedValues.TPC_SIM, getResponsavel())) {
            LOG.info("Inicia envio de email de notificação de envio de arquivos da folha.");
            LOG.debug("Executa envio de email de notificação de envio de arquivos da folha.");
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            cseDelegate.enviaNotificacaoEnvioArquivosFolha(getResponsavel());
        }
    }
}
