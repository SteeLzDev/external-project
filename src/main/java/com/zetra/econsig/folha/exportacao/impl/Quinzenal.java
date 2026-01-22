package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: Quinzenal</p>
 * <p>Description: Implementações específicas para sistemas Quinzenais.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Quinzenal extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Quinzenal.class);

    private static final String[] COLUNAS_PERIODO = {"data_desconto", "data_ini_contrato", "data_fim_contrato", "ade_ano_mes_ini", "ade_ano_mes_fim", "ade_ano_mes_ini_folha", "ade_ano_mes_fim_folha", "ade_ano_mes_ini_ref", "ade_ano_mes_fim_ref", "pex_periodo", "pex_periodo_ant", "pex_periodo_pos", "oca_periodo"};
    private static final String SUFIXO_QUINZENAL = "_q";

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();
        try {
            // Adiciona colunas na tabela tb_tmp_exportacao_ordenada para os campos de data em formato quinzenal
            query.append("ALTER TABLE tb_tmp_exportacao_ordenada");
            for (String coluna : COLUNAS_PERIODO) {
                query.append(" ADD COLUMN ").append(coluna).append(SUFIXO_QUINZENAL).append(" varchar(6),");
            }
            query.deleteCharAt(query.length() - 1);
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();
        try {
            // Calcula a data no formato quinzenal
            // concat(year(DATA), lpad(month(DATA) * 2 + case when day(DATA) = 1 then -1 else 0 end, 2, '0'))
            query.append("UPDATE tb_tmp_exportacao_ordenada SET");
            for (String coluna : COLUNAS_PERIODO) {
                query.append(" ").append(coluna).append(SUFIXO_QUINZENAL).append(" = ");
                query.append("concat(year(").append(coluna).append("), lpad(month(").append(coluna).append(") * 2 + case when day(").append(coluna).append(") = 1 then -1 else 0 end, 2, '0')),");
            }
            query.deleteCharAt(query.length() - 1);
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}