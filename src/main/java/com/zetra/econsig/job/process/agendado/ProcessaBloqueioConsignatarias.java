package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: ProcessaBloqueioConsignatarias</p>
 * <p>Description: Processamento de Bloqueio de Consignatárias</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBloqueioConsignatarias extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioConsignatarias.class);

    public ProcessaBloqueioConsignatarias(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Executa cancelamento automático de consignações, se ainda não foi feito no dia
        LOG.debug("Executa Bloqueio de Consignatárias");
        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
        csaDelegate.bloqueiaCsaExpiradas(getResponsavel());
        LOG.debug("Executa Bloqueio de Convênios");
        ConvenioDelegate cnvDelegate = new ConvenioDelegate();
        cnvDelegate.bloquearConveniosExpirados(getResponsavel());
    }
}
