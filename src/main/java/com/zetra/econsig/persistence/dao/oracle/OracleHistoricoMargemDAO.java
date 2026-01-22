package com.zetra.econsig.persistence.dao.oracle;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.HistoricoMargemDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;

/**
 * <p>Title: OracleHistoricoMargemDAO</p>
 * <p>Description: Implementação para Oracle do DAO de Historico de Margem</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleHistoricoMargemDAO implements HistoricoMargemDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleHistoricoMargemDAO.class);
    private static String TMP_TABLE_RSE_SEM_HISTORICO = "tmp_rse_sem_historico_periodo";

    /**
     * Inicia gravação do histórico de margem. Inclui para todos os registros
     * de servidores, para os órgãos e estabelecimentos informados, um registro
     * gravando a margem atual destes servidores.
     * @param orgCodigos: Lista com os códigos dos órgãos, se nulo considera todos os órgãos.
     * @param estCodigos: Lista com os códigos dos estabelecimentos, se nulo considera todos os estabelecimentos.
     * @param rseCodigos: Lista com os códigos de registro servidor, caso seja um recalculo de margem de alguns servidores.
     * @param operacao  : Operação de alteração da margem
     * @throws DAOException
     */
    @Override
    public String iniciarHistoricoMargem(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, OperacaoHistoricoMargemEnum operacao) throws DAOException {
        return iniciarHistoricoMargem(orgCodigos, estCodigos, rseCodigos, operacao, false);
    }

    protected String iniciarHistoricoMargem(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, OperacaoHistoricoMargemEnum operacao, boolean incluiCasoNaoExista) throws DAOException {
        final String hmpData = DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd HH:mm:ss");

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final StringBuilder query = new StringBuilder();
         try {
            // Se é um novo processamento, ou seja, que não se espera que a tabela do período
            // tenha registros úteis, executa a limpeza da tabela de histórico do período.
            // No Oracle é necessário pois um processamento não finalizado não remove os
            // registros da tabela.
            if (!incluiCasoNaoExista) {
                // Cláusula comum de localização de registros de acordo com os parâmetros: orgCodigos, estCodigos, rseCodigos
                final StringBuilder whereClause = new StringBuilder();
                if ((estCodigos != null) && (estCodigos.size() > 0)) {
                    whereClause.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos)");
                    queryParams.addValue("estCodigos", estCodigos);
                }
                if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
                    whereClause.append(" AND ").append(Columns.RSE_ORG_CODIGO).append(" IN (:orgCodigos)");
                    queryParams.addValue("orgCodigos", orgCodigos);
                }
                if ((rseCodigos != null) && (rseCodigos.size() > 0)) {
                    whereClause.append(" AND ").append(Columns.RSE_CODIGO).append(" IN (:rseCodigos)");
                    queryParams.addValue("rseCodigos", rseCodigos);
                }

                final StringBuilder joinClause = new StringBuilder();
                if ((estCodigos != null) && (estCodigos.size() > 0)) {
                    joinClause.append(" INNER JOIN ").append(Columns.TB_ORGAO).append(" ON (");
                    joinClause.append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
                }

                // Remove da tabela do período registros existentes de outros processamentos não completados
                query.setLength(0);
                query.append("DELETE FROM ").append(Columns.TB_HISTORICO_MARGEM_PERIODO);
                if (whereClause.length() > 0) {
                    query.append(" WHERE EXISTS (SELECT 1 FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
                    query.append(joinClause.toString());
                    query.append(" WHERE ").append(Columns.RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO);
                    query.append(whereClause.toString());
                    query.append(")");
                }

                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            // Desabilita os índices antes da inclusão do histórico de margem
            query.setLength(0);
            query.append("CALL disableIndexes('").append(Columns.TB_HISTORICO_MARGEM_PERIODO).append("')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            final StringBuilder fromClause = new StringBuilder();
            fromClause.append(" FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
            if ((estCodigos != null) && (estCodigos.size() > 0)) {
                fromClause.append(" INNER JOIN ").append(Columns.TB_ORGAO).append(" ON (");
                fromClause.append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
            }
            if (incluiCasoNaoExista) {
                fromClause.append(" INNER JOIN ").append(TMP_TABLE_RSE_SEM_HISTORICO).append(" ON (");
                fromClause.append(Columns.RSE_CODIGO).append(" = ").append(TMP_TABLE_RSE_SEM_HISTORICO).append(".RSE_CODIGO)");
            }

            final StringBuilder whereClause = new StringBuilder();
            whereClause.append(" WHERE (1 = 1)");
            if ((estCodigos != null) && (estCodigos.size() > 0)) {
                whereClause.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos)");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
                whereClause.append(" AND ").append(Columns.RSE_ORG_CODIGO).append(" IN (:orgCodigos)");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            if ((rseCodigos != null) && (rseCodigos.size() > 0)) {
                whereClause.append(" AND ").append(Columns.RSE_CODIGO).append(" IN (:rseCodigos)");
                queryParams.addValue("rseCodigos", rseCodigos);
            }

            for (int marCodigo = 1; marCodigo <= 4; marCodigo++) {
                query.setLength(0);
                query.append("SELECT MAX(").append(Columns.HMP_CODIGO).append(") FROM ").append(Columns.TB_HISTORICO_MARGEM_PERIODO);
                final int offset = Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Integer.class)).orElse(0);

                query.setLength(0);
                query.append("INSERT INTO ").append(Columns.TB_HISTORICO_MARGEM_PERIODO);
                query.append(" (HMP_CODIGO, RSE_CODIGO, HMP_DATA, HMP_OPERACAO, MAR_CODIGO, HMP_MARGEM_ANTES, HMP_MARGEM_DEPOIS)");
                query.append(" SELECT ROWNUM + ").append(offset).append(", ");
                query.append(Columns.RSE_CODIGO).append(", '").append(hmpData).append("', '").append(operacao.getCodigo()).append("', ");
                if (marCodigo == 1) {
                    query.append(CodedValues.INCIDE_MARGEM_SIM).append(", ");
                    query.append("COALESCE(").append(Columns.RSE_MARGEM_REST).append(", 0.00), ");
                    query.append("COALESCE(").append(Columns.RSE_MARGEM_REST).append(", 0.00) ");
                } else if (marCodigo == 2) {
                    query.append(CodedValues.INCIDE_MARGEM_SIM_2).append(", ");
                    query.append("COALESCE(").append(Columns.RSE_MARGEM_REST_2).append(", 0.00), ");
                    query.append("COALESCE(").append(Columns.RSE_MARGEM_REST_2).append(", 0.00) ");
                } else if (marCodigo == 3) {
                    query.append(CodedValues.INCIDE_MARGEM_SIM_3).append(", ");
                    query.append("COALESCE(").append(Columns.RSE_MARGEM_REST_3).append(", 0.00), ");
                    query.append("COALESCE(").append(Columns.RSE_MARGEM_REST_3).append(", 0.00) ");
                } else {
                    query.append(Columns.MRS_MAR_CODIGO).append(", ");
                    query.append("COALESCE(").append(Columns.MRS_MARGEM_REST).append(", 0.00), ");
                    query.append("COALESCE(").append(Columns.MRS_MARGEM_REST).append(", 0.00) ");
                }
                query.append(fromClause.toString());
                if (marCodigo == 4) {
                    query.append(" INNER JOIN ").append(Columns.TB_MARGEM_REGISTRO_SERVIDOR).append(" ON (");
                    query.append(Columns.RSE_CODIGO).append(" = ").append(Columns.MRS_RSE_CODIGO).append(")");
                }
                query.append(whereClause.toString());

                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            // Habilita novamente os índices após a carga
            query.setLength(0);
            query.append("CALL enableIndexes('").append(Columns.TB_HISTORICO_MARGEM_PERIODO).append("')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            return hmpData;
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public String iniciarHistoricoMargemCasoNaoExista(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, OperacaoHistoricoMargemEnum operacao) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            // Cria tabela temporária com
            final StringBuilder query = new StringBuilder();
            query.append("CALL dropTableIfExists('").append(TMP_TABLE_RSE_SEM_HISTORICO).append("')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createTemporaryTable('").append(TMP_TABLE_RSE_SEM_HISTORICO).append(" (RSE_CODIGO varchar2(32) NOT NULL, PRIMARY KEY (RSE_CODIGO))')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO ").append(TMP_TABLE_RSE_SEM_HISTORICO).append(" (RSE_CODIGO) ");
            query.append(" SELECT ").append(Columns.RSE_CODIGO);
            query.append(" FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
            if ((estCodigos != null) && (estCodigos.size() > 0)) {
                query.append(" INNER JOIN ").append(Columns.TB_ORGAO).append(" ON (");
                query.append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
            }
            query.append(" WHERE (1 = 1)");
            if ((estCodigos != null) && (estCodigos.size() > 0)) {
                query.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos)");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
                query.append(" AND ").append(Columns.RSE_ORG_CODIGO).append(" IN (:orgCodigos)");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            if ((rseCodigos != null) && (rseCodigos.size() > 0)) {
                query.append(" AND ").append(Columns.RSE_CODIGO).append(" IN (:rseCodigos)");
                queryParams.addValue("rseCodigos", rseCodigos);
            }
            query.append(" AND NOT EXISTS (SELECT 1 FROM ").append(Columns.TB_HISTORICO_MARGEM_PERIODO);
            query.append(" WHERE ").append(Columns.RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO).append(")");
            LOG.trace(query.toString());
            final int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            return iniciarHistoricoMargem(orgCodigos, estCodigos, rseCodigos, operacao, true);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void finalizarHistoricoMargem(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, OperacaoHistoricoMargemEnum operacao) throws DAOException {
        final String hmpData = DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd HH:mm:ss");

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            final StringBuilder query = new StringBuilder();

            // Cláusula comum de localização de registros de acordo com os parâmetros: orgCodigos, estCodigos, rseCodigos
            final StringBuilder whereClause = new StringBuilder();
            if ((estCodigos != null) && (estCodigos.size() > 0)) {
                whereClause.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos)");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
                whereClause.append(" AND ").append(Columns.RSE_ORG_CODIGO).append(" IN (:orgCodigos)");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            if ((rseCodigos != null) && (rseCodigos.size() > 0)) {
                whereClause.append(" AND ").append(Columns.RSE_CODIGO).append(" IN (:rseCodigos)");
                queryParams.addValue("rseCodigos", rseCodigos);
            }

            final StringBuilder joinClause = new StringBuilder();
            if ((estCodigos != null) && (estCodigos.size() > 0)) {
                joinClause.append(" INNER JOIN ").append(Columns.TB_ORGAO).append(" ON (");
                joinClause.append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
            }

            // 1) Atualiza o valor de margem depois, para os registros relativos às margens 1, 2 e 3,
            // fixas na tb_registro_servidor, com o valor após o processamento
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_HISTORICO_MARGEM_PERIODO).append(" SET ");
            query.append(Columns.HMP_DATA).append(" = '").append(hmpData).append("', ");
            query.append(Columns.HMP_MARGEM_DEPOIS).append(" = (");
            query.append(" SELECT CASE ");
            query.append(" WHEN ").append(Columns.HMP_MAR_CODIGO).append(" = 1 THEN COALESCE(").append(Columns.RSE_MARGEM_REST).append(", 0.00)");
            query.append(" WHEN ").append(Columns.HMP_MAR_CODIGO).append(" = 2 THEN COALESCE(").append(Columns.RSE_MARGEM_REST_2).append(", 0.00)");
            query.append(" WHEN ").append(Columns.HMP_MAR_CODIGO).append(" = 3 THEN COALESCE(").append(Columns.RSE_MARGEM_REST_3).append(", 0.00)");
            query.append(" END");
            query.append(" FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
            query.append(" WHERE ").append(Columns.RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO);
            query.append(")");
            query.append(" WHERE ").append(Columns.HMP_MAR_CODIGO).append(" IN (1, 2, 3) ");
            query.append(" AND EXISTS (");
            query.append(" SELECT 1 ");
            query.append(" FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
            query.append(joinClause.toString());
            query.append(" WHERE ").append(Columns.RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO);
            query.append(whereClause.toString());
            query.append(")");

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            if (!ParamSist.paramEquals(CodedValues.TPC_REGISTRA_HISTORICO_MARGEM_MESMO_SENDO_IGUAIS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                // 2) Exclui os registros de histórico de margem que não tiveram alteração,
                // referentes às margens 1, 2 e 3.
                query.setLength(0);
                query.append("DELETE FROM ").append(Columns.TB_HISTORICO_MARGEM_PERIODO);
                query.append(" WHERE ").append(Columns.HMP_MAR_CODIGO).append(" IN (1, 2, 3) ");
                query.append(" AND ").append(Columns.HMP_MARGEM_ANTES);
                query.append(" = ").append(Columns.HMP_MARGEM_DEPOIS);
                if (whereClause.length() > 0) {
                    query.append(" AND EXISTS (SELECT 1 FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
                    query.append(joinClause.toString());
                    query.append(" WHERE ").append(Columns.RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO);
                    query.append(whereClause.toString());
                    query.append(")");
                }

                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                // 3) Apaga os registros relativos às demais margens na tb_margem_registro_servidor
                // que não tiveram alteração, antes da atualização que é mais demorada.
                query.setLength(0);
                query.append("DELETE FROM ").append(Columns.TB_HISTORICO_MARGEM_PERIODO);
                query.append(" WHERE ").append(Columns.HMP_MAR_CODIGO).append(" NOT IN (1, 2, 3) ");
                query.append(" AND EXISTS (SELECT 1 FROM ").append(Columns.TB_MARGEM_REGISTRO_SERVIDOR);
                if (whereClause.length() > 0) {
                    query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR);
                    query.append(" ON (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.MRS_RSE_CODIGO).append(")");
                    query.append(joinClause.toString());
                }
                query.append(" WHERE ").append(Columns.MRS_RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO);
                query.append(" AND ").append(Columns.MRS_MAR_CODIGO).append(" = ").append(Columns.HMP_MAR_CODIGO);
                query.append(" AND COALESCE(").append(Columns.HMP_MARGEM_ANTES).append(", 0.00) = ");
                query.append(" COALESCE(").append(Columns.MRS_MARGEM_REST).append(", 0.00)");
                query.append(whereClause.toString());
                query.append(")");

                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            // 4) Atualiza os registros relativos às demais margens na tb_margem_registro_servidor
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_HISTORICO_MARGEM_PERIODO).append(" SET ");
            query.append(Columns.HMP_DATA).append(" = '").append(hmpData).append("', ");
            query.append(Columns.HMP_MARGEM_DEPOIS).append(" = (");
            query.append(" SELECT ").append(Columns.MRS_MARGEM_REST);
            query.append(" FROM ").append(Columns.TB_MARGEM_REGISTRO_SERVIDOR);
            query.append(" WHERE ").append(Columns.MRS_RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO);
            query.append(" AND ").append(Columns.MRS_MAR_CODIGO).append(" = ").append(Columns.HMP_MAR_CODIGO);
            query.append(")");
            query.append(" WHERE ").append(Columns.HMP_MAR_CODIGO).append(" NOT IN (1, 2, 3) ");
            query.append(" AND EXISTS (");
            query.append(" SELECT 1 ");
            query.append(" FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
            query.append(" INNER JOIN ").append(Columns.TB_MARGEM_REGISTRO_SERVIDOR);
            query.append(" ON (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.MRS_RSE_CODIGO).append(")");
            query.append(joinClause.toString());
            query.append(" WHERE ").append(Columns.MRS_RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO);
            query.append(" AND ").append(Columns.MRS_MAR_CODIGO).append(" = ").append(Columns.HMP_MAR_CODIGO);
            query.append(whereClause.toString());
            query.append(")");

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // 5) Move os dados da tabela do período para a tabela histórica
            query.setLength(0);
            query.append("INSERT INTO ").append(Columns.TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR);
            query.append(" (RSE_CODIGO, MAR_CODIGO, HMR_DATA, HMR_OPERACAO, HMR_MARGEM_ANTES, HMR_MARGEM_DEPOIS)");
            query.append(" SELECT ");
            query.append(Columns.HMP_RSE_CODIGO).append(", ");
            query.append(Columns.HMP_MAR_CODIGO).append(", ");
            query.append(Columns.HMP_DATA).append(", ");
            query.append(Columns.HMP_OPERACAO).append(", ");
            query.append(Columns.HMP_MARGEM_ANTES).append(", ");
            query.append(Columns.HMP_MARGEM_DEPOIS);
            query.append(" FROM ").append(Columns.TB_HISTORICO_MARGEM_PERIODO);
            if (whereClause.length() > 0) {
                query.append(" INNER JOIN ").append(Columns.TB_REGISTRO_SERVIDOR);
                query.append(" ON (").append(Columns.RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO).append(")");
                query.append(joinClause.toString());
                query.append(" WHERE (1 = 1)");
                query.append(whereClause.toString());
            }

            LOG.trace(query.toString());
            final int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // 6) Remove da tabela do período os registros já migrados
            query.setLength(0);
            query.append("DELETE FROM ").append(Columns.TB_HISTORICO_MARGEM_PERIODO);
            if (whereClause.length() > 0) {
                query.append(" WHERE EXISTS (SELECT 1 FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
                query.append(joinClause.toString());
                query.append(" WHERE ").append(Columns.RSE_CODIGO).append(" = ").append(Columns.HMP_RSE_CODIGO);
                query.append(whereClause.toString());
                query.append(")");
            }

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
