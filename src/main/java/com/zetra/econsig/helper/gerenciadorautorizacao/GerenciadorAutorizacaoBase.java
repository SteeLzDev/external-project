package com.zetra.econsig.helper.gerenciadorautorizacao;

import com.zetra.econsig.exception.GerenciadorAutorizacaoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: GerenciadorAutorizacaoBase</p>
 * <p>Description: Classe base para as classes de manuteção de autorização de modo a centralizar
 * métodos comuns e evitar a declaração de métodos vazios.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GerenciadorAutorizacaoBase implements GerenciadorAutorizacao {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GerenciadorAutorizacaoBase.class);

    public GerenciadorAutorizacaoBase() {
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.helper.gerenciadorautorizacao.GerenciadorAutorizacao#finalizarReservaMargem(java.lang.String, java.lang.Integer, boolean, com.zetra.econsig.helper.seguranca.AcessoSistema)
     */
    @Override
    public void finalizarReservaMargem(String adeCodigo, boolean apenasValidacao, AcessoSistema responsavel) throws GerenciadorAutorizacaoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.helper.gerenciadorautorizacao.GerenciadorAutorizacao#finalizarCancelamentoConsignacao(java.lang.String)
     */
    @Override
    public void finalizarCancelamentoConsignacao(String adeCodigo) throws GerenciadorAutorizacaoException {
        LOG.debug("Método não implementado.");
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.helper.gerenciadorautorizacao.GerenciadorAutorizacao#finalizarDeferimentoConsignacao(java.lang.String)
     */
    @Override
    public void finalizarDeferimentoConsignacao(String adeCodigo) throws GerenciadorAutorizacaoException {
        LOG.debug("Método não implementado.");
    }
}
