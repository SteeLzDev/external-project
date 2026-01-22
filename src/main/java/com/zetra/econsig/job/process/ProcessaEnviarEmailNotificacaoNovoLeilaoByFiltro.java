package com.zetra.econsig.job.process;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;

/**
 * <p>Title: ProcessaEnviarEmailNotificacaoNovoLeilaoByFiltro</p>
 * <p>Description: Classe que dispara processo para enviar notificação ao criar novo leilão que enquadre em algum dos filtros</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnviarEmailNotificacaoNovoLeilaoByFiltro extends Processo {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnviarEmailNotificacaoNovoLeilaoByFiltro.class);

    private final AcessoSistema responsavel;
    private final SolicitacaoAutorizacao solicitacao;


    public ProcessaEnviarEmailNotificacaoNovoLeilaoByFiltro(SolicitacaoAutorizacao solicitacao, AcessoSistema responsavel) {
        this.solicitacao = solicitacao;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {
        try {
            AutorizacaoDelegate delegate = new AutorizacaoDelegate();
            CustomTransferObject ade = delegate.buscaAutorizacao(solicitacao.getAutDesconto().getAdeCodigo(), responsavel);
            if (ade != null) {
                EnviaEmailHelper.enviarEmailNotificacaoNovoLeilaoByFiltro(solicitacao, responsavel);
            }
        } catch (ViewHelperException | AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
