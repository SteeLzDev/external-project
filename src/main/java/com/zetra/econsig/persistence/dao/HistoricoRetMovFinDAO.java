package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: HistoricoRetMovFinDAO</p>
 * <p>Description: Interface do DAO de Historico de Retorno</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface HistoricoRetMovFinDAO {

    public void iniciarHistoricoConclusaoRetorno(List<String> orgCodigos, List<String> estCodigos, String periodo, String chaveHistMargem) throws DAOException;
    public void finalizarHistoricoConclusaoRetorno(List<String> orgCodigos, List<String> estCodigos) throws DAOException;
    public void desfazerHistoricoConclusaoRetorno(List<String> orgCodigos, List<String> estCodigos, String periodo) throws DAOException;
    public void atualizaParcelaImportacaoHistorico(List<String> adeCodigoLista) throws DAOException;
}
