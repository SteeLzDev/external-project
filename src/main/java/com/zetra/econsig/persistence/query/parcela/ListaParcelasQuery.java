package com.zetra.econsig.persistence.query.parcela;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParcelasQuery</p>
 * <p>Description: Retorna as parcelas de um contrato.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParcelasQuery extends HQuery {

    public String adeCodigo;
    public List<String> spdCodigos;
    public Short prdNumero;
    public Date prdDataDesconto;
    public boolean ordenaDataDescontoDesc;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                       "prd.autDesconto.adeCodigo, " +
                       "prd.prdCodigo, " +
                       "prd.prdNumero, " +
                       "spd.spdCodigo, " +
                       "spd.spdDescricao, " +
                       "prd.prdDataDesconto, " +
                       "prd.prdDataRealizado, " +
                       "prd.prdVlrPrevisto, " +
                       "prd.prdVlrRealizado, " +
                       "prd.tipoDesconto.tdeCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("FROM ParcelaDesconto prd ");
        corpoBuilder.append("INNER JOIN prd.statusParcelaDesconto spd ");
        corpoBuilder.append("WHERE prd.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (spdCodigos != null && spdCodigos.size() > 0) {
            corpoBuilder.append(" AND spd.spdCodigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));
        }

        if (!TextHelper.isNull(prdNumero)) {
            corpoBuilder.append(" AND prd.prdNumero ").append(criaClausulaNomeada("prdNumero", prdNumero));
        }

        if (prdDataDesconto != null) {
            corpoBuilder.append(" AND prd.prdDataDesconto = :prdDataDesconto");
        }

        if (ordenaDataDescontoDesc) {
            corpoBuilder.append(" ORDER BY prd.prdDataRealizado DESC, prd.prdDataDesconto DESC ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        if (spdCodigos != null && spdCodigos.size() > 0) {
            defineValorClausulaNomeada("spdCodigo", spdCodigos, query);
        }

        if (!TextHelper.isNull(prdNumero)) {
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
                Columns.PRD_ADE_CODIGO,
                Columns.PRD_CODIGO,
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
}
