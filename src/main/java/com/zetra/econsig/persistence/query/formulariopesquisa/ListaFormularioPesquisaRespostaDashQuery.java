package com.zetra.econsig.persistence.query.formulariopesquisa;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaFormularioPesquisaRespostaDashQuery  extends HQuery {
    public String fpeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT fpe.fpeJson, fpr.fprJson ");
        corpoBuilder.append(" FROM FormularioPesquisaResposta fpr ");
        corpoBuilder.append(" INNER JOIN fpr.formularioPesquisa fpe ");
        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(fpeCodigo)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("fpe.fpeCodigo ", "fpeCodigo", fpeCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(fpeCodigo)) {
            defineValorClausulaNomeada("fpeCodigo", fpeCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FPE_JSON,
                Columns.FPR_JSON
        };
    }
}
