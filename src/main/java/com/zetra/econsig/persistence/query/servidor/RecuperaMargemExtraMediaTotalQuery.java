package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

public class RecuperaMargemExtraMediaTotalQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;
    public boolean recuperaRseExcluido = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append("AVG(coalesce(mrs.mrsMargem, 0)) AS MRS_MARGEM, ");
        corpoBuilder.append("mrs.marCodigo AS MAR_CODIGO ");

        corpoBuilder.append("FROM MargemRegistroServidor mrs ");

        corpoBuilder.append("INNER JOIN mrs.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.orgao org ");
        corpoBuilder.append("INNER JOIN org.estabelecimento est ");
        corpoBuilder.append("INNER JOIN rse.statusRegistroServidor srs ");
        corpoBuilder.append("WHERE 1 = 1 ");

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!recuperaRseExcluido) {
            corpoBuilder.append(" AND srs.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        corpoBuilder.append(" GROUP BY mrs.marCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "MRS_MARGEM",
                "MAR_CODIGO"
        };
    }
}
