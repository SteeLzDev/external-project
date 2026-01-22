package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaParcelaQuery</p>
 * <p>Description: Seleciona as parcelas para uma determinada consignação,
 * juntamente com as ocorrências destas parcelas.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaParcelaQuery extends HQuery {

    public String adeCodigo;
    public List<String> spdCodigos;
    public boolean orderASC = true;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                       "prd.prdNumero, " +
                       "prd.prdVlrPrevisto, " +
                       "prd.prdVlrRealizado, " +
                       "prd.prdDataDesconto, " +
                       "prd.prdDataRealizado, " +
                       "prd.statusParcelaDesconto.spdCodigo, " +
                       "ocp.ocpObs, " +
                       "ocp.ocpCodigo, " +
                       "ocp.ocpData"
                     ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM ParcelaDesconto prd ");
        corpoBuilder.append(" LEFT OUTER JOIN prd.ocorrenciaParcelaSet ocp ");
        corpoBuilder.append(" WHERE prd.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (spdCodigos != null && spdCodigos.size() > 0) {
            corpoBuilder.append(" AND prd.statusParcelaDesconto.spdCodigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));
        }

        corpoBuilder.append(" ORDER BY prd.prdDataDesconto");

        if (!orderASC) {
            corpoBuilder.append(" DESC");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        if (spdCodigos != null && spdCodigos.size() > 0) {
            defineValorClausulaNomeada("spdCodigo", spdCodigos, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRD_NUMERO,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_DATA_REALIZADO,
                Columns.SPD_CODIGO,
                Columns.OCP_OBS,
                Columns.OCP_CODIGO,
                Columns.OCP_DATA
        };
    }
}
