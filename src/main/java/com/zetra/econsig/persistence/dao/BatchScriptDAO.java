package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: BatchScriptDAO </p>
 * <p>Description: DAO para execução de múltiplos SQLs</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface BatchScriptDAO {

    public void executeBatch(String batch) throws DAOException;
}
