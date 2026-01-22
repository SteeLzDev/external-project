package com.zetra.econsig.persistence.query.pontuacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaTipoParamPontuacaoRsePorCsaQuery extends HQuery {

    private final String csaCodigo;

    public ListaTipoParamPontuacaoRsePorCsaQuery(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ppr.tpoCodigo ");
        corpoBuilder.append("from ParamPontuacaoRseCsa ppr ");
        corpoBuilder.append("where ppr.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append("group by ppr.tpoCodigo ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        return query;
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TPO_CODIGO
        };
    }
}
