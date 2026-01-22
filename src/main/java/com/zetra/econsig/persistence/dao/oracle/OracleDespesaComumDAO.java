package com.zetra.econsig.persistence.dao.oracle;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.persistence.dao.DespesaComumDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusDespesaComumEnum;

/**
 * <p>Title: MySqlDespesaComumDAO</p>
 * <p>Description: Implemetação para MySQL da Interface do DAO de Despesa Comum</p>
 * <p>Copyright: Copyright (c) 2003-13</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleDespesaComumDAO implements DespesaComumDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleDespesaComumDAO.class);

    @Override
    public void concluirDespesasComum(String periodoRetorno, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("usuCodigo", (responsavel != null ? responsavel.getUsuCodigo() : CodedValues.USU_CODIGO_SISTEMA));
        queryParams.addValue("odcObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.despesa.comum.concluida.data.final", responsavel));
        queryParams.addValue("periodoRetorno", periodoRetorno);

        try {
            int rows = 0;

            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO ").append(Columns.TB_OCORRENCIA_DESPESA_COMUM);
            query.append(" (ODC_CODIGO, TOC_CODIGO, USU_CODIGO, DEC_CODIGO, ODC_DATA, ODC_IP_ACESSO, ODC_OBS) ");
            query.append("SELECT ('Z' || ");
            query.append("TO_CHAR(CURRENT_TIMESTAMP, 'yymmddhh24miss') || ");
            query.append("TO_CHAR(DEC_DATA, 'yymmddhh24miss') || ");
            query.append("SUBSTR(LPAD(ROWNUM, 7, '0'), 1, 7)), ");
            query.append("'").append(CodedValues.TOC_CONCLUSAO_DESPESA_COMUM).append("', ");
            query.append(":usuCodigo, ");
            query.append("DEC_CODIGO, CURRENT_TIMESTAMP, ' ', :odcObs ");
            query.append("FROM ").append(Columns.TB_DESPESA_COMUM);
            query.append(" WHERE ");
            query.append(Columns.DEC_SDC_CODIGO).append(" = '").append(StatusDespesaComumEnum.ATIVO.getCodigo()).append("' AND ");
            query.append(Columns.DEC_DATA_FIM).append(" <= :periodoRetorno ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_DESPESA_COMUM).append(" SET ");
            query.append(Columns.DEC_SDC_CODIGO).append(" = '").append(StatusDespesaComumEnum.CONCLUIDO.getCodigo()).append("' ");
            query.append(" WHERE ");
            query.append(Columns.DEC_SDC_CODIGO).append(" = '").append(StatusDespesaComumEnum.ATIVO.getCodigo()).append("' AND ");
            query.append(Columns.DEC_DATA_FIM).append(" <= :periodoRetorno ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void desfazerConclusaoDespesasComum(String periodoRetorno) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodoRetorno", periodoRetorno);

        try {
            int rows = 0;

            LOG.debug("=== EXCLUI OCORRENCIA DE CONCLUSAO DE DESPESA COMUM");
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM ").append(Columns.TB_OCORRENCIA_DESPESA_COMUM);
            query.append(" WHERE EXISTS (SELECT 1 FROM ").append(Columns.TB_DESPESA_COMUM);
            query.append(" WHERE ").append(Columns.DEC_CODIGO).append(" = ").append(Columns.ODC_DEC_CODIGO).append(" AND ");
            query.append(Columns.DEC_SDC_CODIGO).append(" = '").append(StatusDespesaComumEnum.CONCLUIDO.getCodigo()).append("' AND ");
            query.append(Columns.DEC_DATA_FIM).append(" = :periodoRetorno) AND ");
            query.append(Columns.ODC_TOC_CODIGO).append(" = '").append(CodedValues.TOC_CONCLUSAO_DESPESA_COMUM).append("'");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            LOG.debug("=== DESFAZ CONCLUSAO DE DESPESA COMUM");
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_DESPESA_COMUM).append(" SET ");
            query.append(Columns.DEC_SDC_CODIGO).append(" = '").append(StatusDespesaComumEnum.ATIVO.getCodigo()).append("'");
            query.append(" WHERE ");
            query.append(Columns.DEC_SDC_CODIGO).append(" = '").append(StatusDespesaComumEnum.CONCLUIDO.getCodigo()).append("' AND ");
            query.append(Columns.DEC_DATA_FIM).append(" = :periodoRetorno ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {


        }
    }
}
