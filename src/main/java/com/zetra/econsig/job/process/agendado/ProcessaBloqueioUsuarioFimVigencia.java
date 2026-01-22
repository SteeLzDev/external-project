package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: ProcessaBloqueioUsuarioFimVigencia</p>
 * <p>Description: Processamento de Bloqueio de Usuários por fim de vigencia</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaBloqueioUsuarioFimVigencia extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaBloqueioUsuarioFimVigencia.class);

    public ProcessaBloqueioUsuarioFimVigencia(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.debug("Executa Bloqueio de Usuários por Fim de Vigencia");
        UsuarioDelegate usuDelegate = new UsuarioDelegate();
        usuDelegate.bloqueiaUsuariosFimVigencia(getResponsavel());
    }
}
