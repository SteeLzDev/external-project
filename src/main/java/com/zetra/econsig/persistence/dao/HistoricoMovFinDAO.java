package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: HistoricoMovFinDAO</p>
 * <p>Description: Interface do DAO de Histórico de Movimento Financeiro</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface HistoricoMovFinDAO {
  public void limparTabelaArquivo() throws DAOException;
  public void atualizarCamposTabelaArquivo() throws DAOException;
  public void inserirHistoricoTabelaArquivo() throws DAOException;
}
