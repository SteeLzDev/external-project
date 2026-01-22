package com.zetra.econsig.helper.gerenciadorautorizacao;

import java.io.Serializable;

import com.zetra.econsig.exception.GerenciadorAutorizacaoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: GerenciadorAutorizacao</p>
 * <p>Description: Interface de definição das classes de manutenção de autorização.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface GerenciadorAutorizacao extends Serializable {
    
    /**
     * Realiza operações adicionais imediatamente antes de concluir o processo de
     * reserva de margem.
     * @param adeCodigo Código da nova reserva.
     * @param apenasValidacao Indica se é apenas uma validação de reserva de margem.
     * @param responsavel Usuário responsável pela operação.
     * @throws GerenciadorAutorizacaoException
     */
    public abstract void finalizarReservaMargem(String adeCodigo, boolean apenasValidacao, AcessoSistema responsavel) throws GerenciadorAutorizacaoException;

    /**
     * Realiza operações adicionais imediatamente antes de concluir o processo de
     * cancelamento de contrato.
     * @param adeCodigo Código do contrato sendo cancelado.
     * @throws GerenciadorAutorizacaoException
     */
    public abstract void finalizarCancelamentoConsignacao(String adeCodigo) throws GerenciadorAutorizacaoException;

    /**
     * Realiza operações adicionais imediatamente antes de concluir o processo de
     * deferimento de contrato.
     * @param adeCodigo Código do contrato sendo cancelado.
     * @throws GerenciadorAutorizacaoException
     */
    public abstract void finalizarDeferimentoConsignacao(String adeCodigo) throws GerenciadorAutorizacaoException;
}
