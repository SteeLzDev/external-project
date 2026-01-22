package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p> Title: RelatorioGerencialEstatiscoMargemQuery</p>
 * <p> Description: Recupera informações estatísticas sobre a margem.</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialEstatiscoMargemQuery extends ReportHNativeQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String srsAtivo = CodedValues.SRS_ATIVO;

        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT ");
        corpo.append("sum(rse.rse_margem) AS MARGEM_TOTAL, ");
        corpo.append("sum(rse.rse_margem_rest) AS MARGEM_REST_TOTAL, ");
        corpo.append("sum(rse.rse_margem_usada) AS MARGEM_USADA_TOTAL, ");
        corpo.append("min(rse.rse_margem_rest) AS MARGEM_REST_MINIMO, ");
        corpo.append("max(rse.rse_margem_rest) AS MARGEM_REST_MAXIMO, ");
        corpo.append("to_decimal(round(avg(rse.rse_margem_rest), 2), 13, 2) AS MARGEM_REST_MEDIA, ");
        corpo.append("to_decimal(round(desvio_padrao(rse.rse_margem_rest), 2), 13, 2) AS MARGEM_REST_DESVIO, ");
        corpo.append("sum(case when coalesce(rse.rse_margem_rest,0) < 0 then 1 else 0 end) AS QTDE_SER_MARGEM_NEGATIVA, ");
        corpo.append("sum(case when coalesce(rse.rse_margem_rest,0) = 0 then 1 else 0 end) AS QTDE_SER_MARGEM_ZERADA, ");
        corpo.append("sum(case when coalesce(rse.rse_margem_rest,0) > 0 then 1 else 0 end) AS QTDE_SER_MARGEM_POSITIVA, ");
        corpo.append("sum(rse.rse_margem_2) AS MARGEM_TOTAL_2, ");
        corpo.append("sum(rse.rse_margem_rest_2) AS MARGEM_REST_TOTAL_2, ");
        corpo.append("sum(rse.rse_margem_usada_2) AS MARGEM_USADA_TOTAL_2, ");
        corpo.append("min(rse.rse_margem_rest_2) AS MARGEM_REST_MINIMO_2, ");
        corpo.append("max(rse.rse_margem_rest_2) AS MARGEM_REST_MAXIMO_2, ");
        corpo.append("to_decimal(round(avg(coalesce(rse.rse_margem_rest_2, 0)), 2), 13, 2) AS MARGEM_REST_MEDIA_2, ");
        corpo.append("to_decimal(round(desvio_padrao(coalesce(rse.rse_margem_rest_2, 0)), 2), 13, 2) AS MARGEM_REST_DESVIO_2, ");
        corpo.append("sum(case when coalesce(rse.rse_margem_rest_2,0) < 0 then 1 else 0 end) AS QTDE_SER_MARGEM_2_NEGATIVA, ");
        corpo.append("sum(case when coalesce(rse.rse_margem_rest_2,0) = 0 then 1 else 0 end) AS QTDE_SER_MARGEM_2_ZERADA, ");
        corpo.append("sum(case when coalesce(rse.rse_margem_rest_2,0) > 0 then 1 else 0 end) AS QTDE_SER_MARGEM_2_POSITIVA, ");
        corpo.append("sum(rse.rse_margem_3) AS MARGEM_TOTAL_3, ");
        corpo.append("sum(rse.rse_margem_rest_3) AS MARGEM_REST_TOTAL_3, ");
        corpo.append("sum(rse.rse_margem_usada_3) AS MARGEM_USADA_TOTAL_3, ");
        corpo.append("min(rse.rse_margem_rest_3) AS MARGEM_REST_MINIMO_3, ");
        corpo.append("max(rse.rse_margem_rest_3) AS MARGEM_REST_MAXIMO_3, ");
        corpo.append("to_decimal(round(avg(coalesce(rse.rse_margem_rest_3, 0)), 2), 13, 2) AS MARGEM_REST_MEDIA_3, ");
        corpo.append("to_decimal(round(desvio_padrao(coalesce(rse.rse_margem_rest_3, 0)), 2), 13, 2) AS MARGEM_REST_DESVIO_3, ");
        corpo.append("sum(case when coalesce(rse.rse_margem_rest_3,0) < 0 then 1 else 0 end) AS QTDE_SER_MARGEM_3_NEGATIVA, ");
        corpo.append("sum(case when coalesce(rse.rse_margem_rest_3,0) = 0 then 1 else 0 end) AS QTDE_SER_MARGEM_3_ZERADA, ");
        corpo.append("sum(case when coalesce(rse.rse_margem_rest_3,0) > 0 then 1 else 0 end) AS QTDE_SER_MARGEM_3_POSITIVA ");
        corpo.append("FROM (");
        corpo.append("SELECT rse.rse_codigo, ");
        corpo.append("coalesce(sum(CASE WHEN (ade.ade_inc_margem = 1 or (coalesce(psi091.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3)) or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + rse.rse_margem_rest AS rse_margem, ");
        corpo.append("coalesce(sum(CASE WHEN (ade.ade_inc_margem = 1 or (coalesce(psi091.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3)) or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) AS rse_margem_usada, ");
        corpo.append("rse.rse_margem_rest, ");
        corpo.append("coalesce(sum(CASE WHEN (ade.ade_inc_margem = 2 or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + rse.rse_margem_rest_2 AS rse_margem_2, ");
        corpo.append("coalesce(sum(CASE WHEN (ade.ade_inc_margem = 2 or (coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,3))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) AS rse_margem_usada_2, ");
        corpo.append("rse.rse_margem_rest_2, ");
        corpo.append("coalesce(sum(CASE WHEN (ade.ade_inc_margem = 3 or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 1) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,2))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0) + rse.rse_margem_rest_3  AS rse_margem_3, ");
        corpo.append("coalesce(sum(CASE WHEN (ade.ade_inc_margem = 3 or (coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 1) or (coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,2))) THEN (coalesce(ade.ade_vlr_folha, ade.ade_vlr)) ELSE 0 END), 0)  AS rse_margem_usada_3, ");
        corpo.append("rse.rse_margem_rest_3 ");
        corpo.append("FROM tb_registro_servidor rse ");
        corpo.append("INNER JOIN tb_orgao org on (org.org_codigo = rse.org_codigo) ");
        corpo.append("INNER JOIN tb_estabelecimento est on (est.est_codigo = org.est_codigo) ");
        corpo.append("INNER JOIN tb_status_registro_servidor srs on (srs.srs_codigo = rse.srs_codigo) ");
        corpo.append("LEFT OUTER JOIN tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo ");
        corpo.append("AND ade.sad_codigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')) ");
        corpo.append("LEFT OUTER JOIN tb_param_sist_consignante psi091 on (psi091.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3).append("') ");
        corpo.append("LEFT OUTER JOIN tb_param_sist_consignante psi173 on (psi173.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_2_3_CASADAS).append("') ");
        corpo.append("LEFT OUTER JOIN tb_param_sist_consignante psi218 on (psi218.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA).append("') ");
        corpo.append("LEFT OUTER JOIN tb_param_sist_consignante psi219 on (psi219.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA).append("') ");
        corpo.append("WHERE rse.srs_codigo ").append(criaClausulaNomeada("srsAtivo", srsAtivo)).append(" ");
        corpo.append("GROUP BY rse.rse_codigo, rse_margem_rest, rse_margem_rest_2, rse_margem_rest_3 ");
        corpo.append(") rse ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("srsAtivo", srsAtivo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "MARGEM_TOTAL",
                "MARGEM_REST_TOTAL",
                "MARGEM_USADA_TOTAL",
                "MARGEM_REST_MINIMO",
                "MARGEM_REST_MAXIMO",
                "MARGEM_REST_MEDIA",
                "MARGEM_REST_DESVIO",
                "QTDE_SER_MARGEM_NEGATIVA",
                "QTDE_SER_MARGEM_ZERADA",
                "QTDE_SER_MARGEM_POSITIVA",
                "MARGEM_TOTAL_2",
                "MARGEM_REST_TOTAL_2",
                "MARGEM_USADA_TOTAL_2",
                "MARGEM_REST_MINIMO_2",
                "MARGEM_REST_MAXIMO_2",
                "MARGEM_REST_MEDIA_2",
                "MARGEM_REST_DESVIO_2",
                "QTDE_SER_MARGEM_2_NEGATIVA",
                "QTDE_SER_MARGEM_2_ZERADA",
                "QTDE_SER_MARGEM_2_POSITIVA",
                "MARGEM_TOTAL_3",
                "MARGEM_REST_TOTAL_3",
                "MARGEM_USADA_TOTAL_3",
                "MARGEM_REST_MINIMO_3",
                "MARGEM_REST_MAXIMO_3",
                "MARGEM_REST_MEDIA_3",
                "MARGEM_REST_DESVIO_3",
                "QTDE_SER_MARGEM_3_NEGATIVA",
                "QTDE_SER_MARGEM_3_ZERADA",
                "QTDE_SER_MARGEM_3_POSITIVA"
        };
    }
}