package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ResultadoRegraValidacaoMovimentoDAO</p>
 * <p>Description: Interface do DAO de Resultado de Regra de Validaçao de Movimento</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ResultadoRegraValidacaoMovimentoDAO {
    public List<TransferObject> selectResultadoRegras(String rvaCodigo) throws DAOException;
    public void deleteResultadoRegras(String rvaCodigo, String rvmCodigo) throws DAOException;
}
