package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: LogDAO</p>
 * <p>Description: Interface do DAO de Log</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface LogDAO {

    public void geraHistoricoLog(AcessoSistema responsavel) throws DAOException;
}
