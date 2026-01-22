package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;

/**
 * <p>Title: RelatorioSinteticoGerencialCsaVolumeFinanceiroQuery</p>
 * <p>Description: Recuperar volume financeiro</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaVolumeFinanceiroQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();

        corpo.append(" SELECT prd.prd_data_desconto AS PRD_DATA_DESCONTO,");
        corpo.append(" CASE ");
        corpo.append(" WHEN prd.prd_vlr_previsto = prd_vlr_realizado THEN SUM(PRD_VLR_REALIZADO)");
        corpo.append(" WHEN prd.prd_vlr_previsto <> prd_vlr_realizado AND prd.prd_vlr_realizado > 0 THEN SUM(PRD_VLR_REALIZADO)");
        corpo.append(" WHEN prd.prd_vlr_previsto <> prd_vlr_realizado AND (prd.prd_vlr_realizado = 0 OR prd.prd_vlr_realizado IS NULL) THEN SUM(PRD_VLR_PREVISTO)");
        corpo.append(" ELSE '0' END AS VALORES, ");
        corpo.append(" CASE");
        corpo.append(" WHEN prd.prd_vlr_previsto = prd_vlr_realizado THEN '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.parcelas.desconto.desconto.efetuado", responsavel)).append("'");
        corpo.append(" WHEN prd.prd_vlr_previsto <> prd_vlr_realizado AND prd.prd_vlr_realizado > 0 THEN '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.parcelas.desconto.desconto.parcial", responsavel)).append("'");
        corpo.append(" WHEN prd.prd_vlr_previsto <> prd_vlr_realizado AND (prd.prd_vlr_realizado = 0 OR prd.prd_vlr_realizado is NULL) THEN '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.parcelas.desconto.desconto.nao.efetuado", responsavel)).append("'");
        corpo.append(" ELSE '").append(ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel)).append("' END AS STATUS_PAGAMENTO");
        corpo.append(" FROM tb_parcela_desconto prd");
        corpo.append(" INNER JOIN tb_status_parcela_desconto spd USING (spd_codigo)");
        corpo.append(" INNER JOIN tb_aut_desconto ade USING (ade_codigo)");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER join tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo)");
		corpo.append(" WHERE prd.prd_data_desconto BETWEEN add_month(current_date(), -12) AND current_date()");
		corpo.append(" AND spd.spd_codigo <> '1'");
        if (!TextHelper.isNull(csaCodigo)) {
        	corpo.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
		corpo.append(" GROUP BY prd_data_desconto, status_pagamento");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if (!TextHelper.isNull(csaCodigo)) {
        	defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
        	"PRD_DATA_DESCONTO",
            "VALORES",
            "STATUS_PAGAMENTO"
        };
    }
}
