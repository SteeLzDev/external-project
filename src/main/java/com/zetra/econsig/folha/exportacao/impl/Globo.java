package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: Globo</p>
 * <p>Description: Implementações específicas para a Globo</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Globo extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Globo.class);

    public static final String LINHAS_AFETADAS = "Linhas afetadas: ";

    @Override
    public void preProcessaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();
        try {
            int rows = 0;

            //DESENV-19446
            query.append("DELETE tmp.* FROM tb_tmp_exportacao tmp ");
            query.append("INNER JOIN tb_periodo_exportacao pex on (pex.org_codigo = tmp.org_codigo and pex.pex_sequencia = 0) ");
            query.append("WHERE tmp.ade_ano_mes_ini > pex.pex_periodo_pos ");
            query.append("AND NOT EXISTS (select 1 from tb_arquivo_movimento arq where arq.ade_numero = tmp.ade_numero) ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug(LINHAS_AFETADAS + rows);
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
            int rows = 0;

            //DESENV-19411
            //1.1) Identificar os comandos de Exclusão e Inclusão relativos a operações de renegociação.
            //1.2) As operações de renegociação são identificadas pelo relacionamento de autorização de natureza 6 (TNT_CODIGO).
            //1.3) As consignações origem do relacionamento estarão liquidadas, e o campo "tb_tmp_exportacao_ordenada.situacao" deve ser atualizado para "RE".
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("INNER JOIN tb_relacionamento_autorizacao relOrin ON (relOrin.ade_codigo_origem = ade.ade_codigo AND relOrin.tnt_codigo='").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("') ");
            query.append("SET tmp.situacao='RE' ");
            query.append("WHERE tmp.situacao='E' ");
            query.append("AND ade.sad_codigo='").append(CodedValues.SAD_LIQUIDADA).append("' ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug(LINHAS_AFETADAS + rows);
            query.setLength(0);

            //1.4) As consignações destino do relacionamento estarão deferidas, e o campo "tb_tmp_exportacao_ordenada.situacao" deve ser atualizado para "RI".
            //1.5) Eventualmente a consignação destino pode estar cancelada ou liquidada pós corte, e ainda sim é enviada como uma Inclusão, devendo ser mapeada como "RI".
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("INNER JOIN tb_relacionamento_autorizacao relDest ON (relDest.ade_codigo_destino = ade.ade_codigo AND relDest.tnt_codigo='").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("') ");
            query.append("SET tmp.situacao='RI' ");
            query.append("WHERE tmp.situacao='I' ");
            query.append("AND ade.sad_codigo in ('").append(CodedValues.SAD_LIQUIDADA).append("','").append(CodedValues.SAD_DEFERIDA).append("','").append(CodedValues.SAD_CANCELADA).append("') ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug(LINHAS_AFETADAS + rows);
            query.setLength(0);

            //Ajustar a situação para inclusão caso existiam duas linhas para o mesmo contrato caso as situações sejam A para os dois.
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_ajuste_alteracao ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE tb_tmp_ajuste_alteracao ");
            query.append("SELECT contador, ade_numero, situacao, count(*) from tb_tmp_exportacao_ordenada tmp ");
            query.append("WHERE situacao='A' ");
            query.append("GROUP BY ade_numero, situacao HAVING COUNT(*) >1 ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_tmp_ajuste_alteracao taa on (tmp.contador = taa.contador) ");
            query.append("SET tmp.situacao='I' ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug(LINHAS_AFETADAS + rows);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
