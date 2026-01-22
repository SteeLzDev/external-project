package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ControleSaldoDvExpMovimentoDAO</p>
 * <p>Description: DAO para rotinas de controle de saldo devedor
 * efetuadas na exportação de movimento financeiro.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ControleSaldoDvExpMovimentoDAO {
    public boolean coeficientesCorrecaoAusentes() throws DAOException;

    public void ajustarSaldoDevedor() throws DAOException;

    public void corrigirSaldoDevedor() throws DAOException;

    public void ajustarAdeValor() throws DAOException;

    public void ajustarValorParcelasAbertas() throws DAOException;
}
