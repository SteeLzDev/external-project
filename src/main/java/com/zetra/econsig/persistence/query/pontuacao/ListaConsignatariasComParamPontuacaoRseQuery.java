package com.zetra.econsig.persistence.query.pontuacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaConsignatariasComParamPontuacaoRseQuery extends HQuery {

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ppr.csaCodigo ");
        corpoBuilder.append("from ParamPontuacaoRseCsa ppr ");
        corpoBuilder.append("group by ppr.csaCodigo ");

        return instanciarQuery(session, corpoBuilder.toString());
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO
        };
    }
}
