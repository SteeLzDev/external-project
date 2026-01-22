package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: GEM</p>
 * <p>Description: Classe específica de exportação para o sistema GEM - Gobierno del Estado de México</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Gem extends Quinzenal {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Gem.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.atualizaValoresFolha.inicio.data.arg0", AcessoSistema.getAcessoUsuarioSistema(), DateHelper.getSystemDatetime().toString()));

            /*
             * DESENV-10276 : Quando há mais de um período em aberto é necessário atualizar os valores folha para aqueles sistemas
             * que necessitem desta informação para encontrar o contrato na folha.
             */
            StringBuilder query = new StringBuilder();

            // 1. Valor folha é diferente mas não houve alteração de valor no período: usar ade_vlr.
            query.append("update tb_tmp_exportacao tmp ");
            query.append("inner join tb_aut_desconto ade using (ade_codigo) ");
            query.append("inner join tb_periodo_exportacao pex using (org_codigo) ");
            query.append("set tmp.ade_vlr_folha = ade.ade_vlr ");
            query.append("where (tmp.ade_vlr_folha is null or ade.ade_vlr_folha <> ade.ade_vlr) ");
            query.append("and (tmp.ade_prd_pagas > 0 or ade.ade_prd_pagas_total > 0) ");
            query.append("and not exists ( ");
            query.append("select 1 ");
            query.append("from tb_ocorrencia_autorizacao oca2 ");
            query.append("where oca2.ade_codigo = ade.ade_codigo ");
            query.append("and oca2.toc_codigo = '14' ");
            query.append("and oca2.oca_periodo = pex.pex_periodo ");
            query.append("and oca2.oca_ade_vlr_ant <> oca2.oca_ade_vlr_novo) ");

            LOG.debug(query);
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);

            // 2. Houve alteração de valor no período: usar oca_ade_vlr_ant da alteração mais velha do período.
            query.setLength(0);
            query.append("update tb_tmp_exportacao tmp ");
            query.append("inner join tb_aut_desconto ade using (ade_codigo) ");
            query.append("inner join tb_periodo_exportacao pex using (org_codigo) ");
            query.append("set tmp.ade_vlr_folha = (select oca_ade_vlr_ant from tb_ocorrencia_autorizacao oca2 ");
            query.append("where oca2.ade_codigo = ade.ade_codigo ");
            query.append("and oca2.toc_codigo = '14' ");
            query.append("and oca2.oca_periodo = pex.pex_periodo ");
            query.append("and oca2.oca_ade_vlr_ant <> oca2.oca_ade_vlr_novo ");
            query.append("order by oca_data limit 1) ");
            query.append("where exists ( ");
            query.append("select 1 ");
            query.append("from tb_ocorrencia_autorizacao oca2 ");
            query.append("where oca2.ade_codigo = ade.ade_codigo ");
            query.append("and oca2.toc_codigo = '14' ");
            query.append("and oca2.oca_periodo = pex.pex_periodo ");
            query.append("and oca2.oca_ade_vlr_ant <> oca2.oca_ade_vlr_novo) ");

            LOG.debug(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);

            // Coloca os parametros no formato {n} para facilitar a validação do código fonte abaixo.
            String msgAlteracao = ApplicationResourcesHelper.getMessage("mensagem.informacao.prazo.alterado.arg0.de.arg1.para.arg2", AcessoSistema.getAcessoUsuarioSistema(), "{0}", "{1}", "{2}");
            int msgOcaAltIndex0 = msgAlteracao.indexOf("{0}");
            String prefixMsgPrazoOcaAlteracao = TextHelper.escapeSql(msgAlteracao.substring(0, msgOcaAltIndex0));
            if (TextHelper.isNull(prefixMsgPrazoOcaAlteracao) || msgOcaAltIndex0 < 0) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.formato.incorreto.mensagem.prazo", AcessoSistema.getAcessoUsuarioSistema(), msgAlteracao, "mensagem.informacao.prazo.alterado.arg0.de.arg1.para.arg2"));
            }

            // Coloca os parametros no formato {n} para facilitar a validação do código fonte abaixo.
            String msgRelancamento = ApplicationResourcesHelper.getMessage("mensagem.informacao.prazo.alterado.de.arg0.para.arg1", AcessoSistema.getAcessoUsuarioSistema(), "{0}", "{1}");
            int msgOcaRelIndex0 = msgRelancamento.indexOf("{0}");
            String prefixMsgPrazoOcaRelancamento = TextHelper.escapeSql(msgRelancamento.substring(0, msgOcaRelIndex0));
            if (TextHelper.isNull(prefixMsgPrazoOcaRelancamento) || msgOcaRelIndex0 < 0) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.formato.incorreto.mensagem.prazo", AcessoSistema.getAcessoUsuarioSistema(), msgRelancamento, "mensagem.informacao.prazo.alterado.de.arg0.para.arg1"));
            }

            // 3. Prazo folha é diferente mas não houve alteração de prazo no período: usar ade_prazo.
            query.setLength(0);
            query.append("update tb_tmp_exportacao tmp ");
            query.append("inner join tb_aut_desconto ade using (ade_codigo) ");
            query.append("inner join tb_periodo_exportacao pex using (org_codigo) ");
            // INICIO - SET
            query.append("set tmp.ade_prazo_folha = ade.ade_prazo, tmp.ade_ano_mes_ini_folha = ade.ade_ano_mes_ini, tmp.ade_ano_mes_fim = ade.ade_ano_mes_fim ");
            // FIM - SET
            query.append("where (tmp.ade_prazo_folha is null or ade.ade_prazo_folha <> ade.ade_prazo) ");
            query.append("and (tmp.ade_prd_pagas > 0 or ade.ade_prd_pagas_total > 0) ");
            query.append("and not exists ( ");
            query.append("select 1 ");
            query.append("from tb_ocorrencia_autorizacao oca2 ");
            query.append("where oca2.ade_codigo = ade.ade_codigo ");
            query.append("and oca2.oca_periodo = pex.pex_periodo ");
            query.append("and ((oca2.toc_codigo = '14' and oca_obs like '%").append(prefixMsgPrazoOcaAlteracao).append("%') or ");
            query.append("(oca2.toc_codigo = '3' and oca_obs like '%").append(prefixMsgPrazoOcaRelancamento).append("%')) ");
            query.append(")");

            LOG.debug(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);

            // 4. Houve alteração de prazo no período: usar o valor presente na ocorrência de alteração ou reimplante
            query.setLength(0);
            query.append("update tb_tmp_exportacao tmp ");
            query.append("inner join tb_aut_desconto ade using (ade_codigo) ");
            query.append("inner join tb_periodo_exportacao pex using (org_codigo) ");
            // INICIO - SET
            query.append("set tmp.ade_prazo_folha = (select case toc_codigo ");
            query.append("when '14' ");
            query.append("then cast(substring(oca_obs, locate(' ', oca_obs, locate('").append(prefixMsgPrazoOcaAlteracao).append("', oca_obs) ");
            query.append("+ char_length('").append(prefixMsgPrazoOcaAlteracao).append("') + ").append(msgOcaAltIndex0).append(")) as unsigned) ");
            query.append("when '3' ");
            query.append("then cast(substring(oca_obs, locate('").append(prefixMsgPrazoOcaRelancamento).append("', oca_obs) ");
            query.append("+ char_length('").append(prefixMsgPrazoOcaRelancamento).append("')) as unsigned) ");
            query.append("else tmp.ade_prazo_folha end ");
            query.append("from tb_ocorrencia_autorizacao oca2 ");
            query.append("where oca2.ade_codigo = ade.ade_codigo ");
            query.append("and oca2.oca_periodo = pex.pex_periodo ");
            query.append("and ((oca2.toc_codigo = '14' and oca_obs like '%").append(prefixMsgPrazoOcaAlteracao).append("%') or ");
            query.append("(oca2.toc_codigo = '3' and oca_obs like '%").append(prefixMsgPrazoOcaRelancamento).append("%')) ");
            query.append("order by oca_data limit 1) ");
            // FIM - SET
            query.append("where exists ( ");
            query.append("select 1 ");
            query.append("from tb_ocorrencia_autorizacao oca2 ");
            query.append("where oca2.ade_codigo = ade.ade_codigo ");
            query.append("and oca2.oca_periodo = pex.pex_periodo ");
            query.append("and ((oca2.toc_codigo = '14' and oca_obs like '%").append(prefixMsgPrazoOcaAlteracao).append("%') or ");
            query.append("(oca2.toc_codigo = '3' and oca_obs like '%").append(prefixMsgPrazoOcaRelancamento).append("%')) ");
            query.append(")");

            LOG.debug(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.atualizaValoresFolha.fim.data.arg0", AcessoSistema.getAcessoUsuarioSistema(), DateHelper.getSystemDatetime().toString()));

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
