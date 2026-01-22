package com.zetra.econsig.persistence.dao.generic;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.ParamNseRegistroServidorDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericParamNseRegistroServidorDAO</p>
 * <p>Description: Implementacao Genérica do DAO de parametros de natureza de serviço por
 * Registro Servidor. Instruções SQLs contidas aqui devem funcionar em todos
 * os SGDBs suportados pelo sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericParamNseRegistroServidorDAO implements ParamNseRegistroServidorDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericParamNseRegistroServidorDAO.class);

    /**
     * Copia todos os bloqueios de natureza de serviço do servidor antigo para o novo servidor.
     * Rotina utilizada pela transferência de servidor.
     * @param rseCodNovo : código do novo registro servidor
     * @param rseCodAnt  : código do antigo registro servidor
     */
    @Override
    public void copiaBloqueioNse(String rseCodNovo, String rseCodAnt) throws DAOException {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();

            query.append(" INSERT INTO ").append(Columns.TB_PARAM_NSE_REGISTRO_SERVIDOR).append(" (tps_codigo, nse_codigo, rse_codigo, pnr_vlr, pnr_obs, pnr_alterado_pelo_servidor, pnr_data_cadastro) ");
            query.append(" SELECT pnr.tps_codigo, pnr.nse_codigo, :rseCodNovo, pnr.pnr_vlr, pnr.pnr_obs, pnr.pnr_alterado_pelo_servidor, :dataAtual ");
            query.append(" FROM ").append(Columns.TB_PARAM_NSE_REGISTRO_SERVIDOR).append(" pnr ");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_NSE_REGISTRO_SERVIDOR).append(" pnrNovo ON (pnr.tps_codigo = pnrNovo.tps_codigo and pnr.nse_codigo = pnrNovo.nse_codigo and pnrNovo.rse_codigo = :rseCodNovo) ");
            query.append(" WHERE pnr.tps_codigo = :tpsCodigo ");
            query.append(" AND pnr.rse_codigo = :rseCodAnt ");
            query.append(" AND pnrNovo.rse_codigo IS NULL");

            queryParams.addValue("rseCodNovo", rseCodNovo);
            queryParams.addValue("rseCodAnt", rseCodAnt);
            queryParams.addValue("tpsCodigo", CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO);
            queryParams.addValue("dataAtual", DateHelper.getSystemDatetime());

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
