package com.zetra.econsig.persistence.query.beneficios;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListarNacionalidadeQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT nac.nacCodigo, " +
                    "nac.nacDescricao " +
                    "from Nacionalidade nac ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("order by nac.nacDescricao ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.NAC_CODIGO,
                Columns.NAC_DESCRICAO
        };
    }
}
