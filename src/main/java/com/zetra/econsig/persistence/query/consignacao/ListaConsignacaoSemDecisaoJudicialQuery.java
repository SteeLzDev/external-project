package com.zetra.econsig.persistence.query.consignacao;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaConsignacaoSemDecisaoJudicialQuery extends HQuery {

    public Collection<String> adeCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT DISTINCT ade.adeCodigo  ");
        corpo.append("FROM AutDesconto ade ");
        corpo.append("WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", adeCodigos));
        corpo.append(" AND (NOT EXISTS ( ");
        corpo.append("   SELECT 1 FROM DecisaoJudicial dju ");
        corpo.append("   INNER JOIN dju.ocorrenciaAutorizacao oca ");
        corpo.append("   WHERE ade.adeCodigo = oca.adeCodigo ");
        corpo.append(")  ");
        corpo.append(" OR EXISTS ( ");
        corpo.append("   SELECT 1 FROM DecisaoJudicial dju ");
        corpo.append("   INNER JOIN dju.ocorrenciaAutorizacao oca ");
        corpo.append("   WHERE ade.adeCodigo = oca.adeCodigo ");
        corpo.append("     AND dju.djuDataRevogacao IS NOT NULL ");
        corpo.append("     AND oca.ocaData = ( ");
        corpo.append("         SELECT MAX(oca2.ocaData) ");
        corpo.append("         FROM OcorrenciaAutorizacao oca2 ");
        corpo.append("         INNER JOIN oca2.decisaoJudicialSet dju2 ");
        corpo.append("         WHERE oca2.adeCodigo = ade.adeCodigo ");
        corpo.append("     ) ");
        corpo.append("   ) ");
        corpo.append(") ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("adeCodigos", adeCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
        };
    }
}
