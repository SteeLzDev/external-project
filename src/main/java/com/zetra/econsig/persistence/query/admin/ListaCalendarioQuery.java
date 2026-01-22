package com.zetra.econsig.persistence.query.admin;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalendarioQuery</p>
 * <p>Description: Listagem de itens de Calendário</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalendarioQuery extends HQuery {
    
    public String calDiaUtil;
    public String anoMes;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "cal.calData, " +
                       "cal.calDescricao, " +
                       "cal.calDiaUtil";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Calendario cal");
        corpoBuilder.append(" where 1=1");
        if (!TextHelper.isNull(calDiaUtil)) {
            corpoBuilder.append(" and cal.calDiaUtil ").append(criaClausulaNomeada("calDiaUtil", calDiaUtil));
        }
        if (!TextHelper.isNull(anoMes)) {
            corpoBuilder.append(" and to_year_month(cal.calData) ").append(criaClausulaNomeada("anoMes", anoMes));
        }
        corpoBuilder.append(" order by cal.calData");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(calDiaUtil)) {
            defineValorClausulaNomeada("calDiaUtil", calDiaUtil, query);
        }
        if (!TextHelper.isNull(anoMes)) {
            defineValorClausulaNomeada("anoMes", anoMes, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.CAL_DATA,
    			Columns.CAL_DESCRICAO,
    			Columns.CAL_DIA_UTIL
    	};
    }
}
