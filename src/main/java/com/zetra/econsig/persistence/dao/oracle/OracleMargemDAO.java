package com.zetra.econsig.persistence.dao.oracle;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
 * <p>Title: OracleMargemDAO</p>
 * <p>Description: Implementacao do DAO de margem para o Oracle</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleMargemDAO implements MargemDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleMargemDAO.class);

    @Override
    public void criaTabelaHistoricoRse(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        // Verifica se está habilitado o módulo de descontos de permissionário
        final boolean moduloSDP = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_SDP, AcessoSistema.getAcessoUsuarioSistema());

        final StringBuilder query = new StringBuilder();

        // Cria tabela temporária para armazenar os postos e tipos dos registros servidores
        query.setLength(0);
        query.append("CALL dropTableIfExists('tmp_historico_rse')");
        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);

        query.setLength(0);
        query.append("CALL createTemporaryTable('tmp_historico_rse (");
        query.append(" rse_codigo varchar2(32)");
        query.append(", srs_codigo varchar2(32)");

        if (moduloSDP) {
            query.append(", trs_codigo_old varchar2(32)");
            query.append(", pos_codigo_old varchar2(32)");
        }

        query.append(")')");
        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);

        query.setLength(0);
        query.append("CALL createIndexOnTemporaryTable('tmp_historico_rse_rse_idx', 'tmp_historico_rse', 'rse_codigo')");
        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);

        query.setLength(0);
        query.append("CALL createIndexOnTemporaryTable('tmp_historico_rse_srs_idx', 'tmp_historico_rse', 'srs_codigo')");
        LOG.trace(query);
        jdbc.update(query.toString(), queryParams);

        if (moduloSDP) {
            query.setLength(0);
            query.append("CALL createIndexOnTemporaryTable('tmp_historico_rse_trs_idx', 'tmp_historico_rse', 'trs_codigo_old')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createIndexOnTemporaryTable('tmp_historico_rse_pos_idx', 'tmp_historico_rse', 'pos_codigo_old')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);
        }

        // Insere os dados dos registros servidores selecionados
        query.setLength(0);
        query.append("insert into tmp_historico_rse (rse_codigo, srs_codigo");

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
            query.append("CALL dropTableIfExists('tmp_rse_status_alt_rse')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createTemporaryTable('tmp_rse_status_alt_rse (");
            query.append(" rse_codigo varchar(32) not null, ");
            query.append(" srs_codigo varchar(32) not null, ");
            query.append(" srs_codigo_old varchar(32) DEFAULT NULL ");
            query.append(")')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createIndexOnTemporaryTable('tmp_rse_status_alt_rse_idx', 'tmp_rse_status_alt_rse', 'rse_codigo')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_rse_status_alt_rse ");
            query.append("select ");
            query.append(Columns.RSE_CODIGO).append(MySqlDAOFactory.SEPARADOR);
            query.append(Columns.RSE_SRS_CODIGO).append(MySqlDAOFactory.SEPARADOR);
            query.append("tmp_historico_rse.srs_codigo as srs_codigo_old ");
            query.append("from tb_registro_servidor ");
            query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
            query.append("inner join tb_estabelecimento on (tb_estabelecimento.est_codigo = tb_orgao.est_codigo) ");
            query.append("left outer join tmp_historico_rse on (tb_registro_servidor.rse_codigo = tmp_historico_rse.rse_codigo) ");
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
            query.append(" and (tmp_historico_rse.srs_codigo is null or tb_registro_servidor.srs_codigo <> tmp_historico_rse.srs_codigo) ");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Inserindo ocorrência para os registro de servidores que são novos.
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_registro_ser (ORS_CODIGO, TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, ORS_OBS, ORS_IP_ACESSO) ");
            query.append("SELECT 'I' || ");
            query.append("TO_CHAR(CURRENT_TIMESTAMP, 'yyyymmddhhmmss') || ");
            query.append("SUBSTRING(LPAD(rse_matricula, 12, '0'), 1, 12) || ");
            query.append("SUBSTRING(rse.rse_codigo, 1, 5), ");
            query.append("'").append(CodedValues.TOC_RSE_INCLUSAO_POR_CARGA_MARGEM).append("', ");
            query.append("rse.rse_codigo, ");
            query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', ");
            query.append("CURRENT_DATE, :orsObs, :ipUsuario ");
            query.append(" FROM tmp_rse_status_alt_rse tmp");
            query.append(" INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = tmp.rse_codigo) ");
            query.append(" WHERE tmp.srs_codigo_old IS NULL AND tmp.srs_codigo = '").append(CodedValues.SRS_ATIVO).append("'");
            queryParams.addValue("orsObs", ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.inclusao.carga.margem", responsavel));
            queryParams.addValue("ipUsuario", responsavel.getIpUsuario());
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            // Inserindo ocorrência para reativação de registor servidor
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_registro_ser (ORS_CODIGO, TOC_CODIGO, RSE_CODIGO, USU_CODIGO, ORS_DATA, ORS_OBS, ORS_IP_ACESSO) ");
            query.append("SELECT 'I' || ");
            query.append("TO_CHAR(CURRENT_TIMESTAMP, 'yyyymmddhhmmss') || ");
            query.append("SUBSTRING(LPAD(rse_matricula, 12, '0'), 1, 12) || ");
            query.append("SUBSTRING(rse.rse_codigo, 1, 5), ");
            query.append("'").append(CodedValues.TOC_RSE_REATIVACAO_POR_CARGA_MARGEM).append("', ");
            query.append("rse.rse_codigo, ");
            query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', ");
            query.append("CURRENT_DATE, :orsObs, :ipUsuario ");
            query.append(" FROM tmp_rse_status_alt_rse tmp");
            query.append(" INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = tmp.rse_codigo) ");
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
        query.append("left outer join tmp_historico_rse on (tb_registro_servidor.rse_codigo = tmp_historico_rse.rse_codigo) ");
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
        query.append("((tb_registro_servidor.trs_codigo <> tmp_historico_rse.trs_codigo_old) or (tb_registro_servidor.trs_codigo is null and tmp_historico_rse.trs_codigo_old is not null) or (tb_registro_servidor.trs_codigo is not null and tmp_historico_rse.trs_codigo_old is null)) or ");
        query.append("((tb_registro_servidor.pos_codigo <> tmp_historico_rse.pos_codigo_old) or (tb_registro_servidor.pos_codigo is null and tmp_historico_rse.pos_codigo_old is not null) or (tb_registro_servidor.pos_codigo is not null and tmp_historico_rse.pos_codigo_old is null))");
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
            final String insereNaoExiste = "INSERT INTO tb_margem_registro_servidor (mar_codigo, rse_codigo, mrs_margem, mrs_margem_rest, mrs_margem_usada, mrs_periodo_ini, mrs_periodo_fim) SELECT ?, ?, ?, 0, 0, ?, ? FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM tb_margem_registro_servidor  WHERE rse_codigo = ? AND mar_codigo = ?)";
            final String historicoAlteraQuandoExiste = "UPDATE tb_historico_margem_folha SET hma_margem_folha = ?, hma_data = ? WHERE rse_codigo = ? AND mar_codigo = ? AND hma_periodo = ?";
            final String historicoInsereNaoExiste = "INSERT INTO tb_historico_margem_folha (rse_codigo, mar_codigo, hma_periodo, hma_data, hma_margem_folha) SELECT ?, ?, ?, ?, ? FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM tb_historico_margem_folha WHERE rse_codigo = ? AND mar_codigo = ? AND hma_periodo = ?)";

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
            query.append("CALL dropTableIfExists('tb_tmp_arquivo_margem_servico_externo')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createTemporaryTable('tb_tmp_arquivo_margem_servico_externo (");
            query.append(" ams_codigo INT NOT NULL AUTO_INCREMENT ");
            for (final String key : keys.keySet()) {
                if(key.contains("RSE_MARGEM")) {
                    query.append(" , " + key + " decimal(13,2) ");
                } else {
                    query.append(" , " + key + " varchar(255) ");
                }
            }
            query.append(")')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createIndexOnTemporaryTable('tb_tmp_arquivo_margem_servico_externo_ams_idx', 'tb_tmp_arquivo_margem_servico_externo', 'ams_codigo')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createIndexOnTemporaryTable('tb_tmp_arquivo_margem_servico_externo_ams_idx', 'tb_tmp_arquivo_margem_servico_externo', 'rse_matricula')");
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
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("vrsData", DateHelper.getSystemDatetime());

        try {
            final StringBuilder query = new StringBuilder();

            query.setLength(0);
            query.append("SELECT MAX(").append(Columns.VRS_CODIGO).append(") FROM ").append(Columns.TB_VINCULO_REGISTRO_SERVIDOR);
            final int offset = Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Integer.class)).orElse(0);
            queryParams.addValue("offset", offset);

            query.setLength(0);
            query.append("INSERT INTO ").append(Columns.TB_VINCULO_REGISTRO_SERVIDOR);
            query.append(" (VRS_CODIGO, VRS_IDENTIFICADOR, VRS_DESCRICAO, VRS_ATIVO, VRS_DATA_CRIACAO)");
            query.append(" SELECT ROWNUM + :offset, ROWNUM + :offset, TIPO, '1', :vrsData");
            query.append(" FROM ( SELECT rse.rse_tipo as TIPO FROM tb_registro_servidor rse");
            query.append(" WHERE srs_codigo NOT IN ('3', '4')");
            query.append(" AND NOT EXISTS (SELECT 1 FROM tb_vinculo_registro_servidor vrs WHERE vrs.vrs_descricao = rse.rse_tipo OR rse.rse_tipo is null) GROUP BY rse.rse_tipo) ");

            LOG.debug("Cria vinculos do registro servidor após carga de margem:" + DateHelper.getSystemDatetime());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("UPDATE tb_registro_servidor rse SET rse.vrs_codigo = ( SELECT vrs.vrs_codigo FROM tb_vinculo_registro_servidor vrs WHERE vrs.vrs_descricao = rse.rse_tipo) ");

            LOG.debug("Atualiza codigo do vinculo do registro servidor: " + DateHelper.getSystemDatetime());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DAOException(ex);
        }
    }

    @Override
    public void criaTabelaVariacaoMargemLimiteDefinidoCSA(String periodoAtual, boolean margemTotal, AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bloqueiaVariacaoMargemLimiteDefinidoCSA(boolean margemTotal, AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TransferObject> lstCsaQntdaVerbaBloqLimiteVariacaoMargem(AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String montaQueryListaBloqVarMargemCsa(AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void criaTabelaHistoricoRseMargemComplementar() throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insereHistoricoRseMargemComplementar(String rseCodigo) throws DAOException {
        throw new UnsupportedOperationException();
    }
}
