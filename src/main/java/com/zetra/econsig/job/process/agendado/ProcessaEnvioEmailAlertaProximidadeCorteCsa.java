package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: ProcessaEnvioEmailAlertaProximidadeCorteCsa</p>
 * <p>Description: Verifica e envia email de alerta às consignatárias com data de corte próxima.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmailAlertaProximidadeCorteCsa extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmailAlertaProximidadeCorteCsa.class);

    public ProcessaEnvioEmailAlertaProximidadeCorteCsa(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.debug("Executa envio de email de alerta às consignatárias com data de corte próxima.");
        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
        csaDelegate.enviaEmailAlertaProximidadeCorte(getResponsavel());
    }
}
