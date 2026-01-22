package com.zetra.econsig.persistence.query.formulariopesquisa;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class BuscaFormularioPesquisaSemRespostaQuery extends HQuery {
    public String usuCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select fpe.fpeCodigo ");
        corpoBuilder.append("from FormularioPesquisa fpe ");
        corpoBuilder.append("where 1=1 ");
        corpoBuilder.append("and fpe.fpePublicado = true ");
        corpoBuilder.append("and fpe.fpeDtFim >= now() ");
        corpoBuilder.append("and not exists ( ");
        corpoBuilder.append("select 1 from FormularioPesquisaResposta fpr ");
        corpoBuilder.append("where fpr.formularioPesquisa = fpe ");
        corpoBuilder.append("and fpr.usuario.usuCodigo = :usuCodigo) ");
        corpoBuilder.append("order by fpe.fpeDtCriacao asc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FPE_CODIGO
        };
    }
}

