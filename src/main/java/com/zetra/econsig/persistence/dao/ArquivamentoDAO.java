package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ArquivamentoDAO</p>
 * <p>Description: Interface do DAO de Arquivamento</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ArquivamentoDAO {

    public void arquivarConsignacoesFinalizadas(AcessoSistema responsavel) throws DAOException;
}
