package com.zetra.econsig.job.process;

import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
/**
 * <p>Title: ProcessaEnvioEmailNotificaoReativacaoAdePrdRejeitada</p>
 * <p>Description: Classe para processamento de envio de email para consignante da reativação do contrato com parcela rejeitada.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmailNotificaoReativacaoAdePrdRejeitada extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmailNotificaoReativacaoAdePrdRejeitada.class);

    private final String adeCodigo;
    private final AcessoSistema responsavel;

    public ProcessaEnvioEmailNotificaoReativacaoAdePrdRejeitada(String adeCodigo, AcessoSistema responsavel) throws CloneNotSupportedException {
        this.adeCodigo = adeCodigo;
        this.responsavel = (AcessoSistema) responsavel.clone();
    }

    @Override
    protected void executar() {
        if (ParamSist.paramEquals(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, CodedValues.TPC_SIM, responsavel) && responsavel.isSer()) {
            LOG.info("ENVIANDO EMAIL ALERTA DE OPERAÇÃO REATIVAÇÃO DE CONTRATO COM PARCELA REJEITADA");
            try {
                EnviaEmailHelper.enviarEmailNotificacaoCseReativacaoPrdRejeitada(adeCodigo, responsavel);

            } catch (Exception ex) {
                // exceção no envio de email não faz rollback na operação
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}
