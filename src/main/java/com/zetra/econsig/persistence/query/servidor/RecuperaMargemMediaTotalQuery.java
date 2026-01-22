package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RecuperaMargemMediaTotalQuery</p>
 * <p>Description: Retorna a média das margens dos servidores</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RecuperaMargemMediaTotalQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;
    public boolean recuperaRseExcluido = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append("AVG(coalesce(rse.rseMargem, 0)) AS RSE_MARGEM, ");
        corpoBuilder.append("AVG(coalesce(rse.rseMargem2, 0)) AS RSE_MARGEM_2, ");
        corpoBuilder.append("AVG(coalesce(rse.rseMargem3, 0)) AS RSE_MARGEM_3 ");
        corpoBuilder.append("FROM RegistroServidor rse ");

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
                "RSE_MARGEM",
                "RSE_MARGEM_2",
                "RSE_MARGEM_3"

        };
    }
}
