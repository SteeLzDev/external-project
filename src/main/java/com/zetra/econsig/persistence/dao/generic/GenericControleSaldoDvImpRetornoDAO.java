package com.zetra.econsig.persistence.dao.generic;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.persistence.dao.ControleSaldoDvImpRetornoDAO;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericControleSaldoDvImpRetornoDAO</p>
 * <p>Description: Implementacao Genérica do DAO de Controle de Saldo no Retorno. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericControleSaldoDvImpRetornoDAO extends GenericControleSaldoDevedorDAO implements ControleSaldoDvImpRetornoDAO {

    protected String getSufixo(List<String> orgCodigos, List<String> estCodigos, MapSqlParameterSource queryParams) {
        StringBuilder sufixo = new StringBuilder();
        if (estCodigos != null && estCodigos.size() > 0) {
            sufixo.append(" AND ").append(Columns.CNV_ORG_CODIGO).append(" IN (SELECT ");
            sufixo.append(Columns.ORG_CODIGO).append(" FROM ").append(Columns.TB_ORGAO).append(" WHERE ").append(Columns.ORG_EST_CODIGO);
            sufixo.append(" IN (:estCodigos))");
            queryParams.addValue("estCodigos", estCodigos);
        } else if (orgCodigos != null && orgCodigos.size() > 0) {
            sufixo.append(" AND ").append(Columns.CNV_ORG_CODIGO).append(" IN (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigos);
        }
        return sufixo.toString();
    }
}
