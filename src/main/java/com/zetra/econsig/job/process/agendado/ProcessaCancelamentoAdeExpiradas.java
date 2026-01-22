package com.zetra.econsig.job.process.agendado;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;


/**
 * <p>Title: ProcessaCancelamentoAdeExpiradas</p>
 * <p>Description: Processamento de Cancelamento Automático de Consignações</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaCancelamentoAdeExpiradas extends ProcessoAgendadoPeriodico {
    
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaCancelamentoAdeExpiradas.class);

    public ProcessaCancelamentoAdeExpiradas(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Se o parametro diz que o cancelamento automático é diário (de consignações
        // ou de solicitações), então executa rotina de cancelamento para todos os servidores
        List<String> sad = new ArrayList<String>();
        Object adeExpiradas = ParamSist.getInstance().getParam(CodedValues.TPC_CANC_AUT_DIARIO_CONSIGNACOES, getResponsavel());
        Object solExpiradas = ParamSist.getInstance().getParam(CodedValues.TPC_CANC_AUT_DIARIO_SOLICITACOES, getResponsavel());
        if (adeExpiradas != null && adeExpiradas.equals(CodedValues.TPC_SIM)) {
            sad.add(CodedValues.SAD_AGUARD_CONF);
            sad.add(CodedValues.SAD_AGUARD_DEFER);
        }
        if (solExpiradas != null && solExpiradas.equals(CodedValues.TPC_SIM)) {
            sad.add(CodedValues.SAD_SOLICITADO);
        }
        if (sad.size() > 0) {
            // Executa cancelamento automático de consignações
            LOG.debug("Executa Cancelamento Automático de Consignações");
            AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            adeDelegate.cancelaAdeExpiradas(sad, getResponsavel());
        }
    }
}
