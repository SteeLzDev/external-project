package com.zetra.econsig.job.process.agendado;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;

/**
 * <p>Title: ProcessaNotificacaoBloqueioUsuarioInatividade</p>
 * <p>Description: Processamento de Notificacao de Bloqueio de Usuários Inativos</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: ricardo.magno $
 * $Revision: 10790 $
 * $Date: 2010-04-19 15:50:06 -0300 (Seg, 19 abr 2010) $
 */
public class ProcessaNotificacaoBloqueioUsuarioInatividade extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaNotificacaoBloqueioUsuarioInatividade.class);

    public ProcessaNotificacaoBloqueioUsuarioInatividade(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        // Executa notificação de bloqueio automático de usuários inativos, se ainda não foi feito no dia
        LOG.debug("Executa Notificação de Bloqueio de Usuários Inativos");
        EnviaEmailHelper.enviaNotificacaoUsuariosPorTempoInatividade();
    }
}
