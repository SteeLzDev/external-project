package com.zetra.econsig.persistence.query.modelotermoaditivo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCepQuery</p>
 * <p>Description: Listagem Código e descrição do Termo Aditivo</p>
 * <p>Copyright: Copyright (c) 2002-2025</p>
 */

public class ListaCodTituloModeloTermoAditivoQuery extends HQuery {

    public String mtaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT mtaCodigo, mtaDescricao");
        corpoBuilder.append(" FROM ModeloTermoAditivo mta ");

        if (!TextHelper.isNull(mtaCodigo)) {
            corpoBuilder.append(" WHERE mta.mtaCodigo = :mtaCodigo");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(mtaCodigo)) {
            defineValorClausulaNomeada("mtaCodigo", mtaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.MTA_CODIGO,
                Columns.MTA_DESCRICAO
        };
    }
}
