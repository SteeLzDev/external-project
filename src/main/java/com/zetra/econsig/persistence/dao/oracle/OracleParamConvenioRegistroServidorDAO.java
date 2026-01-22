package com.zetra.econsig.persistence.dao.oracle;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.generic.GenericParamConvenioRegistroServidorDAO;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: OracleParamConvenioRegistroServidorDAO</p>
 * <p>Description: Implementação para Oracle do DAO de Parametros de convênio por regsitro servidor</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleParamConvenioRegistroServidorDAO extends GenericParamConvenioRegistroServidorDAO {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleParamConvenioRegistroServidorDAO.class);

    /**
     * Corrige os bloqueios de convênio do servidor antigo para o novo servidor.
     * Rotina utilizada pela transferência de servidor.
     */
    @Override
    public void corrigeBloqueioServidor() throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            StringBuilder query = new StringBuilder();

            query.append(" INSERT INTO ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" (RSE_CODIGO, CNV_CODIGO, TPS_CODIGO, PCR_VLR, PCR_VLR_SER, PCR_VLR_CSA, PCR_VLR_CSE, PCR_OBS, PCR_DATA_CADASTRO) ");
            query.append(" SELECT ").append(Columns.RSE_CODIGO).append(", CNV_CERTO.CNV_CODIGO, PCR.TPS_CODIGO, PCR.PCR_VLR, PCR.PCR_VLR_SER, PCR.PCR_VLR_CSA, PCR.PCR_VLR_CSE, PCR.PCR_OBS, PCR.PCR_DATA_CADASTRO");
            query.append(" FROM ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" PCR ");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" CNV ON (PCR.CNV_CODIGO = CNV.CNV_CODIGO)");
            query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" ON (").append(Columns.RSE_CODIGO).append(" = PCR.RSE_CODIGO)");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" CNV_CERTO ON (CNV.CSA_CODIGO = CNV_CERTO.CSA_CODIGO AND CNV.SVC_CODIGO = CNV_CERTO.SVC_CODIGO AND CNV_CERTO.CNV_COD_VERBA = CNV.CNV_COD_VERBA AND CNV_CERTO.ORG_CODIGO = ").append(Columns.RSE_ORG_CODIGO).append(")");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" PCR_CERTO ON (PCR_CERTO.CNV_CODIGO = CNV_CERTO.CNV_CODIGO AND PCR_CERTO.RSE_CODIGO = ").append(Columns.RSE_CODIGO).append(" AND PCR_CERTO.TPS_CODIGO = PCR.TPS_CODIGO)");
            query.append(" WHERE CNV.ORG_CODIGO <> ").append(Columns.RSE_ORG_CODIGO).append(" AND PCR_CERTO.TPS_CODIGO IS NULL");

            String sql = DBHelper.applyTableNameRestriction(query.toString());
            LOG.trace(sql);
            jdbc.update(sql, queryParams);

            // TODO Otimizar - demora 30 minutos

            query.setLength(0);
            query.append(" DELETE FROM ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" WHERE EXISTS ( ");
            query.append(" SELECT 1 FROM ").append(Columns.TB_CONVENIO).append(" CNV ");
            query.append(" CROSS JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" RSE ");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" CNV_CERTO ON (CNV.CSA_CODIGO = CNV_CERTO.CSA_CODIGO AND CNV.SVC_CODIGO = CNV_CERTO.SVC_CODIGO AND CNV_CERTO.CNV_COD_VERBA = CNV.CNV_COD_VERBA AND CNV_CERTO.ORG_CODIGO = RSE.ORG_CODIGO) ");
            query.append(" WHERE CNV.CNV_CODIGO = ").append(Columns.PCR_CNV_CODIGO);
            query.append("   AND RSE.RSE_CODIGO = ").append(Columns.PCR_RSE_CODIGO);
            query.append("   AND CNV.ORG_CODIGO <> RSE.ORG_CODIGO)");

            sql = DBHelper.applyTableNameRestriction(query.toString());
            LOG.trace(sql);
            jdbc.update(sql, queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {


        }
    }
}
