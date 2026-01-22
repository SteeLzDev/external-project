package com.zetra.econsig.persistence.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.BeneficiarioDAO;
import com.zetra.econsig.values.StatusBeneficiarioEnum;


/**
 * <p>Title: MysqlBeneficiariosDAO</p>
 * <p>Description: Mysql DAO para o importação de beneficiários dependentes</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MysqlBeneficiariosDAO implements BeneficiarioDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MysqlBeneficiariosDAO.class);

    private Connection conn;
    private PreparedStatement pstat;

    /**
     * Criar as tabelas necessarias para executar essa rotina.
     */
    @Override
    public void criaTabelaImportaBeneficiarios() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            final StringBuilder sql = new StringBuilder();

            sql.append("drop temporary table if exists tb_tmp_imp_beneficiarios");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("create temporary table tb_tmp_imp_beneficiarios (");
            sql.append("    nome_arquivo varchar(255) NOT NULL,");
            sql.append("    ser_codigo varchar(32) not null,");
            sql.append("    rse_codigo varchar(32),");
            sql.append("    bfc_cpf varchar(19) ,");
            sql.append("    bfc_identificador varchar(40),");
            sql.append("    KEY idx_ser_codigo_ib (ser_codigo),");
            sql.append("    KEY idx_bfc_cpf_ib (bfc_cpf),");
            sql.append("    KEY idx_bfc_identificador_ib (bfc_identificador), ");
            sql.append("    KEY idx_rse_codigo_ib (rse_codigo)");
            sql.append(") engine=innodb default charset=latin1;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void iniciarCargaBeneficiarios() throws DAOException {
        try {
            conn = DBHelper.makeConnection();
            pstat = conn.prepareStatement(INSERT_TMP_BENEFICIARIO_SQL);
        } catch (final SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void encerrarCargaBeneficiarios() throws DAOException {
        try {
            pstat.executeBatch();
        } catch (final SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(pstat);
            DBHelper.releaseConnection(conn);
        }
    }

    @Override
    public void incluiBeneficiarios(String nomeArquivo, String serCodigo, String bfcCpf, String bfcIdentificador, String rseCodigo) throws DAOException {
        try {
            pstat.setString(1, nomeArquivo);
            pstat.setString(2, serCodigo);
            pstat.setString(3, bfcCpf);
            pstat.setString(4, bfcIdentificador);
            pstat.setString(5, rseCodigo);
            pstat.addBatch();
        } catch (final SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void excluiBeneficiarios() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            final StringBuilder sql = new StringBuilder();

            sql.append(" update tb_beneficiario bfc ");
            sql.append(" set bfc.sbe_codigo = '").append(StatusBeneficiarioEnum.EXCLUIDO.sbeCodigo).append("'");
            sql.append(" where not exists (");
            sql.append("   select 1 from tb_tmp_imp_beneficiarios tmp ");
            sql.append("   where tmp.ser_codigo = bfc.ser_codigo");
            sql.append("    and (bfc.bfc_cpf = tmp.bfc_cpf and bfc.rse_codigo = tmp.rse_codigo ");
            sql.append("      or bfc.bfc_identificador = tmp.bfc_identificador and bfc.rse_codigo = tmp.rse_codigo ");
            sql.append("    ) ");
            sql.append(" ) ");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);

        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new DAOException(e);
        }
    }
}
