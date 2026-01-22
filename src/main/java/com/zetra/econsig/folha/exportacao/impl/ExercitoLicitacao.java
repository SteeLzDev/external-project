package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;

public class ExercitoLicitacao extends Exercito {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExercitoLicitacao.class);

    /**
     * Remove da tabela de parcelas do período (tb_parcela_desconto_periodo) as parcelas referente
     * ao período que está sendo exportado, evitando que as exportações com período ainda aberto
     * (parâmetro de sistema 898) sejam corretamente geradas para apresentação na licitação.
     */
    @Override
    public void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EXPORTAR_MOVIMENTO_DATA_FIM_FUTURA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            StringBuilder query = new StringBuilder();
            try {
                query.append("DELETE FROM tb_parcela_desconto_periodo ");
                query.append("WHERE tb_parcela_desconto_periodo.PRD_DATA_DESCONTO IN ( ");
                query.append("    SELECT DISTINCT tb_periodo_exportacao.PEX_PERIODO ");
                query.append("    FROM tb_periodo_exportacao ");
                query.append(") ");
                query.append("AND NOT EXISTS ( ");
                query.append("    SELECT 1 FROM tb_ocorrencia_parcela_periodo ");
                query.append("    WHERE tb_ocorrencia_parcela_periodo.PRD_CODIGO = tb_parcela_desconto_periodo.PRD_CODIGO ");
                query.append(") ");

                LOG.debug(query.toString());
                int linhasAfetadas = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + linhasAfetadas);
                query.setLength(0);

            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }

        super.preProcessaAutorizacoes(parametrosExportacao, responsavel);
    }
}
