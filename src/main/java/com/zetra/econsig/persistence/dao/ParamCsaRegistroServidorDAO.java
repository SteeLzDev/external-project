package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ParamCsaRegistroServidorDAO</p>
 * <p>Description: Interface do DAO para os Parametros de consignatária por regsitro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ParamCsaRegistroServidorDAO {

    public void copiaBloqueioCsa(String rseCodNovo, String rseCodAnt) throws DAOException;
}
