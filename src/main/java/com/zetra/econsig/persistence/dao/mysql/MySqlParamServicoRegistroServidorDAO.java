package com.zetra.econsig.persistence.dao.mysql;

import com.zetra.econsig.persistence.dao.generic.GenericParamServicoRegistroServidorDAO;

/**
 * <p>Title: ParamServicoRegistroServidorDAO</p>
 * <p>Description: Implementação para MySql do DAO de Parametros de serviço por regsitro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlParamServicoRegistroServidorDAO extends GenericParamServicoRegistroServidorDAO {
    /** Log object for this class. */
    // private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlParamServicoRegistroServidorDAO.class);

    /**
     * Copia todos os bloqueios de serviço do servidor antigo para o novo servidor
     * Rotina utilizada pela transferência de servidor.
     *
    public void copiaBloqueioSvc(String rseCodNovo, String rseCodAnt, String novoOrgao) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            StringBuilder query = new StringBuilder();
            String fields = "psr.tps_codigo" + MySqlDAOFactory.SEPARADOR
                          + "psr.svc_codigo" + MySqlDAOFactory.SEPARADOR
                          +  "'" + rseCodNovo + "'" + MySqlDAOFactory.SEPARADOR
                          + "psr.psr_vlr" + MySqlDAOFactory.SEPARADOR
                          + "psr.psr_obs" + MySqlDAOFactory.SEPARADOR
                          + "psr.psr_alterado_pelo_servidor" + MySqlDAOFactory.SEPARADOR;


            query.append(" INSERT INTO ").append(Columns.TB_PARAM_SERVICO_REGISTRO_SERVIDOR).append(" (tps_codigo, svc_codigo, rse_codigo, psr_vlr, psr_obs, psr_alterado_pelo_servidor, psr_data_cadastro) ");
            query.append(" SELECT ").append(fields).append("NOW()");
            query.append(" FROM ").append(Columns.TB_PARAM_SERVICO_REGISTRO_SERVIDOR).append(" psr ");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SERVICO_REGISTRO_SERVIDOR).append(" psrNovo ON (psr.tps_codigo = psrNovo.tps_codigo and psr.svc_codigo = psrNovo.svc_codigo and psrNovo.rse_codigo = '").append(rseCodNovo).append("') ");
            query.append(" WHERE psr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO).append("'");
            query.append(" AND psr.rse_codigo = '").append(rseCodAnt).append("'");
            query.append(" AND psrNovo.rse_codigo IS NULL");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            try {
                if (stat != null) {

                }
                if (conn != null) {

                }
            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }*/
}
