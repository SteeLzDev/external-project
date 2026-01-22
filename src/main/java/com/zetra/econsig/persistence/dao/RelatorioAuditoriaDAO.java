package com.zetra.econsig.persistence.dao;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.exception.DAOException;

/**
 * <p>Title: RelatorioAuditoriaDAO</p>
 * <p>Description: Interface DAO do Relatorio de Auditoria</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface RelatorioAuditoriaDAO {

    public void preparaDadosRelatorio(Map<String, Object> parametros) throws DAOException;

    public String select(Map<String, Object> parametros, MapSqlParameterSource queryParams) throws DAOException;

}
