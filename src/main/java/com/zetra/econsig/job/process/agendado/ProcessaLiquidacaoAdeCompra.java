package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.CompraContratoDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaLiquidacaoAdeCompra</p>
 * <p>Description: Processamento de liquidação de contrato em compra ainda não liquidado</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaLiquidacaoAdeCompra extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaLiquidacaoAdeCompra.class);

    public ProcessaLiquidacaoAdeCompra(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // A liquidação automática deve ser feita caso o ciclo não seja fixo (275!=S) ou a liquidação seja permitida em qualquer passo (296=S).
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, getResponsavel()) &&
                (!ParamSist.paramEquals(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, CodedValues.TPC_SIM, getResponsavel()) ||
                        ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQ_CICLO_VIDA_FIXO_CSA_COR, CodedValues.TPC_SIM, getResponsavel()))) {

            // Verifica se a quantidade de dias para liquidação é maior que zero
            Object param = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_LIQUIDACAO_AUTOMATICA_ADE_COMPRA, getResponsavel());
            if (!TextHelper.isNull(param)) {
                int diasLiqAutomatica = Integer.parseInt(param.toString());
                if (diasLiqAutomatica > 0) {
                    // Executa a rotina para efetuar a liquidação das consignações com relacionamento de compra
                    // não liquidadas a mais de X dias
                    LOG.debug("Executa processo de liquidação contrato em compra");
                    CompraContratoDelegate prcDelegate = new CompraContratoDelegate();
                    prcDelegate.liquidarAdeCompraNaoLiquidada(diasLiqAutomatica, getResponsavel());
                }
            }
        }
    }
}
