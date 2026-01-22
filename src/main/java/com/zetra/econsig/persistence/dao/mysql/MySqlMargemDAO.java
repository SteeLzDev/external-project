package com.zetra.econsig.persistence.dao.mysql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorBaseDeDados;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.MargemDAO;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlMargemDAO</p>
 * <p>Description: Implementação para MySql do DAO de Margem.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlMargemDAO implements MargemDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlMargemDAO.class);

    @Override
    public void criaTabelaHistoricoRse(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        // Verifica se está habilitado o módulo de descontos de permissionário
        final boolean moduloSDP = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_SDP, AcessoSistema.getAcessoUsuarioSistema());

        final StringBuilder query = new StringBuilder();

        // Cria tabela temporária para armazenar os postos e tipos dos registros servidores
        query.setLength(0);
        query.append("drop temporary table if exists tmp_historico_registro_servidor");
        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);

        query.setLength(0);
        query.append("create temporary table tmp_historico_registro_servidor (");
        query.append(" rse_codigo varchar(32)");
        query.append(", srs_codigo varchar(32)");

        if (moduloSDP) {
            query.append(", trs_codigo_old varchar(32)");
            query.append(", pos_codigo_old varchar(32)");
        }

        query.append(", primary key (rse_codigo)");
        query.append(", key srs_idx (srs_codigo)");

        if (moduloSDP) {
            query.append(", key trs_idx (trs_codigo_old)");
            query.append(", key pos_idx (pos_codigo_old)");
        }

        query.append(")");
        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);

        // Insere os dados dos registros servidores selecionados
        query.setLength(0);
        query.append("insert into tmp_historico_registro_servidor (rse_codigo, srs_codigo");

        if (moduloSDP) {
            query.append(", trs_codigo_old, pos_codigo_old");
        }

        query.append(") ");
        query.append("select rse_codigo, srs_codigo ");

        if (moduloSDP) {
            query.append(", trs_codigo, pos_codigo ");
        }

        query.append("from tb_registro_servidor ");
        query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
        query.append("inner join tb_estabelecimento on (tb_estabelecimento.est_codigo = tb_orgao.est_codigo) ");
        query.append("where 1 = 1 ");

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            query.append(" and tb_orgao.org_codigo in (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        if ((estCodigos != null) && !estCodigos.isEmpty()) {
            query.append(" and tb_estabelecimento.est_codigo in (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
        }

        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);
    }

    @Override
    public void insereOcorrenciaRseStatusAlterados(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();
        try {
            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_rse_status_alterados");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_rse_status_alterados (");
            query.append(" rse_codigo varchar(32) not null, ");
            query.append(" srs_codigo varchar(32) not null, ");
            query.append(" srs_codigo_old varchar(32) DEFAULT NULL, ");
            query.append(" primary key (rse_codigo) ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_rse_status_alterados ");
            query.append("select ");
            query.append(Columns.RSE_CODIGO).append(MySqlDAOFactory.SEPARADOR);
            query.append(Columns.RSE_SRS_CODIGO).append(MySqlDAOFactory.SEPARADOR);
            query.append("tmp_historico_registro_servidor.srs_codigo as srs_codigo_old ");
            query.append("from tb_registro_servidor ");
            query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
            query.append("inner join tb_estabelecimento on (tb_estabelecimento.est_codigo = tb_orgao.est_codigo) ");
            query.append("left outer join tmp_historico_registro_servidor on (tb_registro_servidor.rse_codigo = tmp_historico_registro_servidor.rse_codigo) ");
            query.append("where 1 = 1 ");

            if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
                query.append(" and tb_orgao.org_codigo in (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }

            if ((estCodigos != null) && !estCodigos.isEmpty()) {
                query.append(" and tb_estabelecimento.est_codigo in (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }

            // Se não existe histórico do registro servidor, ou se o status do registro servidor é diferente do atual
            query.append(" and (tmp_historico_registro_servidor.srs_codigo is null or tb_registro_servidor.srs_codigo <> tmp_historico_registro_servidor.srs_codigo) ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Inserindo ocorrência para os registro de servidores que são novos.
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_registro_ser (ORS_CODIGO, TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, ORS_OBS, ORS_IP_ACESSO) ");
            query.append("SELECT CONCAT('I', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(rse_codigo, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'").append(CodedValues.TOC_RSE_INCLUSAO_POR_CARGA_MARGEM).append("', ");
            query.append("rse_codigo, ");
            query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', ");
            query.append("NOW(), :orsObs, :ipUsuario ");
            query.append(" FROM tb_tmp_rse_status_alterados tmp");
            query.append(" WHERE tmp.srs_codigo_old IS NULL AND tmp.srs_codigo = '").append(CodedValues.SRS_ATIVO).append("'");
            queryParams.addValue("orsObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.inclusao.carga.margem", responsavel));
            queryParams.addValue("ipUsuario", responsavel.getIpUsuario());
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Inserindo ocorrência para reativação de registor servidor
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_registro_ser (ORS_CODIGO, TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, ORS_OBS, ORS_IP_ACESSO) ");
            query.append("SELECT CONCAT('I', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(rse_codigo, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'").append(CodedValues.TOC_RSE_REATIVACAO_POR_CARGA_MARGEM).append("', ");
            query.append("rse_codigo, ");
            query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', ");
            query.append("NOW(), :orsObs, :ipUsuario ");
            query.append(" FROM tb_tmp_rse_status_alterados tmp");
            query.append(" WHERE tmp.srs_codigo_old IS NOT NULL ");
            query.append(" AND tmp.srs_codigo_old != tmp.srs_codigo ");
            query.append(" AND tmp.srs_codigo = '").append(CodedValues.SRS_ATIVO).append("'");
            queryParams.addValue("orsObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.reativacao.carga.margem", responsavel));
            queryParams.addValue("ipUsuario", responsavel.getIpUsuario());
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DAOException(ex);
        }
    }

    @Override
    public List<TransferObject> lstTabelaPostoTipoRseAlterados(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String fields = Columns.RSE_CODIGO + MySqlDAOFactory.SEPARADOR +
                        Columns.RSE_MATRICULA + MySqlDAOFactory.SEPARADOR +
                        Columns.TRS_CODIGO + MySqlDAOFactory.SEPARADOR +
                        Columns.POS_CODIGO + MySqlDAOFactory.SEPARADOR +
                        "trs_codigo_old" + MySqlDAOFactory.SEPARADOR +
                        "pos_codigo_old";

        final StringBuilder query = new StringBuilder();
        query.append("select ").append(fields).append(" ");
        query.append("from tb_registro_servidor ");
        query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
        query.append("inner join tb_estabelecimento on (tb_estabelecimento.est_codigo = tb_orgao.est_codigo) ");
        query.append("left outer join tb_tipo_registro_servidor on (tb_tipo_registro_servidor.trs_codigo = tb_registro_servidor.trs_codigo) ");
        query.append("left outer join tb_posto_registro_servidor on (tb_posto_registro_servidor.pos_codigo = tb_registro_servidor.pos_codigo) ");
        query.append("left outer join tmp_historico_registro_servidor on (tb_registro_servidor.rse_codigo = tmp_historico_registro_servidor.rse_codigo) ");
        query.append("where 1 = 1 ");

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            query.append(" and tb_orgao.org_codigo in (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        if ((estCodigos != null) && !estCodigos.isEmpty()) {
            query.append(" and tb_estabelecimento.est_codigo in (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
        }

        query.append(" and (");
        query.append("((tb_registro_servidor.trs_codigo <> tmp_historico_registro_servidor.trs_codigo_old) or (tb_registro_servidor.trs_codigo is null and tmp_historico_registro_servidor.trs_codigo_old is not null) or (tb_registro_servidor.trs_codigo is not null and tmp_historico_registro_servidor.trs_codigo_old is null)) or ");
        query.append("((tb_registro_servidor.pos_codigo <> tmp_historico_registro_servidor.pos_codigo_old) or (tb_registro_servidor.pos_codigo is null and tmp_historico_registro_servidor.pos_codigo_old is not null) or (tb_registro_servidor.pos_codigo is not null and tmp_historico_registro_servidor.pos_codigo_old is null))");
        query.append(") ");
        LOG.trace(query);

        return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fields, MySqlDAOFactory.SEPARADOR);
    }

    @Override
    public void criaAtualizaMargemExtraServidorBatch(List<MargemRegistroServidor> margensExtraAdd, String strPeriodo, String rseCodigo) throws UpdateException {
        Connection conn = null;
        PreparedStatement psAlteraQuandoExiste = null;
        PreparedStatement psInsereNaoExiste = null;
        PreparedStatement psHistoricoAlteraQuandoExiste = null;
        PreparedStatement psHistoricoInsereNaoExiste = null;
        final Session session = SessionUtil.getSession();
        final Date data = DateHelper.getSystemDatetime();
        try {

            final java.sql.Date periodo = DateHelper.toSQLDate(DateHelper.parse(strPeriodo, "yyyy-MM-dd"));
            final String alteraQuandoExiste = "UPDATE tb_margem_registro_servidor SET mrs_margem = ?, mrs_periodo_ini = ?, mrs_periodo_fim = ? WHERE rse_codigo = ? AND mar_codigo = ?";
            final String insereNaoExiste = "INSERT INTO tb_margem_registro_servidor (mar_codigo, rse_codigo, mrs_margem, mrs_margem_rest, mrs_margem_usada, mrs_periodo_ini, mrs_periodo_fim) SELECT ?, ?, ?, 0, 0, ?, ? WHERE NOT EXISTS (SELECT 1 FROM tb_margem_registro_servidor  WHERE rse_codigo = ? AND mar_codigo = ?)";
            final String historicoAlteraQuandoExiste = "UPDATE tb_historico_margem_folha SET hma_margem_folha = ?, hma_data = ? WHERE rse_codigo = ? AND mar_codigo = ? AND hma_periodo = ?";
            final String historicoInsereNaoExiste = "INSERT INTO tb_historico_margem_folha (rse_codigo, mar_codigo, hma_periodo, hma_data, hma_margem_folha) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS (SELECT 1 FROM tb_historico_margem_folha WHERE rse_codigo = ? AND mar_codigo = ? AND hma_periodo = ?)";

            conn = DBHelper.makeConnection();
            psAlteraQuandoExiste = conn.prepareStatement(alteraQuandoExiste);
            psInsereNaoExiste = conn.prepareStatement(insereNaoExiste);
            psHistoricoAlteraQuandoExiste = conn.prepareStatement(historicoAlteraQuandoExiste);
            psHistoricoInsereNaoExiste = conn.prepareStatement(historicoInsereNaoExiste);

            for (final MargemRegistroServidor margemRegistroServidor : margensExtraAdd) {
                // Atualiza quando existe
                psAlteraQuandoExiste.setBigDecimal(1, margemRegistroServidor.getMrsMargem());
                psAlteraQuandoExiste.setDate(2, margemRegistroServidor.getMrsPeriodoIni() != null ? DateHelper.toSQLDate(margemRegistroServidor.getMrsPeriodoIni()) : null);
                psAlteraQuandoExiste.setDate(3, margemRegistroServidor.getMrsPeriodoFim() != null ? DateHelper.toSQLDate(margemRegistroServidor.getMrsPeriodoFim()) : null);
                psAlteraQuandoExiste.setString(4, rseCodigo);
                psAlteraQuandoExiste.setShort(5, margemRegistroServidor.getMarCodigo());
                psAlteraQuandoExiste.addBatch();

                // Insere quando não existe
                psInsereNaoExiste.setShort(1, margemRegistroServidor.getMarCodigo());
                psInsereNaoExiste.setString(2, rseCodigo);
                psInsereNaoExiste.setBigDecimal(3, margemRegistroServidor.getMrsMargem());
                psInsereNaoExiste.setDate(4, margemRegistroServidor.getMrsPeriodoIni() != null ? DateHelper.toSQLDate(margemRegistroServidor.getMrsPeriodoIni()) : null);
                psInsereNaoExiste.setDate(5, margemRegistroServidor.getMrsPeriodoFim() != null ? DateHelper.toSQLDate(margemRegistroServidor.getMrsPeriodoFim()) : null);
                psInsereNaoExiste.setString(6, rseCodigo);
                psInsereNaoExiste.setShort(7, margemRegistroServidor.getMarCodigo());
                psInsereNaoExiste.addBatch();

                // Atualiza histórico quando existe
                psHistoricoAlteraQuandoExiste.setBigDecimal(1, margemRegistroServidor.getMrsMargem());
                psHistoricoAlteraQuandoExiste.setDate(2, new java.sql.Date(data.getTime()));
                psHistoricoAlteraQuandoExiste.setString(3, rseCodigo);
                psHistoricoAlteraQuandoExiste.setShort(4, margemRegistroServidor.getMarCodigo());
                psHistoricoAlteraQuandoExiste.setDate(5, periodo);
                psHistoricoAlteraQuandoExiste.addBatch();

                // Insere histórico quando não existe
                psHistoricoInsereNaoExiste.setString(1, rseCodigo);
                psHistoricoInsereNaoExiste.setShort(2, margemRegistroServidor.getMarCodigo());
                psHistoricoInsereNaoExiste.setDate(3, periodo);
                psHistoricoInsereNaoExiste.setDate(4, new java.sql.Date(data.getTime()));
                psHistoricoInsereNaoExiste.setBigDecimal(5, margemRegistroServidor.getMrsMargem());
                psHistoricoInsereNaoExiste.setString(6, rseCodigo);
                psHistoricoInsereNaoExiste.setShort(7, margemRegistroServidor.getMarCodigo());
                psHistoricoInsereNaoExiste.setDate(8, periodo);
                psHistoricoInsereNaoExiste.addBatch();
            }

            psAlteraQuandoExiste.executeBatch();
            psInsereNaoExiste.executeBatch();
            psHistoricoAlteraQuandoExiste.executeBatch();
            psHistoricoInsereNaoExiste.executeBatch();

            session.flush();
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
            DBHelper.closeStatement(psAlteraQuandoExiste);
            DBHelper.closeStatement(psInsereNaoExiste);
            DBHelper.closeStatement(psHistoricoAlteraQuandoExiste);
            DBHelper.closeStatement(psHistoricoInsereNaoExiste);
            DBHelper.releaseConnection(conn);
        }
    }

    @Override
    public void criaTabelaArquivoMargem(List<Map<String, Object>> dados, String nomeArqConfMargemSaida, String nomeArqConfMargemEntrada, String nomeArqConfMargemTradutor, String nomeArquivoFinal,  AcessoSistema responsavel) throws UpdateException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        Connection conn = null;
        PreparedStatement statement = null;
        final Session session = SessionUtil.getSession();
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder query = new StringBuilder();
            final Map<String, Object> keys = dados.get(0);

            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_arquivo_margem_servico_externo");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_arquivo_margem_servico_externo (");
            query.append(" ams_codigo INT NOT NULL AUTO_INCREMENT ");
            for (final String key : keys.keySet()) {
                if(key.contains("RSE_MARGEM")) {
                    query.append(" , " + key + " decimal(13,2) ");
                } else {
                    query.append(" , " + key + " varchar(255) ");
                }
            }
            query.append(", primary key (ams_codigo)");
            query.append(", key ams_matricula_idx (rse_matricula)");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            final StringBuilder queryInsere = new StringBuilder();
            queryInsere.append("INSERT INTO tb_tmp_arquivo_margem_servico_externo (");
            keys.keySet().forEach(k -> queryInsere.append(k + ","));
            queryInsere.deleteCharAt(queryInsere.length() - 1);
            queryInsere.append(") VALUES (");
            keys.keySet().forEach(k -> queryInsere.append("?,"));
            queryInsere.deleteCharAt(queryInsere.length() - 1);
            queryInsere.append(")");

            statement = conn.prepareStatement(queryInsere.toString());
            for (final Map<String, Object> valorCampos : dados) {
                int index = 1;
                for (final String chave : valorCampos.keySet()) {
                    if(chave.contains("RSE_MARGEM")) {
                        statement.setBigDecimal(index++, new BigDecimal(String.valueOf(valorCampos.get(chave))));
                    } else {
                        statement.setString(index++, String.valueOf(valorCampos.get(chave)));
                    }
                }
                statement.addBatch();
            }

            statement.executeBatch();
            session.flush();

            final EscritorArquivoTexto escritorArquivoTexto = new EscritorArquivoTexto(nomeArqConfMargemSaida, nomeArquivoFinal);
            final Leitor leitor = new LeitorBaseDeDados(nomeArqConfMargemEntrada, "select * from tb_tmp_arquivo_margem_servico_externo");
            final Tradutor tradutor = new Tradutor(nomeArqConfMargemTradutor, leitor, escritorArquivoTexto);
            tradutor.traduz();
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
            DBHelper.releaseConnection(conn);
        }
    }

    @Override
    public void alinhaVinculosRse() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            String rowCodigo = "SET @rowCodigo = (SELECT COALESCE(MAX(CAST(VRS_CODIGO AS UNSIGNED)), 999) + 1 FROM tb_vinculo_registro_servidor WHERE LENGTH(VRS_CODIGO) < 7)";
            jdbc.update(rowCodigo, queryParams);

            String query = "INSERT INTO tb_vinculo_registro_servidor (VRS_CODIGO, VRS_IDENTIFICADOR, VRS_DESCRICAO, VRS_ATIVO, VRS_DATA_CRIACAO) " +
                    "SELECT @rowCodigo := FLOOR(@rowCodigo) + 1, FLOOR(@rowCodigo), tipo, 1, current_timestamp() " +
                    "FROM (SELECT rse.rse_tipo as tipo " +
                    "FROM tb_registro_servidor rse " +
                    "WHERE srs_codigo NOT IN ('3', '4') " +
                    "AND NOT EXISTS (SELECT 1 FROM tb_vinculo_registro_servidor vrs WHERE vrs.vrs_descricao = rse.rse_tipo OR rse.rse_tipo is null) " +
                    "GROUP BY rse.rse_tipo) AS X ";

            LOG.debug("Cria vinculos do registro servidor após carga de margem:" + DateHelper.getSystemDatetime());
            jdbc.update(query, queryParams);

            query = "UPDATE tb_registro_servidor rse " +
                    "INNER JOIN tb_vinculo_registro_servidor vrs ON (vrs.vrs_descricao = rse.rse_tipo) " +
                    "SET rse.vrs_codigo = vrs.vrs_codigo;";

            jdbc.update(query, queryParams);
            LOG.debug("Atualiza codigo do vinculo do registro servidor: " + DateHelper.getSystemDatetime());
        } catch (final DataAccessException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DAOException(ex);
        }
    }

    @Override
    public void criaTabelaVariacaoMargemLimiteDefinidoCSA(String periodoAtual, boolean margemTotal, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodoAtual", periodoAtual);

        try {
            final StringBuilder query = new StringBuilder();

            LOG.debug("INÍCIO - CRIAÇÃO TABELA VARIAÇÃO MARGEM LIMITE CSA: " + DateHelper.getSystemDatetime());

            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_variacao_margem");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_variacao_margem (");
            query.append(" rse_codigo varchar(32) not null, ");
            query.append(" mar_codigo smallint not null, ");
            query.append(" margem_antes decimal(13,2) not null default 0, ");
            query.append(" margem_depois decimal(13,2) not null default 0, ");
            query.append(" variacao_margem decimal(13,2) not null default 0, ");
            query.append(" primary key (rse_codigo, mar_codigo) ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append(" drop temporary table if exists tb_tmp_periodo_variacao_margem ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append(" create temporary table tb_tmp_periodo_variacao_margem ( ");
            query.append(" rse_codigo varchar(32) not null, ");
            query.append(" mar_codigo smallint not null, ");
            query.append(" periodo date not null, ");
            query.append(" primary key (rse_codigo, mar_codigo, periodo) ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_margem_pos_processamento");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append(" create temporary table tb_tmp_margem_pos_processamento (");
            query.append(" rse_codigo varchar(32) not null, ");
            query.append(" mar_codigo smallint not null, ");
            query.append(" margem_depois decimal(13,2) not null default 0, ");
            query.append(" primary key (rse_codigo, mar_codigo) ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append(" insert into tb_tmp_periodo_variacao_margem (rse_codigo, mar_codigo, periodo) ");
            query.append(" select hmf.rse_codigo, hmf.mar_codigo, MAX(hmf.hma_periodo) from ");
            query.append(" tb_historico_margem_folha hmf ");
            query.append(" INNER JOIN tb_historico_margem_folha hmf2 ON (hmf2.rse_codigo = hmf.rse_codigo AND hmf2.mar_codigo = hmf.mar_codigo) ");
            if (!margemTotal) {
                query.append(" INNER JOIN tmp_hist_rse_mar_complementar hit ON (hmf.rse_codigo = hit.rse_codigo) ");
            }
            query.append(" where hmf.hma_periodo < hmf2.hma_periodo ");
            query.append(" and hmf.hma_periodo <> :periodoAtual ");
            query.append(" group by hmf.rse_codigo, hmf.mar_codigo ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append(" INSERT INTO tb_tmp_variacao_margem (rse_codigo, mar_codigo, margem_antes) ");
            query.append(" SELECT rse.rse_codigo, hmf.mar_codigo, hmf.hma_margem_folha ");
            query.append(" FROM tb_registro_servidor rse ");
            query.append(" INNER JOIN tb_historico_margem_folha hmf ON (hmf.rse_codigo = rse.rse_codigo) ");
            query.append(" INNER JOIN tb_tmp_periodo_variacao_margem pvm ON (pvm.rse_codigo = hmf.rse_codigo AND pvm.mar_codigo = hmf.mar_codigo AND pvm.periodo = hmf.hma_periodo) ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            LOG.debug("FIM - CRIAÇÃO TABELA VARIAÇÃO MARGEM LIMITE CSA: " + DateHelper.getSystemDatetime());

        } catch (final DataAccessException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DAOException(ex);
        }
    }

    @Override
    public void bloqueiaVariacaoMargemLimiteDefinidoCSA(boolean margemTotal, AcessoSistema responsavel) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            final StringBuilder query = new StringBuilder();

            LOG.debug("INÍCIO - BLOQUEIO PELA VARIAÇÃO MARGEM LIMITE CSA: " + DateHelper.getSystemDatetime());

            // Recupera novo valor da margem
            query.setLength(0);
            query.append(" INSERT INTO tb_tmp_margem_pos_processamento (rse_codigo, mar_codigo, margem_depois) ");
            query.append(" SELECT rse.rse_codigo, ").append(CodedValues.INCIDE_MARGEM_SIM).append(" AS CAMPO, COALESCE(rse_margem, 0) AS VLR_MARGEM ");
            query.append(" FROM tb_registro_servidor rse ");
            if (!margemTotal) {
                query.append(" INNER JOIN tmp_hist_rse_mar_complementar hit ON (rse.rse_codigo = hit.rse_codigo) ");
            }
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append(" INSERT INTO tb_tmp_margem_pos_processamento (rse_codigo, mar_codigo, margem_depois) ");
            query.append(" SELECT rse.rse_codigo, ").append(CodedValues.INCIDE_MARGEM_SIM_2).append(" AS CAMPO, COALESCE(rse_margem_2, 0) AS VLR_MARGEM ");
            query.append(" FROM tb_registro_servidor rse ");
            if (!margemTotal) {
                query.append(" INNER JOIN tmp_hist_rse_mar_complementar hit ON (rse.rse_codigo = hit.rse_codigo) ");
            }
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append(" INSERT INTO tb_tmp_margem_pos_processamento (rse_codigo, mar_codigo, margem_depois) ");
            query.append(" SELECT rse.rse_codigo, ").append(CodedValues.INCIDE_MARGEM_SIM_3).append(" AS CAMPO, COALESCE(rse_margem_3, 0) AS VLR_MARGEM ");
            query.append(" FROM tb_registro_servidor rse ");
            if (!margemTotal) {
                query.append(" INNER JOIN tmp_hist_rse_mar_complementar hit ON (rse.rse_codigo = hit.rse_codigo) ");
            }
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append(" INSERT INTO tb_tmp_margem_pos_processamento (rse_codigo, mar_codigo, margem_depois) ");
            query.append(" SELECT rse.rse_codigo, mar_codigo AS CAMPO, COALESCE(mrs_margem, 0) AS VLR_MARGEM ");
            query.append(" FROM tb_margem_registro_servidor rse ");
            if (!margemTotal) {
                query.append(" INNER JOIN tmp_hist_rse_mar_complementar hit ON (rse.rse_codigo = hit.rse_codigo) ");
            }
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Atualiza a margem depois
            query.setLength(0);
            query.append(" UPDATE tb_tmp_variacao_margem v ");
            query.append(" INNER JOIN tb_tmp_margem_pos_processamento p ON (v.rse_codigo = p.rse_codigo AND v.mar_codigo = p.mar_codigo) ");
            query.append(" SET v.margem_depois =  p.margem_depois ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Atualiza percentual de variação
            query.setLength(0);
            query.append("UPDATE tb_tmp_variacao_margem SET variacao_margem = coalesce(round(((margem_depois - margem_antes) / margem_antes) * 100, 2), 0) ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Cria tabela para registrar os convênios por registro servidor que tem o parâmetro de variação máximo cadastrado
            // para aqueles que possuem consignação ativa da natureza de empréstimo
            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_bloq_lmt_var_margem");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_bloq_lmt_var_margem (");
            query.append(" rse_codigo varchar(32) not null, ");
            query.append(" mar_codigo smallint not null, ");
            query.append(" cnv_codigo varchar(32) not null, ");
            query.append(" variacao_maxima decimal(13,2) not null default 0, ");
            query.append(" acao char(1) not null default '").append(ACAO_DEFAULT).append("', ");
            query.append(" primary key (rse_codigo, mar_codigo, cnv_codigo) ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Tabela de apoio para armazenar os servidores que já tem bloqueios existentes para mantermos a criação de temporárias e não correr o risco de erro de Can't reopen table
            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_bloq_lmt_margem2");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_bloq_lmt_margem2 (");
            query.append(" rse_codigo varchar(32) not null, ");
            query.append(" mar_codigo smallint not null, ");
            query.append(" cnv_codigo varchar(32) not null, ");
            query.append(" variacao_maxima decimal(13,2) not null default 0, ");
            query.append(" acao char(1) not null default '").append(ACAO_DEFAULT).append("', ");
            query.append(" primary key (rse_codigo, mar_codigo, cnv_codigo) ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_bloq_lmt_margem3");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_bloq_lmt_margem3 (");
            query.append(" rse_codigo varchar(32) not null, ");
            query.append(" mar_codigo smallint not null, ");
            query.append(" cnv_codigo varchar(32) not null, ");
            query.append(" variacao_maxima decimal(13,2) not null default 0, ");
            query.append(" acao char(1) not null default '").append(ACAO_DEFAULT).append("', ");
            query.append(" primary key (rse_codigo, mar_codigo, cnv_codigo) ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_bloq_lmt_var_margem (rse_codigo, mar_codigo, cnv_codigo, variacao_maxima) ");
            query.append("SELECT tmp.rse_codigo, tmp.mar_codigo, cnv.cnv_codigo, pcs85.pcs_vlr ");
            query.append("FROM tb_tmp_variacao_margem tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.rse_codigo = tmp.rse_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
            query.append("INNER JOIN tb_param_consignataria pcs85 ON (cnv.csa_codigo = pcs85.csa_codigo AND pcs85.tpa_codigo = '").append(CodedValues.TPA_PERCENTUAL_VARIACAO_MENSAL_MARGEM_POR_MATRICULA).append("') ");
            query.append("WHERE ade.ade_inc_margem = tmp.mar_codigo ");
            query.append("AND svc.nse_codigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
            query.append("AND ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            query.append("AND cast(coalesce(nullif(pcs85.pcs_vlr, ''), '0') as decimal) > 0 ");
            query.append("GROUP BY tmp.rse_codigo, tmp.mar_codigo, cnv.cnv_codigo, pcs85.pcs_vlr ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            final String toc228 = CodedValues.TOC_BLOQ_VARIACAO_PERCENTUAL_MARGEM_CSA;
            final String toc229 = CodedValues.TOC_DESBLOQ_VARIACAO_PERCENTUAL_MARGEM_CSA;

            // Registrar também os convênios por registro servidor que já tem bloqueio de verba para a CSA
            // e NÃO tem o parâmetro de variação máximo cadastrado (pode ter sido alterado) ou que NÃO possuem
            // consignação ativa da natureza de empréstimo (pode ter sido liquidada/concluída) para que sejam
            // desbloqueados na sequência da rotina
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_bloq_lmt_margem2 (rse_codigo, mar_codigo, cnv_codigo, variacao_maxima) ");
            query.append("SELECT tmp.rse_codigo, tmp.mar_codigo, cnv.cnv_codigo, 0 as variacao_maxima ");
            query.append("FROM tb_tmp_variacao_margem tmp ");
            query.append("INNER JOIN tb_param_convenio_registro_ser pcr ON (tmp.rse_codigo = pcr.rse_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = pcr.cnv_codigo) ");
            query.append("LEFT OUTER JOIN tb_param_svc_consignante pse3 ON (cnv.svc_codigo = pse3.svc_codigo AND pse3.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("') ");
            query.append("LEFT OUTER JOIN tb_param_consignataria pcs85 ON (cnv.csa_codigo = pcs85.csa_codigo AND pcs85.tpa_codigo = '").append(CodedValues.TPA_PERCENTUAL_VARIACAO_MENSAL_MARGEM_POR_MATRICULA).append("') ");
            query.append("WHERE cast(coalesce(pse3.pse_vlr, '1') as signed) = tmp.mar_codigo ");
            // Existe um bloqueio de verba de CSA
            query.append("  AND pcr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("' ");
            query.append("  AND pcr.pcr_vlr_csa = '0' ");
            // E o parâmetro da consignatária é Zero (desabilitado)
            query.append("  AND (cast(coalesce(nullif(pcs85.pcs_vlr, ''), '0') as decimal) <= 0 ");
            // Ou não tem consignação ativa de natureza empréstimo
            query.append("  OR NOT EXISTS ( ");
            query.append("     SELECT 1 FROM tb_aut_desconto ade2 ");
            query.append("     INNER JOIN tb_verba_convenio vco2 ON (vco2.vco_codigo = ade2.vco_codigo) ");
            query.append("     INNER JOIN tb_convenio cnv2 ON (cnv2.cnv_codigo = vco2.cnv_codigo) ");
            query.append("     INNER JOIN tb_servico svc2 ON (svc2.svc_codigo = cnv2.svc_codigo) ");
            query.append("     WHERE ade2.rse_codigo = tmp.rse_codigo ");
            query.append("       AND ade2.ade_inc_margem = tmp.mar_codigo ");
            query.append("       AND cnv2.cnv_codigo = pcr.cnv_codigo ");
            query.append("       AND svc2.nse_codigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
            query.append("       AND ade2.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            query.append("  ) ");
            query.append(") ");
            query.append("AND NOT EXISTS (select 1 from tb_tmp_bloq_lmt_var_margem tmp2 where tmp2.rse_codigo = tmp.rse_codigo and cnv.cnv_codigo = tmp2.cnv_codigo and tmp.mar_codigo = tmp2.mar_codigo) ");
            query.append("GROUP BY tmp.rse_codigo, tmp.mar_codigo, cnv.cnv_codigo ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            //Inserindo os registro servidores encontrados na lógica anterior
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_bloq_lmt_var_margem (rse_codigo, mar_codigo, cnv_codigo, variacao_maxima) ");
            query.append("SELECT tmp.rse_codigo, tmp.mar_codigo, tmp.cnv_codigo, tmp.variacao_maxima ");
            query.append("FROM tb_tmp_bloq_lmt_margem2 tmp ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Registrando os registro servidores com variação de margem maior que o limite configurado mesmo que não possua contrato com a CSA.
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_bloq_lmt_margem3 (rse_codigo, mar_codigo, cnv_codigo, variacao_maxima) ");
            query.append("SELECT DISTINCT tmp.rse_codigo, tmp.mar_codigo, cnv.cnv_codigo, pcs85.pcs_vlr ");
            query.append("FROM tb_convenio cnv ");
            query.append(" INNER JOIN tb_verba_convenio vco1 ON (cnv.cnv_codigo = vco1.cnv_codigo) ");
            query.append(" INNER JOIN tb_param_svc_consignante tps ON (tps.svc_codigo = cnv.svc_codigo and tps_codigo='").append(CodedValues.TPS_INCIDE_MARGEM).append("') ");
            query.append(" INNER JOIN tb_tmp_variacao_margem tmp ON (tps.pse_vlr = tmp.mar_codigo) ");
            query.append(" INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = tmp.rse_codigo) ");
            query.append(" INNER JOIN tb_param_consignataria pcs85 ON (cnv.csa_codigo = pcs85.csa_codigo AND pcs85.tpa_codigo = '").append(CodedValues.TPA_PERCENTUAL_VARIACAO_MENSAL_MARGEM_POR_MATRICULA).append("') ");
            query.append(" LEFT OUTER JOIN tb_param_consignataria pcs100 ON (cnv.csa_codigo = pcs100.csa_codigo AND pcs100.tpa_codigo='").append(CodedValues.TPA_BLOQUEIA_SER_COM_VARIACAO_MARGEM_MAIOR_QUE_LIMITE).append("') ");
            query.append(" LEFT OUTER JOIN tb_aut_desconto ade ON (ade.rse_codigo = tmp.rse_codigo AND vco1.vco_codigo = ade.vco_codigo ").append(" AND ade.sad_codigo IN (").append(TextHelper.sqlJoin(CodedValues.SAD_CODIGOS_ATIVOS)).append("))");
            query.append(" LEFT OUTER JOIN tb_tmp_bloq_lmt_var_margem tmp2 ON (tmp.rse_codigo = tmp2.rse_codigo AND tmp.mar_codigo = tmp2.mar_codigo AND cnv.cnv_codigo = tmp2.cnv_codigo)");
            query.append(" WHERE 1=1 ");
            query.append(" AND (pcs100.pcs_vlr= '").append(CodedValues.TPA_NAO).append("' ").append(" OR pcs100.pcs_vlr IS NULL) ");
            query.append(" AND rse.org_codigo = cnv.org_codigo ");
            query.append(" AND ade.rse_codigo IS NULL ");
            query.append(" AND tmp2.rse_codigo IS NULL ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            //Inserindo os registro servidores encontrados na lógica anterior
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_bloq_lmt_var_margem (rse_codigo, mar_codigo, cnv_codigo, variacao_maxima) ");
            query.append("SELECT tmp.rse_codigo, tmp.mar_codigo, tmp.cnv_codigo, tmp.variacao_maxima ");
            query.append("FROM tb_tmp_bloq_lmt_margem3 tmp ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Registra os servidores que deversão ser bloqueados pois possuem variação acima do permitido para a CSA e não possuem o bloqueio na verba
            query.setLength(0);
            query.append("UPDATE tb_tmp_variacao_margem var ");
            query.append("INNER JOIN tb_tmp_bloq_lmt_var_margem bloq on (var.rse_codigo = bloq.rse_codigo AND var.mar_codigo = bloq.mar_codigo) ");
            query.append("SET bloq.acao = '").append(ACAO_BLOQUEADO).append("' ");
            query.append("WHERE bloq.acao = '").append(ACAO_DEFAULT).append("' ");
            query.append("AND bloq.variacao_maxima > 0 AND var.variacao_margem > bloq.variacao_maxima ");
            query.append("AND NOT EXISTS (");
            query.append("   SELECT 1 FROM tb_param_convenio_registro_ser pcr ");
            query.append("   WHERE pcr.rse_codigo = bloq.rse_codigo ");
            query.append("     AND pcr.cnv_codigo = bloq.cnv_codigo ");
            query.append("     AND pcr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("'");
            query.append(") ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Registra os servidores que deversão ser desbloqueados pois possuem variação abaixo do permitido para a CSA e possuem o bloqueio na verba
            query.setLength(0);
            query.append("UPDATE tb_tmp_variacao_margem var ");
            query.append("INNER JOIN tb_tmp_bloq_lmt_var_margem bloq on (var.rse_codigo = bloq.rse_codigo AND var.mar_codigo = bloq.mar_codigo) ");
            query.append("SET bloq.acao = '").append(ACAO_DESBLOQUEADO).append("' ");
            query.append("WHERE bloq.acao = '").append(ACAO_DEFAULT).append("' ");
            query.append("AND (bloq.variacao_maxima <= 0 OR var.variacao_margem <= bloq.variacao_maxima) ");
            query.append("AND EXISTS (");
            query.append("   SELECT 1 FROM tb_param_convenio_registro_ser pcr ");
            query.append("   WHERE pcr.rse_codigo = bloq.rse_codigo ");
            query.append("     AND pcr.cnv_codigo = bloq.cnv_codigo ");
            query.append("     AND pcr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("'");
            query.append("     AND pcr.pcr_vlr = '0' AND pcr.pcr_vlr_csa = '0' ");
            query.append(") AND EXISTS ( ");
            query.append("    SELECT 1   ");
            query.append("    FROM tb_ocorrencia_registro_ser ocr228  ");
            query.append("    WHERE ocr228.rse_codigo = var.rse_codigo  ");
            query.append("      AND ocr228.toc_codigo = '").append(toc228).append("' ");
            query.append("      AND ocr228.ors_data > COALESCE(  ");
            query.append("        (SELECT MAX(ocr229.ors_data)  ");
            query.append("         FROM tb_ocorrencia_registro_ser ocr229  ");
            query.append("         WHERE ocr229.rse_codigo = ocr228.rse_codigo  ");
            query.append("         AND ocr229.toc_codigo ='").append(toc229).append("' ");
            query.append("         ),  ");
            query.append("         ocr228.ors_data - INTERVAL 1 SECOND ) ");
            query.append(") ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            final String rownum = "SET @rownum := 0";
            LOG.trace(rownum);
            jdbc.update(rownum, queryParams);

            // Insere ocorrência de bloqueio para o registro servidor
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_registro_ser (ORS_CODIGO, TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, ORS_OBS) ");
            query.append("SELECT CONCAT('G', ");
            query.append(" DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append(" SUBSTRING(LPAD(rse.rse_matricula, 12, '0'), 1, 12), ");
            query.append(" SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append(" '").append(CodedValues.TOC_BLOQ_VARIACAO_PERCENTUAL_MARGEM_CSA).append("', ");
            query.append(" rse.rse_codigo, ");
            query.append(" '1', ");
            query.append(" NOW(),");
            query.append(" CONCAT('").append(ApplicationResourcesHelper.getMessage("mensagem.bloqueio.convenio.limite.csa.verbas", responsavel)).append("', GROUP_CONCAT(distinct cnv.cnv_cod_verba), '. ");
            query.append(ApplicationResourcesHelper.getMessage("mensagem.bloqueio.convenio.limite.csa.margem.complemento", responsavel)).append("', GROUP_CONCAT(distinct var.mar_codigo)) ");
            query.append("FROM tb_tmp_bloq_lmt_var_margem bloq ");
            query.append("INNER JOIN tb_tmp_variacao_margem var on (var.rse_codigo = bloq.rse_codigo AND var.mar_codigo = bloq.mar_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse on (bloq.rse_codigo = rse.rse_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (bloq.cnv_codigo = cnv.cnv_codigo) ");
            query.append("WHERE acao = '").append(ACAO_BLOQUEADO).append("' ");
            query.append("GROUP BY rse.rse_codigo, rse.rse_matricula ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Insere ocorrência de desbloqueio para o registro servidor
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_registro_ser (ORS_CODIGO, TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, ORS_OBS) ");
            query.append("SELECT CONCAT('G', ");
            query.append(" DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append(" SUBSTRING(LPAD(rse.rse_matricula, 12, '0'), 1, 12), ");
            query.append(" SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'").append(CodedValues.TOC_DESBLOQ_VARIACAO_PERCENTUAL_MARGEM_CSA).append("',");
            query.append(" rse.rse_codigo, ");
            query.append(" '1', ");
            query.append(" NOW(),");
            query.append(" CONCAT('").append(ApplicationResourcesHelper.getMessage("mensagem.desbloqueio.convenio.limite.csa.verbas", responsavel)).append("', GROUP_CONCAT(distinct cnv.cnv_cod_verba), '. ");
            query.append(ApplicationResourcesHelper.getMessage("mensagem.desbloqueio.convenio.limite.csa.margem.complemento", responsavel)).append("', GROUP_CONCAT(distinct var.mar_codigo)) ");
            query.append("FROM tb_tmp_bloq_lmt_var_margem bloq ");
            query.append("INNER JOIN tb_tmp_variacao_margem var on (var.rse_codigo = bloq.rse_codigo AND var.mar_codigo = bloq.mar_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse on (bloq.rse_codigo = rse.rse_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (bloq.cnv_codigo = cnv.cnv_codigo) ");
            query.append("WHERE acao = '").append(ACAO_DESBLOQUEADO).append("' ");
            query.append("GROUP BY rse.rse_codigo, rse.rse_matricula ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Insere o bloqueio para o registro servidor para os casos necessários
            query.setLength(0);
            query.append("INSERT INTO tb_param_convenio_registro_ser (RSE_CODIGO, CNV_CODIGO, TPS_CODIGO, PCR_VLR, PCR_VLR_CSA, PCR_DATA_CADASTRO, PCR_OBS) ");
            query.append("SELECT rse_codigo, cnv_codigo, '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("', '0', '0', NOW(), '").append(ApplicationResourcesHelper.getMessage("rotulo.status.bloqueado.variacao.margem", responsavel)).append("' ");
            query.append("FROM tb_tmp_bloq_lmt_var_margem ");
            query.append("WHERE acao = '").append(ACAO_BLOQUEADO).append("' ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Remove o bloqueio para o registro servidor para os casos necessários
            query.setLength(0);
            query.append("DELETE FROM tb_param_convenio_registro_ser pcr ");
            query.append("WHERE pcr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("' ");
            query.append("AND EXISTS ( ");
            query.append("   SELECT 1 FROM tb_tmp_bloq_lmt_var_margem bloq ");
            query.append("   WHERE bloq.cnv_codigo = pcr.cnv_codigo ");
            query.append("     AND bloq.rse_codigo = pcr.rse_codigo ");
            query.append("     AND bloq.acao = '").append(ACAO_DESBLOQUEADO).append("' ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            LOG.debug("FIM - BLOQUEIO PELA VARIAÇÃO MARGEM LIMITE CSA: " + DateHelper.getSystemDatetime());

        } catch (final DataAccessException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DAOException(ex);
        }
    }

    @Override
    public List<TransferObject> lstCsaQntdaVerbaBloqLimiteVariacaoMargem(AcessoSistema responsavel) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            query.append("SELECT csa.csa_codigo AS CSA_COD, csa.csa_nome AS CSA_NOME, csa.csa_email AS CSA_EMAIL, cnv.cnv_cod_verba AS COD_VERBA, count(distinct bloq.rse_codigo) as QUANTIDADE ");
            query.append("FROM tb_tmp_bloq_lmt_var_margem bloq ");
            query.append("INNER JOIN tb_tmp_variacao_margem var ON (var.rse_codigo = bloq.rse_codigo and var.mar_codigo = bloq.mar_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = bloq.cnv_codigo) ");
            query.append("INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo) ");
            query.append("WHERE bloq.acao = '").append(ACAO_BLOQUEADO).append("' ");
            query.append("GROUP BY csa.csa_codigo, csa.csa_nome, csa.csa_email, cnv.cnv_cod_verba ");
            query.append("ORDER BY csa.csa_codigo ");

            return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), "CSA_COD,CSA_NOME,CSA_EMAIL,COD_VERBA,QUANTIDADE", MySqlDAOFactory.SEPARADOR);
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DAOException(ex);
        }
    }

    @Override
    public String montaQueryListaBloqVarMargemCsa(AcessoSistema responsavel) throws DAOException {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(" bloq.ACAO, ");
        query.append(" csa.CSA_IDENTIFICADOR, ");
        query.append(" csa.CSA_NOME_ABREV, ");
        query.append(" csa.CSA_NOME, ");
        query.append(" svc.SVC_IDENTIFICADOR, ");
        query.append(" svc.SVC_DESCRICAO, ");
        query.append(" cnv.CNV_COD_VERBA, ");
        query.append(" est.EST_IDENTIFICADOR, ");
        query.append(" est.EST_NOME, ");
        query.append(" org.ORG_IDENTIFICADOR, ");
        query.append(" org.ORG_NOME, ");
        query.append(" rse.RSE_MATRICULA, ");
        query.append(" ser.SER_CPF, ");
        query.append(" ser.SER_NOME, ");
        query.append(" var.MAR_CODIGO, ");
        query.append(" var.MARGEM_ANTES, ");
        query.append(" var.MARGEM_DEPOIS, ");
        query.append(" var.VARIACAO_MARGEM, ");
        query.append(" bloq.VARIACAO_MAXIMA ");
        query.append("FROM tb_tmp_bloq_lmt_var_margem bloq ");
        query.append("INNER JOIN tb_tmp_variacao_margem var ON (var.rse_codigo = bloq.rse_codigo and var.mar_codigo = bloq.mar_codigo) ");
        query.append("INNER JOIN tb_registro_servidor rse ON (var.rse_codigo = rse.rse_codigo) ");
        query.append("INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");
        query.append("INNER JOIN tb_orgao org ON (rse.org_codigo = org.org_codigo) ");
        query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = bloq.cnv_codigo) ");
        query.append("INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo) ");
        query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
        query.append("WHERE bloq.acao = '").append(ACAO_BLOQUEADO).append("' ");
        query.append("ORDER BY csa.csa_identificador, rse.rse_matricula ");
        return query.toString();
    }

    @Override
    public void criaTabelaHistoricoRseMargemComplementar() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        // Quando o sistema trabalha com bloqueio por variação de margem, precisamos saber quem são os registro servidores que estão sendo importados
        // para que o cálculo de variação afete somente eles e não todo o sistema
        final StringBuilder query = new StringBuilder();

        // Cria tabela temporária para armazenar os códigos dos registros servidores
        query.setLength(0);
        query.append("drop temporary table if exists tmp_hist_rse_mar_complementar");
        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);

        query.setLength(0);
        query.append("create temporary table tmp_hist_rse_mar_complementar (");
        query.append(" rse_codigo varchar(32)");
        query.append(", primary key (rse_codigo)");
        query.append(")");
        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);
    }

    @Override
    public void insereHistoricoRseMargemComplementar(String rseCodigo) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();
        query.append("INSERT IGNORE INTO tmp_hist_rse_mar_complementar (RSE_CODIGO) VALUES (:rseCodigo) ");
        queryParams.addValue("rseCodigo", rseCodigo);
        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);
    }
}
