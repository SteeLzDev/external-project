package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQuery</p>
 * <p>Description: Recuperar conciliação por órgão</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;
    public String periodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT"); 
        corpo.append(" nse.nse_descricao,");
        corpo.append(" '1' AS TIPO,");
        corpo.append(" SUM(prd.prd_vlr_previsto) AS ENVIADO_DESCONTO,");
        corpo.append(" SUM(CASE WHEN prd.spd_codigo IN ('6','7') AND prd.prd_vlr_realizado = prd.prd_vlr_previsto THEN prd.prd_vlr_realizado ELSE 0 END) AS DESCONTO_EFETUADO,");
        corpo.append(" SUM(CASE WHEN prd.spd_codigo IN ('6','7') AND prd.prd_vlr_realizado <> prd.prd_vlr_previsto AND prd.prd_vlr_previsto > 0 THEN prd.prd_vlr_realizado ELSE 0 END) AS DESCONTO_PARCIAL,");
        corpo.append(" SUM(CASE WHEN prd.spd_codigo NOT IN ('6','7') THEN prd.prd_vlr_previsto ELSE 0 END) AS DESCONTO_NAO_EFETUADO,");
        corpo.append(" ROUND(");
        corpo.append(" (SUM(CASE WHEN prd.spd_codigo NOT IN ('6','7') THEN prd.prd_vlr_previsto ELSE 0 END) / NULLIF(SUM(CASE WHEN prd.spd_codigo IN ('6','7') THEN prd.prd_vlr_previsto ELSE 0 END), 0)) * 100,");
        corpo.append(" 2");
        corpo.append(" ) AS INADIMPLENCIA");
        corpo.append(" FROM tb_parcela_desconto prd");
        corpo.append(" INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo)");
        corpo.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo)");
        corpo.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo)");
        corpo.append(" INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo)");
        corpo.append(" INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse ON (nse.nse_codigo = svc.nse_codigo)");
        corpo.append(" WHERE 1 = 1");
        if (!TextHelper.isNull(csaCodigo)) {
        	corpo.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(periodo)) {
        	corpo.append(" AND prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
        }
        corpo.append(" GROUP BY nse.nse_codigo, nse.nse_descricao");
        corpo.append(" UNION ALL");
        corpo.append(" SELECT ");
        corpo.append(" nse.nse_descricao,");
        corpo.append(" '2' AS TIPO,");
        corpo.append(" SUM(1) AS ENVIADO_DESCONTO,");
        corpo.append(" SUM(CASE WHEN prd.spd_codigo IN ('6','7') AND prd.prd_vlr_realizado = prd.prd_vlr_previsto THEN 1 ELSE 0 END) AS DESCONTO_EFETUADO,");
        corpo.append(" SUM(CASE WHEN prd.spd_codigo IN ('6','7') AND prd.prd_vlr_realizado <> prd.prd_vlr_previsto AND prd.prd_vlr_previsto > 0 THEN 1 ELSE 0 END) AS DESCONTO_PARCIAL,");
        corpo.append(" SUM(CASE WHEN prd.spd_codigo NOT IN ('6','7') THEN 1 ELSE 0 END) AS DESCONTO_NAO_EFETUADO,");
        corpo.append(" '' AS INADIMPLENCIA");
        corpo.append(" FROM tb_parcela_desconto prd");
        corpo.append(" INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo)");
        corpo.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo)");
        corpo.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo)");
        corpo.append(" INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo)");
        corpo.append(" INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse ON (nse.nse_codigo = svc.nse_codigo)");
        corpo.append(" WHERE 1 = 1");
        if (!TextHelper.isNull(csaCodigo)) {
        	corpo.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(periodo)) {
        	corpo.append(" AND prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
        }
        corpo.append(" GROUP BY nse.nse_codigo, nse.nse_descricao");
        corpo.append(" ORDER BY 1, 2");
        
        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if (!TextHelper.isNull(csaCodigo)) {
        	defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
        	Columns.NSE_DESCRICAO,
            "TIPO",
            "ENVIADO_DESCONTO",
            "DESCONTO_EFETUADO",
            "DESCONTO_PARCIAL",
            "DESCONTO_NAO_EFETUADO",
            "INADIMPLENCIA"
        };
    }
}
