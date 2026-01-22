package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: ArquivoFaturamentoBeneficioDAO</p>
 * <p>Description: Interface do DAO de ArquivoFaturamentoBeneficio</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ArquivoFaturamentoBeneficioDAO {

    public void removerArquivoFaturamentoBeneficio(List<String> orgCodigos, List<String> estCodigos, String periodo) throws DAOException;

    public void inserirArquivoFaturamentoBeneficio(List<String> orgCodigos, List<String> estCodigos, String periodo) throws DAOException;

}
