package com.zetra.econsig.persistence.query.uf;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUfQuery</p>
 * <p>Description: Listagem de unidade federativa.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUfQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select uf.ufCod, uf.ufNome from Uf uf order by uf.ufNome";
        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.UF_COD, Columns.UF_NOME };
    }
}
