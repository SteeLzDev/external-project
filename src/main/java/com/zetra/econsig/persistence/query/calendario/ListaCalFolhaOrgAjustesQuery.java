package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalFolhaOrgAjustesQuery</p>
 * <p>Description: Lista os períodos do calendário da folha do órgão que estão no período de ajustes.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalFolhaOrgAjustesQuery extends HQuery {

    public String orgCodigo;
    public Date dataLimite;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cfo.cfoPeriodo ");
        corpoBuilder.append("FROM CalendarioFolhaOrg cfo ");
        corpoBuilder.append("WHERE cfo.orgCodigo = :codigo ");
        corpoBuilder.append("AND current_date() between cfo.cfoDataFim and cfo.cfoDataFimAjustes ");
        if (dataLimite != null) {
            corpoBuilder.append("AND cfo.cfoPeriodo <= :dataLimite ");
        }
        corpoBuilder.append("ORDER BY cfo.cfoPeriodo ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", orgCodigo, query);
        if (dataLimite != null) {
            defineValorClausulaNomeada("dataLimite", dataLimite, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFO_PERIODO
        };
    }
}
