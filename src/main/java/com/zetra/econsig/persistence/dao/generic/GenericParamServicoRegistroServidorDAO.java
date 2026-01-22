package com.zetra.econsig.persistence.dao.generic;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.ParamServicoRegistroServidorDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: GenericParamServicoRegistroServidorDAO</p>
 * <p>Description: Implementacao Genérica do DAO de parametros de serviço por
 * Registro Servidor. Instruções SQLs contidas aqui devem funcionar em todos
 * os SGDBs suportados pelo sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericParamServicoRegistroServidorDAO implements ParamServicoRegistroServidorDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericParamServicoRegistroServidorDAO.class);

    /**
     * Copia todos os bloqueios de serviço do servidor antigo para o novo servidor
     * Rotina utilizada pela transferência de servidor.
     * @param rseCodNovo : código do novo registro servidor
     * @param rseCodAnt  : código do antigo registro servidor
     */
    @Override
    public void copiaBloqueioSvc(String rseCodNovo, String rseCodAnt) throws DAOException {
        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final StringBuilder query = new StringBuilder();

            query.append(" INSERT INTO ").append(Columns.TB_PARAM_SERVICO_REGISTRO_SERVIDOR).append(" (tps_codigo, svc_codigo, rse_codigo, psr_vlr, psr_obs, psr_alterado_pelo_servidor, psr_data_cadastro) ");
            query.append(" SELECT psr.tps_codigo, psr.svc_codigo, :rseCodNovo, psr.psr_vlr, psr.psr_obs, psr.psr_alterado_pelo_servidor, :dataAtual ");
            query.append(" FROM ").append(Columns.TB_PARAM_SERVICO_REGISTRO_SERVIDOR).append(" psr ");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SERVICO_REGISTRO_SERVIDOR).append(" psrNovo ON (psr.tps_codigo = psrNovo.tps_codigo and psr.svc_codigo = psrNovo.svc_codigo and psrNovo.rse_codigo = :rseCodNovo) ");
            query.append(" WHERE psr.tps_codigo = :tpsCodigo ");
            query.append(" AND psr.rse_codigo = :rseCodAnt ");
            query.append(" AND psrNovo.rse_codigo IS NULL");

            queryParams.addValue("rseCodNovo", rseCodNovo);
            queryParams.addValue("rseCodAnt", rseCodAnt);
            queryParams.addValue("tpsCodigo", CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO);
            queryParams.addValue("dataAtual", DateHelper.getSystemDatetime());

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
