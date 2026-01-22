package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemProximoDiaUtilQuery</p>
 * <p>Description: Obtem próximo dia útil.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemProximoDiaUtilQuery extends HQuery {

    private final Date dataInicio;
    private final Date dataFim;

    public ObtemProximoDiaUtilQuery(Date dataInicio, Integer diasApos) {
        this(dataInicio, null, diasApos);
    }

    public ObtemProximoDiaUtilQuery(Date dataInicio, Date dataFim, Integer diasApos) {
        super();
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;

        firstResult = diasApos;
        maxResults = 1;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append(" cab.cabData");
        corpoBuilder.append(" FROM CalendarioBase cab ");
        corpoBuilder.append(" WHERE cab.cabDiaUtil = '").append(CodedValues.TPC_SIM).append("'");
        corpoBuilder.append(" AND cab.cabData >= to_date(:dataInicio)");
        if (dataFim != null) {
            corpoBuilder.append(" AND cab.cabData <= to_date(:dataFim)");
        }
        corpoBuilder.append(" ORDER BY cab.cabData ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("dataInicio", dataInicio, query);
        if (dataFim != null) {
            defineValorClausulaNomeada("dataFim", dataFim, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CAL_DATA
        };
    }
}
