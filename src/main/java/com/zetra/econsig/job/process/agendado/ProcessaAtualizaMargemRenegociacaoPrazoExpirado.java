package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.consignacao.RenegociarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaAtualizaMargemRenegociacaoPrazoExpirado</p>
 * <p>Description: Processamento liberação de margem de contratos renegociados para menor.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaAtualizaMargemRenegociacaoPrazoExpirado extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaAtualizaMargemRenegociacaoPrazoExpirado.class);

    public ProcessaAtualizaMargemRenegociacaoPrazoExpirado(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        final AcessoSistema responsavel = getResponsavel();
        if (ParamSist.paramEquals(CodedValues.TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS, CodedValues.TPC_SIM, responsavel)) {
            final RenegociarConsignacaoController renegociarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean("renegociarConsignacaoController", RenegociarConsignacaoController.class);

            LOG.debug("Libera margem de contratos que tiveram a margem presa por ser de menor valor");
            renegociarConsignacaoController.liberaMargemRenegociacaoPrazoExpirado(responsavel);
        }
    }
}
