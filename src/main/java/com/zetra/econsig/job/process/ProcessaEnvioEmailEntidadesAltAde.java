package com.zetra.econsig.job.process;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.OperacaoEConsigEnum;
/**
 * <p>Title: ProcessaEnvioEmailEntidadesAltAde</p>
 * <p>Description: Classe para processamento de envio de email para entidades relacionadas ao contrato
 *                 que estiverem configuradas para tal.
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmailEntidadesAltAde extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmailEntidadesAltAde.class);

    private final OperacaoEConsigEnum opEnum;
    private final String adeCodigo;
    private final String observacao;
    private final TransferObject motivoOperacao;
    private final AcessoSistema responsavel;

    public ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum opEnum, String adeCodigo, String observacao, TransferObject motivoOperacao, AcessoSistema responsavel) throws CloneNotSupportedException {
        this.opEnum = opEnum;
        this.adeCodigo = adeCodigo;
        this.observacao = observacao;
        this.motivoOperacao = motivoOperacao;
        this.responsavel = (AcessoSistema) responsavel.clone();
    }

    @Override
    protected void executar() {
        boolean enviaEmailAlertaAlteracaoAde = ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_ENTIDADES_QNDO_ALTERA_ADE, CodedValues.TPC_SIM, responsavel);

        if (enviaEmailAlertaAlteracaoAde) {
            LOG.info("ENVIANDO EMAIL ALERTA DE OPERAÇÃO: " + opEnum.getOperacao() + " REALIZADA");

            try {
                EnviaEmailHelper.enviarEmailAlteracaoAdePapDestinatarios(opEnum, adeCodigo, observacao, motivoOperacao, responsavel);
            } catch (Exception ex) {
                // exceção no envio de email não faz rollback na operação
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}
