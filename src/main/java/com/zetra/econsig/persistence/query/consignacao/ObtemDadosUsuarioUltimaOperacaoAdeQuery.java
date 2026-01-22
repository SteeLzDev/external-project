package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemDadosUsuarioUltimaOperacaoAdeQuery</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemDadosUsuarioUltimaOperacaoAdeQuery extends HQuery {

    public String adeCodigo;
    public String tocCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT oca.ocaData ");
        corpoBuilder.append(", usu.usuNome ");
        corpoBuilder.append(", usu.usuEmail ");

        corpoBuilder.append("FROM OcorrenciaAutorizacao oca ");
        corpoBuilder.append("INNER JOIN oca.usuario usu ");
        corpoBuilder.append("WHERE oca.tipoOcorrencia.tocCodigo = :tocCodigo ");
        corpoBuilder.append("AND oca.autDesconto.adeCodigo = :adeCodigo ");
        corpoBuilder.append("ORDER BY oca.ocaData desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);
        defineValorClausulaNomeada("tocCodigo", tocCodigo, query);
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OCA_DATA,
                Columns.USU_NOME,
                Columns.USU_EMAIL
        };
    }
}
