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
public class RelatorioSinteticoGerencialCsaVolumeAverbacaoQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();
        corpo.append(" select  ");
        corpo.append(" case when a = 1 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.diaria", responsavel)).append("'");
        corpo.append("      when a = 2 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.semana", responsavel)).append("'");
        corpo.append("      when a = 3 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.quinzena", responsavel)).append("'");
        corpo.append("      when a = 4 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.mes", responsavel)).append("'");
        corpo.append("      when a = 5 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.trimestre", responsavel)).append("'");
        corpo.append("      when a = 6 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.semestre", responsavel)).append("'");
        corpo.append("      when a = 7 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.averbacao.descricao.ano", responsavel)).append("'");
        corpo.append(" end as STATUS, ");
        corpo.append(" case when a = 1 then diario  ");
        corpo.append("      when a = 2 then semanal ");
        corpo.append("      when a = 3 then quinzenal ");
        corpo.append("      when a = 4 then mensal ");
        corpo.append("      when a = 5 then trimestral ");
        corpo.append("      when a = 6 then semestre  ");
        corpo.append("      when a = 7 then anual  ");
        corpo.append(" end as QUANTIDADE, ");
        corpo.append(" case when a = 1 then diario_vlr  ");
        corpo.append("      when a = 2 then semanal_vlr ");
        corpo.append("      when a = 3 then quinzenal_vlr ");
        corpo.append("      when a = 4 then mensal_vlr ");
        corpo.append("      when a = 5 then trimestral_vlr ");
        corpo.append("      when a = 6 then semestre_vlr ");
        corpo.append("      when a = 7 then anual_vlr  ");
        corpo.append(" end as VLR_TOTAL ");
        corpo.append(" from ( ");
        corpo.append(" SELECT ");
        corpo.append("     sum(case when ade.ade_data BETWEEN add_day(:periodoIni, -1 ) and :periodoFim then 1 else 0 end) as 'diario', ");
        corpo.append("     sum(case when ade.ade_data BETWEEN add_day(:periodoIni , -7 ) and :periodoFim then 1 else 0 end) as 'semanal', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_day(:periodoIni , -15 ) and :periodoFim then 1 else 0 end) as 'quinzenal', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni , -1 ) and :periodoFim then 1 else 0 end) as 'mensal', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni , -3 ) and :periodoFim then 1 else 0 end) as 'trimestral', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni , -6 ) and :periodoFim then 1 else 0 end) as 'semestre', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni , -12 ) and :periodoFim then 1 else 0 end) as 'anual', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_day(:periodoIni , -1 ) and :periodoFim then ade.ade_vlr else 0 end) as 'diario_vlr', ");
        corpo.append("     sum(case when ade.ade_data BETWEEN add_day(:periodoIni , -7 ) and :periodoFim then ade.ade_vlr else 0 end) as 'semanal_vlr', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_day(:periodoIni , -15 ) and :periodoFim then ade.ade_vlr else 0 end) as 'quinzenal_vlr', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni , -1 ) and :periodoFim then ade.ade_vlr else 0 end) as 'mensal_vlr', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni , -3 ) and :periodoFim then ade.ade_vlr else 0 end) as 'trimestral_vlr', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni , -6 ) and :periodoFim then ade.ade_vlr else 0 end) as 'semestre_vlr', ");
        corpo.append("  sum(case when ade.ade_data BETWEEN add_month(:periodoIni , -12 ) and :periodoFim then ade.ade_vlr else 0 end) as 'anual_vlr' ");
        corpo.append(" FROM tb_aut_desconto ade ");
        corpo.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
        corpo.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        corpo.append(" INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
        corpo.append(" WHERE ade_data BETWEEN add_month(:periodoIni , -12 ) and :periodoFim  ");
        corpo.append(" AND csa.csa_codigo = :csaCodigo");
        corpo.append(" ) as X ");
        corpo.append(" cross join tb_aux_pivot ");

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
            "QUANTIDADE",
            "VLR_TOTAL",
        };
    }
}
