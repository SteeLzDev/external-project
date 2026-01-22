package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.CompraContratoDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaControleCompra</p>
 * <p>Description: Processamento de Controle de Compra</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaControleCompra extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaControleCompra.class);

    public ProcessaControleCompra(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        if (ParamSist.paramEquals(CodedValues.TPC_CONTROLE_DETALHADO_PROCESSO_COMPRA, CodedValues.TPC_SIM, getResponsavel())) {
            // Executa a rotina detalhada de process de compra, bloqueando consignatárias ou cancelando as operações
            CompraContratoDelegate prcDelegate = new CompraContratoDelegate();
            LOG.info("Executa Desbloqueio Automático de Consignatárias");
            prcDelegate.executarDesbloqueioAutomaticoConsignatarias(null, getResponsavel());
            LOG.info("Executa Bloqueio Automático de Consignatárias");
            prcDelegate.aplicarPenalidadesPrazosExcedidos(getResponsavel());
        }
    }
}
