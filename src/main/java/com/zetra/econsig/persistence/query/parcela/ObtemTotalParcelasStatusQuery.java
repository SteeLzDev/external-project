package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalParcelasQuery</p>
 * <p>Description: Retorna o total de parcelas de um contrato para um status.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalParcelasStatusQuery extends HQuery {

    public String adeCodigo;
    public List<String> spdCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT COUNT(*) ");
        corpoBuilder.append("FROM ParcelaDesconto prd ");
        corpoBuilder.append("WHERE prd.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (spdCodigos != null && spdCodigos.size() > 0) {
            corpoBuilder.append(" AND prd.statusParcelaDesconto.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        if (spdCodigos != null && spdCodigos.size() > 0) {
            defineValorClausulaNomeada("spdCodigos", spdCodigos, query);
        }

        return query;
    }
}
