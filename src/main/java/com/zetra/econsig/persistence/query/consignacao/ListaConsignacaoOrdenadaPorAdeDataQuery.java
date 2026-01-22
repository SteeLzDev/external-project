package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoOrdenadaPorAdeDataQuery</p>
 * <p>Description: Listagem de Consignações ordenadas por ade_data</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoOrdenadaPorAdeDataQuery extends HQuery {
    public String tipoOrdenacao;
    public List<String> adeCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (TextHelper.isNull(tipoOrdenacao)) {
            tipoOrdenacao = "ASC";
        }

        corpoBuilder.append("SELECT ade.adeCodigo ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("WHERE ade.adeCodigo IN (:adeCodigos) ");
        corpoBuilder.append("ORDER BY ade.adeData ").append((tipoOrdenacao.equals("DESC") ? "DESC" : "ASC"));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigos", adeCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }
}
