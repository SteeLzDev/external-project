package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParcelasReimplantadasManualQuery</p>
 * <p>Description: Retorna as parcelas reimplantadas manualmente de um contrato.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParcelasReimplantadasManualQuery extends HQuery {

    public String adeCodigo;
    public List<String> spdCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                       "pdp.autDesconto.adeCodigo, " +
                       "pdp.prdNumero, " +
                       "spd.spdCodigo, " +
                       "spd.spdDescricao, " +
                       "pdp.prdDataDesconto, " +
                       "pdp.prdVlrPrevisto ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("FROM ParcelaDescontoPeriodo pdp ");
        corpoBuilder.append("INNER JOIN pdp.statusParcelaDesconto spd ");
        corpoBuilder.append("INNER JOIN pdp.autDesconto ade ");
        corpoBuilder.append("WHERE pdp.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (spdCodigos != null && !spdCodigos.isEmpty()) {
            corpoBuilder.append(" AND spd.spdCodigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));
        }

        corpoBuilder.append(" AND ade.adePrazo IS NOT NULL ");
        corpoBuilder.append(" AND pdp.prdDataDesconto > ade.adeAnoMesFim ");

        corpoBuilder.append("ORDER BY pdp.prdDataDesconto DESC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        if (spdCodigos != null && !spdCodigos.isEmpty()) {
            defineValorClausulaNomeada("spdCodigo", spdCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PDP_ADE_CODIGO,
                Columns.PDP_NUMERO,
                Columns.PDP_SPD_CODIGO,
                Columns.SPD_DESCRICAO,
                Columns.PDP_DATA_DESCONTO,
                Columns.PDP_VLR_PREVISTO
        };
    }
}
