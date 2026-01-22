package com.zetra.econsig.persistence.query.margem;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaContratosIncideMargemQuery</p>
 * <p>Description: Lista contratos de acordo com a incidência de margem.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaContratosIncideMargemQuery extends HQuery {

    public boolean count = false;
    public String rseCodigo;
    public List<Short> adeIncideMargens;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "";
        if (count) {
            corpo = "SELECT COUNT(*) ";
        } else {
            corpo = "SELECT COUNT(DISTINCT ade.adeCodigo) as QTDE ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM  AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse ");

        corpoBuilder.append(" WHERE rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND ade.adeIncMargem ").append(criaClausulaNomeada("adeIncideMargens", adeIncideMargens));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        defineValorClausulaNomeada("adeIncideMargens", adeIncideMargens, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }

}
