package com.zetra.econsig.persistence.dao.mysql;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.FaturamentoBeneficioControllerException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.ImportaNotaFiscalArquivoFaturamentoBeneficioDAO;


/**
 * <p>Title: MySqlImportaNotaFiscalArquivoFaturamentoBeneficioDAO</p>
 * <p>Description: Mysql DAO para o importação de notas ficais para arquivos de faturamento de beneficios</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlImportaNotaFiscalArquivoFaturamentoBeneficioDAO implements ImportaNotaFiscalArquivoFaturamentoBeneficioDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlImportaNotaFiscalArquivoFaturamentoBeneficioDAO.class);

    /**
     * Deleta registros antigos
     */
    @Override
    public void deletarNotasFiscaisAnteriores(String fatCodigo) throws FaturamentoBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("fatCodigo", fatCodigo);

        try {
            final StringBuilder sql = new StringBuilder();
            sql.append("delete from tb_faturamento_beneficio_nf where FAT_CODIGO = :fatCodigo");

            LOG.warn(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new FaturamentoBeneficioControllerException(e);
        }
    }

    /**
     * Criar as tabelas necessarias para executar essa rotina.
     */
    @Override
    public void criarTabelaTemporariaLancamentosCredito(String fatCodigo) throws FaturamentoBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("fatCodigo", fatCodigo);

        try {
            final StringBuilder sql = new StringBuilder();

            sql.append("CREATE TEMPORARY TABLE tb_tmp_lancamentos_credito ");
            sql.append("select afb.FAT_CODIGO as FAT_CODIGO, ");
            sql.append("sum(afb.AFB_VALOR_SUBSIDIO) as VALORES_LANCAMENTOS_CREDITO, ");
            sql.append("(sum(afb.AFB_VALOR_REALIZADO) + sum(afb.AFB_VALOR_NAO_REALIZADO)) as VALOR_REALIZADO_NAO_REALIZADO_CREDITO, ");
            sql.append("afb.AFB_NUMERO_LOTE as AFB_NUMERO_LOTE, ");
            sql.append("afb.BEN_CODIGO_CONTRATO as BEN_CODIGO_CONTRATO ");
            sql.append("from tb_arquivo_faturamento_ben afb ");
            sql.append("inner join tb_tipo_lancamento tla on afb.TLA_CODIGO = tla.TLA_CODIGO ");
            sql.append("where ");
            sql.append("afb.FAT_CODIGO = :fatCodigo ");
            sql.append("and tla.tnt_codigo in (28,  29, 40, 46) ");
            sql.append("group by afb.AFB_NUMERO_LOTE, afb.BEN_CODIGO_CONTRATO, afb.FAT_CODIGO ");

            LOG.warn(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new FaturamentoBeneficioControllerException(e);
        }
    }

    /**
     * Criar as tabelas necessarias para executar essa rotina.
     */
    @Override
    public void criarTabelaTemporariaLancamentosDebito(String fatCodigo) throws FaturamentoBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("fatCodigo", fatCodigo);

        try {
            final StringBuilder sql = new StringBuilder();

            sql.append("CREATE TEMPORARY TABLE tb_tmp_lancamentos_debito ");
            sql.append("select afb.FAT_CODIGO as FAT_CODIGO, ");
            sql.append("sum(AFB_VALOR_SUBSIDIO) as VALORES_LANCAMENTOS_DEBITO, ");
            sql.append("(sum(AFB_VALOR_REALIZADO) + sum(AFB_VALOR_NAO_REALIZADO)) as VALOR_REALIZADO_NAO_REALIZADO_DEBITO, ");
            sql.append("afb.AFB_NUMERO_LOTE as AFB_NUMERO_LOTE, ");
            sql.append("afb.BEN_CODIGO_CONTRATO as BEN_CODIGO_CONTRATO ");
            sql.append("from tb_arquivo_faturamento_ben afb ");
            sql.append("inner join tb_tipo_lancamento tla on afb.TLA_CODIGO = tla.TLA_CODIGO ");
            sql.append("where ");
            sql.append("afb.FAT_CODIGO = :fatCodigo ");
            sql.append("and tla.tnt_codigo in (30, 31, 42, 48) ");
            sql.append("group by afb.AFB_NUMERO_LOTE, afb.BEN_CODIGO_CONTRATO, afb.FAT_CODIGO ");

            LOG.warn(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new FaturamentoBeneficioControllerException(e);
        }
    }

    /**
     * Deleta as tabelas necessarias para executar essa rotina.
     */
    @Override
    public void deletaTabelasTemporarias() throws FaturamentoBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            final StringBuilder sql = new StringBuilder();
            sql.append("drop temporary table if exists tb_tmp_lancamentos_credito");
            LOG.warn(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("drop temporary table if exists tb_tmp_lancamentos_debito");
            LOG.warn(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new FaturamentoBeneficioControllerException(e);
        }
    }

    @Override
    public void gerarNotasFiscaisArquivoFaturamentoBeneficioSubsidio(String fatCodigo) throws FaturamentoBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("fatCodigo", fatCodigo);

        try {
            final StringBuilder sql = new StringBuilder();

            sql.append("INSERT INTO tb_faturamento_beneficio_nf ");
            sql.append("(FNF_CODIGO, FAT_CODIGO, TNF_CODIGO, FNF_CODIGO_CONTRATO, FNF_VALOR_BRUTO, FNF_DATA_GERACAO) ");
            sql.append("SELECT ");
            sql.append("UPPER(MD5(UUID())), ");
            sql.append(":fatCodigo, ");
            sql.append("'S', ");
            sql.append("afb.BEN_CODIGO_CONTRATO, ");
            sql.append("SUM(afb.AFB_VALOR_SUBSIDIO - COALESCE(abfcred.VALORES_LANCAMENTOS_CREDITO,0) + COALESCE(abfdeb.VALORES_LANCAMENTOS_DEBITO,0)), ");
            sql.append("NOW() ");
            sql.append("FROM tb_arquivo_faturamento_ben afb ");
            sql.append("INNER JOIN tb_faturamento_beneficio as fat on fat.fat_codigo = afb.FAT_CODIGO ");
            sql.append("INNER JOIN tb_tipo_lancamento tla on afb.TLA_CODIGO = tla.TLA_CODIGO ");
            sql.append("LEFT JOIN tb_tmp_lancamentos_credito abfcred ON (afb.AFB_NUMERO_LOTE = abfcred.AFB_NUMERO_LOTE and afb.BEN_CODIGO_CONTRATO=abfcred.BEN_CODIGO_CONTRATO) ");
            sql.append("LEFT JOIN tb_tmp_lancamentos_debito abfdeb ON (afb.AFB_NUMERO_LOTE = abfdeb.AFB_NUMERO_LOTE and afb.BEN_CODIGO_CONTRATO = abfdeb.BEN_CODIGO_CONTRATO) ");
            sql.append("WHERE ");
            sql.append("afb.FAT_CODIGO =:fatCodigo ");
            sql.append("and tla.tnt_codigo in (25, 26) ");
            sql.append("group by afb.AFB_NUMERO_LOTE, afb.BEN_CODIGO_CONTRATO ");
            sql.append("having SUM(afb.AFB_VALOR_SUBSIDIO - COALESCE(abfcred.VALORES_LANCAMENTOS_CREDITO,0) + COALESCE(abfdeb.VALORES_LANCAMENTOS_DEBITO,0)) is not null ");

            LOG.warn(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new FaturamentoBeneficioControllerException(e);
        }
    }

    @Override
    public void gerarNotasFiscaisArquivoFaturamentoBeneficioMcMnc(String fatCodigo) throws FaturamentoBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("fatCodigo", fatCodigo);

        try {
            final StringBuilder sql = new StringBuilder();

            sql.append("INSERT INTO tb_faturamento_beneficio_nf ");
            sql.append("(FNF_CODIGO, FAT_CODIGO, TNF_CODIGO, FNF_CODIGO_CONTRATO, FNF_VALOR_BRUTO, FNF_DATA_GERACAO) ");
            sql.append("SELECT ");
            sql.append("UPPER(MD5(UUID())), ");
            sql.append(":fatCodigo, ");
            sql.append("'M', ");
            sql.append("afb.BEN_CODIGO_CONTRATO, ");
            sql.append("SUM(COALESCE(afb.AFB_VALOR_REALIZADO,0) + COALESCE(afb.AFB_VALOR_NAO_REALIZADO,0) - COALESCE(abfcred.VALORES_LANCAMENTOS_CREDITO,0) + COALESCE(abfdeb.VALORES_LANCAMENTOS_DEBITO,0)), ");
            sql.append("NOW() ");
            sql.append("FROM tb_arquivo_faturamento_ben afb ");
            sql.append("INNER JOIN tb_faturamento_beneficio as fat on fat.fat_codigo = afb.FAT_CODIGO ");
            sql.append("INNER JOIN tb_tipo_lancamento tla on afb.TLA_CODIGO = tla.TLA_CODIGO ");
            sql.append("LEFT JOIN tb_tmp_lancamentos_credito abfcred ON (afb.AFB_NUMERO_LOTE = abfcred.AFB_NUMERO_LOTE and afb.BEN_CODIGO_CONTRATO=abfcred.BEN_CODIGO_CONTRATO) ");
            sql.append("LEFT JOIN tb_tmp_lancamentos_debito abfdeb ON (afb.AFB_NUMERO_LOTE = abfdeb.AFB_NUMERO_LOTE and afb.BEN_CODIGO_CONTRATO = abfdeb.BEN_CODIGO_CONTRATO) ");
            sql.append("WHERE ");
            sql.append("afb.FAT_CODIGO =:fatCodigo ");
            sql.append("and tla.tnt_codigo in (25, 26) ");
            sql.append("group by afb.AFB_NUMERO_LOTE, afb.BEN_CODIGO_CONTRATO ");
            sql.append("having SUM(COALESCE(afb.AFB_VALOR_REALIZADO,0) + COALESCE(afb.AFB_VALOR_NAO_REALIZADO,0) - COALESCE(abfcred.VALORES_LANCAMENTOS_CREDITO,0) + COALESCE(abfdeb.VALORES_LANCAMENTOS_DEBITO,0)) is not null");

            LOG.warn(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new FaturamentoBeneficioControllerException(e);
        }
    }

    @Override
    public void gerarNotasFiscaisArquivoFaturamentoBeneficioCopart(String fatCodigo) throws FaturamentoBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("fatCodigo", fatCodigo);

        try {
            final StringBuilder sql = new StringBuilder();

            sql.append("INSERT INTO tb_faturamento_beneficio_nf ");
            sql.append("(FNF_CODIGO, FAT_CODIGO, TNF_CODIGO, FNF_CODIGO_CONTRATO, FNF_VALOR_BRUTO, FNF_DATA_GERACAO) ");
            sql.append("SELECT ");
            sql.append("UPPER(MD5(UUID())), ");
            sql.append(":fatCodigo, ");
            sql.append("'C', ");
            sql.append("afb.BEN_CODIGO_CONTRATO, ");
            sql.append("SUM(COALESCE(afb.AFB_VALOR_REALIZADO,0) + COALESCE(afb.AFB_VALOR_NAO_REALIZADO,0) - COALESCE(abfcred.VALORES_LANCAMENTOS_CREDITO,0) + COALESCE(abfdeb.VALORES_LANCAMENTOS_DEBITO,0)), ");
            sql.append("NOW() ");
            sql.append("FROM tb_arquivo_faturamento_ben afb ");
            sql.append("INNER JOIN tb_faturamento_beneficio as fat on fat.fat_codigo = afb.FAT_CODIGO ");
            sql.append("INNER JOIN tb_tipo_lancamento tla on afb.TLA_CODIGO = tla.TLA_CODIGO ");
            sql.append("LEFT JOIN tb_tmp_lancamentos_credito abfcred ON (afb.AFB_NUMERO_LOTE = abfcred.AFB_NUMERO_LOTE and afb.BEN_CODIGO_CONTRATO=abfcred.BEN_CODIGO_CONTRATO) ");
            sql.append("LEFT JOIN tb_tmp_lancamentos_debito abfdeb ON (afb.AFB_NUMERO_LOTE = abfdeb.AFB_NUMERO_LOTE and afb.BEN_CODIGO_CONTRATO = abfdeb.BEN_CODIGO_CONTRATO) ");
            sql.append("WHERE ");
            sql.append("afb.FAT_CODIGO =:fatCodigo ");
            sql.append("and tla.tnt_codigo in (27) ");
            sql.append("group by afb.AFB_NUMERO_LOTE, afb.BEN_CODIGO_CONTRATO ");
            sql.append("having SUM(COALESCE(afb.AFB_VALOR_REALIZADO,0) + COALESCE(afb.AFB_VALOR_NAO_REALIZADO,0) - COALESCE(abfcred.VALORES_LANCAMENTOS_CREDITO,0) + COALESCE(abfdeb.VALORES_LANCAMENTOS_DEBITO,0)) is not null");

            LOG.warn(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new FaturamentoBeneficioControllerException(e);
        }
    }
}
