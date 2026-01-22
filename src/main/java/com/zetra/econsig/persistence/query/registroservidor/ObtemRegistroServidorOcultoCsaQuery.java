package com.zetra.econsig.persistence.query.registroservidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemRegistroServidorOcultoCsaQuery</p>
 * <p>Description: Obtém consignatárias ocultas para o registro servidor informado.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemRegistroServidorOcultoCsaQuery extends HQuery {

    private String rseCodigo = null;

    public ObtemRegistroServidorOcultoCsaQuery(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder("select");
        corpo.append(" csa.csaCodigo ");
        corpo.append(" FROM RegistroServidorOcultoCsa roc ");
        corpo.append(" INNER JOIN roc.registroServidor rse ");
        corpo.append(" INNER JOIN roc.consignataria csa ");
        corpo.append(" WHERE rse.rseCodigo = :rseCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.CSA_CODIGO
        };
    }	
}
