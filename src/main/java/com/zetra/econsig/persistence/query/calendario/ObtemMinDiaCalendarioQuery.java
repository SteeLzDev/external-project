package com.zetra.econsig.persistence.query.calendario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemMinDiaCalendarioQuery</p>
 * <p>Description: Obtém a menor data da tabela calendário</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemMinDiaCalendarioQuery extends HQuery {

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "min(cal.calData)";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Calendario cal");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CAL_DATA
        };
    }

}
