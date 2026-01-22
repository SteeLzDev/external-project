package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;

/**
 * <p>Title: RelatorioSinteticoGerencialCsaUltimoMovimentoQuery</p>
 * <p>Description: Recupera dados do último retorno processado</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaUltimoMovimentoQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;
    public String ultimoPeriodoProcessado;
    public String AntesUltimoPeriodoProcessado;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT nse.nse_descricao , 'i' as tipoNumero, null as tipoSimbolo, '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.ultimo.movimento.ativa.bloqueada", responsavel)).append("' AS volume, count(*) as quantidade, '0' as 'percentual'");
        corpo.append(" FROM tb_aut_desconto ade");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse USING (nse_codigo)");
        corpo.append(" INNER JOIN tb_parcela_desconto prd USING (ade_codigo)");
        corpo.append(" WHERE csa_codigo = :csaCodigo");
        corpo.append(" AND prd.prd_data_desconto = :AntesUltimoPeriodoProcessado");
        corpo.append(" GROUP BY nse.nse_codigo");
        corpo.append(" UNION ALL ");
        corpo.append(" SELECT nse.nse_descricao , 'i' as tipoNumero, null as tipoSimbolo, '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.ultimo.movimento.enviada", responsavel)).append("' AS volume, count(*) as quantidade, ROUND(((COUNT(prd.ade_codigo)-total_passado.tt)/total_passado.tt)*100,2) as 'percentual'");
        corpo.append(" FROM tb_aut_desconto ade");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_parcela_desconto prd USING (ade_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse USING (nse_codigo)");
        corpo.append(" CROSS JOIN ( ");
        corpo.append("             SELECT COUNT(*) as tt, nse.nse_codigo");
        corpo.append(" FROM tb_aut_desconto ade");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse USING (nse_codigo)");
        corpo.append(" INNER JOIN tb_parcela_desconto prd USING (ade_codigo)");
        corpo.append("         WHERE csa_codigo = :csaCodigo");
        corpo.append("     AND prd.prd_data_desconto = :AntesUltimoPeriodoProcessado");
        corpo.append(" GROUP BY nse.nse_codigo");
        corpo.append(" ) AS total_passado ON (total_passado.nse_codigo = nse.nse_codigo)");
        corpo.append(" WHERE csa_codigo = :csaCodigo");
        corpo.append(" AND prd.prd_data_desconto = :ultimoPeriodoProcessado");
        corpo.append(" GROUP BY nse.nse_codigo");
        corpo.append(" UNION ALL");
        corpo.append(" SELECT nse.nse_descricao , 'd' as tipoNumero, 'm' as tipoSimbolo, '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.ultimo.movimento.vlr.medio", responsavel)).append("' AS volume, ROUND(AVG(prd.prd_vlr_previsto),2) as quantidade, ROUND(((AVG(prd.prd_vlr_previsto)-total_passado.tt)/total_passado.tt)*100,2) as 'percentual'");
        corpo.append(" FROM tb_aut_desconto ade");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse USING (nse_codigo)");
        corpo.append(" INNER JOIN tb_parcela_desconto prd USING (ade_codigo)");
        corpo.append(" CROSS JOIN ( ");
        corpo.append("             SELECT to_decimal(AVG(prd.prd_vlr_previsto),5,2) as tt, nse.nse_codigo");
        corpo.append(" FROM tb_aut_desconto ade");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse USING (nse_codigo)");
        corpo.append(" INNER JOIN tb_parcela_desconto prd USING (ade_codigo)");
        corpo.append("         WHERE csa_codigo = :csaCodigo");
        corpo.append("     AND prd.prd_data_desconto = :AntesUltimoPeriodoProcessado");
        corpo.append(" GROUP BY nse.nse_codigo");
        corpo.append(" ) AS total_passado ON (total_passado.nse_codigo = nse.nse_codigo)");
        corpo.append(" WHERE csa_codigo = :csaCodigo");
        corpo.append(" AND prd.prd_data_desconto = :ultimoPeriodoProcessado");
        corpo.append(" GROUP BY nse.nse_codigo");
        corpo.append(" UNION ALL");
        corpo.append(" SELECT nse.nse_descricao , 'd' as tipoNumero, null as tipoSimbolo, '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.ultimo.movimento.prazo.medio", responsavel)).append("' AS volume, to_decimal(AVG(ade.ade_prazo),5,2) as quantidade, ROUND(((AVG(ade.ade_prazo)-total_passado.tt)/total_passado.tt)*100,2) as 'percentual'");
        corpo.append(" FROM tb_aut_desconto ade");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse USING (nse_codigo)");
        corpo.append(" INNER JOIN tb_parcela_desconto prd USING (ade_codigo)");
        corpo.append(" CROSS JOIN ( ");
        corpo.append("             SELECT AVG(ade.ade_prazo) as tt, nse.nse_codigo");
        corpo.append(" FROM tb_aut_desconto ade");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_parcela_desconto prd USING (ade_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse USING (nse_codigo)");
        corpo.append(" WHERE ade.ade_prazo is not null");
        corpo.append("     AND csa_codigo = :csaCodigo");
        corpo.append("     AND prd.prd_data_desconto = :AntesUltimoPeriodoProcessado");
        corpo.append(" GROUP BY nse.nse_codigo");
        corpo.append(" ) AS total_passado ON (total_passado.nse_codigo = nse.nse_codigo)");
        corpo.append(" WHERE ade.ade_prazo is not null");
        corpo.append(" AND csa_codigo = :csaCodigo");
        corpo.append(" AND prd.prd_data_desconto = :ultimoPeriodoProcessado");
        corpo.append(" GROUP BY nse.nse_codigo");
        corpo.append(" UNION ALL");
        corpo.append(" SELECT nse.nse_descricao , 'd' as tipoNumero, 'p' as tipoSimbolo, '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.ultimo.movimento.inadimplencia", responsavel)).append("' AS volume, to_decimal(total_inadimplencia.tt/count(prd.ade_codigo)*100,5,2) as quantidade, '0' as 'percentual'");
        corpo.append(" FROM tb_aut_desconto ade");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse USING (nse_codigo)");
        corpo.append(" INNER JOIN tb_parcela_desconto prd USING (ade_codigo)");
        corpo.append(" CROSS JOIN ( ");
        corpo.append("             SELECT COUNT(*) as tt, nse.nse_codigo");
        corpo.append(" FROM tb_aut_desconto ade");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse USING (nse_codigo)");
        corpo.append(" INNER JOIN tb_parcela_desconto prd USING (ade_codigo)");
        corpo.append(" WHERE prd.prd_vlr_previsto <> prd.prd_vlr_realizado");
        corpo.append("     AND csa_codigo = :csaCodigo");
        corpo.append("     AND prd.prd_data_desconto = :AntesUltimoPeriodoProcessado");
        corpo.append(" GROUP BY nse.nse_codigo");
        corpo.append(" ) AS total_inadimplencia ON (total_inadimplencia.nse_codigo = nse.nse_codigo)");
        corpo.append(" WHERE csa_codigo = :csaCodigo");
        corpo.append(" AND prd.prd_data_desconto = :ultimoPeriodoProcessado");
        corpo.append(" GROUP BY nse.nse_codigo");
        corpo.append(" ORDER BY 1;");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("ultimoPeriodoProcessado", ultimoPeriodoProcessado, query);
        defineValorClausulaNomeada("AntesUltimoPeriodoProcessado", AntesUltimoPeriodoProcessado, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            "TIPO",
            "STATUS",
            "SIMBOLO",
            "NOME",
            "QUANTIDADE",
            "PERCENTUAL"
        };
    }
}