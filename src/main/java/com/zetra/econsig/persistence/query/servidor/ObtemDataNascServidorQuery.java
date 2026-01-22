package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemRegistroServidorQuery</p>
 * <p>Description: Listagem de registros de servidores</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemDataNascServidorQuery extends HQuery {

    private final String rseCodigo;

    public ObtemDataNascServidorQuery(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ser.serDataNasc ");
        corpoBuilder.append("from RegistroServidor rse ");
        corpoBuilder.append("inner join rse.servidor ser ");
        corpoBuilder.append("where rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_DATA_NASC
        };
    }
}
