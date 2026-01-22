package com.zetra.econsig.persistence.query.parcela;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParcelasLiquidarParcialQuery</p>
 * <p>Description: Retorna as parcelas de um contrato de parcela parcial.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 30402 $
 * $Date: 2020-09-17 15:45:56 -0300 (qui, 17 set 2020) $
 */
public class ListaParcelasLiquidarParcialQuery extends HNativeQuery {

    public String adeCodigo;
    public Integer prdCodigo;
    public Short prdNumero;
    public Date prdDataDesconto;
    public boolean ordenaDataDescontoDesc;
    public boolean permiteLiquidarParcelaParcial;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        if (permiteLiquidarParcelaParcial) {
            corpoBuilder.append(" SELECT " +
                    "prd.prd_codigo, " +
                    "prd.ade_codigo, " +
                    "prd.prd_numero, " +
                    "spd.spd_codigo, " +
                    "spd.spd_descricao, " +
                    "prd.prd_data_desconto, " +
                    "prd.prd_data_realizado, " +
                    "prd.prd_vlr_previsto, " +
                    "prd.prd_vlr_realizado, " +
                    "prd.tde_codigo ");
            corpoBuilder.append("FROM tb_parcela_desconto prd ");
            corpoBuilder.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo )");
            corpoBuilder.append("INNER JOIN tb_status_parcela_desconto spd ON (prd.spd_codigo = spd.spd_codigo) ");
            corpoBuilder.append("INNER JOIN tb_ocorrencia_parcela ocp ON (ocp.prd_codigo = prd.prd_codigo AND ocp.toc_codigo = '").append(CodedValues.TOC_RETORNO_PARCIAL).append("') ");
            corpoBuilder.append("WHERE prd.ade_codigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
            corpoBuilder.append("AND ade.ade_tipo_vlr= '").append(CodedValues.TIPO_VLR_FIXO).append("' ");
            corpoBuilder.append("AND prd.prd_vlr_realizado < prd.prd_vlr_previsto ");
            corpoBuilder.append("AND prd.spd_codigo in ('").append(CodedValues.SPD_LIQUIDADAMANUAL).append("','").append(CodedValues.SPD_LIQUIDADAFOLHA).append("') ");
        }

        if (prdCodigo != null) {
            corpoBuilder.append(" AND prd.prd_codigo ").append(criaClausulaNomeada("prdCodigo", prdCodigo));
        }

        if (prdNumero != null) {
            corpoBuilder.append(" AND prd.prd_numero ").append(criaClausulaNomeada("prdNumero", prdNumero));
        }

        if (prdDataDesconto != null) {
            corpoBuilder.append(" AND prd.prd_data_desconto = :prdDataDesconto");
        }

        if (ordenaDataDescontoDesc) {
            corpoBuilder.append(" ORDER BY prd.prd_data_realizado DESC, prd.prd_data_desconto DESC ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        if (prdCodigo != null) {
            defineValorClausulaNomeada("prdCodigo", prdCodigo, query);
        }

        if (prdNumero != null) {
            defineValorClausulaNomeada("prdNumero", prdNumero, query);
        }

        if (prdDataDesconto != null) {
            defineValorClausulaNomeada("prdDataDesconto", DateHelper.clearHourTime(prdDataDesconto), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRD_CODIGO,
                Columns.PRD_ADE_CODIGO,
                Columns.PRD_NUMERO,
                Columns.PRD_SPD_CODIGO,
                Columns.SPD_DESCRICAO,
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_DATA_REALIZADO,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.PRD_TDE_CODIGO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
