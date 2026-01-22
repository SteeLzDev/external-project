package com.zetra.econsig.persistence.dao.mysql;

import static com.zetra.econsig.helper.texto.TextHelper.escapeSql;
import static com.zetra.econsig.helper.texto.TextHelper.joinWithEscapeSql;
import static com.zetra.econsig.helper.texto.TextHelper.sqlJoin;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.PontuacaoServidorDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoPontuacaoEnum;

/**
 * <p>Title: MySqlPontuacaoServidorDAO</p>
 * <p>Description: Implementação para MySql do DAO de Pontuação do Servidor</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlPontuacaoServidorDAO implements PontuacaoServidorDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlPontuacaoServidorDAO.class);

    /**
     * Conclui as autorizações de desconto que não foram pagas no mês.
     * @param responsavel : usuário responsável pela operação
     * @throws DAOException
     */
    @Override
    public void calcularPontuacaoLeilao(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            LOG.debug("Calcula pontuação de leilão dos servidores: " + DateHelper.getSystemDatetime());

            final StringBuilder query = new StringBuilder();
            int rows = 0;

            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_score");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE `tmp_score` (");
            query.append("`rse_codigo` varchar(32) NOT NULL DEFAULT '', ");
            query.append("`quant_contratos_concluidos` decimal(23,0) DEFAULT NULL, ");
            query.append("`pont_contratos_concluidos` int(11) DEFAULT NULL, ");
            query.append("`quant_contratos_suspensos` decimal(23,0) DEFAULT NULL, ");
            query.append("`pont_contratos_suspensos` int(11) DEFAULT NULL, ");
            query.append("`porcent_inadimplencia` int(11) DEFAULT NULL, ");
            query.append("`pont_inadimplencia` int(11) DEFAULT NULL, ");
            query.append("`porcent_margem_utilizada` decimal(39,0) DEFAULT NULL, ");
            query.append("`pont_margem_utilizada` int(11) DEFAULT NULL, ");
            query.append("`pont_total` int(11) DEFAULT NULL, ");
            query.append("PRIMARY KEY (`rse_codigo`) ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Insere dados na tabela geral Score
            // Não inclui "Inadimplencia", pois envolve parcelas, que atrapalharia a contagem de ADEs

            String[] suspenso = { CodedValues.SAD_SUSPENSA, CodedValues.SAD_SUSPENSA_CSE };
            String[] naoAtivo = { CodedValues.SAD_SOLICITADO, CodedValues.SAD_AGUARD_CONF, CodedValues.SAD_AGUARD_DEFER,
                    CodedValues.SAD_INDEFERIDA, CodedValues.SAD_CANCELADA, CodedValues.SAD_LIQUIDADA, CodedValues.SAD_CONCLUIDO, CodedValues.SAD_ENCERRADO };

            query.append("insert into tmp_score (rse_codigo, quant_contratos_concluidos, quant_contratos_suspensos, porcent_margem_utilizada) ");
            query.append("select ");
            query.append("rse.rse_codigo, ");
            query.append("sum(case when ade.sad_codigo in ('").append(CodedValues.SAD_CONCLUIDO).append("') then 1 else 0 end) 'quant_contratos_concluidos', ");
            query.append("sum(case when ade.sad_codigo in (").append(sqlJoin(suspenso)).append(") then 1 else 0 end) 'quant_contratos_suspensos', ");
            query.append("truncate((sum(case when ade.sad_codigo not in (").append(sqlJoin(naoAtivo)).append(") then ade.ade_vlr else 0 end) * 100) / ");
            query.append("(rse.rse_margem + sum(case when ade.sad_codigo not in (").append(sqlJoin(naoAtivo)).append(") then ade.ade_vlr else 0 end)), 0) 'porcent_margem_utilizada' ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("where rse.srs_codigo in (").append(sqlJoin(CodedValues.SRS_ATIVOS)).append(") ");
            query.append("and svc.nse_codigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
            query.append("and ade.ade_data between date_add(now(), interval -5 year) and now() ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, true, null, queryParams));
            query.append("group by rse.rse_codigo");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_score_inadimplencia");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Popula tabela Score com dados de inadimplência

            String[] processadas = { CodedValues.SPD_REJEITADAFOLHA, CodedValues.SPD_LIQUIDADAFOLHA };

            query.append("CREATE TEMPORARY TABLE `tmp_score_inadimplencia` (");
            query.append("`rse_codigo` varchar(32) NOT NULL DEFAULT '', ");
            query.append("`Porcent_inadimplencia` decimal(26,0) DEFAULT NULL, ");
            query.append("PRIMARY KEY (`rse_codigo`) ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("insert into tmp_score_inadimplencia (rse_codigo, Porcent_inadimplencia) ");
            query.append("select rse.rse_codigo, ");
            query.append("truncate(sum(case when prd.spd_codigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("' then 1 else 0 end) * 100 / ");
            query.append("sum(case when prd.spd_codigo in (").append(sqlJoin(processadas)).append(") then 1 else 0 end), 0) 'Porcent_inadimplencia' ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("where rse.srs_codigo in (").append(sqlJoin(CodedValues.SRS_ATIVOS)).append(") ");
            query.append("and svc.nse_codigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
            query.append("and ade.ade_data between date_add(now(), interval -5 year) and now() ");
            query.append("and prd.spd_codigo in (").append(sqlJoin(processadas)).append(") ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, true, null, queryParams));
            query.append("group by rse.rse_codigo");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Corrige porcentagem de margem utilizada para quem tem margem disponível = 0
            query.append("update tmp_score sco ");
            query.append("join tmp_score_inadimplencia sci on (sco.rse_codigo = sci.rse_codigo) ");
            query.append("set sco.porcent_inadimplencia = sci.porcent_inadimplencia");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Popula tabela Score com pontuação
            // Popula pontuação individual de cada item
            query.append("update tmp_score sco ");
            query.append("set sco.pont_contratos_concluidos = coalesce((select psc_cc.PPO_PONTUACAO from tb_param_pontuacao_rse psc_cc where psc_cc.TPO_CODIGO = '").append(TipoPontuacaoEnum.QTDE_CONTRATOS_CONCLUIDOS.getCodigo()).append("' and sco.quant_contratos_concluidos >= psc_cc.PPO_LIM_INFERIOR and sco.quant_contratos_concluidos <= psc_cc.PPO_LIM_SUPERIOR), sco.pont_contratos_concluidos), ");
            query.append("    sco.pont_contratos_suspensos  = coalesce((select psc_cs.PPO_PONTUACAO from tb_param_pontuacao_rse psc_cs where psc_cs.TPO_CODIGO = '").append(TipoPontuacaoEnum.QTDE_CONTRATOS_SUSPENSOS.getCodigo()).append("' and sco.quant_contratos_suspensos >= psc_cs.PPO_LIM_INFERIOR and sco.quant_contratos_suspensos <= psc_cs.PPO_LIM_SUPERIOR), sco.pont_contratos_suspensos), ");
            query.append("    sco.pont_inadimplencia        = coalesce((select psc_in.PPO_PONTUACAO from tb_param_pontuacao_rse psc_in where psc_in.TPO_CODIGO = '").append(TipoPontuacaoEnum.PORCENTAGEM_INADIMPLENCIA.getCodigo()).append("' and coalesce(sco.porcent_inadimplencia,0) >= psc_in.PPO_LIM_INFERIOR and coalesce(sco.porcent_inadimplencia,0) <= psc_in.PPO_LIM_SUPERIOR), 0), ");
            query.append("    sco.pont_margem_utilizada     = coalesce((select psc_mu.PPO_PONTUACAO from tb_param_pontuacao_rse psc_mu where psc_mu.TPO_CODIGO = '").append(TipoPontuacaoEnum.PORCENTAGEM_MARGEM_UTILIZADA.getCodigo()).append("' and sco.porcent_margem_utilizada >= psc_mu.PPO_LIM_INFERIOR and sco.porcent_margem_utilizada <= psc_mu.PPO_LIM_SUPERIOR), sco.pont_margem_utilizada) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            // Tabela auxiliar com a quantidade de leilões cancelados no último ano por RSE
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_cancelamento_leilao");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE `tmp_cancelamento_leilao` (`rse_codigo` varchar(32) NOT NULL DEFAULT '', `qtd_leiloes` int NOT NULL DEFAULT 0, PRIMARY KEY (`rse_codigo`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("insert into tmp_cancelamento_leilao (rse_codigo, qtd_leiloes) ");
            query.append("select ade.rse_codigo, count(distinct oca.ade_codigo) ");
            query.append("from tb_ocorrencia_autorizacao oca ");
            query.append("inner join tb_aut_desconto ade on (oca.ade_codigo = ade.ade_codigo) ");
            query.append("where oca.toc_codigo = '").append(CodedValues.TOC_CANCELAMENTO_LEILAO_COM_PROPOSTA).append("' ");
            query.append("  and oca.oca_data > date_sub(now(), interval 1 year) ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "ade", queryParams));
            query.append("group by ade.rse_codigo");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Popula pontuação total
            query.append("update tmp_score sco ");
            query.append("set sco.pont_total = (select ppo_pontuacao from tb_param_pontuacao_rse where tpo_codigo = '").append(TipoPontuacaoEnum.PONTUACAO_INICIAL.getCodigo()).append("') ");
            query.append("                   + coalesce((select qtd_leiloes from tmp_cancelamento_leilao cal where sco.rse_codigo = cal.rse_codigo), 0) ");
            query.append("                   * (select PPO_PONTUACAO from tb_param_pontuacao_rse where tpo_codigo = '").append(TipoPontuacaoEnum.PONTOS_PERDIDOS_POR_LEILAO_NAO_CONCRETIZADO.getCodigo()).append("') ");
            query.append("                   + sco.pont_contratos_suspensos ");
            query.append("                   + sco.pont_contratos_concluidos ");
            query.append("                   + sco.pont_margem_utilizada ");
            query.append("                   + sco.pont_inadimplencia");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Insere Servidores "Vazios" com pontuação padrão (Que não possuem nenhuma ADE no registro)
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_sem_score");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE `tmp_sem_score` (");
            query.append("`rse_codigo` varchar(32) NOT NULL DEFAULT '', ");
            query.append("PRIMARY KEY (`rse_codigo`) ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("insert into tmp_sem_score (rse_codigo) ");
            query.append("select rse.rse_codigo ");
            query.append("from tb_registro_servidor rse ");
            query.append("where rse.rse_codigo not in (select rse_codigo from tmp_score) ");
            query.append("and rse.srs_codigo in (").append(sqlJoin(CodedValues.SRS_ATIVOS)).append(")");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, true, null, queryParams));
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            query.append("insert into tmp_score (rse_codigo, pont_total) ");
            query.append("select tmp.rse_codigo, ");
            query.append("   (select ppo_pontuacao from tb_param_pontuacao_rse where tpo_codigo = '").append(TipoPontuacaoEnum.PONTUACAO_INICIAL.getCodigo()).append("')");
            query.append(" + (select ppo_pontuacao from tb_param_pontuacao_rse where tpo_codigo = '").append(TipoPontuacaoEnum.QTDE_CONTRATOS_SUSPENSOS.getCodigo()).append("' and ppo_lim_inferior <= 0 and ppo_lim_superior >= 0)");
            query.append(" + (select ppo_pontuacao from tb_param_pontuacao_rse where tpo_codigo = '").append(TipoPontuacaoEnum.QTDE_CONTRATOS_CONCLUIDOS.getCodigo()).append("' and ppo_lim_inferior <= 0 and ppo_lim_superior >= 0)");
            query.append(" + (select ppo_pontuacao from tb_param_pontuacao_rse where tpo_codigo = '").append(TipoPontuacaoEnum.PORCENTAGEM_MARGEM_UTILIZADA.getCodigo()).append("' and ppo_lim_inferior <= 0 and ppo_lim_superior >= 0)");
            query.append(" + (select ppo_pontuacao from tb_param_pontuacao_rse where tpo_codigo = '").append(TipoPontuacaoEnum.PORCENTAGEM_INADIMPLENCIA.getCodigo()).append("' and ppo_lim_inferior <= 0 and ppo_lim_superior >= 0)");
            query.append(" + coalesce((select qtd_leiloes from tmp_cancelamento_leilao cal where tmp.rse_codigo = cal.rse_codigo), 0)");
            query.append(" * (select PPO_PONTUACAO from tb_param_pontuacao_rse where tpo_codigo = '").append(TipoPontuacaoEnum.PONTOS_PERDIDOS_POR_LEILAO_NAO_CONCRETIZADO.getCodigo()).append("') ");
            query.append("from tmp_sem_score tmp ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Faz update na tb_registro_servidor
            query.append("update tb_registro_servidor rse ");
            query.append("inner join tmp_score sco on (rse.rse_codigo = sco.rse_codigo) ");
            query.append("set rse_pontuacao = sco.pont_total ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            LOG.debug("FIM Calcula pontuação de leilão dos servidores: " + DateHelper.getSystemDatetime());

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void zerarPontuacaoRseCsa(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            int rows = 0;
            final StringBuilder query = new StringBuilder();
            LOG.debug("Zera pontuação dos servidores para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            // apaga o valor de pontuacao
            query.append("delete from tb_pontuacao_rse_csa ");
            query.append("where csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "tb_pontuacao_rse_csa", queryParams));
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // insere o valor inicial da pontuacao, ou zero
            query.append("insert into tb_pontuacao_rse_csa (CSA_CODIGO, RSE_CODIGO, PON_VLR, PON_DATA) ");
            query.append("select '").append(escapeSql(csaCodigo)).append("', rse.rse_codigo, coalesce(max(ppr.ppr_pontuacao), 0), NOW() ");
            query.append("from tb_registro_servidor rse ");
            query.append("left outer join tb_param_pontuacao_rse_csa ppr on (ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' and ppr.tpo_codigo = '").append(TipoPontuacaoEnum.PONTUACAO_INICIAL).append("') ");
            query.append("where rse.srs_codigo in ('").append(joinWithEscapeSql(CodedValues.SRS_ATIVOS, "','")).append("') ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, true, null, queryParams));
            query.append("group by rse.rse_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void calcularPontuacaoRseCsaPercentualMargemUtilizada(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            int rows = 0;
            final StringBuilder query = new StringBuilder();
            LOG.debug("Calcula pontuação do percentual de margem utilizada dos servidores para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_margem_por_nse");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // cria tabela temporária para relacionar natureza de serviço com a incidência de margem
            query.append("CREATE TEMPORARY TABLE tb_tmp_margem_por_nse ( ");
            query.append("  mar_codigo smallint NOT NULL, ");
            query.append("  nse_codigo varchar(32) NOT NULL, ");
            query.append("  PRIMARY KEY (mar_codigo, nse_codigo), ");
            query.append("  KEY id01 (nse_codigo) ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // insere na tabela a incidência de margem por natureza de serviço para naturezas de serviço
            // que existam regras de pontuação para a CSA ou que esta possui convênios ativos
            query.append("INSERT INTO tb_tmp_margem_por_nse (nse_codigo, mar_codigo) ");
            query.append("SELECT svc.nse_codigo, min(cast(coalesce(nullif(pse3.pse_vlr, ''), '").append(CodedValues.INCIDE_MARGEM_SIM).append("') as unsigned)) ");
            query.append("FROM tb_servico svc ");
            query.append("LEFT OUTER JOIN tb_param_svc_consignante pse3 ON (svc.svc_codigo = pse3.svc_codigo AND pse3.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("') ");
            query.append("WHERE svc.nse_codigo IS NOT NULL ");
            query.append("  AND coalesce(nullif(pse3.pse_vlr, ''), '").append(CodedValues.INCIDE_MARGEM_SIM).append("') <> '").append(CodedValues.INCIDE_MARGEM_NAO).append("' ");
            query.append("  AND (EXISTS (");
            query.append("    SELECT 1 FROM tb_param_pontuacao_rse_csa ppr ");
            query.append("    WHERE ppr.tpo_codigo = '").append(TipoPontuacaoEnum.PORCENTAGEM_MARGEM_UTILIZADA.getCodigo()).append("' ");
            query.append("      AND ppr.nse_codigo = svc.nse_codigo ");
            query.append("      AND ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("'");
            query.append("  ) ");
            query.append("  OR EXISTS (");
            query.append("    SELECT 1 FROM tb_convenio cnv ");
            query.append("    WHERE cnv.svc_codigo = svc.svc_codigo ");
            query.append("      AND cnv.csa_codigo = '").append(escapeSql(csaCodigo)).append("'");
            query.append("      AND cnv.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("'");
            query.append("  ) ");
            query.append(") ");
            query.append("GROUP BY svc.nse_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_perc_margem_utilizada");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // cria tabela temporária para armazenar o percentual de utilização da margem por natureza de serviço
            query.append("CREATE TEMPORARY TABLE tb_tmp_perc_margem_utilizada ( ");
            query.append("  rse_codigo varchar(32) NOT NULL,  ");
            query.append("  nse_codigo varchar(32) NOT NULL,  ");
            query.append("  percentual int NOT NULL DEFAULT 0,  ");
            query.append("  pontuacao int NOT NULL DEFAULT 0,  ");
            query.append("  PRIMARY KEY (rse_codigo, nse_codigo), ");
            query.append("  KEY id01 (nse_codigo)  ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // não considerar apenas 5 anos pois aqui só pega contratos abertos
            query.append("insert into tb_tmp_perc_margem_utilizada (rse_codigo, nse_codigo, percentual) ");
            query.append("select rse.rse_codigo, tmp.nse_codigo, ");
            query.append("round( ");
            // total que consumido na natureza (multiplica por 100 para não ser valor decimal)
            query.append("  coalesce((select sum(coalesce(nullif(ade.ade_vlr_folha, 0), ade.ade_vlr)) * 100 ");
            query.append("   from tb_aut_desconto ade ");
            query.append("   inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("   inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("   inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("   where ade.rse_codigo = rse.rse_codigo ");
            query.append("     and svc.nse_codigo = tmp.nse_codigo ");
            query.append("     and coalesce(ade.ade_inc_margem, ").append(CodedValues.INCIDE_MARGEM_SIM).append(") = tmp.mar_codigo ");
            query.append("     and ade.sad_codigo not in ('").append(joinWithEscapeSql(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");

            query.append("  ), 0) / ( ");

            // margem rest + total consumido na margem
            query.append("    (case when tmp.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM).append(" then coalesce(rse.rse_margem_rest, 0) ");
            query.append("          when tmp.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_2).append(" then coalesce(rse.rse_margem_rest_2, 0) ");
            query.append("          when tmp.mar_codigo = ").append(CodedValues.INCIDE_MARGEM_SIM_3).append(" then coalesce(rse.rse_margem_rest_3, 0) ");
            query.append("          else coalesce(mrs.mrs_margem_rest, 0) ");
            query.append("     end) + coalesce((select sum(coalesce(nullif(ade.ade_vlr_folha, 0), ade.ade_vlr)) ");
            query.append("   from tb_aut_desconto ade  ");
            query.append("   inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("   inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("   inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("   where ade.rse_codigo = rse.rse_codigo ");
            query.append("     and ade.sad_codigo not in ('").append(joinWithEscapeSql(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");
            query.append("     and coalesce(ade.ade_inc_margem, ").append(CodedValues.INCIDE_MARGEM_SIM).append(") = tmp.mar_codigo ");
            query.append("     ), 0) ");

            query.append("  ), 0)  ");

            query.append("from tb_registro_servidor rse ");
            query.append("cross join tb_tmp_margem_por_nse tmp ");
            query.append("left outer join tb_margem_registro_servidor mrs on (rse.rse_codigo = mrs.rse_codigo and mrs.mar_codigo = tmp.mar_codigo) ");
            query.append("where rse.srs_codigo in ('").append(joinWithEscapeSql(CodedValues.SRS_ATIVOS, "','")).append("') ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, true, null, queryParams));
            query.append("group by rse.rse_codigo, tmp.nse_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza a pontuacao por natureza, baseado nos limites
            query.append("update tb_tmp_perc_margem_utilizada tmp ");
            query.append("inner join tb_param_pontuacao_rse_csa ppr on (ppr.nse_codigo = tmp.nse_codigo) ");
            query.append("set tmp.pontuacao = ppr.ppr_pontuacao ");
            query.append("where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("  and ppr.tpo_codigo = '").append(TipoPontuacaoEnum.PORCENTAGEM_MARGEM_UTILIZADA.getCodigo()).append("' ");
            query.append("  and tmp.percentual between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza a pontuacao para regra que não tem natureza de serviço, para os itens que ainda não tenha pontuação
            query.append("update tb_tmp_perc_margem_utilizada tmp ");
            query.append("set tmp.pontuacao = coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(TipoPontuacaoEnum.PORCENTAGEM_MARGEM_UTILIZADA.getCodigo()).append("' ");
            query.append("    and ppr.nse_codigo is null ");
            query.append("    and tmp.percentual between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where tmp.pontuacao = 0 ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza o somatório da pontuacao para o RSE
            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("set pon.pon_vlr = pon.pon_vlr + coalesce(( ");
            query.append("  select sum(tmp.pontuacao) ");
            query.append("  from tb_tmp_perc_margem_utilizada tmp  ");
            query.append("  where pon.rse_codigo = tmp.rse_codigo ");
            query.append("  group by tmp.rse_codigo ");
            query.append("), 0) ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    private void calcularPontuacaoRseCsaQtdeContratosPorStatus(String csaCodigo, String tipoEntidade, List<String> entCodigos, List<String> sadCodigos, TipoPontuacaoEnum tipoPontuacao, boolean geral, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            int rows = 0;
            final StringBuilder query = new StringBuilder();
            LOG.debug("Calcula pontuação da quantidade de contratos para os status (" + joinWithEscapeSql(sadCodigos, ",") + ") dos servidores para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_qtd_ade_por_status");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // cria tabela temporária para armazenar a quantidade de contratos do status informado por servidor
            query.append("CREATE TEMPORARY TABLE tb_tmp_qtd_ade_por_status ( ");
            query.append("  rse_codigo varchar(32) NOT NULL,  ");
            query.append("  quantidade int NOT NULL DEFAULT 0,  ");
            query.append("  pontuacao int NOT NULL DEFAULT 0,  ");
            query.append("  PRIMARY KEY (rse_codigo) ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // considerar contratos criados nos últimos 5 anos
            query.append("insert into tb_tmp_qtd_ade_por_status (rse_codigo, quantidade) ");
            query.append("select ade.rse_codigo, count(*) ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("where ade.sad_codigo in ('").append(joinWithEscapeSql(sadCodigos, "','")).append("') ");
            if (!geral) {
                query.append("and cnv.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            }
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "ade", queryParams));
            query.append("group by ade.rse_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza a pontuacao de acordo com a regra de pontuação passada por parâmetro
            query.append("update tb_tmp_qtd_ade_por_status tmp ");
            query.append("set tmp.pontuacao = coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(tipoPontuacao.getCodigo()).append("' ");
            query.append("    and tmp.quantidade between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where tmp.pontuacao = 0 ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza o somatório da pontuacao para o RSE
            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("inner join tb_tmp_qtd_ade_por_status tmp on (pon.rse_codigo = tmp.rse_codigo) ");
            query.append("set pon.pon_vlr = pon.pon_vlr + tmp.pontuacao ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeContratosSuspensos(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaQtdeContratosPorStatus(csaCodigo, tipoEntidade, entCodigos, CodedValues.SAD_CODIGOS_SUSPENSOS, TipoPontuacaoEnum.QTDE_CONTRATOS_SUSPENSOS, false, responsavel);
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeContratosSuspensosGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaQtdeContratosPorStatus(csaCodigo, tipoEntidade, entCodigos, CodedValues.SAD_CODIGOS_SUSPENSOS, TipoPontuacaoEnum.QTDE_CONTRATOS_SUSPENSOS, true, responsavel);
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeContratosConcluidos(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaQtdeContratosPorStatus(csaCodigo, tipoEntidade, entCodigos, List.of(CodedValues.SAD_CONCLUIDO), TipoPontuacaoEnum.QTDE_CONTRATOS_CONCLUIDOS, false, responsavel);
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeContratosConcluidosGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaQtdeContratosPorStatus(csaCodigo, tipoEntidade, entCodigos, List.of(CodedValues.SAD_CONCLUIDO), TipoPontuacaoEnum.QTDE_CONTRATOS_CONCLUIDOS, true, responsavel);
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeContratosLiquidados(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaQtdeContratosPorStatus(csaCodigo, tipoEntidade, entCodigos, List.of(CodedValues.SAD_LIQUIDADA), TipoPontuacaoEnum.QTDE_CONTRATOS_CONCLUIDOS, false, responsavel);
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeContratosLiquidadosGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaQtdeContratosPorStatus(csaCodigo, tipoEntidade, entCodigos, CodedValues.SAD_CODIGOS_SUSPENSOS, TipoPontuacaoEnum.QTDE_CONTRATOS_SUSPENSOS, true, responsavel);
    }

    @Override
    public void calcularPontuacaoRseCsaPercentualInadimplencia(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaPercentualInadimplencia(csaCodigo, tipoEntidade, entCodigos, false, TipoPontuacaoEnum.PORCENTAGEM_INADIMPLENCIA, responsavel);
    }

    @Override
    public void calcularPontuacaoRseCsaPercentualInadimplenciaGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaPercentualInadimplencia(csaCodigo, tipoEntidade, entCodigos, true, TipoPontuacaoEnum.PORCENTAGEM_INADIMPLENCIA_GERAL, responsavel);
    }

    private void calcularPontuacaoRseCsaPercentualInadimplencia(String csaCodigo, String tipoEntidade, List<String> entCodigos, boolean geral, TipoPontuacaoEnum tipoPontuacao, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            LOG.debug("Calcula pontuação do percentual de inadimplência dos servidores para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            final StringBuilder query = new StringBuilder();
            int rows = 0;

            final List<String> spdCodigosProcessadas = List.of(CodedValues.SPD_REJEITADAFOLHA, CodedValues.SPD_LIQUIDADAFOLHA);

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_percent_inadimplencia");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE tb_tmp_percent_inadimplencia (");
            query.append("  rse_codigo varchar(32) NOT NULL, ");
            query.append("  percentual int NOT NULL DEFAULT 0, ");
            query.append("  pontuacao int NOT NULL DEFAULT 0, ");
            query.append("  PRIMARY KEY (rse_codigo) ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("insert into tb_tmp_percent_inadimplencia (rse_codigo, percentual) ");
            query.append("select rse.rse_codigo, ");
            query.append("round(sum(case when prd.spd_codigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("' then 1 else 0 end) * 100 / count(*), 0) ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("where rse.srs_codigo in (").append(sqlJoin(CodedValues.SRS_ATIVOS)).append(") ");
            query.append("and svc.nse_codigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
            query.append("and ade.ade_data between date_add(now(), interval -5 year) and now() ");
            query.append("and prd.spd_codigo in ('").append(joinWithEscapeSql(spdCodigosProcessadas, "','")).append("') ");
            if (!geral) {
                query.append("and cnv.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            }
            query.append(getComplementoWhere(tipoEntidade, entCodigos, true, null, queryParams));
            query.append("group by rse.rse_codigo");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza a pontuacao de acordo com a regra de pontuação do percentual de inadimplência
            query.append("update tb_tmp_percent_inadimplencia tmp ");
            query.append("set tmp.pontuacao = coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(tipoPontuacao.getCodigo()).append("' ");
            query.append("    and tmp.percentual between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where tmp.pontuacao = 0 ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza o somatório da pontuacao para o RSE
            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("inner join tb_tmp_percent_inadimplencia tmp on (pon.rse_codigo = tmp.rse_codigo) ");
            query.append("set pon.pon_vlr = pon.pon_vlr + tmp.pontuacao ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeLeilaoNaoConcretizado(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            LOG.debug("Calcula pontuação da quantidade de leilões não concretizados dos servidores para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            final StringBuilder query = new StringBuilder();
            int rows = 0;

            // Tabela auxiliar com a quantidade de leilões cancelados no último ano por RSE
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_leilao_n_concretizado");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE tb_tmp_leilao_n_concretizado (");
            query.append("  rse_codigo varchar(32) NOT NULL, ");
            query.append("  quantidade int NOT NULL DEFAULT 0, ");
            query.append("  pontuacao int NOT NULL DEFAULT 0, ");
            query.append("  PRIMARY KEY (rse_codigo) ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("insert into tb_tmp_leilao_n_concretizado (rse_codigo, quantidade) ");
            query.append("select ade.rse_codigo, count(distinct oca.ade_codigo) ");
            query.append("from tb_ocorrencia_autorizacao oca ");
            query.append("inner join tb_aut_desconto ade on (oca.ade_codigo = ade.ade_codigo) ");
            query.append("where oca.toc_codigo = '").append(CodedValues.TOC_CANCELAMENTO_LEILAO_COM_PROPOSTA).append("' ");
            query.append("  and oca.oca_data > date_sub(now(), interval 1 year) ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "ade", queryParams));
            query.append("group by ade.rse_codigo");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza a pontuacao de acordo com a regra de pontuação passada por parâmetro
            query.append("update tb_tmp_leilao_n_concretizado tmp ");
            query.append("set tmp.pontuacao = quantidade * coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(TipoPontuacaoEnum.PONTOS_PERDIDOS_POR_LEILAO_NAO_CONCRETIZADO.getCodigo()).append("' ");
            query.append("    and tmp.quantidade between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where tmp.pontuacao = 0 ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza o somatório da pontuacao para o RSE
            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("inner join tb_tmp_leilao_n_concretizado tmp on (pon.rse_codigo = tmp.rse_codigo) ");
            query.append("set pon.pon_vlr = pon.pon_vlr + tmp.pontuacao ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void calcularPontuacaoRseCsaFaixaEtariaEmAnos(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            LOG.debug("Calcula pontuação por faixa etária dos servidores para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            final StringBuilder query = new StringBuilder();
            int rows = 0;

            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("inner join tb_registro_servidor rse on (pon.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) ");
            query.append("set pon.pon_vlr = pon.pon_vlr + coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(TipoPontuacaoEnum.FAIXA_ETARIA_EM_ANOS.getCodigo()).append("' ");
            query.append("    and truncate(datediff(curdate(), ser.ser_data_nasc) / 365, 0) between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("  and ser.ser_data_nasc is not null ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));

            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void calcularPontuacaoRseCsaTempoServicoEmMeses(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            LOG.debug("Calcula pontuação por faixa por tempo de serviço dos servidores para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            final StringBuilder query = new StringBuilder();
            int rows = 0;

            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("inner join tb_registro_servidor rse on (pon.rse_codigo = rse.rse_codigo) ");
            query.append("set pon.pon_vlr = pon.pon_vlr + coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(TipoPontuacaoEnum.FAIXA_TEMPO_SERVICO_EM_MESES.getCodigo()).append("' ");
            query.append("    and truncate(datediff(curdate(), rse.rse_data_admissao) / 30, 0) between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("  and rse.rse_data_admissao is not null ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));

            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void calcularPontuacaoRseCsaFaixaSalarial(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            LOG.debug("Calcula pontuação por faixa salarial servidores para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            final StringBuilder query = new StringBuilder();
            int rows = 0;

            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("inner join tb_registro_servidor rse on (pon.rse_codigo = rse.rse_codigo) ");
            query.append("set pon.pon_vlr = pon.pon_vlr + coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(TipoPontuacaoEnum.FAIXA_SALARIAL.getCodigo()).append("' ");
            query.append("    and rse.rse_salario between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("  and rse.rse_salario is not null ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));

            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void calcularPontuacaoRseCsaFaixaValorMargem(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException("Não implementado");
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeContratosAfetadosJudicial(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaQtdeContratosAfetadosJudicial(csaCodigo, tipoEntidade, entCodigos, false, TipoPontuacaoEnum.QTDE_CONTRATOS_AFETADOS_DECISAO_JUDICIAL, responsavel);
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeContratosAfetadosJudicialGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        calcularPontuacaoRseCsaQtdeContratosAfetadosJudicial(csaCodigo, tipoEntidade, entCodigos, true, TipoPontuacaoEnum.QTDE_CONTRATOS_AFETADOS_DECISAO_JUDICIAL_GERAL, responsavel);
    }

    private void calcularPontuacaoRseCsaQtdeContratosAfetadosJudicial(String csaCodigo, String tipoEntidade, List<String> entCodigos, boolean geral, TipoPontuacaoEnum tipoPontuacao, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            int rows = 0;
            final StringBuilder query = new StringBuilder();
            LOG.debug("Calcula pontuação da quantidade de contratos afetados por decisão judicial dos servidores para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_qtd_ade_decisao_judicial");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // cria tabela temporária para armazenar a quantidade de contratos afetados por decisão judicial por servidor
            query.append("CREATE TEMPORARY TABLE tb_tmp_qtd_ade_decisao_judicial ( ");
            query.append("  rse_codigo varchar(32) NOT NULL,  ");
            query.append("  quantidade int NOT NULL DEFAULT 0,  ");
            query.append("  pontuacao int NOT NULL DEFAULT 0,  ");
            query.append("  PRIMARY KEY (rse_codigo) ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // considerar contratos criados nos últimos 5 anos
            query.append("insert into tb_tmp_qtd_ade_decisao_judicial (rse_codigo, quantidade) ");
            query.append("select ade.rse_codigo, count(*) ");
            query.append("from tb_dados_autorizacao_desconto dad ");
            query.append("inner join tb_aut_desconto ade on (dad.ade_codigo = ade.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("where dad.tda_codigo = '").append(CodedValues.TDA_AFETADA_DECISAO_JUDICIAL).append("' ");
            if (!geral) {
                query.append("and cnv.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            }
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "ade", queryParams));
            query.append("group by ade.rse_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza a pontuacao de acordo com a regra de pontuação passada por parâmetro
            query.append("update tb_tmp_qtd_ade_decisao_judicial tmp ");
            query.append("set tmp.pontuacao = coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(tipoPontuacao.getCodigo()).append("' ");
            query.append("    and tmp.quantidade between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where tmp.pontuacao = 0 ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // atualiza o somatório da pontuacao para o RSE
            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("inner join tb_tmp_qtd_ade_decisao_judicial tmp on (pon.rse_codigo = tmp.rse_codigo) ");
            query.append("set pon.pon_vlr = pon.pon_vlr + tmp.pontuacao ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void calcularPontuacaoRseCsaQtdeColaboradoresCse(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            LOG.debug("Calcula pontuação por quantidade de colaboradores do CSE para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            final StringBuilder query = new StringBuilder();
            int rows = 0;

            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("set pon.pon_vlr = pon.pon_vlr + coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(TipoPontuacaoEnum.QTDE_COLABORADORES_CSE.getCodigo()).append("' ");
            query.append("    and (");
            query.append("       select count(*) from tb_registro_servidor rse where rse.srs_codigo in ('").append(joinWithEscapeSql(CodedValues.SRS_ATIVOS, "','")).append("') ");
            query.append("    ) between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));

            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    @Override
    public void calcularPontuacaoRseCsaMediaSalarialCse(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            LOG.debug("Calcula pontuação por média salarial do CSE para CSA '" + csaCodigo + "': " + DateHelper.getSystemDatetime());

            final StringBuilder query = new StringBuilder();
            int rows = 0;

            query.append("update tb_pontuacao_rse_csa pon ");
            query.append("set pon.pon_vlr = pon.pon_vlr + coalesce((");
            query.append("  select ppr.ppr_pontuacao ");
            query.append("  from tb_param_pontuacao_rse_csa ppr ");
            query.append("  where ppr.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append("    and ppr.tpo_codigo = '").append(TipoPontuacaoEnum.MEDIA_SALARIAL_CSE.getCodigo()).append("' ");
            query.append("    and (");
            query.append("       select avg(rse.rse_salario) from tb_registro_servidor rse where rse.srs_codigo in ('").append(joinWithEscapeSql(CodedValues.SRS_ATIVOS, "','")).append("') and rse.rse_salario is not null ");
            query.append("    ) between ppr.ppr_lim_inferior and ppr.ppr_lim_superior ");
            query.append("), 0) ");
            query.append("where pon.csa_codigo = '").append(escapeSql(csaCodigo)).append("' ");
            query.append(getComplementoWhere(tipoEntidade, entCodigos, false, "pon", queryParams));

            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }


    @Override
    public void calcularPontuacaoRseCsaPercentualTurnoverCse(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException("Não implementado");
    }

    @Override
    public void calcularPontuacaoRseCsaMediaMargemLivreCse(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException("Não implementado");
    }

    @Override
    public void calcularPontuacaoRseCsaPercentualDescontos(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException("Não implementado");
    }


    /**
     * De acordo com o tipo da entidade, gera o complemento da query para
     * incluir restrição que selecione apenas os servidores da entidade.
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @return
     */
    private String getComplementoWhere(String tipoEntidade, List<String> entCodigos, boolean temRse, String aliasJuncaoRse, MapSqlParameterSource queryParams) {
        final StringBuilder where = new StringBuilder();

        if (entCodigos != null && entCodigos.size() > 0) {
            if (temRse) {
                if (tipoEntidade.equalsIgnoreCase("EST")) {
                    where.append(" and exists (");
                    where.append(" select 1 from tb_orgao org ");
                    where.append(" where rse.org_codigo = org.org_codigo");
                    where.append(" and org.est_codigo in (:entCodigos)");
                    where.append(")");
                    queryParams.addValue("entCodigos", entCodigos);

                } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                    where.append(" and rse.org_codigo in (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);

                } else if (tipoEntidade.equalsIgnoreCase("RSE")) {
                    where.append(" and rse.rse_codigo in (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }

            } else if (!TextHelper.isNull(aliasJuncaoRse)) {
                if (tipoEntidade.equalsIgnoreCase("EST")) {
                    where.append(" and exists (");
                    where.append(" select 1 from tb_registro_servidor rse ");
                    where.append(" inner join tb_orgao org on (rse.org_codigo = org.org_codigo) ");
                    where.append(" where rse.rse_codigo = ").append(aliasJuncaoRse).append(".rse_codigo");
                    where.append(" and org.est_codigo in (:entCodigos)");
                    where.append(")");
                    queryParams.addValue("entCodigos", entCodigos);

                } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                    where.append(" and exists (");
                    where.append(" select 1 from tb_registro_servidor rse ");
                    where.append(" where rse.rse_codigo = ").append(aliasJuncaoRse).append(".rse_codigo");
                    where.append(" and rse.org_codigo in (:entCodigos)");
                    where.append(")");
                    queryParams.addValue("entCodigos", entCodigos);

                } else if (tipoEntidade.equalsIgnoreCase("RSE")) {
                    where.append(" and ").append(aliasJuncaoRse).append(".rse_codigo in (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }

        return where.toString();
    }
}