package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;

/**
 * <p>Title: HistoricoMargemDAO</p>
 * <p>Description: Interface do DAO de Historico de Margem</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface HistoricoMargemDAO {

    public String iniciarHistoricoMargem(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, OperacaoHistoricoMargemEnum operacao) throws DAOException;
 
    public String iniciarHistoricoMargemCasoNaoExista(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, OperacaoHistoricoMargemEnum operacao) throws DAOException;

    public void finalizarHistoricoMargem(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, OperacaoHistoricoMargemEnum operacao) throws DAOException;
}
