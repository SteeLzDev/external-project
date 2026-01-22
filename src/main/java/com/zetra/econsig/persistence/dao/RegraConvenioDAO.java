package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
/**
 * <p>Title: RegraConvenioDAO</p>
 * <p>Description: Interface do DAO de Regra de Convênio, utilizado para
 * manipulação da tabela de regra de convênio.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface RegraConvenioDAO {

	public void insereRegrasConvenio(List<CustomTransferObject> listParams) throws DAOException;
}
