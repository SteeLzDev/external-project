package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;

/**
 * <p>Title: RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQuery</p>
 * <p>Description: Recuperar conciliação por órgão</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaIndicadorInsucessoQuery extends ReportHNativeQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;
    public String periodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();

        corpo.append(" SELECT ");
        corpo.append(" nse.nse_descricao AS NSE_DESCRICAO, ");
        corpo.append(" SUBSTRING_INDEX(SUBSTRING(ocp.ocp_obs, INSTR(ocp.ocp_obs, ':') + 2), ' ', 3) AS OBSERVACAO,");
        corpo.append(" CASE ");
        corpo.append(" WHEN total_nao_pago.tt > 0 THEN ROUND((count(prd.ade_codigo)/total_nao_pago.tt)*100,2)");
        corpo.append(" ELSE '' ");
        corpo.append(" END AS PERCENTUAL ");
        corpo.append(" FROM tb_parcela_desconto prd");
        corpo.append(" INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo)");
        corpo.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo)");
        corpo.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo)");
        corpo.append(" INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo)");
        corpo.append(" INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo)");
        corpo.append(" INNER JOIN tb_natureza_servico nse ON (nse.nse_codigo = svc.nse_codigo)");
        corpo.append(" INNER JOIN tb_ocorrencia_parcela ocp ON (ocp.prd_codigo = prd.prd_codigo)");
        corpo.append(" LEFT JOIN (");
        corpo.append(" SELECT count(*) as tt, svc.nse_codigo as nse_codigo");
        corpo.append(" FROM tb_parcela_desconto prd");
        corpo.append(" INNER JOIN tb_aut_desconto ade USING (ade_codigo)");
        corpo.append(" INNER JOIN tb_verba_convenio vco USING (vco_codigo)");
        corpo.append(" INNER JOIN tb_convenio cnv USING (cnv_codigo)");
        corpo.append(" INNER JOIN tb_servico svc USING (svc_codigo)");
        corpo.append(" INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo)");
        corpo.append(" WHERE prd.spd_codigo NOT IN ('6','7')");
        if (!TextHelper.isNull(csaCodigo)) {
        	corpo.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(periodo)) {
        	corpo.append(" AND prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
        }
		corpo.append(" GROUP BY svc.nse_codigo");
      	corpo.append(" ) as total_nao_pago ON (total_nao_pago.nse_codigo = nse.nse_codigo)");
      	corpo.append(" WHERE prd.spd_codigo NOT IN ('6','7')");
        if (!TextHelper.isNull(csaCodigo)) {
        	corpo.append(" AND csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(periodo)) {
        	corpo.append(" AND prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
        }
		corpo.append(" GROUP BY svc.nse_codigo, OBSERVACAO");
    	corpo.append(" ORDER BY 1");

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
            "NSE_DESCRICAO",
            "OBSERVACAO",
            "PERCENTUAL"
        };
    }
}
