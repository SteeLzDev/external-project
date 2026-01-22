package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;


/**
 * <p>Title: ProcessaDeferimentoAutomatico</p>
 * <p>Description: Processamento de Deferimento Automático de Consignações</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaDeferimentoAutomatico extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaDeferimentoAutomatico.class);

    public ProcessaDeferimentoAutomatico(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.debug("Executa deferimento automático de consignações");
        ConsignacaoDelegate delegate = new ConsignacaoDelegate();
        delegate.executarDeferimentoAutomatico(getResponsavel());
    }
}
