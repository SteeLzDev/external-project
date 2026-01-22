package com.zetra.econsig.persistence.dao.generic;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.ResultadoRegraValidacaoMovimentoDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericResultadoRegraValidacaoMovimentoDAO</p>
 * <p>Description: Implementacao Genérica do DAO de Resultado de Validação de Movimento. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericResultadoRegraValidacaoMovimentoDAO implements ResultadoRegraValidacaoMovimentoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericResultadoRegraValidacaoMovimentoDAO.class);

    @Override
    public List<TransferObject> selectResultadoRegras(String rvaCodigo) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("rvaCodigo", rvaCodigo);

		final String fields = Columns.RVM_CODIGO + MySqlDAOFactory.SEPARADOR
    				+ Columns.RVM_IDENTIFICADOR + MySqlDAOFactory.SEPARADOR
    				+ Columns.RVM_DESCRICAO + MySqlDAOFactory.SEPARADOR
    				+ Columns.RVM_SEQUENCIA + MySqlDAOFactory.SEPARADOR
    				+ Columns.RRV_RESULTADO  + MySqlDAOFactory.SEPARADOR
    				+ Columns.RRV_VALOR_ENCONTRADO;

		final StringBuilder query = new StringBuilder();
		query.append("SELECT ").append(fields);
		query.append(" FROM ").append(Columns.TB_RESULTADO_REGRA_VALIDACAO_MOVIMENTO);
		query.append(" INNER JOIN ").append(Columns.TB_REGRA_VALIDACAO_MOVIMENTO);
		query.append(" ON (").append(Columns.RVM_CODIGO).append(" = ").append(Columns.RRV_RVM_CODIGO).append(")");
		query.append("  WHERE ").append(Columns.RRV_RVA_CODIGO).append(" = :rvaCodigo ");
		query.append(" ORDER BY ").append(Columns.RVM_SEQUENCIA);
		
		return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fields, MySqlDAOFactory.SEPARADOR);
    }

    @Override
    public void deleteResultadoRegras(String rvaCodigo, String rvmCodigo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
    	
    	try {
    		StringBuilder query = new StringBuilder();
    		query.append("DELETE FROM ").append(Columns.TB_RESULTADO_REGRA_VALIDACAO_MOVIMENTO);
    		query.append("  WHERE 1=1");
    		if (rvaCodigo != null) {
    			query.append("    AND ").append(Columns.RRV_RVA_CODIGO).append(" = :rvaCodigo ");
				queryParams.addValue("rvaCodigo", rvaCodigo);
    		}
    		if (rvmCodigo != null) {
    			query.append("    AND ").append(Columns.RRV_RVM_CODIGO).append(" = :rvmCodigo ");
				queryParams.addValue("rvmCodigo", rvmCodigo);
    		}

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
			
		} catch (final DataAccessException e) {
    		LOG.error(e.getMessage(), e);
    		throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, e);
    	}
    }
}
