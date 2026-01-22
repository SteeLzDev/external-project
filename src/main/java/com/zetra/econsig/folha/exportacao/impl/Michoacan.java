package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: Michoacan</p>
 * <p>Description: Implementações específicas para sistema eNomina Michoacán.</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Michoacan extends Quinzenal {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Michoacan.class);

    @Override
    public void preProcessaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.preProcessaTabelaExportacao(parametrosExportacao, responsavel);

        /**
         * 1.1) Ao exportar um contrato que foi alterado, deve ser enviado um registro correspondente
         *      ao contrato com o comando Exclusão e enviar registros de Inclusão para TODOS os contratos
         *      do servidor que tiverem a mesma verba do contrato alterado, incluindo o próprio contrato alterado.
         *
         * 1.2) Ao exportar um contrato que foi liquidado, deve ser enviado um registro correspondente ao
         *      contrato com o comando Exclusão e enviar registros de Inclusão para TODOS os outros contratos
         *      do servidor que tiverem a mesma verba do contrato liquidado.
         */

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();
        try {
            query.append("INSERT INTO tb_tmp_exp_mov_fin (ADE_CODIGO, OCA_PERIODO, TIPO) ");
            query.append("SELECT ade.ADE_CODIGO, exp.OCA_PERIODO, 'I' ");

            query.append("FROM tb_tmp_exp_mov_fin exp ");
            query.append("INNER JOIN tb_aut_desconto ade2 ON (exp.ADE_CODIGO = ade2.ADE_CODIGO) ");
            query.append("INNER JOIN tb_verba_convenio vco2 ON (ade2.VCO_CODIGO = vco2.VCO_CODIGO) ");
            query.append("INNER JOIN tb_convenio cnv2 ON (vco2.CNV_CODIGO = cnv2.CNV_CODIGO) ");

            query.append("INNER JOIN tb_aut_desconto ade ON (ade.RSE_CODIGO = ade2.RSE_CODIGO) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.VCO_CODIGO = vco.VCO_CODIGO) ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.CNV_CODIGO = cnv.CNV_CODIGO AND cnv.CNV_COD_VERBA = cnv2.CNV_COD_VERBA) ");

            // Contrato que integra folha e a exportação está permitida
            query.append("WHERE ade.ADE_INT_FOLHA IN (").append(CodedValues.INTEGRA_FOLHA_SIM).append(", ").append(CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO).append(") ");
            query.append("  AND COALESCE(ade.ADE_EXPORTACAO, '").append(CodedValues.ADE_EXPORTACAO_PERMITIDA).append("') <> '").append(CodedValues.ADE_EXPORTACAO_BLOQUEADA).append("' ");

            // Que tem parcela a ser exportada no período
            query.append("  AND EXISTS ( ");
            query.append("    SELECT 1 FROM tb_parcela_desconto_periodo prd ");
            query.append("    WHERE ade.ADE_CODIGO = prd.ADE_CODIGO ");
            query.append("      AND exp.OCA_PERIODO = prd.PRD_DATA_DESCONTO ");
            query.append("  ) ");

            // Que ainda não está na tabela com os contratos a serem exportados
            query.append("  AND NOT EXISTS ( ");
            query.append("    SELECT 1 FROM tb_tmp_exp_mov_fin tmp ");
            query.append("    WHERE ade.ADE_CODIGO = tmp.ADE_CODIGO ");
            query.append("  ) ");

            // Que o movimento a ser enviado é de uma exclusão ou alteração (A = contrato Aberto)
            query.append("  AND (exp.TIPO <> 'A' OR COALESCE(ade2.ADE_PRD_PAGAS, 0) > 0) ");

            query.append("GROUP BY ade.ADE_CODIGO, exp.OCA_PERIODO, exp.TIPO ");

            LOG.debug(query.toString());
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }

    }

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.preGeraArqLote(parametrosExportacao, responsavel);

        /**
         * 1.3) Os registros referentes a comandos de Exclusão devem ser enviados a folha consolidados por
         *      verba e servidor, ou seja, se for liquidado/alterado mais de um contrato da mesma verba e servidor,
         *      no arquivo deve ser enviado somente um registro de Exclusão, mesmo o sistema não consolidando
         *      descontos (parâmetro de sistema 19 = N).
         */

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();
        try {
            query.append("DELETE FROM tmp ");
            query.append("USING tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_tmp_exportacao_ordenada tmp2 ON (tmp2.SITUACAO = tmp.SITUACAO ");
            query.append("  AND tmp2.EST_IDENTIFICADOR = tmp.EST_IDENTIFICADOR ");
            query.append("  AND tmp2.ORG_IDENTIFICADOR = tmp.ORG_IDENTIFICADOR ");
            query.append("  AND tmp2.RSE_MATRICULA = tmp.RSE_MATRICULA ");
            query.append("  AND tmp2.CNV_COD_VERBA = tmp.CNV_COD_VERBA ");
            query.append(") ");
            query.append("WHERE tmp.SITUACAO IN ('E','A') ");
            query.append("  AND tmp2.CONTADOR < tmp.CONTADOR ");

            LOG.debug(query.toString());
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            query.append("DELETE FROM tmp ");
            query.append("USING tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_tmp_exportacao_ordenada tmp2 ON ( tmp2.situacao ='E' ");
            query.append("  and tmp2.EST_IDENTIFICADOR = tmp.EST_IDENTIFICADOR ");
            query.append("  AND tmp2.ORG_IDENTIFICADOR = tmp.ORG_IDENTIFICADOR ");
            query.append("  AND tmp2.RSE_MATRICULA = tmp.RSE_MATRICULA ");
            query.append("  AND tmp2.CNV_COD_VERBA = tmp.CNV_COD_VERBA ");
            query.append(") ");
            query.append("WHERE tmp.SITUACAO = 'A' ");

            LOG.debug(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
