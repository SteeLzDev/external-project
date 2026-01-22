package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaIdentificadorQuery</p>
 * <p>Description: Listagem de Consignatárias para map de CSA_IDENTIFICADOR por CSA_CODIGO</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaIdentificadorQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        return instanciarQuery(session, "select csa.csaIdentificador, csa.csaCodigo from Consignataria csa");
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_CODIGO,
        };
    }
}
