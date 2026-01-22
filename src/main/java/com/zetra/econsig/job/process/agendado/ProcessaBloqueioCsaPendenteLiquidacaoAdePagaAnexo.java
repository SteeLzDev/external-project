package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaBloqueioConsignatarias</p>
 * <p>Description: Processamento de Bloqueio de Consignatárias com liquidação pendente de contrato pago e com comprovante anexo</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBloqueioCsaPendenteLiquidacaoAdePagaAnexo extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioCsaPendenteLiquidacaoAdePagaAnexo.class);

    public ProcessaBloqueioCsaPendenteLiquidacaoAdePagaAnexo(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        AcessoSistema responsavel = getResponsavel();
        boolean permiteBloqCsaNaoLiqAdePagoAnexoSer = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, responsavel);
        if (permiteBloqCsaNaoLiqAdePagoAnexoSer) {
            // Executa cancelamento automático de consignações, se ainda não foi feito no dia
            LOG.debug("Executa Bloqueio de Consignatárias com liquidação pendente de contrato pago e com comprovante anexo");
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            csaDelegate.bloqueiaCsaSolicitacaoSaldoPagoComAnexoNaoLiquidado(getResponsavel());
        }
    }
}
