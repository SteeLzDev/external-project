package com.zetra.econsig.persistence.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: RelatorioDAO</p>
 * <p>Description: Interface do DAO de Relatorio</p>
 * <p>Copyright: Copyright (c) 2003-2005</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface RelatorioDAO {

    public List<TransferObject> selectRepasse() throws DAOException;

    public String montaQueryRelatorioIntegracao(String csaCodigo, String chaveSeparador, String separarRelIntegracao, int sequencial, String nomeArqEntrada, MapSqlParameterSource queryParams);

    public String montaQueryRelatorioEstatistico(TransferObject criterio, Map<String, Object> parameters);

    List<TransferObject> executarQuerySubrelatorio(String sql, AcessoSistema responsavel) throws DAOException;

    public void pivotAux() throws DAOException;
}