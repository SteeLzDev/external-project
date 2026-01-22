package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalFolhaCseAjustesQuery</p>
 * <p>Description: Lista os períodos do calendário da folha do consignante que estão no período de ajustes.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalFolhaCseAjustesQuery extends HQuery {

    public String cseCodigo;
    public Date dataLimite;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cfc.cfcPeriodo ");
        corpoBuilder.append("FROM CalendarioFolhaCse cfc ");
        corpoBuilder.append("WHERE cfc.cseCodigo = :codigo ");
        corpoBuilder.append("AND current_date() between cfc.cfcDataFim and cfc.cfcDataFimAjustes ");
        if (dataLimite != null) {
            corpoBuilder.append("AND cfc.cfcPeriodo <= :dataLimite ");
        }
        corpoBuilder.append("ORDER BY cfc.cfcPeriodo ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", cseCodigo, query);
        if (dataLimite != null) {
            defineValorClausulaNomeada("dataLimite", dataLimite, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFC_PERIODO
        };
    }
}
