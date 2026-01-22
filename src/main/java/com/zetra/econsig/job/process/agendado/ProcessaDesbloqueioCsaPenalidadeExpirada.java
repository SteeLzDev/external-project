package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaDesbloqueioCsaPenalidadeExpirada</p>
 * <p>Description: Processamento de desbloqueio de consignatárias com penalidade expirada</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaDesbloqueioCsaPenalidadeExpirada extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaDesbloqueioCsaPenalidadeExpirada.class);

    public ProcessaDesbloqueioCsaPenalidadeExpirada(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        AcessoSistema responsavel = getResponsavel();
        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

        if (ParamSist.paramEquals(CodedValues.TPC_DESBL_AUTOMAT_CSA_PRAZO_PENALIDADE, CodedValues.TPC_SIM, responsavel)) {
            // Executa desbloqueio de consignatárias com penalidade expirada
            LOG.debug("Executa desbloqueio de consignatárias com penalidade expirada");
            csaDelegate.desbloqueiaCsaPenalidadeExpirada();
        }

        // Executa desbloqueio de consignatárias com penalidade expirada
        LOG.debug("Executa desbloqueio de consignatárias com data de desbloqueio automático passada");
        csaDelegate.desbloqueiaCsaPrazoDesbloqAutomatico();
    }
}
