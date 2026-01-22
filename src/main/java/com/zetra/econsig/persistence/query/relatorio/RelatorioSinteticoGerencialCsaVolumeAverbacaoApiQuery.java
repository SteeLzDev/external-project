package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;

/**
 * <p>Title: RelatorioVolumeAverbacaoCsaQuery</p>
 * <p>Description: Recuperar voluma averbação por período</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaVolumeAverbacaoApiQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT Y.tipo as status, COALESCE(X.quantidade, '0') as quantidade_api, COALESCE(Y.quantidade_averbacao, '0') as quantidade");
        corpo.append(" FROM (");
        corpo.append(" select ");
        corpo.append(" case when a = 1 then 1 ");
        corpo.append("      when a = 2 then 2");
        corpo.append("      when a = 3 then 3");
        corpo.append("      when a = 4 then 4");
        corpo.append("      when a = 5 then 5");
        corpo.append("      when a = 6 then 6");
        corpo.append("      when a = 7 then 7");
        corpo.append(" end as codigo,");
        corpo.append(" case when a = 1 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.diaria", responsavel)).append("'");
        corpo.append("      when a = 2 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.semana", responsavel)).append("'");
        corpo.append("      when a = 3 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.quinzena", responsavel)).append("'");
        corpo.append("      when a = 4 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.mes", responsavel)).append("'");
        corpo.append("      when a = 5 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.trimestre", responsavel)).append("'");
        corpo.append("      when a = 6 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.semestre", responsavel)).append("'");
        corpo.append("      when a = 7 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.ano", responsavel)).append("'");
        corpo.append(" end as tipo,");
        corpo.append(" case when a = 1 then diario ");
        corpo.append("      when a = 2 then semanal");
        corpo.append("      when a = 3 then quinzenal");
        corpo.append("      when a = 4 then mensal");
        corpo.append("      when a = 5 then trimestral");
        corpo.append("      when a = 6 then simestral");
        corpo.append("      when a = 7 then anual ");
        corpo.append(" end as quantidade");
        corpo.append(" from (");
        corpo.append(" SELECT");
        corpo.append("     sum(case when tlog.log_data between add_day(:periodoIni, -1 ) and :periodoFim then 1 else 0 end) as 'diario',");
        corpo.append("     sum(case when tlog.log_data between add_day(:periodoIni, -7 ) and :periodoFim then 1 else 0 end) as 'semanal',");
        corpo.append("  sum(case when tlog.log_data between add_day(:periodoIni, -15 ) and :periodoFim then 1 else 0 end) as 'quinzenal',");
        corpo.append("  sum(case when tlog.log_data between add_month(:periodoIni, -1 ) and :periodoFim then 1 else 0 end) as 'mensal',");
        corpo.append("  sum(case when tlog.log_data between add_month(:periodoIni, -3 ) and :periodoFim then 1 else 0 end) as 'trimestral',");
        corpo.append("  sum(case when tlog.log_data between add_month(:periodoIni, -6 ) and :periodoFim then 1 else 0 end) as 'simestral',");
        corpo.append("  sum(case when tlog.log_data between add_month(:periodoIni, -12 ) and :periodoFim then 1 else 0 end) as 'anual'");
        corpo.append(" FROM tb_log tlog");
        corpo.append(" INNER JOIN tb_funcao fun on (fun.fun_codigo = tlog.fun_codigo)");
        corpo.append(" INNER JOIN tb_usuario_csa usucsa on (usucsa.usu_codigo = tlog.usu_codigo)");
        corpo.append(" where fun.fun_codigo in ('57','26','76')");
        corpo.append(" and log_canal='2'");
        corpo.append(" and tlog.log_data between add_month(:periodoIni , -12) and :periodoFim");
        corpo.append(" AND usucsa.csa_codigo = :csaCodigo");
        corpo.append(" ) as x_in");
        corpo.append(" cross join tb_aux_pivot) as X");
        corpo.append(" inner join ");
        corpo.append(" (");
        corpo.append(" select ");
        corpo.append(" case when a = 1 then 1 ");
        corpo.append("      when a = 2 then 2");
        corpo.append("      when a = 3 then 3");
        corpo.append("      when a = 4 then 4");
        corpo.append("      when a = 5 then 5");
        corpo.append("      when a = 6 then 6");
        corpo.append("      when a = 7 then 7");
        corpo.append(" end as codigo,");
        corpo.append(" case when a = 1 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.diaria", responsavel)).append("'");
        corpo.append("      when a = 2 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.semana", responsavel)).append("'");
        corpo.append("      when a = 3 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.quinzena", responsavel)).append("'");
        corpo.append("      when a = 4 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.mes", responsavel)).append("'");
        corpo.append("      when a = 5 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.trimestre", responsavel)).append("'");
        corpo.append("      when a = 6 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.semestre", responsavel)).append("'");
        corpo.append("      when a = 7 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.ano", responsavel)).append("'");
        corpo.append(" end as tipo,");
        corpo.append(" case when a = 1 then diario_qnt ");
        corpo.append("      when a = 2 then semanal_qnt ");
        corpo.append("      when a = 3 then quinzenal_qnt ");
        corpo.append("      when a = 4 then mensal_qnt ");
        corpo.append("      when a = 5 then trimestral_qnt ");
        corpo.append("      when a = 6 then semestre_qnt ");
        corpo.append("      when a = 7 then anual_qnt  ");
        corpo.append(" end as quantidade_averbacao");
        corpo.append(" from (");
        corpo.append(" SELECT");
        corpo.append("     sum(case when ade.ade_data BETWEEN add_day(:periodoIni, -1 ) and :periodoFim then 1 else 0 end) as 'diario_qnt', ");
        corpo.append("     sum(case when ade.ade_data BETWEEN add_day(:periodoIni, -7 ) and :periodoFim then 1 else 0 end) as 'semanal_qnt', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_day(:periodoIni, -15 ) and :periodoFim then 1 else 0 end) as 'quinzenal_qnt', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni, -1 ) and :periodoFim then 1 else 0 end) as 'mensal_qnt', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni, -3 ) and :periodoFim then 1 else 0 end) as 'trimestral_qnt', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni, -6 ) and :periodoFim then 1 else 0 end) as 'semestre_qnt', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni, -12 ) and :periodoFim then 1 else 0 end) as 'anual_qnt' ");
        corpo.append(" FROM tb_aut_desconto ade ");
        corpo.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
        corpo.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        corpo.append(" INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
        corpo.append(" WHERE ade_data BETWEEN add_month(:periodoIni, -12 ) and :periodoFim ");
        corpo.append(" AND csa.csa_codigo = :csaCodigo");
        corpo.append(" ) as X");
        corpo.append(" cross join tb_aux_pivot_1) as Y ON (X.codigo = Y.codigo)");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        try {
        	defineValorClausulaNomeada("periodoIni", DateHelper.parse(DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss"), query);
        	defineValorClausulaNomeada("periodoFim", DateHelper.parse(DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss"), query);
        } catch (ParseException ex) {
            throw new HQueryException("mensagem.erro.data.fim.parse.invalido",  (AcessoSistema) null);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            "STATUS",
            "QUANTIDADE_API",
            "QUANTIDADE",
        };
    }
}
