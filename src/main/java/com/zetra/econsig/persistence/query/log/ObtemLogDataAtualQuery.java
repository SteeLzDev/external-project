package com.zetra.econsig.persistence.query.log;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemLogDataAtualQuery</p>
 * <p>Description: Retorna o último log da data atual registrado no sistema</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemLogDataAtualQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select "
                     + "logData as log_data "
                     + "from Log "
                     + "where to_date(logData) = current_date() "
                     + "order by logData desc";

        return instanciarQuery(session, corpo).setMaxResults(1);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.LOG_DATA
        };
    }
}