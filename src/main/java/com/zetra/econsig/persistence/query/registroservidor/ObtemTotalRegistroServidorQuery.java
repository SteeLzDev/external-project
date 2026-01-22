package com.zetra.econsig.persistence.query.registroservidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalRegistroServidorQuery</p>
 * <p>Description: Obtém quantidade total de registros servidores</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalRegistroServidorQuery extends HQuery {

    private String serCodigo = null;

    public ObtemTotalRegistroServidorQuery(String serCodigo) {
        this.serCodigo = serCodigo;
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder("select");
        corpo.append(" count(*) as QTDE ");

        corpo.append(" FROM RegistroServidor rse ");
        corpo.append(" WHERE rse.servidor.serCodigo = :serCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("serCodigo", serCodigo, query);

        return query;
    }

}
