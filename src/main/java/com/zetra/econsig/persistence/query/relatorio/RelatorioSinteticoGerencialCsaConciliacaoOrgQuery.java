package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;

/**
 * <p>Title: RelatorioSinteticoGerencialCsaConciliacaoOrgQuery</p>
 * <p>Description: Recuperar conciliação por órgão</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaConciliacaoOrgQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;
    public String periodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();

        corpo.append(" SELECT NOME,");
        corpo.append(" CASE WHEN a = 1 THEN '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.conciliacao.orgao.percentual.efetuado", responsavel)).append("'");
        corpo.append(" WHEN a = 2 THEN '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.conciliacao.orgao.percentual.parcial", responsavel)).append("'");
        corpo.append(" WHEN a = 3 THEN '").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sinteticogerencialconsignataria.conciliacao.orgao.percentual.nao.efetuado", responsavel)).append("'");
        corpo.append(" END AS TIPO,");
        corpo.append(" CASE WHEN a = 1 THEN EFETUADO");
        corpo.append(" WHEN a = 2 THEN PARCIAL");
        corpo.append(" WHEN a = 3 THEN NAO_DESCONTADO");
        corpo.append(" END AS PERCENTUAL");
        corpo.append(" FROM (");
        corpo.append(" SELECT ");
        corpo.append(" org.org_nome AS NOME,");
        corpo.append(" ROUND(SUM(CASE WHEN prd.spd_codigo IN ('6','7') AND prd.prd_vlr_realizado >= prd.prd_vlr_previsto AND prd.prd_vlr_realizado > 0 THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0) * 100, 2) AS EFETUADO,");
        corpo.append(" ROUND(SUM(CASE WHEN prd.spd_codigo IN ('6','7') AND prd.prd_vlr_realizado  < prd.prd_vlr_previsto AND prd.prd_vlr_realizado > 0 THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0) * 100, 2) AS PARCIAL,");
        corpo.append(" ROUND(SUM(CASE WHEN prd.spd_codigo NOT IN ('6','7') OR prd.prd_vlr_realizado <= 0 THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0) * 100, 2) AS NAO_DESCONTADO");
        corpo.append(" FROM tb_parcela_desconto prd");
        corpo.append(" INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo)");
        corpo.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo)");
        corpo.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo)");
        corpo.append(" INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo)");
        corpo.append(" INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo)");
        corpo.append(" WHERE 1 = 1");
        if (!TextHelper.isNull(csaCodigo)) {
        	corpo.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(periodo)) {
        	corpo.append(" AND prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
        }
		corpo.append(" GROUP BY org.org_nome)");
        corpo.append(" as X");
        corpo.append(" cross join tb_aux_pivot");
        corpo.append(" WHERE a in (1, 2, 3)");
        corpo.append(" ORDER BY NOME");


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
        	"NOME",
            "TIPO",
            "PERCENTUAL"
        };
    }
}
