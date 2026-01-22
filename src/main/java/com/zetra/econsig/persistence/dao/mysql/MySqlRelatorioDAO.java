package com.zetra.econsig.persistence.dao.mysql;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.generic.GenericRelatorioDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlRelatorioDAO</p>
 * <p>Description: Implementacao do DAO de Relatório para o MySql</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlRelatorioDAO extends GenericRelatorioDAO {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlRelatorioDAO.class);

    //  -------------------------------------------------------------
    /* RELATÓRIO DE REPASSE */
    @Override
    public List<TransferObject> selectRepasse() throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder sql = new StringBuilder();

            // Cria tabela que relaciona as linhas do arquivo de retorno às consignatárias e aos serviços correspondentes.
            sql.append("DROP TEMPORARY TABLE IF EXISTS TMP_LINHA_CSA_SVC");
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("CREATE TEMPORARY TABLE TMP_LINHA_CSA_SVC (");
            sql.append(" `NOME_ARQUIVO` varchar(100) NOT NULL,");
            sql.append(" `ID_LINHA` int(11) NOT NULL,");
            sql.append(" `CSA_CODIGO` varchar(32) NOT NULL default '',");
            sql.append(" `SVC_CODIGO` varchar(32) NOT NULL default '',");
            sql.append(" `CSA_ATIVO` smallint(6) default '1',");
            sql.append(" `SVC_ATIVO` smallint(6) default '1',");
            sql.append(" `SCV_CODIGO` varchar(32) NOT NULL default '',");
            sql.append(" `DUPLICADO` char(1) NOT NULL default '0',");
            sql.append(" KEY `IX_LINHA` (`NOME_ARQUIVO`, `ID_LINHA`),");
            sql.append(" KEY `IX_SVC_ATIVO` (`SVC_ATIVO`),");
            sql.append(" KEY `IX_CSA_ATIVO` (`CSA_ATIVO`),");
            sql.append(" KEY `IX_SCV_CODIGO` (`SCV_CODIGO`)");
            sql.append(" )");
            sql.append(" SELECT DISTINCT ").append(Columns.ART_NOME_ARQUIVO).append(MySqlDAOFactory.SEPARADOR);
            sql.append(Columns.ART_ID_LINHA).append(MySqlDAOFactory.SEPARADOR);
            sql.append(Columns.CNV_CSA_CODIGO).append(MySqlDAOFactory.SEPARADOR);
            sql.append(Columns.SVC_CODIGO).append(MySqlDAOFactory.SEPARADOR);
            sql.append(Columns.CSA_ATIVO).append(MySqlDAOFactory.SEPARADOR);
            sql.append(Columns.SVC_ATIVO).append(MySqlDAOFactory.SEPARADOR);
            sql.append(Columns.CNV_SCV_CODIGO);
            sql.append(" FROM ").append(Columns.TB_ARQUIVO_RETORNO);
            sql.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
            sql.append(Columns.CNV_COD_VERBA).append(" = ").append(Columns.ART_CNV_COD_VERBA).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" ON (");
            sql.append(Columns.CSA_CODIGO).append(" = ").append(Columns.CNV_CSA_CODIGO).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_SERVICO).append(" ON (");
            sql.append(Columns.SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_ORGAO).append(" ON (");
            sql.append(Columns.ORG_CODIGO).append(" = ").append(Columns.CNV_ORG_CODIGO).append(")");
            sql.append(" INNER JOIN ").append(Columns.TB_ESTABELECIMENTO).append(" ON (");
            sql.append(Columns.EST_CODIGO).append(" = ").append(Columns.ORG_EST_CODIGO).append(")");
            sql.append(" WHERE ").append(Columns.ART_SPD_CODIGO).append(" = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("'");
            sql.append(" AND ").append(Columns.EST_IDENTIFICADOR).append(" = IFNULL(").append(Columns.ART_EST_IDENTIFICADOR).append(", ").append(Columns.EST_IDENTIFICADOR).append(") ");
            sql.append(" AND ").append(Columns.ORG_IDENTIFICADOR).append(" = IFNULL(").append(Columns.ART_ORG_IDENTIFICADOR).append(", ").append(Columns.ORG_IDENTIFICADOR).append(") ");
            sql.append(" AND ").append(Columns.CSA_IDENTIFICADOR).append(" = IFNULL(").append(Columns.ART_CSA_IDENTIFICADOR).append(", ").append(Columns.CSA_IDENTIFICADOR).append(") ");
            sql.append(" AND ").append(Columns.SVC_IDENTIFICADOR).append(" = IFNULL(").append(Columns.ART_SVC_IDENTIFICADOR).append(", ").append(Columns.SVC_IDENTIFICADOR).append(") ");
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            // Remove das linhas duplicadas aquelas cujo CNV não está ativo.
            removeDuplicadas("CNV");

            // Remove das linhas duplicadas aquelas cujo SVC não está ativo.
            removeDuplicadas("SVC");

            // Remove das linhas duplicadas aquelas cuja CSA não está ativa.
            removeDuplicadas("CSA");

            // Remove o restante das linhas duplicadas.
            removeDuplicadas("TODAS");

            sql.append("DROP TEMPORARY TABLE IF EXISTS TMP_LINHAS_DUPLICADAS");
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            // Cria tabela que agrupa por CSA e taxa de interveniência.
            final StringBuilder fieldsDDL = new StringBuilder();
            fieldsDDL.append(Columns.ART_CNV_COD_VERBA).append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append(Columns.CSA_CODIGO).append(" AS CSA_CODIGO ").append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append(Columns.CSA_IDENTIFICADOR).append(" AS CSA_IDENTIFICADOR ").append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append(Columns.SVC_CODIGO).append(" AS SVC_CODIGO ").append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append(Columns.SVC_DESCRICAO).append(" AS SVC_DESCRICAO ").append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append("IFNULL(").append(Columns.CSA_NOME).append(", '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.desconhecida", (AcessoSistema) null)).append("') AS CSA_NOME ").append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append("IFNULL(IF(").append(Columns.PCV_FORMA_CALC).append(" = '").append(CodedValues.PCV_FORMA_VLR_FIXO).append("', ");
            fieldsDDL.append("CONCAT('").append(ApplicationResourcesHelper.getMessage("rotulo.moeda", (AcessoSistema) null)).append("', COALESCE(NULLIF(").append(Columns.PSC_VLR).append(",''), cast(").append(Columns.PCV_VLR).append(" as char))), ");
            fieldsDDL.append("CONCAT(COALESCE(NULLIF(").append(Columns.PSC_VLR).append(",''), cast(").append(Columns.PCV_VLR).append(" as char)), '%')), '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.isencao.taxa", (AcessoSistema) null)).append("') AS TAXA").append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append("REPLACE(SUM(").append(Columns.ART_PRD_VLR_REALIZADO).append("), ',', '') * 1 AS VALOR_APURADO").append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append("REPLACE(IFNULL(IF(").append(Columns.PCV_FORMA_CALC).append(" = '").append(CodedValues.PCV_FORMA_VLR_FIXO).append("', COUNT(");
            fieldsDDL.append(Columns.ART_ID_LINHA).append(") * COALESCE(NULLIF(").append(Columns.PSC_VLR).append(",''), cast(").append(Columns.PCV_VLR).append(" as char)), SUM(").append(Columns.ART_PRD_VLR_REALIZADO).append(") * ");
            fieldsDDL.append("COALESCE(NULLIF(").append(Columns.PSC_VLR).append(",''), cast(").append(Columns.PCV_VLR).append(" as char)) / 100), 0.0), ',', '') * 1 AS VALOR_TARIFACAO").append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append(" NULL AS ORDEM_EXTRA").append(MySqlDAOFactory.SEPARADOR);
            fieldsDDL.append("IF(").append(Columns.CSA_CODIGO).append(" IS NULL, '0', '1') AS ENCONTROU_CSA");

            final StringBuilder selectDDL = new StringBuilder();
            selectDDL.append(" SELECT ").append(fieldsDDL);
            selectDDL.append(" FROM ").append(Columns.TB_ARQUIVO_RETORNO);
            selectDDL.append(" LEFT OUTER JOIN TMP_LINHA_CSA_SVC TMP ON (TMP.NOME_ARQUIVO = ").append(Columns.ART_NOME_ARQUIVO);
            selectDDL.append(" AND TMP.ID_LINHA = ").append(Columns.ART_ID_LINHA).append(" AND TMP.DUPLICADO = '0')");
            selectDDL.append(" LEFT OUTER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" ON (");
            selectDDL.append(Columns.CSA_CODIGO).append(" = TMP.CSA_CODIGO)");
            selectDDL.append(" LEFT OUTER JOIN ").append(Columns.TB_SERVICO).append(" ON (");
            selectDDL.append(Columns.SVC_CODIGO).append(" = TMP.SVC_CODIGO)");
            selectDDL.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_TARIF_CONSIGNANTE).append(" ON (");
            selectDDL.append(Columns.PCV_CSE_CODIGO).append(" = '").append(CodedValues.CSE_CODIGO_SISTEMA).append("'");
            selectDDL.append(" AND ").append(Columns.PCV_TPT_CODIGO).append(" = '").append(CodedValues.TPT_VLR_INTERVENIENCIA).append("'");
            selectDDL.append(" AND ").append(Columns.PCV_ATIVO).append(" = '").append(CodedValues.STS_ATIVO).append("'");
            selectDDL.append(" AND ").append(Columns.PCV_BASE_CALC).append(" = '").append(CodedValues.PCV_BASE_PARCELA).append("'");
            selectDDL.append(" AND ").append(Columns.PCV_SVC_CODIGO).append(" = TMP.SVC_CODIGO)");
            selectDDL.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNATARIA);
            selectDDL.append(" ON (").append(Columns.PSC_TPS_CODIGO).append(" = '").append(CodedValues.TPS_VLR_INTERVENIENCIA).append("'");
            selectDDL.append(" AND ").append(Columns.PCV_SVC_CODIGO).append(" = ").append(Columns.PSC_SVC_CODIGO);
            selectDDL.append(" AND ").append(Columns.CSA_CODIGO).append(" = ").append(Columns.PSC_CSA_CODIGO).append(")");
            selectDDL.append(" WHERE ").append(Columns.ART_SPD_CODIGO).append(" = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("'");
            selectDDL.append(" GROUP BY ").append(Columns.ART_CNV_COD_VERBA).append(", ").append(Columns.CSA_CODIGO).append(", ").append(Columns.SVC_CODIGO);

            final StringBuilder sqlDDL = new StringBuilder();
            sqlDDL.append("DROP TEMPORARY TABLE IF EXISTS TMP_REL_REPASSE");
            LOG.trace(sqlDDL.toString());
            jdbc.update(sqlDDL.toString(), queryParams);
            sqlDDL.setLength(0);

            sqlDDL.append("CREATE TEMPORARY TABLE TMP_REL_REPASSE (");
            sqlDDL.append(" `CNV_COD_VERBA` varchar(32),");
            sqlDDL.append(" `CSA_CODIGO` varchar(32),");
            sqlDDL.append(" `CSA_IDENTIFICADOR` text,");
            sqlDDL.append(" `SVC_CODIGO` varchar(32),");
            sqlDDL.append(" `SVC_DESCRICAO` text,");
            sqlDDL.append(" `CSA_NOME` text,");
            sqlDDL.append(" `TAXA` varchar(100),");
            sqlDDL.append(" `VALOR_APURADO` decimal(13,2),");
            sqlDDL.append(" `VALOR_TARIFACAO` decimal(13,2),");
            sqlDDL.append(" `ORDEM_EXTRA` varchar(100),");
            sqlDDL.append(" `ENCONTROU_CSA` char(1)");
            sqlDDL.append(" )");
            sqlDDL.append(selectDDL);
            LOG.trace(sqlDDL.toString());
            jdbc.update(sqlDDL.toString(), queryParams);
            sqlDDL.setLength(0);

            // Cria tabela com os resultados duplicados, caso existam, para definir label da linha duplicada
            sqlDDL.append("DROP TEMPORARY TABLE IF EXISTS TMP_REL_REPASSE_DUPLICADO");
            LOG.trace(sqlDDL.toString());
            jdbc.update(sqlDDL.toString(), queryParams);
            sqlDDL.setLength(0);

            sqlDDL.append("CREATE TEMPORARY TABLE TMP_REL_REPASSE_DUPLICADO (");
            sqlDDL.append(" `CNV_COD_VERBA` varchar(32),");
            sqlDDL.append(" `CSA_IDENTIFICADOR` text,");
            sqlDDL.append(" `CSA_NOME` text,");
            sqlDDL.append(" `SVC_DESCRICAO` text");
            sqlDDL.append(" )");
            sqlDDL.append(" SELECT ");
            sqlDDL.append(Columns.ART_CNV_COD_VERBA).append(MySqlDAOFactory.SEPARADOR);
            sqlDDL.append("GROUP_CONCAT(DISTINCT ").append(Columns.CSA_IDENTIFICADOR).append(" SEPARATOR ' / ') AS CSA_IDENTIFICADOR, ");
            sqlDDL.append("GROUP_CONCAT(DISTINCT ").append(Columns.CSA_NOME).append(" SEPARATOR ' / ') AS CSA_NOME, ");
            sqlDDL.append("GROUP_CONCAT(distinct ").append(Columns.SVC_DESCRICAO).append(" SEPARATOR ' / ') as SVC_DESCRICAO ");
            sqlDDL.append(" FROM ").append(Columns.TB_ARQUIVO_RETORNO);
            sqlDDL.append(" INNER JOIN TMP_LINHA_CSA_SVC TMP ON (TMP.NOME_ARQUIVO = ").append(Columns.ART_NOME_ARQUIVO);
            sqlDDL.append(" AND TMP.ID_LINHA = ").append(Columns.ART_ID_LINHA).append(" AND TMP.DUPLICADO = '1')");
            sqlDDL.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA).append(" ON (");
            sqlDDL.append(Columns.CSA_CODIGO).append(" = TMP.CSA_CODIGO)");
            sqlDDL.append(" INNER JOIN ").append(Columns.TB_SERVICO).append(" ON (");
            sqlDDL.append(Columns.SVC_CODIGO).append(" = TMP.SVC_CODIGO)");
            sqlDDL.append(" WHERE ").append(Columns.ART_SPD_CODIGO).append(" = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("'");
            sqlDDL.append(" GROUP BY ").append(Columns.ART_CNV_COD_VERBA);
            LOG.trace(sqlDDL.toString());
            jdbc.update(sqlDDL.toString(), queryParams);
            sqlDDL.setLength(0);

            sqlDDL.append("UPDATE TMP_REL_REPASSE TMP1 ");
            sqlDDL.append("INNER JOIN TMP_REL_REPASSE_DUPLICADO TMP2 ON (TMP1.CNV_COD_VERBA = TMP2.CNV_COD_VERBA)");
            sqlDDL.append("SET TMP1.CSA_IDENTIFICADOR = TMP2.CSA_IDENTIFICADOR, TMP1.CSA_NOME = TMP2.CSA_NOME, TMP1.SVC_DESCRICAO = TMP2.SVC_DESCRICAO ");
            sqlDDL.append("WHERE TMP1.CSA_NOME = '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.desconhecida", (AcessoSistema) null)).append("'");
            LOG.trace(sqlDDL.toString());
            jdbc.update(sqlDDL.toString(), queryParams);
            sqlDDL.setLength(0);

            sqlDDL.append("DROP TEMPORARY TABLE IF EXISTS TMP_LINHA_CSA_SVC");
            LOG.trace(sqlDDL.toString());
            jdbc.update(sqlDDL.toString(), queryParams);
            sqlDDL.setLength(0);

            // Agrupa o resultado por CSA
            sql.append("SELECT CSA_IDENTIFICADOR").append(MySqlDAOFactory.SEPARADOR);
            sql.append("IF(IFNULL(PCS_VLR, 'N') = 'S' OR TMP_REL_REPASSE.CSA_CODIGO IS NULL, CONCAT(CSA_NOME, ' - ', IFNULL(SVC_DESCRICAO, cnv_cod_verba)), CSA_NOME) AS CSA_NOME").append(MySqlDAOFactory.SEPARADOR);
            sql.append("CAST(SUM(VALOR_APURADO) AS DECIMAL(13,2)) AS VALOR_APURADO").append(MySqlDAOFactory.SEPARADOR);
            sql.append("CAST(SUM(VALOR_TARIFACAO) AS DECIMAL(13,2)) AS VALOR_TARIFACAO").append(MySqlDAOFactory.SEPARADOR);
            sql.append("CAST((SUM(VALOR_APURADO) - SUM(VALOR_TARIFACAO)) AS DECIMAL(13,2)) AS VALOR_LIQUIDO").append(MySqlDAOFactory.SEPARADOR);
            sql.append("ORDEM_EXTRA");
            sql.append(" FROM ").append("TMP_REL_REPASSE ");
            sql.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_CONSIGNATARIA);
            sql.append(" ON (").append(Columns.PCS_CSA_CODIGO).append(" = TMP_REL_REPASSE.CSA_CODIGO");
            sql.append(" AND ").append(Columns.PCS_TPA_CODIGO).append(" = '").append(CodedValues.TPA_SEPARA_REPASSE_POR_VERBA).append("')");
            sql.append(" GROUP BY 2 ");
            sql.append(" ORDER BY 1 ");

            final String fieldNames = "CSA_IDENTIFICADOR" + MySqlDAOFactory.SEPARADOR
                    + "CSA_NOME" + MySqlDAOFactory.SEPARADOR
                    + "VALOR_APURADO" + MySqlDAOFactory.SEPARADOR
                    + "VALOR_TARIFACAO" + MySqlDAOFactory.SEPARADOR
                    + "VALOR_LIQUIDO" + MySqlDAOFactory.SEPARADOR
                    + "ORDEM_EXTRA";

            LOG.trace(sql.toString());
            final List<TransferObject> resultado = MySqlGenericDAO.getFieldsValuesList(queryParams, sql.toString(), fieldNames, MySqlDAOFactory.SEPARADOR);

            sqlDDL.append("DROP TEMPORARY TABLE IF EXISTS TMP_REL_REPASSE ");
            LOG.trace(sqlDDL.toString());
            jdbc.update(sqlDDL.toString(), queryParams);
            sqlDDL.setLength(0);

            return resultado;

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /* Método auxiliar para o relatório de repasse */
    private void criaTabelaLinhasDuplicadas () throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        sql.append("DROP TEMPORARY TABLE IF EXISTS TMP_LINHAS_DUPLICADAS");
        LOG.trace(sql.toString());
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);

        sql.append("CREATE TEMPORARY TABLE `TMP_LINHAS_DUPLICADAS` (");
        sql.append(" `NOME_ARQUIVO` varchar(100) NOT NULL,");
        sql.append(" `ID_LINHA` int(11) NOT NULL,");
        sql.append(" `QTDE` bigint(21) NOT NULL default '0',");
        sql.append(" KEY `IX_LINHA` (`NOME_ARQUIVO`, `ID_LINHA`)");
        sql.append(" )");
        sql.append(" SELECT NOME_ARQUIVO, ID_LINHA, COUNT(*) AS QTDE");
        sql.append(" FROM TMP_LINHA_CSA_SVC");
        sql.append(" GROUP BY NOME_ARQUIVO, ID_LINHA");
        sql.append(" HAVING QTDE > 1");
        LOG.trace(sql.toString());
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    /* Método auxiliar para o relatório de repasse */
    private void removeDuplicadas (String tipo) throws DataAccessException {
        criaTabelaLinhasDuplicadas ();

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder sql = new StringBuilder();

        if ("TODAS".equals(tipo)) {
            sql.append("UPDATE TMP_LINHA_CSA_SVC ");
            sql.append("INNER JOIN TMP_LINHAS_DUPLICADAS ON (TMP_LINHA_CSA_SVC.NOME_ARQUIVO = TMP_LINHAS_DUPLICADAS.NOME_ARQUIVO AND TMP_LINHA_CSA_SVC.ID_LINHA = TMP_LINHAS_DUPLICADAS.ID_LINHA) ");
            sql.append("SET DUPLICADO = '1'");

        } else {
            // Define o critério de acordo com o que deve ser excluído
            String criterio = "1 = 2";

            if ("SVC".equals(tipo)) {
                criterio = " TMP_LINHA_CSA_SVC.SVC_ATIVO <> '"  + CodedValues.STS_ATIVO + "'";
            } else if ("CSA".equals(tipo)) {
                criterio = " TMP_LINHA_CSA_SVC.CSA_ATIVO <> '"  + CodedValues.STS_ATIVO + "'";
            } else if ("CNV".equals(tipo)) {
                criterio = " TMP_LINHA_CSA_SVC.SCV_CODIGO <> '" + CodedValues.SCV_ATIVO + "'";
            }

            sql.append("DELETE FROM TMP_LINHA_CSA_SVC ");
            sql.append(" USING TMP_LINHA_CSA_SVC ");
            sql.append(" INNER JOIN TMP_LINHAS_DUPLICADAS ON (TMP_LINHA_CSA_SVC.NOME_ARQUIVO = TMP_LINHAS_DUPLICADAS.NOME_ARQUIVO AND TMP_LINHA_CSA_SVC.ID_LINHA = TMP_LINHAS_DUPLICADAS.ID_LINHA) ");
            sql.append(" WHERE ").append(criterio);
        }

        LOG.trace(sql.toString());
        jdbc.update(sql.toString(), queryParams);
        sql.setLength(0);
    }

    @Override
    public List<TransferObject> executarQuerySubrelatorio(String sql, AcessoSistema responsavel) throws DAOException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        final List<TransferObject> retornoQuery = new ArrayList<>();

        try {
            conn = DBHelper.makeConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);

            final int nrCampos = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                final CustomTransferObject linha = new CustomTransferObject();
                for (int i = 1; i <= nrCampos; i++) {
                    linha.setAttribute(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                }
                retornoQuery.add(linha);
            }
        } catch (final SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeResultSet(rs);
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
        return retornoQuery;
    }

    @Override
    public void pivotAux() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            final StringBuilder sql = new StringBuilder();

            sql.append("DROP TEMPORARY TABLE IF EXISTS tb_aux_pivot");
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("CREATE TEMPORARY TABLE tb_aux_pivot (");
            sql.append("a int ");
            sql.append(")");
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("INSERT INTO tb_aux_pivot VALUES (1), (2), (3) , (4), (5), (6), (7)");
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            // tabel auxiliar 2, pois não se pode reabrir a mesma tabela temporária
            sql.append("DROP TEMPORARY TABLE IF EXISTS tb_aux_pivot_1");
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("CREATE TEMPORARY TABLE tb_aux_pivot_1 (");
            sql.append("a int ");
            sql.append(")");
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("INSERT INTO tb_aux_pivot_1 VALUES (1), (2), (3) , (4), (5), (6), (7)");
            LOG.trace(sql.toString());
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
