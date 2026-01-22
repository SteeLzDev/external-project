package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ParamServicoRegistroServidorDAO</p>
 * <p>Description: Interface do DAO para os Parametros de serviço por regsitro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ParamServicoRegistroServidorDAO {

    public void copiaBloqueioSvc(String rseCodNovo, String rseCodAnt) throws DAOException;
}
