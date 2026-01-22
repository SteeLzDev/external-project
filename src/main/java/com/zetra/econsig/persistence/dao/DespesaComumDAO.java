package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DespesaComumDAO</p>
 * <p>Description: Interface do DAO de Despesa Comum</p>
 * <p>Copyright: Copyright (c) 2003-13</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface DespesaComumDAO {

    /**
     * Realiza a conclusão das despesas comuns que atingiram a data final.
     * Altera o status da despesa e inclui ocorrência de conclusão.
     * @param periodoRetorno
     * @param responsavel
     * @throws DAOException
     */
    public void concluirDespesasComum(String periodoRetorno, AcessoSistema responsavel) throws DAOException;

    /**
     * Desfaz a conclusão das despesas comum do período, por causa da reversão do retorno.
     * Volta o status para ativo e remove ocorrências de conclusão.
     * @param periodoRetorno
     * @throws DAOException
     */
    public void desfazerConclusaoDespesasComum(String periodoRetorno) throws DAOException;
}
