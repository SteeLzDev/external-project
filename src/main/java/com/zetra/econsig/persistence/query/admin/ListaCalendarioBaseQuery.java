package com.zetra.econsig.persistence.query.admin;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalendarioBaseQuery</p>
 * <p>Description: Lista registros de calendário base</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalendarioBaseQuery extends HQuery {

    public String cabDiaUtil;
    public String anoMes;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "cab.cabData, " +
                       "cab.cabDescricao, " +
                       "cab.cabDiaUtil";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from CalendarioBase cab");
        corpoBuilder.append(" where 1=1");
        if (!TextHelper.isNull(cabDiaUtil)) {
            corpoBuilder.append(" and cab.cabDiaUtil ").append(criaClausulaNomeada("cabDiaUtil", cabDiaUtil));
        }
        if (!TextHelper.isNull(anoMes)) {
            corpoBuilder.append(" and to_year_month(cab.cabData) ").append(criaClausulaNomeada("anoMes", anoMes));
        }
        corpoBuilder.append(" order by cab.cabData");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(cabDiaUtil)) {
            defineValorClausulaNomeada("cabDiaUtil", cabDiaUtil, query);
        }
        if (!TextHelper.isNull(anoMes)) {
            defineValorClausulaNomeada("anoMes", anoMes, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CAB_DATA,
                Columns.CAB_DESCRICAO,
                Columns.CAB_DIA_UTIL
        };
    }

}
