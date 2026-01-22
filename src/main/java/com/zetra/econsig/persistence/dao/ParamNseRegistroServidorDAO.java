package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ParamNseRegistroServidorDAO</p>
 * <p>Description: Interface do DAO para os Parametros de natureza de serviço por regsitro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ParamNseRegistroServidorDAO {

    public void copiaBloqueioNse(String rseCodNovo, String rseCodAnt) throws DAOException;
}
