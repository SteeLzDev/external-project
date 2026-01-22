package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ObtemDecisaoJudicialQuery extends HQuery {

    public String adeCodigo;
    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT oca.adeCodigo, ");
        corpo.append(" dju.djuCodigo, ");
        corpo.append(" dju.djuData, ");
        corpo.append(" oca.ocaData, ");
        corpo.append(" dju.djuDataRevogacao ");
        corpo.append(" FROM DecisaoJudicial dju ");
        corpo.append(" INNER JOIN dju.ocorrenciaAutorizacao oca");
        corpo.append(" WHERE oca.adeCodigo").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpo.append(" ORDER BY oca.ocaData DESC");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.DJU_CODIGO,
                Columns.DJU_DATA,
                Columns.OCA_DATA,
                Columns.DJU_DATA_REVOGACAO
        };
    }
}
