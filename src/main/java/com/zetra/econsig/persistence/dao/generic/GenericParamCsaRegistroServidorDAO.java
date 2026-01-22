package com.zetra.econsig.persistence.dao.generic;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.ParamCsaRegistroServidorDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericParamCsaRegistroServidorDAO</p>
 * <p>Description: Implementacao Genérica do DAO de parametros de contrato por
 * Registro Servidor. Instruções SQLs contidas aqui devem funcionar em todos
 * os SGDBs suportados pelo sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericParamCsaRegistroServidorDAO implements ParamCsaRegistroServidorDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericParamCsaRegistroServidorDAO.class);

    /**
     * Copia todos os bloqueios de consignatárias do servidor antigo para o novo servidor.
     * Rotina utilizada pela transferência de servidor.
     * @param rseCodNovo : código do novo registro servidor
     * @param rseCodAnt  : código do antigo registro servidor
     */
    @Override
    public void copiaBloqueioCsa(String rseCodNovo, String rseCodAnt) throws DAOException {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();

            query.append(" INSERT INTO ").append(Columns.TB_PARAM_CSA_REGISTRO_SERVIDOR).append(" (tpa_codigo, csa_codigo, rse_codigo, prc_vlr, prc_obs, prc_data_cadastro) ");
            query.append(" SELECT prc.tpa_codigo, prc.csa_codigo, :rseCodNovo, prc.prc_vlr, prc.prc_obs, :dataAtual ");
            query.append(" FROM ").append(Columns.TB_PARAM_CSA_REGISTRO_SERVIDOR).append(" prc ");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_CSA_REGISTRO_SERVIDOR).append(" prcNovo ON (prc.tpa_codigo = prcNovo.tpa_codigo and prc.csa_codigo = prcNovo.csa_codigo and prcNovo.rse_codigo = :rseCodNovo) ");
            query.append(" WHERE prc.tpa_codigo = :tpaCodigo ");
            query.append(" AND prc.rse_codigo = :rseCodAnt ");
            query.append(" AND prcNovo.rse_codigo IS NULL");

            queryParams.addValue("rseCodNovo", rseCodNovo);
            queryParams.addValue("rseCodAnt", rseCodAnt);
            queryParams.addValue("tpaCodigo", CodedValues.TPA_QTD_CONTRATOS_POR_CSA);
            queryParams.addValue("dataAtual", DateHelper.getSystemDatetime());

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
