package com.zetra.econsig.persistence.dao.mysql;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.generic.GenericParamConvenioRegistroServidorDAO;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlParamConvenioRegistroServidorDAO</p>
 * <p>Description: Implementação para MySql do DAO de Parametros de convênio por regsitro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlParamConvenioRegistroServidorDAO extends GenericParamConvenioRegistroServidorDAO {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlParamConvenioRegistroServidorDAO.class);

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
            query.append(" SELECT DISTINCT ").append(Columns.RSE_CODIGO).append(", CNV_CERTO.CNV_CODIGO, PCR.TPS_CODIGO, PCR.PCR_VLR, PCR.PCR_VLR_SER, PCR.PCR_VLR_CSA, PCR.PCR_VLR_CSE, PCR.PCR_OBS, PCR.PCR_DATA_CADASTRO");
            query.append(" FROM ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" PCR ");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" CNV ON (PCR.CNV_CODIGO = CNV.CNV_CODIGO)");
            query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" ON (").append(Columns.RSE_CODIGO).append(" = PCR.RSE_CODIGO)");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" CNV_CERTO ON (CNV.CSA_CODIGO = CNV_CERTO.CSA_CODIGO AND CNV.SVC_CODIGO = CNV_CERTO.SVC_CODIGO AND CNV_CERTO.CNV_COD_VERBA = CNV.CNV_COD_VERBA AND CNV_CERTO.ORG_CODIGO = ").append(Columns.RSE_ORG_CODIGO).append(")");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" PCR_CERTO ON (PCR_CERTO.CNV_CODIGO = CNV_CERTO.CNV_CODIGO AND PCR_CERTO.RSE_CODIGO = ").append(Columns.RSE_CODIGO).append(" AND PCR_CERTO.TPS_CODIGO = PCR.TPS_CODIGO)");
            query.append(" WHERE CNV.ORG_CODIGO <> ").append(Columns.RSE_ORG_CODIGO).append(" AND PCR_CERTO.TPS_CODIGO IS NULL");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append(" DELETE FROM PCR USING ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" PCR ");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" CNV ON (CNV.CNV_CODIGO = PCR.CNV_CODIGO)");
            query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" RSE ON (RSE.RSE_CODIGO = PCR.RSE_CODIGO)");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" CNV_CERTO ON (CNV.CSA_CODIGO = CNV_CERTO.CSA_CODIGO AND CNV.SVC_CODIGO = CNV_CERTO.SVC_CODIGO AND CNV_CERTO.CNV_COD_VERBA = CNV.CNV_COD_VERBA AND CNV_CERTO.ORG_CODIGO = RSE.ORG_CODIGO)");
            query.append(" WHERE CNV.ORG_CODIGO <> RSE.ORG_CODIGO");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {


        }
    }

    /**
     * Copia todos os bloqueios de convênio do servidor antigo para o novo servidor.
     * Rotina utilizada pela transferência de servidor.
     *
    @Override
    public void copiaBloqueioCnv(String rseCodNovo, String rseCodAnt, String novoOrgao) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            StringBuilder query = new StringBuilder();
            String fields = "rseNovo.rse_codigo" + MySqlDAOFactory.SEPARADOR
                          + "cnvNovo.cnv_codigo" + MySqlDAOFactory.SEPARADOR
                          + "pcr.tps_codigo" + MySqlDAOFactory.SEPARADOR
                          + "pcr.pcr_vlr" + MySqlDAOFactory.SEPARADOR
                          + "pcr.pcr_vlr_ser" + MySqlDAOFactory.SEPARADOR
                          + "pcr.pcr_vlr_csa" + MySqlDAOFactory.SEPARADOR
                          + "pcr.pcr_vlr_cse" + MySqlDAOFactory.SEPARADOR
                          + "pcr.pcr_obs" + MySqlDAOFactory.SEPARADOR;


            query.append(" INSERT INTO ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" (rse_codigo, cnv_codigo, tps_codigo, pcr_vlr, pcr_vlr_ser, pcr_vlr_csa, pcr_vlr_cse, pcr_obs, pcr_data_cadastro) ");
            query.append(" SELECT ").append(fields).append("NOW() AS pcr_data").append(" FROM ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" pcr ");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" cnvAntigo ON (pcr.cnv_codigo = cnvAntigo.cnv_codigo)");
            query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" rseAntigo ON (pcr.rse_codigo = rseAntigo.rse_codigo and rseAntigo.org_codigo = cnvAntigo.org_codigo)");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" cnvNovo ON (cnvNovo.svc_codigo = cnvAntigo.svc_codigo and cnvNovo.csa_codigo = cnvAntigo.csa_codigo)");
            query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR).append(" rseNovo ON (rseNovo.org_codigo = cnvNovo.org_codigo)");
            query.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_CNV_REGISTRO_SERVIDOR).append(" pcrNovo on (rseNovo.rse_codigo = pcrNovo.rse_codigo and cnvNovo.cnv_codigo = pcrNovo.cnv_codigo and pcr.tps_codigo = pcrNovo.tps_codigo) ");
            query.append(" WHERE pcr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("'");
            query.append(" AND rseAntigo.rse_codigo = '").append(rseCodAnt).append("'");
            query.append(" AND rseNovo.rse_codigo = '").append(rseCodNovo).append("'");
            query.append(" AND pcrNovo.rse_codigo IS NULL ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }*/
}
