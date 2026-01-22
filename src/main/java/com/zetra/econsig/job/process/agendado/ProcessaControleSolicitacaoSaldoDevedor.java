package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.SaldoDevedorDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.financiamentodivida.FinanciamentoDividaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaControleSolicitacaoSaldoDevedor</p>
 * <p>Description: Verifica se as solicitações de saldo devedor feitas pelo servidor estão sendo atendidas pelas
 * consignatárias. Se não foram atendidas, então bloqueia as consignatárias.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaControleSolicitacaoSaldoDevedor extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaControleSolicitacaoSaldoDevedor.class);

    public ProcessaControleSolicitacaoSaldoDevedor(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.debug("Verifica se as consignatárias devem ser bloqueadas por não atenderem às solicitações de saldo devedor feitas pelo servidor");
        SaldoDevedorDelegate sdvDelegate = new SaldoDevedorDelegate();
        sdvDelegate.verificarBloqueioCsaSolicitacaoSaldoDevedor(getResponsavel());
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, getResponsavel())) {
            LOG.debug("Verifica se as solicitações de propostas de pagamento ou as propostas de financiamento de dívida devem ser expiradas");
            FinanciamentoDividaController financiamentoDividaController = ApplicationContextProvider.getApplicationContext().getBean(FinanciamentoDividaController.class);
            financiamentoDividaController.processarPrazoExpiracaoFinancDivida(getResponsavel());
        }
    }
}
