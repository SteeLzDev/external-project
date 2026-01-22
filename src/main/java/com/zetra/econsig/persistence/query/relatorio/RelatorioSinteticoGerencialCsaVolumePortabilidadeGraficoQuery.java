package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioVolumeAverbacaoCsaQuery</p>
 * <p>Description: Recuperar voluma averbação por período</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaVolumePortabilidadeGraficoQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String nseCompra = CodedValues.TNT_CONTROLE_COMPRA;

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" select ");
        corpo.append(" case when a = 1 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.volume.portabilidade.compra", responsavel)).append("'");
        corpo.append("      when a = 2 then '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.volume.portabilidade.venda", responsavel)).append("'");
        corpo.append(" end as tipo,");
        corpo.append(" case when a = 1 then vendida ");
        corpo.append("      when a = 2 then comprada");
        corpo.append(" end as percentual");
        corpo.append(" FROM (");
        corpo.append(" SELECT");
        corpo.append("     COALESCE(ROUND((SUM(CASE WHEN vendida > 0 THEN vendida ELSE 0 END) / total_agrupado.tt) * 100, 2),0) AS vendida,");
        corpo.append("     COALESCE(ROUND((SUM(CASE WHEN comprada > 0 THEN comprada ELSE 0 END) / total_agrupado.tt) * 100, 2),0) AS comprada");
        corpo.append(" from (SELECT");
        corpo.append("     csa.csa_codigo,");
        corpo.append("     SUM(CASE WHEN rad.ade_codigo_origem = ade.ade_codigo THEN 1 ELSE 0 END) AS 'vendida',");
        corpo.append("     SUM(CASE WHEN rad.ade_codigo_destino = ade.ade_codigo THEN 1 ELSE 0 END) AS 'comprada'");
        corpo.append(" from tb_aut_desconto ade");
        corpo.append(" inner join tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER join tb_consignataria csa USING (csa_codigo)");
        corpo.append(" INNER join tb_relacionamento_autorizacao rad ON (rad.tnt_codigo = :nseCompra AND (rad.ade_codigo_origem = ade.ade_codigo OR rad.ade_codigo_destino = ade.ade_codigo))");
        corpo.append(" where rad.rad_data BETWEEN add_month(:periodoIni, -6 ) AND :periodoFim");
        corpo.append(" AND csa_codigo = :csaCodigo ");
        corpo.append(" ) as y");
        corpo.append(" CROSS JOIN ( ");
        corpo.append("      SELECT");
        corpo.append("      csa.csa_codigo, count(*) as tt");
        corpo.append("      from tb_aut_desconto ade");
        corpo.append("  inner join tb_verba_convenio vco USING (vco_codigo)");
        corpo.append("  INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append("  INNER join tb_consignataria csa USING (csa_codigo)");
        corpo.append("  INNER join tb_relacionamento_autorizacao rad ON (rad.tnt_codigo = :nseCompra AND (rad.ade_codigo_origem = ade.ade_codigo OR rad.ade_codigo_destino = ade.ade_codigo))");
        corpo.append("  where rad.rad_data BETWEEN add_month(:periodoIni, -6 ) AND :periodoFim");
        corpo.append("  AND csa_codigo = :csaCodigo ");
        corpo.append("  GROUP by csa.csa_codigo");
        corpo.append(" ) AS total_agrupado ON (y.csa_codigo = total_agrupado.csa_codigo)");
        corpo.append(" ) as X");
        corpo.append(" cross join tb_aux_pivot");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("nseCompra", nseCompra, query);

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
            "TIPO",
            "PERCENTUAL",
        };
    }
}
